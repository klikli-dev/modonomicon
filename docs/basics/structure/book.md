---
sidebar_position: 20
---

# Book.json

`book.json` defines the general settings for a book. It is placed in the `/data/<mod_id>/modonomicon/books/<book_id>/` folder.

## Attributes

* **name** (DescriptionId, _mandatory_)

The book name. Will not parse markdown.

* **tooltip** (DescriptionId, _optional_)

The book tooltip, shown when the item is hovered. Will not parse markdown.

* **generate_book_item** (Boolean, _optional_)

Default value: `true`. If true, a book item will be automatically generated for this book. If false, no book item will be generated and you need to create a Java item class that will open this book. 

<!-- TODO: Link to extending book guide and maybe occultism example -->

:::caution

if `true`, `model` needs to be set.   
if `false`, `custom_book_item` needs to be set.

:::


* **model** (ResourceLocation, _mandatory_ if `generate_book_item` is `true`)

Default value: `modonomicon:modonomicon_purple`. 
The ResourceLocation for the item model to use for the book item, if an item should be automatically generated.

Builtin always loaded models: 

* `modonomicon:modonomicon_purple`
* `modonomicon:modonomicon_blue`
* `modonomicon:modonomicon_green`
* `modonomicon:modonomicon_red`

:::caution

Beware: This cannot just be a texture, it must be a **loaded** model in `/assets/<mod_id>/models/item/<model_file>.json`. This means either it must be a model for an existing (potentially dummy-) item, which causes Forge to load the model, or it must be forced to load with e.g. `ForgeModelBakery#addSpecialModel`

:::

* **custom_book_item** (ResourceLocation, _mandatory_ if `generate_book_item` is `false`)

The ResourceLocation for the custom book item. This is your custom item instance that will open the book.

<!-- TODO: Link to extending book guide and maybe occultism example -->

* **creative_tab** (String, _optional_)

Default value: `misc`. The creative tab to place the automatically generated book item in. If `generate_book_item` is `false`, this setting is ignored.

* **default_title_color** (Integer, _optional_)

Default value: `0` (= black). The default title color for all entries in this book that are not markdown-styled. The color is specified as a **decimal RGB value**.

:::caution

JSON does not support hex values, so you need to convert them to decimal. For example, `#FF0000` is `16711680` in decimal.

:::

* **auto_add_read_conditions** (Boolean, _optional_)

Default value: `false`. If true, the book will automatically generate an "Entry Read Condition" for each entry in the book. This means that the entry will only be visible if the parent entry has been read. This is useful if you want to make sure that the player reads the book in order.   
See also [Unlock Conditions](../unlock-conditions) for details on Conditions and manually adding Conditions.

:::tip

Categories are not affected by this. Category conditions can only be added manually.

:::

* **book_overview_texture** (ResourceLocation, _optional_)

Default value: `modonomicon:textures/gui/book_overview.png`.   
The file for the category, search and read all button textures.   
The texture must be a **256x256** png file.   

* **frame_texture** (ResourceLocation, _optional_)

Default value: `modonomicon:textures/gui/book_frame.png`.   
The file for the main book frame texture. This book frame is rendered in such a way that the central part of each side is stretched to fit the screen.

* **\*_frame_overlay** (JSON Object, _optional_) 

Variants: `left_frame_overlay`, `right_frame_overlay`, `top_frame_overlay`, `bottom_frame_overlay`    
Default values are set up to render the default `modonomicon:textures/gui/book_frame_*_overlay.png` over the repeating center a of the frame texture.  

The frame overlay textures are used to render a non-repeating texture over the repeating center part of the frame texture. This is useful if you want to add details or ornaments to your frame.   

The texture must be a **256x256**, unless a different `texture_width` and `texture_height` are specified in the object.

Sample value: 

```JSON
"bottom_frame_overlay": {
    "frame_height": 8,
    "frame_width": 72,
    "frame_x_offset": 0,
    "frame_y_offset": -4,
    "texture": "modonomicon:textures/gui/book_frame_bottom_overlay.png",
    "texture_height": 256,
    "texture_width": 256
  },
```

:::tip

To disable rendering of a frame overlay entirely, set the `frame_height` and `frame_width` to `0`. 
In that case you should still supply a a valid texture property (note that omitting it is valid, but an empty string is not) in order to avoid issues during loading, but it will not be rendered.

::: 


* **book_content_texture** (ResourceLocation, _optional_)

Default value: `modonomicon:textures/gui/book_content.png`. The texture to use for rendering the pages in an entry, that is the book background, navigation buttons, title separator and image frames. 
The texture must be a **512x256** png file.

* **crafting_texture** (ResourceLocation, _optional_)

Default value: `modonomicon:textures/gui/crafting_textures.png`. The texture to use for crafting page elements such as crafting grids, slot borders and crafting arrows. The texture must be a **128x256** png file.

* **category_button_icon_scale** (Float, _optional_)

Default value: `1.0`   
The render scale for icons on the category buttons. This is useful when using non-default category button textures to make the icons fit better.

* **category_button_x_offset** (Integer, _optional_)

Default value: `0`  
Allows to move category buttons horizontally to make them fit with your custom frame texture.

* **category_button_y_offset** (Integer, _optional_)

Default value: `0`  
Allows to move the vertical render start position of category buttons up or down to make them fit with your custom frame texture.

* **search_button_x_offset** (Integer, _optional_)

Default value: `0`  
Allows to move the search button horizontally to make it fit with your custom frame texture.

* **search_button_y_offset** (Integer, _optional_)

Default value: `0`  
Allows to move the search button vertically to make it fit with your custom frame texture.

* **read_all_button_y_offset** (Integer, _optional_)

Default value: `0`  
Allows to move the read all button vertically to make it fit with your custom frame texture.   

:::info

A horizontal offset is currently not supported as it does not need to fit evenly to the side of the frame texture like the other buttons, and instead is centered over the frame. Feel free to request additional features if you have a use case for this!

:::


* **turn_page_sound** (ResourceLocation, _optional_)

Default value: `minecraft:turn_page`. The sound to play when turning a page. The sound must be a **loaded sound event** specified in `/assets/<mod_id>/sounds.json`.

## Usage Examples

`/data/<mod_id>/modonomicon/books/<book_id>/book.json`:

```json 
{
  "auto_add_read_conditions": false,
  "book_content_texture": "modonomicon:textures/gui/book_content.png",
  "book_overview_texture": "modonomicon:textures/gui/book_overview.png",
  "book_text_offset_width": -5,
  "book_text_offset_x": 5,
  "book_text_offset_y": 0,
  "bottom_frame_overlay": {
    "frame_height": 8,
    "frame_width": 72,
    "frame_x_offset": 0,
    "frame_y_offset": -4,
    "texture": "modonomicon:textures/gui/book_frame_bottom_overlay.png",
    "texture_height": 256,
    "texture_width": 256
  },
  "category_button_icon_scale": 1.0,
  "category_button_x_offset": 0,
  "category_button_y_offset": 0,
  "crafting_texture": "modonomicon:textures/gui/crafting_textures.png",
  "creative_tab": "modonomicon",
  "default_title_color": 0,
  "frame_texture": "modonomicon:textures/gui/book_frame.png",
  "generate_book_item": true,
  "left_frame_overlay": {
    "frame_height": 70,
    "frame_width": 7,
    "frame_x_offset": 3,
    "frame_y_offset": 0,
    "texture": "modonomicon:textures/gui/book_frame_left_overlay.png",
    "texture_height": 256,
    "texture_width": 256
  },
  "model": "modonomicon:modonomicon_purple",
  "name": "book.modonomicon.demo.name",
  "read_all_button_y_offset": 0,
  "right_frame_overlay": {
    "frame_height": 70,
    "frame_width": 8,
    "frame_x_offset": -4,
    "frame_y_offset": 0,
    "texture": "modonomicon:textures/gui/book_frame_right_overlay.png",
    "texture_height": 256,
    "texture_width": 256
  },
  "search_button_x_offset": 0,
  "search_button_y_offset": 0,
  "tooltip": "book.modonomicon.demo.tooltip",
  "top_frame_overlay": {
    "frame_height": 7,
    "frame_width": 72,
    "frame_x_offset": 0,
    "frame_y_offset": 4,
    "texture": "modonomicon:textures/gui/book_frame_top_overlay.png",
    "texture_height": 256,
    "texture_width": 256
  }
}
```