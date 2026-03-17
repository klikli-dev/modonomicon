---
sidebar_position: 30
---

# Step 4: Test and extend the demo book

You can now test your datapack in game. An item named `book.modonomicon.demo.name` should be available in the Modonomicon creative tab and JEI/REI/EMI. 

When you first open the book, it will not have proper texts instead it will have contents like `book.modonomicon.demo.features.spotlight.description`.  
 
Both the weird book name and weird content texts happen because the demo book uses the minecraft translation system. You can either create a resource pack with a language file (you can copy individual lines or the entire file from https://github.com/klikli-dev/modonomicon/blob/version/1.21.1/neo/src/generated/resources/assets/modonomicon/lang/en_us.json), or you can directly type your desired text in the json files. 

:::tip
It is recommended to use the translation system, otherwise it is impossible for others to translate your book.
:::

**You can now edit the demo book to your liking to make your own book**.
Reference the [Basics](../../basics/) and [Advanced](../../advanced/) sections to learn about the valid JSON keys and structures.