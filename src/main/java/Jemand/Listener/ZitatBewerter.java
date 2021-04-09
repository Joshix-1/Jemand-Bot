package Jemand.Listener;

import Jemand.Zitat;
import Jemand.func;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.core.entity.user.UserImpl;

import java.util.Objects;

public class ZitatBewerter {

    public static class Add implements ReactionAddListener {
        @Override
        public void onReactionAdd(ReactionAddEvent event) {
            if(event.getUser().isEmpty() || event.getUser().get().isBot()  || event.getReaction().isEmpty()) return;
            try {
                event.requestMessage().exceptionally(t -> null).thenAccept(message -> {
                    String zid = getZid(event, message);
                    if (!zid.isEmpty()) {
                        Reaction reaction = event.requestReaction().join().orElse(null);
                        if (reaction != null) {
                            try {
                                EmbedBuilder embed = message.getEmbeds().get(0).toBuilder().setImage("");

                                int rating = Zitat.getRating(zid);
                                if (reaction.getEmoji().asCustomEmoji().map(customEmoji -> customEmoji.getId() == Zitat.WITZIG_ID).orElse(false)) {
                                    Zitat.rateQuote(zid, 1, event.getUser().orElse(event.getApi().getYourself()));
                                }
                                if (reaction.getEmoji().equalsEmoji(Zitat.DOWNVOTE_EMOJI)) {
                                    Zitat.rateQuote(zid, -1, event.getUser().orElse(event.getApi().getYourself()));
                                }

                                if (reaction.getEmoji().equalsEmoji(Zitat.REPORT_EMOJI)) {
                                    func.OWNER.ifPresent(owner -> {
                                        owner.sendMessage(event.getUser().map(User::toString).orElse("null") + " mag Zitat https://asozial.org/zitate/#/" + zid + " nicht.").exceptionally(ExceptionLogger.get());
                                    });
                                }

                                if (rating != Zitat.getRating(zid)) {
                                    message.edit(embed.removeAllFields().addField("\u200B", Zitat.upvote + ": " + Zitat.getRating(zid)));
                                }

                            } catch (NullPointerException e) {
                                func.handle(e);
                            }
                        }
                    }
                });
            } catch(Exception e) {func.handle(e);};
        }
    }

    public static class Remove implements ReactionRemoveListener {

        @Override
        public void onReactionRemove(ReactionRemoveEvent event) {
            if(event.getUser().isEmpty() || event.getUser().get().isBot() || event.getReaction().isEmpty()) return;
            try {
                event.requestMessage().exceptionally(t -> null).thenAccept(message -> {
                    String zid = getZid(event, message);
                    if (!zid.isEmpty()) {
                        try {
                            Reaction reaction = event.requestReaction().join().orElse(null);
                            if (reaction != null) {
                                int rating = Zitat.getRating(zid);

                                if (reaction.getEmoji().asCustomEmoji().map(customEmoji -> customEmoji.getId() == Zitat.WITZIG_ID).orElse(false)) {
                                    Zitat.rateQuote(zid, -1, event.getUser().orElse(event.getApi().getYourself()));
                                }
                                if (reaction.getEmoji().equalsEmoji(Zitat.DOWNVOTE_EMOJI)) {
                                    Zitat.rateQuote(zid, 1, event.getUser().orElse(event.getApi().getYourself()));
                                }

                                if (rating != Zitat.getRating(zid)) {
                                    EmbedBuilder embed = message.getEmbeds().get(0).toBuilder().setImage("");
                                    //message.getEmbeds().get(0).getImage().ifPresent(image -> embed.setImage(image.downloadAsBufferedImage(message.getApi()).join()));
                                    message.edit(embed.removeAllFields().addField("\u200B", Zitat.upvote + ": " + Zitat.getRating(zid)));
                                }
                            }
                        } catch (NullPointerException e) {
                            func.handle(e);
                        }
                    }
                });
            } catch(Exception e) {func.handle(e);};
        }
    }

    private static String getZid(SingleReactionEvent e, Message message) {
        if(message == null || !message.getAuthor().isYourself()) return "";
        String zid = "";

        if(e.getUser().isPresent() && !e.getUser().get().isBot() && message.getEmbeds().size() > 0) {
            String title = message.getEmbeds().get(0).getTitle().orElse("");

            if(title.startsWith("Zitat-Id: ")) {
                title = title.replace("Zitat-Id: ", "");
                if(title.contains("-")) zid = title;
            }
        }

        return zid;
    }
}
