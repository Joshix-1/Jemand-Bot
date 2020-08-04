package Jemand.Listener;

import Jemand.func;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.WebhookMessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.user.User;
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
    private static Pattern WEBHOOK_MENTION = Pattern.compile("@(?<name>.{1,32})#0000");




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
                if(channel.getServer().getMembers().contains(u)) {
                    mb.setDisplayName(u.getDisplayName(channel.getServer()));
                }
            });

            String content = m.getContent();

            AllowedMentionsBuilder amb = new AllowedMentionsBuilder();

            Matcher mentions = WEBHOOK_MENTION.matcher(content);
            while (mentions.find()) {
                String name = mentions.group("name");
                User user = m.getServer()
                        .map(server -> server.getMembersByDisplayName(name))
                        .flatMap(users -> users.stream()
                                .filter(u -> channel.getServer().getMembers().contains(u)).findFirst())
                        .orElseGet(() -> channel.getServer().getMembersByDisplayName(name).stream().findFirst().orElse(null));
                if (user == null) {
                    content = mentions.replaceFirst("@invalid-user");
                } else {
                    amb.addUser(user.getId());
                    content = mentions.replaceFirst(user.getMentionTag());
                }
                mentions.reset(content);
            }

            mb.setAllowedMentions(amb.build());

            String id = func.longToBinaryBlankString(m.getId()) + '\u200C';
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
                    other = m.getApi().getMessageById(func.binaryBlankStringToLong(m.getContent().split("\u200C", 2)[0]), channel).join();
                }
            } else {
                String blank = func.longToBinaryBlankString(m.getId()) + '\u200C';
                other = channel.getMessagesAfterUntil(message ->  message.getAuthor().isWebhook() && message.getContent().startsWith(blank), m).join().getNewestMessage().orElse(null);
            }

            if (other != null) {
                other.addReaction(event.getEmoji()).exceptionally(ExceptionLogger.get());
            }
        });
    }
}
