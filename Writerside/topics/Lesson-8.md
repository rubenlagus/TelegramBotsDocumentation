# Lesson 8. Integrating with Redis

Hi! 
Long time I haven't posted lessons. 
Sorry for that. 
Today we are integrating our ~~high-load~~ bot with a lighting fast database called [Redis](https://redis.io). 
I am using it for data that needs quick access.

## Library

For driver, I chose [Lettuce](https://lettuce.io) because of its popularity and good documentation. You can download it [here](https://lettuce.io/core/release/download/) or install with Maven:

<tabs group="dependency">
    <tab title="Maven" group-key="Maven">
        <code-block lang="xml">
            <![CDATA[
              <dependency>
                <groupId>io.lettuce</groupId>
                <artifactId>lettuce-core</artifactId>
                <version>%lettuce_version%</version>
              </dependency>
            ]]>
        </code-block>
    </tab>
    <tab title="Gradle" group-key="Gradle">
        <code-block lang="gradle">
            <![CDATA[
                implementation 'io.lettuce:lettuce-core:%lettuce_version%'
            ]]>
        </code-block>
    </tab>
    <tab title="Manual Jar" group-key="Manual">
       Download `.jar` from <a href="https://github.com/lettuce-io/lettuce-core/releases/tag/%lettuce_version%">here</a>
    </tab>
</tabs>


## Establish connection

Then, you need to connect to Redis:

```java
RedisClient redisClient;
StatefulRedisConnection<String, String> redisConnection;
RedisCommands<String, String> syncCommands;

redisClient = RedisClient.create("redis://localhost:6379/0"); // Format: redis://ip:post/dbNumber
redisConnection = redisClient.connect();
syncCommands = this.redisConnection.sync();
```

## Connection established

And that's all! Now you can execute commands like that:

```java
syncCommands.get("key");
syncCommands.set("key", "value");
syncCommands.lrange("key", 0, -1);
```

Also, don't forget to close connection with Redis when you have done your work:

```java
redisConnection.close();
redisClient.shutdown();
```

Quite a short lesson, but I think useful :D Thanks for your time, hope to see you soon!

