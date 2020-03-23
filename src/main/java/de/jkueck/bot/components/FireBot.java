package de.jkueck.bot.components;

import com.vdurmont.emoji.EmojiParser;
import de.jkueck.bot.EmailReceivedEvent;
import de.jkueck.bot.database.entity.Chat;
import de.jkueck.bot.database.repository.ChatRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Log4j2
@Component
public class FireBot extends TelegramLongPollingBot implements ApplicationListener<EmailReceivedEvent> {

    private final ChatRepository chatRepository;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.user.admin}")
    private long adminTelegramChatId;


    public FireBot(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {

            Message message = update.getMessage();

            long telegramChatId = message.getChatId();

            if (message.hasText()) {

                log.info("receive text message: " + message.getText());

                switch (message.getText()) {
                    case "/start":
                        Optional<Chat> optionalChat = this.chatRepository.findByTelegramChatId(telegramChatId);
                        if (!optionalChat.isPresent()) {
                            if (telegramChatId == this.adminTelegramChatId) {
                                this.chatRepository.save(new Chat(telegramChatId, Boolean.TRUE));
                            } else {
                                this.chatRepository.save(new Chat(telegramChatId));
                            }
                            this.sendTextMessage(telegramChatId, "Willkommen, du bekommst nun Benachrichtigungen.");
                        } else {
                            this.sendTextMessage(telegramChatId, "Du bekommst bereits Benachrichtigungen.");
                        }
                        break;
                }

            }

        }

    }

    private void sendTextMessage(long telegramChatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(telegramChatId);
        sendMessage.setText(text);
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onApplicationEvent(EmailReceivedEvent event) {
        try {
            log.info("received event: " + event.getMessage());

            for (Chat chat : this.chatRepository.findAll()) {
                this.sendTextMessage(chat.getTelegramChatId(), EmojiParser.parseToUnicode(":fire_engine:") + " " + EmojiParser.parseToUnicode(":rotating_light:") + " - EINSATZ! Alarmierung war um " + new SimpleDateFormat("HH:mm").format(event.getMessage().getAlertTimestamp()) + " mit dem Stichwort: " + event.getMessage().getMessage() + ".");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
