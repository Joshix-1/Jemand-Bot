package Jemand.Listener;

import Jemand.func;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.Webhook;
import org.javacord.api.entity.webhook.WebhookBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Channelportal implements MessageCreateListener {
    static String[][] channels; //unser
    static  {
        String[] lol = func.readtextoffile("channelportal.txt").split("\\s+");
        channels = new String[lol.length][2];
        for (int i = 0; i < lol.length; i++)
            channels[i] = lol[i].split("_");
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getMessageAuthor().isWebhook() || event.getServer().isEmpty()) return;
        for (String[] channel : channels) {
            for (int i = 0; i < channel.length; i++) {
                if (channel[i].equals(event.getChannel().getIdAsString())) {
                    try {
                        mirror_message(event.getMessage(), channel[(i + 1) % 2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private void mirror_message(Message m, String channel_id) throws  Exception {
        m.getApi().getServerTextChannelById(channel_id).ifPresent(channel ->{
            AtomicReference<Webhook> w = new AtomicReference<>(null);
            channel.getWebhooks().join().forEach(webhook -> {
                if(m.getApi().getYourself().getId() == webhook.getCreator().map(User::getId).orElse(0L)) {
                    w.set(webhook);
                }
            });
            User u = m.getUserAuthor().orElse(m.getApi().getYourself());
            Webhook w2;
            String DisplayName = u.getNickname(channel.getServer()).orElse(u.getDisplayName(m.getServer().get()));

            if(w.get() == null) {
                w2 = new WebhookBuilder(channel).setAvatar(u.getAvatar()).setName(DisplayName + " (" + u.getDiscriminatedName() + ")").create().join();
            } else {
                w2  = w.get().createUpdater().setAvatar(u.getAvatar()).setName(DisplayName + " (" + u.getDiscriminatedName() + ")").update().join();
            }
            WebhookClient client = WebhookClient.withId(w2.getId(), w2.getToken().get());
            String content = m.getContent();
            if(content.isEmpty() && m.getEmbeds().size() > 0)
                content = m.getEmbeds().get(0).getDescription().orElse("");

            int roleColor = u.getRoleColor(channel.getServer()).flatMap(color -> Optional.of(color.getRGB())).orElse(u.getRoleColor(m.getServer().get()).flatMap(color -> Optional.of(color.getRGB())).orElse(Color.GRAY.getRGB()));
            WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                    .setColor(roleColor)
                    .setDescription(content);

            m.getAttachments().forEach(att ->{
                embed.addField(new WebhookEmbed.EmbedField(false, "\u200B", att.getUrl().toString()));
            });

            client.send(embed.build()).join();
        });
    }
}
