package Jemand.Listener;

import Jemand.func;
import com.fasterxml.jackson.databind.JsonNode;
import de.jojii.matrixclientserver.Bot.Client;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.UncachedMessageUtil;
import org.javacord.api.entity.message.WebhookMessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.MessageEditListener;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.util.DiscordRegexPattern;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.core.DiscordApiImpl;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Channelportal implements MessageCreateListener, MessageEditListener, ReactionAddListener {

    public static final String[][] channels;
    private static final Pattern WEBHOOK_MENTION = Pattern.compile("@(?<name>.{1,32})#0000");
    public static final String SEPARATOR = "\u200C";
    public static final Pattern CLONED_MESSAGE = Pattern.compile("(?s)^[\u200D\u200B]{42,64}\u200C.*");

    static  {
        String[] lol = func.readtextoffile("channelportal.txt").split("\\s+");
        channels = new String[lol.length][2];
        for (int i = 0; i < lol.length; i++)
            channels[i] = lol[i].split("_");
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getServer().isEmpty() || (event.getMessageAuthor().isWebhook() && CLONED_MESSAGE.matcher(event.getMessage().getContent()).matches())) return;

        for (String[] channel : channels) {
            for (int i = 0; i < channel.length; i++) {
                if (channel[i].equals(event.getChannel().getIdAsString())) {
                    try {
                        mirrorMessage(event.getMessage(), channel[(i + 1) % 2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
       }
    }

    @Override
    public void onMessageEdit(MessageEditEvent event) {
        if(event.getServer().isEmpty() || CLONED_MESSAGE.matcher(event.getNewContent()).matches()) return;

        for (String[] channel : channels) {
            for (int i = 0; i < channel.length; i++) {
                if (channel[i].equals(event.getChannel().getIdAsString())) {
                    try {
                        String otherChannel = channel[(i + 1) % 2];

                        getClonedMessage(null, event.getMessageId(), otherChannel).ifPresent(mf ->  {
                            mf.thenAccept(message -> {
                                if (message != null && message.getAuthor().isWebhook()) {
                                    message.getAuthor().asWebhook().ifPresent(webhookCompletableFuture -> {
                                        webhookCompletableFuture.thenAccept(webhook -> {
                                            webhook.asIncomingWebhook().ifPresent(iw -> {

                                                String newContent = addIdToContent(event.getMessageId(), event.getNewContent());

                                                EmbedBuilder embed = event.getNewEmbeds().size() > 0 ? event.getNewEmbeds().get(0).toBuilder() : null;

                                                event.getApi().getUncachedMessageUtil()
                                                        .edit(webhook.getId(), iw.getToken(), message.getId(), newContent, true, embed, true)
                                                        .exceptionally(ExceptionLogger.get());
                                            });
                                        });
                                    });
                                } else {
                                    System.out.println((message == null ? mf : message.getLink()) + " isn't present or not by webhook");
                                }
                            }).exceptionally(ExceptionLogger.get());
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    static private void mirrorMessage(Message m, String channel_id) {
        if (m.getContent().isEmpty() && m.getEmbeds().isEmpty() && m.getAttachments().size() == 0) return;

        m.getApi().getServerTextChannelById(channel_id).ifPresent(channel -> {

            func.getIncomingWebhook(channel).ifPresent( webhook -> {

                WebhookMessageBuilder mb = m.toWebhookMessageBuilder();

                m.getAuthor().asUser().ifPresent(u -> {
                    if (channel.getServer().getMembers().contains(u)) {
                        mb.setDisplayName(u.getDisplayName(channel.getServer()));
                    }
                });

                String content = m.getContent();

                AllowedMentionsBuilder amb = new AllowedMentionsBuilder();

                Matcher mentions = WEBHOOK_MENTION.matcher(content);
                while (mentions.find()) {
                    String name = mentions.group("name");
                    String id = m.getServer()
                            .map(server -> server.getMembersByDisplayName(name))
                            .flatMap(users -> users.stream()
                                    .filter(u -> channel.getServer().getMembers().contains(u)).findFirst())
                            .map(DiscordEntity::getIdAsString)
                            .orElseGet(() -> channel.getServer().getMembersByDisplayName(name).stream().findFirst().map(DiscordEntity::getIdAsString).orElse(null));

                    if (id == null) {
                        content = mentions.replaceFirst("@" + name);
                    } else {
                        amb.addUser(id);
                        content = mentions.replaceFirst("<@" + id + ">");
                    }
                    mentions.reset(content);
                }

                mb.setAllowedMentions(amb.build());

                mb.setContent(addIdToContent(m.getId(), content));

                mb.send(webhook).exceptionally(ExceptionLogger.get()).join();
            });
        });
    }

    static private String addIdToContent(long id, String content) {
        String idStr = func.longToBinaryBlankString(id) + SEPARATOR;
        return idStr + (content.length() > 2000 - idStr.length() ? content.substring(0, 2000 - idStr.length()) : content);
    }

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        for (String[] channel : channels) {
            for (int i = 0; i < channel.length; i++) {
                if (channel[i].equals(event.getChannel().getIdAsString())) {
                    int other = (i + 1) % 2;
                    event.requestMessage().thenAccept(m -> mirrorReactions(event, m, channel[other]));
                    break;
                }
            }
        }
    }

    static private void mirrorReactions(ReactionAddEvent event, Message m, String channel_id) {
        getClonedMessage(m.getContent(), m.getId(), channel_id).ifPresent(mf ->  {
            mf.thenAccept(message -> {
                if (message != null) {
                    message.addReaction(event.getEmoji()).exceptionally(ExceptionLogger.get());
                }
            });
        });
    }

    static private Optional<CompletableFuture<Message>> getClonedMessage(String messageContent, long messageId, String otherChannelId) {
        return func.getApi().getServerTextChannelById(otherChannelId).flatMap(channel -> {
            CompletableFuture<Message> other = null;
            if (messageContent != null && CLONED_MESSAGE.matcher(messageContent).matches()) {
                other = channel.getApi().getMessageById(func.binaryBlankStringToLong(messageContent.split(SEPARATOR, 2)[0]), channel);
            } else {
                String blank = func.longToBinaryBlankString(messageId) + SEPARATOR;
                other = channel.getMessagesBetweenUntil(message ->  message.getAuthor().isWebhook() && message.getContent().startsWith(blank), messageId, messageId + 7549747200000L /*30 Minuten in millis + 22 leere bits*/)
                        .exceptionally(t -> null)
                        .thenApply(messages -> messages.getNewestMessage().orElse(null));
            }
            return Optional.ofNullable(other);
        });
    }
}
