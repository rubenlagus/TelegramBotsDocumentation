package org.telegram.telegrambots.tutorial.Lesson2.src;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;
import java.util.List;

public class PhotoBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public PhotoBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (message_text.equals("/start")) {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text(message_text)
                        .build();
                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/pic")) {
                SendPhoto msg = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        .photo(new InputFile("https://png.pngtree.com/background/20230519/original/pngtree-this-is-a-picture-of-a-tiger-cub-that-looks-straight-picture-image_2660243.jpg"))
                        .caption("This is a little cat :)")
                        .build();
                try {
                    telegramClient.execute(msg); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/markup")) {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text("Here is your keyboard")
                        .build();

                // Add the keyboard to the message
                message.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        // Add first row of 3 buttons
                        .keyboardRow(new KeyboardRow("Row 1 Button 1", "Row 1 Button 2", "Row 1 Button 3"))
                        // Add second row of 3 buttons
                        .keyboardRow(new KeyboardRow("Row 2 Button 1", "Row 2 Button 2", "Row 2 Button 3"))
                        .build());

                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Row 1 Button 1")) {
                // Send a picture to the user
                SendPhoto msg = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        // This time will send the picture using a URL
                        .photo(new InputFile("https://www.kaspersky.com/content/en-global/images/repository/isc/2021/what_are_bots_image2_710x400px_300dpi.jpg"))
                        .caption("Clicked button: " + message_text)
                        .build();

                try {
                    telegramClient.execute(msg); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/hide")) {
                // Hide the keyboard
                SendMessage message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Keyboard hidden")
                        .replyMarkup(new ReplyKeyboardRemove())
                        .build();
                try {
                    telegramClient.execute(message); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text("Unknown command")
                        .build();
                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            // Message contains photo
            // Set variables
            long chat_id = update.getMessage().getChatId();

            List<PhotoSize> photos = update.getMessage().getPhoto();
            String f_id = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getFileId)
                    .orElse("");
            int f_width = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getWidth)
                    .orElse(0);
            int f_height = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getHeight)
                    .orElse(0);
            String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight: " + Integer.toString(f_height);
            SendPhoto msg = SendPhoto
                    .builder()
                    .chatId(chat_id)
                    .photo(new InputFile(f_id))
                    .caption(caption)
                    .build();
            try {
                telegramClient.execute(msg); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
