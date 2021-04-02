package Jemand.Listener;

/*
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramDiscordBridge extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (!update.getMessage().getFrom().getId().equals(0l)) {

        }
    }

    public void senMessage(org.javacord.api.entity.message.Message m) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId("")
                .setText(m.getContent());
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "jemand_bot";
    }

    @Override
    public String getBotToken() {
        return func.pws[9];
    }
}*/
