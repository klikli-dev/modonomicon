---
sidebar_position: 20
---

# Book.json

`book.json` defines the general settings for a book. It is placed in the `/data/<mod_id>/modonomicons/<book_id>/` folder.

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
See also [Unlock Conditions](../unlock-conditions/unlock-conditions) for details on Conditions and manually adding Conditions.

* **book_overview_texture** (ResourceLocation, _optional_)

Default value: `modonomicon:textures/gui/book_overview.png`. The texture to use for the book overview page, that is the page that shows the current category, with a book border and the "bookmark" texture for bookmark buttons.   
The texture must be a **256x256** png file.

* **book_content_texture** (ResourceLocation, _optional_)

Default value: `modonomicon:textures/gui/book_content.png`. The texture to use for rendering the pages in an entry, that is the book background, navigation buttons, title separator and image frames. The texture must be a **512x256** png file.

* **crafting_texture** (ResourceLocation, _optional_)

Default value: `modonomicon:textures/gui/crafting_textures.png`. The texture to use for crafting page elements such as crafting grids, slot borders and crafting arrows. The texture must be a **128x256** png file.

* **turn_page_sound** (ResourceLocation, _optional_)

Default value: `minecraft:turn_page`. The sound to play when turning a page. The sound must be a **loaded sound event** specified in `/assets/<mod_id>/sounds.json`.

## Usage Examples

`/data/<mod_id>/modonomicons/<book_id>/book.json`:

```json 
{
  "auto_add_read_conditions": true,
  "book_content_texture": "modonomicon:textures/gui/book_content.png",
  "book_overview_texture": "modonomicon:textures/gui/book_overview.png",
  "crafting_texture": "occultism:textures/gui/book/crafting_textures.png",
  "creative_tab": "misc",
  "default_title_color": 0,
  "generate_book_item": true,
  //in the case of occultism, this is a dummy item with corresponding model that is not used in game
  "model": "occultism:dictionary_of_spirits_icon", 
  "name": "book.occultism.dictionary_of_spirits.name",
  "tooltip": "book.occultism.dictionary_of_spirits.tooltip"
}
```