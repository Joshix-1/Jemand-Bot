package Jemand.Listener;

import Jemand.func;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.auditlog.AuditLogActionType;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ChannelCategoryBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.ServerChannelDeleteEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.server.member.ServerMemberBanEvent;
import org.javacord.api.event.server.role.RoleChangePermissionsEvent;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.List;

public class GuildCloner {
    static public final long AN = 367648314184826880L;
    static private final long COPY = 740214894036779009L;
    static public final long MITGLIED = 367649615484551179L;
    static public final long ROLLENMEISTER = 493572898577973249L;

    public GuildCloner(DiscordApi api) {
        api.getServerById(AN).ifPresent(server -> {
            server.addUserRoleAddListener(this::userAddedRole);
            server.addMessageCreateListener(this::messageCreated);
            server.addServerMemberBanListener(this::memberBanned);
            server.addServerChannelDeleteListener(this::channelDeleted);
            server.addRoleChangePermissionsListener(this::roleChangedPermission);
        });
    }

    private void roleChangedPermission(RoleChangePermissionsEvent event) {
        long role = event.getRole().getId();
        if (event.getRole().isEveryoneRole()) {
            if (event.getNewPermissions().getAllowedBitmask() != 70321344) //https://discordapi.com/permissions.html#70321344
                event.getRole().updatePermissions(Permissions.fromBitmask(70321344)).exceptionally(ExceptionLogger.get());
        } else if (role != MITGLIED && role != ROLLENMEISTER) {
            if (event.getRole().getName().toLowerCase().contains("mitlgied")) { //vorläufiges Mitglied, rest braucht die Rechte nicht
                if (event.getNewPermissions().getAllowedBitmask() != 267902401) //https://discordapi.com/permissions.html#267902401
                    event.getRole().updatePermissions(Permissions.fromBitmask(267902401)).exceptionally(ExceptionLogger.get());
            } else if (event.getNewPermissions().getAllowedBitmask() != 0)
                event.getRole().updatePermissions(Permissions.fromBitmask(0)).exceptionally(ExceptionLogger.get());
        }
    }

    private void userAddedRole(UserRoleAddEvent event) {
        if (event.getRole().getId() == MITGLIED) {
            event.getUser().sendMessage("Herzlichen Glückwunsch. Du bist nun Mitglied auf der Gilde des asozialen Netzwerkes.").exceptionally(ExceptionLogger.get());
        }
    }

    private void messageCreated(MessageCreateEvent event) {
        event.getApi().getServerById(COPY).ifPresent(copy -> {
            List<ServerTextChannel> channels = copy.getTextChannelsByName(event.getChannel().getIdAsString());
            ServerTextChannel channel;
            if (channels.size() > 0) {
                channel = channels.get(0);
            } else {
                ServerTextChannel old = event.getServerTextChannel().get();
                channel = new ServerTextChannelBuilder(copy)
                        .setName(old.getIdAsString())
                        .setSlowmodeDelayInSeconds(old.getSlowmodeDelayInSeconds())
                        .setTopic(old.getTopic()).create().join();
            }
            event.getMessage().toWebhookMessageBuilder().send(func.getIncomingWebhook(channel)).exceptionally(ExceptionLogger.get());
        });

        if(!event.getMessageAuthor().isRegularUser() || (event.getMessageContent().isEmpty() && event.getMessage().getEmbeds().isEmpty())) return;
        User user = event.getMessageAuthor().asUser().orElse(null);
        Server server = event.getServer().orElse(null);
        if (user != null && server != null && user.getRoles(server).size() < 2) {
            try {
                user.addRole(server.getRoleById(559141475812769793L).orElseThrow(() -> new AssertionError("Rolle nicht da"))).join();
                user.addRole(server.getRoleById(559444155726823484L).orElseThrow(() -> new AssertionError("Rolle nicht da"))).join();
            } catch (Exception e) {
                func.handle(e);
            }
        }
    }

    private void memberBanned(ServerMemberBanEvent event) {
        if (func.userIsTrusted(event.getUser())) {
            event.getServer().unbanUser(event.getUser());
        }
    }

    private void channelDeleted(ServerChannelDeleteEvent event) {
        try {
            event.getServer().getAuditLog(1, AuditLogActionType.CHANNEL_DELETE).join().getInvolvedUsers().forEach(user1 -> {
                user1.removeRole(func.getApi().getRoleById(MITGLIED).orElseThrow(() -> new AssertionError("Mitgliedsrolle nicht da")), "Channel got deleted").join();
                event.getServer().getOwner()
                        .sendMessage(user1.getDisplayName(event.getServer()) + " (name: " + user1.getDiscriminatedName() + "; id: " + user1.getIdAsString() + ") hat #" + event.getChannel().getName() + " gelöscht.").join();

            });
        } catch (Exception e) {
            func.handle(e);
        }
    }

//Fortnite-Detektor:
        //server.addUserChangeActivityListener(event -> {
        //	server.getTextChannelById(623940807619248148L).ifPresent(textchannel -> {
        //		event.getNewActivity().ifPresent(activity -> {
        //			event.getOldActivity().ifPresent(oldactivity -> {
        //				if (!oldactivity.getApplicationId().orElse(0L).equals(activity.getApplicationId().orElse(1L)) && ((activity.getType().equals(ActivityType.PLAYING) && activity.getName().equals("Fortnite")) || (activity.getApplicationId().orElse(0L) == 432980957394370572L))) {
        //					EmbedBuilder embed = new EmbedBuilder()
        //							.setColor(Color.RED)
        //							.setTimestampToNow()
        //							.setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar());
        //					activity.getDetails().ifPresent(string -> {
        //						embed.addField("\u200B", "\nDetails: (" + string + ")");
        //					});
        //					textchannel.sendMessage(embed.setDescription(event.getUser().getMentionTag() + " spielt " + activity.getName() + "."));
        //					func.getApi().getRoleById(623193804551487488L).ifPresent(event.getUser()::addRole);
        //				}
        //			});
        //		});
        //	});
        //	//}
        //});
}
