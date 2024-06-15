---
sidebar_position: 30
---

# Step 3: Creating your Book

A book consists of Categories, each Category of Entries, and each Entry of Pages. 

Any information that is closely related usually is grouped together as pages within an entry. For example if you introduce a new furnace you might have an entry for that furnace with a text page providing some information on how it works, a recipe page that shows how to craft the furnace, and an image page that shows how the furnace looks like.

Related entries are usually grouped in a category. Categories can be be (and are by default) displayed in "node mode" or "node view", which is the thaumonomicon inspired display mode where each entry is a node on a 2D screen that you can connect with arrows to child and parent entries to guide the player through the book.

To understand all the offered features and how to create such a book structure you should modify the demo book to your needs.   
The recommended process is to first read through all the code files in the demo book and get a basic understanding of what is possible.   
Then, create a new category and copy entries and pages that match your need into the category and start linking them together.    
Finally when things start taking shape you can delete the remaining demo content.  

## Datagen Structure

While datagen can be freely modified it is recommended to stick to the structure the demo book uses, and to also use the helper classes provided by modonomicon.

Let's take a closer look once again:

```
book
  demo
    features
      CommandEntry.java
      ...
      ImageEntry.java
      ...
    formatting
      ...
      BasicFormattingEntry.java
      ...
    indexmode
      ...
    FeaturesCategory.java
    FormattingCategory.java
    IndexmModeCategory.java
  DemoBook.java 
```

- `DemoBook.java` is the main entry point for the book. It is a subclass of the type SingleBookSubProvider which sets up the book and links its categories. 
  - Categories are set up in `generateCategories()`. Just call `this.add(new <...>)` as shown in the existing file to link a category to the book.
- `FeaturesCategory.java`, `FormattingCategory.java`, `IndexModeCategory.java` are category providers. They work the same way as the DemoBook.java, except that they set up data for categories, and allow to add entries.
  - Entries are added in generateEntries(). Just call `this.add(new <...>)` as shown in the existing file to link an entry to the category.
- `CommandEntry.java`, `ImageEntry.java`, `BasicFormattingEntry.java` (and all the other java files in the category subdirectories) are entry providers. They work the same way as the category providers, except that they set up data for entries, and allow to add pages.
  - Pages are added in `generatePages()`. 
  - Pages are a bit more complicated than simply calling `this.add()`, it is best to take a look at some of the entries to understand how they work. Very simply put, you always need a page definition that you can set with `this.page(...)`, followed by the texts you want to display on that page.

## Make some modifications

1. Start by copying and renaming the FormattingCategory into e.g. "MyTestCategory.java" and adding a directory/package "mytestcategory".  
2. Add a new entry (e.g. copy the "BasicFormattingEntry.java" to "/mytestcategory/MyTestEntry.java") with a few pages into that new directory.
3. Remove the references in your MyTestCategory to the entries of FormattingCategory and instead place `this.add(new MyTestEntry().generate('l'))`.
4. Modify the placement of the entry in the 2D string array in generateEntryMap() to place your entry where you would like it to be in the node view.

## Test your changes

This will generate an updated book for us. Let's test how that works!

1. In the terminal, run `./gradlew neo:runData` to generate the json file(s).
2. After it is complete, run `./gradlew neo:runClient` to start Minecraft.
3. Create or join a world with cheats enabled.
4. Switch to creative mode with `/gamemode creative`.
5. Open the inventory and look for the "Modonomicon" creative tab or the search tab.
6. Find your additional category and click your new entry.
7. Et voila! 


## Next Steps

You now have a working and already modified book that you can extend to your liking:
- Add additional category providers for more categories.
- Add more entries and pages to the existing category provider.
- Remove demo content.

For an overview of the possible page types and other content settings, you can view once again the demo book content, or take a look at the [Basics](/docs/basics) and [Advanced](/docs/advanced) documentation. The documentation documents the JSON format, however if you compare it to the demo book you will notice corresponding java methods, usually prefixed with "with".
Another good resource is: https://github.com/klikli-dev/theurgy/tree/HEAD/src/main/java/com/klikli_dev/theurgy/datagen/book as theurgy extensively uses Modonomicon for its in-game documentation.