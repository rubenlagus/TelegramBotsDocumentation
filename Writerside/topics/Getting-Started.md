# Getting Started

So, you’d like to create your own Telegram bot with %project%? Then Let's get You started quickly.


## Receive Updates
First you need to acquire the corresponding library and add it to your project, in this tutorial (for the sake of simplicity), we are going to build a [Long Polling Bot](http://en.wikipedia.org/wiki/Push_technology#Long_polling):. There are several ways to do this:

<tabs group="dependency">
    <tab title="Maven" group-key="Maven">
        <code-block lang="xml">
            <![CDATA[
            <dependency>
              <groupId>org.telegram</groupId>
              <artifactId>telegrambots-longpolling</artifactId>
              <version>%version%</version>
            </dependency>
            ]]>
        </code-block>
    </tab>
    <tab title="Gradle" group-key="Gradle">
        <code-block lang="gradle">
            <![CDATA[implementation 'org.telegram:telegrambots-longpolling:%version%']]></code-block>
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

### Create your bot
Now that you have the library, you can start coding. There are few steps to follow:

1. **Creating your updates consumer:**
   The class must implement `LongPollingUpdateConsumer`, for simplicity, you can use `LongPollingSingleThreadUpdateConsumer` instead:

    ```java

    public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {
        @Override
        public void consume(Update update) {
            // TODO
        }
    }
    ```

    * `consume`: This method will be called when an [Update](https://core.telegram.org/bots/api#update) is received by your bot. In this example, this method will just read text messages and print the text received:

        ```java
 
        @Override
        public void consume(Update update) {
            // We check if the update has a message and the message has text
            if (update.hasMessage() && update.getMessage().hasText()) {
                System.out.println(update.getMessage().getText());
            }
        }
 
        ```

### Create the Telegram Application and register your bot

Now let's start our bot

<list type="decimal" start="1">
   <li>
      <b>Instantiate <code>TelegramBotsLongPollingApplication</code> and register our new bot:</b>
      <p>
      For this part, we need to actually perform 2 steps: <i>Instantiate Telegram Api</i> and <i>Register our Bot</i>. In this tutorial, we are going to do it in our <i>main</i> method:
      </p>
      <code-block lang="java">
            <![CDATA[
            public class Main {
                 public static void main(String[] args) {
                     // TODO Instantiate Telegram Bots API
                     // TODO Register our bot
                 }
             }
            ]]>
      </code-block>
   </li>
   <li>
      <b>Instantiate Telegram Bots Application:</b>
      <p>
      Easy as well, just create a new instance. Remember that a single instance can handle different bots but each bot can run only once (Telegram doesn't support concurrent calls to <code>GetUpdates</code>):
      </p>
      <code-block lang="java">
            <![CDATA[
            public class Main {
               public static void main(String[] args) {
                   // Instantiate Telegram Bots API
                   TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
                   // TODO Register our bot
               }
            }
            ]]>
      </code-block>
   </li>
   <li>
      <b>Register our bot:</b>
      <p>
      Now we need to register a new instance of our previously created bot class in the api:
      </p>
      <code-block lang="java">
            <![CDATA[
            public class Main {
               public static void main(String[] args) {
                   try {
                       String botToken = "12345:YOUR_TOKEN";
                       TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
                       botsApplication.registerBot(botToken, new MyAmazingBot());
                   } catch (TelegramApiException e) {
                       e.printStackTrace();
                   }
               }
            }
            ]]>
      </code-block>
   </li>
   <li>
      <b>Play with your bot:</b>
      <p>
      Done, now you just need to run this <code>main</code> method and your Bot should start working.
      </p>
   </li>
</list>

## Send Messages
Let's continue with our previously created bot, now we are going to do it echo the received text back.
First, you'll need to add a library to send messages, you can use the default implementation or create your own. There are several ways to do this:

<tabs group="dependency">
    <tab title="Maven Central" group-key="Maven">
        <code-block lang="xml">
            <![CDATA[
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
            <![CDATA[implementation 'org.telegram:telegrambots-client:%version%']]></code-block>
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

### Create an instance of the client

1. Create your `TelegramClient`, you can use the default implementation as `OkHttpTelegramClient`:

      ```java
         TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");
      ```

2. Let's add it to our previous bot

      ```java
         public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {
            private TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");
         
            @Override
            public void consume(Update update) {
               // We check if the update has a message and the message has text
               if (update.hasMessage() && update.getMessage().hasText()) {
                   System.out.println(update.getMessage().getText());
               }
            }
         }
      ```
### Execute SendMessage method to echo the text received
Let's complete our amazing Bot that we created earlier:

   ```java
      public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {
         private TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");
      
         @Override
         public void consume(Update update) {
            // We check if the update has a message and the message has text
            if (update.hasMessage() && update.getMessage().hasText()) {
                // Create your send message object
                SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(), update.getMessage().getText());
                try {
                   // Execute it
                   telegramClient.execute(method);
                } catch (TelegramApiException e) {
                   e.printStackTrace();
                }
            }
         }
      }
   ```
