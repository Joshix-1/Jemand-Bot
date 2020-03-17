package Jemand.Listener;

import Jemand.Texte;
import Jemand.func;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedField;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;
import org.javacord.api.util.DiscordRegexPattern;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

public class ReactionRole {
    private static String[] abc = "\uD83C\uDDE6 \uD83C\uDDE7 \uD83C\uDDE8 \uD83C\uDDE9 \uD83C\uDDEA \uD83C\uDDEB \uD83C\uDDEC \uD83C\uDDED \uD83C\uDDEE \uD83C\uDDEF \uD83C\uDDF0 \uD83C\uDDF1 \uD83C\uDDF2 \uD83C\uDDF3 \uD83C\uDDF4 \uD83C\uDDF5 \uD83C\uDDF6 \uD83C\uDDF7 \uD83C\uDDF8 \uD83C\uDDF9 \uD83C\uDDFA \uD83C\uDDFB \uD83C\uDDFC \uD83C\uDDFD \uD83C\uDDFE \uD83C\uDDFF".split(" ");

    public static class Add implements ReactionAddListener {

        @Override
        public void onReactionAdd(ReactionAddEvent event) {
            try {
                if(!event.getUser().isBot()) getRole(event).ifPresent(role -> role.addUser(event.getUser()).join());
            } catch (Exception e) {func.handle(e);}
        }
    }

    public static class Remove implements ReactionRemoveListener {

        @Override
        public void onReactionRemove(ReactionRemoveEvent event) {
            try {
                if(!event.getUser().isBot()) getRole(event).ifPresent(role -> role.removeUser(event.getUser()).join());
            } catch (Exception e) {func.handle(e);}
        }
    }

    private static Optional<Role> getRole(SingleReactionEvent e) {
        Message m = e.getApi().getMessageById(e.getMessageId(), e.getChannel()).join();
        String emoji = e.getEmoji().asUnicodeEmoji().orElse("");

        if(!m.getAuthor().isYourself()
                || !m.getContent().equals(getMessageContentOfChannel(m.getChannel()))
                || e.getChannel().asServerTextChannel().isEmpty()
                || emoji.isEmpty()
                || m.getEmbeds().isEmpty()
                || !m.getEmbeds().get(0).getDescription().orElse("").equals("Reaction-Role:")
                || m.getEmbeds().get(0).getFields().isEmpty())
            return Optional.empty();
        List<EmbedField> fields = m.getEmbeds().get(0).getFields();
        for(EmbedField f : fields) {
            if(!f.isInline() && f.getName().equals(emoji + ":")) {
                Matcher matcher = DiscordRegexPattern.ROLE_MENTION.matcher(f.getValue());
                if(matcher.find()) {
                    Optional<Role> role = e.getChannel().asServerChannel().get().getServer().getRoleById(matcher.group("id"));
                    if(role.isPresent()) return role;
                }
            }
        }
        return Optional.empty();
    }

    public static void sendReactionRoleMessage(MessageCreateEvent event, String... role_ids) throws Exception {
        if(event.getServer().isEmpty()) return;
        EmbedBuilder embed = func.getNormalEmbed(event).setTitle("Reaction-Role:").setDescription(new Texte(event).get("ReactionRoleDesc"));
        int count = 0;
        for (int i = 0; i < role_ids.length && count < 20; i++) {
            Optional<Role> role = event.getServer().get().getRoleById(func.LongFromString(role_ids[i], 0L));
            if(role.isPresent() && event.getMessageAuthor().asUser().map(user -> user.canManageRole(role.get())).orElse(false))
                embed.addField(abc[count++] + ":", role.get().getMentionTag());
        }
        if(count==0) embed.setDescription(new Texte(event).get("ReactionRoleNo"));
        CompletableFuture<Message> m = event.getChannel().sendMessage(getMessageContentOfChannel(event.getChannel()), embed);
        final int finalCount = count;
        m.thenAcceptAsync(message -> {
            for (int i = 0; i < finalCount; i++)
                message.addReaction(abc[i]).join();

        }).exceptionally(ExceptionLogger.get());
    }


    public static String getMessageContentOfChannel(TextChannel channel)  {
        return Long.toBinaryString(channel.getId()).replace('0', '\u200B').replace('1', '\u200D');
    }

}
