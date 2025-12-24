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

    if (photo.getFilePath() != null) {
        return photo.getFilePath();
    } else {
        // Use the Builder pattern for GetFile instead of empty constructor + setter
        GetFile getFileMethod = GetFile.builder()
                .fileId(photo.getFileId())
                .build();
        
        try {
            // We use the full package name or a specific import to avoid 'File' confusion
            org.telegram.telegrambots.meta.api.objects.File telegramFile = telegramClient.execute(getFileMethod);
            
            // Return the path on Telegram's servers
            return telegramFile.getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    return null;
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
    Long chatId = update.getMessage().getChatId();

    // 1. Determine the action based on the command
    ActionType action;
    if (text.equals("/type")) {
        action = ActionType.TYPING;
    } else if (text.equals("/record_audio")) {
        action = ActionType.RECORD_VOICE;
    } else {
        // Default or other actions from ActionType enum
        action = ActionType.UPLOAD_DOCUMENT;
    }

    // 2. Use the Builder to construct the object with required arguments
    SendChatAction sendChatAction = SendChatAction.builder()
            .chatId(chatId.toString()) // chatId is required
            .action(action.toString()) // action is required
            .build();

    try {
        // 3. Execute the action
        telegramClient.execute(sendChatAction);
    } catch (TelegramApiException e) {
        e.printStackTrace();
    }
}
```

## How to send photos? 

There are several methods to send a photo to a user using `sendPhoto` method: With a `file_id`, with an `url` or uploading the file. In this example, we assume that we already have the *chat_id* where we want to send the photo:

```java
    public void sendImageFromUrl(String url, String chatId) {
     // Use the builder to set required fields: chatId and photo
     SendPhoto sendPhotoRequest = SendPhoto.builder()
            .chatId(chatId)
            .photo(new InputFile(url))
            .build();

     try {
         telegramClient.execute(sendPhotoRequest);
     } catch (TelegramApiException e) {
         e.printStackTrace();
     }
   }

    public void sendImageFromFileId(String fileId, String chatId) {
      // Re-using a File ID already present on Telegram's servers
      SendPhoto sendPhotoRequest = SendPhoto.builder()
            .chatId(chatId)
            .photo(new InputFile(fileId))
            .build();

      try {
          telegramClient.execute(sendPhotoRequest);
      } catch (TelegramApiException e) {
          e.printStackTrace();
      }
   }

    public void sendImageUploadingAFile(String filePath, String chatId) {
      // Create a Java File object from the path
      File imageFile = new File(filePath);

      // Ensure InputFile wraps the File object correctly for a multipart upload
      SendPhoto sendPhotoRequest = SendPhoto.builder()
            .chatId(chatId)
            .photo(new InputFile(imageFile, imageFile.getName()))
            .build();

      try {
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
	    // 1. Prepare the rows and buttons
	    List<KeyboardRow> keyboard = new ArrayList<>();
	
	    // Create the first row
	    KeyboardRow row1 = new KeyboardRow();
	    row1.add("Row 1 Button 1");
	    row1.add("Row 1 Button 2");
	    row1.add("Row 1 Button 3");
	    keyboard.add(row1);
	
	    // Create the second row
	    KeyboardRow row2 = new KeyboardRow();
	    row2.add("Row 2 Button 1");
	    row2.add("Row 2 Button 2");
	    row2.add("Row 2 Button 3");
	    keyboard.add(row2);
	
	    // 2. Build the ReplyKeyboardMarkup
	    ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
	            .keyboard(keyboard)           // The constructor/builder now requires the list
	            .resizeKeyboard(true)         // Recommended: makes buttons fit the screen
	            .oneTimeKeyboard(false)       // Keeps the keyboard visible after use
	            .selective(false)
	            .build();
	
	    // 3. Build the SendMessage with the markup
	    SendMessage message = SendMessage.builder()
	            .chatId(chatId)
	            .text("Custom message text")
	            .replyMarkup(keyboardMarkup)
	            .build();
	
	    try {
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
