---
sidebar_position: 5
---

# Step by Step Guide for Book Datagen

This page will guide you through the recommended and most convenient way to create a book.  
It uses the [Demo Book](https://github.com/klikli-dev/modonomicon/blob/-/neo/src/generated/resources/data/modonomicon/modonomicon/books) modonomicon provides as a starting point. 

## Book Creation Demo Project

To show how to create a book with datagen, we have created a demo book that showcases the most commonly used modonomicon features and you can use as a starting point for your own book. 

You can find the files at *[*https://github.com/klikli-dev/modonomicon-demo-book](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book)**

### For Mod Developers

You can copy and paste code from the Demo Book into your own mod. 
Please also see [Maven Dependencies](../maven-dependencies) for information on how to set up the Modonomicon dependency.

:::caution

Please note that you cannot mix a manually created `lang/*.json` file with language datagen (datagen will override your manually created file). 
However, it is best practice to datagen all your lang files anyway.

::: 

###  For Modpack Creators

You can use the Modonomicon development setup to generate JSON files that you can copy into your modpack's datapack/resourcepack. The following sections will assume that you use the "neo" (=neoforge) subproject to generate the data. You can use the generated files for any mod loader.

The generated files for the book will end up in `/neo/src/generated/resources/data/modonomicon/modonomicon/books/demo`, while the generated language files can be found in `/neo/src/generated/resources/assets/modonomicon/lang/en_us.json`.

Follow this guide to learn how to generate your own files.

:::info

Java programming knowledge will be helpful to get the most out of modonomicon datagen, but is not strictly necessary. You can also copy, paste and modify the demo book code to your liking.

:::

## Understanding the Datagen Setup

Modonomicon provides a "BookProvider", which works much like the Loot Table provider. It generates book JSON files for a given book definition (book model). Neither mod developers nor pack developers need to change or subclass the book provider. Instead, it takes "Subproviders" that define the content of the book, and which it will convert into JSON files.

Modonomicon comes with "SingleBookSubProvider", which is a datagen helper class that comes with convenience methods for setting up a book.   
The example modonomicon provides, https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/DemoBook.java, uses this class to set up the demo book. It is recommended to simply copy this file (or modify it directly).


## Step 0: The Editor of your Choice 

You can use any text editor you like to create the book.

For convenience, and if you are familiar with it, we recommend using [Intellij Community Version](https://www.jetbrains.com/idea/download/), however other Editors such as Eclipse and Visual Studio Code will work as well.

The guide will mostly be editor-agnostic, but we will provide some tips for Intellij users. Steps are very similar for users of other IDEs.
