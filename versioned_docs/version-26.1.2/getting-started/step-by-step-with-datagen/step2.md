---
sidebar_position: 20
---

# Step 2: A first look at the Demo Book

## File Structure

First, take a minute to look at the folder structure relevant for demo book, either locally if you downloaded it in the previous step, or on [GitHub](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book).
```
.
└── modonomicon/
    ├── common/
    │   └── src/
    │       └── main/
    │           └── java/
    │               └── com/
    │                   └── klikli_dev/
    │                       └── modonomicon/
    │                           └── datagen/
    │                               └── book/
    │                                   ├── demo/
    │                                   │   ├── ConditionalCategory.java
    │                                   │   └── ...
    │                                   └── DemoBook.java
    └── neo/
        └── src/
            └── generated/
                ├── resources/
                │   ├── assets/
                │   │   └── modonomicon/
                │   │       └── lang/
                │   │           └── en_us.json
                │   └── data/
                │       └── modonomicon/
                │           └── books/
                │               └── demo/
                │                   └── book.json
                └── main/
                    └── java/
                        └── com/
                            └── klikli_dev/
                                └── modonomicon/
                                    └── datagen/
                                        └── DataGenerators.java
```

- `/common/src/java/.../datagen/book` contains the source code files used to generate the book. 
  - **Mod Developers**: This code is mod-loader independent and works on all mod loaders. That means, you can simply copy that entire folder into your own datagen folder. If you use a multiloader setup it should also go into common. 
  - **Modpack Creators**: These are the files you will be modifying to create your own book.
- `/neo/src/java/.../datagen/DataGenerators.java` shows how the book provider is registered. Your mod loader documentation should tell you how to make sure the events used in the DataGenerators classes are registered.
  - **Modpack Creators**: You need not concern yourself with this file.
  - **Mod Developers**: This file shows how to register the book provider and how to hook it up with the language providers. The other mod loader folders (forge, fabric) contain similar files in the same path. You can copy the lines related to the book provider into your own datagen setup.
- `/neo/src/generated/resources/data/.../demo/...` contains the generated files book definition files.
- `/neo/src/generated/resources/assets/.../lang/en_us.json` contains the book texts. 
  - **Modpack Creators**: Please note that this file will also contain a few texts related to modonomicon functionality. It is best to remove them before publishing the book as a data/resource pack.

## A closer look at the existing files

### DataGenerators.java

:::tip

Modpack creators can and should skip this step and move the "DemoBook.java".

::: 

The data generator registration happens in three steps. 
1. First a Language Provider Cache is created. This allows you to write texts directly in the book (sub) provider, and have them added to the language provider.
2. Then the DemoBook is registered using the BookProvider. Note that it is handed over the language provider cache we created in the first step.
   1. Additionally a DemoLeaflet is registered. A leaflet is a special book with just one entry and no categories. You can remove this line in your setup, unless you want to create a leaflet.
3. Finally, the language provider is registered, notice how it also receives the cache as parameter.
   1. It is important that your main mod language provider takes all contents of the cache and adds them to its output.
   2. To simplify this, make your language provider extend `AbstractModonomiconLanguageProvider`, like Modonomicon's [EnUsProvider](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/EnUsProvider.java) does. Then you can just leave your provider as-is.
4. Modonomicon Datagen then also registers a datagen for multiblocks which you can look into if you want to generate multiblock definitions for your book.
5. Further an ItemModelProvider is registered, which you can ignore, unless you want to add additional item models.

If you copied the demo book datagen classes you can just copy-paste most of the event method and let your IDE handle imports.

:::tip

You can also skip the language provider cache system. Instead:
1. Create your e.g. neoforge language provider and let it implement `ModonomiconLanguageProvider`. 
2. In it implement `accept(String, String)` to call `this.add(String, String)`.
3. Create an instance of your language provider in the data generator registration but don't register it yet.
4. Create and register your book sub provider and hand over your language provider.
5. Finally register your language provider.

:::

### DemoBook.java

This file contains code that generates metadata for the book and that calls the code that generates categories. 
We'll take a closer look at that soon.

### EnUsProvider.java

:::tip

Modpack creators can and should ignore this file.

::: 

"EnUS" corresponds to the "en_us" in the line `super(generator, modid, "en_us");` in the upper region of the file. 
This is the language code for the English language, meaning in this file we will generate English texts. 


:::warning

Please note that EnUsProvider depends on `AbstractModonomiconLanguageProvider` , not the Forge/Neo one! The Modonomicon version is multi platform and allows to "consume" another language provider as mentioned above regarding the cache. You can also use a child class of a platform specific language providers and simply implement the `ModonomiconLanguageProvider` interface.

:::

#### Optional: Additional Languages

It is recommended to do translations only in JSON files and use e.g. crowdin. 

If you want to translate in DataGen you can create additional language provider caches, and hand them over to the book sub provider ("DemoBook.java"). Then simply make your additional language providers also extend `AbstractModonomiconLanguageProvider` and hand over the fitting cache to them. 

You can then access the the other language providers in the book/category/entry providers with e.g. `this.add(this.lang("<LANG>"), "<KEY>", "<VALUE>");` where lang could be e.g. `ru_ru` or `fr_fr`, etc. This call allows you to access the modonomicon formatting helpers. 

To access language provider functions directly instead, you can also use `this.lang("<LANG>").add("<KEY>", "<VALUE>");`.
