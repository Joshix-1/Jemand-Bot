package Jemand;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordScanner {
    public static final Long ALL_USERS = -1L;
    private final List<Long> users;
    private final Map<Long, Message> messages = new HashMap<>();
    ListenerManager<MessageCreateListener> messageCreateListenerListenerManager;
    private long lastHandled;
    private boolean terminated = false;
    private final DiscordApi api;

    public DiscordScanner (Message after) {
        this(after, ALL_USERS);
    }

    public DiscordScanner (Message after, MessageAuthor... authorsToHandle) {
        this(after, getIds(authorsToHandle));
    }

    static private Long[] getIds(DiscordEntity[] entities) {
        Long[] ids = new Long[entities.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = entities[i].getId();
        }
        return ids;
    }

    public DiscordScanner (Message after, Long... authorsToHandle) {
        this.api = after.getApi();
        this.users = Arrays.asList(authorsToHandle);
        users.remove(ALL_USERS);
        this.lastHandled = after.getId();
        after.getChannel().getMessagesAfter(10, lastHandled).thenAccept(messages -> messages.forEach(m -> {
            if (authorIsOk(m.getAuthor())) {
                this.messages.put(m.getId(), m);
            }
        }));

        this.messageCreateListenerListenerManager = after.getChannel().addMessageCreateListener(event -> {
            if (authorIsOk(event.getMessageAuthor())) {
                this.messages.put(event.getMessageId(), event.getMessage());
            }
        });
    }

    private boolean authorIsOk(MessageAuthor author) {
        return !author.isYourself() && users.size() == 0 || users.contains(author.getId());
    }

    private void removeHandledMessages() {
        if (messages.isEmpty()) {
            return;
        }

        messages.keySet().iterator().forEachRemaining(id -> {
            if (id <= this.lastHandled) { //message is older or is last and should've been handled.
                messages.remove(id);
            }
        });
    }

    public boolean nextIsReady() {
        removeHandledMessages();
        return messages.size() != 0;
    }

    public Message nextMessage() {
        if (!nextIsReady() || isTerminated()) {
            return null;
        }

        return messages.keySet().stream().sorted().findFirst().map(id -> {
            this.lastHandled = id;
            return messages.get(id);
        }).orElse(null);
    }

    public String next() {
        if (!nextIsReady() || isTerminated()) {
            return null;
        }

        return messages.keySet().stream().sorted().findFirst().map(id -> {
            this.lastHandled = id;
            return messages.get(id).getContent();
        }).orElse(null);
    }

    public void terminate() {
        if (isTerminated()) return;

        messageCreateListenerListenerManager.remove();
        messages.clear();
        lastHandled = 0;
        terminated = true;
    }

    public boolean isTerminated() {
        return terminated;
    }

}

