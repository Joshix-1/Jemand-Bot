package Jemand.Listener;

import de.jojii.matrixclientserver.Bot.Client;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.WebhookMessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.DiscordRegexPattern;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.core.DiscordApiImpl;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixDiscordBridge {
    public static final String MATRIX_ROOM = "!LwIeJlTcsvlrcuyZqL:matrix.org";

    private final DiscordApi api;
    private final Client client;
    private final String matrixRoom;
    private final List<String> discordChannels;

    //sends messages from all discord channels to matrix, but only to the first discord channel.
    public MatrixDiscordBridge(DiscordApi api, Client client, String matrixRoom, String... discordChannels) {
        this.api = api;
        this.client = client;
        this.matrixRoom = matrixRoom;
        this.discordChannels = Arrays.asList(discordChannels);

        api.addMessageCreateListener(this::onMessageCreate);
        client.registerRoomEventListener(this::onRoomEvent);
    }

    private void onRoomEvent(List<RoomEvent> roomEvent) {
       roomEvent.forEach(event -> {
           if (event.getType().equals("m.room.message") && !event.getSender().equals(client.getLoginData().getUser_id())) {
               api.getServerTextChannelById(discordChannels.get(0)).flatMap(MatrixDiscordBridge::getIncomingWebhook).ifPresent(webhook -> {
                   WebhookMessageBuilder mb = new WebhookMessageBuilder();

                   MatrixUser user = MatrixUser.byId(event.getSender());

                   user.getAvatar().ifPresentOrElse(mb::setDisplayAvatar, () -> mb.setDisplayAvatar(api.getYourself().getAvatar()));
                   mb.setDisplayName(user.getName());

                   JSONObject js = event.getContent();

                   boolean att = false;
                   if (js.has("url")) {
                       Optional<URL> url = convertMatrixFileURls(js.getString("url"));
                       try {
                           System.out.println(url);
                           if (url.isPresent()) {
                               String fileName = js.has("body") ? js.getString("body") : "unknown";
                               if (!fileName.contains(".")) {
                                   fileName = fileName + ".png";
                               }
                               mb.addAttachment(url.get().openStream(), fileName);

                               att = true;
                           }
                       } catch (IOException | JSONException ignored) { }
                   }

                   if (!att && js.has("body")) {
                       mb.setContent(js.getString("body"));
                   }

                   mb.send(webhook).exceptionally(ExceptionLogger.get());
               });

               try {
                   client.sendReadReceipt(event.getRoom_id(), event.getEvent_id(), "m.read", null);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       });
    }

    private static Optional<IncomingWebhook> getIncomingWebhook(ServerTextChannel channel) {
        if (channel == null) {
            return Optional.empty();
        }
        List<IncomingWebhook> webhooks = channel.getIncomingWebhooks().join();
        return webhooks.size() == 0 ? Optional.ofNullable(channel.createWebhookBuilder()
                .setAvatar(channel.getApi().getYourself().getAvatar()).setName(channel.getApi().getYourself().getName()).create().exceptionally(ExceptionLogger.get()).join())
                : Optional.of(webhooks.get(0));
    }

    private void cloneMessageToMatrix(Message message) {
        StringBuilder text = new StringBuilder();
        StringBuilder formattedText = new StringBuilder();

        String name = message.getAuthor().getDisplayName().equals(message.getAuthor().getName())
                ? message.getAuthor().getDiscriminatedName()
                : message.getAuthor().getDisplayName() + " (" + message.getAuthor().getDiscriminatedName() + ")";
        text.append(name).append(":\n");
        formattedText.append("<h6>").append(name).append(":</h6>");

        if (!message.getContent().isEmpty()) {
            text.append(message.getReadableContent());
            formattedText.append(makeToHtml(message.getContent()));
        }

        message.getEmbeds().forEach(embed -> {
            embed.getDescription().ifPresent(desc -> {
                text.append("\n").append(desc);
                formattedText.append("<br>").append(makeToHtml(desc));
            });
        });

        if (message.getAttachments().size() > 0) {
            for (MessageAttachment attachment : message.getAttachments()){
                text.append('\n').append(attachment.getUrl());

                if (false && attachment.isImage()) { //<img src="pic_trulli.jpg" alt="Italian Trulli">
                    formattedText.append("<br><img src='")
                            .append(attachment.getUrl())
                            .append("' alt='")
                            .append(attachment.getUrl())
                            .append("'>");
                } else { //<a href="url">link text</a>
                    formattedText.append("<br><a href='")
                            .append(attachment.getUrl())
                            .append("'>")
                            .append(attachment.getFileName())
                            .append("</a>");
                }
            }
        }

        /*
        "content": {
            "body": "This is an example text message",
            "format": "org.matrix.custom.html",
            "formatted_body": "<b>This is an example text message</b>",
            "msgtype": "m.text"
        }
         */

        try {
            client.sendText(matrixRoom, text.toString(), /*null);//*/ true, formattedText.toString(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private String HTML_EMOJI = "<img src='%s' alt='%s' width='25' height='25'>";

    //https://matrix.org/docs/spec/client_server/r0.6.1#m-room-message-msgtypes
    //only: font, del, h1, h2, h3, h4, h5, h6, blockquote, p, a, ul, ol, sup, sub, li, b, i, u, strong, em, strike, code, hr, br, div, table, thead, tbody, tr, th, td, caption, pre, span, img.
    private String makeToHtml(String content) {
        //content = Jsoup.clean(content, Whitelist.none());

        Matcher customEmoji = DiscordRegexPattern.CUSTOM_EMOJI.matcher(content);
        while (customEmoji.find()) {
            long id = Long.parseLong(customEmoji.group("id"));
            String name = customEmoji.group("name");
            boolean animated = customEmoji.group(0).charAt(1) == 'a';
            CustomEmoji emoji = ((DiscordApiImpl) api).getKnownCustomEmojiOrCreateCustomEmoji(id, name, animated);
            //content = content.replace(emoji.getMentionTag(), String.format(HTML_EMOJI, emoji.getImage().getUrl().toExternalForm(), ":" + emoji.getName() + ":"));
            // <a href="https://www.w3schools.com">Visit W3Schools.com!</a>
            content = customEmoji.replaceFirst(Matcher.quoteReplacement("<a href=\"" + emoji.getImage().getUrl().toExternalForm() + "\">:" + emoji.getName() + ":</a>"));
            customEmoji.reset(content);
        }

        Matcher userMention = DiscordRegexPattern.USER_MENTION.matcher(content);
        while (userMention.find()) {
            String userId = userMention.group("id");
            Optional<User> userOptional = api.getCachedUserById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String userName = user.getDiscriminatedName();
                content = userMention.replaceFirst(Matcher.quoteReplacement("<strong>@" + userName + "</strong>"));
                userMention.reset(content);
            }
        }

        Matcher roleMention = DiscordRegexPattern.ROLE_MENTION.matcher(content);
        while (roleMention.find()) {
            String roleId = roleMention.group("id");
            String roleName = api.getRoleById(roleId)
                    .map(Role::getName)
                    .orElse("deleted-role");
            content = roleMention.replaceFirst(Matcher.quoteReplacement("<strong>@" + roleName + "</strong>"));
            roleMention.reset(content);
        }

        Matcher channelMention = DiscordRegexPattern.CHANNEL_MENTION.matcher(content);
        while (channelMention.find()) {
            String channelId = channelMention.group("id");
            String channelName = api.getServerTextChannelById(channelId)
                    .map(ServerChannel::getName)
                    .orElse("deleted-channel");
            content = channelMention.replaceFirst(Matcher.quoteReplacement("<strong>#" + channelName + "</strong>"));
            channelMention.reset(content);
        }

        content = content.replace("\n", "<br>");

        content = replaceMarkdown(content, CODE_BLOCK_WITH_BREAK, "code");
        content = replaceMarkdown(content, CODE_BLOCK, "code"); //3
        content = replaceMarkdown(content, WRONG_CODE_BLOCK, "code"); //2
        content = replaceMarkdown(content, SMALL_CODE_BLOCK, "code"); //1
        content = replaceMarkdown(content, BOLD, "b");
        content = replaceMarkdown(content, ITALIC, "i");

        return content;
    }

    static final private Pattern CODE_BLOCK_WITH_BREAK = Pattern.compile("```(?:<br>)?(?<text>.+)<br>```");
    static final private Pattern CODE_BLOCK = Pattern.compile("```(?:<br>)?(?<text>.+)```");
    static final private Pattern WRONG_CODE_BLOCK = Pattern.compile("``(?<text>.+)``");
    static final private Pattern SMALL_CODE_BLOCK = Pattern.compile("`(?<text>.+)`");
    static final private Pattern BOLD = Pattern.compile("\\*\\*(?<text>.+)\\*\\*");
    static final private Pattern ITALIC = Pattern.compile("\\*(?<text>.+)\\*");

    private static String replaceMarkdown(String content, Pattern p, String htmlTag) {
        Matcher markDown = p.matcher(content);
        while (markDown.find()) {
            String text = markDown.group("text");
            content = markDown.replaceFirst(Matcher.quoteReplacement("<" + htmlTag + ">" + text + "</" + htmlTag + ">"));
            markDown.reset(content);
        }
        return content;
    }

    static class MatrixUser {
        URL avatar;
        String name;

        private MatrixUser(URL avatar, String name) {
            this.avatar = avatar;
            this.name = name;
        }

        static MatrixUser byId(String id) {
            JSONObject response = makeHttpGetRequest("https://matrix.org/_matrix/client/r0/profile/" + id).orElse(null);

            if (response == null) return new MatrixUser(null, id);

            URL avatar = response.has("avatar_url") ? convertMatrixFileURls(response.getString("avatar_url")).orElse(null) : null;
            return new MatrixUser(avatar,
                    response.has("displayname") ? response.getString("displayname") : id);
        }

        Optional<URL> getAvatar() {
            return Optional.ofNullable(avatar);
        }

        String getName() {
            return name;
        }
    }

    static private Optional<URL> convertMatrixFileURls(String url) {
        if (url == null) return Optional.empty();
        String[] info = url.substring(6).split("/");
        if (info.length != 2) {
            return Optional.empty();
        }
        try {
            return Optional.of(new URL(String.format("https://matrix.org/_matrix/media/r0/download/%s/%s", info[0], info[1])));
        } catch (MalformedURLException ignored) {
            return Optional.empty();
        }
    }

    static private Optional<JSONObject> makeHttpGetRequest(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() != 200) {
                return Optional.empty();
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return Optional.of(new JSONObject(content.toString()));
        } catch (IOException | JSONException e) {
            //System.out.println(e);
            return Optional.empty();
        }
    }

    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageAuthor().isUser() && discordChannels.contains(event.getChannel().getIdAsString())) {
            cloneMessageToMatrix(event.getMessage());
        }
    }
}
