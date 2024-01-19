---
sidebar_position: 30
---

# Categories

Categories are defined in json files placed in the `/data/<mod_id>/modonomicons/<book_id>/categories/` folder. 

## Attributes

### **name** (DescriptionId, _mandatory_)

The category name. Will not parse markdown.

### **icon** (ResourceLocation, _mandatory_)

**Either** an item/block ResourceLocation that should be used as icon. E.g.:  `minecraft:nether_star` or `minecraft:chest`.  
**Or** the ResourceLocation to a texture. The texture must be 16x16 pixels. E.g.:  `modonomicon:textures/gui/some_random_icon.png`. 

:::tip

To use a texture make sure the ResourceLocation includes the file endinge `.png` as seen in the example above.

::: 

### **sort_number** (Integer, _optional_)

Defaults to `-1`.   
Category "Bookmark"-Buttos on the left side of the Book will be sorted by this number.

### **condition** (Condition, _optional_)

Categories, like Entries, can be hidden until an Unlock Condition is fulfilled. Conditions are JSON objects.  
See **[Unlock Conditions](../unlock-conditions)** for details.

### **background** (ResourceLocation, _optional_)

Defaults to `modonomicon:textures/gui/dark_slate_seamless.png`.   
The ResourceLocation for the Background texture to use for this category. The texture must be 512px by 512px.


### **background_parallax_layers** (JSON Array of JSON Objects, _optional_)

If any parallax layers are supplied, the `background` property will be ignored.   

Parallax layers allow a multi-layered background with a parallax effect. That means, the textures supplied here likely will feature transparent elements, however the first layer should be fully opaque to avoid visual artifacts.   

Sample Value: 

```json
"background_parallax_layers": [
    {
      "background": "modonomicon:textures/gui/parallax/flow/base.png",
      "speed": 0.7
    },
    {
      "background": "modonomicon:textures/gui/parallax/flow/1.png",
      "speed": 1.0
    },
    {
      "background": "modonomicon:textures/gui/parallax/flow/2.png",
      "speed": 1.4,
      "vanish_zoom": 0.9
    }
  ],
```

### **background_height** (Integer, _optional_)

Default value: `512`   
The height of the background texture. Applies both to the `background` property as well as the `background_parallax_layers` property.

### **background_width** (Integer, _optional_)

Default value: `512`   
The width of the background texture. Applies both to the `background` property as well as the `background_parallax_layers` property.

### **background_texture_zoom_multiplier** (Float, _optional_)

Default value: `1.0`
Allows to modify how "zoomed in" the background texture is rendered.    
A lower value means the texture is zoomed OUT more -> it is sharper / less blurry.    
This is especially useful for textures larger than 512x512px, as they might end up looking blurry otherwise.   
Make sure to use seamless textures as the texture may be repeated (especially horizontally) to fill the screen.

### **entry_textures** (ResourceLocation, _optional_)

Defaults to `modonomicon:textures/gui/entry_textures.png`.   
The ResourceLocation for the Entry textures to use for this category. The texture must be 512px by 512px.   
Entry textures govern how the Entry background behind the Icon as well as the arrows connecting entries look.   

If you want to use a custom texture, make sure to copy the default file from [`/assets/modonomicon/textures/gui/entry_textures.png`](https://github.com/klikli-dev/modonomicon/blob/version/1.19/src/main/resources/assets/modonomicon/textures/gui/entry_textures.png) and modify it in order to preserve the UV coordinates of all parts.

### **show_category_button** (Boolean, _optional_)

Defaults to `true`.   
If false, the book overview screen will not show a button/bookmark for this category. 

:::tip

This is intended to be used with an entry that links to this category to effectively create "sub-categories". See also **[Entries](./entries)** for the `category_to_open` attribute.

:::


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