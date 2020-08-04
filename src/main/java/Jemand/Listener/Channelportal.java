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

import java.util.List;
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
    private static final String SEPARATOR = "\u200C";

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getMessageAuthor().isWebhook() || event.getServer().isEmpty()) return;
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
        m.getApi().getServerTextChannelById(channel_id).ifPresent(channel ->{
            List<IncomingWebhook> webhooks = channel.getIncomingWebhooks().join();
            IncomingWebhook webhook = webhooks.size() == 0 ? channel.createWebhookBuilder()
                    .setAvatar(m.getApi().getYourself().getAvatar()).setName(m.getApi().getYourself().getName()).create().join()
                    : webhooks.get(0);

            WebhookMessageBuilder mb = m.toWebhookMessageBuilder();

            m.getAuthor().asUser().ifPresent(u -> {
                //String name;
                if(channel.getServer().getMembers().contains(u)) {
                    mb.setDisplayName(u.getDisplayName(channel.getServer()));
                } /* else {
                    name = m.getAuthor().getDisplayName();
                }
                mb.setDisplayName(func.longToBinaryBlankString(u.getId()) + SEPARATOR + name);
                */
            });

            String content = m.getContent();

            AllowedMentionsBuilder amb = new AllowedMentionsBuilder();

            Matcher mentions = WEBHOOK_MENTION.matcher(content);
            while (mentions.find()) {
                //String id = mentions.group("id");
                //if (id != null) {
                //    id = channel.getServer().getMemberById(func.binaryBlankStringToLong(id)).map(DiscordEntity::getIdAsString).orElse(null);
                //}
                String name = mentions.group("name");
                //if (id == null) {
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
            if (m.getAuthor().isWebhook()) {
                if (m.getContent().contains("\u200C")) {
                    other = m.getApi().getMessageById(func.binaryBlankStringToLong(m.getContent().split(SEPARATOR, 2)[0]), channel).join();
                }
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
