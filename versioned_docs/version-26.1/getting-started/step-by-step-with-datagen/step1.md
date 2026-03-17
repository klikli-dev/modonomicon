---
sidebar_position: 10
---

# Step 1: [Modpack Creators Only] Setting up a Datagen Environment

:::info

Mod developers presumably already have a development environment set up.
If not, please refer to the MDK setup guide of your modloader of choice. It is not a good idea to use modonomicon as a template for your own mod, unless you plan to make another book mod :) 

:::

##  Installing OpenJDK 

Modonomicon uses the Forge, Neoforge and Fabric modloaders, which require OpenJDK 21 installed on your system.
Start by installing OpenJDK 21 from https://adoptium.net/temurin/releases/?version=21.

:::tip

Make sure to select your correct operating system, and a 64 bit version of the JDK.  
32 bit is not fully supported by all modloaders.

::: 

:::tip

If the installer asks to "Add Java to PATH" and or "Set JAVA_HOME" please accept / check the corresponding boxes.

::: 

## Downloading Modonomicon Source Code

### via Git

1. Open terminal
2. Go to a folder of your choice that you want to work in 
3. Run `git clone git@github.com:klikli-dev/modonomicon.git` 

:::tip

If you know what you are doing you can also skip ahead by skipping step 4, as step 3 will give you the final version of the Demo Project, allowing you to directly edit the provided files.

::: 

### via ZIP File 

1. Go to https://github.com/klikli-dev/modonomicon
2. Click the green "Code" button
3. Click "Download Zip"   
   ![Download Zip](/img/docs/getting-started/step1-download-zip.png)
4. Extract the downloaded zip file to a folder of your choice.

## First Test Run

The next steps are required to set up a minecraft development environment allowing you to run the datagen as well as minecraft locally for testing the generated files.

1. Open Terminal in the folder you downloaded and extracted the Demo Project to
   1. If you see the files `gradlew` and `gradlew.bat` you are in the right folder

:::tip

Not sure how to open the terminal in the folder? 
1. Most Operating Systems will allow you to right click or shift + right click in the folder and select "Open in Terminal" or similar.
2. Otherwise use the command `cd` like so: `cd "<path to folder>"`
   1. Note: On windows you might first have to switch to the correct drive. If e.g. terminal shows `C:\\` but your path starts with `D:\\` you can switch to the correct drive by running simply `D:` (no `cd` or any other command/prefix required)

::: 

### Running Minecraft

1. Open Terminal in the folder you downloaded the Project to.
2. Run `./gradlew neo:runClient`.
3. After a few seconds (possibly minutes) Minecraft should open and show the main menu.   
4. Success!

:::tip 

**Windows Users:** If you are getting a message along the lines of `command . not found` try running `gradlew.bat <...>` instead of `./gradlew <...>`

:::