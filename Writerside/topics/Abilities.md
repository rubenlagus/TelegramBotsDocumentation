# Abilities

<p align="center">
<img src="api_bot_03.png" alt="abilities" />
</p>

The AbilityBot abstraction defines a new object, named Ability. An ability combines conditions, flags, action, post-action and replies.

## Usage

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

## Motivation

Ever since I've started programming bots for Telegram, I've been using the Telegram Bot Java API. It's a basic and nicely done API that is a 1-to-1 translation of the HTTP API exposed by Telegram.

Dealing with a basic API has its advantages and disadvantages. Obviously, there's nothing hidden. If it's there on Telegram, it's here in the Java API.
When you want to implement a feature in your bot, you start asking these questions:

* The **WHO**?
    * Who is going to use this feature? Should they be allowed to use all the features?
* The **WHAT**?
    * Under what conditions should I allow this feature?
    * Should the message have a photo? A document? Oh, maybe a callback query?
* The **HOW**?
    * If my bot crashes, how can I resume my operation?
    * Should I utilize a DB?
    * How can I separate logic execution of different features?
    * How can I unit-test my feature outside of Telegram?

Every time you write a command or a feature, you will need to answer these questions and ensure that your feature logic works.

## Ability Bot Abstraction

After implementing my fifth bot using that API, I had had it with the amount of **boilerplate code** that was needed for every added feature. Methods were getting overly-complex and readability became subpar.
That is where the notion of another layer of abstraction (AbilityBot) began taking shape.

The AbilityBot abstraction defines a new object, named **Ability**. An ability combines conditions, flags, action, post-action and replies.
As an example, here is a code-snippet of an ability that creates a ***/hello*** command:

```java
public Ability sayHelloWorld() {
    return Ability
              .builder()
              .name("hello")
              .info("says hello world!")
              .input(0)
              .locality(USER)
              .privacy(ADMIN)
              .action(ctx -> sender.send("Hello world!", ctx.chatId()))
              .post(ctx -> sender.send("Bye world!", ctx.chatId()))
              .build();
}
```
Here is a breakdown of the above code snippet:
* *.name()* - the name of the ability (essentially, this is the command)
* *.info()* - provides information for the command
    * More on this later, but it basically centralizes command information in-code.
* *.input()* - the number of input arguments needed, 0 is for do-not-care
* *.locality()* - this answers where you want the ability to be available
    * In GROUP, USER private chats or ALL (both)
* *.privacy()* - this answers who you want to access your ability
    * CREATOR, ADMIN, or everyone as PUBLIC
* *.action()* - the feature logic resides here (a lambda function that takes a *MessageContext*)
    * *MessageContext* provides fast accessors for the **chatId**, **user** and the underlying **update**. It also conforms to the specifications of the basic API.
* *.post()* - the logic executed **after** your main action finishes execution

The following is a snippet of how this would look like with the plain basic API.

```java
   @Override
   public void onUpdateReceived(Update update) {
       // Global checks...
       // Switch, if, logic to route to hello world method
       // Execute method
   }

   public void sayHelloWorld(Update update) {
       if (!update.hasMessage() || !update.getMessage().isUserMessage() || !update.getMessage().hasText() || update.getMessage.getText().isEmpty())
           return;
       User maybeAdmin = update.getMessage().getFrom();
       /* Query DB for if the user is an admin, can be SQL, Reddis, Ignite, etc...
          If user is not an admin, then return here.
       */

       SendMessage snd = new SendMessage();
       snd.setChatId(update.getMessage().getChatId());
       snd.setText("Hello world!");

       try {
           sendMessage(snd);
       } catch (TelegramApiException e) {
           BotLogger.error("Could not send message", TAG, e);
       }
   }
```

I will leave you the choice to decide between the two snippets as to which is more **readable**, **writable** and **testable**.

***You can do so much more with abilities, besides plain commands. Head over to our [examples](#examples) to check out all of its features!***

## Objective

The AbilityBot abstraction intends to provide the following:
* New feature is a new **Ability**, a new method - no fuss, zero overhead, no cross-code with other features
* Argument length on a command is as easy as changing a single integer
* Privacy settings per Ability - access levels to Abilities! User | Admin | Creator
* Embedded database - available for every declared ability
* Proxy sender interface - enhances testability; accurate results pre-release

Alongside these exciting core features of the AbilityBot, the following have been introduced:
* The bot automatically maintains an up-to-date set of all the users who have contacted the bot
    * up-to-date: if a user changes their Username, First Name or Last Name, the bot updates the respective field in the embedded-DB
* Backup and recovery for the DB
    * Default implementation relies on JSON/Jackson
* Ban and unban users from accessing your bots
    * The bot will execute the shortest path to discard the update the next time they try to spam
* Promote and demote users as bot administrators
    * Allows admins to execute admin abilities

# Examples
-------------------
* [Example Bots](https://github.com/addo37/ExampleBots)

Do you have a project that uses **AbilityBots**? Let us know!