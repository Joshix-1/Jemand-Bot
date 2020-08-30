package Jemand.Listener;

import Jemand.func;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.WebhookMessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Channelportal implements MessageCreateListener, ReactionAddListener {
    private static final String[][] channels; //unser
    static  {
        String[] lol = func.readtextoffile("channelportal.txt").split("\\s+");
        channels = new String[lol.length][2];
        for (int i = 0; i < lol.length; i++)
            channels[i] = lol[i].split("_");
    }
    private static final Pattern WEBHOOK_MENTION = Pattern.compile("@(?<name>.{1,32})#0000");
    public static final String SEPARATOR = "\u200C";
    public static final Pattern CLONED_MESSAGE = Pattern.compile("(?s)^[\u200D\u200B]{42,64}\u200C.*");

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

    static private void mirrorMessage(Message m, String channel_id) {
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
                    //}
                    if (id == null) {
                        content = mentions.replaceFirst("@" + name);
                    } else {
                        amb.addUser(id);
                        content = mentions.replaceFirst("<@" + id + ">");
                    }
                    mentions.reset(content);
                }

                mb.setAllowedMentions(amb.build());

                String id = func.longToBinaryBlankString(m.getId()) + SEPARATOR;
                mb.setContent(id + (content.length() > 2000 - id.length() ? content.substring(0, 2000 - id.length()) : content));

                mb.send(webhook).exceptionally(ExceptionLogger.get()).join();
            });
        });
    }

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        for (String[] channel : channels) {
            for (int i = 0; i < channel.length; i++) {
                if (channel[i].equals(event.getChannel().getIdAsString())) {
                    int other = (i + 1) % 2;
                    event.getApi().getMessageById(event.getMessageId(), event.getChannel()).thenAccept(m -> mirrorReactions(event, m, channel[other]));
                    break;
                }
            }
        }
    }

    static private void mirrorReactions(ReactionAddEvent event, Message m, String channel_id) {
        m.getApi().getServerTextChannelById(channel_id).ifPresent(channel -> {
            Message other = null;
            if (CLONED_MESSAGE.matcher(m.getContent()).matches()) {
                other = m.getApi().getMessageById(func.binaryBlankStringToLong(m.getContent().split(SEPARATOR, 2)[0]), channel).exceptionally(ExceptionLogger.get()).join();
            } else {
                String blank = func.longToBinaryBlankString(m.getId()) + SEPARATOR;
                other = channel.getMessagesBetweenUntil(message ->  message.getAuthor().isWebhook() && message.getContent().startsWith(blank), m.getId(), m.getId() + 7549747200000L /*30 Minuten in millis + 22 leere bits*/).join().getNewestMessage().orElse(null);
            }

            if (other != null) {
                other.addReaction(event.getEmoji()).exceptionally(ExceptionLogger.get());
            }
        });
    }
}
