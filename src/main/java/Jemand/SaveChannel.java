package Jemand;


import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.Webhook;
import org.javacord.api.entity.webhook.WebhookBuilder;
import org.javacord.api.util.DiscordRegexPattern;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaveChannel {
    private static final Pattern SID = Pattern.compile("SID", Pattern.LITERAL);
    private static final Pattern SERVER = Pattern.compile("SERVER", Pattern.LITERAL);
    private static final Pattern NAME = Pattern.compile("NAME", Pattern.LITERAL);
    private static final Pattern COUNT = Pattern.compile("COUNT", Pattern.LITERAL);
    //private static final Pattern ID = Pattern.compile("ID", Pattern.LITERAL);
    //private static final Pattern ZEIT = Pattern.compile("ZEIT", Pattern.LITERAL);
    //private static final Pattern TEXT = Pattern.compile("TEXT", Pattern.LITERAL);
    //private static final Pattern HINT = Pattern.compile("HINT", Pattern.LITERAL);

    private static final Pattern WHITE = Pattern.compile("\\s");
    private static final Pattern ESCAPED_CHARACTER = Pattern.compile("\\\\(?<char>[^a-zA-Z0-9\\p{javaWhitespace}\\xa0\\u2007\\u202E\\u202F])");
    private static final DiscordApi api = func.getApi();
    private ServerTextChannel channel;
    private Map<Long, Map<Integer, String>> channelmap;
    private Webhook wh;
    private Map<String, String> usersDisc = new HashMap<>();
    private Map<String, String> usersName = new HashMap<>();
    private Map<String, Color> usersColor = new HashMap<>();
    private String HTML = "";
    private String NDJSON = "";
    private static final String s1 = "<div id=\"channels\"><div data-channel=\"\" class=\"act507234919085506591ive\"><div class=\"info\"><strong class=\"name\"><div class=\"tooltip\">#NAME<span class=\"tooltiptext\">SID</span></div></strong><span class=\"msgcount\">COUNT</span></div><span class=\"server\">SERVER</span></div></div>\n";
    private static final String s2 = "<div><h2>" +
            //"<img class=\"avatar\" src=\"IMG\" />" +
            "<strong class=\"username\"><div class=\"tooltip\">NAME<span class=\"tooltiptext\">ID</span></div></strong><span class=\"info time\">ZEIT</span></h2><div class=\"message\"><p>TEXT</p></div></div>\n";

    public SaveChannel(Long ChannelID) {
        api.getServerTextChannelById(ChannelID).ifPresent(textChannel -> channel = textChannel);
        createMap();
    }

    public SaveChannel(ServerTextChannel channel) {
        this.channel = channel;
        try {
            createMap();
        } catch (Exception e) {func.handle(e);}
    }

    public SaveChannel(ServerTextChannel channel, Map<String, String> usersDisc, Map<String, String> usersName, Map<String, Color> usersColor) {
        this.channel = channel;
        this. usersDisc = usersDisc;
        this.usersName = usersName;
        this.usersColor = usersColor;
        try {
            createMap();
        } catch (Exception e) {func.handle(e);}

    }

    public SaveChannel(String filename) {
        loadJson(func.JsonFromFile(filename));
    }

    public SaveChannel(URL link_to_json) throws IOException, ParseException {
        loadJson(Objects.requireNonNull(func.readJsonFromUrl(link_to_json.toString())));
    }

    public final SaveChannel updateMap() {
        createMap();
        return this;
    }
    public final Map<Long, Map<Integer, String>> getChannelmap() { return channelmap; }

    public final SaveChannel apply() {
        return applyToChannel(channel);
    }

    public Map<String, Color> getUsersColor() {
        return usersColor;
    }

    public Map<String, String> getUsersDisc() {
        return usersDisc;
    }

    public Map<String, String> getUsersName() {
        return usersName;
    }

    public final File saveAsJson(String filename) {
        File f = new File(func.filepathof(filename));
        try {
            Map<Integer, String> m = new HashMap<>(1);
            m.put(0, channel.getIdAsString());
            channelmap.put(0L, m);
            JSONParser jp = new JSONParser();
            try {
                new File(func.filepathof(filename)).createNewFile();
            } catch(Exception e) {func.handle(e);}
            f = func.JsonToFile((JSONObject) jp.parse(JSONObject.toJSONString(channelmap)), filename);
        } catch (Exception e) {
            func.handle(e);
        }
        return f;
    }

    public File toHTML() {
        String s = Integer.toString(channel.getPosition());
        if(s.length() == 1) s = "00" + s;
        if(s.length() == 2) s = "0" + s;
        return toHTML("backups" + func.getFileSeparator() + s + "_" + func.WHITE_SPACE.matcher(func.replaceNonNormalChars(channel.getName())).replaceAll("") + "_" + channel.getId() + ".html");
    }

    public File toHTML(String filename) {
        AtomicReference<String> htmltext = new AtomicReference<>("");
        if(func.stringIsBlank(HTML)) {
            htmltext.set(func.readtextoffile("vorlage_sc.txt"));
            try {
                htmltext.set(htmltext.get() + SID.matcher(
                        SERVER.matcher(
                                NAME.matcher(
                                        COUNT.matcher(s1)
                                                .replaceAll(Matcher.quoteReplacement(Integer.toString(channelmap.size() - 1))))
                                        .replaceAll(Matcher.quoteReplacement(channel.getName())))
                                .replaceAll(Matcher.quoteReplacement(channel.getServer().getName())))
                        .replaceAll(Matcher.quoteReplacement(channel.getServer().getIdAsString())));

                final Stream<Long> keys = channelmap.keySet().parallelStream().sorted();
                keys.forEachOrdered(time -> {
                    try {
                        Map<Integer, String> content = channelmap.get(time);
                        if (time != 0L && !content.get(1).isEmpty()) {
                            String name = content.get(3);
                            String DiscName = null;
                            Color color = null;
                            if (usersName.containsKey(content.get(0))) name = usersName.get(content.get(0));
                            if (usersDisc.containsKey(content.get(0))) DiscName = usersDisc.get(content.get(0));
                            if(usersColor.containsKey(content.get(0))) color = usersColor.get(content.get(0));
                            try {
                                if (DiscName == null || color == null) {
                                    User user = api.getUserById(content.get(0)).join();
                                    DiscName = user.getDiscriminatedName();
                                    usersDisc.put(content.get(0), DiscName);
                                    if (color == null) {
                                        color = user.getRoleColor(channel.getServer()).orElse(Color.white);
                                        usersColor.put(content.get(0), color);
                                    }
                                }
                            } catch (Exception ignored) { }
                            String hint = content.get(0);
                            name = applyColorToString(color, name);
                            if (DiscName != null) hint = DiscName + " (" + hint + ")";

                            String date = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, hh:mm:ss"));

                            htmltext.set(htmltext.get() + "\n" + HtmlMessage(hint, date, content.get(1), name));
                        }
                    } catch (Exception e) {
                        func.handle(e);
                    }
                });
                htmltext.set(htmltext.get() + "</div></div></body></html>");
                return func.writetexttofile(htmltext.get(), filename);
            } catch (Exception e) {
                func.handle(e);
            }
        } else {
            htmltext.set(HTML);
        }
        try {
            new File(func.filepathof(filename)).createNewFile();
        } catch(Exception e) {func.handle(e);}
        return func.writetexttofile(htmltext.get() + "</div></div></body></html>", filename);
    }

    public final SaveChannel applyToChannel(ServerTextChannel textChannel) {
        wh = new WebhookBuilder(textChannel).setAvatar(api.getYourself().getAvatar()).setName("test").create().join();
        final Stream<Long> keys = channelmap.keySet().parallelStream().sorted();
        keys.forEachOrdered(time -> {
                try {
                    Map<Integer, String> content = channelmap.get(time);
                    AtomicReference<User> user = new AtomicReference<>(null);
                    String name, icon;
                    user.set(textChannel.getServer().getMemberById(content.get(0)).orElse(null));

                    if(user.get() != null) {
                        name = user.get().getDisplayName(textChannel.getServer());
                        icon = user.get().getAvatar().getUrl().toString();
                    } else {
                        name = content.get(3);
                        icon = content.get(4);
                    }
                    if(!func.isURL(icon)) icon = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
                    if(!content.get(1).isEmpty()) {
                        wh = wh.createUpdater().setAvatar(new URL(icon)).setName(name).update().join();
                        textChannel.getApi().getYourself().getLatestInstance().join();
                        textChannel.getApi().getYourself().getLatestInstance().join();
                        TemporalAccessor ta = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(Instant.ofEpochMilli(time).toString());
                        WebhookEmbed we = new WebhookEmbedBuilder().setDescription(content.get(1)).setTimestamp(ta).build();
                        try (WebhookClient webhookClient = new WebhookClientBuilder("https://discordapp.com/api/webhooks/" + wh.getId() + "/" + wh.getToken().orElse("")).setWait(true).build()) {
                            webhookClient.send(we).join();
                        }
                    }
                } catch (Exception e) {
                    func.handle(e);
                }
            });
        wh.delete("I am cleaning.");
        return this;
    }

    private final SaveChannel loadJson(JSONObject jsonObject) {
        Map<Long, Map<Integer, String>> map = new HashMap<>(1+ jsonObject.keySet().size());
        final JSONParser jsonParser = new JSONParser();
        try {
            channel = api.getServerTextChannelById(String.valueOf(((JSONObject) jsonParser.parse(JSONObject.toJSONString((Map) jsonObject.get("0")))).get("0"))).orElse(channel);
        } catch (Exception e) {
            func.handle(e);
        }
        jsonObject.keySet().forEach(key -> {
            try {
                if(key != null && !key.equals("0")) {
                    Map<Integer, String> map2 = new HashMap<>(5);
                    JSONObject jo2 = (JSONObject) jsonParser.parse(JSONObject.toJSONString((Map) jsonObject.get(key)));
                    //System.out.println(jo2);
                    if (jo2.get("0") != null) map2.put(0, (String) jo2.get("0"));
                    else map2.put(0, "null");
                    if (jo2.get("1") != null) map2.put(1, (String) jo2.get("1"));
                    else map2.put(1, "null");
                    //if(jo2.get("2") != null) map2.put(2, (String) jo2.get("2")); else map2.put(2, "2000-01-01T00:00:42.666Z");
                    if (jo2.get("3") != null) map2.put(3, (String) jo2.get("3"));
                    else map2.put(3, "null");
                    if (jo2.get("4") != null) map2.put(4, (String) jo2.get("4"));
                    else map2.put(4, "null");
                    map.put(Long.parseLong((String) key), map2);
                }
            } catch (Exception e) {
                func.handle(e);
            }
        });
        loadMap(map);
        return this;
    }

    public final SaveChannel loadMap(Map<Long, Map<Integer, String>> Map) {
        channelmap = Map;
        Map<Integer, String> m = new HashMap<>(1);
        m.put(0, channel.getIdAsString());
        channelmap.put(0L, m);
        return this;
    }

    private void createMap() {

        try {
            AtomicReference<String> htmltext = new AtomicReference<>("");
            AtomicInteger messages = new AtomicInteger(0);
            AtomicLong oldkey = new AtomicLong(0L);
            channelmap = channel.getMessagesAsStream().collect(Collectors.toMap(message -> {
                messages.incrementAndGet();
                long l = message.getCreationTimestamp().toEpochMilli();
                while (!(oldkey.get() < l)) l++;
                oldkey.set(l);
                return l;
            }, message -> {
                String content = message.getContent();
                if (content == null) content = "";
                Map<Integer, String> map = new HashMap<>(4); // 0, 1, 3, 4
                MessageAuthor ma = message.getAuthor();
                if (WHITE.matcher(content).replaceAll("").isEmpty() && !message.getEmbeds().isEmpty()) {
                    content = message.getEmbeds().get(0).getDescription().orElse(content);
                }
                map.put(0, ma.getIdAsString());
                map.put(1, replaceExceptMentions(content));
                map.put(3, ma.getDisplayName()); //Author name
                map.put(4, ma.getAvatar().getUrl().toString()); //avatar url

                usersName.put(ma.getIdAsString(), ma.getDisplayName());
                usersDisc.put(ma.getIdAsString(), ma.getDiscriminatedName());

                String hint = ma.getDiscriminatedName() + " (" + ma.getIdAsString() + ")";
                Color color = ma.asUser().flatMap(user -> user.getRoleColor(channel.getServer())).orElse(Color.white);
                usersColor.put(ma.getIdAsString(), color);
                String date = message.getCreationTimestamp().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, hh:mm:ss"));

                htmltext.set(htmltext.get() + "\n" + HtmlMessage(hint, date, map.get(1), applyColorToString(color, ma.getDisplayName())));
                NDJSON += ("{\"content\":\"" + replaceMentions(map.get(1)) + "\", \"author_displayed_name\":\"" + map.get(3) + ", \"author_discriminated_name\":\"" + ma.getDiscriminatedName() + "\", \"author_id\":\"" + map.get(0) + "\", \"time\":\"" + message.getCreationTimestamp().toEpochMilli() + "\", \"channel_id\":\"" + channel.getId() + "\"}").replace("\n", "\\n") + "\n";
                return map;
            }));
            HTML = func.readtextoffile("vorlage_sc.txt") + SID.matcher(
                    SERVER.matcher(
                            NAME.matcher(
                                    COUNT.matcher(s1)
                                            .replaceAll(Matcher.quoteReplacement(Integer.toString(messages.get()))))
                                    .replaceAll(Matcher.quoteReplacement(channel.getName())))
                            .replaceAll(Matcher.quoteReplacement(channel.getServer().getName())))
                    .replaceAll(Matcher.quoteReplacement(channel.getServer().getIdAsString()))
                    + htmltext.get();
            System.out.println("3: " + System.currentTimeMillis()); // + "</div></div></body></html>" is missing
        }catch (Exception e) {
            func.handle(e);
        }
    }

    private void createMap2() {
        try {
            AtomicReference<String> htmltext = new AtomicReference<>("");
            AtomicInteger messages = new AtomicInteger(0);
            AtomicLong oldkey = new AtomicLong(0L);
            channelmap = channel.getMessagesAsStream().collect(Collectors.toMap(message -> {
                messages.incrementAndGet();
                long l = message.getCreationTimestamp().toEpochMilli();
                while (!(oldkey.get() < l)) l++;
                oldkey.set(l);
                return l;
            }, message -> {
                String content = message.getContent();
                if (content == null) content = "";
                Map<Integer, String> map = new HashMap<>(4); // 0, 1, 3, 4
                MessageAuthor ma = message.getAuthor();
                if (WHITE.matcher(content).replaceAll("").isEmpty() && !message.getEmbeds().isEmpty()) {
                    content = message.getEmbeds().get(0).getDescription().orElse(content);
                }

                if(!message.getAttachments().isEmpty()) {
                    AtomicReference<String> urls = new AtomicReference<>("");
                    message.getAttachments().forEach(att -> urls.set(urls.get().concat(att.getUrl().toString()).concat("\n")));
                    content += "\n\n" + urls.get();
                }
                String user_id = ma.getIdAsString();
                map.put(0, user_id);
                map.put(1, replaceExceptMentions(content));
                if(usersName.containsKey(ma.getIdAsString())) map.put(3, usersName.get(user_id));
                else {
                    map.put(3, user_id); //Author name
                    usersName.put(user_id, ma.getDisplayName());
                }
                map.put(4, ma.getAvatar().getUrl().toString()); //avatar url

                usersDisc.put(user_id, ma.getDiscriminatedName());

                String hint = usersDisc.get(user_id) + " (" + user_id + ")";

                Color color;
                if(usersColor.containsKey(user_id)) {
                    color = usersColor.get(user_id);
                } else {
                    color = ma.asUser().flatMap(user -> user.getRoleColor(channel.getServer())).orElse(Color.white);
                    usersColor.put(user_id, color);
                }
                String date = message.getCreationTimestamp().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, hh:mm:ss"));

                htmltext.set(htmltext.get() + "\n" + HtmlMessage(hint, date, map.get(1), applyColorToString(color, usersName.get(user_id))));
                NDJSON += ("{\"content\":\"" + replaceMentions(map.get(1)) + "\", \"author_displayed_name\":\"" + map.get(3) + ", \"author_discriminated_name\":\"" + usersDisc.get(user_id) + "\", \"author_id\":\"" + map.get(0) + "\", \"time\":\"" + message.getCreationTimestamp().toEpochMilli() + "\", \"channel_id\":\"" + channel.getId() + "\"}").replace("\n", "\\n") + "\n";
                return map;
            }));
            HTML = func.readtextoffile("vorlage_sc.txt") + SID.matcher(
                    SERVER.matcher(
                            NAME.matcher(
                                    COUNT.matcher(s1)
                                            .replaceAll(Matcher.quoteReplacement(Integer.toString(messages.get()))))
                                    .replaceAll(Matcher.quoteReplacement(channel.getName())))
                            .replaceAll(Matcher.quoteReplacement(channel.getServer().getName())))
                    .replaceAll(Matcher.quoteReplacement(channel.getServer().getIdAsString()))
                    + htmltext.get();
            System.out.println("3: " + System.currentTimeMillis()); // + "</div></div></body></html>" is missing
        }catch (Exception e) {
            func.handle(e);
        }
    }

    public String getNDJSON() {
        return NDJSON;
    }

    private String replaceHTML(String content) {
        try {
            content = replaceURL(content);
            Matcher customEmoji = DiscordRegexPattern.CUSTOM_EMOJI.matcher(content);
            while (customEmoji.find()) {
                String emojiId = customEmoji.group("id");
                String name = api
                        .getCustomEmojiById(emojiId)
                        .map(CustomEmoji::getName)
                        .orElseGet(() -> customEmoji.group("name"));
                String text = "<a href=\"https://cdn.discordapp.com/emojis/" + emojiId + ".png\">:" + name + ":</a>";
                //<a href="url">link text</a>
                content = customEmoji.replaceFirst(text);
                customEmoji.reset(content);
            }
            Matcher userMention = DiscordRegexPattern.USER_MENTION.matcher(content);
            while (userMention.find()) {
                String userId = userMention.group("id");
                String hint;
                String userName;
                if (usersDisc.containsKey(userId) && usersName.containsKey(userId)) {
                    userName = usersName.get(userId);
                    hint = usersDisc.get(userId) + " (" + userId + ")";
                    String s = "<div class=\"tooltip\">@" +
                            userName +
                            "<span class=\"tooltiptext\">" +
                            hint +
                            "</span></div>";
                    content = userMention.replaceFirst(s);
                    userMention.reset(content);
                } else {
                    Optional<User> userOptional = api.getCachedUserById(userId);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        userName = user.getDisplayName(channel.getServer());
                        hint = user.getDiscriminatedName() + " (" + userId + ")";
                        String s = "<div class=\"tooltip\">@" +
                                userName +
                                "<span class=\"tooltiptext\">" +
                                hint +
                                "</span></div>";
                        content = userMention.replaceFirst(s);
                        userMention.reset(content);
                    }
                }
            }

            return ESCAPED_CHARACTER.matcher(content).replaceAll("${char}");
        } catch(Exception e) {func.handle(e);}
        return content;
    }

    private String replaceMentions(String content) {
        Matcher userMention = DiscordRegexPattern.USER_MENTION.matcher(content);
        Server server = channel.getServer();
        while (userMention.find()) {
            String userId = userMention.group("id");
            if (usersName.containsKey(userId)) {
                content = userMention.replaceFirst("@" + usersName.get(userId));
                userMention.reset(content);
            } else {
                content = userMention.replaceFirst("@" + api.getCachedUserById(userId).map(server::getDisplayName).orElse("invalid-user"));
                userMention.reset(content);
            }
        }
        Matcher customEmoji = DiscordRegexPattern.CUSTOM_EMOJI.matcher(content);
        while (customEmoji.find()) {
            String emojiId = customEmoji.group("id");
            String name = func.getApi()
                    .getCustomEmojiById(emojiId)
                    .map(CustomEmoji::getName)
                    .orElseGet(() -> customEmoji.group("name"));
            content = customEmoji.replaceFirst(":" + name + ":");
            customEmoji.reset(content);
        }
        return ESCAPED_CHARACTER.matcher(content).replaceAll("${char}");
    }

    private String replaceExceptMentions(String content) {
        try {
            Matcher roleMention = DiscordRegexPattern.ROLE_MENTION.matcher(content);
            while (roleMention.find()) {
                String roleId = roleMention.group("id");
                String roleName = channel.getServer().getRoleById(roleId)
                        .map(Role::getName).orElse("deleted-role");

                content = roleMention.replaceFirst("@" + roleName);
                roleMention.reset(content);
            }
            Matcher channelMention = DiscordRegexPattern.CHANNEL_MENTION.matcher(content);
            while (channelMention.find()) {
                String channelId = channelMention.group("id");
                String channelName = channel.getServer()
                        .getTextChannelById(channelId)
                        .map(ServerChannel::getName)
                        .orElse("deleted-channel");
                content = channelMention.replaceFirst("#" + channelName);
                channelMention.reset(content);
            }
            return ESCAPED_CHARACTER.matcher(content).replaceAll("${char}");
        } catch(Exception e) {func.handle(e);}
        return content;
    }
    private String replaceURL(String text) {
        String output = text;
        Pattern url_pattern = Pattern.compile("(?<url>\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");
        Matcher url = url_pattern.matcher(text);
        while (url.find()) {
            String  str = url.group("url");
            text = text.replace(str, "");
            output = output.replace(str, "<a href=\"url\">url</a>".replace("url", str));
            url.reset(text);
        }
        return output;
    }

    private static String applyColorToString(Color color, String string) {
        if(color == null || color.equals(Color.white)) {
            return string;
        } else {
            return "<p style=\"font-size:15px;color:rgb("+
                    color.getRed() + "," +
                    color.getGreen() + "," +
                    color.getBlue() +
                    ");\">" + string + "</p>";
        }
    }

    private String HtmlMessage(String hint, String date, String MessageContent, String UserName) {
        return "<div><h2>" +
                //"<img class=\"avatar\" src=\"IMG\" />" +
                "<strong class=\"username\"><div class=\"tooltip\">" +
                UserName +
                "<span class=\"tooltiptext\">" +
                hint +
                "</span></div></strong><span class=\"info time\">" +
                date +
                "</span></h2><div class=\"message\"><p>" +
                replaceHTML(MessageContent) +
                "</p></div></div>";
    }

    public void delete() {
        this.channel = null;
        this.channelmap  = null;
        this.wh  = null;
        this.usersDisc = null;
        this.usersName  = null;
        this.usersColor  = null;
        this.HTML  = null;
        this.NDJSON = null;
    }
}