# FAQ

Quick answers for Quick questions

<note>
    <p>
      Most of the answers assume that you already have an instance of a <code>TelegramClient</code> available as <code>telegramClient</code>
    </p>
</note>

## How to download photo?

To download a picture (or any other file), you will need the `file_path` of the file. Let start by finding the photo we want to download, the following method will extract the `PhotoSize` from a photo sent to the bot (in our case, we are taken the bigger size of those provided):

```java
public PhotoSize getPhoto(Update update) {
    // Check that the update contains a message and the message has a photo
    if (update.hasMessage() && update.getMessage().hasPhoto()) {
        // When receiving a photo, you usually get different sizes of it
        List<PhotoSize> photos = update.getMessage().getPhoto();

        // We fetch the bigger photo
        return photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
    }

    // Return null if not found
    return null;
}
```

Once we have the *photo* we have two options: The `file_path` is already present or we need to get it. The following method will handle both of them and return the final `file_path`:

```java
public String getFilePath(PhotoSize photo) {
    Objects.requireNonNull(photo);

    if (photo.getFilePath() != null) { // If the file_path is already present, we are done!
        return photo.getFilePath();
    } else { // If not, let find it
        // We create a GetFile method and set the file_id from the photo
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(photo.getFileId());
        try {
            // We execute the method using AbsSender::execute method.
            File file = telegramClient.execute(getFileMethod);
            // We now have the file_path
            return file.getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    return null; // Just in case
}
```

Now that we have the `file_path` we can download it:

```java
public java.io.File downloadPhotoByFilePath(String filePath) {
    try {
        // Download the file calling AbsSender::downloadFile method
        return telegramClient.downloadFile(filePath);
    } catch (TelegramApiException e) {
        e.printStackTrace();
    }

    return null;
}
```

The returned `java.io.File` object will be your photo

## How to display ChatActions like "typing" or "recording a voice message"?
Quick example here that is showing ChatActions for commands like "/type" or "/record_audio"

```java
if (update.hasMessage() && update.getMessage().hasText()) {

    String text = update.getMessage().getText();

    SendChatAction sendChatAction = new SendChatAction();
    sendChatAction.setChatId(update.getMessage().getChatId());

    if (text.equals("/type")) {
        // -> "typing"
        sendChatAction.setAction(ActionType.TYPING);
        // -> "recording a voice message"
    } else if (text.equals("/record_audio")) {
        sendChatAction.setAction(ActionType.RECORD_VOICE);
    } else {
        // -> more actions in the Enum ActionType
        // For information: https://core.telegram.org/bots/api#sendchataction
        sendChatAction.setAction(ActionType.UPLOAD_DOCUMENT);
    }

    try {
        Boolean wasSuccessfull = telegramClient.execute(sendChatAction);
    } catch (TelegramApiException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
```

## How to send photos? 

There are several methods to send a photo to a user using `sendPhoto` method: With a `file_id`, with an `url` or uploading the file. In this example, we assume that we already have the *chat_id* where we want to send the photo:

```java
    public void sendImageFromUrl(String url, String chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(new InputFile(url));
        try {
            // Execute the method
            telegramClient.execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendImageFromFileId(String fileId, String chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(new InputFile(fileId));
        try {
            // Execute the method
            telegramClient.execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendImageUploadingAFile(String filePath, String chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo file as a new photo (You can also use InputStream with a constructor overload)
        sendPhotoRequest.setPhoto(new InputFile(new File(filePath)));
        try {
            // Execute the method
            telegramClient.execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
```

## How to send stickers?

There are several ways to send a sticker, but now we will use `file_id` and `url`.

`file_id`: To get the *file_id*, you have to send your sticker to the [**Get Sticker ID**](https://t.me/idstickerbot?do=open_link) bot and then you will receive a string.
`url`: All you need to have is a link to the sticker in `.webp` format, like [**This**](https://www.gstatic.com/webp/gallery/5.webp).

#### Implementation

Just call the method below in your `onUpdateReceived(Update update)` method.

```java
   // Sticker_file_id is received from @idstickerbot bot  
   private void StickerSender(Update update, String Sticker_file_id) { 
      //the ChatId that  we received form Update class  
      String ChatId = update.getMessage().getChatId().toString();  
      // Create an InputFile containing Sticker's file_id or URL  
	  InputFile StickerFile = new InputFile(Sticker_file_id);  
      // Create a SendSticker object using the ChatId and StickerFile  
	  SendSticker TheSticker = new SendSticker(ChatId, StickerFile); 
	 
      // Will reply the sticker to the message sent  
      //TheSticker.setReplyToMessageId(update.getMessage().getMessageId());  
   
      try {  // Execute the method
         telegramClient.execute(TheSticker);  
      } catch (TelegramApiException e) {  
         e.printStackTrace();  
      }     
   }
```
## How to send photo by its file_id?

In this example we will check if user sends to bot a photo, if it is, get Photo's file_id and send this photo by file_id to user.
```java
// If it is a photo
if (update.hasMessage() && update.getMessage().hasPhoto()) {
   // Array with photos
   List<PhotoSize> photos = update.getMessage().getPhoto();
   // Get largest photo's file_id
   String f_id = photos.stream()
           .max(Comparator.comparing(PhotoSize::getFileSize))
           .orElseThrow().getFileId();
   // Send photo by file_id we got before
   SendPhoto sendPhoto = SendPhoto.builder()
           .chatId(update.getMessage().getChatId())
           .photo(new InputFile(f_id))
           .caption("Photo")
           .build();
   try {
       telegramClient.execute(sendPhoto); // Call method to send the photo
   } catch (TelegramApiException e) {
       e.printStackTrace();
   }
}
```

## How to use custom keyboards?

Custom keyboards can be appended to messages using the `setReplyMarkup`. In this example, we will build a simple [ReplyKeyboardMarkup](https://core.telegram.org/bots/api#replykeyboardmarkup) with two rows and three buttons per row, but you can also use other types like [ReplyKeyboardHide](https://core.telegram.org/bots/api#replykeyboardhide), [ForceReply](https://core.telegram.org/bots/api#forcereply) or [InlineKeyboardMarkup](https://core.telegram.org/bots/api#inlinekeyboardmarkup):

```java
    public void sendCustomKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Custom message text");

        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add("Row 1 Button 1");
        row.add("Row 1 Button 2");
        row.add("Row 1 Button 3");
        // Add the first row to the keyboard
        keyboard.add(row);
        // Create another keyboard row
        row = new KeyboardRow();
        // Set each button for the second line
        row.add("Row 2 Button 1");
        row.add("Row 2 Button 2");
        row.add("Row 2 Button 3");
        // Add the second row to the keyboard
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup);

        try {
            // Send the message
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
```

[InlineKeyboardMarkup](https://core.telegram.org/bots/api#inlinekeyboardmarkup) use list to capture the buttons instead of KeyboardRow.

```java
   public void sendInlineKeyboard(String chatId) {
      SendMessage message = new SendMessage();
      message.setChatId(chatId);
      message.setText("Inline model below.");
      
      // Create InlineKeyboardMarkup object
      InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
      // Create the keyboard (list of InlineKeyboardButton list)
      List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
      // Create a list for buttons
      List<InlineKeyboardButton> Buttons = new ArrayList<InlineKeyboardButton>();
      // Initialize each button, the text must be written
      InlineKeyboardButton youtube= new InlineKeyboardButton("youtube");
      // Also must use exactly one of the optional fields,it can edit  by set method
      youtube.setUrl("https://www.youtube.com");
      // Add button to the list
      Buttons.add(youtube);
      // Initialize each button, the text must be written
      InlineKeyboardButton github= new InlineKeyboardButton("github");
      // Also must use exactly one of the optional fields,it can edit  by set method
      github.setUrl("https://github.com");
      // Add button to the list
      Buttons.add(github);
      keyboard.add(Buttons);
      inlineKeyboardMarkup.setKeyboard(keyboard);
      // Add it to the message
      message.setReplyMarkup(inlineKeyboardMarkup);
      
      try {
         // Send the message
         telegramClient.execute(message);
      } catch (TelegramApiException e) {
         e.printStackTrace();
      }
   }
```

## How can I run my bot?

You don't need to spend a lot of money into hosting your own telegram bot. Basically, there are two options around how to host:

1. Hosting on your own hardware. It can be a Mini-PC like a Raspberry Pi. The costs for the hardware (~35€) and annual costs for power (~7-8€) are low. Keep in mind that your internet connection might be limited and a Mini-Pc is not ideal for a large users base.
2. Run your bot in a Virtual Server/dedicated root server. There are many hosters out there that are providing cheap servers that fit your needs. The cheapest one should be openVZ-Containers or a KVM vServer. Example providers are [Hetzner](https://www.hetzner.de/ot/), [DigitalOcean](https://www.digitalocean.com/),  (are providing systems that have a high availability but cost's a bit more) and [OVH](https://ovh.com)
   For a deeper explanation for deploying your bot on DigitalOcean please see the [Lesson 5. Deploy your bot](Lesson-5.md) in the tutorials.

## Method ```sendMessage()``` (or other) is deprecated, what should I do? 
Please use `TelegramClient::execute()` instead.

Example:
```java
SendMessage message = new SendMessage();
//add chat id and text
telegramClient.execute(message);
```

## Is there any example for WebHook?
Please see the example Bot for https://telegram.me/SnowcrashBot in the [TelegramBotsExample](https://github.com/rubenlagus/TelegramBotsExample) repo and also an [example bot for Spring Boot](https://github.com/UnAfraid/SpringTelegramBot) from [UnAfraid](https://github.com/UnAfraid) [here](https://github.com/UnAfraid/SpringTelegramBot/blob/master/src/main/java/com/github/unafraid/spring/bot/TelegramWebHookBot.java)


## How to use spring boot starter
Your main spring boot class should look like this:

```java
@SpringBootApplication
public class YourApplicationMainClass {

    public static void main(String[] args) {
        SpringApplication.run(YourApplicationMainClass.class, args);
    }
}
```

After that you´ll need to choose whether you want to use the Webhook started or the Long polling starter. Below example assume you are going with Long polling version.

```java
  //Standard Spring component annotation
  @Component
  public class YourBotClassName extends SpringLongPollingBot {
    //Bot body.
  }
```

You could just implement SpringLongPollingBot or extend SpringTelegramWebhookBot interfaces. 
All this bots will be registered in context and connected to Telegram api.
