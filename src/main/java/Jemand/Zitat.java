package Jemand;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.json.simple.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Zitat {
    static public final long WITZIG_ID = 703173500139208734L;
    static public final CustomEmoji UPVOTE_EMOJI = func.getApi().getCustomEmojiById(Zitat.WITZIG_ID).orElse(null);
    static public final String upvote = UPVOTE_EMOJI == null ? "" : UPVOTE_EMOJI.getMentionTag();
    static public final String DOWNVOTE_EMOJI = EmojiParser.parseToUnicode(":thumbsdown:");
    static public final String REPORT_EMOJI = EmojiParser.parseToUnicode(":warning:");

    static public String[] NAMEN;
    static public String[] ZITATE;
    static public JSONObject BEWERTUNGEN;


    static {
        try {
            BEWERTUNGEN = func.readJsonFromUrl("https://raw.githubusercontent.com/asozialesnetzwerk/zitate/master/bewertung_zitate.json");
        } catch (Exception e) {
            BEWERTUNGEN = func.JsonFromFile("bewertung_zitate.json");
        }
        updateQuotes();
        updateNames();
    }
    static {
        if(UPVOTE_EMOJI == null) {
            func.handle(new Exception("Witzig-Emoji not found"));
        }
    }
    private int zitat, name;
    private String typ = "";

    public  Zitat(int zitatId, int nameId, String Typ) {
        zitat = zitatId;
        name = nameId;
        typ = Typ.toLowerCase();
    }

    static void updateNames() {
        try {
            NAMEN = func.readStringFromUrl("https://raw.githubusercontent.com/asozialesnetzwerk/zitate/master/namen.txt").split("\n");
        } catch (IOException e) {
            NAMEN = func.readtextoffile("namen.txt").split("\n");
            func.handle(e);
        }
    }

    static void addName(String name, User user) {
        String names = getNameString();
        if(!names.endsWith("\n")) names += "\n";
        try {
            if(user == null) {
                func.setGithub("zitate", "namen.txt", names + name);
            } else {
                func.setGithub("zitate", "namen.txt", '"' + names + name, name + "\" zur Namensliste hinzugefügt. Danke an " + user.getDiscriminatedName() + " (" + user.getIdAsString() + ")");
            }
            NAMEN = (names + name).split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void addQuote(String quote) {
        try {
            String quotes = func.readStringFromUrl("https://raw.githubusercontent.com/asozialesnetzwerk/zitate/master/zitate.txt");
            if(!quotes.endsWith("\n")) quotes += "\n";
            func.setGithub("zitate", "zitate.txt", quotes + quote);
            updateQuotes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getNameString() {
        try {
            return func.readStringFromUrl("https://raw.githubusercontent.com/asozialesnetzwerk/zitate/master/namen.txt");
        } catch (IOException e) {
            func.handle(e);
            return  func.readtextoffile("namen.txt");
        }
    }

    static public void rateQuote(String id, int rating) {
        if (!BEWERTUNGEN.containsKey(id)) {
            BEWERTUNGEN.put(id, rating);
        } else if(rating != 0) {
            BEWERTUNGEN.put(id, getRatingAsLong(id) + rating);
        }
        if(rating != 0 && func.getRandom(0, 8) == 0) {
            saveRating();
        }
    }

    static public long getRatingAsLong(String id) {
        try {
            return Long.parseLong(getRating(id));
        } catch(NumberFormatException e) {
            return 0L;
        }
    }

    static public String getRating(String id) {
        if(BEWERTUNGEN.containsKey(id)) {
            return BEWERTUNGEN.get(id).toString();
        } else {
            return "0";
        }
    }

    public static void saveRating() {
        saveRating(BEWERTUNGEN.toString());
    }

    private static void saveRating(String bewertung) {
        try {
            bewertung = bewertung.replace(",", ",\n");
            if (!func.getGithub("zitate", "bewertung_zitate.json").equals(bewertung))
                func.setGithub("zitate", "bewertung_zitate.json", bewertung);
        } catch (IOException e) {
            func.handle(e);
        }
        func.JsonToFile(BEWERTUNGEN, "bewertung_zitate.json");

    }

    static void updateQuotes() {
        try {
            ZITATE = func.readStringFromUrl("https://raw.githubusercontent.com/asozialesnetzwerk/zitate/master/zitate.txt").split("\n");
        } catch (IOException e) {
            ZITATE = func.readtextoffile("zitate.txt").split("\n");
            func.handle(e);
        }
    }

    public Zitat(int zitatId, int nameId) {
        zitat = zitatId;
        name = nameId;
    }

    public Zitat() {
        typ = "";

        do {
            zitat = func.getRandom(0, ZITATE.length - 1);
            name = func.getRandom(0, NAMEN.length - 1);

            rateQuote(getZid(), 0);
        } while (getRatingAsLong(getZid()) <= -1 || func.readtextoffile("blacklist_namen.txt").contains("+" + name+ "+"));
    }
    public void setTyp(String Typ) {typ = Typ.toLowerCase();}
    public void setName(int Id_Name) {name = Id_Name;}
    public void setZitat(int Id_Zitat) {zitat = Id_Zitat;}
    public String getZid() {return zitat + "-" + name;}

    public void sendMessage(TextChannel channel, EmbedBuilder embed) throws Exception {
        if(upvote == null) return;

        embed.removeAllFields();


        if(!(zitat< ZITATE.length)) {
            Zitat.updateQuotes();
            if(!(zitat< ZITATE.length)) zitat = ZITATE.length-1;
        }
        if(!(name < NAMEN.length)) {
            Zitat.updateNames();
            if(!(name < NAMEN.length)) name = NAMEN.length-1;
        }

        rateQuote(getZid(), 0);

        final String zid2 = getZid();

        embed.setUrl("https://asozialesnetzwerk.github.io/zitate/#/" + zid2);

        embed.addField("\u200B", upvote + ": " + getRating(zid2)).setTitle("Zitat-Id: "+ zid2).setDescription(func.LinkedEmbed(ZITATE[zitat]) + "\n\n- " + func.LinkedEmbed(NAMEN[name]));

        if(typ.toLowerCase().contains("bild")) {
            try {
                String api_url = "https://api.ritekit.com/v1/images/quote?quote=<ZITAT>&author=<AUTHOR>&fontSize=55&quoteFont=Lora&quoteFontColor=%234f4f4f&authorFont=Lato%20Black&authorFontColor=%23f5f5f5&enableHighlight=1&highlightColor=%23f0ea66&bgType=gradient&backgroundColor=%23000000&gradientType=linear&gradientColor1=%231ee691&gradientColor2=%231ddad6&animation=circle";
                api_url += "&brandLogo=https://upload.wikimedia.org/wikipedia/commons/5/59/Empty.png";
                //api_url = "https://api.ritekit.com/v1/images/quote?quote=<ZITAT>&author=<AUTHOR>&fontSize=30&quoteFont=PassionOne&quoteFontColor=%23f7fffa&authorFont=Lato%20Black&authorFontColor=%23ffffff&enableHighlight=1&highlightColor=%23182578&bgType=gradient&backgroundColor=%23000000&gradientType=radial&gradientColor1=%230e0c1c&gradientColor2=%20%23040024&brandLogo=https%3A%2F%2Fimages.emojiterra.com%2Ftwitter%2Fv12%2F128px%2F1f998.png&animation=none";
                api_url += "&client_id=" + func.pws[7];
                String author = NAMEN[name];
                if(func.StringBlank(author.substring(NAMEN[name].length()-1))) author = NAMEN[name].substring(0, NAMEN[name].length()-1);
                embed.removeAllFields()
                        .setImage(func.readJsonFromUrl(api_url.replace("<ZITAT>", func.replaceNonNormalChars(ZITATE[zitat]).replace("\"", ""))
                                .replace("<AUTHOR>", func.replaceNonNormalChars(author))
                                .replace(" ", "%20")
                                .replaceAll("  [\\t\\n\\x08\\x0c\\r]", ""))
                                .get("url").toString())
                        .addField("\u200B", upvote + ": " +getRating(zid2)).setTitle("Zitat-Id: "+ zid2).setDescription("");

            } catch (Exception e) {
                func.handle(e);
            }
        } else if(typ.toLowerCase().contains("ka")) {
            String str = Memes.KALENDER;
            if(func.getRandom(0, 1) == 1) str = Memes.KALENDER2;
            BufferedImage bi = new Memes(str, "»" + ZITATE[zitat].substring(1, ZITATE[zitat].length()-1) + "«", NAMEN[name], new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime())).getFinalMeme().orElse(null);
            if(bi != null) {
                embed.removeAllFields().setImage(bi).addField("\u200B", upvote + ": " + getRating(zid2)).setTitle("Zitat-Id: " + zid2).setDescription("");
            }
        } else if(typ.toLowerCase().contains("ata")) {
            new Memes(Memes.ZITAT_ATA, "»" + ZITATE[zitat].substring(1, ZITATE[zitat].lastIndexOf('"') -1) + "«", "- " + NAMEN[name]).getFinalMeme().ifPresent(img -> {
                embed.removeAllFields().setImage(img).addField("\u200B", upvote + ": " + getRating(zid2)).setTitle("Zitat-Id: " + zid2).setDescription("");
            });
        }

        Message message = channel.sendMessage(embed).join();

        message.addReactions(UPVOTE_EMOJI.getReactionTag(), DOWNVOTE_EMOJI, REPORT_EMOJI).join();
    }

}

