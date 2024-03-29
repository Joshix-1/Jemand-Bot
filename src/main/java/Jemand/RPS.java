package Jemand;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.util.event.ListenerManager;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RPS {
    final private KnownCustomEmoji[] EMOTES = {
            func.getApi().getCustomEmojiById(703315798881599519L).orElse(null),
            func.getApi().getCustomEmojiById(703315818079060018L).orElse(null),
            func.getApi().getCustomEmojiById(703315818338975784L).orElse(null),
            func.getApi().getCustomEmojiById(703315817973940265L).orElse(null)
    };
    static final private int SCHERE = 0;
    static final private int STEIN = 1;
    static final private int PAPIER = 2;
    static final private int BRUNNEN = 3;
    static final private KnownCustomEmoji OHNE_BRUNNEN = func.getApi().getCustomEmojiById(703173587246252032L).orElse(null);

    private final long[] user;
    private final int[] userInput = {-1, -1};
    private final Texte texte;
    private final Texte texte2;
    private final DiscordApi api;
    private final long channel;

    private boolean ohneBrunnen = false;
    private boolean ended = false;

    private final ArrayList<Message> messages = new ArrayList<>();
    private final LinkedList<ListenerManager<?>> listenerManagers = new LinkedList<>();

    public RPS(User user1, User user2, Message m) {
        this.api = m.getApi();
        if (Math.random() >= 0.5) {
            this.user = new long[]{user1.getId(), user2.getId()};
        } else {
            this.user = new long[]{user2.getId(), user1.getId()};
        }
        this.texte = new Texte(user1);
        this.texte2 = new Texte(user2);

        this.channel = m.getChannel().getId();

        CompletableFuture<Void> messageUser1 = user1.sendMessage(func.getNormalEmbed(user1, m).setTitle(texte.get("SSSTitle")).setDescription(texte.get("SSSDesc")))
                .thenAccept(message -> handleSentMessages(message, user1.getId()));
        CompletableFuture<Void> messageUser2 = user2.sendMessage(func.getNormalEmbed(user2, m).setTitle(texte2.get("SSSTitle")).setDescription(texte2.get("SSSDesc")))
                .thenAccept(message -> handleSentMessages(message, user2.getId()));

        CompletableFuture.allOf(messageUser1, messageUser2).exceptionally(e -> {
            m.getChannel().sendMessage(func.setColorRed(func.getNormalEmbed(user1, m)).setDescription(e.getMessage()));
            return null;
        });
    }

    public RPS(Message m, User user) {
        this.api = m.getApi();
        this.user = new long[] {user.getId(), api.getYourself().getId()};
        this.texte = new Texte(user);
        this.texte2 = texte;

        this.channel = m.getChannel().getId();

        handleSentMessages(m, user.getId());
    }

    private void handleSentMessages(Message m, long user) {
        messages.add(m);
        addAllEmojisToMessage(m);
        addOhneBrunnenListener(m, user);
        addChooseListener(m, user);
    }

    private void addAllEmojisToMessage(Message m) {
        m.addReactions(EMOTES[SCHERE], EMOTES[STEIN], EMOTES[PAPIER], EMOTES[BRUNNEN], OHNE_BRUNNEN).exceptionally(ExceptionLogger.get());
    }

    private void removeBrunnen(Message m) {
        m.removeOwnReactionsByEmoji(EMOTES[BRUNNEN], OHNE_BRUNNEN).exceptionally(ExceptionLogger.get());
    }

    private void removeOwnReactions(Message m) {
        m.removeOwnReactionsByEmoji(EMOTES[SCHERE], EMOTES[STEIN], EMOTES[PAPIER]).exceptionally(ExceptionLogger.get());
        if (!ohneBrunnen) { //emojis still there
            removeBrunnen(m);
        }
    }

    private void addChooseListener(Message m, long user) {
        listenerManagers.add(m.addReactionAddListener(event -> {
            if (alreadyBothChosen()) {
                handleEnding();
                return;
            }
            if (event.getUserId() == user) {
                int emoji = emojiToInt(event.getEmoji());
                if (emoji != -1) {
                    if (emoji == BRUNNEN && ohneBrunnen) {
                        m.getChannel().sendMessage(func.setColorRed(func.getNormalEmbed(m.getUserAuthor().orElse(null), m))
                                .setTitle(texte.get("NichtMehrMöglich"))
                                .setDescription(texte.get("SSSOhneBrunnen")))
                                .exceptionally(ExceptionLogger.get());
                        return;
                    }

                    int userIndex = this.user[0] == user ? 0 : 1;
                    userInput[userIndex] = emoji;
                    if (m.getAuthor().isYourself()) {
                        m.edit(event.getMessage().orElse(m).getEmbeds().get(0).toBuilder().removeAllFields().addField("\u200B", EMOTES[emoji].getMentionTag())).exceptionally(ExceptionLogger.get());
                    }
                    handleEnding();
                }
            }
        }));
    }

    private int emojiToInt(Emoji emoji) {
        for (int i = 0; i < EMOTES.length; i++) {
            if (emoji.equalsEmoji(EMOTES[i])) {
                return i;
            }
        }
        return -1;
    }

    private void handleEnding() {
        if (ended || !alreadyBothChosen()) return;
        ended = true;

        if (onlyOnePlayer()) {
            userInput[1] = func.getRandom(0, ohneBrunnen ? 2 : 3);
        }

        if (userInput[0] == userInput[1]) {
            tie();
        } else {
            if (firstPlayerWins()) {
                wins(0);
            } else {
                wins(1);
            }
        }

        messages.forEach(this::removeOwnReactions);
        listenerManagers.forEach(ListenerManager::remove);
    }

    private void wins(int winnerId) {
        int loserId = winnerId == 0 ? 1 : 0;
        api.getTextChannelById(channel).ifPresent(channel -> {
            channel.sendMessage(
                    func.getNormalEmbed(api.getCachedUserById(user[winnerId]).orElse(null), null).setTitle(texte.getString("SSSTitle").toString())
                    .setDescription(api.getCachedUserById(user[winnerId])
                            .map(Texte::new).orElse(texte).getString(
                                    "SSSEndMessage",
                                    Long.toUnsignedString(user[winnerId]),
                                    EMOTES[userInput[winnerId]].getMentionTag(),
                                    Long.toUnsignedString(user[loserId]),
                                    EMOTES[userInput[loserId]].getMentionTag()
                            ).toString())
            ).exceptionally(ExceptionLogger.get()).thenAccept(this::addRerunListener);
        });
        if(gameGivesPoints()) func.addGame("sss", user[winnerId], user[loserId]);
    }

    private boolean gameGivesPoints() {
        return !onlyOnePlayer() && user[0] != user[1];
    }

    private void addRerunListener(Message message) {
        if (message != null) {
            message.addReactions(Befehl.REPEAT_EMOJI);
            ReactionAddListener[] listener = new ReactionAddListener[1];
            listener[0] = event -> {
                if (event.getUserId() == api.getYourself().getId()) return;
                if (event.getUserId() == user[0] || event.getUserId() == user[1]) {
                    if (event.getEmoji().equalsEmoji(Befehl.REPEAT_EMOJI)) {
                        try {
                            new RPS(api.getUserById(user[0]).get(), api.getUserById(user[1]).get(), message);
                        } catch (Exception e) {
                            message.getChannel().sendMessage(e.toString());
                        }
                        message.removeListener(ReactionAddListener.class, listener[0]);
                        listener[0] = null;
                    }
                }
            };
            message.addReactionAddListener(listener[0]);

            message.getApi().getThreadPool().getScheduler().schedule(() -> {
                if (listener[0] != null) { //if it hasn't been removed
                    message.removeListener(ReactionAddListener.class, listener[0]);
                    message.removeReactionsByEmoji(Befehl.REPEAT_EMOJI);
                }
            }, 10, TimeUnit.MINUTES);
        }
    }

    private void tie() {
        api.getTextChannelById(channel).ifPresent(channel -> {
            channel.sendMessage(
                func.getNormalEmbed(api.getCachedUserById(user[0]).orElse(null), null)
                    .setTitle(texte.getString("SSSTitle").toString())
                    .addField("\u200B", texte.getString("SSSUnentschieden", EMOTES[userInput[0]].getMentionTag()).toString())
            ).exceptionally(ExceptionLogger.get()).thenAccept(this::addRerunListener);
        });
        if(gameGivesPoints()) func.addGame0("sss", user[0], user[1]);
    }

    private void addOhneBrunnenListener(Message m, long user) {
        ReactionAddListener[] listener = new ReactionAddListener[1];
        listener[0] = event -> {
            if (!alreadyMinOneChosen()
                    && event.getUserId() == user
                    && event.getEmoji().equalsEmoji(OHNE_BRUNNEN)) {
                ohneBrunnen = true;
                for (Message mess : messages) {
                    removeBrunnen(mess);
                }
                m.removeListener(ReactionAddListener.class, listener[0]);
            }

            if (alreadyMinOneChosen()) {
                m.removeListener(ReactionAddListener.class, listener[0]);
            }
        };
        listenerManagers.add(m.addReactionAddListener(listener[0]));
    }

    private boolean alreadyMinOneChosen() {
        return ohneBrunnen //someone disabled brunnen
                || userInput[0] != -1 || userInput[1] != -1;
    }

    private boolean alreadyBothChosen() {
        return userInput[0] != -1 && (onlyOnePlayer() || userInput[1] != -1);
    }

    private boolean onlyOnePlayer() {
        return user[1] == api.getYourself().getId();
    }

    private boolean firstPlayerWins() {
        int user = userInput[0];
        int geg = userInput[1];
        if (user == 3 && geg != 2) return true; // 0     , 1    , 2     , 3
        if (geg == 3 && user == 2) return true; //{schere, stein, papier, brunnen};
        if (geg == 3 && user != 2) return false;
        if (user == 0 && geg != 1) return true;
        if (user == 1 && geg != 2) return true;
        if (user == 2 && geg != 0) return true;
        return false;
    }
}
