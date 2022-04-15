---
sidebar_position: 20
title: Error Handling
---

Depending on the type of error there are different ways Modonomicon will handle them.
Syntax errors in the JSON files representing your book will be caught by Minecraft's `SimpleJsonResourceReloadListener`, while any errors later in the loading process will be caught by the Modonomicon's own error handler.

## JSON Syntax Errors 

- will show up as `ERROR` in the log files.

:::danger

JSON Syntax Errors will in most cases not be shown in-game, only in the log, leading to "silent failures"!
Make sure to check your log files for errors before releasing your book.

:::

Any errors in the json syntax will generally only show up in the log files, because they occur in Minecraft's loading logic, before Modonomicon receives the file content. In some cases they can cause follow-up errors (e.g. because an entry is missing) in Modonomicon's error handler and will be shown in-game.

In Singleplayer you will find the error log in your `/logs/latest.log` file in your Minecraft installation directory. In Multiplayer that's in the `/logs/latest.log` file in your server's `/logs` directory.

The log could be e.g. as follows for an extra comma where there shouldn't be one: 
```
[minecraft/SimpleJsonResourceReloadListener]: Couldn't parse data file modonomicon:test/entries/test_category/test_entry_child from modonomicon:modonomicons/test/entries/test_category/test_entry_child.json
com.google.gson.JsonParseException: com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON at line 30 column 4 path $.pages[3]
```

:::caution

If loading a JSON file fails server-side, there will not be any log on the client side (Minecraft loads the JSONs only server-side).
Make sure to test your book in both SP and MP.

:::

### Finding JSON Syntax Errors in the Log

Search log files for:

- `minecraft/SimpleJsonResourceReloadListener`
- `com.google.gson.JsonParseException`

to find any errors handled by Minecraft that never reach Modonomicon.


## Errors handled by Modonomicon

- will show up as `WARN` in the log files.

Modonomicon will attempt to handle errors gracefully - that means, the player will not crash, but instead will see a book screen with the error message, and a hint to check the log for further errors.

Errors come with as much context as possible, that means the error handler will attempt to include the book, category, entry, page and potentially even page content element the error is associated with. 

For example, an invalid link within a book page could produce the following error message:
```
[<time>] [Render thread/WARN] [co.kl.mo.Modonomicon/]: BookErrorManager.error() called for book: modonomicon:test with error: BookErrorInfo{ 
errorMessage='Failed to render markdown for book 'modonomicon:test'', 
context='Link: entry://modonomicon:test/test_category/test_entry@test_anchor, 
Category: modonomicon:test_category, 
Entry: modonomicon:test_category/test_entry_child, 
Page: 2', 
exception='java.lang.IllegalArgumentException: Invalid entry link, anchor not found in entry: modonomicon:test/test_category/test_entry@test_anchor'
}
```

:::caution

If you notice any issues such as crashes or log entries, but no Modonomicon error log in the above format or no Modonomicon error screen shown, please report this at https://github.com/klikli-dev/modonomicon/issues 

:::

### Finding Modonomicon Errors in the Log

Search log files for `BookErrorManager.error() called for book` to find any errors handled by Modonomicon.

### Error Screen

The error screen for the above error message would look like this:

![Error Screen](/img/docs/advanced/error-handling/error-window.png)