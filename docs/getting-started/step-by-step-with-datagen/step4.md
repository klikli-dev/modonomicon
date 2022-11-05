---
sidebar_position: 40
---

# Step 4: Adding texts

Finally, it is time to add our texts. That means we will now tell the game what texts to display for all the language keys (or "Description Ids") we have seen in the previous step. To this end we will once again use Modonomicon's LanguageHelper to make things easier, as it will help us keep track of the correct language keys so we just have to focus on the texts.

If you are impatient you can skip ahead to **[Results](#results)** to see what we will be creating.


## Preparations: Java Imports

1. Open `EnUsProvider.java` in your IDE or text editor.
2. Below `package ...` but above `public class ...` add the following lines:
   ```java 
   import com.klikli_dev.modonomicon.api.ModonomiconAPI;
   import com.klikli_dev.modonomicon.api.datagen.BookLangHelper;
   import com.klikli_dev.modonomicon_demo_book.ModonomiconDemoBook;
   ```

:::tip 

If you are using an IDE it might do this step for you.

::: 


## Adding translations for the Book Name and Tooltip

1. Open `EnUsProvider.java`
2. Into the existing method `addTranslations` add:
   ```java
    this.addDemoBook();
   ```
3. And at the bottom of the file, before the last `}`, add the `addDemoBook()` method we are calling above:
   ```java
    private void addDemoBook(){
        //We again set up a lang helper to keep track of the translation keys for us.
        //Forge language provider does not give us access to this.modid, so we get it from our main mod class
        var helper = ModonomiconAPI.get().getLangHelper(ModonomiconDemoBook.MODID);
        helper.book("demo"); //we tell the helper the book we're in.
        this.add(helper.bookName(), "Demo Book"); //and now we add the actual textual book name
        this.add(helper.bookTooltip(), "A book to showcase & test Modonomicon features."); //and the tooltip text
    }
   ```


What is happening here? `this.add(<language key>, <text>)` adds a mapping for a given translation key, which we get from our helper, to the given text. The game will then display the text wherever the translation key is used in the book. 

:::tip

The final ingredient is the language code we talked about in [Step 2](./step2#enusproviderjava). This way we can have different texts for different languages, by providing a different Language Provider that uses a different text (such as, a French version) for the same language key. Minecraft will select the correct text based on the language settings of each player.

::: 

Let's take a look at our all-new translated book item:

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Hover over the book item - you should now see the name and tooltip we just added, instead of the language key we saw before.

## Adding translations for the Features Category

1. Open `EnUsProvider.java`
2. And at the bottom of the file, before the last `}`, add:
   ```java
    private void addDemoBookFeaturesCategory(BookLangHelper helper) {
        helper.category("features"); //tell the helper the category we are in
        this.add(helper.categoryName(), "Features Category"); //annd provide the category name text
    }
   ```
3. Now, at the end of the `addDemoBook` method, add:
   ```java
   this.addDemoBookFeaturesCategory(helper);
   ```

Now we can test our translated category:

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Open the book by right-clicking with the book item.
5. Hover over the category button on the top leftyou should now see the correct name.

## Addint translations for our Entry and Pages

Finally it is time to add our page texts!

1. Open `EnUsProvider.java`
2. And at the bottom of the file, before the last `}`, add:
   ```java
    private void addDemoBookMultiblockEntry(BookLangHelper helper) {
        helper.entry("multiblock"); //tell the helper the entry we are in
        this.add(helper.entryName(), "Multiblock Entry"); //provide the entry name
        this.add(helper.entryDescription(), "An entry showcasing a multiblock."); //and description

        helper.page("intro"); //now we configure the intro page
        this.add(helper.pageTitle(), "Multiblock Page"); //page title
        this.add(helper.pageText(), "Multiblock pages allow to preview multiblocks both in the book and in the world."); //page text

        helper.page("multiblock"); //and finally the multiblock page
        //now provide the multiblock name
        //the lang helper does not handle multiblocks, so we manually add the same key we provided in the DemoBookProvider
        this.add("multiblocks.modonomicon.blockentity", "Blockentity Multiblock.");
        this.add(helper.pageText(), "A sample multiblock."); //and the multiblock page text 
    }
   ```
3. Now, at the end of the `addDemoBookFeaturesCategory` method, add:
   ```java
   this.addDemoBookMultiblockEntry(helper);
   ```

:::info 

Note how we mirror the book setup we prepared in DemoBookProvider here in the EnUSProvider. The language helper works hierarchically - if we call `page()` it keeps the book, category and entry, but changes the page, if we call `entry()` it keeps the book and category, but switches the entry, and so on. This way we can easily keep track of what we are configuring, we just need to remember in each `addDemoBook<...>()` method to call the correct helper method before we add the texts.

::: 

:::tip 

If a text element - such as a title or text - is shown is governed in the DemoBookProvider. If we e.g. do not call the `withTitle()` method, there will not be a title on that page. Here in the Language provider we only need to provide the text for elements we configured in the Book Provider. If we do not provide a text for any element the language key will be shown in game, reminding us to add a translation.

::: 

Now let's take the entry and pages for a spin:

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Open the book by right-clicking with the book item.
5. Hover over the entry to see the name and description in the tooltip.
6. Click the entry to open it.
7. You should see the page texts:
   ![Entry Texts](/img/docs/getting-started/step4-add-entry-texts.png)

## Results


You can view the final code for this step in the branch for step 3: https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step4 

Using the green "Code" Button and "Download ZIP" you can download the code for this step as a zip file to compare to your code.
