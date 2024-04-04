# Lesson 2. Photo Bot

Our mission today is to create a "photo" bot, that will send user a photo.
It is just an example, so there will be no photos from online, no group chat support.
Just local pics.
But there is a good thing: 
we will learn how to create [custom keyboards](https://core.telegram.org/bots/api#replykeyboardmarkup), 
how to send [photos](https://core.telegram.org/bots/api#photosize) and create commands.

## Let's respect Telegram's servers

OK, for a start, let's prepare our pictures.
Let's download 5 ~~completely unknown~~ photos. 
Just look: we will send same files to users many times, so let's coast our traffic and disk space on Telegram Servers. 
It is amazing that we can upload our files to their server once and then just send files 
\([photos, audio files, documents, voice messages, etc.](https://core.telegram.org/bots/api#available-types)\) 
by their unique `file_id`. 
OK, then.
Now let's know photo's `file_id` when we will send it to our bot. 
As always, create a new project in `IntelliJ IDEA` 
and create two files within the `src` directory: `Main.java` and `PhotoBot.java`. 
Open our first file and type next:

> Don't forget to install [TelegramBots library](https://github.com/rubenlagus/TelegramBots)

```java
package org.telegram.telegrambots.tutorial.Lesson2.src;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new PhotoBot(botToken));
            System.out.println("PhotoBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

This code will register our bot print "PhotoBot successfully started!" when it is successfully started. 
Then, save it and open up `PhotoBot.java`. 
Paste the following code from our previous lesson:

> Don't forget to change `bot username` and `bot token` if you have created another bot.

```java
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

public class PhotoBot implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");
    
    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            SendMessage message = SendMessage // Create a message object object
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

Now let's update our `consume` method. 
We want to send `file_id` of the picture we send to bot. 
Let's check if the message contains a photo object:

```java
@Override
public void consume(Update update) {

    // We check if the update has a message and the message has text
    if (update.hasMessage() && update.getMessage().hasText()) {
        // Set variables
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
        // Message contains photo
    }
}
```

We want our bot to send `file_id` of the photo. Well, let's do this:

```java
else if (update.hasMessage() && update.getMessage().hasPhoto()) {
    // Message contains photo
    // Set variables
    long chat_id = update.getMessage().getChatId();

    // Array with photo objects with different sizes
    // We will get the biggest photo from that array
    List<PhotoSize> photos = update.getMessage().getPhoto();
    // Know file_id
    String f_id = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
            .map(PhotoSize::getFileId)
            .orElse("");
    // Know photo width
    int f_width = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
            .map(PhotoSize::getWidth)
            .orElse(0);
    // Know photo height
    int f_height = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
            .map(PhotoSize::getHeight)
            .orElse(0);
    // Set photo caption
    String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight: " + Integer.toString(f_height);
    SendPhoto msg = SendPhoto
                .builder()
                .chatId(chat_id)
                .photo(new InputFile(f_id))
                .caption(caption)
                .build();
    try {
        telegramClient.execute(msg); // Call method to send the photo with caption
    } catch (TelegramApiException e) {
        e.printStackTrace();
    }
}
```

Let's take a look:

![Bot sends file\_id](bot_sends_photo.png)

Amazing! Now we know photo's file\_id, so we can send them by file\_id. Let's make our bot answer with that photo when we send command `/pic`.

```java
if (update.hasMessage() && update.getMessage().hasText()) {
    // Set variables
    String message_text = update.getMessage().getText();
    long chat_id = update.getMessage().getChatId();
    if (message_text.equals("/start")) {
        // User send /start
        SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    } else if (message_text.equals("/pic")) {
        // User sent /pic
        SendPhoto msg = SendPhoto
                .builder()
                .chatId(chat_id)
                .photo(new InputFile("https://png.pngtree.com/background/20230519/original/pngtree-this-is-a-picture-of-a-tiger-cub-that-looks-straight-picture-image_2660243.jpg"))
                .caption("This is a little cat :)")
                .build();
        try {
            telegramClient.execute(msg); // Call method to send the photo
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    } else {
        // Unknown command
        SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text("Unknown command")
                    .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
```

Now bot sends a photo like this:

![Bot sends a_photo by command /pic](bot_sends_photo_command.png)

And he can even reply to unknown command!

![Bot answers to unknown command](bot_unknown_command.png)

Now let's take a look at [ReplyKeyboardMarkup](https://core.telegram.org/bots/api#replykeyboardmarkup). 
We will now create a custom keyboard like this:

![Custom keyboards preview](custom_keyboard_preview.png)

Well, you already know how to make our bot recognise command. Let's make another `if` for command `/markup`.

> Remember! When you press the button, it will send to bot the text on this button. For example, if I put "Hello" text on the button, when I press it, it will send "Hello" text for me

```java
else if (message_text.equals("/markup")) {
    SendMessage message = SendMessage // Create a message object object
                    .builder()
                    .chatId(chat_id)
                    .text("Here is your keyboard")
                    .build();
    
    // Add the keyboard to the message
    message.setReplyMarkup(ReplyKeyboardMarkup
                    .builder()
                    // Add first row of 3 buttons
                    .keyboardRow(new KeyboardRow("Row 1 Button 1", "Row 1 Button 2", "Row 1 Button 3"))
                    // Add second row of 3 buttons
                    .keyboardRow(new KeyboardRow("Row 2 Button 1", "Row 2 Button 2", "Row 2 Button 3"))
                    .build());
    try {
        telegramClient.execute(message); // Sending our message object to user
    } catch (TelegramApiException e) {
        e.printStackTrace();
    }
}
```

Amazing! Now let's teach our bot to react on these buttons:

```java
else if (message_text.equals("Row 1 Button 1")) {
    // Send a picture to the user
    SendPhoto msg = SendPhoto
            .builder()
            .chatId(chat_id)
            // This time will send the picture using a URL
            .photo(new InputFile("https://www.kaspersky.com/content/en-global/images/repository/isc/2021/what_are_bots_image2_710x400px_300dpi.jpg"))
            .caption("Clicked button: " + message_text)
            .build();
    try {
        telegramClient.execute(msg); // Call method to send the photo
    } catch (TelegramApiException e) {
        e.printStackTrace();
    }
}
```

Now, when a user press button with "Row 1 Button 1" text on it, bot will send picture by `file_id` to user:

![Bot sends a_photo from keyboard](bot_sends_photo_command.png)

And let's add a "Hide keyboard" function when a user sends `/hide` command to bot. 
This can be done with `ReplyMarkupRemove`.

```java
else if (message_text.equals("/hide")) {
    // Hide the keyboard
    SendMessage message = SendMessage 
                    .builder()
                    .chatId(chat_id)
                    .text("Keyboard hidden")
                    .replyMarkup(new ReplyKeyboardRemove())
                    .build();
                      
    try {
        telegramClient.execute(message); // Call method to send the photo
    } catch (TelegramApiException e) {
        e.printStackTrace();
    }
}
```

Here is code of our files. You can also find all sources at [GitHub repository](https://github.com/rubenlagus/TelegramBotsDocumentation/tree/main/java-telegram-bot-tutorial).

> src/Main.java

```java
package org.telegram.telegrambots.tutorial.Lesson2.src;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new PhotoBot(botToken));
            System.out.println("PhotoBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

> src/PhotoBot.java

```java
package org.telegram.telegrambots.tutorial.Lesson2.src;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;
import java.util.List;

public class PhotoBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public PhotoBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (message_text.equals("/start")) {
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
            } else if (message_text.equals("/pic")) {
                SendPhoto msg = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        .photo(new InputFile("https://png.pngtree.com/background/20230519/original/pngtree-this-is-a-picture-of-a-tiger-cub-that-looks-straight-picture-image_2660243.jpg"))
                        .caption("This is a little cat :)")
                        .build();
                try {
                    telegramClient.execute(msg); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/markup")) {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text("Here is your keyboard")
                        .build();

                // Add the keyboard to the message
                message.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        // Add first row of 3 buttons
                        .keyboardRow(new KeyboardRow("Row 1 Button 1", "Row 1 Button 2", "Row 1 Button 3"))
                        // Add second row of 3 buttons
                        .keyboardRow(new KeyboardRow("Row 2 Button 1", "Row 2 Button 2", "Row 2 Button 3"))
                        .build());

                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Row 1 Button 1")) {
                // Send a picture to the user
                SendPhoto msg = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        // This time will send the picture using a URL
                        .photo(new InputFile("https://www.kaspersky.com/content/en-global/images/repository/isc/2021/what_are_bots_image2_710x400px_300dpi.jpg"))
                        .caption("Clicked button: " + message_text)
                        .build();

                try {
                    telegramClient.execute(msg); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/hide")) {
                // Hide the keyboard
                SendMessage message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Keyboard hidden")
                        .replyMarkup(new ReplyKeyboardRemove())
                        .build();
                try {
                    telegramClient.execute(message); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text("Unknown command")
                        .build();
                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            // Message contains photo
            // Set variables
            long chat_id = update.getMessage().getChatId();

            List<PhotoSize> photos = update.getMessage().getPhoto();
            String f_id = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getFileId)
                    .orElse("");
            int f_width = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getWidth)
                    .orElse(0);
            int f_height = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getHeight)
                    .orElse(0);
            String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight: " + Integer.toString(f_height);
            SendPhoto msg = SendPhoto
                    .builder()
                    .chatId(chat_id)
                    .photo(new InputFile(f_id))
                    .caption(caption)
                    .build();
            try {
                telegramClient.execute(msg); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
```

Now you can create and remove custom `ReplyMarkup` keyboards, create custom commands and send photos by `file_id`! You are doing very well!

