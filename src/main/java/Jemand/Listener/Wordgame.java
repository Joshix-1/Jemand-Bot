package Jemand.Listener;

import Jemand.func;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wordgame implements MessageCreateListener {

    private final long channelId;
    private final DiscordApi api;
    private final List<String> messages;

    public Wordgame(TextChannel channel) {
        this.channelId = channel.getId();
        this.api = channel.getApi();
        messages = getMessages();


        channel.addMessageCreateListener(this);
    }

    public void onMessageCreate(MessageCreateEvent event) {
        String word = event.getMessageContent().trim().toLowerCase();
        if (word.split("\\s+").length > 1) {
            event.getMessage().delete("Nachricht ist länger als ein Wort.").exceptionally(ExceptionLogger.get());
            return;
        }

        if (messages.contains(word)) {
            event.getMessage().delete("Wort wurde bereits gesendet.").exceptionally(ExceptionLogger.get());
            return;
        }

        String lastWord = getLastWord();
        if (lastWord == null) {
            addMessage(word);
            return;
        }

        if (areSimilarEnough(lastWord, word)) {
            addMessage(word);
            return;
        }

        event.getMessage().delete("Die Nachrichten unterscheiden sich zu sehr.").exceptionally(ExceptionLogger.get());
    }

    private boolean areSimilarEnough(String mesg1, String mesg2) {
        if (Math.abs(mesg1.length() - mesg2.length()) > 1) {
            return false; // diff of length bigger than one
        }
        int diffCount = 0;
        for (int i = 0; i < Math.min(mesg1.length(), mesg2.length()); i++) {

        }
        return false;
        //return StringUtils.difference(mesg1, mesg2).length();
    }

    private void addMessage(String mesg) {
        messages.add(mesg);
        saveMessages();
    }

    private String getLastWord() {
        if (messages.size() > 0) {
            return messages.get(messages.size() - 1);
        } else {
            return null;
        }
    }

    private String getFileName() {
        return "wordgame/" + channelId + ".txt";
    }

    private List<String> getMessages() {
        return Arrays.asList(func.readtextoffile(getFileName()).split("\\s+"));
    }

    private void saveMessages() {
        StringBuilder sb = new StringBuilder(messages.size());
        messages.forEach(sb::append);
        func.writetexttofile(sb.toString(), getFileName());
    }
}
