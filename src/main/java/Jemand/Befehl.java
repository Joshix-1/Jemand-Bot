package Jemand;

import Jemand.Listener.CommandCleanupListener;
import Jemand.Listener.GuildUtilities;
import Jemand.Listener.MusicPlayer;
import Jemand.Listener.ReactionRole;
import com.vdurmont.emoji.EmojiParser;
import me.bramhaag.owo.OwO;
import org.apache.commons.io.FileUtils;
import org.apfloat.Apfloat;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedField;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.invite.InviteBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.DiscordRegexPattern;
import org.javacord.api.util.logging.ExceptionLogger;
import org.json.simple.JSONObject;
import org.zeroturnaround.zip.ZipUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Befehl {
    private static final Pattern WHITE_SPACES = Pattern.compile("\\s+");
    private OwO owo;

    private DiscordApi api;

    private AtomicReference<Integer> bid = new AtomicReference<>(-1);
    private AtomicReference<String> befehl = new AtomicReference<>("");

    private AtomicReference<String> subtext1 = new AtomicReference<>("");
    private AtomicReference<String> subbefehl1 = new AtomicReference<>("");
    private AtomicReference<String> subbefehl2 = new AtomicReference<>("");
    private AtomicReference<String> subtext2 = new AtomicReference<>("");
    private AtomicReference<String> idmentioneduser = new AtomicReference<>("");
    private AtomicReference<Boolean> boo1 = new AtomicReference<>(false);
    private AtomicReference<Boolean> rps = new AtomicReference<>(false);
    private AtomicReference<Boolean> Delete = new AtomicReference<>(true);

    private AtomicReference<String> prefix = new AtomicReference<>("J!");

    private final String[] normalabc = func.NORMALABC;


    //Schere Stein Papier
    final private KnownCustomEmoji schere = func.getApi().getCustomEmojiById(703315798881599519L).orElse(null);
    final private KnownCustomEmoji stein = func.getApi().getCustomEmojiById(703315818079060018L).orElse(null);
    final private KnownCustomEmoji papier = func.getApi().getCustomEmojiById(703315818338975784L).orElse(null);
    final private KnownCustomEmoji brunnen = func.getApi().getCustomEmojiById(703315817973940265L).orElse(null);
    final private KnownCustomEmoji ohne_brunnen = func.getApi().getCustomEmojiById(703173587246252032L).orElse(null);

    private final String[] helpabc = func.EMOJIABC;

    //roll
    private final String[] zahl = {":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};

    private final String[] s1 = {"play", "excecute", "captcha", "skip", "ddg", "roleinfo", "userinfo", "lmgtfy", "mitglied", "AllQuotes", "kalender", "donald","reaction-role", "wth", "lisa", "winnie", "drake", "rnd_4g","rnd_img","encrypt", "decrypt", "ship", "dg", "dice-game", "give", "addcoins", "coins", "rnd_ttt", "lr","getlog", "restart", "levelroles", "qr", "car","ss", "save-secure", "screenshot", "pw", "password", "bf", "brainfuck", "owo", "sp", "save-private", "clear", "welcome-message", "wm", "leave-message", "lm", "c4", "stats", "speak", "Channel", "Connect-Four", "calculate", "game-of-quotes","language", "Backup", "Help", "Ping", "Roll", "Pong","RPS", "Say", "4-Gewinnt", "SSPB", "Invite", "Report", "Guildinvite", "Guild-invite", "Emote", "React", "TicTacToe", "Fake-Person", "Fake-Cat", "Fake-Art", "Fake-Horse", "resize", "8-Ball", "prefix", "SSS", "load", "SaveAs", "Save", "delete", "rename", "edit", "random-robot", "random-face", "random-alien", "random-human", "random-cat", "random-picture", "top", "rank", "calc", "goq", "rp", "rc", "rr", "rh", "ra", "rf", "8ball", "fp", "fc", "fa", "fh", "TTT", "4gewinnt", "4g", "addpro", "activity", "r", "l", "d", "e", "sa", "s", "zitat"}; //neu vor SSS einfügen, da danach doppelt

    private User user;
    private Server server;
    private String MentionBot;

    private MessageCreateEvent event;

    EmbedBuilder embed;

    Texte texte;



    public Befehl(MessageCreateEvent Event1) {

        event = Event1;

        api = Event1.getApi();

        MentionBot = api.getYourself().getMentionTag();

        user = event.getMessageAuthor().asUser().orElse(null);
        server = event.getServer().orElse(null);

        texte = new Texte(user, server);

        //embedbuilder
        embed = CommandCleanupListener.insertResponseTracker(new EmbedBuilder()
                .setColor(func.randomColor())
                .setTimestampToNow(), event.getMessageId(), new Texte(user, server).get("NormalEmbedFooter"), user.getAvatar());

        if(func.userIsTrusted(user)) {
            owo = new OwO.Builder()
                    .setKey(func.pws[5])
                    .setUploadUrl("https://jemand.is-pretty.cool")
                    .build();
        }

        setAll(event.getMessageContent());

    }

    CompletableFuture<Message> addRerun(CompletableFuture<Message> message) {
        message.thenAcceptAsync(this::addRerun);
        return message;
    }

    void addRerun(Message message) {
        final String repeat = EmojiParser.parseToUnicode(":repeat:");
        try {
            message.addReaction(repeat).join();
        } catch (Exception e) {
            func.handle(e);
            message.addReaction(repeat);
        }
        message.addReactionAddListener(event2 -> {
            if(event2.getEmoji().equalsEmoji(repeat) && event2.getUserId() != event2.getApi().getYourself().getId()) {
                if(user.getId() == event2.getUserId()) {
                    event2.removeReactionsByEmojiFromMessage(repeat);
                    Delete.set(false);
                    try {
                        fa();
                    } catch (Exception e) {
                        func.handle(e);
                    }
                } else {
                    event2.removeReaction();
                }
            }
        }).removeAfter(30L, TimeUnit.MINUTES)
        .addRemoveHandler(()->{
            message.removeReactionsByEmoji(repeat);
        });
    }

    public void setBefehl(String befehl1) {
        befehl.set(befehl1);
    }

    public Befehl setAll(String MessageContent1) {
        //prefix
        JSONObject prefixjs = func.JsonFromFile("prefix.json");

        if (prefixjs.containsKey(server.getIdAsString())) {
            prefix.set((String) prefixjs.get(server.getIdAsString()));
        } else {
            prefixjs.put(server.getIdAsString(), "J!");
            prefix.set("J!");
        }

        String messageContent = func.removeSpaceAtStart(MessageContent1
                .replace("\u200B", "")
                .replace("\n", "\u200B")
                .replace(event.getApi().getYourself().getNicknameMentionTag(), event.getApi().getYourself().getMentionTag()));
        boo1.set(false);
        bid.set(-1);
        befehl.set("");
        String strop = ""; //ohne Prefix
        if (messageContent.startsWith(MentionBot)) {
            strop = func.removeSpaceAtStart(messageContent.substring(MentionBot.length()));
        } else if (messageContent.toLowerCase().startsWith(prefix.get().toLowerCase())) {
            strop = func.removeSpaceAtStart(messageContent.substring(prefix.get().length()));
        }
        for (int i = 0; i < s1.length; i++) {
            if (strop.matches("(?m)(?i)" + s1[i] + "\\s+.+") || strop.matches("(?m)(?i)" + s1[i])) {
                bid.set(i);
                break;
            }
        }
        if ((bid.get() == -1)) {
            event.addReactionToMessage("❌");
            boo1.set(false);
        } else {
            boo1.set(true);
            subtext1.set("");

            befehl.set(s1[bid.get()].toLowerCase());
            subtext1.set(func.removeSpaceAtStart(strop.substring(befehl.get().length())).replace("\u200B", "\n"));
            String[] st = func.WHITE_SPACE.split(subtext1.get());
            if (st.length >= 1) {
                subbefehl1.set(st[0]);
                subtext2.set(func.removeSpaceAtStart(subtext1.get().substring(subtext1.get().toLowerCase().indexOf(subbefehl1.get().toLowerCase()) + subbefehl1.get().length())));
            }
            subbefehl2.set(subtext2.get().split(" ")[0]);
            idmentioneduser.set("");
            if (!event.getMessage().getMentionedUsers().isEmpty() && !subtext1.get().isEmpty() && subtext1.get().contains("<@")) {
                if (subtext1.get().contains("<@!")) {
                    idmentioneduser.set(subtext1.get().substring(subtext1.get().lastIndexOf("<@!") + 3, subtext1.get().lastIndexOf('>')));
                } else {
                    idmentioneduser.set(subtext1.get().substring(subtext1.get().lastIndexOf("<@") + 2, subtext1.get().lastIndexOf('>')));
                }
            }
            if (idmentioneduser.get().equalsIgnoreCase(api.getYourself().getIdAsString()) || server.getMemberById(idmentioneduser.get()).isEmpty()) {
                idmentioneduser.set("");
            }

            if (!(bid.get() == -1)) { //Befehle
                boo1.set(true);
                befehl.set(s1[bid.get()].toLowerCase());
            }

        }
        return this;
    }
    private void fa() throws Exception {
        embed = func.getNormalEmbed(event);
        Delete.set(false);
        fuehreAus();
    }

    public Texte getTexte() {
        return texte;
    }

    public Server getServer() {
        return server;
    }

    public boolean fuehreAus() throws Exception {
        if(!boo1.get()) {
            return false;
        } else event.getChannel().type().join();

        //if(befehl.get().equalsIgnoreCase("tw") && func.userIsTrusted(user)) {
        //    if(subtext1.get().isEmpty()) subtext1.set("1");
        //    new TwitterFactory().getInstance().setOAuthConsumer();
        //}

        if(befehl.get().equalsIgnoreCase("getlog")  && func.getFileSeparator().equals("/")) {
            if (!func.userIsTrusted(user)) return false;
            File f = new File("/usr/home/admin/Jemand.log");
            String log = FileUtils.readFileToString(f);
            int leng = log.length();
            if(leng > 1016)
                log = log.substring(leng - 1015);
            log = "```\n" + log + "```";
            user.sendMessage(getGruenEmbed().setTitle("Last Log:").setDescription(log), f);
            return true;
        }

        //restart
        if(befehl.get().equalsIgnoreCase("restart") && func.getFileSeparator().equals("/")) {
            if(!func.userIsTrusted(user)) return false;
            if(func.getFileSeparator().equals("/")) if(Runtime.getRuntime().exec("chmod +x /usr/home/admin/Jemand/Jemand-1.0-SNAPSHOT/bin/Jemand").waitFor() == 0) {
                func.shutdown();
                System.exit(69);
                return true;
            } else return false;
        }

        //mitglied
        if (befehl.get().equalsIgnoreCase("mitglied")) {
            if(!func.userIsTrusted(user)) return false;
            server.getMemberById(func.LongFromString(subtext1.get(), 0L)).ifPresent(u -> {
                server.getRoleById(GuildUtilities.MITGLIED).ifPresent(role -> {
                    role.addUser(u, user.getId() + " wollte es so.").join();
                });
            });
            return true;
        }

        if (befehl.get().equalsIgnoreCase("captcha")) {
            if(!func.userIsTrusted(user)) return false;
            server.getMemberById(func.LongFromString(subtext1.get(), 0L)).ifPresent(u -> {
                int i = GuildUtilities.calculateCaptchaNumber(u, server);
                event.getChannel().sendMessage(NumberToText.intToText(i) + " → " + i + "!");
            });
            return true;
        }



        if (befehl.get().equalsIgnoreCase("play")) {
            return MusicPlayer.play(server, user, event.getServerTextChannel().get(), subtext1.get());
        }

        if (befehl.get().equalsIgnoreCase("skip")) {
            return MusicPlayer.skip(server, user);
        }

        //allQuotes //zitat
        if(befehl.get().equalsIgnoreCase("allQuotes")) {
            String filepath = "tmp" + func.getFileSeparator() + func.randomstr(7) + func.getFileSeparator();
            File folder = new File(filepath);
            folder.mkdirs();

            Pattern minus = Pattern.compile("-");

            String[] zitate = func.readtextoffile("zitate.txt").split("\n");
            String[] namen = func.readtextoffile("namen.txt").split("\n");

            String template = Memes.ZITAT_ATA;
            String date = "";
            if(subtext1.get().contains("ka")) {
                if(subtext1.get().contains("2")) {
                    template = Memes.KALENDER;
                } else {
                    template = Memes.KALENDER2;
                }
                date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
            }

            final String temp = template;
            final String d = date;

            Zitat.BEWERTUNGEN.keySet().forEach(k -> {
                String key = k.toString();
                int val = Integer.parseInt(Zitat.BEWERTUNGEN.get(key).toString());
                if(val > 1) {
                    try {
                        String[] zitat = minus.split(key);
                        zitat[0] = zitate[Integer.parseInt(zitat[0])];
                        zitat[1] = namen[Integer.parseInt(zitat[1])];

                        BufferedImage img = new Memes(temp, "»" + zitat[0].substring(1, zitat[0].lastIndexOf('"') -1) + "«", "- " + zitat[1], d).getFinalMeme().orElse(null);

                        if(img != null)
                            ImageIO.write(img, "png", new File(filepath + key + ".png"));

                    } catch(IOException | ArrayIndexOutOfBoundsException e) {
                       func.handle(e);
                    }
                }
            });

            File f = new File(func.filepathof(filepath.substring(0, filepath.length() - 1) + ".zip"));
            ZipUtil.pack(folder, f);
            event.getChannel().sendMessage(embed.setDescription(texte.get("Erfolgreich")), f).join();

            f.delete();
            folder.delete();
            return true;
        }

        if (befehl.get().equalsIgnoreCase("excecute") && func.userIsTrusted(user)) {
            Matcher m = DiscordRegexPattern.WEBHOOK_URL.matcher(subtext1.get());
            if (m.find()) {
                IncomingWebhook w = api.getIncomingWebhookByIdAndToken(m.group("id"), m.group("token")).join();
                String content = m.replaceFirst("");
                event.getMessage().toMessageBuilder().setContent(content).send(w);
                event.getMessage().delete();
                return true;
            }
        }

        //reaction-role //role-
        if(befehl.get().equalsIgnoreCase("reaction-role")) {
            String[] role_id = WHITE_SPACES.split(subtext1.get());
            Matcher m = DiscordRegexPattern.MESSAGE_LINK.matcher(subtext1.get());
            if (m.find()) {
                //return getTextChannelById(matcher.group("channel"))
                //                .map(textChannel -> textChannel.getMessageById(matcher.group("message")));
                Optional<TextChannel> channel = api.getTextChannelById(m.group("channel"));

                if (channel.isPresent()) {
                    Message message = channel.get().getMessageById(m.group("message")).exceptionally(ExceptionLogger.get()).join();
                    if (message != null && message.getAuthor().isYourself()) {
                        return ReactionRole.addRolesToMessage(message, user, role_id);
                    }
                }
            }

            try {
                ReactionRole.sendReactionRoleMessage(event, role_id);
                return true;
            } catch (Exception e) {
                func.handle(e);
                return false;
            }

        }

        JSONObject saved = func.JsonFromFile("save.json");
        //pro
        JSONObject prousers = func.JsonFromFile("prouser.json");
        //speak
        if(befehl.get().equalsIgnoreCase("speak")) {
            if(func.userIsTrusted(user)) {
                event.getMessage().toMessageBuilder().setContent(subtext1.get()).send(event.getChannel());
                event.getMessage().delete();
            } else return false;
        }

        if(befehl.get().equalsIgnoreCase("screenshot")) {
            if(subtext1.get().isEmpty()) {
                return false;
            } else {
                File f = new WebScreenshotter(subtext1.get()).makeScreen();
                event.getChannel().sendMessage(f);
                f.delete();
                return true;
            }
        }

        if(befehl.get().equalsIgnoreCase("ddg")) {
            String url = "https://ddg.gg/" + URLEncoder.encode(subtext1.get(), StandardCharsets.UTF_8);
            event.getChannel().sendMessage(embed.setTitle("DuckDuckGo").setDescription(url).setUrl(url)).join();
            return true;
        }

        if(befehl.get().equalsIgnoreCase("lmgtfy")) {
            String url = "https://lmgtfy.com/?q="+ URLEncoder.encode(subtext1.get(), StandardCharsets.UTF_8) + "&s=d&iie=1";
            event.getChannel().sendMessage(embed.setTitle("LMGTFY").setDescription(url).setUrl(url)).join();
            return true;
        }



        //kalender:
        if(befehl.get().equalsIgnoreCase("kalender")) {
            String template;
            switch(subbefehl1.get()) {
                case "1": template = Memes.KALENDER;
                    break;
                case "2": template = Memes.KALENDER2;
                    break;
                case "3": template = Memes.ZITAT_ATA;
                    break;
                default: event.getChannel().sendMessage(getRotEmbed().setTitle(texte.get("FehlerTitle")).setDescription(texte.get("FehlerDesc", "Kalender")));
                    return false;
            }

            try {
                Memes m = new Memes(template, subtext2.get().split("\\|"));
                event.getChannel().sendMessage(embed.setImage(m.getFinalMeme().orElseThrow()).setTitle("Kalender")).join();
                return true;
            } catch(Exception e) {
                func.handle(e);
                event.getChannel().sendMessage(getRotEmbed().setTitle("Kalender").setDescription(texte.get("FehlerAufgetreten")));
                return false;
            }
        }

        //donald:
        if(befehl.get().equalsIgnoreCase("donald") || befehl.get().equalsIgnoreCase("Lisa") || befehl.get().equals("wth")) {
            String template;
            String name;
            if(befehl.get().equalsIgnoreCase("donald")) {
                template = Memes.DONALD1;
                name = "Donald";
            } else if(befehl.get().equals("lisa")){
                template = Memes.LISA_PRESENTATION;
                name = "Lisa";
            } else {
                template = Memes.WORSE_THAN_HITLER;
                name = "Wth";
            }
            Optional<URL> url = func.getImageURL(event.getMessage());
            Memes m;
            try {
                if (url.isPresent()) {
                    m = new Memes(template, url.get());
                } else if (!func.stringIsBlank(subtext1.get())) {
                    m = new Memes(template, subtext1.get());
                } else {
                    event.getChannel().sendMessage(func.Fehler(name, user));
                    return false;
                }
                event.getChannel().sendMessage(embed.setImage(m.getFinalMeme().orElseThrow()).setTitle(name)).join();
                return true;
            } catch(Exception e) {
                func.handle(e);
                event.getChannel().sendMessage(getRotEmbed().setTitle(name).setDescription("FehlerAufgetreten"));
                return false;
            }
        }

        //drake: //winnie
        if(befehl.get().equalsIgnoreCase("drake") || befehl.get().equalsIgnoreCase("winnie")) {
            String template;
            String name;

            List<MessageAttachment> lma = event.getMessageAttachments().stream().filter(MessageAttachment::isImage).collect(Collectors.toList());


            if(befehl.get().equalsIgnoreCase("drake")) {
                template = Memes.DRAKE1;
                name = "Drake";
            } else {
                if(subtext1.get().split("\\|").length > 2 || lma.size() > 2) template = Memes.WINNIE_3;
                else template = Memes.WINNIE_2;
                name = "Winnie";
            }
            Memes m;
            try {
                if (lma.size() > 1) {
                    URL[] urls = new URL[lma.size()];
                    Iterator<MessageAttachment> it = lma.iterator();
                    int i = 0;
                    while(it.hasNext())
                        urls[i++] = it.next().getUrl();
                    m = new Memes(template, urls);
                } else if (subtext1.get().contains("|")) {
                    m = new Memes(template, subtext1.get().split("\\|"));
                } else {
                    event.getChannel().sendMessage(func.Fehler(name, user));
                    return false;
                }
                event.getChannel().sendMessage(embed.setImage(m.getFinalMeme().orElseThrow()).setTitle(name)).join();
                return true;
            } catch(Exception e) {
                func.handle(e);
                event.getChannel().sendMessage(getRotEmbed().setTitle(name).setDescription("FehlerAufgetreten"));
                return false;
            }
        }

        //qr
        //https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=TEST%20
        if(befehl.get().equalsIgnoreCase("qr")) {
            if(func.stringIsBlank(subtext1.get())) return false;
            embed.setUrl("http://goqr.me/");
            if(subbefehl1.get().equalsIgnoreCase("create") && !func.stringIsBlank(subtext2.get())) {
                String url = "https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=" + func.WHITE_SPACE.matcher(subtext2.get()).replaceAll("%20");
                BufferedImage image = ImageIO.read(new URL(url));
                Message m = event.getChannel().sendMessage(embed.setTitle("QR Code").setImage(image)).join();
                return true;
            } else if(subbefehl1.get().equalsIgnoreCase("read")) { // has to be less than 1,048576 MB
                ImageResizer ir = new ImageResizer(func.getImageURL(event.getMessage()).orElse(new URL(func.WHITE_SPACE.matcher(subtext2.get()).replaceAll(""))));
                while(ir.getSize() > 1048576L) {
                    ir.resize(ir.getWidth() - 20, ir.getHeight() - 20);
                }
                EmbedBuilder embed2 = embed;
                File f = func.tempFile(ir.getFileType());
                ImageIO.write(ir.getBufferedImage(), ir.getFileType(), f);
                Message m = event.getChannel().sendMessage(embed2.setImage(f)).join();
                try {
                    event.getChannel().sendMessage(embed.setTitle("QR Code").setImage("").setDescription(func.readQrCodeFromURL(m.getEmbeds().get(0).getImage().orElse(null).getUrl().toString(), event))).join();
                } catch(NullPointerException e) {
                    func.handle(e);
                    event.getChannel().sendMessage(getRotEmbed().setTitle("QR Code").setImage("").setDescription("FehlerAufgetreten"));
                }
                m.delete();
                f.delete();
                return true;
            }
            return false;
        }

        //password //pw
        if(befehl.get().equalsIgnoreCase("pw") || befehl.get().equalsIgnoreCase("password")) {
            String[] arr = func.readtextoffile("words/en.txt".replace("en", texte.getSprache())).split("\n");
            int length = func.IntFromString(subtext1.get(), 4);
            user.sendMessage(getNormalEmbed().setDescription("```\n" + func.createPassword(arr, length) + "```"));
            addRerun(event.getChannel().sendMessage(getNormalEmbed().setDescription("```\n" + func.createPassword(arr, length) + "```")).join());
            return true;
        }

        //brain //bf
        if(befehl.get().equalsIgnoreCase("bf") || befehl.get().equalsIgnoreCase("brainfuck")) {
            try {
                String[] s = event.getMessageContent().split(" ");
                event.getChannel().sendMessage(embed.setDescription(new Brainfuck().interpret(subtext1.get(), s[s.length -1]))).join();
                return true;
            } catch(Exception e) {
                func.handle(e);
                event.getChannel().sendMessage(getRotEmbed().setTitle(texte.get("FehlerAufgetreten")).setDescription(e.getMessage()));
                return false;
            }
        }

        //owo
        if(befehl.get().equalsIgnoreCase("owo")) {
            if(owo != null && !event.getMessageAttachments().isEmpty()) {
                List<MessageAttachment> a = event.getMessageAttachments();
                int size = a.size();
                if(size > 5) {
                    embed.setDescription(texte.get("OwO5"));
                    size = 5;
                }
                for (int i = 0; i < size; i++) {
                    try {
                        embed.addField(a.get(i).getFileName(), owo.upload(a.get(i).downloadAsByteArray().join(), a.get(i).getFileName()).executeSync().getFullUrl());
                    } catch (Throwable e) {
                        func.handle(e);
                        return false;
                    }
                }
                event.getChannel().sendMessage(embed.setTitle("OwO")).join();
                return true;
            } else return false;
        }

        //wm//welcome//lm//leave
        if(befehl.get().equalsIgnoreCase("welcome-message") || befehl.get().equalsIgnoreCase("wm") || befehl.get().equalsIgnoreCase("leave-message") || befehl.get().equalsIgnoreCase("lm")) {
            if (server.hasAnyPermission(user, PermissionType.ADMINISTRATOR)) {
                boolean join = befehl.get().contains("w");
                userleave u = new userleave(server, user, join);
                if(subbefehl1.get().toLowerCase().contains("remove")) {
                    u.setActivated(false);
                    u.setUses(0);
                    event.getChannel().sendMessage(embed.setTitle(texte.get("Erfolgreich")).setDescription(texte.get("MessageRemoved")));
                    return true;
                }
                if(subbefehl1.get().toLowerCase().contains("add") || subbefehl1.get().toLowerCase().contains("set")) {
                    String sub3 = func.removeSpaceAtStart(subtext2.get().substring(func.removeSpaceAtStart(subbefehl2.get()).length()));
                    if(api.getServerTextChannelById(func.LongFromString(subbefehl2.get(), 0L)).isPresent()) {
                        u.setChannel(api.getServerTextChannelById(func.LongFromString(subbefehl2.get(), 0L)).get());
                        if(!func.StringBlank(sub3)) u.setMessage(sub3);
                    } else {
                        if(!func.StringBlank(subtext2.get())) u.setMessage(subtext2.get());
                    }
                    u.setActivated(true);
                    event.getChannel().sendMessage(embed.setTitle(texte.get("Erfolgreich")).setDescription(texte.get("MessageAdd")));
                    return true;
                }
                if(subbefehl1.get().toLowerCase().contains("get")) {
                    if(u.getActivated()) {
                        event.getChannel().sendMessage(u.getMessage());
                    } else  {
                        event.getChannel().sendMessage(getRotEmbed().setTitle(texte.get("FehlerAufgetreten")).setDescription(texte.get("NoMessage")));
                    }
                    return true;
                }
                if(subbefehl1.get().toLowerCase().contains("help")) {
                    event.getChannel().sendMessage(embed.setDescription(texte.get("Replacements")));
                    return true;
                }
                if(subbefehl1.get().toLowerCase().contains("channel")) {
                    if(api.getServerTextChannelById(func.LongFromString(subbefehl2.get(), 0L)).isPresent()) {
                        u.setChannel(api.getServerTextChannelById(func.LongFromString(subbefehl2.get(), 0L)).get());
                        event.getChannel().sendMessage(embed.setTitle(texte.get("Erfolgreich")).setDescription(texte.get("ChannelAdd", Long.toString(func.LongFromString(subbefehl2.get(), 0L)))));
                    } else {
                        event.getChannel().sendMessage(getRotEmbed().setTitle(texte.get("FehlerAufgetreten")).setDescription(texte.get("NoChannel")));
                    }
                    return true;
                }
            } else {
                event.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte.get("Fehler2Title")).setDescription(texte.get("MisssingPermission", PermissionType.ADMINISTRATOR.name())));
            }
            return false;
        }

        //clear
        if(befehl.get().equalsIgnoreCase("clear")) {
            if (server.hasAnyPermission(user, PermissionType.ADMINISTRATOR)) {
                int i = func.IntFromString(subtext1.get(), 0);
                if(i>100 && !server.getOwner().equals(user)) i = 100;
                if(i == 0) {
                    event.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte.get("FehlerTitle")).setDescription(texte.get("FehlerDesc", "Clear")));
                    return false;
                } else {
                    event.getChannel().getMessages(i).thenAccept(MessageSet::deleteAll);
                    event.getChannel().sendMessage(embed.setTitle(texte.get("Erfolgreich")).setDescription(texte.get("Cleared", Integer.toString(i)))).thenAccept(message -> {
                        api.getThreadPool().getScheduler().schedule((Runnable) message::delete, 10, TimeUnit.SECONDS);
                    });
                    return true;
                }
            } else {
                event.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte.get("Fehler2Title")).setDescription(texte.get("MisssingPermission", PermissionType.ADMINISTRATOR.name())));
                return false;
            }
        }

        //language
        if(befehl.get().equalsIgnoreCase("language")) {
            boo1.set(texte.putSprache(subtext1.get()));
            if(boo1.get()) {
                event.getChannel().sendMessage(embed.setTitle(texte.get("Erfolgreich")).setDescription(texte.get("SpracheChanged")));
            } else {
                event.getChannel().sendMessage(func.getRotEmbed(event).setDescription(texte.get("SpracheFehler")).setTitle(texte.get("FehlerAufgetreten")));
            }
            return boo1.get();
        }

        //channel
        if(befehl.get().equalsIgnoreCase("channel")) {
            try {
                SaveChannel sc;
                if (!event.getMessageAttachments().isEmpty() && event.getMessageAttachments().get(0).getFileName().endsWith(".json")) {
                    sc = new SaveChannel(event.getMessageAttachments().get(0).getUrl()); // channel von json aus attachments
                } else {
                    sc = new SaveChannel(api.getServerTextChannelById(func.LongFromString(subtext1.get(), event.getChannel().getId())).orElse(event.getServerTextChannel().orElse(null))); //entweder channel in Nachricht oder channel von Nachricht
                    //sc.saveAsJson("backups/channel_" + func.LongFromString(subtext1.get(), event.getChannel().getId()) + ".json").applyToChannel(event.getChannel().asServerTextChannel().get());
                }
                File f = sc.toHTML();
                event.getChannel().sendMessage(f).join();
                return true;
            } catch(Exception e) {func.handle(e);}
            return false;
        }


        //backup " + server.getId() + "/
        if (befehl.get().equalsIgnoreCase("backup")) {
            String ratelimit = func.readtextoffile("backups/ratelimit.txt");
            if(ratelimit.contains(server.getIdAsString())) {
                event.getChannel().sendMessage(getRotEmbed().setDescription(texte.get("BackupRateLimit")));
                return false;
            } else {
                try {
                    CompletableFuture<Message> m = event.getChannel().sendMessage(getGruenEmbed().setDescription(texte.get("BackupStarted")));
                    func.writetexttofile(ratelimit + "+" + server.getId(), "backups/ratelimit.txt");
                    final String Name = "backups/" + server.getId() + "_" + System.currentTimeMillis();
                    File file = new File(func.filepathof(Name));

                    AtomicReference<String> NDJson = new AtomicReference<>("");
                    //AtomicReference<Map<String, String>> usersDisc = new AtomicReference<>(new HashMap<>());
                    //AtomicReference<Map<String, String>> usersName = new AtomicReference<>(new HashMap<>());
                    //AtomicReference<Map<String, Color>> usersColor = new AtomicReference<>(new HashMap<>());

                    try {
                        file.mkdirs();
                    } catch (Exception e) {func.handle(e);}

                    server.getTextChannels().forEach(textChannel -> {
                         try {
                             String pos = Integer.toString(textChannel.getRawPosition());
                             if (pos.length() == 1) pos = "00" + pos;
                             else if (pos.length() == 2) pos = "0" + pos;
                             String name = Name + func.getFileSeparator() + pos + "_" + func.replaceNonNormalChars(func.WHITE_SPACE.matcher(textChannel.getName()).replaceAll("-")) + "_" + textChannel.getId();
                             SaveChannel sc = new SaveChannel(textChannel); //, usersDisc.get(), usersName.get(), usersColor.get());
                             sc.saveAsJson(name + ".json");
                             sc.toHTML(name + ".html");

                             //usersDisc.set(sc.getUsersDisc());
                             //usersName.set(sc.getUsersName());
                             //usersColor.set(sc.getUsersColor());

                             NDJson.set(NDJson.get() + sc.getNDJSON());

                             sc.delete();
                         } catch (Exception e) {
                             func.handle(e);
                         }
                    });

                    func.writetexttofile(NDJson.get(), Name + func.getFileSeparator() + "all_channel.ndjson");

                    ZipUtil.pack(file, new File(func.filepathof(Name + ".zip")));
                    File f = new File(func.filepathof(Name + ".zip"));
                    PrivateChannel pc = null;
                    try {
                        pc = user.openPrivateChannel().join();
                    } catch(Exception ignored) {}
                    Message answer;
                    if(pc == null) answer = event.getChannel().sendMessage(func.getPing(event) + "ms", f).join();
                    else answer = pc.sendMessage(func.getPing(event) + "ms", f).join();
                    try {
                        Message m2 = m.join();
                        m2.edit(m2.getEmbeds().get(0).toBuilder().addField("\u200B", answer.getAttachments().get(0).getUrl().toString()));
                    } catch(Exception e) {func.handle(e);}
                    func.deleteDir(file);
                    f.delete();
                    func.writetexttofile(func.readtextoffile("backups/ratelimit.txt").replace("+" + server.getId(), ""), "backups/ratelimit.txt");
                    return true;
                } catch(Exception e) {
                    func.handle(e);
                    func.writetexttofile(func.readtextoffile("backups/ratelimit.txt").replace("+" + server.getId(), ""), "backups/ratelimit.txt");
                    return false;
                }
            }
        }

        //stats
        if (befehl.get().equalsIgnoreCase("stats")) {
            User u = api.getUserById(idmentioneduser.get()).getNow(user);
            event.getChannel().sendMessage(func.getStats(u, event));
            return true;
        }
        //resize
        if (befehl.get().equalsIgnoreCase("resize")) {
            if (func.StringBlank(subtext1.get().replaceAll("\\D", ""))) {
                event.getChannel().sendMessage(func.Fehler("Resize", user));
                return (false);
            } else {
                URL url = new URL("https://pbs.twimg.com/profile_images/828302727746879488/6ttX0ZI0_400x400.jpg");
                String text = "";
                Optional<URL> op_url = func.getImageURL(event.getMessage());
                if(op_url.isPresent()) {
                    url = op_url.get();
                    text = subtext1.get();
                } else if(func.isURL(subbefehl1.get())) {
                    url = new URL(subbefehl1.get());
                    text = subtext2.get();
                }

                if(text.isEmpty()) {
                    event.getChannel().sendMessage(func.Fehler("Resize", user));
                    return (false);
                }

                double d = func.DoubleFromString(text, 1);
                if(d==0) d= 0.1;
                ImageResizer ir = new ImageResizer(url);
                Image image = ir.getImage();
                long oldsize = ir.getSize()/1000;
                Image img = ir.resize(d).getImage();
                event.getChannel().sendMessage(embed.setTitle(image.getWidth(null) + "x" + image.getHeight(null) + " (" + oldsize + "kB) -> " + img.getWidth(null) + "x" + img.getHeight(null) + " (" + ir.getSize()/1000 + "kB)").setImage(ir.getBufferedImage(), ir.getFileType()));

            }
            return true;
        }
        //activity
        if (befehl.get().equalsIgnoreCase("Activity")) {
            if (func.StringBlank(subtext1.get())) {
                event.getChannel().sendMessage(func.Fehler("Activity", user));
                return (false);
            } else {
                if (user.getId() == 396294727814610944L || user.getId() == 254982202197016586L || user.isBotOwner()) {
                    String[] subtext = subtext1.get().replaceAll("  ", " ").split(" ");
                    String str88 = "\u200B" + func.removeSpaceAtStart(subtext1.get().substring(subtext1.get().indexOf(subtext[0]) + subtext[0].length()));
                    if (subtext[0].equals("0")) {
                        event.getApi().updateActivity(ActivityType.PLAYING, str88);
                    } else if (subtext[0].equals("1")) {
                        event.getApi().updateActivity(ActivityType.LISTENING, str88);
                    } else if (subtext[0].equals("2")) {
                        event.getApi().updateActivity(ActivityType.WATCHING, str88);
                    } else if (subtext[0].equals("3")) {
                        event.getApi().updateActivity(ActivityType.STREAMING, str88);
                    }
                } else {
                    event.getChannel().sendMessage(func.Fehler2("Activity", user));
                }
            }
            return true;
        }
        //game-of-qoutes //goq
        //choose from 6 zitate
        //an only //&& (server.getId() == 367648314184826880L || server.getId() == 563387219620921347L)
        if ((befehl.get().equalsIgnoreCase("goq") || befehl.get().equalsIgnoreCase("game-of-quotes"))) {
            Zitat.updateQuotes();
            Zitat.updateNames();
            final String[] zitate = Zitat.ZITATE;
            final String[] namen = Zitat.NAMEN;
            AtomicReference<Boolean> boo = new AtomicReference<>(true);
            AtomicReference<Boolean> boo2 = new AtomicReference<>(true);
            AtomicReference<Integer> iz = new AtomicReference<>(-1);
            AtomicReference<Integer> in = new AtomicReference<>(-1);
            AtomicReference<int[]> zid0 = new AtomicReference<>(func.differentRandomInts(0, zitate.length - 1, 6));

            int subtext = func.IntFromString(subtext1.get(), -1);
            if(subtext >= 0 && subtext < zitate.length) {
                int[] copy = zid0.get();
                copy[0] = subtext;
                zid0.set(copy);
            }

            embed.setTitle("Wähle ein Zitat:").removeAllFields();
            for (int j = 0; j < zid0.get().length; j++) {
                embed.addField(helpabc[j]  + " (" + zid0.get()[j] + ")", func.LinkedEmbed(zitate[zid0.get()[j]]));

            }
            Message message = event.getChannel().sendMessage(embed).join();
            message.addReactions(EmojiParser.parseToUnicode(":repeat:"), helpabc[0], helpabc[1], helpabc[2], helpabc[3], helpabc[4], helpabc[5]);

            message.addReactionAddListener(event2 -> {
                if (event2.getUserId() == user.getId()) {
                    if (iz.get() == -1) {
                        if (event2.getEmoji().equalsEmoji(EmojiParser.parseToUnicode(":repeat:"))) {
                            event2.removeReaction();
                            zid0.set(func.differentRandomInts(0, zitate.length, 6));
                            embed.setTitle("Wähle ein Zitat:").removeAllFields();
                            for (int j = 0; j < zid0.get().length; j++) {
                                embed.addField(helpabc[j]  + " (" + zid0.get()[j] + ")", func.LinkedEmbed(zitate[zid0.get()[j]]));
                            }
                            message.edit(embed);
                            //message.edit(embed.setTitle("Wähle ein Zitat:").removeAllFields().addField(helpabc[0], zitate[zid0.get()[0]]).addField(helpabc[1], zitate[zid0.get()[1]]).addField(helpabc[2], zitate[zid0.get()[2]]).addField(helpabc[3], zitate[zid0.get()[3]]).addField(helpabc[4], zitate[zid0.get()[4]]).addField(helpabc[5], zitate[zid0.get()[5]])).join();
                        } else {
                            int[] zid = zid0.get();
                            for (int i = 0; i < 6; i++) {
                                if (event2.getEmoji().equalsEmoji(helpabc[i])) {
                                    event2.removeReaction();
                                    boo.set(false);
                                    //message.removeOwnReactionsByEmoji(EmojiParser.parseToUnicode(":repeat:"), helpabc[0], helpabc[1], helpabc[2], helpabc[3], helpabc[4], helpabc[5]);
                                    iz.set(zid[i]);
                                    zid0.set(func.differentRandomInts(0, namen.length, 6));
                                    embed.removeAllFields().setTitle("Zitat:").setDescription(func.LinkedEmbed(zitate[iz.get()]) +  " (" +  iz.get() + "-?)\n\n - Schreibe einen witzigen Autoren in den Chat, oder wähle einen aus den unteren.\n\u200B");
                                    for (int j = 0; j < zid0.get().length; j++) {
                                        embed.addField(helpabc[j] + " (" + zid0.get()[j] + ")", func.LinkedEmbed(namen[zid0.get()[j]]));
                                    }
                                    message.edit(embed);
                                    event.getChannel().addMessageCreateListener(event3 -> {
                                        if (event3.getMessageAuthor().isUser() && in.get() == -1) {
                                            if (event3.getMessageAuthor().asUser().get().equals(user)) {
                                                if (func.isURL(func.WHITE_SPACE.matcher(event3.getMessage().getContent()).replaceAll( "")) || event3.getMessageContent().contains("://") || event3.getMessageContent().contains("!") || event3.getMessageContent().contains("?") || (event3.getMessageContent().contains(".") && !event3.getMessageContent().contains(" "))) {
                                                    event3.getMessage().addReaction("❌");
                                                    EmbedBuilder embed2 = new EmbedBuilder()
                                                            .setColor(new Color(func.getRandom(250, 255), func.getRandom(0, 5), func.getRandom(0, 5)))
                                                            .setTimestampToNow()
                                                            .setDescription("Autor kann aus Sicherheitsgründen nicht akzepiert werden.")
                                                            .setTitle("Versuche es erneut!");
                                                    event.getChannel().sendMessage(embed2);
                                                } else {
                                                    boo2.set(false);
                                                    String mc = event3.getMessageContent();
                                                    mc = mc.substring(0, 1).toUpperCase() + mc.substring(1);
                                                    message.delete();

                                                    if (func.goq_replace(Zitat.getNameString().toLowerCase()).contains(func.goq_replace(mc.toLowerCase()))) {
                                                        for (int j = 0; j < namen.length; j++) {
                                                            if (func.goq_replace(namen[j]).equals(func.goq_replace(mc))) {
                                                                in.set(j);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if(in.get() == -1) {
                                                        if (mc.endsWith("\"") && mc.startsWith("\"")) {
                                                            mc = mc.substring(1, mc.length() - 1);
                                                        }
                                                        in.set(namen.length);
                                                        Zitat.addName(mc, user);
                                                    }
                                                    event3.getMessage().delete();
                                                    try {
                                                        new Zitat(iz.get(), in.get(), subtext1.get()).sendMessage(event.getChannel().asTextChannel().get(), embed);
                                                    } catch (Exception e) {
                                                        func.handle(e);
                                                    }
                                                }
                                            }
                                        }
                                    }).removeAfter(50, TimeUnit.MINUTES)
                                    .addRemoveHandler(() -> {
                                        if (boo2.get()) {
                                            try {
                                                message.edit(embed.setDescription(zitate[iz.get()] + "\n\n - Niemand (Du warst zu langsam)"));
                                            } catch (Exception e) {
                                                func.handle(e);
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    } else {
                        if (in.get() == -1) {
                            if (event2.getEmoji().equalsEmoji(EmojiParser.parseToUnicode(":repeat:"))) {
                                zid0.set(func.differentRandomInts(0, namen.length, 6));
                                embed.removeAllFields().setTitle("Zitat:").setDescription(func.LinkedEmbed(zitate[iz.get()]) + " (" +  iz.get() + "-?)\n\n - Schreibe einen witzigen Autoren in den Chat, oder wähle einen aus den unteren.\n\u200B");
                                for (int i = 0; i < zid0.get().length; i++) {
                                    embed.addField(helpabc[i] + " (" + zid0.get()[i] + ")", func.LinkedEmbed(namen[zid0.get()[i]]));
                                }
                                message.edit(embed);
                                event2.removeReaction();
                                //message.edit(embed.setTitle("Zitat:").setDescription(zitate[iz.get()] + "\n\n - Schreibe einen witzigen Autoren in den Chat, oder wähle einen aus den unteren.").removeAllFields().addField(helpabc[0], namen[zid0.get()[0]]).addField(helpabc[1], namen[zid0.get()[1]]).addField(helpabc[2], namen[zid0.get()[2]]).addField(helpabc[3], namen[zid0.get()[3]]).addField(helpabc[4], namen[zid0.get()[4]]).addField(helpabc[5], namen[zid0.get()[5]])).join();
                            } else {
                                for (int i = 0; i < 6; i++) {
                                    if (event2.getEmoji().equalsEmoji(helpabc[i])) {
                                        in.set(zid0.get()[i]);
                                        boo.set(false);
                                        boo2.set(false);
                                        message.delete();
                                        try {
                                            new Zitat(iz.get(), in.get(), subtext1.get()).sendMessage(event.getChannel().asTextChannel().get(), embed);
                                        } catch (Exception e) {
                                            func.handle(e);
                                        }
                                    }
                                }
                            }
                            event2.removeReaction();
                        }
                    }
                } else {
                    if (event2.getUserId() != event2.getApi().getYourself().getId()) {
                        event2.removeReaction();
                    }
                }
            }).removeAfter(60, TimeUnit.MINUTES)
            .addRemoveHandler(()-> {
                if (boo.get()) {
                    try {
                        if (message.getEmbeds().get(0).getTitle().isPresent()) {
                            if (message.getEmbeds().get(0).getTitle().get().contains("Wähle ein Zitat:")) {
                                message.edit(embed.setTitle("Wähle ein Zitat: (Nicht mehr möglich, du warst zu langsam)"));
                                message.removeAllReactions();
                            }
                        }
                    } catch (Exception e) {
                        func.handle(e);
                    }
                }
            });
            return true;
        }

            //zitate //an only && (server.getId() == 367648314184826880L || server.getId() == 563387219620921347L)
            if (befehl.get().equalsIgnoreCase("zitat")) {
                Zitat zitat = new Zitat();

                zitat.setTyp(subtext1.get());

                if (!func.StringBlank(subtext1.get()) && !subtext1.get().contains("-")) {
                    subtext1.set(subtext1.get().toLowerCase().replace("kalender", "")
                                               .replace("ka", "")
                                               .replace("bild", ""));

                    if (subtext1.get().toLowerCase().contains("w")) {
                        Boolean b1 = subtext1.get().toLowerCase().contains("n");
                        if (b1) {
                            b1 = subtext1.get().indexOf('n') < subtext1.get().indexOf('w');
                        }
                        StringBuilder keys = new StringBuilder();
                        for (Object o : Zitat.BEWERTUNGEN.keySet()) {
                            keys.append(o).append("\n");
                        }
                        String[] keylist = keys.toString().split("\n");
                        while (true) {
                            int i = func.getRandom(0, keylist.length - 1);
                            long rating = Zitat.getRatingAsLong(keylist[i]);
                            if (rating > 0 && !b1 || rating < 0 && b1) {
                                subtext1.set(subtext1.get().replace("-", "") + " " + keylist[i]);
                                break;
                            }
                        }
                    }
                }
                if (subtext1.get().contains("-")) {
                    String[] str1 = subtext1.get().split("-");
                    if (str1[0].contains(" ")) str1[0] = str1[0].split(" ")[str1[0].split(" ").length - 1];
                    if (str1[1].contains(" ")) str1[1] = str1[1].split(" ")[0];
                    zitat.setZitat(Integer.parseInt(str1[0]));
                    zitat.setName(Integer.parseInt(str1[1]));
                }
                zitat.sendMessage(event.getChannel(), embed);
                return true;
            }

            //give
            if(befehl.get().equalsIgnoreCase("give") || (befehl.get().equalsIgnoreCase("addcoins") && func.userIsTrusted(user))) {
                boolean bgive = befehl.get().equalsIgnoreCase("give");
                String number;
                if(idmentioneduser.get().isEmpty()) {
                    if(subbefehl2.get().contains("@")) {
                        idmentioneduser.set(func.NO_NUMBER.matcher(subbefehl2.get()).replaceAll(""));
                        number = subbefehl1.get();
                    } else if(subbefehl1.get().contains("@")) {
                        number = subbefehl2.get();
                        idmentioneduser.set(func.NO_NUMBER.matcher(subbefehl1.get()).replaceAll(""));
                    } else number = "";
                } else {
                    if (subbefehl1.get().contains(idmentioneduser.get())) number = subbefehl2.get();
                    else number = subbefehl1.get();
                }
                if(!bgive) number = func.WHITE_SPACE.matcher(number).replaceAll("");
                else number = func.NO_NUMBER.matcher(number).replaceAll("");

                long l = api.getCachedUserById(idmentioneduser.get()).map(User::getId).orElse(user.getId());

                if(number.length() == 0 || l == user.getId()) {
                    event.getChannel().sendMessage(func.Fehler("Give", user));
                    return false;
                }

                Coins give = new Coins(l);
                Coins own = new Coins(user);
                String[] old_coins = {own.getCoins().toString(), give.getCoins().toString()};
                BigInteger coinsToGive = new BigInteger(number);

                if(own.hasCoins(coinsToGive) || !bgive) {
                    BigInteger i = give.addCoins(coinsToGive);

                    if(bgive) own.removeCoins(i);

                    if(bgive) embed.addField("Coins (" + user.getDisplayName(server) + "):", old_coins[0]  + " - " + i.toString() + " = " + own.getCoins().toString() + " " + texte.get("Coins"));
                    embed.addField("Coins (" + api.getCachedUserById(l).map(server::getDisplayName).orElse("invalid-user") + "):", old_coins[1]  + " + " + i.toString() + " = " + give.getCoins().toString() + " " + texte.get("Coins"));

                    event.getChannel().sendMessage(embed.setTitle("Give:").setDescription(texte.get("CoinsGive", user.getIdAsString(), Long.toString(l), i.toString()))).join();
                    return true;
                } else {
                    event.getChannel().sendMessage(getRotEmbed().setTitle(texte.get("FehlerAufgetreten")).setDescription(texte.get("CoinsMissing"))).join();
                    return false;
                }
            }


            //coins
            if(befehl.get().equalsIgnoreCase("coins")) {
                if(subbefehl1.get().equalsIgnoreCase("top")) {
                    int[] i = new int[1];
                    i[0] = 1;
                    Coins.top().forEach(entry ->{
                        if(i[0] <= 10) {
                            String str;
                            if(i[0] <= 3) str = zahl[i[0]] + ":";
                            else str = i[0] + ":";
                            embed.addField(str, api.getCachedUserById(entry.getKey()).map(server::getDisplayName).orElse("invalid-user") + " (<@" + entry.getKey() + ">): " + entry.getValue() + " " + texte.get("Coins"));
                            i[0]++;
                        }
                    });
                    event.getChannel().sendMessage(embed.setTitle("Top " + texte.get("Coins") + ":")).join();
                } else {
                    long l = api.getCachedUserById(func.LongFromString(subtext1.get(), user.getId())).map(User::getId).orElse(user.getId());
                    event.getChannel().sendMessage(embed.setTitle("Coins:").setDescription(texte.get("CoinsMessage", Long.toString(l), new Coins(l).getCoins().toString()))).join();
                }
                return true;
            }
            //calculate
            if (befehl.get().equalsIgnoreCase("calculate") || befehl.get().equalsIgnoreCase("calc")) {

                if (func.StringBlank(subtext1.get())) {
                    event.getChannel().sendMessage(func.Fehler("Calculate", user));
                    return (false);
                } else {
                    long t = System.nanoTime();
                    try {
                        String output = "```\n" + subtext1.get() + " = " ;

                        int availableChars = 2048 - 3 - output.length();

                        String val = func.eval(subtext1.get(), availableChars);

                        if(val.length() > availableChars) {
                            Apfloat f = new Apfloat(val, availableChars - 40);
                            output += f.toString(false);
                        } else {
                            output += val;
                        }

                        if(output.length() > 2048 - 3)  {   // embed desc max: 2048
                            output = output.substring(0, 2048 - 3 - 3) + "...";
                        }

                        event.getChannel().sendMessage(embed.setDescription(output + "```").addField("\u200B", (((System.nanoTime() - t)/100000L)/10000.0) + "ms")).join();
                    } catch (Exception e) {
                        event.getChannel().sendMessage(embed.setTitle("Fehler:").setUrl("https://www.wolframalpha.com/input/?i=" + subtext1.get().replace(" ", "")).setColor(new Color(func.getRandom(250, 255), func.getRandom(0, 5), func.getRandom(0, 5))).setDescription(e.getMessage())).join();
                    }

                }
                return true;
            }

            //add pro user
            if (befehl.get().equalsIgnoreCase("addpro")) {
                if (event.getMessageAuthor().isBotOwner()) {
                    if (!idmentioneduser.get().isEmpty()) {
                        prousers.put(idmentioneduser.get(), event.getMessage().getCreationTimestamp().toEpochMilli());
                        func.JsonToFile(prousers, "prouser.json");
                    } else {
                        event.getChannel().sendMessage(func.Fehler("Addpro", user));
                        return (false);
                    }
                } else {
                    event.getChannel().sendMessage(func.Fehler2("Addpro", user));
                    return (false);
                }
                return true;
            }
            //fake person //fp
            if (befehl.get().equalsIgnoreCase("Fake-Person") || befehl.get().equalsIgnoreCase("fp")) {
                int size = func.IntFromString(subtext1.get(), 1024);
                if (size > 1024) size = 1024;
                ImageResizer ir = new ImageResizer(new URL("https://thispersondoesnotexist.com/image"));
                addRerun(event.getChannel().sendMessage(embed.setImage(ir.resize(size, size).getBufferedImage()).setAuthor("thispersondoesnotexist.com", "https://thispersondoesnotexist.com/", "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/72/docomo/205/baby_1f476.png")));
                return true;
            }
            //fake-cat //fc
            if (befehl.get().equalsIgnoreCase("Fake-Cat") || befehl.get().equalsIgnoreCase("fc")) {
                int size = func.IntFromString(subtext1.get(), 256);
                if (size > 1024) size = 1024;
                ImageResizer ir = new ImageResizer(new URL("https://thiscatdoesnotexist.com/"));
                addRerun(event.getChannel().sendMessage(embed.setImage(ir.resize(size, size).getBufferedImage()).setAuthor("thiscatdoesnotexist.com", "https://thiscatdoesnotexist.com", "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/docomo/205/cat-face_1f431.png")));
                return true;
            }

            if (befehl.get().equalsIgnoreCase("Fake-Horse") || befehl.get().equalsIgnoreCase("fh")) {
                int size = func.IntFromString(subtext1.get(), 256);
                if (size > 1512) size = 1512;
                ImageResizer ir = new ImageResizer(new URL("https://thishorsedoesnotexist.com/"));
                addRerun(event.getChannel().sendMessage(embed.setImage(ir.resize(size, size).getBufferedImage()).setAuthor("thishorsedoesnotexist.com", "https://thishorsedoesnotexist.com/", "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/60/docomo/205/horse_1f40e.png")));
                return true;
            }
            //fake-cat //fc
            if (befehl.get().equalsIgnoreCase("Fake-Art") || befehl.get().equalsIgnoreCase("fa")) {
                int size = func.IntFromString(subtext1.get(), 512);
                if (size > 1024) size = 1024;
                ImageResizer ir = new ImageResizer(new URL("https://thisartworkdoesnotexist.com/"));
                addRerun(event.getChannel().sendMessage(embed.setImage(ir.resize(size, size).getBufferedImage()).setAuthor("thisartworkdoesnotexist.com", "https://thisartworkdoesnotexist.com", "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/60/au-kddi/194/artist-palette_1f3a8.png")));
                return true;
            }

            if (befehl.get().equalsIgnoreCase("userinfo")) {
                List<User> users = new LinkedList<>();
                String[] subtext = WHITE_SPACES.split(subtext1.get());

                for (String s : subtext) {
                    api.getUserById(func.LongFromString(s, 0L)).thenAccept(users::add).exceptionally((e) -> null).join();
                    users.addAll(api.getCachedUsersByName(s));
                    users.addAll(api.getCachedUsersByDisplayName(s, server));
                }

                if (users.size() == 0) {
                    users.add(user);
                }

                List<Long> handled = new LinkedList<>();
                users.forEach(user -> {
                    if (handled.contains(user.getId())) {
                        return;
                    }
                    handled.add(user.getId());
                    boolean isOnServer = server.getMemberById(user.getId()).isPresent();

                    embed.addInlineField("Id", user.getIdAsString());
                    embed.addInlineField("Name", user.getDiscriminatedName());
                    user.getNickname(server).ifPresent(nickname -> embed.addInlineField("Nickname", nickname));
                    embed.addInlineField("Mention", "`" + user.getMentionTag() + "`");
                    embed.addInlineField("CreatedAt", user.getCreationTimestamp().toString());
                    embed.addInlineField("Bot", user.isBot() ? "Yes" : "No");
                    embed.addInlineField("OnServer", isOnServer ? "Yes" : "No");
                    embed.addInlineField("Status", user.getStatus().getStatusString());
                    if (isOnServer) {
                        user.getJoinedAtTimestamp(server).ifPresent(instant -> embed.addInlineField("JoinedServerAt", instant.toString()));
                        embed.addInlineField("RoleCount", user.getRoles(server).size() + "");
                        embed.addInlineField("Permissions", "[" + server.getPermissions(user).getAllowedBitmask() + "](https://discordapi.com/permissions.html#" + server.getPermissions(user).getAllowedBitmask() + ")");
                    }

                    embed.setImage(user.getAvatar());

                    event.getChannel().sendMessage(embed.setTitle("UserInfo")).join();
                });

                return true;
            }


            if (befehl.get().equalsIgnoreCase("roleinfo")) {
                List<Role> roles = new LinkedList<>();
                String[] subtext = WHITE_SPACES.split(subtext1.get());

                for (String s : subtext) {
                    api.getRoleById(func.LongFromString(s, 0)).ifPresent(roles::add);
                    roles.addAll(server.getRolesByNameIgnoreCase(s));
                }

                List<Long> handled = new LinkedList<>();

                roles.forEach(role -> {
                    if (handled.contains(role.getId())) {
                        return;
                    }
                    handled.add(role.getId());
                    embed.addInlineField("Id", role.getIdAsString());
                    embed.addInlineField("Name", role.getName());
                    role.getColor().ifPresent(color -> embed.addInlineField("Color", "#" + Integer.toHexString(color.getRGB())));
                    embed.addInlineField("Mention", "`" + role.getMentionTag() + "`");
                    embed.addInlineField("Managed", role.isManaged() ? "Yes" : "No");
                    embed.addInlineField("Position", role.getPosition() + "");
                    embed.addInlineField("Mentionable", role.isMentionable() ? "Yes" : "No");
                    embed.addInlineField("CreatedAt",  role.getCreationTimestamp().toString());
                    embed.addInlineField("MemberCount", role.getUsers().size() + "");
                    embed.addInlineField("Displayed Seperatly", role.isDisplayedSeparately() ? "Yes" : "No");
                    embed.addInlineField("Permissions", "[" + role.getPermissions().getAllowedBitmask() + "](https://discordapi.com/permissions.html#" + role.getPermissions().getAllowedBitmask() + ")");

                    event.getChannel().sendMessage(embed.setTitle("RoleInfo")).join();
                });

                return roles.size() != 0;
            }

            //https://yesno.wtf/# //8ball
            if (befehl.get().equalsIgnoreCase("8ball") || befehl.get().equalsIgnoreCase("8-ball")) {
                String answer;

                int hash = subtext1.get().length() == 0 ? func.getRandom(0, 20) : subtext1.get().toLowerCase().replace("<@!", "<@").hashCode() % 21;
                if (hash == 0) {
                    answer = "maybe";
                } else if (hash > 10) {
                    answer = "yes";
                } else {
                    answer = "no";
                }

                JSONObject js = func.readJsonFromUrl("https://yesno.wtf/api/?force=" + answer);

                String str = "";
                if (!subtext1.get().isEmpty()) {
                    str = subtext1.get() + " - ";
                }
                if (texte.getSprache().equals("de")) {
                    str += answer.replace("yes", "Ja").replace("maybe", "Vielleicht").replace("no", "Nein") + "!";
                } else {
                    str += answer + "!";
                }
                event.getChannel().sendMessage(embed.setImage(js.get("image").toString()).setThumbnail(new File(func.filepathof("8ball.png"))).setAuthor(str, "https://yesno.wtf/", "")).join();
                return true;
            }


            //img
            if(befehl.get().equals("decrypt")) {
                BufferedImage bi = ImageIO.read(new URL(func.WHITE_SPACE.matcher(subtext1.get()).replaceAll("")));

                event.getChannel().sendMessage(embed.setTitle("Decrypt").setDescription(func.imgDecrypt(bi))).join();
                return true;
            }

            //encrypt //crypt
            if(befehl.get().equals("encrypt")) {
                if (func.stringIsBlank(subtext1.get())) {
                    event.getChannel().sendMessage(func.Fehler("Encrypt", user));
                    return (false);
                } else {
                    BufferedImage bi = ImageIO.read(func.getImageURL(event.getMessage()).orElseGet(() -> event.getMessageAuthor().getAvatar().getUrl()));
                    bi = func.imgEncrypt(subtext1.get(), bi);
                    embed.setTitle("Encrypt");
                    try {
                        String u = func.uploadToImgur(bi, "image.png");
                        event.getChannel().sendMessage(embed.setImage(u).setUrl(u).setDescription(u)).join();
                    } catch (Exception e) {
                        event.getChannel().sendMessage(embed.setImage(bi)).join();
                        func.handle(e);
                    }
                    return true;
                }
            }
            //rnd_img
            if(befehl.get().equalsIgnoreCase("rnd_img")) {
                int size = func.IntFromString(subbefehl1.get(), 512);
                if(size < 1) size = 1;
                else if(size > 2048) size = 2048;

                File f = func.tempFile("png");
                ImageIO.write(func.randomImage(size, size), "PNG", f);
                event.getChannel().sendMessage(f).join();
                f.delete();
                return true;
            }
            //random robot https://robohash.org/
            //?bgset=any

            if (befehl.get().equalsIgnoreCase("random-robot") || befehl.get().equalsIgnoreCase("rr")) {
                addRerun(event.getChannel().sendMessage(embed.setImage(func.robo("1", subtext1.get())).setAuthor("Random Robot", "https://robohash.org/", "https://robohash.org/1")));
                return true;
            }
            //robohead
            if (befehl.get().equalsIgnoreCase("random-face") || befehl.get().equalsIgnoreCase("rf")) {
                addRerun(event.getChannel().sendMessage(embed.setImage(func.robo("3", subtext1.get())).setAuthor("Random Face", "https://robohash.org/", "https://robohash.org/1?set=set3")));
                return true;
            }
            //random alien
            if (befehl.get().equalsIgnoreCase("random-alien") || befehl.get().equalsIgnoreCase("ra")) {
                addRerun(event.getChannel().sendMessage(embed.setImage(func.robo("2", subtext1.get())).setAuthor("Random Alien", "https://robohash.org/", "https://robohash.org/1?set=set2")));
                return true;
            }
            //random cat
            if (befehl.get().equalsIgnoreCase("random-cat") || befehl.get().equalsIgnoreCase("rc")) {
                addRerun(event.getChannel().sendMessage(embed.setImage(func.robo("4", subtext1.get())).setAuthor("Random Cat", "https://robohash.org/", "https://robohash.org/1?set=set4")));
                return true;
            }
            //random human
            if (befehl.get().equalsIgnoreCase("random-human") || befehl.get().equalsIgnoreCase("rh")) {
                addRerun(event.getChannel().sendMessage(embed.setImage(func.robo("5", subtext1.get())).setAuthor("Random Human", "https://robohash.org/", "https://robohash.org/1?set=set5")));
                return true;
            }
            //random picture
            if (befehl.get().equalsIgnoreCase("random-picture") || befehl.get().equalsIgnoreCase("rp")) {
                addRerun(event.getChannel().sendMessage(embed.setImage(func.robo("any", subtext1.get())).setAuthor("Random Picture", "https://robohash.org/", "https://robohash.org/1?set=any")));
                return true;
            }
            //Ping Pong
            if (befehl.get().equalsIgnoreCase("ping")) {
                event.getChannel().sendMessage(embed.setDescription(func.getPing(event).concat("ms")).setTitle(event.getMessage().getContent().replace("i", "o").replace("I", "O").replace(prefix.get(), "").concat("!").replace(api.getYourself().getMentionTag(), event.getMessageAuthor().getDisplayName()).replaceAll(subtext1.get(), "")));
                return true;
            }
            if (befehl.get().equalsIgnoreCase("pong")) {
                event.getChannel().sendMessage(embed.setDescription(func.getPing(event).concat("ms")).setTitle(event.getMessage().getContent().replace("o", "i").replace("O", "I").replace(prefix.get(), "").concat("!").replace(api.getYourself().getMentionTag(), event.getMessageAuthor().getDisplayName()).replaceAll(subtext1.get(), "")));
                return true;
            }
            //saveas //sa String text = "\u200B" + removeSpaceAtStart(subtext1.get().substring(subtext1.get().indexOf(subtext[0])+ subtext[0].length()));
            if (befehl.get().equalsIgnoreCase("saveas") || befehl.get().equalsIgnoreCase("sa")) {
                if (func.stringIsBlank(subbefehl1.get()) || func.StringBlank(subtext2.get()) && event.getMessageAttachments().isEmpty()) {
                    event.getChannel().sendMessage(func.Fehler("SaveAs", user));
                    return (false);
                } else {
                    String[] subtext = subtext1.get().replace("  ", " ").split(" ");
                    String text = func.removeSpaceAtStart(subtext1.get().substring(subtext1.get().indexOf(subtext[0]) + subtext[0].length()));
                    String key = subtext[0];
                    if (saved.containsKey(key)) {
                        embed.addField(texte.get("KeyExBereits", key), texte.get("UseRename"));
                        key = func.randomstr(func.getRandom(3, 4));
                        while (saved.containsKey(key)) {
                            key = func.randomstr(func.getRandom(5, 7));
                        }
                    }
                    String attach = "";
                    if (!event.getMessageAttachments().isEmpty()) {
                        attach = "\u200B\u200B";
                        for (int i = 0; i < event.getMessageAttachments().size(); i++) {
                            try {
                                attach += owo.upload(event.getMessageAttachments().get(i).downloadAsByteArray().join(), event.getMessageAttachments().get(i).getFileName()).executeSync().getFullUrl() + "\u200B";
                            } catch (Throwable e) {
                                attach += event.getMessageAttachments().get(i).getUrl() + "\u200B";
                            }
                        }
                    }
                    saved.put(key, text.replaceAll("\u200B", " ") + "\u200B\u200B" + user.getIdAsString() + "\u200B\u200B" + event.getMessage().getCreationTimestamp().toEpochMilli() + attach);
                    event.getChannel().sendMessage(embed.setTitle(texte.get("SaveTitle")).setDescription(key).addField(texte.get("SaveFieldTitle"), texte.get("SaveField", key)));
                    func.JsonToFile(saved, "save.json");

                }
                return true;
            }
            //save
            if (befehl.get().equalsIgnoreCase("save") || befehl.get().equalsIgnoreCase("s")) {
                if ((func.StringBlank(subtext1.get()) && event.getMessageAttachments().isEmpty())) {
                    event.getChannel().sendMessage(func.Fehler("Save", user));
                    return (false);
                } else {
                    String key = func.randomstr(func.getRandom(3, 4));
                    while (saved.containsKey(key)) {
                        key = func.randomstr(func.getRandom(5, 7));
                    }
                    String attach = "";
                    if (!event.getMessageAttachments().isEmpty()) {
                        attach = "\u200B\u200B";
                        for (int i = 0; i < event.getMessageAttachments().size(); i++) {
                            try {
                                attach += owo.upload(event.getMessageAttachments().get(i).downloadAsByteArray().join(), event.getMessageAttachments().get(i).getFileName()).executeSync().getFullUrl() + "\u200B";
                            } catch (Throwable e) {
                                attach += event.getMessageAttachments().get(i).getUrl() + "\u200B";
                            }
                        }
                    }
                    saved.put(key, subtext1.get().replaceAll("\u200B", " ") + "\u200B\u200B" + user.getIdAsString() + "\u200B\u200B" + event.getMessage().getCreationTimestamp().toEpochMilli() + attach);
                    event.getChannel().sendMessage(embed.setTitle(texte.get("SaveTitle")).setDescription(key).addField(texte.get("SaveFieldTitle"), texte.get("SaveField", key)));
                    func.JsonToFile(saved, "save.json");
                }
                return true;
            }
            //sp //save-private
            if (befehl.get().equalsIgnoreCase("save-private") || befehl.get().equalsIgnoreCase("sp")) {
                if ((func.StringBlank(subtext1.get()) && event.getMessageAttachments().isEmpty())) {
                    event.getChannel().sendMessage(func.Fehler("Save-Private", user));
                    return (false);
                } else {
                    String key;
                    do {
                        key = func.randomstr(func.getRandom(4, 5)) + "-" + func.randomstr(func.getRandom(4, 5)) + "-" + func.randomstr(func.getRandom(4, 5)) + "-" + func.randomstr(func.getRandom(4, 5)) + "-" + func.randomstr(func.getRandom(4, 5));
                    }  while (saved.containsKey(key));
                    String attach = "";
                    if (!event.getMessageAttachments().isEmpty()) {
                        attach = "";
                        for (int i = 0; i < event.getMessageAttachments().size(); i++) {
                            try {
                                attach += owo.upload(event.getMessageAttachments().get(i).downloadAsByteArray().join(), event.getMessageAttachments().get(i).getFileName()).executeSync().getFullUrl() + "\u200B";
                            } catch (Throwable e) {
                                attach += event.getMessageAttachments().get(i).getUrl() + "\u200B";
                            }
                        }
                    }
                    String text = subtext1.get().replaceAll("\u200B", "");
                    try {
                        String k = func.createCryptKey(user.getIdAsString());
                        String salt = func.createCryptKey2(key) + " owo " + func.hashString(k, true, key.length());
                        attach = func.encrypt(attach, k, salt + "3");
                        text = func.encrypt(text, k, salt + "0");
                    } catch (Exception e) {
                        func.handle(e);
                    }
                    saved.put(key,  text  + "\u200B\u200Bs\u200B\u200B" + event.getMessage().getCreationTimestamp().toEpochMilli() + "\u200B\u200B" + attach);
                    user.sendMessage(embed.setTitle(texte.get("SaveTitle")).setDescription(key).addField(texte.get("SaveFieldTitle"), texte.get("SaveField", key)));
                    func.JsonToFile(saved, "save.json");
                    event.deleteMessage("Safety");
                }
                return true;
            }
        //ss //save-secure
        if (befehl.get().equalsIgnoreCase("save-secure") || befehl.get().equalsIgnoreCase("ss")) {
            if (func.StringBlank(subbefehl1.get()) || (func.StringBlank(subtext2.get()) && event.getMessageAttachments().isEmpty())) {
                event.getChannel().sendMessage(func.Fehler("Save-Secure", user));
                return (false);
            } else {
                String key;
                do {
                    key = func.randomstr(func.getRandom(4, 5)) + "_" + func.randomstr(func.getRandom(4, 5)) + "_" + func.randomstr(func.getRandom(4, 5)) + "_" + func.randomstr(func.getRandom(4, 5)) + "_" + func.randomstr(func.getRandom(4, 5));
                }  while (saved.containsKey(key));
                StringBuilder attach = new StringBuilder();
                if (!event.getMessageAttachments().isEmpty()) {
                    attach = new StringBuilder();
                    for (int i = 0; i < event.getMessageAttachments().size(); i++) {
                        try {
                            attach.append(owo.upload(event.getMessageAttachments().get(i).downloadAsByteArray().join(), event.getMessageAttachments().get(i).getFileName()).executeSync().getFullUrl()).append("\u200B");
                        } catch (Throwable e) {
                            attach.append(event.getMessageAttachments().get(i).getUrl()).append("\u200B");
                        }
                    }
                }
                String text = subtext2.get().replaceAll("\u200B", "");
                try {
                    String k = func.createCryptKey2(subbefehl1.get());
                    String salt = "ss" + user.getId() + "u" + subbefehl1.get();
                    attach = new StringBuilder(func.encrypt(attach.toString(), k, salt + "3"));
                    text = func.encrypt(text, k, salt + "0");
                } catch (Exception e) {
                    func.handle(e);
                }
                saved.put(key,  text  + "\u200B\u200Bss" + user.getId() + "\u200B\u200B" + event.getMessage().getCreationTimestamp().toEpochMilli() + "\u200B\u200B" + attach);
                user.sendMessage(embed.setTitle(texte.get("SaveTitle")).setDescription(key).addField(texte.get("SaveFieldTitle"), texte.get("SaveField", key + " " + subbefehl1.get())));
                func.JsonToFile(saved, "save.json");
                event.deleteMessage("Safety");
            }
            return true;
        }
            //load
            if (befehl.get().equalsIgnoreCase("load") || befehl.get().equalsIgnoreCase("l")) {
                if (subtext1.get().equals("")) {
                    event.getChannel().sendMessage(func.Fehler("Load", user));
                    return (false);
                } else if (saved.containsKey(subbefehl1.get())) {
                    //0=text, 1=id, 2=time, 3 = attachments
                    Messageable m = event.getChannel();
                    embed.setTitle(subbefehl1.get());
                    MessageBuilder mb = new MessageBuilder();
                    String[] saves = saved.get(subbefehl1.get()).toString().split("\u200B\u200B");
                    User author;
                    try {
                        author = event.getApi().getUserById(saves[1].replace("s", "")).join();
                    } catch(Exception e) {
                        author = api.getYourself();
                    }
                    String discName = author.getDiscriminatedName();
                    String avatar = author.getAvatar().getUrl().toString();
                    if(saves[1].equalsIgnoreCase("s")) {
                        event.deleteMessage("Safety");
                        m = user;
                        avatar = "https://jemand.is-pretty.cool/6CC5Nbz.png";
                        discName = "Anonymous#0000";
                        try {
                            String key = func.createCryptKey(user.getIdAsString());
                            String salt = func.createCryptKey2(subbefehl1.get()) + " owo " + func.hashString(key, true, subbefehl1.get().length());
                            saves[0] = func.decrypt(saves[0], key, salt + "0");
                            if (saves.length > 3) saves[3] = func.decrypt(saves[3], key, salt + "3");
                        } catch (Exception e) {
                            saves[0] = func.hashString(saves[0], false, 128);
                            if (saves.length > 3) saves[3] = "";
                            m = event.getChannel();
                            embed.setTitle("XXX-XXX-XXX-XXX-XXX");
                        }
                    }
                    if(saves[1].toLowerCase().startsWith("ss")) {
                        event.deleteMessage("Safety");
                        m = user;
                        //avatar = "https://jemand.is-pretty.cool/6CC5Nbz.png";
                        //discName = "Anonymous#0000";
                        String key = func.createCryptKey2(subbefehl2.get());
                        String salt = saves[1] + "u" + subbefehl2.get();
                        try {
                            saves[0] = func.decrypt(saves[0], key, salt + "0");
                            if(saves.length > 3) saves[3] = func.decrypt(saves[3], key, salt + "3");
                        } catch (Exception e) {
                            saves[0] = func.hashString(saves[0] + key + salt, false, 128);
                            if (saves.length > 3) saves[3] = "";
                            m = event.getChannel();
                            embed.setTitle("XXX_XXX_XXX_XXX_XXX");
                        }
                    }
                    String text = saves[0];
                    if (!subbefehl2.get().contains("raw")) {
                        text = text.replaceAll("(?i)<text>", subtext2.get());
                        StringBuilder randuser = new StringBuilder();


                        if (text.toLowerCase().contains("<randuser>")) {
                            try {
                                for (Iterator<User> iterator = server.getMembers().iterator(); iterator.hasNext(); ) {
                                    randuser.append(iterator.next().getMentionTag()).append(" ");
                                }

                                randuser = new StringBuilder(randuser.toString().split(" ")[func.getRandom(0, randuser.toString().split(" ").length)]);
                            } catch (Exception e) {
                                func.handle(e);
                            }
                        }
                        text = func.replaceRandom(text);
                        DateFormat df = new SimpleDateFormat(texte.get("ZeitFormat"));
                        DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                        text = text.replaceAll("(?i)<user>", user.getMentionTag()).replaceAll("(?i)<server>", server.getName()).replaceAll("(?i)<randuser>", randuser.toString()).replaceAll("(?i)<channel>", event.getServerTextChannel().get().getMentionTag()).replaceAll("(?i)<datum>", df.format((new Date(System.currentTimeMillis())))).replaceAll("(?i)<uhrzeit>", df2.format((new Date(System.currentTimeMillis()))));
                        text = text.replaceAll("(?i)<rawtext>", subtext2.get());
                    }
                    mb.setEmbed(CommandCleanupListener.insertResponseTracker(embed.setDescription(text), event.getMessageId(), texte.getString("SaveEmbedFooter", discName).toString(), avatar).setTimestamp(Instant.ofEpochMilli(Long.parseLong(saves[2]))));

                    if (saves.length > 3) {
                        String[] attach = saves[3].split("\u200B");
                        if (attach.length == 1 && (attach[0].toLowerCase().endsWith(".png") || attach[0].toLowerCase().endsWith(".jpg") || attach[0].toLowerCase().endsWith(".gif") || attach[0].toLowerCase().endsWith(".jpeg"))) {
                            embed.setImage(attach[0]);
                        } else {
                            for (int i = 0; i < attach.length; i++) {
                                try {
                                    if(!func.stringIsBlank(attach[i]))
                                        mb.addAttachment(new URL(attach[i]));
                                } catch (MalformedURLException e) {
                                    func.handle(e);
                                }
                            }
                        }
                        //Boolean noimage = true;
                        //for (int i = 0; i < attach.length; i++) {
                        //    if (noimage && (attach[i].toLowerCase().endsWith(".png") || attach[i].toLowerCase().endsWith(".jpg") || attach[i].toLowerCase().endsWith(".gif") || attach[i].toLowerCase().endsWith(".jpeg"))) {
                        //        embed.setImage(attach[i]);
                        //        noimage = false;
                        //    } else {
                        //        embed.addField("\u200B", attach[i]);
                        //    }
                        //}
                    }
                    mb.send(m).join();

                } else {
                    event.getChannel().sendMessage(func.getRotEmbed(event).setDescription(texte.get("NichtsGefunden")).setTitle(texte.get("FehlerAufgetreten")));
                    return (false);
                }
                return true;
            }
            //delete
            if (befehl.get().equalsIgnoreCase("delete") || befehl.get().equalsIgnoreCase("d")) {
                if (subtext1.get().equals("")) {
                    event.getChannel().sendMessage(func.Fehler("Delete", user));
                    return (false);
                } else if (saved.containsKey(subtext1.get())) {
                    //0=text, 1=id, 2=time, 3 = attachments
                    String[] saves = saved.get(subtext1.get()).toString().split("\u200B\u200B");
                    User author = event.getApi().getUserById(saves[1]).get();
                    if (author.equals(user) || user.isBotOwner()) {
                        saved.remove(subtext1.get());
                        event.getChannel().sendMessage(embed.setTitle(texte.getString("Erfolgreich").toString()).setDescription(texte.get("DeleteTrue", subtext1.get())));
                        func.JsonToFile(saved, "save.json");
                    } else {
                        event.getChannel().sendMessage(func.getRotEmbed(event).setDescription(texte.get("DeleteFalse")).setTitle(texte.get("Fehler2Title")));
                    }
                } else {
                    event.getChannel().sendMessage(func.getRotEmbed(event).setDescription(texte.get("NichtsGefunden")).setTitle(texte.get("FehlerAufgetreten")));
                    return (false);
                }
                return true;
            }
            //rename
            if (befehl.get().equalsIgnoreCase("Rename") || befehl.get().equalsIgnoreCase("r")) {
                if (func.stringIsBlank(subtext1.get())|| !subtext1.get().contains(" ")) {
                    event.getChannel().sendMessage(func.Fehler("Rename", user));
                    return (false);
                } else {
                    String[] subtext = subtext1.get().split(" ");
                    if (saved.containsKey(subtext[0])) {
                        if (saved.containsKey(subtext[1])) {
                            event.getChannel().sendMessage(getRotEmbed().setTitle(texte.get("FehlerAufgetreten")).setDescription(texte.get("KeyExBereits", subtext[0])));
                        } else {
                            //0=text, 1=id, 2=time, 3 = attachments
                            String[] saves = saved.get(subtext[0]).toString().split("\u200B\u200B");
                            User author = event.getApi().getUserById(saves[1].replace("s", "")).get();
                            if ((author.equals(user)) || user.isBotOwner()) { // && ispro(user.get())
                                saved.put(subtext[1], saved.get(subtext[0]).toString());
                                event.getChannel().sendMessage(embed.setTitle(texte.getString("Erfolgreich").toString()).setDescription(texte.getString("Rename", subtext[0], subtext[1]).toString()));
                                saved.remove(subtext[0]);
                                func.JsonToFile(saved, "save.json");
                            } else {
                                event.getChannel().sendMessage(func.Fehler2("Rename", user));
                            }
                        }
                    } else {
                        event.getChannel().sendMessage(getRotEmbed().setTitle(texte.get("FehlerAufgetreten")).setDescription(texte.get("KeyExNicht", subtext[0])));
                        return (false);
                    }
                }
                return true;
            }
            //edit
            if (befehl.get().equalsIgnoreCase("Edit") || befehl.get().equalsIgnoreCase("e")) {
                if (subtext1.get().isEmpty() || !subtext1.get().contains(" ")) {
                    event.getChannel().sendMessage(func.Fehler("Edit", user));
                    return (false);
                } else {
                    String[] subtext = subtext1.get().replace("  ", " ").split(" ");
                    String[] saves = saved.get(subtext[0]).toString().split("\u200B\u200B");
                    User author = event.getApi().getUserById(saves[1]).get();
                    if (author.equals(user) || user.isBotOwner()) { //&& func.ispro(user))
                        String text = "\u200B" + func.removeSpaceAtStart(subtext1.get().substring(subtext1.get().indexOf(subtext[0]) + subtext[0].length()));
                        String key = subtext[0];
                        StringBuilder attach = new StringBuilder();
                        if (!event.getMessageAttachments().isEmpty()) {
                            attach = new StringBuilder("\u200B\u200B");
                            for (int i = 0; i < event.getMessageAttachments().size(); i++) {
                                try {
                                    attach.append(owo.upload(event.getMessageAttachments().get(i).downloadAsByteArray().join(), event.getMessageAttachments().get(i).getFileName()).executeSync().getFullUrl()).append("\u200B");
                                } catch (Throwable e) {
                                    attach.append(event.getMessageAttachments().get(i).getUrl()).append("\u200B");
                                }
                            }
                        }
                        saved.put(key, text.replaceAll("\u200B", "") + "\u200B\u200B" + user.getIdAsString() + "\u200B\u200B" + event.getMessage().getCreationTimestamp().toEpochMilli() + attach);
                        event.getChannel().sendMessage(embed.setTitle(texte.get("EditTitle")).setDescription(key).addField(texte.get("SaveFieldTitle"), texte.get("SaveField", key)));
                        func.JsonToFile(saved, "save.json");
                    } else {
                        event.getChannel().sendMessage(func.Fehler2("Edit", user));
                    }
                }
                return true;
            }
            //lr //levelroles
            if (befehl.get().equalsIgnoreCase("lr") || befehl.get().equalsIgnoreCase("levelroles")) {
                boolean haspermission = server.hasPermission(user, PermissionType.ADMINISTRATOR);
                if(subbefehl1.get().equalsIgnoreCase("add") && haspermission) {
                    String[] content = subtext2.get().split(" ");
                    if (content.length > 1) {
                        content[1] = func.removeSpaceAtStart(subtext2.get().replaceFirst(content[0], ""));
                        Levelroles lr2 = new Levelroles(server);
                        Role  r = server.getRoleById(func.LongFromString(content[1], -1)).orElse(null);
                        if (r == null) {
                            List<Role> list = server.getRolesByName(content[1]);
                            if (list.size() > 0) r = list.get(0);
                        }
                        if (r == null) {
                            List<Role> list = server.getRolesByNameIgnoreCase(content[1]);
                            if (list.size() > 0) r = list.get(0);
                        }
                        if(lr2.addRole(func.LongFromString(content[0], -1), r)) embed.setDescription(texte.get("LevelRolesAdded"));
                    }
                }

                Levelroles lr = new Levelroles(server);
                Map<Long, Long> map = lr.getMap();
                if(map.size() == 0) embed.setDescription(texte.get("LevelRolesAddHelp"));
                AtomicInteger i = new AtomicInteger(0);
                final String[] str = "1️⃣ ! 2️⃣ ! 3️⃣ ! 4️⃣ ! 5️⃣ ! 6️⃣ ! 7️⃣ ! 8️⃣ ! 9️⃣ ! \uD83D\uDD1F".split(" ! ");
                EmbedBuilder embed2 = embed.setTitle(texte.get("LevelRolesTitle"));
                if (map.keySet().size() > 0) map.keySet().stream().sorted().forEach(aLong -> {
                    embed.addField(str[i.getAndIncrement()] + ":", texte.get("LevelRolesText", Long.toString(aLong), api.getRoleById(map.get(aLong)).map(Role::getMentionTag).orElse("@invalid-role")));
                });
                if(haspermission) embed.addField("\u200B", texte.get("LevelRolesInfo"));
                Message m = event.getChannel().sendMessage(embed).join();
                if(haspermission) {
                    m.addReactions(str).join();
                    m.addReactionAddListener(event2 -> {
                        if (event2.getUserId() == user.getId()) {
                            int index = -1;
                            for (int j = 0; j < str.length; j++) {
                                if (event2.getEmoji().equalsEmoji(str[j])) {
                                    index = j;
                                    break;
                                }
                            }
                            if (index != -1) {
                                Levelroles lr2 = new Levelroles(server);
                                final int finalIndex = index;
                                event2.getMessage().ifPresent(message -> {
                                    List<EmbedField> list = message.getEmbeds().get(0).getFields();
                                    if (list.size() > finalIndex && lr2.removeRole(func.LongFromString(list.get(finalIndex).getValue().split(":")[0], -1))) {
                                        Map<Long, Long> map2 = lr2.getMap();
                                        embed2.removeAllFields();
                                        if (map2.keySet().size() > 0)
                                            map2.keySet().stream().forEachOrdered(aLong -> {
                                                embed2.addField(str[i.getAndIncrement()] + ":", texte.get("LevelRolesText", Long.toString(aLong), api.getRoleById(map.get(aLong)).map(Role::getMentionTag).orElse("@invalid-role")));
                                            });
                                        embed2.addField("\u200B", texte.get("LevelRolesInfo"));
                                        message.edit(embed2);
                                    }
                                });
                                event2.removeReaction().join();
                            }
                        }
                    }).removeAfter(40, TimeUnit.MINUTES).addRemoveHandler(m::removeAllReactions);
                }
                return true;
            }

            //top //rank //xp
            if (befehl.get().equalsIgnoreCase("top") || befehl.get().equalsIgnoreCase("rank")) {
                HashMap<String, Long> top_unsorted = func.JsonFromFile("xp/user_xp_" + server.getIdAsString() + ".json");
                Map<String, Long> top = func.sortByValue(top_unsorted, false);
                String[] keys1 = top.keySet().toArray(new String[top.size()]);
                int newlength = keys1.length;
                for (String s : keys1) {
                    if (server.getMemberById(s).isEmpty()) {
                        newlength--;
                    }
                }
                String[] keys = new String[newlength];

                int i2 = 0;
                for (int i = 0; i < keys.length; i++) {
                    if (server.getMemberById(keys1[i]).isPresent()) {
                        if (keys.length > i2) {
                            keys[i2] = keys1[i];
                        }
                        i2++;
                    }
                }

                if (befehl.get().equalsIgnoreCase("rank")) {
                    String id2 = user.getIdAsString();
                    if (!func.StringBlank(idmentioneduser.get())) {
                        if (!event.getApi().getUserById(idmentioneduser.get()).get().isBot()) {
                            if (top.containsKey(idmentioneduser.get())) {
                                id2 = idmentioneduser.get();
                            }
                        }
                    } else if (!func.StringBlank(subbefehl1.get())) {
                        try {
                            int i = Integer.parseInt(subbefehl1.get()) - 1;
                            if (i >= keys.length) {
                                i = keys.length - 1;
                            }
                            id2 = keys[i];
                        } catch (Exception e) {
                            func.handle(e);
                        }
                    }
                    Integer i1 = -1;
                    for (int i = 0; i < keys.length; i++) {
                        if (keys[i].equals(id2)) {
                            i1 = i;
                            break;
                        }
                    }
                    if (i1 != -1) {
                        User user1 = event.getApi().getUserById(id2).get();
                        //String str = "Platz: **" + Long.toString(i1 +1 ) + "** \n xp: **" + (top.get(id2) * 17 / 3) + "**";
                        String str = texte.get("Rank", Long.toString(i1 + 1), Long.toString((long)func.getLevel(top.get(id2))), func.getLvlPercent(top.get(id2)), Long.toString((long)func.getXp(top.get(id2))));

                        event.getChannel().sendMessage(embed.setDescription(str).setTitle(user1.getDisplayName(server) + " (" + user1.getDiscriminatedName() + "):"));
                    }
                } else if (befehl.get().equalsIgnoreCase("top")) { //top:
                    EmbedBuilder embed2 = embed;
                    int count = keys.length;
                    int maxcount = func.IntFromString(subtext1.get(), 8);
                    if(maxcount > 25) maxcount = 24;
                    if (count > maxcount) {
                        count = maxcount;
                    }
                    int fix = 0;
                    for (int i = 0; i < maxcount; i++) {
                        if (keys.length > i) {
                            try {
                                if (server.getMemberById(keys[i]).isPresent()) {
                                    String platz;
                                    if (i + fix < 3) {
                                        platz = texte.get("TopPlatz", zahl[i + 1 + fix]);
                                        embed2 = embed2.addField(platz, texte.get("TopField", event.getApi().getUserById(keys[i]).get().getDisplayName(server),  Long.toString((long)func.getLevel(top.get(keys[i]))), Long.toString((long)func.getXp(top.get(keys[i])))));

                                    } else {
                                        platz = texte.get("TopPlatz", Integer.toString(i + 1 + fix));
                                        embed2 = embed2.addInlineField(platz, texte.get("TopField", event.getApi().getUserById(keys[i]).get().getDisplayName(server), Long.toString((long)func.getLevel(top.get(keys[i]))), Long.toString((long)func.getXp(top.get(keys[i])))));
                                    }
                                } else {
                                    fix--;
                                }
                            } catch (Exception e) {
                                fix--;
                            }
                        }
                    }
                    int finalFix = fix;
                    event.getChannel().sendMessage(embed2.setTitle("Top " + (count + fix) + ":")).thenAccept(m -> {

                        if (m.getEmbeds().get(0).getFields().size() != keys.length + finalFix) {
                            final EmbedBuilder embed3 = embed;
                            final int ranks = keys.length + finalFix;
                            final int fields = m.getEmbeds().get(0).getFields().size();
                            final String back = EmojiParser.parseToUnicode(":arrow_backward:");
                            final String forward = EmojiParser.parseToUnicode(":arrow_forward:");
                            m.addReactions(back, forward);
                            m.addReactionAddListener(event2 -> {
                                int site = 0;
                                if (event2.getEmoji().isUnicodeEmoji() && event2.getEmoji().equalsEmoji(back))
                                    site = -1;
                                if (event2.getEmoji().isUnicodeEmoji() && event2.getEmoji().equalsEmoji(forward))
                                    site = 1;
                                if (site != 0 && event2.getUserId() == user.getId()) {
                                    int fix2 = 0;
                                    String str = event2.getMessage().orElse(m).getEmbeds().get(0).getTitle().orElse("");
                                    int asite = str.length() - str.replace("\u200B", "").length() + site;
                                    if (asite < 0) asite = 0;
                                    if (asite * fields > ranks) asite = ranks / fields;
                                    embed3.removeAllFields();
                                    int t = 0;
                                    for (int i = asite * fields; i < asite * fields + fields; i++) {
                                        if (keys.length > i && i >= 0) {
                                            try {
                                                if (server.getMemberById(keys[i]).isPresent()) {
                                                    t = i + 1 + fix2;
                                                    String platz;
                                                    if (i + fix2 < 3) {
                                                        platz = texte.get("TopPlatz", zahl[i + 1 + fix2]);
                                                        embed3.addField(platz, texte.get("TopField", event.getApi().getUserById(keys[i]).get().getDisplayName(server), Long.toString((long) func.getLevel(top.get(keys[i]))), Long.toString((long) func.getXp(top.get(keys[i])))));
                                                    } else {
                                                        platz = texte.get("TopPlatz", Integer.toString(i + 1 + fix2));
                                                        embed3.addInlineField(platz, texte.get("TopField", event.getApi().getUserById(keys[i]).get().getDisplayName(server), Long.toString((long) func.getLevel(top.get(keys[i]))), Long.toString((long) func.getXp(top.get(keys[i])))));
                                                    }
                                                } else {
                                                    fix2--;
                                                }
                                            } catch (Exception e) {
                                                fix2--;
                                            }
                                        }
                                    }
                                    str = "";
                                    for (int i = 0; i < asite; i++) {
                                        str += "\u200B";
                                    }
                                    if (t != 0) event2.editMessage(embed3.setTitle("Top " + t + ":" + str));
                                    event2.removeReaction();
                                }
                            }).removeAfter(40, TimeUnit.MINUTES)
                                    .addRemoveHandler(() -> m.removeOwnReactionsByEmoji(back, forward));

                        }

                    });
                }
                return true;
            }

            //Prefix
            if (befehl.get().equalsIgnoreCase("prefix")) {
                embed.setTitle("Prefix:");
                if (func.stringIsBlank(subtext1.get())) {
                    event.getChannel().sendMessage(embed.setDescription(texte.getString("Prefix").toString()));
                } else {
                    if (func.WHITE_SPACE.matcher(subtext1.get()).replaceAll("").length() < 11) {
                        if (server.hasAnyPermission(user, PermissionType.ADMINISTRATOR)) {
                            String oldp = prefix.get();
                            JSONObject prefixjs = func.JsonFromFile("prefix.json");
                            prefix.set(func.WHITE_SPACE.matcher(subtext1.get()).replaceAll(""));
                            prefixjs.put(server.getIdAsString(), prefix.get());
                            func.JsonToFile(prefixjs, "prefix.json");
                            event.getChannel().sendMessage(embed.setDescription(texte.getString("PrefixChanged").toString()));
                            if (server.canYouChangeOwnNickname() && server.getNickname(api.getYourself()).isPresent())
                                api.getYourself().updateNickname(server, server.getNickname(api.getYourself()).get().replaceAll("(?i)" + oldp, prefix.get()));
                        } else {
                            event.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte.get("Fehler2Title")).setDescription(texte.get("MisssingPermission", PermissionType.ADMINISTRATOR.name())));
                        }
                    } else {
                        event.getChannel().sendMessage(func.getRotEmbed(event).setDescription(texte.get("PrefixMax")));
                    }
                }
                return true;
            }

            //react
            if (befehl.get().equalsIgnoreCase("react")) {
                if (subtext1.get().equals("")) {
                    event.getChannel().sendMessage(func.Fehler("React", user));
                    return (false);
                } else {
                    if (func.StringBlank(subtext2.get()) || !(subbefehl1.get().matches("^[0-9]*$") || subbefehl1.get().startsWith(" https://discordapp.com/channels/"))) {
                        rps.set(true);
                        func.reactText(event, subtext1.get(), event.getMessage().getIdAsString());
                    } else {
                        func.reactText(event, subtext2.get(), subbefehl1.get());
                    }
                }
                return true;
            }

            //ship
            if(befehl.get().equals("ship")) {
                if(subbefehl1.get().isEmpty() || subbefehl2.get().isEmpty()) {
                    event.getChannel().sendMessage(func.Fehler("Ship", user));
                    return false;
                } else {
                    String[] str = {subbefehl1.get() + " & " + subbefehl2.get(), subbefehl2.get() + " & " + subbefehl1.get()};
                    long l = 1;
                    for (int i = 0; i < 2; i++) {
                        l *= str[i].replace("<@!", "").replace("<@", "").replace(">", "").hashCode();
                    }
                    Random r = new Random(l);
                    String percent;
                    do {
                        percent = (r.nextInt(10) + "," + r.nextInt(10) + "." + r.nextInt(10) + "," + r.nextInt(10)).replace(",", "");
                    } while(percent.equals("00.00") || r.nextInt(5) == 0);
                    event.getChannel().sendMessage(embed.setTitle(str[0]).setDescription(percent + "%")).join();
                    return true;
                }
            }
            //say
            if (befehl.get().equalsIgnoreCase("say")) {
                if (subtext1.get().equals("")) {
                    event.getChannel().sendMessage(func.Fehler("Say", user));
                    return (false);
                } else {
                    event.getChannel().sendMessage(func.getSayEmbed(event).setDescription(func.highlightMessageUrl(subtext1.get())));
                    if (Delete.get()) event.getMessage().delete(texte.getString("DeleteReason").toString());
                }
                return true;
            }
            //emote
            if (befehl.get().equalsIgnoreCase("emote")) {
                if (subtext1.get().equals("")) {
                    event.getChannel().sendMessage(func.Fehler("Emote", user));
                    return false;
                } else {
                    String e1 = subtext1.get().toLowerCase();
                    for (int i = 0; i < normalabc.length; i++) {
                        e1 = e1.replace(normalabc[i].toLowerCase(), helpabc[i].concat("\u200B"));
                    }
                    event.getChannel().sendMessage(func.getSayEmbed(event).setDescription(e1));
                    if (Delete.get()) event.getMessage().delete(texte.getString("DeleteReason").toString());
                }
                return true;
            }

            //invite
            if (befehl.get().equalsIgnoreCase("invite")) {
                embed.setTitle(texte.get("Invite"))
                        .setDescription(func.createBotInvite());
                event.getChannel().sendMessage(embed);
                return true;
            }
            //roll
            if (befehl.get().equalsIgnoreCase("roll")) {
                long i = func.LongFromString(subtext1.get(), 6L);
                if(i < 2) i = 6;
                if (i > 10) {
                    addRerun(event.getChannel().sendMessage(embed.setTitle("Roll 1 - " + i).setDescription(Long.toString(func.getRandomLong(1L, i)))));
                } else {
                    event.getMessage().addReaction(EmojiParser.parseToUnicode(zahl[func.getRandom(1, (int) i)]));
                }
                return true;
            }

            //Schere Stein Papier Brunnen //rps //sss
            if (befehl.get().equalsIgnoreCase("sspb") || befehl.get().equalsIgnoreCase("SSS") || befehl.get().equalsIgnoreCase("RPS")) {
                final String[] NameSSPB = {schere.getMentionTag(), stein.getMentionTag(), papier.getMentionTag(), brunnen.getMentionTag()};
                final KnownCustomEmoji[] EmojiSSPB = {schere, stein, papier, brunnen};

                AtomicReference<Boolean> Bbrunnen = new AtomicReference<>(false);
                AtomicReference<Boolean> sspbreacted = new AtomicReference<>(false);
                AtomicReference<Integer> sspb = new AtomicReference<>(0);
                AtomicReference<Integer> sspbG = new AtomicReference<>(0);
                if (idmentioneduser.get().isEmpty()) {
                    Bbrunnen.set(true);
                    rps.set(true);
                    sspbreacted.set(false);
                    event.getMessage().addReactions(schere, stein, papier, brunnen, ohne_brunnen);

                    event.getMessage().addReactionAddListener(event2 -> {

                        if (event2.getEmoji().isCustomEmoji() && event2.getUser().equals(user)) {
                            sspb.set(-1);
                            if (event2.getEmoji().equals(ohne_brunnen)) {
                                event2.removeReactionsByEmojiFromMessage(brunnen, ohne_brunnen);
                                Bbrunnen.set(false);
                            } else {

                                if (event2.getEmoji().equals(schere)) {
                                    sspb.set(0);
                                    event2.removeReactionsByEmojiFromMessage(ohne_brunnen, stein, papier, brunnen);
                                    event2.removeOwnReactionByEmojiFromMessage(schere);
                                }
                                if (event2.getEmoji().equals(stein)) {
                                    sspb.set(1);
                                    event2.removeReactionsByEmojiFromMessage(schere, ohne_brunnen, papier, brunnen);
                                    event2.removeOwnReactionByEmojiFromMessage(stein);
                                }
                                if (event2.getEmoji().equals(papier)) {
                                    sspb.set(2);
                                    event2.removeReactionsByEmojiFromMessage(schere, stein, ohne_brunnen, brunnen);
                                    event2.removeOwnReactionByEmojiFromMessage(papier);
                                }
                                if (event2.getEmoji().equals(brunnen) && Bbrunnen.get()) {
                                    sspb.set(3);
                                    event2.removeReactionsByEmojiFromMessage(schere, stein, papier, ohne_brunnen);
                                    event2.removeOwnReactionByEmojiFromMessage(brunnen);
                                }
                                if (!(-1 == sspb.get()) && !sspbreacted.get()) {
                                    sspbreacted.set(true);
                                    if (Bbrunnen.get()) {
                                        sspbG.set(func.getRandom(0, 3));

                                    } else {
                                        sspbG.set(func.getRandom(0, 2));
                                    }
                                    event.getChannel().sendMessage(func.rps(event, sspb, sspbG, NameSSPB, api.getYourself()));
                                }
                            }
                        }
                    }).removeAfter(30, TimeUnit.MINUTES);
                } else {
                    User user2 = event.getApi().getCachedUserById(idmentioneduser.get()).orElse(null);
                    if (user2 == null) return false;
                    int countMentionedUsers = 0;
                    List<User> mus = event.getMessage().getMentionedUsers();
                    // wenn nicht man selbst, oder Bot +1
                    for (int i = 0; i < mus.size(); i++) {
                        if (!(mus.get(i).isBot() || mus.equals(user))) {
                            Boolean b = true;
                            for (int j = 0; j < i; j++) {
                                if (mus.get(j).equals(mus.get(i))) b = false;
                                break;
                            }
                            if (b) countMentionedUsers++;
                        }
                    }
                    User[] u1 = new User[countMentionedUsers];
                    int count = 0;
                    for (int i = 0; i < mus.size(); i++) {
                        if (!(mus.get(i).isBot() || mus.equals(user))) {
                            Boolean b = true;
                            for (int j = 0; j < i; j++) {
                                if (mus.get(j).equals(mus.get(i))) b = false;
                                break;
                            }
                            if (b) {
                                u1[count] = mus.get(i);
                                count++;
                            }
                        }
                    }
                    if (u1.length < 2) {
                        final Texte texte2 = new Texte(user2);


                        Message messageUser1 = user.sendMessage(getNormalEmbed().setTitle(texte.get("SSSTitle")).setDescription(texte.get("SSSDesc"))).join();
                        Message messageUser2 = user2.sendMessage(getNormalEmbed().setTitle(texte2.get("SSSTitle")).setDescription(texte2.get("SSSDesc"))).join();

                        messageUser1.addReactions(schere, stein, papier, brunnen, ohne_brunnen);
                        messageUser2.addReactions(schere, stein, papier, brunnen, ohne_brunnen);

                        sspb.set(-1);
                        sspbG.set(-1);

                        AtomicReference<Boolean> booleanAtomicReference = new AtomicReference<>(false);
                        AtomicReference<Boolean> B = new AtomicReference<>(true);

                        messageUser1.addReactionAddListener(event2 -> {
                            if (event2.getUserId() != event2.getApi().getYourself().getId()) {
                                if (event2.getEmoji().asKnownCustomEmoji().orElse(brunnen).equals(ohne_brunnen)) {
                                    if (sspb.get() == -1 && sspbG.get() == -1) {
                                        B.set(false);
                                        messageUser2.removeOwnReactionsByEmoji(brunnen, ohne_brunnen);
                                        messageUser1.removeOwnReactionsByEmoji(brunnen, ohne_brunnen);
                                    } else
                                        event2.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte.get("NichtMehrMöglich")).setDescription(texte.get("SSSGewählt")));
                                } else if (event2.getEmoji().asKnownCustomEmoji().orElse(ohne_brunnen).equals(brunnen)) {
                                    if (B.get()) sspb.set(3);
                                    else
                                        event2.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte.get("NichtMehrMöglich")).setDescription(texte.get("SSSOhneBrunnen")));
                                } else {
                                    for (int i = 0; i < EmojiSSPB.length; i++)
                                        if (event2.getEmoji().asKnownCustomEmoji().orElse(ohne_brunnen).equals(EmojiSSPB[i]))
                                            sspb.set(i);
                                }
                                if (sspb.get() != -1)
                                    messageUser1.edit(event2.getMessage().orElse(messageUser1).getEmbeds().get(0).toBuilder().removeAllFields().addField("\u200B", NameSSPB[sspb.get()]));
                                if (!(sspb.get() == -1) && !(sspbG.get() == -1)) {
                                    if (!booleanAtomicReference.get())
                                        addRerun(event.getChannel().sendMessage(func.rps(event, sspb, sspbG, NameSSPB, user2)));
                                    booleanAtomicReference.set(true);
                                }
                            }
                        }).removeAfter(30L, TimeUnit.MINUTES);

                        messageUser2.addReactionAddListener(event2 -> {
                            if (event2.getUserId() != event2.getApi().getYourself().getId()) {
                                if (event2.getEmoji().asKnownCustomEmoji().orElse(brunnen).equals(ohne_brunnen)) {
                                    if (sspb.get() == -1 && sspbG.get() == -1) {
                                        B.set(false);
                                        messageUser2.removeOwnReactionsByEmoji(brunnen, ohne_brunnen);
                                        messageUser1.removeOwnReactionsByEmoji(brunnen, ohne_brunnen);
                                    } else
                                        event2.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte2.get("NichtMehrMöglich")).setDescription(texte2.get("SSSGewählt")));
                                } else if (event2.getEmoji().asKnownCustomEmoji().orElse(ohne_brunnen).equals(brunnen)) {
                                    if (B.get()) sspbG.set(3);
                                    else
                                        event2.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte2.get("NichtMehrMöglich")).setDescription(texte2.get("SSSOhneBrunnen")));
                                } else {
                                    for (int i = 0; i < EmojiSSPB.length; i++)
                                        if (event2.getEmoji().asKnownCustomEmoji().orElse(ohne_brunnen).equals(EmojiSSPB[i]))
                                            sspbG.set(i);
                                }
                                if (sspbG.get() != -1)
                                    messageUser2.edit(event2.getMessage().orElse(messageUser2).getEmbeds().get(0).toBuilder().removeAllFields().addField("\u200B", NameSSPB[sspbG.get()]));
                                if (!(sspb.get() == -1) && !(sspbG.get() == -1)) {
                                    if (!booleanAtomicReference.get())
                                        addRerun(event.getChannel().sendMessage(func.rps(event, sspb, sspbG, NameSSPB, user2)));
                                    booleanAtomicReference.set(true);
                                }
                            }
                        }).removeAfter(30L, TimeUnit.MINUTES)
                        .addRemoveHandler(()->{
                            if (!booleanAtomicReference.get())
                                addRerun(event.getChannel().sendMessage(func.rps(event, sspb, sspbG, NameSSPB, user2)));
                        });
                    } else {
                        User[] u = new User[u1.length + 1];
                        u[u1.length] = user;
                        for (int i = 0; i < u1.length; i++) {u[i] = u1[i]; }
                        AtomicReference<Boolean> booleanAtomicReference = new AtomicReference<>(false);
                        AtomicReference<Boolean> B = new AtomicReference<>(true);

                        AtomicReference<Message[]> messages = new AtomicReference<>(new Message[u.length]);
                        AtomicIntegerArray sspbA = new AtomicIntegerArray(u.length);
                        for (int i = 0; i < u.length; i++) {
                            sspbA.set(i, -1);
                            final Texte t = new Texte(u[i]);
                            Message[] m = messages.get();
                            m[i] = u[i].sendMessage(getNormalEmbed().setTitle(t.get("SSSTitle"))
                                    //.setDescription(t.get("SSSDesc")))
                            ).join();
                            messages.set(m);
                            messages.get()[i].addReactions(schere, stein, papier, brunnen, ohne_brunnen); //, brunnen, ohne_brunnen
                            final int i2 = i;
                            messages.get()[i].addReactionAddListener(event2 -> {
                                if (event2.getUserId() != event2.getApi().getYourself().getId()) {
                                    if (event2.getEmoji().asKnownCustomEmoji().orElse(brunnen).equals(ohne_brunnen)) {
                                        if (func.allSame(sspbA, -1)) {
                                            B.set(false);
                                            for (int j = 0; j < u.length; j++) if(messages.get()[j] != null) messages.get()[j].removeOwnReactionsByEmoji(brunnen, ohne_brunnen);
                                        } else event2.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte.get("NichtMehrMöglich")).setDescription(texte.get("SSSGewählt")));
                                    } else if (event2.getEmoji().asKnownCustomEmoji().orElse(ohne_brunnen).equals(brunnen)) {
                                        if (B.get()) sspbA.set(i2,3);
                                        else
                                            event2.getChannel().sendMessage(func.getRotEmbed(event).setTitle(texte.get("NichtMehrMöglich")).setDescription(texte.get("SSSOhneBrunnen")));
                                    } else {
                                      for (int j = 0; j < EmojiSSPB.length; j++) if (event2.getEmoji().asKnownCustomEmoji().orElse(ohne_brunnen).equals(EmojiSSPB[j])) sspbA.set(i2,j);
                                    }
                                    if (sspbA.get(i2) != -1 && !func.allNotSame(sspbA, -1)) messages.get()[i2].edit(event2.getMessage().orElse(messages.get()[i2]).getEmbeds().get(0).toBuilder().removeAllFields().addField("\u200B", NameSSPB[sspbA.get(i2)]));

                                    if (func.allNotSame(sspbA, -1)) {
                                        if (!booleanAtomicReference.get()) addRerun(event.getChannel().sendMessage(func.rps(event, u, sspbA, NameSSPB)));
                                        booleanAtomicReference.set(true);
                                    }
                                }
                            }).removeAfter(30L, TimeUnit.MINUTES)
                            .addRemoveHandler(()->{
                                if(!booleanAtomicReference.get()) {
                                    for (int j = 0; j < u.length; j++)
                                        if (messages.get()[j] != null)
                                            messages.get()[j].removeOwnReactionsByEmoji(schere, stein, papier);
                                    event.getChannel().sendMessage(getRotEmbed().setDescription(texte.get("ZeitAbgelaufen")));
                                }
                            });
                        }
                    }
                }
                return true;
            }

            //report
            if (befehl.get().equalsIgnoreCase("report")) {
                if (subtext1.get().isEmpty()) {
                    event.getChannel().sendMessage(func.Fehler("Report", user));
                    return (false);
                } else {
                    event.getChannel().sendMessage(embed.setDescription(texte.get("ReportText")));

                    func.OWNER.ifPresent(u -> u.sendMessage(embed.setDescription(subtext1.get()).setTitle("Report (Server: " + server.getName()).addField( "UserID:", user.getIdAsString())));
                    return true;
                }
            }

            //"Guildinvite", "Guild-invite",
            if (befehl.get().equalsIgnoreCase("Guild-invite") || befehl.get().equalsIgnoreCase("Guildinvite")) {
                if (subtext1.get().isEmpty()) {
                    event.getChannel().sendMessage(func.Fehler("Guild-Invite", user));
                    return false;
                } else {
                    if (server.hasPermission(user, PermissionType.CREATE_INSTANT_INVITE)) {
                        try {
                            int i1 = -1;
                            int i2 = -1;
                            String[] nums = subtext1.get().replaceAll("  ", " ").replaceAll("  ", " ").split(" ");
                            for (int n = 0; n < nums.length; n++) {
                                if (!(nums[n].replaceAll(" ", "").isEmpty())) {
                                    if (i1 == -1) {
                                        i1 = Integer.parseInt(nums[n]);
                                    } else {
                                        i2 = Integer.parseInt(nums[n]);
                                    }

                                }
                            }

                            if (i1 < 1) i1 = 1;
                            if (i2 < 1) i2 = 1;
                            if (i2 > 100) i2 = 0;

                            if (i1 > 24) {
                                new InviteBuilder(event.getChannel().asServerChannel().get())
                                        .setNeverExpire()
                                        .setMaxUses(i2)
                                        .create().thenAccept(invite -> event.getChannel().sendMessage(invite.getUrl().toString()));
                            } else {
                                new InviteBuilder(event.getChannel().asServerChannel().get())
                                        .setMaxAgeInSeconds(i1 * 60 * 60) //Alter in Stunden
                                        .setMaxUses(i2)
                                        .create().thenAccept(invite -> event.getChannel().sendMessage(invite.getUrl().toString()));
                            }
                        } catch (NullPointerException e) {
                            func.handle(e);
                            event.getChannel().sendMessage(getRotEmbed().setDescription(texte.get("FehlerAufgetreten")));
                            return false;
                        }
                    } else {
                        event.getChannel().sendMessage(func.Fehler2("Serverinvite", user));
                        return false;
                    }
                }
                return true;
            }

            if(befehl.get().equalsIgnoreCase("rnd_4g")) {
                VierGewinnt vg = new VierGewinnt(this, api.getYourself(), api.getYourself().getId(), !subtext1.get().contains("3"));
                embed.setTitle(texte.get("4GTitle")).addField("\u200B", vg.getPlayers());
                event.getChannel().sendMessage(embed.setDescription(vg.getDisplayField())).thenAccept(vg::setMessage);
                return true;
            }
            //4 gewinnt //4g //connect //c4
            if (befehl.get().equalsIgnoreCase("4g") || befehl.get().equalsIgnoreCase("c4") || befehl.get().equalsIgnoreCase("4gewinnt") || befehl.get().equalsIgnoreCase("4-gewinnt") || befehl.get().equalsIgnoreCase("connect-four")) {
                if (idmentioneduser.get().isEmpty()) {
                    idmentioneduser.set(api.getYourself().getIdAsString());
                }
                try {
                   VierGewinnt vg = new VierGewinnt(this, user, Long.parseLong(idmentioneduser.get()), true);
                    event.getChannel().sendMessage(embed.setTitle(texte.get("4GTitle")).setDescription(vg.getDisplayField()).addField("\u200B", vg.getPlayers())).thenAccept(message4g -> {
                        String[] reactions = {EmojiParser.parseToUnicode(":one:"), EmojiParser.parseToUnicode(":two:"), EmojiParser.parseToUnicode(":three:"), EmojiParser.parseToUnicode(":four:"), EmojiParser.parseToUnicode(":five:"), EmojiParser.parseToUnicode(":six:"), EmojiParser.parseToUnicode(":seven:")};
                        message4g.addReactions(reactions).join();
                        message4g.edit(embed.setDescription(vg.setMessage(message4g)));
                        message4g.addReactionAddListener(event2 -> {
                            if (event2.getUserId() != event2.getApi().getYourself().getId() && !vg.isEnded()) {
                                vg.doMove(event2);
                                if(!vg.isEnded()) message4g.edit(embed.setDescription(vg.getDisplayField()));
                            }
                        }).removeAfter(100L, TimeUnit.MINUTES).addRemoveHandler(()->{
                            if(!vg.isEnded()) {
                                message4g.edit(embed.setDescription(vg.getDisplayField()).addField(texte.get("ZeitAbgelaufen"), texte.get("4GUnentschieden")));
                                message4g.removeAllReactions().join();
                            }
                        });
                    });
                } catch (Exception e) {
                    func.handle(e);
                    return false;
                }
                return true;
            }

            //dg //dice-game
            if(befehl.get().equalsIgnoreCase("dg") || befehl.get().equalsIgnoreCase("dice-game")) {
                boolean einsatz;
                BigInteger coins;
                Coins c = new Coins(user);
                try {
                    coins = new BigInteger(func.NO_NUMBER.matcher(subbefehl1.get()).replaceAll(""));
                    einsatz = coins.compareTo(BigInteger.ZERO) > 0;

                    if(einsatz && !c.hasCoins(coins)) {
                        event.getChannel().sendMessage(getRotEmbed().setTitle("Dice-Game").setDescription(texte.get("CoinsMissing")).addField("Coins:", texte.get("CoinsMessage", user.getIdAsString(), c.getCoins().toString()))).join();
                        return false;
                    }
                } catch(NumberFormatException e) {
                    einsatz = false;
                    coins = null;
                }

                int[][] wuerfe = new int[2][3];
                int[] ergebnisse = new int[2];
                String[] names =  {user.getDisplayName(server), api.getYourself().getDisplayName(server)};
                for (int i = 0; i < 2; i++) {
                    wuerfe[i] = func.randomInts(1, 6, 3);
                    ergebnisse[i] = (int) (wuerfe[i][0] + Math.pow(wuerfe[i][1], 2) + Math.pow(wuerfe[i][2], 3));
                    embed.addField(names[i] + ":" ,zahl[wuerfe[i][0]] + " ^ 1 + " + zahl[wuerfe[i][1]] + " ^ 2 + " + zahl[wuerfe[i][2]] + " ^ 3 = " + ergebnisse[i]);
                }
                int won = Integer.compare(ergebnisse[0], ergebnisse[1]); //the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y
                if(einsatz) {
                    String oldcoins = c.getCoins().toString();
                    if(won == 0)
                        embed.addField("Coins:", oldcoins  + " + 0 = " + oldcoins + " " + texte.get("Coins"));
                    else if(won > 0)
                        embed.addField("Coins:", oldcoins  + " + " + c.addCoins(coins).toString() + " = " + c.getCoins().toString() + " " + texte.get("Coins"));
                    else {
                        c.removeCoins(coins);
                        embed.addField("Coins:", oldcoins + " - " + coins.toString() + " = " + c.getCoins().toString() + " " + texte.get("Coins"));
                    }
                }
                if(won == 0) embed.setDescription(texte.get("4GUnentschieden"));
                else if(won > 0) embed.setDescription(names[0] + " " + texte.get("4GGewonnen"));
                else embed.setDescription(names[0] + " " + texte.get("4GVerloren"));

                addRerun(event.getChannel().sendMessage(embed.setTitle("Dice-Game:"))).join();
                return true;
            }

            if(befehl.get().equalsIgnoreCase("rnd_ttt")) {
                TTT ttt = new TTT(this, api.getYourself(), api.getYourself().getId());
                embed.setTitle(texte.get("TTTTitle")).addField("\u200B", ttt.getPlayers());
                event.getChannel().sendMessage(embed.setDescription(ttt.getMessage())).thenAccept(ttt::setMessage);
                return true;
            }
            //Tic Tac Toe //ttt
            if (befehl.get().equalsIgnoreCase("ttt") || befehl.get().equalsIgnoreCase("TicTacToe")) {
                if (idmentioneduser.get().isEmpty()) {
                    idmentioneduser.set(event.getApi().getYourself().getIdAsString());
                }
                try {
                    TTT ttt = new TTT(this, user, Long.parseLong(idmentioneduser.get()));
                    embed.setTitle(texte.get("TTTTitle")).addField("\u200B", ttt.getPlayers());
                    event.getChannel().sendMessage(embed.setDescription(ttt.getMessage())).thenAccept(tttmessage -> {
                        tttmessage.addReactions(EmojiParser.parseToUnicode(":one:"), EmojiParser.parseToUnicode(":two:"), EmojiParser.parseToUnicode(":three:"), EmojiParser.parseToUnicode(":four:"), EmojiParser.parseToUnicode(":five:"), EmojiParser.parseToUnicode(":six:"), EmojiParser.parseToUnicode(":seven:"), EmojiParser.parseToUnicode(":eight:"), EmojiParser.parseToUnicode(":nine:")).join();
                        tttmessage.edit(embed.setDescription(ttt.setMessage(tttmessage)));
                        tttmessage.addReactionAddListener(event2 -> {
                            if (event2.getUserId() != event2.getApi().getYourself().getId() && !ttt.isEnded()) {
                                ttt.doRound(event2);
                                if (!ttt.isEnded()) tttmessage.edit(embed.setDescription(ttt.getMessage()));
                            }
                        }).removeAfter(60L, TimeUnit.MINUTES)
                        .addRemoveHandler(()->{
                            if (!ttt.isEnded()) {
                                tttmessage.edit(embed.addField(texte.get("ZeitAbgelaufen"), texte.getString("4GUnentschieden").toString()));
                                tttmessage.removeAllReactions();
                                //if(!idmentioneduser.get().equals(api.getYourself().getIdAsString()))
                                //    func.addGame0("ttt", user.getIdAsString(), idmentioneduser.get());
                            }
                        });
                    });
                } catch (Exception e) {
                    sendOwner("TTT:\n" + e.toString());
                    return false;
                }
                return true;
            }
            
            //help
            if (befehl.get().equalsIgnoreCase("help")) {
                AtomicReference<Integer> site = new AtomicReference<>(0);
                String[] kategorien = texte.get("Kategorien").split(" ");

                String Bot = "Invite Report Help Language";
                String Bilder = "Fake-Person Fake-Cat Fake-Horse Fake-Art Random-Picture Random-Robot Random-Face Random-Alien Random-Cat Random-Human Resize QR";
                String Memes = "Donald WTH Lisa Drake Winnie";
                String Spaß = "8-Ball Emote React Roll Say Ping Pong";
                if (server.getId() == GuildUtilities.AN || server.getId() == 563387219620921347L) Spaß += " Zitat GOQ";
                String Spiele = "4-Gewinnt SSS TTT Stats Dice-Game";
                String Server = "Rank Top Prefix GuildInvite Clear Welcome-Message Leave-Message Backup Reaction-Role";
                String Anderes = "Save SaveAs Save-Private Load Delete Rename Edit Calculate";


                String befehlestr = Bot + " \u200B " + Bilder + " \u200B "+ Memes + " \u200B " + Spaß + " \u200B " + Spiele + " \u200B "+ Server + " \u200B " + Anderes;
                String[] befehle = (befehlestr.replace("J!", func.WHITE_SPACE.matcher(prefix.get()).replaceAll(""))).split(" \u200B ");

                //IDK WHAT THIS LINE WANTS TO DO:
                //befehlestr = " " + texte.get("Kategorien") + " " + befehlestr.replace("J!", "") + " ";

                StringBuilder helpstr = new StringBuilder();
                //static final String[] helpstrings = {"Die W�rfel werden gefallen sein. Nutze \"+**roll** (Zahl)\", um einen W�rfel mit so vielen Seiten zu werfen. Wenn du kein Argument nutzt sind es 6 Seiten.", "Pong - Ping.", "Zwinge mich etwas zusagen.", "Wenn du jemandem anonym deinen Hass gestehen willst.", "Nutze +**4g {User}** oder +**4-gewinnt {User}**, um gegen den makierten User dieses hervorangende Spiel spielen zu k�nnen, ohne Discord verlassen zu m�ssen.", "Nutze +**sspb** oder +**sss**, um gegen den Bot **Schnick-Schnack-Schnuck** zu spielen.", "Wenn du diesen tollen Bot verbreiten willst.", "Sende Joshix#6613 anonym Hate-Kommentare.", "Erstelle einen Server-Invite mit: \"+**Serverinvite** [Dauer in h] [Anzahl Aufrufe]\". Wenn die Dauer �ber 24h ist, dann l�uft er nie ab. Wennn die Anzahl der Aufrufe h�her als 100 ist, gibt es kein Maximum.", "Wie der Say-Befehl nur in cool.", "Reagiere auf eine Nachricht mit einem Text, nutze \"+react [Text] (MessageId)\". Wenn keine MessageId angegeben ist, dann reagiert der Bot auf die Nachricht mit dem Befehl. Wenn ein Buchstabe doppelt enthalten ist, dann wird nur der Erste beachtet.", "Spiele gegen einen Freund Tic Tac Toe, nutze \"+ttt {Mitspieler}\". \nDas TicTacToe-Feld ist so aufgebaut: \n:one: :two: :three:\n:four: :five: :six:\n:seven: :eight: :nine:\n\n**Tipp:** Wenn du am Pc bist kannst du einfach mit deiner Maus �ber ein leeres Feld fahren und so sehen, um welche Nummer es sich handelt.", "Nutze \"**+Fake-Person**\"/\"**+Fp**\", um dir ein Bild einer nicht realen, von einer k�nstlichen Intelligenz erstellten Person anzusehen.", "Nutze \"**+Fake-Cat**\"/\"**+Fc**\", um dir ein Bild einer nicht realen, von einer k�nstlichen Intelligenz erstellten Katze anzusehen.", "Wenn du mal nicht wei�t, ob du etwas tun willst, nutze einfach \"**+8-Ball**\"/\"**+8ball**\", um eine einfache Antwort auf deine Frage zu bekommen. Das funktioniert ||fast|| immer.", "Lasse den Bot zuf�llige Bilder von [robohash.org](https://robohash.org/) senden. \n**+Random-Picture**/**RP**, um ein zuf�lliges Bild, der unten beschriebenden zu bekommen.\n**+Random-Robot**/**RR**, um ein Bild eines Roboters zu bekommen.\n**+Random-Face**/**RF**, um ein Bild eines Roboter-Kopfes zu erhalten.\n**+Random-Alien**/**RA**, um ein Bild eines Aliens zu kriegen.\n**+Random-Cat**/**RC**, um ein Bild einer Katze zu erhalten.\n**+Random-Robot**/**RR**, um ein Bild eines Roboters zu bekommen.", "Nutze \"**+Prefix** [neues Prefix]\", um f�r dich ein anderes Prefix f�r die Bot-Befehle zu setzen."};

                for (int i = 0; i < kategorien.length; i++) {
                    helpstr.append(helpabc[i]).append(" -> ").append(kategorien[i]).append("\n");
                }
                final String nhelp = helpstr.toString();
                EmbedBuilder helpembed = embed;


                helpembed.setTitle("Help").setDescription(nhelp).setThumbnail("https://cdn2.iconfinder.com/data/icons/flat-style-svg-icons-part-1/512/confirmation_verification-512.png").setAuthor(event.getApi().getYourself().getDiscriminatedName(), func.createBotInvite(), event.getApi().getYourself().getAvatar());


                event.getChannel().sendMessage("", helpembed).thenAccept(helpmessage -> {
                    helpmessage.addReaction(EmojiParser.parseToUnicode(":arrow_backward:"));
                    func.react(helpmessage, helpabc, 12);

                    //if(!func.stringIsBlank(subtext1.get()) && befehlestr.toLowerCase().contains(" " + subtext1.get().toLowerCase() +  " ")) {
                    //
                    //}

                    helpmessage.addReactionAddListener((event2 -> {
                        if (event2.getUserId() != event2.getApi().getYourself().getId() && event2.getUserId() == user.getId()) {
                            event2.removeReaction();
                            if (event2.getReaction().get().getEmoji().equalsEmoji(EmojiParser.parseToUnicode(":arrow_backward:")) && !event2.getMessage().get().getEmbeds().get(0).getTitle().get().equals("Help")) {
                                helpmessage.edit("", helpembed.removeAllFields().setDescription(nhelp).setTitle("Help"));
                            } else {
                                if (event2.getMessageContent().get().isEmpty()) {
                                    for (int i = 0; i < befehle.length; i++) {
                                        if (event2.getUserId() != event2.getApi().getYourself().getId() && event2.getReaction().get().getEmoji().equalsEmoji(helpabc[i].replaceAll("\u200B", ""))) {
                                            EmbedBuilder helpembed2 = helpembed;
                                            String[] befehle2 = befehle[i].split(" ");
                                            String helpstr2 = "";
                                            for (int i2 = 0; i2 < befehle2.length; i2++) {
                                                helpembed2.addField(helpabc[i2] + " -> " + texte.get(befehle2[i2] + "Arguments"), texte.get(befehle2[i2] + "Helptext"));
                                            }
                                            site.set(i);
                                            helpembed2.setDescription(helpstr2).setTitle(kategorien[i]).addField("\u200B", texte.get("HelpErklärung"));
                                            helpmessage.edit("\u200B", helpembed2);
                                            break;
                                        }
                                    }
                                } else if (event2.getMessageContent().get().startsWith("\u200B")) {
                                    for (int i = 0; i < kategorien.length; i++) {
                                        if (event2.getMessage().orElse(helpmessage).getEmbeds().get(0).getTitle().orElse("").equals(kategorien[i]))
                                            site.set(i);
                                        break;
                                    }
                                    String[] befehle2 = befehle[site.get()].split(" ");
                                    for (int i = 0; i < befehle2.length; i++) {
                                        if (event2.getUserId() != event2.getApi().getYourself().getId() && event2.getReaction().get().getEmoji().equalsEmoji(helpabc[i].replaceAll("\u200B", ""))) {
                                            if (!helpmessage.getEmbeds().isEmpty() && helpmessage.getEmbeds().get(0).getFields().size() > i && (helpmessage.getEmbeds().get(0).getFields().get(i).getValue().toLowerCase().contains("e.g.:") || helpmessage.getEmbeds().get(0).getFields().get(i).getValue().toLowerCase().contains("z.b.:"))) {
                                                String text = helpmessage.getEmbeds().get(0).getFields().get(i).getValue().substring(helpmessage.getEmbeds().get(0).getFields().get(i).getValue().lastIndexOf(prefix.get()));
                                                if (text.toLowerCase().contains("report"))
                                                    event.getChannel().sendMessage(func.getNormalEmbed(event).setDescription(texte.get("ReportText")).setTimestampToNow());
                                                else {
                                                    try {
                                                        new Befehl(event).setAll(text.replace("\"", "")).fa();
                                                    } catch (Exception e) {func.handle(e);};
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })).removeAfter(40L, TimeUnit.MINUTES)
                    .addRemoveHandler(()->{
                        helpmessage.removeAllReactions().join();
                    });
                });
                return true;
            }
        return false;
    }

    public final EmbedBuilder getNormalEmbed() {
        return func.getNormalEmbed(event);
    }

    public final EmbedBuilder getRotEmbed() {
        return func.getRotEmbed(event);
    }

    public final EmbedBuilder getGruenEmbed() {
        return func.getGruenEmbed(event);
    }

    public void sendOwner (String text) {
        func.sendOwner(text, event);
    }
}
