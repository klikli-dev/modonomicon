---
sidebar_position: 60
---

# Item Properties

Item properties let item models change depending on runtime conditions. Modonomicon exposes a property you can use to change a book item's model depending on whether the Modonomicon UI is open for the player.

## Open state

### 1) Custom book item requirement

This only works when using a custom book item (an item you register yourself). It does *not* work with the automatically generated book item.

### 2) Java helper / Occultism example

Occultism demonstrates a common approach: their [GuideBookItem](https://github.com/klikli-dev/occultism/blob/version/1.21.1/src/main/java/com/klikli_dev/occultism/common/item/tool/GuideBookItem.java) shows how to use `ModonomiconCustomItemBase` to obtain the same default Modonomicon behavior on your own item. `ModonomiconCustomItemBase` handles the DataComponents and registers the item property for you, which is why using it is recommended.

:::tip
Tip: extending `ModonomiconCustomItemBase` is the easiest route because it wires up the required DataComponents handling and property registration automatically.
:::

### 3) Registering the item

Register your custom item as you would any other item. For reference see Occultism's item registration (their [OccultismItems](https://github.com/klikli-dev/occultism/blob/version/1.21.1/src/main/java/com/klikli_dev/occultism/registry/OccultismItems.java#L55) class) — the book item is just a normal item registration.

### 4) Disable auto-generated book and datagen

If you normally generate a book item from datagen, disable that generation and tell Modonomicon about your custom item. In `book.json` set:

```json
"generate_book_item": false,
"custom_book_item": "<your_item_id>"
```

Or in datagen via the `BookModel` helpers:

```java
.withGenerateBookItem(false)
.withCustomBookItem(this.modLoc("<your_item_id>"))
```

### 5) Item model: changing the model when open

Modonomicon exposes the item property `modonomicon:open_state`. The value is `1.0` while the Modonomicon UI is open for the player and `0.0` otherwise. Use that predicate in your item model `overrides` to change model when the book is open. The following example shows a green book when closed and a purple book when open:

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "modonomicon:item/modonomicon_green"
  },
  "overrides": [
    {
      "predicate": {
        "modonomicon:open_state": 1.0
      },
      "model": "modonomicon:item/modonomicon_purple"
    }
  ]
}
```

:::info
See also https://minecraft.wiki/w/Tutorial:Models#Item_models and https://docs.minecraftforge.net/en/1.20.1/resources/client/models/itemproperties/
:::

:::tip
This example points at the `modonomicon:item/modonomicon_purple` model shipped by Modonomicon. If you want to use your own open-state model make sure the model JSON is present and loaded (register the model file or force-load it). A texture alone is not enough — Minecraft loads models, not raw textures.
:::