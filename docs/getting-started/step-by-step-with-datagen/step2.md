---
sidebar_position: 20
---

# Step 2: A first look at the Demo Project

## Demo Project File Structure

First, take a minute to look at the folder structure of the test project. 
The most intersting parts are in the `/src/` folder, you can ignore the other folders and files for now.

```
src
+---generated
|   \---resources
+---main
|   +---java
|   |   \---com
|   |       \---klikli_dev
|   |           \---modonomicon_demo_book
|   |               |   ModonomiconDemoBook.java
|   |               |
|   |               \---datagen
|   |                   |   DataGenerators.java
|   |                   |   DemoBookProvider.java
|   |                   |
|   |                   \---lang
|   |                           EnUsProvider.java
|   |
|   \---resources
|       |   modonomicon.png
|       |   pack.mcmeta
|       |
|       \---META-INF
|               mods.toml
```

- `/main/java/` contains the source code files used to generate the book.
- `/generated/` contains the generated files.

## Interlude for Mod developers: Copying relevant files 

At this point it might be a good idea to copy the `/datagen/` folder and all contents into your mod. 
You should skip ModonomiconDemoBook.java, it is the main mod class for the demo project, you should have your own main mod class.

## A closer look at the existing files

### ModonomiconDemoBook.java

:::tip

Modpack creators can and should skip this step.

::: 

Interesting is only the line `modEventBus.addListener(DataGenerators::gatherData);`: It registers our DataGenerator to run in Forge's GatherData event. Make sure to include it in your main mod class.

### DataGenerators.java

:::tip

Modpack creators can and should skip this step.

::: 

This file registers the two Data Generators from the Demo Project - one that will generate the book, and one that will create the corresponding `/lang/*.json` file. You may want to add more generators for other mod content here in the future. 

### DemoBookProvider.java

In this file we will add instructions that will generate our book content, such as calls to category providers, which in turn will create entries and pages.
Additionally it holds a reference to the language providers to also easily add the actual book texts.

For now this file is mostly empty - that's OK!


### EnUsProvider.java

"EnUS" corresponds to the "en_us" in the line `super(generator, modid, "en_us");` in the upper region of the file. 
This is the language code for the English language, meaning in this file we will generate English texts. 


#### Optional: Additional Languages

The Demo Book comes with a language provider for Spanish (although only small parts of the demo book are translated to Spanish!).
If you want to provide translations for other languages, you can copy and rename the file, and change the language code to e.g. "fr_fr" for French.

In Java the "class" name and file name must correspond. Thus, a hypothethical file FrFrProvider would have to look something like this:

```java 
public class FrFrProvider extends LanguageProvider {
   public FrFrProvider(DataGenerator generator, String modid) {
        super(generator, modid, "fr_fr");
    }
}
```

:::tip

If you do create an additional language - make sure to register it in the DataGenerators.java file as well!

e.g. for frFr Provider modify it to look like this:
```java

var enUsProvider = new EnUsProvider(generator.getPackOutput(), ModonomiconDemoBook.MODID);
var esEsProvider = new EsEsProvider(generator.getPackOutput(), ModonomiconDemoBook.MODID);
var frFrProvider = new EsEsProvider(generator.getPackOutput(), ModonomiconDemoBook.MODID);

generator.addProvider(event.includeServer(), new DemoBookProvider(generator.getPackOutput(), ModonomiconDemoBook.MODID,
        enUsProvider, //first add the default language, will be accessed via .lang()
        esEsProvider, //then all other languages, will be accessed via .lang("<lang code>"), for example .lang("es_es")
        frFrProvider
));

//Important: Lang providers need to be added after the book provider to process the texts added by the book provider
generator.addProvider(event.includeClient(), enUsProvider);
generator.addProvider(event.includeClient(), esEsProvider);
generator.addProvider(event.includeClient(), frFrProvider);

```

:::