---
sidebar_position: 30
---

# Categories

Categories are defined in json files placed in the `/data/<mod_id>/modonomicons/<book_id>/categories/` folder. 

## Attributes

* **name** (DescriptionId, _mandatory_)

The category name. Will not parse markdown.

* **icon** (ResourceLocation, _mandatory_)

**Either** an item/block ResourceLocation that should be used as icon. E.g.:  `minecraft:nether_star` or `minecraft:chest`.  
**Or** the ResourceLocation to a texture. The texture must be 16x16 pixels. E.g.:  `modonomicon:textures/gui/some_random_icon.png`. 

:::tip

To use a texture make sure the ResourceLocation includes the file endinge `.png` as seen in the example above.

::: 

* **sort_number** (Integer, _optional_)

Defaults to `-1`.   
Category "Bookmark"-Buttos on the left side of the Book will be sorted by this number.

* **condition** (Condition, _optional_)

Categories, like Entries, can be hidden until an Unlock Condition is fulfilled. Conditions are JSON objects.  
See [Unlock Conditions](../unlock-conditions) for details.

* **background** (ResourceLocation, _optional_)

Defaults to `modonomicon:textures/gui/dark_slate_seamless.png`.   
The ResourceLocation for the Background texture to use for this category. The texture must be 512px by 512px.

* **entry_textures** (ResourceLocation, _optional_)

Defaults to `modonomicon:textures/gui/entry_textures.png`.   
The ResourceLocation for the Entry textures to use for this category. The texture must be 512px by 512px.   
Entry textures govern how the Entry background behind the Icon as well as the arrows connecting entries look.   

If you want to use a custom texture, make sure to copy the default file from [`/assets/modonomicon/textures/gui/entry_textures.png`](https://github.com/klikli-dev/modonomicon/blob/version/1.19/src/main/resources/assets/modonomicon/textures/gui/entry_textures.png) and modify it in order to preserve the UV coordinates of all parts.

## Usage Examples

`/data/<mod_id>/modonomicons/<book_id>/categories/features.json`:

```json 
{
  "background": "modonomicon:textures/gui/dark_slate_seamless.png",
  "entry_textures": "modonomicon:textures/gui/entry_textures.png",
  "icon": "minecraft:nether_star",
  "name": "book.modonomicon.demo.features.name",
  "sort_number": -1
}
```

`/lang/*.json`:
```json
{
    "book.modonomicon.demo.features.name": "Features Category",
}
```