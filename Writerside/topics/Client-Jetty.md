# Jetty HTTP client
By default, TelegramBotsApi performs HTTP requests using OkHttp library.
If for some reason you don't want or can't use it, there is another implementation of `AbstractTelegramClient` that perform HTTP requests using Jetty HttpClient.
To switch from OkHttp to Jetty Client you have to use `telegrambots-client-jetty-adapter` instead of `telegrambots-client`:

```xml
<dependency>
    <groupId>org.telegram</groupId>
    <artifactId>telegrambots-client-jetty-adapter</artifactId>
</dependency>
```

Now you can create an instance of `JettyTelegramClient` with one of constructors provided:
```java
JettyTelegramClient(ObjectMapper objectMapper, HttpClient client, String botToken, TelegramUrl telegramUrl);
JettyTelegramClient(HttpClient client, String botToken, TelegramUrl telegramUrl);
JettyTelegramClient(HttpClient client, String botToken);
JettyTelegramClient(String botToken, TelegramUrl telegramUrl);
JettyTelegramClient(String botToken);
```

Simple usage:
```java
// create telegram client instance with default HttpClient configuration
TelegramClient telegramClient = new JettyTelegramClient("TOKEN");
```

Advanced usage:

```java
// create fine-tuned Jetty HttpClient
QueuedThreadPool threadPool = new QueuedThreadPool(4);
threadPool.setName("jetty-client-qtp");
threadPool.setVirtualThreadsExecutor(VirtualThreads.getNamedVirtualThreadsExecutor("jcvt-"));

HttpClient httpClient = new HttpClient(new HttpClientTransportOverHTTP(1));
httpClient.setMaxConnectionsPerDestination(5);
httpClient.setMaxRequestsQueuedPerDestination(100);
httpClient.setConnectTimeout(5000);
httpClient.setExecutor(threadPool);

// create telegram client instance with custom HttpClient
TelegramClient telegramClient = new JettyTelegramClient(httpClient, "TOKEN");
```
