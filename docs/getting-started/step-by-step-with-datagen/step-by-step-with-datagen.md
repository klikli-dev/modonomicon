---
sidebar_position: 10
---

# Step by Step Guide for Book Datagen

This page will guide you through the recommended and most convenient way to create a book. 

:::tip

If you run into any issues or have any questions, join our Discord: **https://dsc.gg/klikli**

:::

## Book Creation Demo Project

To show how to create a book with datagen, we have created a demo project that you can use as a starting point for your own book. 

You can find the files at **https://github.com/klikli-dev/modonomicon-demo-book**

### For Mod Developers

You can copy and paste code from the Demo Project into your own mod. 
Please also see [Maven Dependencies](../maven-dependencies) for information on how to set up the Modonomicon dependency (alternatively, look into the `build.gradle` and `gradle.properties` files of the demo project).

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