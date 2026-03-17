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

### 5) Client item: changing the model when open

Modonomicon exposes the ConditionalItemModelProperty `modonomicon:is_book_open`. The value is `true` while the Modonomicon UI is open for the player and `false` otherwise. Use that in your client item to change model when the book is open. The following example shows a green book when closed and a purple book when open:

```json
{
  "model": {
    "type": "minecraft:condition",

    "property": "modonomicon:is_book_open",

    "on_true": {
      "type": "minecraft:model",
      "model": "modonomicon:item/modonomicon_purple"
    },
    "on_false": {
      "type": "minecraft:model",
      "model": "modonomicon:item/modonomicon_green"
    }
  }
}
```

:::info
See also https://docs.neoforged.net/docs/1.21.4/resources/client/models/items
:::

:::tip
This example points at the `modonomicon:item/modonomicon_purple` and `modonomicon:item/modonomicon_green` models shipped by Modonomicon. If you want to use your own open-state model make sure the model JSON is present and loaded (register the model file or force-load it). A texture alone is not enough — Minecraft loads models, not raw textures.
:::