package Jemand.Listener;

import Jemand.func;
import com.fasterxml.jackson.databind.JsonNode;
import de.jojii.matrixclientserver.Bot.Client;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.WebhookMessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Channelportal implements MessageCreateListener, ReactionAddListener {

    private static final String[][] channels;
    private static final Pattern WEBHOOK_MENTION = Pattern.compile("@(?<name>.{1,32})#0000");
    public static final String SEPARATOR = "\u200C";
    public static final Pattern CLONED_MESSAGE = Pattern.compile("(?s)^[\u200D\u200B]{42,64}\u200C.*");

    private static final Client client = new Client("http://matrix.org");
    private static final String MATRIX_ROOM = "!LwIeJlTcsvlrcuyZqL:matrix.org";


    static  {
        String[] lol = func.readtextoffile("channelportal.txt").split("\\s+");
        channels = new String[lol.length][2];
        for (int i = 0; i < lol.length; i++)
            channels[i] = lol[i].split("_");
    }

    static {
        try {
            client.login("jemand-bot", func.pws[8], loginData -> {
                if (loginData.isSuccess()) {
                    client.registerRoomEventListener(roomEvent -> {
                        roomEvent.forEach(event -> {
                            if (event.getType().equals("m.room.message") && !event.getSender().equals("@jemand-bot:matrix.org")) {
                                func.getApi().getServerTextChannelById(channels[0][1]).flatMap(func::getIncomingWebhook).ifPresent(webhook -> {
                                    WebhookMessageBuilder mb = new WebhookMessageBuilder()
                                            .setDisplayName(event.getSender());

                                    getAvatarUrl(event.getSender()).ifPresentOrElse(mb::setDisplayAvatar, () -> mb.setDisplayAvatar(func.getApi().getYourself().getAvatar()));
                                    getDisplayName(event.getSender()).ifPresent(mb::setDisplayName);

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
                    });
                } else {
                    System.err.println("error logging in");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private Optional<String> getDisplayName(String user) {
        try {
            return makeHttpGetRequest(String.format("https://matrix.org/_matrix/client/r0/profile/%s/displayname", user))
                    .filter(json -> json.has("displayname"))
                    .map(json -> json.getString("displayname"));
        } catch (JSONException e) {
            //e.printStackTrace();
            return Optional.empty();
        }
    }

    static private Optional<URL> getAvatarUrl(String user) {
        try {
            JSONObject response = makeHttpGetRequest(String.format("https://matrix.org/_matrix/client/r0/profile/%s/avatar_url", user)).orElse(null);

            if (response == null) return Optional.empty();
            //mxc://<server-name>/<media-id>
            //https://matrix.org/docs/spec/client_server/r0.6.1#matrix-content-mxc-uris

            return convertMatrixFileURls(response.getString("avatar_url"));
        } catch (JSONException e) {
            System.out.println(e);
            return Optional.empty();
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

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getServer().isEmpty() || (event.getMessageAuthor().isWebhook() && CLONED_MESSAGE.matcher(event.getMessage().getContent()).matches())) return;

        for (String[] channel : channels) {
            for (int i = 0; i < channel.length; i++) {
                if (channel[i].equals(event.getChannel().getIdAsString())) {
                    try {
                        mirrorMessage(event.getMessage(), channel[(i + 1) % 2]);

                        if (!event.getMessageAuthor().isWebhook()) {
                            cloneMessageToMatrix(event.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
       }
    }

    static private void mirrorMessage(Message m, String channel_id) {
        if (m.getContent().isEmpty() && m.getEmbeds().isEmpty() && m.getAttachments().size() == 0) return;

        m.getApi().getServerTextChannelById(channel_id).ifPresent(channel -> {

            func.getIncomingWebhook(channel).ifPresent( webhook -> {

                WebhookMessageBuilder mb = m.toWebhookMessageBuilder();

                m.getAuthor().asUser().ifPresent(u -> {
                    if (channel.getServer().getMembers().contains(u)) {
                        mb.setDisplayName(u.getDisplayName(channel.getServer()));
                    }
                });

                String content = m.getContent();

                AllowedMentionsBuilder amb = new AllowedMentionsBuilder();

                Matcher mentions = WEBHOOK_MENTION.matcher(content);
                while (mentions.find()) {
                    String name = mentions.group("name");
                    String id = m.getServer()
                            .map(server -> server.getMembersByDisplayName(name))
                            .flatMap(users -> users.stream()
                                    .filter(u -> channel.getServer().getMembers().contains(u)).findFirst())
                            .map(DiscordEntity::getIdAsString)
                            .orElseGet(() -> channel.getServer().getMembersByDisplayName(name).stream().findFirst().map(DiscordEntity::getIdAsString).orElse(null));

                    if (id == null) {
                        content = mentions.replaceFirst("@" + name);
                    } else {
                        amb.addUser(id);
                        content = mentions.replaceFirst("<@" + id + ">");
                    }
                    mentions.reset(content);
                }

                mb.setAllowedMentions(amb.build());

                String id = func.longToBinaryBlankString(m.getId()) + SEPARATOR;
                mb.setContent(id + (content.length() > 2000 - id.length() ? content.substring(0, 2000 - id.length()) : content));

                mb.send(webhook).exceptionally(ExceptionLogger.get()).join();
            });
        });
    }

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        for (String[] channel : channels) {
            for (int i = 0; i < channel.length; i++) {
                if (channel[i].equals(event.getChannel().getIdAsString())) {
                    int other = (i + 1) % 2;
                    event.getApi().getMessageById(event.getMessageId(), event.getChannel()).thenAccept(m -> mirrorReactions(event, m, channel[other]));
                    break;
                }
            }
        }
    }

    static private void mirrorReactions(ReactionAddEvent event, Message m, String channel_id) {
        m.getApi().getServerTextChannelById(channel_id).ifPresent(channel -> {
            Message other = null;
            if (CLONED_MESSAGE.matcher(m.getContent()).matches()) {
                other = m.getApi().getMessageById(func.binaryBlankStringToLong(m.getContent().split(SEPARATOR, 2)[0]), channel).exceptionally(ExceptionLogger.get()).join();
            } else {
                String blank = func.longToBinaryBlankString(m.getId()) + SEPARATOR;
                other = channel.getMessagesBetweenUntil(message ->  message.getAuthor().isWebhook() && message.getContent().startsWith(blank), m.getId(), m.getId() + 7549747200000L /*30 Minuten in millis + 22 leere bits*/).join().getNewestMessage().orElse(null);
            }

            if (other != null) {
                other.addReaction(event.getEmoji()).exceptionally(ExceptionLogger.get());
            }
        });
    }

    static private void cloneMessageToMatrix(Message message) {
        StringBuilder text = new StringBuilder();
        StringBuilder formattedText = new StringBuilder();

        String name = message.getAuthor().getDisplayName().equals(message.getAuthor().getName())
                ? message.getAuthor().getDiscriminatedName()
                : message.getAuthor().getDisplayName() + " (" + message.getAuthor().getDiscriminatedName() + ")";
        text.append(name).append(":\n");
        formattedText.append(name).append(":<br>");

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
            client.sendText(MATRIX_ROOM, text.toString(), /*null);//*/ true, formattedText.toString(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private String HTML_EMOJI = "<img src='%s' alt='%s' width='25' height='25'>";

    //https://matrix.org/docs/spec/client_server/r0.6.1#m-room-message-msgtypes
    //only: font, del, h1, h2, h3, h4, h5, h6, blockquote, p, a, ul, ol, sup, sub, li, b, i, u, strong, em, strike, code, hr, br, div, table, thead, tbody, tr, th, td, caption, pre, span, img.
    static private String makeToHtml(String content) {
        //content = Jsoup.clean(content, Whitelist.none());

        Matcher customEmoji = DiscordRegexPattern.CUSTOM_EMOJI.matcher(content);
        while (customEmoji.find()) {
            long id = Long.parseLong(customEmoji.group("id"));
            String name = customEmoji.group("name");
            boolean animated = customEmoji.group(0).charAt(1) == 'a';
            CustomEmoji emoji = ((DiscordApiImpl) func.getApi()).getKnownCustomEmojiOrCreateCustomEmoji(id, name, animated);
            //content = content.replace(emoji.getMentionTag(), String.format(HTML_EMOJI, emoji.getImage().getUrl().toExternalForm(), ":" + emoji.getName() + ":"));
            // <a href="https://www.w3schools.com">Visit W3Schools.com!</a>
            content = customEmoji.replaceFirst(Matcher.quoteReplacement("<a href=\"" + emoji.getImage().getUrl().toExternalForm() + "\">:" + emoji.getName() + ":</a>"));
            customEmoji.reset(content);
        }

        Matcher userMention = DiscordRegexPattern.USER_MENTION.matcher(content);
        while (userMention.find()) {
            String userId = userMention.group("id");
            Optional<User> userOptional = func.getApi().getCachedUserById(userId);
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
            String roleName = func.getApi().getRoleById(roleId)
                            .map(Role::getName)
                    .orElse("deleted-role");
            content = roleMention.replaceFirst(Matcher.quoteReplacement("<strong>@" + roleName + "</strong>"));
            roleMention.reset(content);
        }

        Matcher channelMention = DiscordRegexPattern.CHANNEL_MENTION.matcher(content);
        while (channelMention.find()) {
            String channelId = channelMention.group("id");
            String channelName = func.getApi().getServerTextChannelById(channelId)
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
}
