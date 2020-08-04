package Jemand.Listener;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.List;

public class GuildCloner {
    static private final long AN = 367648314184826880L;
    static private final long COPY = 740214894036779009L;
    static private final long MITGLIED = 367649615484551179L;

    public GuildCloner(DiscordApi api) {
        api.getServerById(AN).ifPresent(server -> {
            server.addUserRoleAddListener(this::userAddedRole);
            server.addMessageCreateListener(this::messageCreated);
        });
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

            }
        });
    }
}
