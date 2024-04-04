# Ability Toggle
Well, what if you don't like all the default abilities that AbilityBot supplies? Fear not, you are able to disable all of them, even rename them if you will!

You may pass a custom toggle to your abilitybot constructor to customize how these abilities get registered.

## The Bare-bone Toggle
The bare-bone toggle is used to **turn off** all the default abilities that come with the bot (ban, unban, demote, promote, etc...). To use the bare-bone toggle, call your super constructor with:

```java
import toggle.org.telegram.telegrambots.abilitybots.api.BareboneToggle;

public class YourAwesomeBot extends AbilityBot {

  private static final BareboneToggle toggle = new BareboneToggle();

  public YourAwesomeBot(String token, String username) {
    super(token, username, toggle);
  }
  
  // Override ceatorId()
}
```

Obviously, you can export this as a parameter that you can pass to your bot's constructor.

## The Custom Toggle
The custom toggle allows you to customize or turn off the names of the abilities that the abilitybot exposes.
```java
import toggle.org.telegram.telegrambots.abilitybots.api.CustomToggle;

public class YourAwesomeBot extends AbilityBot {
 
  private static final CustomToggle toggle = new CustomToggle()
      .turnOff("ban")
      .toggle("promote", "upgrade");

  public YourAwesomeBot(String token, String username) {
    super(token, username, toggle);
  }
  
  // Override ceatorId()
}
```

With these changes, the ability "ban" is no longer available and the "promote" ability has been renamed to "upgrade". 
## The Default Toggle
The default toggle is automatically used if the user does not specify a toggle. The default toggle allows all the abilities to be effective and unchanged.