---
sidebar_position: 30
---


# Step 3: Creating the Book

Now it is time to add code to generate our book, a first category and an entry with a page.
We will focus on the structure and layout of the book in this step, and add the texts in the next step.

If you are impatient you can skip ahead to **[Results](#results)** to see what we will be creating.

## Preparations: Java Imports

1. Open `DemoBookProvider.java` in your IDE or text editor.
2. Below `package ...` but above `public class ...` add the following lines:
    ```java 
    import com.klikli_dev.modonomicon.api.datagen.BookProvider;
    import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
    import net.minecraft.data.PackOutput;
    import net.minecraft.resources.ResourceLocation;
    import net.minecraftforge.common.data.LanguageProvider;
    ```

:::tip 

If you are using an IDE it might do this step for you.

::: 

## Our first Book

The code for just an empty book is already present if you cloned the step1 branch.

1. Open `DemoBookProvider.java` in your IDE or text editor.
2. In the `DemoBookProvider` constructor, note that we hand `"demo"` to the super constructor - that is our book id.
   It will be used in our context `BookContextHelper` (acessible via `this.context()`) to create DescriptionIds/Translation Keys and ResourceLocations.
3. In the Method `generateBook` we see some code that sets up a basic book model for us
    ```java
    @Override
    protected BookModel generateBook() {
        var demoBook = BookModel.create(
                        this.modLoc(this.context().book), //the id of the book. modLoc() prepends the mod id.
                        this.context().bookName() //the name of the book. The lang helper gives us the correct translation key.
                )
                .withTooltip(this.context().bookTooltip()) //the hover tooltip for the book. Again we get a translation key.
                .withGenerateBookItem(true) //auto-generate a book item for us.
                .withModel(new ResourceLocation("modonomicon:modonomicon_red")) //use the default red modonomicon icon for the book
                .withCreativeTab(new ResourceLocation("modonomicon", "modonomicon")) //and put it in the modonomicon tab
                ;

        return demoBook;
    }
    ```

:::tip

Note the use of `this.context()`. We will use it a lot - whenever we work on a new piece of the book we update the context, which then allows us to retrieve context information (such as the translation key to set the text of a specific page) easily.

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

## Our first Category

1. Open `DemoBookProvider.java`
2. Add the following code to the bottom of the file, before the last `}`:
    ```java
    private BookCategoryModel makeFeaturesCategory() {
        this.context().category("features"); //set context to our new category

        //the entry map is another helper for book datagen
        //it allows us to place entries in the category without manually defining the coordinates.
        //each letter can be used to represent an entry
        //Note that getting it this way is deprecated, but will not be removed.
        //  it is just a hint that it is better to use CategoryProviders, for an example see https://github.com/klikli-dev/modonomicon/tree/version/1.20.1/src/main/java/com/klikli_dev/modonomicon/datagen/book
        var entryMap = ModonomiconAPI.get().getEntryMap();
        entryMap.setMap(
                "_____________________",
                "__m______________d___",
                "__________r__________",
                "__c__________________",
                "__________2___3___i__",
                "__s_____e____________"
        );

        return BookCategoryModel.create(
                        this.modLoc(this.context().category), //the id of the category, as stored in the lang helper. modLoc() prepends the mod id.
                        this.context().categoryName() //the name of the category. The lang helper gives us the correct translation key.
                )
                .withIcon("minecraft:nether_star") //the icon for the category. In this case we simply use an existing item.
                .withEntries(multiblockEntry);
    }
    ```
3. Now we need to add our category to the book. In `generateBook` add:
   1. Right at the start:
    ```java
    var featuresCategory = this.makeFeaturesCategory();
    ```  
    This calls our newly created method that makes our category.

   2. below `.withCreativeTab("modonomicon")`:
    ```java 
    .withCategories(
            featuresCategory
    );
    ```

    And with this we hand over the category to be added to the book.

This will create a category with the id "features" using a nether star as icon and add it to our book. See also **[Categories](../../basics/structure/categories.md#attributes)** to learn more about the other attributes of a category, and how icons work.    

:::tip

Note the use of `entryMap`. This is a helper that allows us to visualize how our category will look and use that to get the right coordinates without manually experimenting.

::: 


Let's see if that fixed our crash:

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Right click with the book in hand.
5. Et voila: 
   ![Category](/img/docs/getting-started/step3-create-category.png)
6. Success! No crash, but no content either. Not too bad, right?
7. You will notice that when hovering over the category's button on the left side we again have a weird text instead of a nice category name - no worries, we'll fix that later!

## Our first Entry

1. Open `DemoBookProvider.java`
2. 2. Add the following code to the bottom of the file, before the last `}`:
    ```java
     private BookEntryModel makeMultiblockEntry(CategoryEntryMap entryMap, char location) {
        this.context().entry("multiblock"); //tell our context the entry we are in. It puts it on top of the category.

        this.context().page("intro"); //and now the page
        var multiBlockIntroPage =
                BookTextPageModel.builder() //we start with a text page
                        .withText(this.context().pageText()) //now we can use the context to retrieve the description id for the text
                        .withTitle(this.context().pageTitle()) //and for the title
                        .build();

        this.context().page("multiblock"); //next page
        var multiblockPreviewPage =
                BookMultiblockPageModel.builder() //now a page to show a multiblock
                        .withMultiblockId("modonomicon:blockentity") //sample multiblock from modonomicon
                        .withMultiblockName("multiblocks.modonomicon_demo_book.blockentity") //and the lang key for its name
                        .withText(this.context().pageText()) //plus a page text
                        .build();

        return BookEntryModel.builder()
                .withId(this.modLoc(this.context().category + "/" + this.context().entry)) //make entry id from lang helper data. It is good practice to use the category as a prefix like this which causes the entry to be in a subfolder.
                .withName(this.context().entryName()) //entry name lang key
                .withDescription(this.context().entryDescription()) //entry description lang key
                .withIcon("minecraft:furnace") //we use furnace as icon
                .withLocation(entryMap.get(location)) //and we place it at the location we defined earlier in the entry helper mapping
                .withPages(multiBlockIntroPage, multiblockPreviewPage) //finally we add our pages to the entry
                .build();
    }
    ```
3. Now we need to add our entry to the features category. In `makeFeaturesCategory` add:
   1. below `entryMap.setMap( ... );`:
    ```java
    //place the multiblock entry where we put the "m" in the map above
    var multiblockEntry = this.makeMultiblockEntry(entryMap, 'm');
    ```  
    1. below `.withIcon("minecraft:nether_star")`:
    ```java 
    .withEntries(
            multiblockEntry
    );
    ```

This will create an entry with a text page and a page that displays a multiblock. 
Text and title properties can be skipped, then the page will simply not use them. Especially pages with content like multiblocks or recipes may not need a text, or are better off without a text because there is only very little space on the page, and you can always add additional text pages to explain things in more detail.

:::info

For now let's not worry about multiblocks - we're using a demo one that comes with modonomicon. Later you can define your own multiblocks.

<!-- Link to multiblock guide -->

:::

Let's take a look at our entry!

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Right click with the book in hand.
5. Find and click the entry in the book (you may have to zoom/pan to find it)
   ![Entry](/img/docs/getting-started/step3-create-entry.png)
6. Still no nice texts, but we see a multiblock, so that's pretty good, right?

## Results

You can view the final code for this step in the branch for step 3: https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step3 

Using the green "Code" Button and "Download ZIP" you can download the code for this step as a zip file to compare to your code.

:::info

There is no branch for step 2, because that was only reviewing the files you downloaded in step 1!

::: 