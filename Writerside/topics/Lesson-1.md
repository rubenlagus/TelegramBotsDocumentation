# Lesson 1. Simple echo bot

Hello! If you want to know how to code Telegram Bots on Java, you are on the right way!

## Prepare to launch

Bot API is based on HTTP-requests, but in this book I will use [Rubenlagus' library for Java](%project_url%).

### Install the library

You can install TelegramBots library with different methods:

<tabs group="dependency">
    <tab title="Maven" group-key="Maven">
        <code-block lang="xml">
            <![CDATA[
            <dependency>
              <groupId>org.telegram</groupId>
              <artifactId>telegrambots-longpolling</artifactId>
              <version>%version%</version>
            </dependency>
            <dependency>
              <groupId>org.telegram</groupId>
              <artifactId>telegrambots-client</artifactId>
              <version>%version%</version>
            </dependency>
            ]]>
        </code-block>
    </tab>
    <tab title="Gradle" group-key="Gradle">
        <code-block lang="gradle">
            <![CDATA[
                implementation 'org.telegram:telegrambots-longpolling:%version%'
                implementation 'org.telegram:telegrambots-client:%version%'
            ]]>
        </code-block>
    </tab>
    <tab title="JitPack" group-key="JitPack">
        If you don't like standard <b>Maven Central Repository</b>,
        see Jitpack steps <a href="https://jitpack.io/#rubenlagus/TelegramBots">here</a>
    </tab>  
    <tab title="Manual Jar" group-key="Manual">
        Import the library <b>.jar</b> directly to your project. You can find it <a href="https://github.com/rubenlagus/TelegramBots/releases">here</a>, don't forget to fetch the latest version, it is usually a good idea. 
        <p></p>
        Depending on the IDE you are using, the process to add a library is different, here is a video that may help with <a href="https://www.youtube.com/watch?v=NZaH4tjwMYg">Intellij</a>
    </tab>
</tabs>

In this tutorial, I will use next machines:

* Ubuntu 16.04 Server with 1GB of RAM
* My home Windows 10 laptop with IntelliJ IDEA pre-installed

## Let's go to code!

<note>
    <p>
        We'll be using the default OkHttp client to perform request to Telegram Servers
    </p>
</note>

Well, enough for words. 
Let's get down to business. 
In this lesson, we will write a simple bot that echoes everything we sent to him. 
Now, open `IntelliJ IDEA` and create a new project.
You can call it whatever you want.
Then, don't forget to install `TelegramBots` library with preferred method. 
I think that, it is most easy to just download `.jar`
from [here](https://github.com/rubenlagus/TelegramBots/releases/latest)

Now, when you are in the project, create files `MyAmazingBot.java` and `Main.java` within the `src` directory. Open `MyAmazingBot.java` and let's write our actual bot:

> Remember! The class must implement `LongPollingSingleThreadUpdateConsumer` and implement the necessary methods.

```java
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");

    @Override
    public void consume(Update update) {
        // TODO
    }
}
```

Now, let's move on to the logic of our bot.
As I said before, we want him to reply to every text we send to him. 
`consume(Update update)` method is for us.
When an update is received, it will call this method.

```java
@Override
public void consume(Update update) {
    // We check if the update has a message and the message has text
    if (update.hasMessage() && update.getMessage().hasText()) {
        // Set variables
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();

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
    }
}
```

Good! 
But how do I run the bot? 
Well, it's a good question. 
Let's save that file and open `Main.java`. 
This file will instantiate TelegramBotsApi and register our new bot. 
It will look like this:

```java
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        // TODO Instantiate Telegram API
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();

        // TODO Register our bot
    }
}
```

And register our bot:

```java
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.exceptions.TelegramApiException;
public class Main {
    public static void main(String[] args) {
        // Instantiate Telegram API
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
       
        // Register our bot
        String botToken = "12345:YOUR_TOKEN";
        botsApplication.registerBot(botToken, new MyAmazingBot(botToken));
       
        System.out.println("MyAmazingBot successfully started!");
    }
}
```

Let's make sure that our app closes all the resources correctly before shutting down:

```java
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.exceptions.TelegramApiException;
public class Main {
    public static void main(String[] args) {
        String botToken = "12345:YOUR_TOKEN";
        // Using try-with-resources to allow autoclose to run upon finishing 
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new MyAmazingBot(botToken));
            System.out.println("MyAmazingBot successfully started!");
            // Ensure this prcess wait forever 
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
```

Here are all our files:

> `src/Main.java`

```java
package org.telegram.telegrambots.tutorial.Lesson1.src;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;


public class Main {
    public static void main(String[] args) {
        String botToken = "12345:YOUR_TOKEN";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new MyAmazingBot(botToken));
            System.out.println("MyAmazingBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

> `src/MyAmazingBot.java`

```java
package org.telegram.telegrambots.tutorial.Lesson1.src;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public MyAmazingBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

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
        }
    }
}
```

Well done! Now we can pack our project into runnable `.jar` file and run it on our computer/server!

You can find all sources to this lesson in [GitHub repository](https://github.com/rubenlagus/TelegramBotsDocumentation/tree/main/java-telegram-bot-tutorial).

```text
java -jar MyAmazingBot.jar
```

Now we can see our bot running:

<img src="bot_reply.jpg" alt="Bot Reply" />

Well, that's all for now. Hope to see you soon! :)

