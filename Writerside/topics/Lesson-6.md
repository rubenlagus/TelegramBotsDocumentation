# Lesson 6. Inline keyboards and editing message's text

I published a poll in our [Telegram chat](https://t.me/JavaBotsApi) about the next lesson. 
So, as you are reading this, Bots API 2.0 won.

![Our chat](chat_voting.png)

On April 9, 2016, 
Telegram released [Bot API 2.0](https://core.telegram.org/bots/api-changelog#april-9-2016) which allows you 
to edit message's text and send new Inline Keyboards. 
So, let's implement it to your bot and see how it's beautiful. 
Now as always open `IntelliJ IDEA`, within `src` folder create files `Main.java` and `BotApi20.java`. 
First look:

> `src/BotApi20.java`

```java
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

public class BotApi20 implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");
    
    @Override
    public void consume(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/start")) {
                // Set variables
                String message_text = update.getMessage().getText();
                long chat_id = update.getMessage().getChatId();

                SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();
                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                
            }

        }
    }
}
```

> `src/Main.java`

```java
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        String botToken = "12345:YOUR_TOKEN";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new BotApi20(botToken));
            System.out.println("BotApi20 successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

I recommend you always look in the [Bot API description](https://core.telegram.org/bots/api), 
so you know every method and type. 
OK, let's make bot answer to the `/start` command:

```java
public void consume(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (update.getMessage().getText().equals("/start")) {
                SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {

            }

        } else if (update.hasCallbackQuery()) {}
    }

```

And now let's add Inline Keyboard to this message:

```java
public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (update.getMessage().getText().equals("/start")) {
                SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                     // Set the keyboard markup
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                    new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Update message text")
                                            .callbackData("update_msg_text")
                                            .build()
                                    )
                            )
                            .build())
                    .build();
                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {}
    }
```

It looks like this now:

![Inline Keyboard](bot_inline_keyboard.png)

We want to edit message text right?
Let's do it when the user presses our button. 
Add a [Callback Query](https://core.telegram.org/bots/api#callbackquery) handler to your bot:

```java
else if (update.hasCallbackQuery()) {}
```

So if update has a `CallbackQuery`, it calls this `else if` operator. Moving forward:

```java
        else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if (call_data.equals("update_msg_text")) {
                String answer = "Updated message text";
                EditMessageText new_message = EditMessageText.builder()
                    .chatId(chat_id)
                    .messageId(toIntExact(message_id))
                    .text(answer)
                    .build();
                try {
                    telegramClient.execute(new_message); 
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
```

Now when the user presses our button, it will change its text:

![editMessageText](bot_updated_text.png)

Source:

> `src/Main.java`

```java
package org.telegram.telegrambots.tutorial.Lesson6.src;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        String botToken = "12345:YOUR_TOKEN";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new BotApi20(botToken));
            System.out.println("BotApi20 successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

> `src/BotApi20.java`

```java
package org.telegram.telegrambots.tutorial.Lesson6.src;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static java.lang.Math.toIntExact;

public class BotApi20 implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public BotApi20(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }
    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (update.getMessage().getText().equals("/start")) {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text(message_text)
                        // Set the keyboard markup
                        .replyMarkup(InlineKeyboardMarkup
                                .builder()
                                .keyboardRow(
                                        new InlineKeyboardRow(InlineKeyboardButton
                                                .builder()
                                                .text("Update message text")
                                                .callbackData("update_msg_text")
                                                .build()
                                        )
                                )
                                .build())
                        .build();
                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if (call_data.equals("update_msg_text")) {
                String answer = "Updated message text";
                EditMessageText new_message = EditMessageText.builder()
                        .chatId(chat_id)
                        .messageId(toIntExact(message_id))
                        .text(answer)
                        .build();
                try {
                    telegramClient.execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

You can also find all source code to all of my lessons at [GitHub](https://github.com/rubenlagus/TelegramBotsDocumentation/tree/main/java-telegram-bot-tutorial).

Thank you for reading this! 
Now you can send Inline Keyboards and edit message's text and extra: handle callback queries. 
I hope you liked this lesson. 
Next time I will show how to create a user database using MongoDB. 
Bye!
