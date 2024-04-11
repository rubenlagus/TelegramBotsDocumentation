# Lesson 9. Simple Spring Boot Bot

Hello! If you want to know how to code Telegram Bots on Java and Spring, this is it!

<tip>
    <p>
        For this example, we'll use long polling, but similar can be done for webhook methods.
    </p>
</tip>

## Install the library

You can install TelegramBots library with different methods:

<tabs group="dependency">
    <tab title="Maven" group-key="Maven">
        <code-block lang="xml">
            <![CDATA[
            <dependency>
              <groupId>org.telegram</groupId>
              <artifactId>telegrambots-springboot-longpolling-starter</artifactId>
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
                implementation 'org.telegram:telegrambots-springboot-longpolling-starter:%version%'
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

## Let's go to code!

<note>
    <p>
        We'll be using the default OkHttp client to perform request to Telegram Servers
    </p>
</note>

We'll start writing a simple echo bot that will just echo any text message received.

Now, when you are in the project, create files `MyAmazingBot.java` and `Main.java` within the `src` directory. Open `MyAmazingBot.java` and let's write our actual bot:

> Remember! The class must implement `LongPollingSingleThreadUpdateConsumer` and `SpringLongPollingBot` and implement the necessary methods.

```java
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyAmazingBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
   private final TelegramClient telegramClient;

    public MyAmazingBot() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return "TOKEN";
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        // TODO
    }
}
```

Now, let's add the logic to process updates, `consume(Update update)` method is for us.
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

And last step here, 
we need to tell Spring Boot to create an instance on our bot, we can do it just adding the `@Component` annotation to it

```java
@Component
public class MyAmazingBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    // ...
}
```

Our bot is now created! 
Let's save that file and go to `Main.java`.
This file will be our Spring Boot application and will do all the magic for us.

We only need to add the `@SpringBootApplication` and tell Spring to start.

```java
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

Here are all our files:

> `src/Main.java`

```java
package org.telegram.telegrambots.tutorial.Lesson8.src;

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

> `src/MyAmazingBot.java`

```java
package org.telegram.telegrambots.tutorial.Lesson8.src;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class MyAmazingBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public MyAmazingBot() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return "TOKEN";
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
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

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}
```

> `pom.xml`

This file is provided only as a sample, feel free to use your own version of it.

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>SpringTestBot</artifactId>
    <name>SpringTestBot</name>
    <groupId>org.example</groupId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.release>17</maven.compiler.release>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.target>${java.version}</maven.compiler.target>

        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>%spring_version%</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>

        <dependency>
            <groupId>org.telegram</groupId>
            <artifactId>telegrambots-springboot-longpolling-starter</artifactId>
            <version>%version%</version>
        </dependency>
        <dependency>
            <groupId>org.telegram</groupId>
            <artifactId>telegrambots-client</artifactId>
            <version>%version%</version>
        </dependency>
    </dependencies>

    <build>
        <directory>${project.basedir}/target</directory>
        <outputDirectory>${project.build.directory}/classes</outputDirectory>
        <finalName>${project.artifactId}-${project.version}</finalName>
        <testOutputDirectory>${project.build.directory}/test-classes</testOutputDirectory>
        <sourceDirectory>${project.basedir}/src/main/java</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>%spring_version%</version>
                <configuration>
                    <mainClass>org.telegram.Main</mainClass>
                    <layout>JAR</layout>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>

```

Well done! Now we can pack our project into runnable `.jar` file and run it on our computer/server!

```Bash
mvn package spring-boot:repackage
```

You can find all sources to this lesson in [GitHub repository](https://github.com/rubenlagus/TelegramBotsDocumentation/tree/main/java-telegram-bot-tutorial).

Now we can see our bot running:

<img src="spring_boot.png" alt="Bot Spring" />

Well, that's all for now. Hope to see you soon! :)

