package Jemand.Listener;

import Jemand.Zitat;
import Jemand.func;
import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedImage;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URL;

public class ZitatBewerter {
    static private final String downvote = EmojiParser.parseToUnicode(":thumbsdown:");
    static private final String report = EmojiParser.parseToUnicode(":warning:");
    

    public static class Add implements ReactionAddListener {
        @Override
        public void onReactionAdd(ReactionAddEvent event) {
            if(event.getUser().isBot() || Zitat.upvote2 == null) return;
            try {
                Message message = event.getApi().getMessageById(event.getMessageId(), event.getChannel()).exceptionally(t -> null).join();
                String zid = getZid(event, message);
                if (!zid.isEmpty()) {
                    EmbedBuilder embed = message.getEmbeds().get(0).toBuilder();
                    if (event.getReaction().isPresent()) {
                        try {
                            if (event.getReaction().get().getEmoji().equalsEmoji(report)) {
                                func.sendOwner("Reportet: \n\nZitat-Id: " + zid + "Von: " + event.getUser().getDiscriminatedName() + "\n\n" + message.getEmbeds().get(0).getImage().map(EmbedImage::getUrl).map(URL::toString).orElse(message.getEmbeds().get(0).getDescription().orElse("")), null);
                                message.edit(embed.addField("\u200B", "Das Zitat wurde reportet."));
                            } else {

                                JSONObject be = func.JsonFromFile("bewertung_zitate.json");

                                if (!be.containsKey(zid)) be.put(zid, 0L);

                                if (event.getReaction().get().getEmoji().equalsEmoji(Zitat.upvote2)) {
                                    be.put(zid, (long) be.get(zid) + 1);
                                }
                                if (event.getReaction().get().getEmoji().equalsEmoji(downvote)) {
                                    be.put(zid, (long) be.get(zid) - 1);
                                }
                                func.JsonToFile(be, "bewertung_zitate.json");

                                message.edit(embed.removeAllFields().setImage("").addField("\u200B", Zitat.upvote + ": " + be.get(zid)));

                                updateBewertung(be.toString());
                            }
                        } catch (NullPointerException e) {
                            func.handle(e);
                        }
                    }
                }
            } catch(Exception e) {func.handle(e);};
        }
    }

    public static void updateBewertung() {
        updateBewertung(func.readtextoffile("bewertung_zitate.json"));
    }

    public static void updateBewertung(String bewertung) {
        try {
             bewertung = bewertung.replace(",", ",\n");
            if (!func.getGithub("zitate", "bewertung_zitate.json").equals(bewertung) && func.getRandom(0, 6) == 0)
                func.setGithub("zitate", "bewertung_zitate.json", bewertung);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Remove implements ReactionRemoveListener {

        @Override
        public void onReactionRemove(ReactionRemoveEvent event) {
            if(event.getUser().isBot() || Zitat.upvote2 == null) return;
            try {
                Message message = event.getApi().getMessageById(event.getMessageId(), event.getChannel()).exceptionally(t -> null).join();
                String zid = getZid(event, message);
                if (!zid.isEmpty()) {
                    try {
                        EmbedBuilder embed = message.getEmbeds().get(0).toBuilder();

                        JSONObject be = func.JsonFromFile("bewertung_zitate.json");

                        if (!be.containsKey(zid)) be.put(zid, 0L);

                        if (event.getReaction().map(Reaction::getEmoji).map(Zitat.upvote2::equalsEmoji).orElse(false)) {
                            be.put(zid, (long) be.get(zid) - 1);
                        }
                        if (event.getReaction().map(Reaction::getEmoji).map(emoji -> emoji.equalsEmoji(downvote)).orElse(false)) {
                            be.put(zid, (long) be.get(zid) + 1);
                        }
                        func.JsonToFile(be, "bewertung_zitate.json");

                        message.edit(embed.removeAllFields().setImage("").addField("\u200B", Zitat.upvote + ": " + be.get(zid)));

                        updateBewertung(be.toString());
                    } catch (NullPointerException e) {
                        func.handle(e);
                    }
                }
            } catch(Exception e) {func.handle(e);};
        }
    }

    private static String getZid(SingleReactionEvent e, Message message) {
        if(message == null) return "";
        String zid = "";

        if(!e.getUser().isBot() && message.getAuthor().isYourself() && message.getEmbeds().size() > 0) {
            String title = message.getEmbeds().get(0).getTitle().orElse("");

            if(title.startsWith("Zitat-Id: ")) {
                title = title.replace("Zitat-Id: ", "");
                if(title.contains("-")) zid = title;
            }
        }

        return zid;
    }
}
