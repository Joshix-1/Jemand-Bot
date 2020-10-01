package Jemand.Listener;

import Jemand.Zitat;
import Jemand.func;
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

import java.net.URL;

public class ZitatBewerter {

    public static class Add implements ReactionAddListener {
        @Override
        public void onReactionAdd(ReactionAddEvent event) {
            if(event.getUser().isEmpty() || event.getUser().get().isBot() || Zitat.UPVOTE_EMOJI == null  || event.getReaction().isEmpty()) return;
            try {
                Message message = event.getMessage()
                        .orElse(event.getApi().getMessageById(event.getMessageId(), event.getChannel()).exceptionally(t -> null).join());
                String zid = getZid(event, message);
                if (!zid.isEmpty()) {
                    if (event.getReaction().isPresent()) {
                        try {
                            EmbedBuilder embed = message.getEmbeds().get(0).toBuilder().setImage("");

                            if (event.getReaction().get().getEmoji().equalsEmoji(Zitat.REPORT_EMOJI)) {
                                func.sendOwner("Reportet: \n\nZitat-Id: " + zid + "Von: " + event.getUser().get().getDiscriminatedName() + "\n\n" + message.getEmbeds().get(0).getImage().map(EmbedImage::getUrl).map(URL::toString).orElse(message.getEmbeds().get(0).getDescription().orElse("")), null);
                                message.edit(embed.addField("\u200B", Zitat.REPORT_EMOJI + "  Zitat wurde reportet."));
                            } else {
                                String rating = Zitat.getRating(zid);
                                if (event.getReaction().map(Reaction::getEmoji).map(Zitat.UPVOTE_EMOJI::equalsEmoji).orElse(false)) {
                                    Zitat.rateQuote(zid, 1);
                                }
                                if (event.getReaction().map(Reaction::getEmoji).map(emoji -> emoji.equalsEmoji(Zitat.DOWNVOTE_EMOJI)).orElse(false)) {
                                    Zitat.rateQuote(zid, -1);
                                }

                                if(!rating.equals(Zitat.getRating(zid))) {
                                    message.edit(embed.removeAllFields().addField("\u200B", Zitat.UPVOTE_EMOJI.getMentionTag() + ": " + Zitat.getRating(zid)));
                                }
                            }
                        } catch (NullPointerException e) {
                            func.handle(e);
                        }
                    }
                }
            } catch(Exception e) {func.handle(e);};
        }
    }

    public static class Remove implements ReactionRemoveListener {

        @Override
        public void onReactionRemove(ReactionRemoveEvent event) {
            if(event.getUser().isEmpty() || event.getUser().get().isBot() || Zitat.UPVOTE_EMOJI == null || event.getReaction().isEmpty()) return;
            try {
                Message message = event.getMessage()
                        .orElse(event.getApi().getMessageById(event.getMessageId(), event.getChannel()).exceptionally(t -> null).join());
                String zid = getZid(event, message);
                if (!zid.isEmpty()) {
                    try {
                        String rating = Zitat.getRating(zid);

                        if (event.getReaction().map(Reaction::getEmoji).map(Zitat.UPVOTE_EMOJI::equalsEmoji).orElse(false)) {
                            Zitat.rateQuote(zid, -1);
                        }
                        if (event.getReaction().map(Reaction::getEmoji).map(emoji -> emoji.equalsEmoji(Zitat.DOWNVOTE_EMOJI)).orElse(false)) {
                            Zitat.rateQuote(zid, 1);
                        }

                        if(!rating.equals(Zitat.getRating(zid))) {
                            EmbedBuilder embed = message.getEmbeds().get(0).toBuilder().setImage("");
                            //message.getEmbeds().get(0).getImage().ifPresent(image -> embed.setImage(image.downloadAsBufferedImage(message.getApi()).join()));
                            message.edit(embed.removeAllFields().addField("\u200B", Zitat.UPVOTE_EMOJI.getMentionTag() + ": " + Zitat.getRating(zid)));
                        }
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

        if(e.getUser().isPresent() && e.getUser().get().isBot() && message.getAuthor().isYourself() && message.getEmbeds().size() > 0) {
            String title = message.getEmbeds().get(0).getTitle().orElse("");

            if(title.startsWith("Zitat-Id: ")) {
                title = title.replace("Zitat-Id: ", "");
                if(title.contains("-")) zid = title;
            }
        }

        return zid;
    }
}
