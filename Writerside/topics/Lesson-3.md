# Lesson 3. Logging

Good afternoon everyone!
Did you look into the console? 
Kinda empty ya? 
Now, we want to see something, isn't it? 
Let's make a logging function!

## Creating project

As always, open `IntelliJ IDEA` and create a new project. 
Within the `src` folder create two files: `Main.java` and `LoggingTestBot.java`. 
Let's create a `body` of our bot:

> src/LoggingTestBot.java

```java
public class LoggingTestBot implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
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
        }
    }
}
```

And our startup file:

> src/Main.java

```java
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        String botToken = "12345:YOUR_TOKEN";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new LoggingTestBot(botToken));
            System.out.println("LoggingTestBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Logs, where are you?

Lets set additional variables for logging:

```java
public void consume(Update update) {
    // We check if the update has a message and the message has text
    if (update.hasMessage() && update.getMessage().hasText()) {
        // Set variables
        String user_first_name = update.getMessage().getChat().getFirstName();
        String user_last_name = update.getMessage().getChat().getLastName();
        String user_username = update.getMessage().getChat().getUserName();
        long user_id = update.getMessage().getChat().getId();
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
    }
}
```

Create `logging` function:

> Don't forget to import:
>
> ```java
> import java.text.DateFormat;
> import java.text.SimpleDateFormat;
> import java.util.Date;
> ```

Add new `private` function:

```java
private void log(String first_name, String last_name, String user_id, String txt, String bot_answer) {
        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + first_name + " " + last_name + ". (id = " + user_id + ") \n Text - " + txt);
        System.out.println("Bot answer: \n Text - " + bot_answer);
    }
```

Now we just need to call this function when we want to log

```java
public void onUpdateReceived(Update update) {
    // We check if the update has a message and the message has text
    if (update.hasMessage() && update.getMessage().hasText()) {
        // Set variables
        String user_first_name = update.getMessage().getChat().getFirstName();
        String user_last_name = update.getMessage().getChat().getLastName();
        String user_username = update.getMessage().getChat().getUserName();
        long user_id = update.getMessage().getChat().getId();
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        String answer = message_text;
        SendMessage message = SendMessage // Create a message object object
                .builder()
                .chatId(chat_id)
                .text(message_text)
                .build();
        log(user_first_name, user_last_name, Long.toString(user_id), message_text, answer);
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
```

Our files:

> src/Main.java

```java
package org.telegram.telegrambots.tutorial.Lesson3.src;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        String botToken = "12345:YOUR_TOKEN";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new LoggingTestBot(botToken));
            System.out.println("LoggingTestBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

> src/LoggingTestBot.java

```java
package org.telegram.telegrambots.tutorial.Lesson3.src;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingTestBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public LoggingTestBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String user_first_name = update.getMessage().getChat().getFirstName();
            String user_last_name = update.getMessage().getChat().getLastName();
            long user_id = update.getMessage().getChat().getId();
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();
            log(user_first_name, user_last_name, Long.toString(user_id), message_text, message_text);
            try {
                telegramClient.execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String first_name, String last_name, String user_id, String txt, String bot_answer) {
        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + first_name + " " + last_name + ". (id = " + user_id + ") \n Text - " + txt);
        System.out.println("Bot answer: \n Text - " + bot_answer);
    }
}
```

You can also find all sources at [GitHub repository](https://github.com/rubenlagus/TelegramBotsDocumentation/tree/main/java-telegram-bot-tutorial).

Now it will do ~~ugly~~ log for us.

![Beautiful Logging 1](bot_logging_1.png)

![Beautiful Logging 2](bot_logging_2.png)

Well, that's all for now. 
In the next lesson, 
we will learn how to make your messages more beautiful with [unicode emojis](https://en.wikipedia.org/wiki/Emoji).

