package Jemand;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.simple.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Zitat {
    func f = new func();
    int zitat, name;
    String typ = "";

    public  Zitat(int Id_Zitat, int Id_Name, String Typ) {
        zitat = Id_Zitat;
        name = Id_Name;
        typ = Typ.toLowerCase();
    }

    public  Zitat(int Id_Zitat, int Id_Name) {
        zitat = Id_Zitat;
        name = Id_Name;
    }

    public Zitat() {
        JSONObject bewertungen = f.JsonFromFile("bewertung_zitate.json");

        typ = "";

        do {
            zitat = f.getRandom(0, f.readtextoffile("zitate.txt").split("\n").length - 1);
            name = f.getRandom(0, f.readtextoffile("namen.txt").split("\n").length - 1);

            if (!bewertungen.containsKey(getzid())) {
                bewertungen.put(getzid(), 0);
            }
        } while (Integer.parseInt(bewertungen.get(getzid()).toString()) <= -1 || f.readtextoffile("blacklist_namen.txt").contains("+" + name+ "+"));
    }
    public void setTyp(String Typ) {typ = Typ.toLowerCase();}
    public void setName(int Id_Name) {name = Id_Name;}
    public void setZitat(int Id_Zitat) {zitat = Id_Zitat;}
    public String getzid() {return zitat + "-" + name;}

    public void sendMessage(TextChannel channel, EmbedBuilder embed) throws IOException {
        embed.removeAllFields();

        final String[] zitate = f.readtextoffile("zitate.txt").split("\n");
        final String[] namen = f.readtextoffile("namen.txt").split("\n");


        final CustomEmoji upvote2 = channel.getApi().getCustomEmojiById(609050310627033099L).get();
        final String upvote = upvote2.getMentionTag();
        final String downvote = EmojiParser.parseToUnicode(":thumbsdown:");
        final String report = EmojiParser.parseToUnicode(":warning:");
        final String a = EmojiParser.parseToUnicode(":a:");
        JSONObject bewertungen = f.JsonFromFile("bewertung_zitate.json");

        if(!(zitat<zitate.length)) zitat = zitate.length-1;
        if(!(name < namen.length)) name = namen.length-1;

        if(!bewertungen.containsKey(getzid())) {
            bewertungen.put(getzid(), 0);
            f.JsonToFile(bewertungen, "bewertung_zitate.json");
        }

        final String zid2 = getzid();
        final int in2 = name;

        embed.setUrl("https://asozialesnetzwerk.github.io/zitate/?id=" + zid2);

        embed.addField("\u200B", upvote + ": " + bewertungen.get(zid2)).setTitle("Zitat-Id: "+ zid2).setDescription(f.LinkedEmbed(zitate[zitat]) + "\n\n- " + f.LinkedEmbed(namen[name]));

        if(typ.toLowerCase().contains("bild")) {
            try {
                String api_url = "https://api.ritekit.com/v1/images/quote?quote=<ZITAT>&author=<AUTHOR>&fontSize=55&quoteFont=Lora&quoteFontColor=%234f4f4f&authorFont=Lato%20Black&authorFontColor=%23f5f5f5&enableHighlight=1&highlightColor=%23f0ea66&bgType=gradient&backgroundColor=%23000000&gradientType=linear&gradientColor1=%231ee691&gradientColor2=%231ddad6&animation=circle";
                api_url += "&brandLogo=https://upload.wikimedia.org/wikipedia/commons/5/59/Empty.png";
                //api_url = "https://api.ritekit.com/v1/images/quote?quote=<ZITAT>&author=<AUTHOR>&fontSize=30&quoteFont=PassionOne&quoteFontColor=%23f7fffa&authorFont=Lato%20Black&authorFontColor=%23ffffff&enableHighlight=1&highlightColor=%23182578&bgType=gradient&backgroundColor=%23000000&gradientType=radial&gradientColor1=%230e0c1c&gradientColor2=%20%23040024&brandLogo=https%3A%2F%2Fimages.emojiterra.com%2Ftwitter%2Fv12%2F128px%2F1f998.png&animation=none";
                api_url += "&client_id=808e102adae0050e7970e26a4d902470f1c07c44b5df";
                String author = namen[name];
                if(func.StringBlank(author.substring(namen[name].length()-1))) author = namen[name].substring(0, namen[name].length()-1);
                embed.removeAllFields().setImage((String)
                        f.readJsonFromUrl(api_url.replace("<ZITAT>", func.replaceNonNormalChars(zitate[zitat]).replace("\"", ""))
                                .replace("<AUTHOR>", func.replaceNonNormalChars(author))
                                .replace(" ", "%20")
                                .replaceAll("  [\\t\\n\\x08\\x0c\\r]", ""))
                                .get("url")).addField("\u200B", upvote + ": " + bewertungen.get(zid2)).setTitle("Zitat-Id: "+ zid2).setDescription("");

            } catch (Exception e) {
                func.handle(e);
            }
        } else if(typ.toLowerCase().contains("ka")) {
            String str = Memes.KALENDER;
            if(func.getRandom(0, 1) == 1) str = Memes.KALENDER2;
            BufferedImage bi = new Memes(str, "»" + zitate[zitat].substring(1, zitate[zitat].length()-1) + "«", namen[name], new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime())).getFinalMeme().orElse(null);
            if(bi != null) {
                embed.removeAllFields().setImage(bi).addField("\u200B", upvote + ": " + bewertungen.get(zid2)).setTitle("Zitat-Id: " + zid2).setDescription("");
            }

        }
        Message message = channel.sendMessage(embed).join();

        message.addReactions("witzig:609050310627033099", downvote, report);
    }

}

