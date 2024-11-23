# Simple Example

This section of the tutorial will present a bare-bone example on creating your first AbilityBot! It is highly recommended to write your very first bot via the [[Getting Started|Getting-Started]]. That will give you a sense of how the basic API allows you to handle commands and features.

## Dependencies
As with any Java project, you will need to set your dependencies.

<tabs group="dependency">
    <tab title="Maven" group-key="Maven">
        <code-block lang="xml">
            <![CDATA[
            <dependency>
              <groupId>org.telegram</groupId>
              <artifactId>telegrambots-abilities</artifactId>
              <version>%version%</version>
            </dependency>
            ]]>
        </code-block>
    </tab>
    <tab title="Gradle" group-key="Gradle">
        <code-block lang="gradle">
            <![CDATA[implementation 'org.telegram:telegrambots-abilities:%version%']]></code-block>
    </tab>
    <tab title="JitPack" group-key="JitPack">
        If you don´t like standard <b>Maven Central Repository</b>, see Jitpack steps <a href="https://jitpack.io/#rubenlagus/TelegramBots">here</a>
    </tab>  
    <tab title="Manual Jar" group-key="Manual">
        Import the library <b>.jar</b> directly to your project. You can find it <a href="https://github.com/rubenlagus/TelegramBots/releases">here</a>, don't forget to fetch the latest version, it is usually a good idea. 
        <p></p>
        Depending on the IDE you are using, the process to add a library is different, here is a video that may help with <a href="https://www.youtube.com/watch?v=NZaH4tjwMYg">Intellij</a>
    </tab>
</tabs>

## Bot Declaration
To use the abilities module, you will need to extend AbilityBot.
```java
import bot.org.telegram.telegrambots.abilitybots.api.AbilityBot;

public class HelloBot extends AbilityBot {
  ...
}
```

## Bot Implementation
Bot username and `TelegramClient` (used to send messages) are passed via the constructor and don't require an override.
```java
 public HelloBot(TelegramClient telegramClient, String botUsername) {
    super(telegramClient, botUsername);
  }
```

AbilityBot forces a single implementation of creator ID. This ID corresponds to you, the bot developer. The bot needs to know its master since it has sensitive commands that only the master can use.
So, if your Telegram ID Is 123456789, then add the following method:
```java
  @Override
  public int creatorId() {
    return 123456789;
  }
```

That's it to have a valid, compilable and ready to be deployed bot. However, your bot doesn't have a single command to use. Let's declare one!

## Hello Ability
To add a feature to your bot, you add an ability. That's it! No routing from onUpdateReceived, no separate checks and no crossovers. Let's write our first ability that simply says hello!

```java
public Ability sayHelloWorld() {
    return Ability
              .builder()
              .name("hello")
              .info("says hello world!")
              .locality(ALL)
              .privacy(PUBLIC)
              .action(ctx -> silent.send("Hello world!", ctx.chatId()))
              .build();
}
```

Save your questions for later! Abilities are described in detail in the following sections of the tutorial.

## Running Your Bot

Running the bot is just like running the regular Telegram bots. Create a Java class similar to the one below.
```java
public class Application {
    public static void main(String[] args) {
        try {
          String botToken = "12345:YOUR_TOKEN"; 
          // Instantiate Telegram Bots API
          TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
          // Create Bot
          HelloBot bot = new HelloBot();
          // Enable abilities
          bot.onRegister();
          // Register your newly created AbilityBot
          botsApplication.registerBot(botToken, bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
```

If you're in doubt that you're missing some code, the full code example can be inspected [here](https://github.com/addo37/ExampleBots/tree/master/src/main/java/org/telegram/examplebots).

## Testing Your Bot

Go ahead and "/hello" to your bot. It should respond back with "Hello World!".

Since you've implemented an AbilityBot, you get **factory abilities** as well. Try:
* /report - Prints all user-defined commands supported by the bot
    * This will essentially print "hello - says hello world!". Yes! This is the information we supplied to the ability. The bot prints the commands in the format accepted by BotFather. So, whenever you change, add or remove commands, you can simply /report and forward that message to BotFather.
* /commands - Prints all commands exposed by the bot (factory and user-defined, with and without info)
* /claim - Claims this bot
* /backup - returns a backup of the bot database
* /recover - recovers the database
* /promote @username - promotes user to bot admin
* /demote @username - demotes bot admin to user
* /ban @username - bans the user from accessing your bot commands and features
* /unban @username - lifts the ban from the user
* /stats - displays how many times were your abilities called
    * **This command returns empty String by default.** To use this command, add  ```.setStatsEnabled(true)``` to your abilities. You'll then be able to view how many times each of them was called.

## Conclusion
Congratulation on creating your first AbilityBot. What's next? So far we've only considered the case of commands, but what about images and inline replies? AbilityBots can also handle that! Oh and, did you know that all ability bots have an embedded database that you can use?
The following sections of the tutorial will describe in detail **abilities** and **replies**. It will also bring into attention how to effectively in-code test your bot, handle the embedded DB and administer your user access levels.
