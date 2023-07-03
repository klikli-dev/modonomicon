---
sidebar_position: 30
---


# Step 3: Creating the Book

Now it is time to add code to generate our book, a first category and an entry with a page.
We will focus on the structure and layout of the book in this step, and add the texts in the next step.

Depending on how familiar you are with java you can either just take a look at the [full code for this step](https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step3) and make changes on your own, or download the complete new step into a new folder.

You can view the final code for this step in the branch for step 3: https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step3 

Using the green "Code" Button and "Download ZIP" you can download the code for this step as a zip file to compare to your code.

:::info

There is no branch for step 2, because that was only reviewing the files you downloaded in step 1!

::: 

## Changes to DemoBookProvider.java

Let's take a look at how our existing class changed: **[See Diff](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step1..guide/step3?diff=split#diff-bae90ab237cc5e78b10e53c7ad47160887a6536b0f8cb361732336cf3f986782)** (on the left we see the old version, on the right, the new)

First we create our features category (we'll get to what `FeaturesCategoryProvider` is in the next step!) with the id "features".
```java
var featuresCategory = new FeaturesCategoryProvider(this, "features").generate();
```

Then we add it to the book model:
```java
.withCategories(
        featuresCategory
);
```

## New Class File: FeaturesCategoryProvider

Additionally we can see that a new file was created containing the class `FeaturesCategoryProvider`.
In Modonomicon CategoryProviders are helper classes that make it easier to create category content (that is, entries and pages). 

When looking at **[the Diff](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step1..guide/step3?diff=split#diff-35d687e007c80093b71ad3400be6e689d5641e8f772b6881d0ccf08642f04598)** we see nothing on the left, indicating that the entire file is new. 

Let's go briefly over the relevant methods we find in there:

### [generateEntryMap()](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step1..guide/step3?diff=split#diff-35d687e007c80093b71ad3400be6e689d5641e8f772b6881d0ccf08642f04598R16)

The EntryMap is a helper that allows us to visualize where entries will be placed in the category view of the book. We use `_` to represent an empty slot, and any other character (numbers, letters and other unicode characters) to represent an entry. 

:::tip

Uppercase and lowercase letters are considered separate characters - if you have both `m` and `M` in the map these can be used to designate two different entry locations!

:::

Later you will see that we then hand a character to each entry, which will allow the entry to look up it's coordinates in the in-game-view.

### [generateEntries()](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step1..guide/step3?diff=split#diff-35d687e007c80093b71ad3400be6e689d5641e8f772b6881d0ccf08642f04598R28)

In this method we are supposed to create our BookEntry objects, and call this.add() to store them in the category.

Currently we have:

```java
var multiblockEntry = this.add(this.makeMultiblockEntry('m'));
```

Which creates an entry showcasing a multiblock (as we will see below), at the location of the `m` character in our entry map.


### [generateCategory()](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step1..guide/step3?diff=split#diff-35d687e007c80093b71ad3400be6e689d5641e8f772b6881d0ccf08642f04598R36)

Here we create our category - currently that is very basic, we simply provide a resource location as ID and a string as translation key for the category name (Don't worry about the name yet - the in game texts will be supplied in the next step), and an icon (the nether star).

:::caution

BookCategories support .withEntries() to add entries, however you are not supposed to do that here - the CategoryProvider does that for you.

::: 

### [makeMultiblockEntry()](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step1..guide/step3?diff=split#diff-35d687e007c80093b71ad3400be6e689d5641e8f772b6881d0ccf08642f04598R46)

This method does the heavy lifting by creating an entry with pages. 

:::tip

To create your own additional entries, all you have to do is copy & adjust this method, and then update `generateEntries()` to also call your new method and hand over the return value to `this.add(...)` as seen above for makeMultiblockEntry().

::: 

:::tip

Note the use of `this.context()`. We will use it a lot - whenever we work on a new piece of the book we update the context, which then allows us to retrieve context information (such as the translation key to set the text of a specific page) easily.

::: 

At the bottom, after creating the pages, it creates the entry with the given location character, which will be looked up in the entry map.
Finally it adds the pages to the entry. 

:::info

For now let's not worry about multiblocks - we're using a demo one that comes with modonomicon. Later you can define your own multiblocks.

<!-- Link to multiblock guide -->

:::


This will generate a book for us. Let's test how that works!

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Create or join a world with cheats enabled.
4. Switch to creative mode with `/gamemode creative`.
5. Open the inventory and look for the "Modonomicon" creative tab or the search tab.
6. There will be two purple books - the builtin modonomicon demos - and one red book, the one we just generated.
7. Take it into your hotbar and take a look at it
   1. You will notice a weird text for the name and tooltip: `book.modonomicon_demo_book.demo.name` and `book.modonomicon_demo_book.demo.tooltip` This is because we haven't added any translations yet.
8. Right click with the book in hand.
9. Crash! That's ok - if we do not add any categories the book does not know what to display. Let's fix that!

## Generate our updated book

Let's take a look at our entry!

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Right click with the book in hand.
5. Find and click the entry in the book (you may have to zoom/pan to find it)
   ![Entry](/img/docs/getting-started/step3-create-entry.png)
6. We don't have texts yet, but we see a multiblock, so that's pretty good, right?