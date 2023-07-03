---
sidebar_position: 40
---

# Step 4: Adding texts

Finally, it is time to add our texts. That means we will now tell the game what texts to display for all the language keys (or "Description Ids") we have seen in the previous step. 

Modonomicon allows to access LanguageProviders in the book provider, so it is very easy to add texts alongside the book structure.

You can view the final code for this step in the branch for step 4: https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step4 

Using the green "Code" Button and "Download ZIP" you can download the code for this step as a zip file to compare to your code.

## [Changes to DemoBookProvider.java](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step3..guide/step4#diff-bae90ab237cc5e78b10e53c7ad47160887a6536b0f8cb361732336cf3f986782)

The changes are very simple, view the **[Diff](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step3..guide/step4#diff-bae90ab237cc5e78b10e53c7ad47160887a6536b0f8cb361732336cf3f986782)** here.

Translations work using `this.lang().add(...)` in the BookProvider.

:::info

this.lang() returns the language provider you handed over to the book provider. That means, if you prefer, you can also do your translations in the language provider directly, but generally it is more convenient to do it in the book provider.

::: 

As you can see we use the context to get the translation keys for our book name and tooltip, and then provide the text.

## [Changes to FeaturesCategoryProvider.java](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step3..guide/step4#diff-35d687e007c80093b71ad3400be6e689d5641e8f772b6881d0ccf08642f04598)

View the **[Diff](https://github.com/klikli-dev/modonomicon-demo-book/compare/guide/step3..guide/step4#diff-35d687e007c80093b71ad3400be6e689d5641e8f772b6881d0ccf08642f04598)** here.

Much like in the DemoBookProvider we simply add calls to `this.lang().add(...)` in the CategoryProvider.
Note that the calls have to be preceded by calls to `this.context.entry()` / `this.context.page()` so the language provider gets the correct translation keys. In our example code that is done by simply doing the `this.lang().add()` calls after we make the page they are associated with *and before we make the next page*.

:::tip 

If a text element - such as a title or text - is shown is governed in the BookXYZPageModel. If we e.g. do not call the `withTitle()` method, there will not be a title on that page. Calls to `this.lang().add()` only provide the text. If we do not provide a text for any element the language key will be shown in game, reminding us to add a translation.

::: 

## Generate our updated book

Now let's take the entry and pages for a spin:

1. In the terminal, run `./gradlew runData` to generate the json file(s).
2. After it is complete, run `./gradlew runClient` to start Minecraft.
3. Re-join our old world.
4. Open the book by right-clicking with the book item.
5. Hover over the entry to see the name and description in the tooltip.
6. Click the entry to open it.
7. You should see the page texts:
   ![Entry Texts](/img/docs/getting-started/step4-add-entry-texts.png)


You now have a working Book that you can extend to your liking:
- Add additional category providers for more categories.
- Add more entries and pages to the existing category provider.

For an overview of the possible page types, you can view modonomicon's builtin demo book: https://github.com/klikli-dev/modonomicon/tree/version/1.20.1/src/main/java/com/klikli_dev/modonomicon/datagen/book

Another good resource is: https://github.com/klikli-dev/theurgy/tree/develop/src/main/java/com/klikli_dev/theurgy/datagen/book