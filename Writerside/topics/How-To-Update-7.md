# How To Update 7

<warning>
    This is a work in progress, please report to our <a href="%tg_url%">Telegram Group</a> if you find anything important missing.
</warning>

## Library dependencies changes

The library has been divided in small pieces, so you only take what you need:

### Before

<code-block lang="xml">
    &lt;dependency&gt;
        &lt;groupId&gt;org.telegram&lt;/groupId&gt;
        &lt;artifactId&gt;telegrambots&lt;/artifactId&gt;
        &lt;version&gt;6.9.7.1&lt;/version&gt;
    &lt;/dependency&gt;
</code-block>

### After

<code-block lang="xml">
    &lt;!-- Long polling bot --&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.telegram&lt;/groupId&gt;
        &lt;artifactId&gt;telegrambots-longpolling&lt;/artifactId&gt;
        &lt;version&gt;7.0.0&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;!-- Webhook bot --&gt; 
    &lt;dependency&gt;
        &lt;groupId&gt;org.telegram&lt;/groupId&gt;
        &lt;artifactId&gt;telegrambots-webhook&lt;/artifactId&gt;
        &lt;version&gt;7.0.0&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;!-- OkHttp client implementation --&gt; 
    &lt;dependency&gt;
        &lt;groupId&gt;org.telegram&lt;/groupId&gt;
        &lt;artifactId&gt;telegrambots-client&lt;/artifactId&gt;
        &lt;version&gt;7.0.0&lt;/version&gt;
    &lt;/dependency&gt;
</code-block>

## Migrating your existing LongPolling bots

`TelegramLongPollingBot` has been removed and `LongPollingUpdateConsumer` is its replacement.

<tabs group="long-polling-7">
    <tab title="I consume updates one by one" group-key="one-update-7">
        <procedure title="Migrate Long Polling Bot" id="long-polling-one-update-7">
            <p>For convenience, a default extension of <code>LongPollingUpdateConsumer</code> is added as <code>LongPollingSingleThreadUpdateConsumer</code>.</p>
            <tip>
            Feel free to use your custom implementation of <code>LongPollingUpdateConsumer</code> if you prefer.
            </tip>
            <step>Instead of extend <code>TelegramLongPollingBot</code>, implement <code>LongPollingSingleThreadUpdateConsumer</code>.</step>
            <step>Instead of overriding <code>void onUpdateReceived(Update update)</code>, override <code>void consume(Update update)</code>.</step>
            <step>Remove any call to super constructor.</step>
            <step>Remove any other overrides from previous versions.</step>
        </procedure>
    </tab>
    <tab title="I consume the array of updates" group-key="multi-update-7">
        <procedure title="Migrate Long Polling Bot" id="long-polling-multi-update-7">
            <step>Instead of extend <code>TelegramLongPollingBot</code>, implement <code>LongPollingUpdateConsumer</code>.</step>
            <step>Instead of overriding <code>void onUpdatesReceived(List&lt;Update&gt; updates)</code>, override <code>void consume(List&lt;Update&gt; updates)</code>.</step>
            <step>Remove any call to super constructor.</step> 
            <step>Remove any other overrides from previous versions.</step>
        </procedure>
    </tab>
</tabs>

## Migrating your existing Webhook bots

`TelegramWebhookBot` has become an interface instead of an abstract class.

<procedure title="Migrate Webhook Bot" id="webhook-update-7">
    <step>Instead of extend <code>TelegramWebhookBot</code>, implement it.</step>
    <step>Instead of overriding <code>BotApiMethod onWebhookUpdateReceived(Update update)</code>, override <code>BotApiMethod&lt;?&gt; consumeUpdate(Update update)</code>.</step>
    <step>Implement your own version of <code>void runDeleteWebhook()</code> and <code>void runSetWebhook()</code>.</step> 
    <step>In <code>getBotPath()</code>, make sure your path starts with <code>/</code>.</step>
</procedure>

## Migrate your TelegramBotsApi

### Long Polling

For long polling bots, you can start the bot application and start registering your bots:

```Java
    TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
    botsApplication.registerBot("TOKEN", new AmazingBot());
```

You have available multiple constructors and implementations of `registerBot` to allow further customisation.

### Webhook

For webhook bots, you can start the bot application and start registering your bots:

```Java
    TelegramBotsWebhookApplication webhookApplication = new TelegramBotsWebhookApplication()
    botsApplication.registerBot(new AmazingBot());
```

You have available multiple constructors and implementations of `registerBot` to allow further customisation.

## Sending API requests

A new class `TelegramClient` will allow you to perform Telegram API requests independent on the updates consumption.

For convenience, an implementation using [okHttp](https://square.github.io/okhttp/) is provided as `OkHttpTelegramClient`.

```Java
    TelegramClient telegramClient = new OkHttpTelegramClient("TOKEN");
    telegramClient.execute(new SendMessage("chatId", "text));
```

<tip>
    Async option is available as <code>executeAsync</code> and returning <code>CompletableFuture&lt;?&gt;</code>.
</tip>


## Classes that have moved packages

1`BotApiMethod` and `PartialBotApiMethod` have been moved to package `org.telegram.telegrambots.meta.api.methods.botapimethods`
2`Message` and `InaccessibleMessage` classes have been moved to `org.telegram.telegrambots.meta.api.objects.message.Message`

## New way to provide Telegram URL

You can use class `TelegramUrl` to provide your custom URL for your Telegram Bots API.

For convenience, use `TelegramUrl.DEFAULT_URL` to use the default Telegram-hosted bots API.

You can build your own like:

```Java
    TelegramUrl
        .builder()
        .schema("https")
        .host("api.telegram.org")
        .port(443)
        .build();
```

## Mandatory parameters must be always included when creating new objects

<note>
    <p>
        This applies to many classes, everything that has mandatory parameters.
    </p>
</note>


All methods with mandatory parameters have lost their no-arguments constructor and enforce to provide them in the constructor.

To show with an example, let's use `SendMessage` method that requires `chatId` and `text` as mandatory:

<compare>
    <code-block lang="java">
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId();
        sendMessage.setText("Text");
    </code-block>
    <code-block lang="java">
        SendMessage sendMessage = new SendMessage("chatId", "text);
    </code-block>
</compare>

Alternatively, you can use the provided builders for them:

```Java
    SendMessage sendMessage = SendMessage
        .builder()
        .chatId("chatId")
        .text("text)
        .build();
```