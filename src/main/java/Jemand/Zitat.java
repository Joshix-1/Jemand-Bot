package Jemand;

import com.goebl.david.Webb;
import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Objects;

public class Zitat {
    static final String WRONGQUOTES_API = "https://zitate.prapsschnalinen.de/api/wrongquotes";
    static final String AUTHORS = "https://zitate.prapsschnalinen.de/api/authors";
    static final String QUOTES = "https://zitate.prapsschnalinen.de/api/quotes";
    static private final List<Author> authorList = new LinkedList<>();
    static private final List<Quote> quotesList = new LinkedList<>();
    static Map<String, Integer> rating = new HashMap<>();
    static private final Map<String, Integer> quoteIds = new HashMap<>();

    static public void main(String[] args) {
        quoteIds.forEach((str, i) -> {
            System.out.println(str + ": " + i);
        });
    }

    static {
       updateQuotes();
    }

    public static void updateQuotes() {
        Webb webb = Webb.create();

        JSONArray jsonArray = webb
                .get(WRONGQUOTES_API)
                .asJsonArray()
                .getBody();

        jsonArray.forEach(o -> {
            handleWrongQuote((JSONObject) o);
        });

        jsonArray = webb
                .get(AUTHORS)
                .asJsonArray()
                .getBody();

        jsonArray.forEach(o -> {
            addAuthor((JSONObject) o);
        });

        jsonArray = webb
                .get(QUOTES)
                .asJsonArray()
                .getBody();

        jsonArray.forEach(o -> {
            addQuote((JSONObject) o);
        });
    }

    static void handleWrongQuote(JSONObject data) {
        JSONObject quote = data.getJSONObject("quote");
        addQuote(quote);
        JSONObject author = data.getJSONObject("author");
        addAuthor(author);

        String id = quote.get("id") + "-" + author.get("id");
        rating.put(id, data.getInt("rating"));
        quoteIds.put(id, data.getInt("id"));
    }

    static void addQuote(JSONObject data) {
        if (data.has("id")) {
            int id = data.getInt("id");
            for (Quote q : quotesList) {
                if (q.id == id) return; //TODO: Binary Search?
            }
            quotesList.add(new Quote(data));
        }
    }

    static void addAuthor(JSONObject data) {
        if (data != null && data.has("id")) {
            int id = data.getInt("id");
            for (Author a : authorList) {
                if (a.id == id) return; //TODO: Binary Search?
            }
            authorList.add(new Author(data));
        }
    }

    static int getQuoteIdForGoq(String qStr) {
        for (Quote q : quotesList) {
            if (q.text.equals(qStr) || func.goq_replace(q.text).equals(func.goq_replace(qStr))) {
                return q.id;
            }
        }
        return -1;
    }

    static int createAuthor(String author, User user) {
        if (Character.isLowerCase(author.charAt(0))) {
            author = Character.toUpperCase(author.charAt(0)) + author.substring(1);
        }

        Webb webb = Webb.create();

        JSONObject result = webb
                .post(AUTHORS)
                .param("author", author)
                .param("contributed_by", user.getName() + " (discord_id: " + user.getId() + ")")
                .asJsonObject()
                .getBody();
        addAuthor(result);
        return result.getInt("id");
    }

    static int getAuthorIdForGoq(String aStr) {
        for (Author a : authorList) {
            if (a.name.equals(aStr) || func.goq_replace(a.name).equals(func.goq_replace(aStr))) {
                return a.id;
            }
        }
        return -1;
    }

    static Optional<Quote> getQuoteById(int id) {
        for (Quote q : quotesList) {
            if (q.id == id) {
                return Optional.of(q);
            }
        }
        return Optional.empty();
    }

    static String getQuotesTextById(int id) {
        return getQuoteById(id).map(q -> q.text).orElse("null");
    }

    static int getQuoteCount() {
        return quotesList.size();
    }

    static Optional<Author> getAuthorById(int id) {
        for (Author a : authorList) {
            if (a.id == id) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    static String getAuthorsNameById(int id) {
        return getAuthorById(id).map(a -> a.name).orElse("null");
    }

    static int getAuthorCount() {
        return authorList.size();
    }

    public static void rateQuote(String zid, int rate, User user) {
        rate /= Math.abs(rate);
        Webb webb = Webb.create();

        if (quoteIds.containsKey(zid)) {
            JSONObject result = webb
                    .post(WRONGQUOTES_API + "/" + quoteIds.get(zid))
                    .param("vote", rate)
                    .asJsonObject()
                    .getBody();
            handleWrongQuote(result);
        } else {
            String[] ids = zid.split("-");
            JSONObject result = webb
                    .post(WRONGQUOTES_API)
                    .param("contributed_by", user.getDiscriminatedName())
                    .param("quote", ids[0])
                    .param("author", ids[1])
                    .asJsonObject()
                    .getBody();

            handleWrongQuote(result);

            if (quoteIds.containsKey(zid)) rateQuote(zid, rate, user);
        }
    }

    public static int getRating(String id) {
        return rating.getOrDefault(id, 0);
    }

    static class Quote {
        public final int id;
        public final String text;
        public final Author origAuthor;

        private Quote(JSONObject data) {
            id = data.getInt("id");
            text = data.getString("quote");
            JSONObject author = data.getJSONObject("author");
            addAuthor(author);
            origAuthor = new Author(author);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Quote quote = (Quote) o;
            return id == quote.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Quote{" +
                    "id=" + id +
                    ", text='" + text + '\'' +
                    ", origAuthor=" + origAuthor +
                    '}';
        }
    }

    static class Author {
        public final int id;
        public final String name;

        private Author(JSONObject data) {
            id = data.getInt("id");
            name = data.getString("author");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Author a = (Author) o;
            return id == a.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Author{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    static public final long WITZIG_ID = 703173500139208734L;
    static public final CustomEmoji UPVOTE_EMOJI = func.getApi().getCustomEmojiById(Zitat.WITZIG_ID).orElse(null);
    static public final String upvote = UPVOTE_EMOJI == null ? "" : UPVOTE_EMOJI.getMentionTag();
    static public final String DOWNVOTE_EMOJI = EmojiParser.parseToUnicode(":thumbsdown:");
    static public final String REPORT_EMOJI = EmojiParser.parseToUnicode(":warning:");

    private Quote quote;
    private Author author;
    private String typ;

    public  Zitat(int zitatId, int nameId, String typ) {
        this.typ = typ;
        quote = getQuoteById(zitatId).orElseThrow(() -> new IllegalArgumentException("Quote id is wrong"));
        author = getAuthorById(nameId).orElseThrow(() -> new IllegalArgumentException("Author id is wrong"));
    }

    public Zitat() {
        typ = "";
        quote = quotesList.get(func.getRandom(0, quotesList.size() - 1));
        author = authorList.get(func.getRandom(0, authorList.size() - 1));
    }

    public void setQuote(int quoteId) {
        this.quote = getQuoteById(quoteId).orElseThrow(() -> new IllegalArgumentException("Quote id is wrong"));
    }

    public void setAuthor(int authorId) {
        this.author = getAuthorById(authorId).orElseThrow(() -> new IllegalArgumentException("Author id is wrong"));
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getZid() {
        return (quote == null ? "-1" : quote.id) + "-" + (quote == null ? "-1" : author.id);
    }

    public void sendMessage(TextChannel channel, EmbedBuilder embed) throws Exception {
        embed.removeAllFields();

        final String zid = getZid();

        embed.setUrl("https://asozialesnetzwerk.github.io/zitate/#/" + zid);

        embed.addField("\u200B", upvote + ": " + getRating(zid)).setTitle("Zitat-Id: "+ zid).setDescription(func.LinkedEmbed(quote.text) + "\n\n- " + func.LinkedEmbed(author.name));

        if(typ.toLowerCase().contains("bild")) {
            try {
                String api_url = "https://api.ritekit.com/v1/images/quote?quote=<ZITAT>&author=<AUTHOR>&fontSize=55&quoteFont=Lora&quoteFontColor=%234f4f4f&authorFont=Lato%20Black&authorFontColor=%23f5f5f5&enableHighlight=1&highlightColor=%23f0ea66&bgType=gradient&backgroundColor=%23000000&gradientType=linear&gradientColor1=%231ee691&gradientColor2=%231ddad6&animation=circle";
                api_url += "&brandLogo=https://upload.wikimedia.org/wikipedia/commons/5/59/Empty.png";
                //api_url = "https://api.ritekit.com/v1/images/quote?quote=<ZITAT>&author=<AUTHOR>&fontSize=30&quoteFont=PassionOne&quoteFontColor=%23f7fffa&authorFont=Lato%20Black&authorFontColor=%23ffffff&enableHighlight=1&highlightColor=%23182578&bgType=gradient&backgroundColor=%23000000&gradientType=radial&gradientColor1=%230e0c1c&gradientColor2=%20%23040024&brandLogo=https%3A%2F%2Fimages.emojiterra.com%2Ftwitter%2Fv12%2F128px%2F1f998.png&animation=none";
                api_url += "&client_id=" + func.pws[7];
                embed.removeAllFields()
                        .setImage(func.readJsonFromUrl(api_url.replace("<ZITAT>", func.replaceNonNormalChars(quote.text).replace("\"", ""))
                                .replace("<AUTHOR>", func.replaceNonNormalChars(author.name))
                                .replace(" ", "%20")
                                .replaceAll("  [\\t\\n\\x08\\x0c\\r]", ""))
                                .get("url").toString())
                        .addField("\u200B", upvote + ": " + getRating(zid)).setTitle("Zitat-Id: "+ zid).setDescription("");

            } catch (Exception e) {
                func.handle(e);
            }
        } else if(typ.toLowerCase().contains("ka")) {
            String str = Memes.KALENDER;
            if(func.getRandom(0, 1) == 1) str = Memes.KALENDER2;
            BufferedImage bi = new Memes(str, "»" + quote.text + "«", author.name, new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime())).getFinalMeme().orElse(null);
            if(bi != null) {
                embed.removeAllFields().setImage(bi).addField("\u200B", upvote + ": " + getRating(zid)).setTitle("Zitat-Id: " + zid).setDescription("");
            }
        } else if(typ.toLowerCase().contains("ata")) {
            new Memes(Memes.ZITAT_ATA, "»" + quote.text + "«", "- " + author.name).getFinalMeme().ifPresent(img -> {
                embed.removeAllFields().setImage(img).addField("\u200B", upvote + ": " + getRating(zid)).setTitle("Zitat-Id: " + zid).setDescription("");
            });
        }

        Message message = channel.sendMessage(embed).join();

        message.addReactions(UPVOTE_EMOJI.getReactionTag(), DOWNVOTE_EMOJI).join();
    }

}

