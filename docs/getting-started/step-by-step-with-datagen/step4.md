---
sidebar_position: 40
---

# Step 4: Adding texts

Finally, it is time to add our texts. That means we will now tell the game what texts to display for all the language keys (or "Description Ids") we have seen in the previous step. 

Modonomicon allows to access LanguageProviders in the book provider, so it is very easy to add texts alongside the book structure.

If you are impatient you can skip ahead to **[Results](#results)** to see what we will be creating.


## Adding translations for the Book Name and Tooltip

1. Open `DemoBookProvider.java`
2. Into the existing method `generateBook` add at the top:
   ```java
   //we can reference the language providers in the book provider
   this.lang().add(this.context().bookName(), "Demo Book"); //and now we add the actual textual book name
   this.lang().add(this.context().bookTooltip(), "A book to showcase & test Modonomicon features."); //and the tooltip text
   ```

   ::: tip

   The base BookProvider Class already sets up context for our book before calling generateBook, so we don't need to do it manually.

   :::

3. Optionally, you can add translations, by their language code:
   ```java
   this.lang("es_es").add(this.context().bookName(), "Libro de demostración");
   this.lang("es_es").add(this.context().bookTooltip(), "Un libro para mostrar y probar las funciones de Modonomicon."); 
   ```


What is happening here? `this.lang().add(<language key>, <text>)` adds a mapping for a given translation key, which we get from our helper, to the given text. The game will then display the text wherever the translation key is used in the book. Under the hood `.lang()` simply gives us a reference to a LanguageProvider that works just like any forge language provider, the only special element is that we get the translation key / description id from our helper. 

:::tip

In order for translations to work a corresponding language provider must be handed over to the DemoBookProvider in the DataGenerators.java file!

::: 

Let's take a look at our all-new translated book item:

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Hover over the book item - you should now see the name and tooltip we just added, instead of the language key we saw before.

## Adding translations for the Features Category

1. Open `DemoBookProvider.java`
2. In the method `makeFeaturesCategory` add below `helper.category("features");`: 
   ```java
   this.lang().add(this.context().categoryName(), "Features Category"); //and provide the category name text
   //here is where you would provide translations, as above with the book name and tooltip
   ```

Now we can test our translated category:

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Open the book by right-clicking with the book item.
5. Hover over the category button on the top leftyou should now see the correct name.

## Adding translations for our Entry and Pages

Finally it is time to add our page texts!

1. Open `DemoBookProvider.java`
2. Find the method `makeMultiblockEntry`
3. Below `helper.entry("multiblock");` add: 
   ```java
   this.lang().add(this.context().entryName(), "Multiblock Entry"); //provide the entry name
   this.lang().add(this.context().entryDescription(), "An entry showcasing a multiblock."); //and description
   ```
   *This adds the text for hovering over the entry in the category/quest view* 

4. Below `helper.page("intro"); ... .build();` (= after the lines that create the "multiBlockIntroPage") add: 
   ```java
   this.lang().add(this.context().pageTitle(), "Multiblock Page"); //page title
   this.lang().add(this.context().pageText(), "Multiblock pages allow to preview multiblocks both in the book and in the world."); //page text
   ```
   *This adds the page text for the intro page* 


4. Below `helper.page("multiblock"); ... .build();` (= after the lines that create the "multiblockPreviewPage") add: 
   ```java
   //now provide the multiblock name
   //the lang helper does not handle multiblocks, so we manually add the same key we provided in the DemoBookProvider
   this.lang().add("multiblocks.modonomicon_demo_book.blockentity", "Blockentity Multiblock.");
   //and the multiblock page text
   this.lang().add(this.context().pageText(), "A sample multiblock.");
   ```
   *This adds the name for the multiblock, and the page text for the multiblock preview page* 

:::tip 

If a text element - such as a title or text - is shown is governed in the BookXYZPageModel. If we e.g. do not call the `withTitle()` method, there will not be a title on that page. Calls to this.lang().add() we only provide the text. If we do not provide a text for any element the language key will be shown in game, reminding us to add a translation.

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
