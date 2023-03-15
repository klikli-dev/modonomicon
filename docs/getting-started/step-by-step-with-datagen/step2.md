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

In this file we will add instructions that will generate our book content, such as categories, entries and pages. 
Please note that this will only create the structure of the book as well as all styling instructions, but not the actual texts the players will see. This is due to how minecraft's translation / multi language system works. 

For now this file is mostly empty - that's OK!

:::caution

Due to vanilla changes to CreativeTabs, if you are on **1.19.3** you need to slightly adjust the code in this file:

```java
//1.18.2/1.19.2
.withCreativeTab("modonomicon")
```

becomes 

```java
//1.19.3
.withCreativeTab(new ResourceLocation("modonomicon","modonomicon"))
```

::: 


### EnUsProvider.java

"EnUS" corresponds to the "en_us" in the line `super(generator, modid, "en_us");` in the upper region of the file. 
This is the language code for the English language, meaning in this file we will generate English texts. 


#### Optional: Additional Languages

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

If you do create a second language - make sure to register it in the DataGenerators.java file as well!

e.g.:
```java

generator.addProvider(event.includeClient(), new FrFrProvider(generator, ModonomiconDemoBook.MODID));

```

:::