---
sidebar_position: 20
---

# Step by Step Guide for Book Datagen

This page will guide you through the recommended and most convenient way to create a book. 

:::tip

If you run into any issues or have any questions, join our Discord: **https://invite.gg/klikli**

:::

## Book Creation Demo Project

To show how to create a book with datagen, we have created a demo project that you can use as a starting point for your own book. 

You can find the files at **https://github.com/klikli-dev/modonomicon-demo-book**

### For Mod Developers

You can copy and paste code from the Demo Project into your own mod. 
Please also see [Maven Dependencies](./maven-dependencies) for information on how to set up the Modonomicon dependency (alternatively, look into the `build.gradle` and `gradle.properties` files of the demo project).

:::caution

Please note that you cannot mix a manually created `lang/*.json` file with language datagen (datagen will override your manually created file). 
However, it is best practice to datagen all your lang files anyway.

::: 

###  For Modpack Creators

You can use the Demo Project to generate JSON files that you can copy into your modpack's datapack/resourcepack.

The generated files for the book will end up in `/src/generated/resources/data/modonomicon_demo_book/`, while the generated language files can be found in `/src/generated/resources/assets/modonomicon_demo_book/lang/`.

Follow this guide to learn how to generate your own files.

:::info

No Java or other programming knowledge is needed to use the Demo Project, but programming knowledge will be helpful if you want to automate the creation of the book to a higher degree.

:::

## Understanding the Demo Project Repository

The Demo Project will offer one git branch for each major step in this guide. A list of branches can be found here: https://github.com/klikli-dev/modonomicon-demo-book/branches

The branch "main", which you also see when opening https://github.com/klikli-dev/modonomicon-demo-book, contains the final result of the guide, including the generated files.

:::tip

If you do not know what a "git branch" is - worry not. Think of it as one snapshot of the demo project that you can download that corresponds to the current step in this guide.

::: 

## Step 0: The Editor of your Choice 

You can use any text editor you like to create the book.

For convenience, and if you are familiar with it, we recommend using [Intellij Community Version](https://www.jetbrains.com/idea/download/), however other Editors such as Eclipse and Visual Studio Code will work as well.

The guide will mostly be editor-agnostic, but we will provide some tips for Intellij users. Steps are very similar for users of other IDEs.

## Step 1: Setting up the Demo Project

### Installing OpenJDK 

The Demo Project uses the Forge Mod loader, which requires OpenJDK 17 installed on your system.
Start by installing OpenJDK 17 from https://adoptium.net/temurin/releases/?version=17.

:::tip

Make sure to select your correct operating system, and a 64 bit version of the JDK.  
32 bit is not fully supported by Forge.

::: 

:::tip

If the installer asks to "Add Java to PATH" and or "Set JAVA_HOME" please accept / check the corresponding boxes.

::: 

### Downloading the Demo Project

For the purposes of this Guide we will start with an empty version of the Demo Project.

#### via Git (Mod Developers)

1. Open terminal
2. Go to a folder of your choice that you want to work in 
3. Run `git clone git@github.com:klikli-dev/modonomicon-demo-book.git` 
4. Run `git branch guide/step1`

:::tip

If you know what you are doing you can also skip ahead by skipping step 4, as step 3 will give you the final version of the Demo Project, allowing you to directly edit the provided files.

::: 


#### via ZIP File (Modpack Creators)

1. Go to https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step1 
2. Click the green "Code" button
3. Click "Download Zip"   
   ![Download Zip](/img/docs/getting-started/step1-download-zip.png)
4. Extract the downloaded zip file to a folder of your choice.

:::tip

If you know what you are doing you can also skip ahead and download the "main" branch from https://github.com/klikli-dev/modonomicon-demo-book/tree/main and directly edit the provided files.

::: 

### First Test Run

The next steps are required to set up a minecraft development environment allowing you to run the datagen as well as minecraft locally for testing the generated files.

#### IDE Setup 

:::info

If you are not using an IDE you can skip this step.

:::

1. Open Terminal in the folder you downloaded the Demo Project to
2. Generate run configurations:
   - For IntelliJ Users: Run `./gradlew genIntellijRuns` 
   - For Eclipse Users: Run `./gradlew genEclipseRuns`
   - For VSCode Users: Run `./gradlew genVSCodeRuns`

In future steps you can use the provided run configurations to run the datagen and minecraft, instead of the commands this guide will use.

#### Running Minecraft

1. Open Terminal in the folder you downloaded the Demo Project to.
2. Run `./gradlew runClient`.
3. After a few seconds (possibly minutes) Minecraft should open and show the main menu.   
4. Success!

:::tip

Not sure how to open the terminal in the folder? 
Most Operating Systems will allow you to right click or shift + right click in the folder and select "Open in Terminal" or similar.

::: 

## Step 2: A first look at the Demo Project

### Demo Project File Structure

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

### Interlude for Mod developers: Copying relevant files 

At this point it might be a good idea to copy the `/datagen/` folder and all contents into your mod. 
You should skip ModonomiconDemoBook.java, it is the main mod class for the demo project, you should have your own main mod class.

### A closer look at the existing files

#### ModonomiconDemoBook.java

:::tip

Modpack creators can and should skip this step.

::: 

Interesting is only the line `modEventBus.addListener(DataGenerators::gatherData);`: It registers our DataGenerator to run in Forge's GatherData event. Make sure to include it in your main mod class.

#### DataGenerators.java

:::tip

Modpack creators can and should skip this step.

::: 

This file registers the two Data Generators from the Demo Project - one that will generate the book, and one that will create the corresponding `/lang/*.json` file. You may want to add more generators for other mod content here in the future. 

#### DemoBookProvider.java

In this file we will add instructions that will generate our book content, such as categories, entries and pages. 
Please note that this will only create the structure of the book as well as all styling instructions, but not the actual texts the players will see. This is due to how minecraft's translation / multi language system works. 

For now this file is mostly empty - that's OK!

#### EnUsProvider.java

"EnUS" corresponds to the "en_us" in the line `super(generator, modid, "en_us");` in the upper region of the file. 
This is the language code for the English language, meaning in this file we will generate English texts. If you want to provide translations for other languages, you can copy and rename the file, and change the language code to e.g. "fr_fr" for French.

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

::: 

## Step 3: Creating the Book

Now it is time to add code to generate our book, a first category and an entry with a page.

### Our first Book

1. Open `DemoBookProvider.java` in your IDE or text editor.
2. In the `generate` method, add the following code:
    ```java
     //call our code that generates a book with the id "demo"
    var demoBook = this.makeDemoBook("demo");

    //then add the book to the list of books to generate    
    this.add(demoBook);
    ```

    This will create a new book with the id "demo" (the code for that follows in the next step) and add it to the list of books to be generated.

3. Now we add the method we're calling above to the bottom of the file:
    ```java
    private BookModel makeDemoBook(String bookName) {
        //The lang helper keeps track of the "DescriptionIds", that is, the language keys for translations, for us
        var helper = ModonomiconAPI.get().getLangHelper(this.modid);

        //we tell the helper the book we're in.
        helper.book(bookName);

        //Now we create the book with settings of our choosing
        var demoBook = BookModel.builder()
                .withId(this.modLoc(bookName)) //the id of the book. modLoc() prepends the mod id.
                .withName(helper.bookName()) //the name of the book. The lang helper gives us the correct translation key.
                .withTooltip(helper.bookTooltip()) //the hover tooltip for the book. Again we get a translation key.
                .withGenerateBookItem(true) //auto-generate a book item for us.
                .withModel(new ResourceLocation("modonomicon:modonomicon_red")) //use the default red modonomicon icon for the book
                .withCreativeTab("modonomicon") //and put it in the modonomicon tab
                .build();
        return demoBook;
    }
    ```

Congratulations! This will generate a book for us. Let's test how that works!

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

### Our first Category

### Our first Entry

### Time for texts!



<!-- TODO: Link to the final version of this step on github -->

## Step 4: Working on Content