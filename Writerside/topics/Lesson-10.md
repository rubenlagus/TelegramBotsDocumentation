# Lesson 10. Using SpringBoot and Proxy

## Prerequisites

### Complete session 9
For this session, we'll assume that you have already completed [lesson 9](Lesson-9.md) and your code is working and running.

### Set up your own proxy
The decision to choose one or another is up to you, as an example, we'll be working with these setups: [HTTP Proxy](https://www.digitalocean.com/community/tutorials/how-to-set-up-squid-proxy-on-ubuntu-20-04) and [SOCKS5 Proxy](https://www.digitalocean.com/community/tutorials/how-to-set-up-squid-proxy-on-ubuntu-20-04)

### Gather required data-points
Through the lesson, you'll need:

* Host name (or IP address)
* Port 
* Username
* Password

## Update your bot class

In this case, we'll need to provide our custom OkHttpClient implementation, hence, let's add some parameters to the constructor:

```Java
@Component
public class MyAmazingBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final String token;

    public MyAmazingBot(TelegramClient telegramClient, String token) {
        this.telegramClient = telegramClient;
        this.token = token;
    }
    
    // ...
}
```

And also let's update our `getBotToken` method:

```Java
@Component
public class MyAmazingBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final String token;

    public MyAmazingBot(
            @Qualifier("telegramClient") TelegramClient telegramClient,
            @Value("botToken") String token) {
        this.telegramClient = telegramClient;
        this.token = token;
    }
    
    @Override
    public String getBotToken() {
        return token;
    }
    
    // ...
}
```

## Now it's time for our custom configurations

Let's start creating our custom configuration class with the beans we'll need

```Java
@Configuration
public class MyBotConfiguration {
    @Bean(value = "okClient")
    public OkHttpClient okClientHttp(
            @Value("${hostname}") String hostname,
            @Value("${port}") int port,
            @Value("${username}") String username,
            @Value("${password}") String password
    ) {
        // This method will create our custom OkHttpClient
         return null;
    }

    @Bean(value = "telegramClient")
    public TelegramClient telegramClient(
            @Qualifier("okClient") OkHttpClient okClient,
            @Value("${botToken}") String botToken
    ) {
        // Here, we'll have our custom TelegramClient
        return null;
    }

    @Bean(value = "telegramBotsApplication")
    public TelegramBotsLongPollingApplication telegramBotsApplication(
            @Value("${okClient}") OkHttpClient okClient
    ) {
        // Here we'll create our TelegramBots application
        return null;
    }
}
```

### Let's go for our Bots Application

This step will be straight forward, let's just use one of the available constructors

```Java
@Configuration
public class MyBotConfiguration {
    // ...
    
    @Bean(value = "telegramBotsApplication")
    public TelegramBotsLongPollingApplication telegramBotsApplication(
            @Qualifier("okClient") OkHttpClient okClient
    ) {
        return new TelegramBotsLongPollingApplication(ObjectMapper::new, () -> okClient);
    }
}
```

### Then our TelegramClient

Again, we have a constructor for it

```Java
@Configuration
public class MyBotConfiguration {
    // ...
    
        @Bean
    public TelegramClient telegramClient(
            @Qualifier("okClient") OkHttpClient okClient,
            @Qualifier("botToken") String botToken
    ) {
        return new OkHttpTelegramClient(okClient, botToken);
    }
    
    // ...
}
```

### And finally, let's go ahead with our OkHttpClient 

Here we have two options, HTTP or SOCKS5, make sure you follow the right steps

#### HTTP

This is probably the simpler forward case since we have a convenience method available in our library

```Java
@Configuration
public class MyBotConfiguration {
    // ...
    
    @Bean(value = "okClient")
    public OkHttpClient okClientHttp(
            @Value("${hostname}") String hostname,
            @Value("${port}") int port,
            @Value("${username}") String username,
            @Value("${password}") String password
    ) {
         // Let's use TelegramOkHttpClientFactory.ProxyOkHttpClientCreator
         return new TelegramOkHttpClientFactory.ProxyOkHttpClientCreator(
                // Pass the proxy address and type
                () -> new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port)),
                // Provide the authenticator for it
                () -> (route, response) -> {
                    String credential = Credentials.basic(username, password);
                    return response
                            .request()
                            .newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
        ).get();
    }
    
    // ...
}
```

### SOCKS5

To use a SOCKS5 proxy, we'll need to do two things:

1. Override the default Java Authenticator
2. Tell OkHttp that we are using a proxy.

```Java
@Configuration
public class MyBotConfiguration {
    // ...
    
    @Bean(value = "okClient")
    public OkHttpClient okClientHttp(
            @Value("${hostname}") String hostname,
            @Value("${port}") int port,
            @Value("${username}") String username,
            @Value("${password}") String password
    ) {
        // TODO Override default authenticator
        // TODO Tell OkHttpClient to use a proxy
        return null
    }
    
    // ...
}
```

Let's do it.

#### Override default authenticator 



```Java
@Configuration
public class MyBotConfiguration {
    // ...
    
    @Bean(value = "okClient")
    public OkHttpClient okClientHttp(
            @Value("${hostname}") String hostname,
            @Value("${port}") int port,
            @Value("${username}") String username,
            @Value("${password}") String password
    ) {
        // Override default authenticator
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // For our host and port, return our auth credentials
                if (getRequestingHost().equalsIgnoreCase(hostname)) {
                    if (port == getRequestingPort()) {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                }
                return null;
            }
        });


        // TODO Tell OkHttpClient to use a proxy
        return null
    }
    
    // ...
}
```

#### Tell OkHttp to use a proxy

```Java
@Configuration
public class MyBotConfiguration {
    // ...
    
    @Bean(value = "okClient")
    public OkHttpClient okClientHttp(
            @Value("${hostname}") String hostname,
            @Value("${port}") int port,
            @Value("${username}") String username,
            @Value("${password}") String password
    ) {
        // Override default authenticator
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // For our host and port, return our auth credentials
                if (getRequestingHost().equalsIgnoreCase(hostname)) {
                    if (port == getRequestingPort()) {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                }
                return null;
            }
        });


        // Tell OkHttpClient to use a proxy (no need to provide any credentials here)
        return new TelegramOkHttpClientFactory.ProxyOkHttpClientCreator(
                () -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostname, port)), () -> null
        ).get();
    }
    
    // ...
}
```

## Ready to roll

Let's test our bot, you can use Maven spring plugin to get it running:

```Bash
mvn spring-boot:run
```

If you wanna build your own Jar file, you can use:

```Bash
mvn package spring-boot:repackage
```

## All our files

### Main.java

```Java
package org.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to start the Spring Boot application.
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

### MyBotConfiguration.java

```Java
package org.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

@Configuration
public class MyBotConfiguration {
    @Bean(value = "okClient") // Comment this line if you don't wanna use HTTP proxy
    public OkHttpClient okClientHttp(
            @Value("${hostname}") String hostname,
            @Value("${port}") int port,
            @Value("${username:squid}") String username,
            @Value("${password}") String password
    ) {
         return new TelegramOkHttpClientFactory.ProxyOkHttpClientCreator(
                () -> new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port)),
                () -> (route, response) -> {
                    String credential = Credentials.basic(username, password);
                    return response
                            .request()
                            .newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
        ).get();
    }


    // @Bean(value = "okClient") // Uncomment this line if you prefer to use SOCKS5
    public OkHttpClient okClientSocks(
            @Value("${hostname}") String hostname,
            @Value("${port}") int port,
            @Value("${username}") String username,
            @Value("${password}") String password
    ) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestingHost().equalsIgnoreCase(hostname)) {
                    if (port == getRequestingPort()) {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                }
                return null;
            }
        });

        return new TelegramOkHttpClientFactory.ProxyOkHttpClientCreator(
                () -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostname, port)), () -> null
        ).get();
    }

    @Bean
    public TelegramClient telegramClient(
            @Qualifier("okClient") OkHttpClient okClient,
            @Value("${botToken}") String botToken
    ) {
        return new OkHttpTelegramClient(okClient, botToken);
    }

    @Bean(value = "telegramBotsApplication")
    public TelegramBotsLongPollingApplication telegramBotsApplication(
            @Qualifier("okClient") OkHttpClient okClient
    ) {
        return new TelegramBotsLongPollingApplication(ObjectMapper::new, () -> okClient);
    }
}

```

### MyAmazingBot.java

```Java
package org.telegram.telegrambots.longpolling.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class TelegramBotInitializer implements InitializingBean {

    private final TelegramBotsLongPollingApplication telegramBotsApplication;
    private final List<SpringLongPollingBot> longPollingBots;

    public TelegramBotInitializer(TelegramBotsLongPollingApplication telegramBotsApplication,
                                  List<SpringLongPollingBot> longPollingBots) {
        Objects.requireNonNull(telegramBotsApplication);
        Objects.requireNonNull(longPollingBots);
        this.telegramBotsApplication = telegramBotsApplication;
        this.longPollingBots = longPollingBots;
    }

    @Override
    public void afterPropertiesSet()  {
        try {
            for (SpringLongPollingBot longPollingBot : longPollingBots) {
                BotSession session = telegramBotsApplication.registerBot(longPollingBot.getBotToken(), longPollingBot.getUpdatesConsumer());
                handleAfterRegistrationHook(longPollingBot, session);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private void handleAfterRegistrationHook(Object bot, BotSession botSession) {
        Stream.of(bot.getClass().getMethods())
                .filter(method -> method.getAnnotation(AfterBotRegistration.class) != null)
                .forEach(method -> handleAnnotatedMethod(bot, method, botSession));

    }

    private void handleAnnotatedMethod(Object bot, Method method, BotSession session) {
        try {
            if (method.getParameterCount() > 1) {
                log.warn(String.format("Method %s of Type %s has too many parameters",
                        method.getName(), method.getDeclaringClass().getCanonicalName()));
                return;
            }
            if (method.getParameterCount() == 0) {
                method.invoke(bot);
                return;
            }
            if (method.getParameterTypes()[0].equals(BotSession.class)) {
                method.invoke(bot, session);
                return;
            }
            log.warn(String.format("Method %s of Type %s has invalid parameter type",
                    method.getName(), method.getDeclaringClass().getCanonicalName()));
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error(String.format("Couldn't invoke Method %s of Type %s",
                    method.getName(), method.getDeclaringClass().getCanonicalName()));
        }
    }
}

```