
# Lesson 4. Emoji

Welcome back! 
Now, you know how to log messages from users. 
But how to make bot messages more user-friendly and beautiful? 
The answer is - [emoji](https://en.wikipedia.org/wiki/Emoji). 
I think you know what is emoji, so let's move forward.

Now, open `IntelliJ IDEA` and create a new project. 
Create files `Main.java` and `EmojiTestBot.java` within the `src` directory. 
Here is the first look of our files:

> `src/Main.java`

```java
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        String botToken = "12345:YOUR_TOKEN";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new EmojiTestBot(botToken));
            System.out.println("EmojiTestBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

> `src/EmojiTestBot.java`

```java
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmojiTestBot implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");

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
            String answer = message_text;
            SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text(answer)
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

OK. Now let's install [emoji library](https://github.com/vdurmont/emoji-java):

<tabs group="dependency">
    <tab title="Maven" group-key="Maven">
        <code-block lang="xml">
            <![CDATA[
              <dependency>
                <groupId>com.vdurmont</groupId>
                <artifactId>emoji-java</artifactId>
                <version>%emoji_version%</version>
              </dependency>
            ]]>
        </code-block>
    </tab>
    <tab title="Gradle" group-key="Gradle">
        <code-block lang="gradle">
            <![CDATA[
                compile 'com.vdurmont:emoji-java:%emoji_version%'
            ]]>
        </code-block>
    </tab>
    <tab title="Manual Jar" group-key="Manual">
       Download `.jar` from <a href="https://github.com/vdurmont/emoji-java/releases/download/v%emoji_version%/emoji-java-%emoji_version%.jar">here</a>
    </tab>
</tabs>

Once the library is installed, import it to your bot class:

```java
import com.vdurmont.emoji.EmojiParser;
```

Now you can view a list of emojis at [EmojiPedia](http://emojipedia.org/) or [Emoji Cheat Sheet](http://webpagefx.com/tools/emoji-cheat-sheet/).
To insert emoji, do this:

```java
String answer = EmojiParser.parseToUnicode("Here is a smile emoji: :smile:\n\n Here is alien emoji: :alien:");
```

Where `:smile:` or `:alien:` is emoji alias or emoji short code. You can also view them at EmojiPedia or Emoji Cheat Sheet.

Here is source code. You can also find it on [GitHub](https://github.com/rubenlagus/TelegramBotsDocumentation/tree/main/java-telegram-bot-tutorial).

> `src/Main.java`

```java
package org.telegram.telegrambots.tutorial.Lesson4.src;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        String botToken = "12345:YOUR_TOKEN";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new EmojiTestBot(botToken));
            System.out.println("EmojiTestBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

> `src/EmojiTestBot.java`

```java
package org.telegram.telegrambots.tutorial.Lesson4.src;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class EmojiTestBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public EmojiTestBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            long chat_id = update.getMessage().getChatId();
            String answer = EmojiParser.parseToUnicode("Here is a smile emoji: :smile:\n\n Here is alien emoji: :alien:");
            SendMessage message = SendMessage // Create a message object
                    .builder()
                    .chatId(chat_id)
                    .text(answer)
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

Now you can see our beautiful messages:

![Bot sends messages with emoji](bot_emoji.png)

Our lesson came to an end. Thank you for reading this. See you soon!

