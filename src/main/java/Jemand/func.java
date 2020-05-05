package Jemand;

import Jemand.Listener.CommandCleanupListener;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.io.FileUtils;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.DiscordRegexPattern;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class func {
    private static final Pattern COMPILE = Pattern.compile("\\D");
    public static final Pattern NO_NUMBER = COMPILE;
    public static final Pattern NO_WORD = Pattern.compile("\\W");
    public static final Pattern WHITE_SPACE = Pattern.compile("\\s+");

    public static final String[] pws = WHITE_SPACE.split(readtextoffile("secret/pw.txt"));


    private static String token = pws[6];
    static private DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
    public static final int PERMISSIONS = 604892353;
    public static Pattern RANDOM = Pattern.compile("(?i)<rand(?<min>\\d+):(?<max>\\d+)>");
    public static final String SALT = hashString(pws[0], false, 256);
    public static final String KEY = createCryptKey2(hashString( pws[1], false, 256));
    public static final Optional<User> OWNER = Optional.ofNullable(api.getOwner().exceptionally((e) -> null).join());

    private static GitHub github;

    static {
        try {
            github = new GitHubBuilder().withPassword("JemandBot", pws[2]).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private static Config config = new Config();


    static {
         config.useSingleServer().setAddress("redis://92.60.37.109:6379").setPassword("jemand 3398b573c024b4e5c7118f26d6c57e1023a2326e").setDatabase(0);

         config.setCodec(StringCodec.INSTANCE);
    }
    //redis-cli -h 92.60.37.109 --user jemand --pass 3398b573c024b4e5c7118f26d6c57e1023a2326e PING
    private static final RedissonClient redis = Redisson.create(config);
    */

    //C:\Users\dingd\OneDrive\Dokumente\OneDrive\
    static private final String normalfilepathwin = "C:\\Users\\dingd\\OneDrive\\Dokumente\\OneDrive\\Jemand_Dateien";
    static private final String normalfilepathlinux = "/usr/home/admin/Jemand_Dateien";
    static public final String[] NORMALABC = "a\tb\tc\td\te\tf\tg\th\ti\tj\tk\tl\tm\tn\to\tp\tq\tr\ts\tt\tu\tv\tw\tx\ty\tz\t10\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9\t#\t*\t+\t-\tä\tö\tü\tß\t?\t!\t&\t ".split("\t");
    static public final String[] EMOJIABC = "\uD83C\uDDE6 \uD83C\uDDE7 \uD83C\uDDE8 \uD83C\uDDE9 \uD83C\uDDEA \uD83C\uDDEB \uD83C\uDDEC \uD83C\uDDED \uD83C\uDDEE \uD83C\uDDEF \uD83C\uDDF0 \uD83C\uDDF1 \uD83C\uDDF2 \uD83C\uDDF3 \uD83C\uDDF4 \uD83C\uDDF5 \uD83C\uDDF6 \uD83C\uDDF7 \uD83C\uDDF8 \uD83C\uDDF9 \uD83C\uDDFA \uD83C\uDDFB \uD83C\uDDFC \uD83C\uDDFD \uD83C\uDDFE \uD83C\uDDFF :keycap_ten: :zero: :one: :two: :three: :four: :five: :six: :seven: :eight: :nine: :hash: :asterisk: ➕ ➖ <:ae:703320745782018179> <:oe:703320746188865637> <:ue:703320746134601758> <:ss:703174443148509264> ❓ ❗ <:und:invalid:> <:leerzeichen:703321360180445224>".split(" ");

    static public void shutdown() {
        //redis.shutdown();
        api.disconnect();
        api.getThreadPool().getExecutorService().shutdown();
    }

    public static String getGithub(String repository, String file) throws IOException {
        File f = getGithub(tempFile(file.replaceAll(".*\\.", "")), repository, file);
        String text = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        f.delete();

        return text;
    }

    static File getGithub(File f, String repository, String file) throws IOException {
        GHContent content = github.getOrganization("asozialesnetzwerk").getRepository(repository).getFileContent(file);
        FileUtils.copyInputStreamToFile(content.read(), f);
        return f;
    }

    public static void setGithub(String repository, String file, String text) throws IOException {
        GHContent content = github.getOrganization("asozialesnetzwerk").getRepository(repository).getFileContent(file);

        content.update(text.getBytes(StandardCharsets.UTF_8), "by Jemand");
    }

    //static public void setRedisKey(String key, String MapKey, String value) {
    //    //redis.getMap("jemand:" + key).fastPut(MapKey, value);
    //}
//
    //static public String getRedisKey(String key, String MapKey) {
    //    //return redis.getMap("jemand:" + key).get(MapKey).toString();
    //}

    static public final EmbedBuilder getNormalEmbed(MessageCreateEvent event) {
        if(event == null) return getNormalEmbed(null, null);
        else return getNormalEmbed(event.getMessageAuthor().asUser().orElse(api.getYourself()), event.getMessage());
    }

    static public final EmbedBuilder getNormalEmbed(User user, Message message) {
        if(user == null) user = api.getYourself();
        Instant i;
        long id;
        if(message == null) {
            i = Instant.now();
            id = 0L;
        } else {
            id = message.getId();
            i = message.getCreationTimestamp();
        }

        EmbedBuilder e = new EmbedBuilder()
                .setColor(new Color(getRandom(0, 200), getRandom(5, 255), getRandom(5, 255)));

        return CommandCleanupListener.insertResponseTracker(
                e.setTimestamp(i),
              id,
              new Texte(user, null).get("NormalEmbedFooter"),
              user.getAvatar());
    }

    static public final EmbedBuilder getSayEmbed(MessageCreateEvent event) {
        return getNormalEmbed(event).setFooter(new Texte(event.getMessageAuthor().asUser().orElse(api.getYourself()), null).get("NormalEmbedFooter"), event.getMessageAuthor().getAvatar());
    }

    static public final EmbedBuilder getRotEmbed(MessageCreateEvent event) {
        return setColorRed(getNormalEmbed(event));
    }

    static public EmbedBuilder setColorRed(EmbedBuilder embed) {
        return embed.setColor(new Color(getRandom(250,255), getRandom(0,5),getRandom(0,5)));
    }

    static public final EmbedBuilder getGruenEmbed(MessageCreateEvent event) {
        return getNormalEmbed(event)
                .setColor(new Color(getRandom(0,5), getRandom(250,255),getRandom(0,5)));
    }

    static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    static public void reactText(MessageCreateEvent event, String text, String MessageID) {
        text = text.toLowerCase();
        Boolean b1 = true;
        Optional<Message> message = event.getApi().getCachedMessageByLink(MessageID);
        if(message.isEmpty()) message =  event.getApi().getCachedMessageById(MessageID);
        if(message.isEmpty()) {
            if (event.getServer().isPresent()) {
                for (int j = 0; j < event.getServer().get().getTextChannels().size() - 1; j++) {
                    try {
                        message = Optional.ofNullable(event.getApi().getMessageById(MessageID, event.getServer().get().getTextChannels().get(j)).exceptionally(null).join());
                        if(message.isPresent()) {
                            break;
                        }
                    } catch (Exception e) {
                        func.handle(e);
                    }
                }
            }
        }
        StringBuilder did = new StringBuilder(" ");
        try{
            Message message1 = message.orElse(event.getMessage());
            for (int i = 0; i < text.length(); i++) {
                if((text+"   ").substring(i, i + 2).equalsIgnoreCase("10") && !did.toString().contains("|")) {
                    message1.addReaction("\uD83D\uDD1F");
                    did.append("|");
                    i++;
                } else {
                    if (!did.toString().toLowerCase().contains((text).substring(i, i + 1).toLowerCase())) {
                        message1.addReaction(EmojiParser.parseToUnicode(parseLetterToEmote(text.substring(i, i + 1)))).join();
                        did.append(text, i, i + 1);
                    } else {
                        message1.addReaction(EmojiParser.parseToUnicode(text.substring(i, i + 1).replace("o", ":o2:").replace("a", ":a:").replace("b", ":b:").replace("i","i1:703595173333958696").replace("t", "t1:703595173342347314").replace(" ", "leerzeichen:703321360180445224"))).join();
                    }
                }
            }
        } catch (Exception e) {
            event.getMessage().addReaction("❌");
            func.handle(e);
        }
    }

    public static String createBotInvite() {
        try {
            return api.createBotInvite(Permissions.fromBitmask(604892353));
        } catch (Exception e) {e.printStackTrace();};
        return "";
    }

     public static void sendOwner(String text, MessageCreateEvent event) {
        try {
            if(event != null) {
                OWNER.ifPresent(u -> u.sendMessage(getNormalEmbed(event).setDescription(text)));
            } else {
                OWNER.ifPresent(u -> u.sendMessage(text));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //func.handle(e);
        }
    }

    static public String parseLetterToEmote (String letter) {
        for (int i = 0; i < NORMALABC.length; i++) {
            if(NORMALABC[i].equalsIgnoreCase(letter)){
                return EmojiParser.parseToUnicode(EMOJIABC[i]);
            }
        }
        return "";
    };
    //"\t#\t*\t+\t-\t?\t!\t&" " :hash: :asterisk: <:plus:576765085582622740> <:minus:576765084492365838> <:fragezeichen:576765084995420180> <:ausrufezeichen:576765084626321410> <:und:576765085565845504>"
    static public void react (Message message, String[] helpabc, Integer i2) {
        for (int i = 0; i < i2; i++) {
            message.addReaction(EmojiParser.parseToUnicode(helpabc[i])).join();
        }
    }

    static <k> k[] reverseArr(k[] a) {
        int n = a.length;
        k[] b = Arrays.copyOf(a, n);
        int j = n;
        for (int i = 0; i < n; i++) {
            b[j - 1] = a[i];
            j = j - 1;
        }

        return b;
    }

    //rand
    static String replaceRandom(String text) {
        Matcher random = RANDOM.matcher(text);
        while(random.find()) {
            long min = func.LongFromString(random.group("min"), 0);
            long max = func.LongFromString(random.group("max"), min + 1);
            if(min >= max) max= min +1;
            text = random.replaceFirst(Long.toString(func.getRandomLong(min, max)));
            random.reset(text);
        }
        return text;
    }

    //diff
    static int differenceOf(int i1, int i2) {
        if(i1 > i2){
            return i1 - i2;
        } else {
            return i2 - i1;
        }
    }

    //rps2
    static EmbedBuilder rps(MessageCreateEvent event, User[] users, AtomicIntegerArray ints, String[] NameSSPB) {
        Long[] points = new Long[users.length];
        Arrays.fill(points, 0);
        Texte t = new Texte(event.getMessageAuthor().asUser().orElse(users[0]));
        for(int i = 0; i < users.length; i++){
            for (int j = 0; j < users.length; j++){
                if (ints.get(i) != ints.get(j) && rps(ints.get(i), ints.get(j))) points[i] = points[i] + 1;
                if (ints.get(i) != ints.get(j) && rps(ints.get(j), ints.get(i))) points[i] = points[i] - 1;
            }
        }
        EmbedBuilder e = getNormalEmbed(event).setTitle(t.get("SSSTitle"));
        int best = 0;
        Map<String, Long> map = new HashMap<>(users.length);
        for (int i = 0; i < users.length; i++) map.put(Long.toString(i), points[i]);
        map = sortByValue(map, false);
        String[] s = map.keySet().toArray(new String[map.size()]);
        for (int j = 0; j < s.length; j++) {
            int i = Integer.parseInt(s[j]);
            if(j == 0) best = i;
            String s1 = users[i].getName();
            if(event.getServer().isPresent()) s1 = users[i].getDisplayName(event.getServer().get());
            e.addField(s1 + ":", t.get("SSSPlatzField", Long.toString(points[i]), NameSSPB[ints.get(i)]));
        }
        if(allSame(points, points[0])) {
            return e.setDescription(t.get("4GUnentschieden"));
        } else {
            return e.setDescription(users[best].getMentionTag() + " " + t.get("4GGewonnen"));
        }
    }
    static Boolean rps(int user, int geg) {
        if (user == 3 && geg != 2) return true; //               0                             1                   2                      3
        if (geg == 3 && user == 2) return true; //NameSSPB = {schere.getMentionTag(), stein.getMentionTag(), papier.getMentionTag(), brunnen.getMentionTag()};
        if (geg == 3 && user != 2) return false;
        if (user == 0 && geg != 1) return true;
        if (user == 1 && geg != 2) return true;
        if (user == 2 && geg != 0) return true;
        return false;
    }
    //rps
    static EmbedBuilder rps(MessageCreateEvent event2, AtomicReference<Integer> sspb, AtomicReference<Integer> sspbG, String[] NameSSPB, User geg) {
        User user = event2.getMessageAuthor().asUser().get();
        if(sspb.get() == -1 || sspbG.get() == -1) {
            Texte t = new Texte(user);
            EmbedBuilder e2 = getRotEmbed(event2).setTitle(t.get("ZeitAbgelaufen"));
            if(sspb.get() == -1 && sspbG.get() == -1) return e2.setDescription(new Texte(user, null).get("SSSLangsam2"));
            else if (sspb.get() == -1) return e2.setDescription(t.get("SSSLangsam", user.getDisplayName(event2.getServer().get())));
            else if (sspbG.get() == -1) return e2.setDescription(t.get("SSSLangsam", geg.getDisplayName(event2.getServer().get())));
        } else if (sspb.get().equals(sspbG.get())) {
            addGame0("sss", user, geg);
            return (getNormalEmbed(event2).setDescription(new Texte(user).getString("SSSUnentschieden", NameSSPB[sspb.get()]).toString()));
        } else {
            if (rps(sspb.get(), sspbG.get())) {
                return (win(NameSSPB[sspbG.get()], NameSSPB[sspb.get()], user, geg, event2));
            }else {
                return (lose(NameSSPB[sspbG.get()], NameSSPB[sspb.get()], user, geg, event2));
            }
        }
        return null;
    }
    static public EmbedBuilder lose (String g1, String o1, User user, User geg, MessageCreateEvent event) {
        String id = user.getIdAsString();
        String gegId = geg.getIdAsString();
        addGame("sss", geg, user);
        EmbedBuilder e = getNormalEmbed(event);
        if(geg.isYourself()) e = getRotEmbed(event);
        return e.setTitle(new Texte(user).getString("SSSTitle").toString())
                .setDescription(new Texte(user).getString("SSSEndMessage", gegId, g1, id, o1).toString());
    }

    static public EmbedBuilder win (String g1, String o1, User user, User geg, MessageCreateEvent event) {
        String id = user.getIdAsString();
        String gegId = geg.getIdAsString();
        EmbedBuilder e = getNormalEmbed(event);
        addGame("sss", user, geg);
        if(geg.isYourself()) e = getGruenEmbed(event);
        return e.setTitle(new Texte(user, null).getString("SSSTitle").toString())
                .setDescription(new Texte(user, null).getString("SSSEndMessage",id, o1, gegId, g1).toString());

    }


    public static String removeSpaceAtStart (String str1) {
        while(str1.length() > 0 && func.stringIsBlank(str1.substring(0, 1))) {
            str1 = str1.substring(1);
        }
        return str1;
    }
    //random string
    public static String randomstr(int laenge) {
        String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String str = "";
        for(int i=0; i< laenge; i++) {
            int i2 = getRandom(0,letter.length()-2);
            str += letter.substring(i2, i2 + 1);
        }
        return str;
    }
    //random string
    public static String randomstr2(int laenge) {
        String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#_?.-";
        String str = "";
        for(int i=0; i< laenge; i++) {
            int i2 = getRandom(0,letter.length()-2);
            str += letter.substring(i2, i2 + 1);
        }
        return str;
    }

    public static String randomCase(String string) {
        char[] c = string.toLowerCase().toCharArray();;
        StringBuilder sb = new StringBuilder(string.length());
        for (int i = 0; i < c.length; i++) {
            String s = String.valueOf(c[i]);
            if(func.getRandom(0,1) == 0) s = s.toUpperCase();
            sb.append(s);
        }
        return sb.toString();
    }

    public static String filepathof(String filename) {
        filename = WHITE_SPACE.matcher(filename.replace("/", getFileSeparator()).replace("\\", getFileSeparator())).replaceAll("");
        if(getFileSeparator().equals("/")) return normalfilepathlinux + getFileSeparator() + filename;
        else return normalfilepathwin + getFileSeparator() + filename;
    }
    public static String replaceUmlaute(String input) {
        //replace all lower Umlauts
        String output = input.replace("ü", "ue")
                .replace("ö", "oe")
                .replace("ä", "ae")
                .replace("ß", "ss");

        //first replace all capital umlaute in a non-capitalized context (e.g. Übung)
        output = output.replaceAll("Ü(?=[a-zäöüß ])", "Ue")
                .replaceAll("Ö(?=[a-zäöüß ])", "Oe")
                .replaceAll("Ä(?=[a-zäöüß ])", "Ae");

        //now replace all the other capital umlaute
        output = output.replace("Ü", "UE")
                .replace("Ö", "OE")
                .replace("Ä", "AE")
                .replace("ẞ", "SS");

        //replace accents
        output = Normalizer.normalize(output, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return output;
    }

    public static String replaceNonNormalChars(String input) {
         String output = replaceUmlaute(input);

         //replace everything, except letters, numbers, leerzeichen, "_,.!?"
         output = output.replaceAll("[^a-zA-Z0-9 _.,!?\\-]", "");

         return output;
    }

    public static String readtextoffile(String filename){
        String content = "";
        try{
            content = FileUtils.readFileToString(new File(filepathof(filename)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            func.handle(e);
        }
        return content;
    }

    public static File  writetexttofile(String text, String filename) {
        File f = new File(filepathof(filename));
        try {
            FileUtils.writeStringToFile(f, text, "UTF-8");
        } catch (Exception e) {func.handle(e);}
        return f;
    }

    static public boolean stringIsBlank(String string) {
        return StringBlank(string);
    }

    //qr
    public static String readQrCodeFromURL(String url, MessageCreateEvent event) {
        url = "https://api.qrserver.com/v1/read-qr-code/?fileurl=" + WHITE_SPACE.matcher(url).replaceAll("");
        String str = null;
        try {
            str = func.readStringFromUrl(url)
                    .split(",\"data\":")[1]
                    .split(",\"error\":")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(str == null || str.equals("null")) {
            sendOwner(url, null);
            return new Texte(event).get("FehlerAufgetreten");
        }
        str = str.replace("\\\"", "\u200B")
                .replace("\"", "")
                .replace("\u200B", "\"");
        return str;
    }

    //check if pro#
    public static Boolean ispro(User user) {
        JSONObject prousers = new JSONObject();
        try {
            FileReader fr = new FileReader(filepathof("prouser.json"));
            JSONParser jsp = new JSONParser();
            Object o = jsp.parse(fr);
            prousers = (JSONObject) o;
        } catch (Exception e3) {
            func.handle(e3);
        }
        return prousers.containsKey(user.getIdAsString());
    }

    public static EmbedBuilder Fehler(String Befehl, User user) {
        //if(Text.toLowerCase().contains(Befehl.toLowerCase())) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(getRandom(250,255), getRandom(0,5),getRandom(0,5)))
                .setTimestampToNow()
                .setTitle(new Texte(user, null).getString("FehlerTitle").toString())
                .setDescription(new Texte(user, null).getString("FehlerDesc", Befehl).toString());
        return embed;

        //}
        //return null;
    }

    public static EmbedBuilder Fehler2(String Befehl, User user) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(getRandom(250,255), getRandom(0,5),getRandom(0,5)))
                .setTimestampToNow()
                .setTitle(new Texte(user, null).getString("Fehler2Title").toString())
                .setDescription(new Texte(user, null).getString("Fehler2Desc", Befehl).toString());
        return embed;
    }


    static public void filedelete (String Filename) {
        if(Files.exists(Paths.get(Filename) )) {
            try {
                Files.delete(Paths.get(Filename));
            } catch (IOException e) {
                func.handle(e);
            }
        }
    }

    //for json reader read from website
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    //random int
    static public int getRandom(int _min, int _max) {
        return getRandom(_min, _max, null);
    }

    static public int getRandom(int _min, int _max, SecureRandom r) {
        if(r == null) r = new SecureRandom();
        int bound = _max - _min + 1;
        if (bound <= 0) return _min;

        return r.nextInt(bound) + _min;
    }

    //temp file
    static public File tempFile (String FileType) {
        File f = new File(filepathof("tmp/temp_" + func.randomstr(4) + System.currentTimeMillis() + "." + FileType));
        if(f.exists()) return tempFile(FileType);
        f.deleteOnExit();
        return f;
    }

    //Text
    static public String readStringFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return readAll(rd);
        }
    }


    //JSON
    static public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONParser jsp = new JSONParser();
            JSONObject json = (JSONObject) jsp.parse(jsonText);
            return json;
        } catch (ParseException e) {
            func.handle(e);
        } finally {
            is.close();
        }
        return null;
    }

    static public JSONObject JsonFromFile(String filename) {
        File f = new File(filepathof(filename));
        if(f.exists()) {
            try {
                JSONParser jsp = new JSONParser();
                return (JSONObject) jsp.parse(FileUtils.readFileToString(f, "UTF-8"));
            } catch (IOException | ParseException e) {
                func.handle(e);
            }
        }
        JsonToFile(new JSONObject(), filename);
        return new JSONObject();
    }

    static public void handle(Exception e) {
        try {
            if (getFileSeparator().equals("/")) {
                String text = e.getLocalizedMessage();
                //if(e.getCause() != null) {
                //    text += "\n\n" + e.getCause().getLocalizedMessage();
                //}
                e.printStackTrace();
                sendOwner(text, null);
            } else e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    static public void handle(Throwable e) {
        if(getFileSeparator().equals("/")) sendOwner(e.getLocalizedMessage(), null);
        else e.printStackTrace();
    }

    static public String highlightMessageUrl(String str) {
        Matcher m = DiscordRegexPattern.MESSAGE_LINK.matcher(str);
        String r = str;
        while(m.find()) {
            String url = m.group();
            System.out.println(url);
            Message mes = api.getMessageByLink(url).map(messageCompletableFuture -> messageCompletableFuture.join()).orElse(null);
            if (mes != null) {
                String con = mes.getContent();
                if (stringIsBlank(con) || con.length() < 1) {
                    List<Embed> embeds = mes.getEmbeds();
                    if (!embeds.isEmpty()) con = embeds.get(0).getDescription().orElse("");
                }
                if (!stringIsBlank(con) && con.length() > 1) {
                    int leng =  (url.length()/2) - 4;
                    if (con.length() > leng) {
                        if(leng > 4) leng -= 3;
                        if(leng > 1) con = con.substring(0, leng) + "...";
                    }
                    r = r.replace(url, "[" + con + "](" + url + ")");
                }
            }
            str = str.replace(url, "");
            m.reset(str);
        }
        return r;
    }

    static public File JsonToFile(JSONObject json, String filename) {
        File f = new File(filepathof(filename));
        try {
            FileUtils.writeStringToFile(f, json.toString(), "UTF-8");
        } catch (Exception e) {handle(e);}
        return f;
    }

    static public String eval(String str) { //calculate
        str = str.toLowerCase().replace(" ", "").replace("\n", "").replace("`", "").replace("**", "^").replace("×", "*").replace("÷", "/").replace("e", "(e)").replace("π", "(π)").replace("pi", "(π)").replace("rand()", "rand(0)")
            .replace(")(", ")*(").replace("0(", "0*(").replace("1(", "1*(").replace("2(", "2*(").replace("3(", "3*(").replace("4(", "4*(").replace("5(", "5*(").replace("6(", "6*(").replace("7(", "7*(").replace("8(", "8*(").replace("9(", "9*(");

        final String finalStr = str;
        return new Object() {
            int pos = -1, ch, precision = 100;
            boolean b1 = false;

            void nextChar() {
                ch = (++pos < finalStr.length()) ? finalStr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            String parse() {
                nextChar();
                Apfloat  x = parseExpression();
                if (pos < finalStr.length()) throw new RuntimeException("Unexpected: \"" + (char)ch + "\" in \"" + finalStr + "\" at Index: " + pos);
                if(b1) {
                    return x.toString(true);
                } else {
                    pos = -1;
                    String str = x.toString(true);
                    precision = str.length() + 2;
                    if(str.contains(".")) {
                        precision = str.lastIndexOf('.') + 16;
                    }
                    if(precision > 2040) precision = 2040;
                    b1 = true;
                    return parse();
                }
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            Apfloat  parseExpression() {
                Apfloat  x = parseTerm();
                for (;;) {
                    if      (eat('+')) x = x.add(parseTerm()); // addition
                    else if (eat('-')) x = x.subtract(parseTerm()); // subtraction
                    else return x;
                }
            }

            Apfloat  parseTerm() {
                Apfloat  x = parseFactor();
                for (;;) {
                    if      (eat('*')) x = x.multiply(parseFactor()); // multiplication
                    else if (eat('/')) x = x.divide(parseFactor()); // division
                    else return x;
                }
            }

            Apfloat parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return parseFactor().multiply(new Apfloat("-1", precision)); // unary minus

                Apfloat  x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = new Apfloat(finalStr.substring(startPos, this.pos), precision);
                } else if(eat('π')) { //pi
                    x = ApfloatMath.pi(precision);
                } else if(eat('e')) { //e:
                    x = new Apfloat("2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274274663919320030599218174135966290435729003342952605956307381323286279434907632338298807531952510190115738341879307021540891499348841675092447614606680822648001684774118537423454424371075390777449920695517027618386062613313845830007520449338265602976067371132007093287091274437470472306969772093101416928368190255151086574637721112523897844250569536967707854499699679468644549059879316368892300987931277361782154249992295763514822082698951936680331825288693984964651058209392398294887933203625094431173012381970684161403970198376793206832823764648042953118023287825098194558153017567173613320698112509961818815930416903515988885193458072738667385894228792284998920868058257492796104841984443634632449684875602336248270419786232090021609902353043699418491463140934317381436405462531520961836908887070167683964243781405927145635490613031072085103837505101157477041718986106873969655212671546889570350354021234078498193343210681701210056278802351930332247450158539047304199577770935036604169973297250886876966403555707162268447162560798826517871341951246652010305921236677194325278675398558944896970964097545918569563802363701621120477427228364896134225164450781824423529486363721417402388934412479635743702637552944483379980161254922785092577825620926226483262779333865664816277251640191059004916449982893150566047258027786318641551956532442586982946959308019152987211725563475463964479101459040905862984967912874068705048958586717479854667757573205681288459205413340539220001137863009455606881667400169842055804033637953764520304024322566135278369511778838638744396625322498506549958862342818997077332761717839280349465014345588970719425863987727547109629537415211151368350627526023264847287039207643100595841166120545297030236472549296669381151373227536450988890313602057248176585118063036442812314965507047510254465011727211555194866850800368532281831521960037356252794495158284188294787610852639813955990067376482922443752871846245780361929819713991475644882626", precision);
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = finalStr.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x = ApfloatMath.sqrt(x);
                            break;
                        case "sin":
                            x = ApfloatMath.sin(ApfloatMath.toRadians(x));
                            break;
                        case "cos":
                            x = ApfloatMath.cos(ApfloatMath.toRadians(x));
                            break;
                        case "tan":
                            x = ApfloatMath.tan(ApfloatMath.toRadians(x));
                            break;
                        case "arcsin":
                            x = ApfloatMath.toDegrees(ApfloatMath.asin(x));
                            break;
                        case "arccos":
                            x = ApfloatMath.toDegrees(ApfloatMath.acos(x));
                            break;
                        case "arctan":
                            x = ApfloatMath.toDegrees(ApfloatMath.atan(x));
                            break;
                        case "sinh":
                            x = ApfloatMath.sinh(x);
                            break;
                        case "cosh":
                            x = ApfloatMath.cosh(x);
                            break;
                        case "tanh":
                            x = ApfloatMath.tanh(x);
                            break;
                        case "log":
                            x = ApfloatMath.log(x);
                            break;
                        case "cbrt":
                            x = ApfloatMath.cbrt(x);
                            break;
                        case "rand":
                            x = ApfloatMath.random(precision);
                            break;
                        case "w":
                            x = ApfloatMath.w(x);
                            break;
                        case "rad" :
                            x = ApfloatMath.toRadians(x);
                            break;
                        case "int" :
                            String str = x.toString(true);
                            if(!str.contains(".")) break;
                            x = new Apfloat(str.replaceAll("\\.\\d+", ""), precision);
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: \"" + (char)ch + "\" in \"`" + finalStr.replace("ß", "e").replace("°", "π") + "`\" at Index: " + pos);
                }

                if (eat('^')) x = ApfloatMath.pow(x, parseFactor()); // exponentiation
                return x;
            }
        }.parse();
    }


    //isUrl
    public static boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static void addGame(String name, User Winner, User Loser) {
        addGame(name, Winner.getIdAsString(), Loser.getIdAsString());
    }
    public static void addGame(String name, long Winner, long Loser) {
        addGame(name, Long.toString(Winner), Long.toString(Loser));
    }

    public static void addGame(String name, String winnerID, String loserID) {
        try {
            String jid = api.getYourself().getIdAsString();
            if (!jid.equals(winnerID) && !jid.equals(loserID) && !winnerID.equals(loserID)) {
                JSONObject gamestats = JsonFromFile("games" + func.getFileSeparator() + name.toLowerCase() + ".json");
                createStats(gamestats, winnerID);
                createStats(gamestats, loserID);
                gamestats = addStats(gamestats, winnerID, "wins");
                gamestats = addStats(gamestats, loserID, "loses");
                JsonToFile(gamestats, "games" +func.getFileSeparator() + name.toLowerCase() + ".json");
            }
        } catch (Exception e) {
            func.handle(e);
        }
    }

    public static void addGame0(String name, User user1, User user2) {
        addGame0(name, user1.getIdAsString(), user2.getIdAsString());
    }
    public static void addGame0(String name, long user1, long user2) {
        addGame0(name,  Long.toString(user1),  Long.toString(user2));
    }

    public static void addGame0(String name, String id1, String id2) {
        try {
            String jid = api.getYourself().getIdAsString();
            if (jid != id1 && jid != id2 && id1 != id2) {
                JSONObject gamestats = JsonFromFile("games" +func.getFileSeparator() + name.toLowerCase() + ".json");
                gamestats = createStats(gamestats, id1);
                gamestats = createStats(gamestats, id2);
                gamestats = addStats(gamestats, id1, null);
                gamestats = addStats(gamestats, id2, null);
                JsonToFile(gamestats, "games\\" + name.toLowerCase() + ".json");
            }
        } catch (Exception e) {
            func.handle(e);
        }
    }

    static public EmbedBuilder getStats(User user, MessageCreateEvent event) {
        Texte t = new Texte(user);
        String id = user.getIdAsString();
        EmbedBuilder e = getNormalEmbed(event).setTitle(t.get("StatsTitle"));
        Map<String, Map<String, Long>> ggg = createStats(JsonFromFile("games" + func.getFileSeparator() + "4g.json"), id);
        Map<String, Map<String, Long>> sss = createStats(JsonFromFile("games" + func.getFileSeparator() +"sss.json"), id);
        Map<String, Map<String, Long>> ttt = createStats(JsonFromFile("games"+ func.getFileSeparator() + "ttt.json"), id);
        AtomicReferenceArray<Map<String, Long>> m = new AtomicReferenceArray<Map<String, Long>>(new Map[]{ggg.get(id), sss.get(id), ttt.get(id)});
        String[] s = {"4G", "SSS", "TTT"};
        for (int i = 0; i < m.length(); i++) {
            long g = m.get(i).get("games");
            long w = m.get(i).get("wins");
            long l = m.get(i).get("loses");
            long d = g - w - l;
            e.addField(t.get(s[i]+"Title"), t.get("StatsField", Long.toString(w), Long.toString(l), Long.toString(d), Long.toString(g)));
        }
        return e;
    }

    private static JSONObject addStats(JSONObject stats, String id, String key) {
        Map<String, Map<String, Long>> m = stats;
        Map<String, Long> m2 = m.get(id);
        if(key != null) m2.put(key, m2.get(key) + 1);
        m2.put("games", m2.get("games") + 1);
        m.put(id, m2);
        return (JSONObject) m;
    }

    private static JSONObject createStats(JSONObject stats, String id) {
        if(stats.containsKey(id)) return stats;
        Map<String, Long> m = new HashMap<>(3);
        m.put("wins", 0L);
        m.put("loses", 0L);
        m.put("games", 0L);
        stats.put(id, m);
        return stats;
    }

    static int countOfStringInString(String text_to_search_through, String text_to_find) {
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = text_to_search_through.indexOf(text_to_find, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += text_to_find.length();
            }
        }
        return count;
    }

    //integer //arr
    static public int[] differentRandomInts(int min, int max, int count) {
        int[] i1;
        do {
            i1 = randomInts(min, max, count);
        } while (!(differenceOf(min, max) > count) && !allDifferent(intArrToIntegerArr(i1)));
        return i1;
    }

    static public int[] randomInts(int min, int max, int count) {
        SecureRandom  r = new SecureRandom();
        int[] i1= new int[count];
        for (int i = 0; i < count; i++) {
            i1[i] = getRandom(min, max, r);
        }
        return i1;
    }

    static public Integer[] intArrToIntegerArr(int[] oldArray) {
        Integer[] newArray = new Integer[oldArray.length];
        for (int i = 0; i < oldArray.length; i++)
            newArray[i] = oldArray[i];
        return newArray;
    }

    static public  <k> boolean allDifferent(k[] ArrToTest){
        for(int i = 0; i < ArrToTest.length - 1; i++){
            for (int j = i + 1; j < ArrToTest.length; j++){
                if (ArrToTest[i].equals(ArrToTest[j]))
                    return false;
            }
        }
        return true;
    }
    static public <k> boolean allSame(k[] ArrToTest, k o) {
        int j = 0;
        for(int i = 0; i < ArrToTest.length; i++) {
            if(ArrToTest[i] == o) j++;
        }
        return j >= ArrToTest.length;
    }
    static public boolean allSame(AtomicIntegerArray ArrToTest, int o){
        int j = 0;
        for(int i = 0; i < ArrToTest.length(); i++){
            if(ArrToTest.get(i) == o) j++;
        }
        return j >= ArrToTest.length();
    }
    static public boolean allNotSame(AtomicIntegerArray ArrToTest, int o){
        int j = 0;
        for(int i = 0; i < ArrToTest.length(); i++){
            if(ArrToTest.get(i) == o) j++;
        }
        return j == 0;
    }

    public static long getMinPoints(long level) {
        return (long) (0.5*level*(level+5));
    }
    //rank //top //long
    public static long getLevel(long points) {
        //long i = 3;
        //long lvl = 0;
        //while (points - i >= 0) {
        //    lvl++;
        //    points -= i;
        //    i += 1;
        //}
        long l = 0;
        while(getMinPoints(l) < points) {
            l++;
        }
        if(getMinPoints(l) != points) l--;
        return l;
    }
    public static long getXp(long points) {
        return points  * 88 / 15;
    }


    public static String getLvlPercent(long points) {
        long i = 3;
        while (points - i >= 0) {
            points -= i;
            i += 1;
        }
        return getXp(points) + "/" + getXp((long) i);
    }
    public static Map<String, Long> sortByValue(Map<String, Long> unsortMap, final boolean order) {
        List<Map.Entry<String, Long>> list = new LinkedList<>(unsortMap.entrySet());

        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    //Goq //replace stuff
    static public String goq_replace(String text) {
        return 	func.NO_WORD.matcher(
                    WHITE_SPACE.matcher(
                        replaceNonNormalChars(text.toLowerCase()))
                            .replaceAll(""))
                    .replaceAll("")
                .replace('c', 'k');
    }

    static public boolean userIsTrusted(DiscordEntity user) {
        return user.getId() == api.getOwnerId() || user.getId() == 396294727814610944L || user.getId() == 564843886434975745L;
    }

    //zitat // goq
    static public String LinkedEmbed (String term) {
        String term2 = term.replaceAll(" ", "+");
        if(term2.length() > 90) {
            term2 = term2.substring(0, 89) + "\"";
        }
        return "[term1](https://ddg.gg/?q=term2)".replace("term1", term).replace("term2", term2);
    }

    static public DiscordApi getApi() {
        return api;
    }
    //fp//fc
    static public int IntFromString(String text, int standard) {
        String r = COMPILE.matcher(text).replaceAll(""); //replace all not numeric values

        if(r.isEmpty()) return standard; //wenn kein Text

        String maxv = Integer.toString(Integer.MAX_VALUE);
        if(r.length() > maxv.length()) return Integer.MAX_VALUE; //wenn länger als Max_Value

        if(r.length() == maxv.length()) {
            char[] txt = r.toCharArray();
            char[] max = maxv.toCharArray();
            for (int i = 0; i < txt.length; i++) {
                if(txt[i] > max [i]) return Integer.MAX_VALUE;
            }
        }
        try {
            return Integer.parseInt(r);
        } catch(NumberFormatException e) {handle(e);}
        return standard;
    }
    static public long LongFromString(String text, long standard) {
        String r = COMPILE.matcher(text).replaceAll("");
        if(r.isEmpty()) return standard;

        String max = Long.toString(Long.MAX_VALUE);

        if(r.length() > max.length()) return Long.MAX_VALUE; //wenn länger als Max_Value

        if(r.length() == max.length()) {
            char[] txt = r.toCharArray();
            char[] chars_max = max.toCharArray();
            for (int i = 0; i < txt.length; i++) {
                if(txt[i] > chars_max [i]) return Long.MAX_VALUE;
            }
        }

        try {
            return Long.parseLong(r);
        } catch(Exception e) {handle(e);}
        return standard;
    }
    static public double DoubleFromString(String text, double standard) {
        String r = text.replace(",", ".").replaceAll("[^0123456789.]", "");
        if(r.isEmpty()) return standard;

        String max = Double.toString(Double.MAX_VALUE);

        if(r.length() > max.length()) return Double.MAX_VALUE; //wenn länger als Max_Value

        if(r.length() == max.length()) {
            char[] txt = r.toCharArray();
            char[] chars_max = max.toCharArray();
            for (int i = 0; i < txt.length; i++) {
                if(txt[i] > chars_max [i]) return Double.MAX_VALUE;
            }
        }

        try {
             return Double.parseDouble(r);
        } catch(Exception e) {handle(e);}
        return standard;
    }

    public static long getRandomLong(long min, long max) {
        try {
            return min + (long) (Math.random() * (max - min));
        } catch (Exception e) {
            handle(e);
            return min;
        }
    }

    public static String longToBinaryBlankString(long l) {
        return Long.toBinaryString(l).replace('0', '\u200B').replace('1', '\u200D');
    }

    public static long longOfBinaryBlankString(String str) {
        return  Long.valueOf(str.replace('\u200B', '0').replace('\u200D', '1'), 2);
    }

    //robot
    static public BufferedImage robo(String set, String Subtext) throws IOException {
        String s;
        String s2;
        int i = IntFromString(Subtext, 666);
        if( i > 3666) s = "3666";
        else s = Integer.toString(i);
        s2 = "?size=" + s + "x" + s;

        String set2 = set;
        if(set2.replaceAll("\\d", "").isEmpty()) set2 = "set" + set;
        if(Subtext.toLowerCase().contains("bg")) set2 += "/bgset_any";

        return ImageIO.read(new URL("https://robohash.org/set_" + set2 +  "/"+ randomstr2(getRandom(2, 15)) + s2));

    }

    static public final Color randomColor() {
        return new Color(getRandom(0, 200), getRandom(5, 255), getRandom(5, 255));
    }

    static public final String getPing(MessageCreateEvent event) {
        return Long.toString(System.currentTimeMillis() - event.getMessage().getCreationTimestamp().toEpochMilli()).replace("-", "");
    }

    public static void deleteDir(File directory) {
        if(!directory.exists()) return;
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        directory.delete();
    }

    public static File fileOfName(String filename) {
        return new File(filepathof(filename));
    }

    public static String randomString(String[] list_of_strings) {
        return list_of_strings[getRandom(0, list_of_strings.length -1)];
    }

    //pw
    public static String createPassword(String[] words, int count_of_words) {
        char[] characters = {'*', '.', '#', '!', '?', '+'};
        String[] strings = new String[count_of_words];
        int rand = getRandom(0, count_of_words - 1);
        strings[0] = randomString(words);
        for (int i = 1; i < count_of_words; i++) {
            String str = randomString(words);
            if(i == rand) {
                Random generator = new Random();
                int num1 = 0;
                StringBuilder phrase = new StringBuilder(str);
                int len = phrase.length();
                char c = ' ';
                while (c == ' ') {
                    num1 = generator.nextInt(len);
                    c = phrase.charAt(num1);
                }
                phrase.setCharAt(num1, characters[getRandom(0, characters.length)]);
                str = phrase.toString();
            }
            strings[i] = "_" + str;
        }
        String pw = "";
        for (int i = 0; i < strings.length && (pw + strings[i]).length() <= 2048; i++) { //Embed-Description limit
            pw += strings[i];
        }
        return WHITE_SPACE.matcher(pw).replaceAll("");
    }

    //resize array
    public static <k> k[] resizeArray(k[] array, int length) {
        if(length < 1 || length >= array.length) return array;
        return Arrays.copyOf(array, length);
    }


    //crypt //encrypt
    public static String encrypt(String strToEncrypt, String key, String salt) throws Exception {
         byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
         IvParameterSpec ivspec = new IvParameterSpec(iv);

         SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
         KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
         SecretKey tmp = factory.generateSecret(spec);
         SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

         Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
         cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

         return Base64.getEncoder().encodeToString(cipher.doFinal(replaceUmlaute(strToEncrypt).getBytes()));
    }

    public static String replaceBlank(String string) {
        return WHITE_SPACE.matcher(string).replaceAll("")
                .replace("\u200B", "").replace("\u200D", "")
                .replace("\u200C", "").replace("\u200E", "")
                .replace("\u200F", "").replace("\u2800", "")
                .replace("⠀", "").replace("️", "");
    }

    public static boolean StringBlank(String string) {
        return string == null || string.length() == 0 || replaceBlank(string).length() == 0;
    }

    public static String decrypt(String strToDecrypt, String key, String salt) throws Exception {
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));

    }

    //save private
    public static String createCryptKey(String s) {
        String s1 = Integer.toString(s.hashCode());
        for (int i = 0; s1.length() < 100; i++) s1 += hashString(s + s1, true, 25);
        return s1.substring(0, 99);
    }
    //save secure
    public static String createCryptKey2(String s) {
        String s1 = replaceUmlaute(WHITE_SPACE.matcher(s).replaceAll("")).replaceAll("[^A-Za-z0-9+/]", "");
        String s2 = Integer.toString(s.hashCode());
        Random r = new Random(s.hashCode());
        while (s1.length() < 100) {
            if(r.nextInt(2) == 0) s1 += s1;
            else s1 += s + s1.hashCode();
        }
        while (s2.length() < 100) {
            if(r.nextInt(2) == 0) s2 += s2;
            else s2 += s2.hashCode();
        }
        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();
        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < 100; i++) {
            if (r.nextInt(2) == 0) sb.append(c1[i]);
            else sb.append(c2[i]);
        }
        return sb.toString();
    }

    //hash
    public static String hashString(String input, boolean OnlyLettersAndNumbers, int leng) {
        char[] letter;
        if(OnlyLettersAndNumbers) letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        else letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"+-:.;,§$%&{([])^°}".toCharArray();

        StringBuilder str = new StringBuilder(leng);
        Random r0 = new Random(input.hashCode());
        Random r1 = new Random((input + input.hashCode()).hashCode());
        int i2;
        for(int i = 0; i< leng; i++) {
            if(r0.nextInt(2) == 1) {
                i2 = r1.nextInt(letter.length);
            }  else {
                i2 = r0.nextInt(letter.length);
            }
            str.append(letter[i2]);
        }
        return str.toString();
    }

    public static String stringToNumber(String str) {
        //char: 0 -
        String zeros = "0000000000";
        char[] s = str.toCharArray();
        StringBuilder sb = new StringBuilder(s.length * 5);
        for (int i = 0; i < s.length; i++) {
            String tmp = Integer.toString(s[i]);
            sb.append(zeros.substring(0, 5  - tmp.length())).append(tmp);
        }
        return sb.toString();
    }

    public static String numberToString(String str) {
        //char: 0 - 65535
        StringBuilder sb = new StringBuilder(str.length() / 5);
        while (str.length() >= 5) {
            sb.append((char) IntFromString(str.substring(0, 5), ' '));
            str = str.substring(5);
        }
        return sb.toString();
    }

    static public BufferedImage imgEncrypt(String str, BufferedImage bi1) throws IOException {
        try {
            str = stringToNumber(encrypt(str, KEY, SALT));
        } catch (Exception e) {
            throw new IOException("Encryption");
        }

        if(bi1 == null) bi1 = randomImage((int) Math.sqrt(str.length()) + 1, (int) Math.sqrt(str.length()) + 1);
        else {
            if(str.length() > bi1.getWidth() * bi1.getHeight()) { //- f
                bi1 = new ImageResizer(bi1, "lol.png").resizeToPixels(str.length()).getBufferedImage(); // / (f/(bi1.getWidth() * bi1.getHeight()))
            }
        }
        BufferedImage bi = new ImageResizer(bi1, "image.png")
                .resizeToMaxSize(700000)
                .getBufferedImage();

        ImageIO.write(bi, "PNG", tempFile("png"));

        boolean b1 = false;
        Graphics g = bi.getGraphics();

        for (int i = 0; i < bi.getWidth(); i++) {
            for (int j = 0; j < bi.getHeight(); j++) {
                Color c = new Color(bi.getRGB(i, j));
                int[] rgb = {c.getRed(), c.getGreen(), c.getBlue()};
                int index = (j * i) % 3;
                if (rgb[index] >= 250) rgb[index] = 240;
                if(str.length() == 0) {
                    if(b1) {
                        String str2 = Integer.toString(rgb[index]);
                        rgb[index] = Integer.parseInt(str2.substring(0, str2.length() - 1) + getRandom(0,9));
                    } else {
                        Arrays.fill(rgb, (rgb[0] + rgb[1] + rgb[2]) / 3);
                        b1 = true;
                    }
                } else {
                    String str2 = Integer.toString(rgb[index]);
                    rgb[index] = Integer.parseInt(str2.substring(0, str2.length() - 1) + str.substring(0, 1));
                    str = str.substring(1);
                    if (rgb[0] == rgb[1] && rgb[1] == rgb[2]) {
                        for (int k = 0; k < rgb.length; k++) {
                            if (k != index) {
                                rgb[k]++;
                                break;
                            }
                        }
                    }
                }

                g.setColor(new Color(rgb[0], rgb[1], rgb[2], c.getAlpha()));
                g.drawLine(i, j, i, j);
            }
        }
        g.dispose();

        return bi;
    }

    static public Optional<URL> getImageURL(Message m) {
        for (int i = 0; i < m.getAttachments().size(); i++)
            if(m.getAttachments().get(i).isImage())
                return Optional.ofNullable(m.getAttachments().get(i).getUrl());
        return Optional.empty();
    }
    static public Optional<URL> getImageURL2(Message m) {
        boolean b1 = false;
        for (int i = 0; i < m.getAttachments().size(); i++)
            if(m.getAttachments().get(i).isImage()) {
                if(b1) return Optional.ofNullable(m.getAttachments().get(i).getUrl());
                else b1 = true;
            }
        return Optional.empty();
    }

    static public String imgDecrypt(BufferedImage bi) {
        StringBuilder sb = new StringBuilder();
        boolean b1 = true;
        for (int i = 0; i < bi.getWidth() && b1; i++) {
            for (int j = 0; j < bi.getHeight() && b1; j++) {
                Color c = new Color(bi.getRGB(i, j));
                int[] rgb = {c.getRed(), c.getGreen(), c.getBlue()};
                String str2 = Integer.toString(rgb[(j * i) % 3]);
                if(rgb[0] == rgb[1] && rgb[1] == rgb[2]) {
                    b1 = false;
                    break;
                } else {
                    sb.append(str2.substring(str2.length() - 1));
                }
            }
        }
        try {
            return decrypt(numberToString(sb.toString()), KEY, SALT);
        } catch (Exception e) {
            return new Texte(null, null).get("FehlerAufgetreten");
        }
    }

    static public BufferedImage randomImage(int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                g.setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
                g.drawLine(i, j, i, j);
            }
        }
        return bi;
    }

    static public String uploadToImgur(BufferedImage image, String name) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", byteArray);
        byte[] byteImage = byteArray.toByteArray();
        String dataImage = Base64.getEncoder().encodeToString(byteImage);
        String data = URLEncoder.encode("image", StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(dataImage, StandardCharsets.UTF_8);

        URL url;
        url = new URL("https://api.imgur.com/3/image");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Client-ID " + "7d0779be94fbaf7");

        conn.connect();
        StringBuilder stb = new StringBuilder();
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        data += "&" + URLEncoder.encode("name", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
                //+ "&" + URLEncoder.encode("title", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("Uploaded by Jemand.", StandardCharsets.UTF_8)
                //+ "&" + URLEncoder.encode("description", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("Invite Jemand to your Discord: " +
                //                                                                                               createBotInvite() + " :)", StandardCharsets.UTF_8);
        wr.write(data);
        wr.flush();

        // Get the response
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            stb.append(line).append("\n");
        }
        wr.close();
        rd.close();

        //  "id": "orunSTu",
        Matcher m = Pattern.compile("\"link\":\"(?<url>https?:.{2,4}i\\.imgur\\.com.{2,4}\\w+\\.\\w{3,4})\"").matcher(WHITE_SPACE.matcher(stb.toString()).replaceAll(""));

        //{"data":{"id":"LNKbeeq","title":null,"description":null,"datetime":1580574464,"type":"image\/png","animated":false,"width":1,"height":1,"size":70,"views":0,"bandwidth":0,"vote":null,"favorite":false,"nsfw":null,"section":null,"account_url":null,"account_id":0,"is_ad":false,"in_most_viral":false,"has_sound":false,"tags":[],"ad_type":0,"ad_url":"","edited":"0","in_gallery":false,"deletehash":"w9L1Z8htzuUdQxR","name":"","link":"https:\/\/i.imgur.com\/LNKbeeq.png"},"success":true,"status":200}
        if(m.find()) {
            return  m.group("url").replace("\\", "");
        }
        else throw new IOException("IDK WHAT HAPPENED BUT HERE IS AN ERROR MESSAGE!");
    }
}