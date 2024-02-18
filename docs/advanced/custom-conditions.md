---
sidebar_position: 40
---

# Custom Conditions

Mods can add custom conditions that can be used to lock entries or categories. To this end you need to:

1. Create ResourceLocation for the new condition (e.g. "mymod:my_condition")
2. Create a custom condition class
3. Register the custom condition 
4. For datagen: create a condition model class
5. Finally use the condition in your book

:::tip

The ResourceLocation is what you will use in your entries and categories to gate them behind your custom condition. 

:::

## Condition Class

Conditions need to extend `BookCondition` in the package `com.klikli_dev.modonomicon.book.conditions`.   
In addition to implementing the interface methods that your IDE will suggest you need two static methods `fromJson` and `fromNetwork` which you will need when registering the condition.

in `getType()` return your ResourceLocation.

See https://github.com/klikli-dev/modonomicon/blob/HEAD/common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookAdvancementCondition.java for an example condition.

## Condition Registration

To register the condition call LoaderRegistry.registerConditionLoader(...).   
For the BookAdvancementCondition this call looks as follows: `registerConditionLoader(Condition.ADVANCEMENT, BookAdvancementCondition::fromJson, BookAdvancementCondition::fromNetwork);`

This needs to be called from both the client and server side mod initialization.   
In Fabric: Call in `onInitialize` in your `ModInitializer`.   
In (Neo)Forge: Call in `onCommonSetup` (your `FMLCommonSetupEvent` handler).	

## Condition Datagen Model

The model class helps you to generate the condition json files via DataGen. 

See https://github.com/klikli-dev/modonomicon/blob/2a067357bacaf3e15a5f490520a4headaf64807fe83e/common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookAdvancementConditionModel.java for a reference model class.

(On 1.20.1 it looks a bit more complicated: 
https://github.com/klikli-dev/modonomicon/blob/bdf639c835908393198f695999c70f2ac53e154c/common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookAdvancementConditionModel.java )

:::tip 
You need not register the model.
::: 

## Usage

On your entry or category, add:

```json
{
  ...
  "condition": {
      "type": "mymod:my_condition"
  },
  ...
}
```	

See also [Unlock Conditions](../basics/unlock-conditions) for more information on how to use conditions.
