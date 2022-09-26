---
sidebar_position: 10
---

# Step 1: Setting up the Demo Project

## Installing OpenJDK 

The Demo Project uses the Forge Mod loader, which requires OpenJDK 17 installed on your system.
Start by installing OpenJDK 17 from https://adoptium.net/temurin/releases/?version=17.

:::tip

Make sure to select your correct operating system, and a 64 bit version of the JDK.  
32 bit is not fully supported by Forge.

::: 

:::tip

If the installer asks to "Add Java to PATH" and or "Set JAVA_HOME" please accept / check the corresponding boxes.

::: 

## Downloading the Demo Project

For the purposes of this Guide we will start with an empty version of the Demo Project.

### via Git (Mod Developers)

1. Open terminal
2. Go to a folder of your choice that you want to work in 
3. Run `git clone git@github.com:klikli-dev/modonomicon-demo-book.git` 
4. Run `git branch guide/step1`

:::tip

If you know what you are doing you can also skip ahead by skipping step 4, as step 3 will give you the final version of the Demo Project, allowing you to directly edit the provided files.

::: 


### via ZIP File (Modpack Creators)

1. Go to https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step1 
2. Click the green "Code" button
3. Click "Download Zip"   
   ![Download Zip](/img/docs/getting-started/step1-download-zip.png)
4. Extract the downloaded zip file to a folder of your choice.

:::tip

If you know what you are doing you can also skip ahead and download the "main" branch from https://github.com/klikli-dev/modonomicon-demo-book/tree/main and directly edit the provided files.

::: 

## First Test Run

The next steps are required to set up a minecraft development environment allowing you to run the datagen as well as minecraft locally for testing the generated files.

### IDE Setup 

:::info

If you are not using an IDE you can skip this step.

:::

1. Open Terminal in the folder you downloaded the Demo Project to
2. Generate run configurations:
   - For IntelliJ Users: Run `./gradlew genIntellijRuns` 
   - For Eclipse Users: Run `./gradlew genEclipseRuns`
   - For VSCode Users: Run `./gradlew genVSCodeRuns`

In future steps you can use the provided run configurations to run the datagen and minecraft, instead of the commands this guide will use.

### Running Minecraft

1. Open Terminal in the folder you downloaded the Demo Project to.
2. Run `./gradlew runClient`.
3. After a few seconds (possibly minutes) Minecraft should open and show the main menu.   
4. Success!

:::tip

Not sure how to open the terminal in the folder? 
Most Operating Systems will allow you to right click or shift + right click in the folder and select "Open in Terminal" or similar.

::: 
