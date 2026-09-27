---
sidebar_position: 30
---

# Categories

Categories are defined in json files placed in the `/data/<mod_id>/modonomicon/books/<book_id>/categories/` folder. 

## Attributes

### **name** (DescriptionId, _mandatory_)

The category name. Will not parse markdown.

### **description** (DescriptionId or Component JSON, _optional_)

The category description. Can be styled using markdown.
Will be displayed on the first page when opening the category if it is in index mode.

### **display_mode** (String, _optional_)

Default value: `node`. The display mode of the category. Can be `node` or `index`.
Index mode ("patchouli-style") will display all entries in a list. 
Node mode ("thaumonomicon-style") will display a "tree/quest/progress" view of the entries.  

The category can be in "index" mode, despite the book being in "node" mode if you have contents that are better suited for a list.   
In this case the styling attributes, such as the background (see below) are still applied, but only to provide a consistent look for the book rendered behind the category view.

### **icon** (Identifier, _mandatory_)

**Either** an item/block Identifier that should be used as icon. E.g.:  `minecraft:nether_star` or `minecraft:chest`.  
**Or** the Identifier to a texture. The texture must be 16x16 pixels. E.g.:  `modonomicon:textures/gui/some_random_icon.png`. 

:::tip

To use a texture make sure the Identifier includes the file ending `.png` as seen in the example above.

::: 

### **sort_number** (Integer, _optional_)

Defaults to `-1`.   
Category "Bookmark"-Buttos on the left side of the Book will be sorted by this number.
Similarly, in index mode, the categories will be sorted in the list by this number.

When using datagen and no sort number is provided, the BookProvider will automatically assign a sort number based on the order the categories are added when using `.add()`.

### **condition** (Condition, _optional_)

Categories, like Entries, can be hidden until an Unlock Condition is fulfilled. Conditions are JSON objects.  
See **[Unlock Conditions](../unlock-conditions)** for details.

### **background** (Identifier, _optional_)

Defaults to `modonomicon:textures/gui/dark_slate_seamless.png`.   
The Identifier for the Background texture to use for this category. The texture must be 512px by 512px.


### **background_rendering_mode** (String, _optional_)

Default value: `repeat`. Configures how the category background texture is rendered. Can be `repeat`, `scale` or `fit`.
Only applies if no `background_parallax_layers` are set (each parallax layer has its own rendering mode, see below).

- `repeat`: the texture is tiled to fill the background area. Best for seamless textures. Note that tiling reacts to the gui scale setting.
- `scale`: the texture is stretched to exactly fill the background area, ignoring the original aspect ratio.
- `fit`: the texture is uniformly scaled to cover the entire background area without distortion. Parts of the texture may be cropped.

`scale` and `fit` look the same at any gui scale. By default they pan with scrolling (see `background_overscan`).

### **background_parallax_layers** (JSON Array of JSON Objects, _optional_)

If any parallax layers are supplied, the `background` property will be ignored.   

Parallax layers allow a multi-layered background with a parallax effect. That means, the textures supplied here likely will feature transparent elements, however the first layer should be fully opaque to avoid visual artifacts.   

Each layer supports the following properties:

- `background` (Identifier, _mandatory_): the layer texture.
- `speed` (Float, _optional_, default `0.5`): how fast the layer pans with scrolling, relative to the other layers.
- `vanish_zoom` (Float, _optional_, default `-1`): the category zoom level at which the layer vanishes. `-1` means it never vanishes.
- `background_rendering_mode` (String, _optional_, default `repeat`): `repeat`, `scale` or `fit`, behaving as described above. Non-tiling layers pan with scrolling inside their overscan margin instead of wrapping, proportionally to their speed.
- `background_overscan` (Float, _optional_, default `1.1`): overdraw margin for non-tiling layers, see below. Ignored for `repeat` layers.

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
      "vanish_zoom": 0.9,
      "background_rendering_mode": "fit",
      "background_overscan": 1.25
    }
  ],
```

### **background_height** (Integer, _optional_)

Default value: `512`   
The height of the background texture. Applies both to the `background` property as well as the `background_parallax_layers` property.

### **background_width** (Integer, _optional_)

Default value: `512`   
The width of the background texture. Applies both to the `background` property as well as the `background_parallax_layers` property.


### **max_scroll_x** (Integer, _optional_)

Default value: `512`
The maximum horizontal scroll distance in this category.

### **max_scroll_y** (Integer, _optional_)

Default value: `512`
The maximum vertical scroll distance in this category.

### **background_texture_zoom_multiplier** (Float, _optional_)

Default value: `1.0`
Allows to modify how "zoomed in" the background texture is rendered.    
A lower value means the texture is zoomed OUT more -> it is sharper / less blurry.    
This is especially useful for textures larger than 512x512px, as they might end up looking blurry otherwise.   
Make sure to use seamless textures as the texture may be repeated (especially horizontally) to fill the screen.
Only applies to tiling backgrounds, i.e. `repeat` mode (or `repeat` parallax layers).

### **background_overscan** (Float, _optional_)

Default value: `1.1`
Uniform extra zoom applied to `scale` and `fit` backgrounds (and parallax layers), rendering them larger than the background area.
The resulting margin allows the background to pan with scrolling, creating a parallax effect.
`1.0` disables panning and renders a fixed backdrop.
Ignored for `repeat` mode (and `repeat` parallax layers), as those already pan by tiling.

### **show_category_button** (Boolean, _optional_)

Defaults to `true`.   
If false, the book overview screen will not show a button/bookmark for this category. 

:::tip

This is intended to be used with an entry that links to this category to effectively create "sub-categories". See also **[Entries](./entries)** for the `category_to_open` attribute.

:::

### **category_button_sprite** (Sprite JSON Object or Button Sprite Object, _optional_)

Overrides the button sprite used for this category in the book sidebar / overview.
If omitted, the active theme's `content.default_category_button_sprite` is used, or the built-in default theme sprite if the theme does not override it.

You can provide either:

- a single sprite JSON object, used as the normal and hover sprite
- or an object with `normal`, optional `hover`, and optional `pressed` sprite JSON objects

Single-sprite example:

```json
"category_button_sprite": {
  "sprite": "yourmod:modonomicon/themes/eldritch/content/buttons/category_button",
  "width": 24,
  "height": 24
}
```

Multi-state example:

```json
"category_button_sprite": {
  "normal": {
    "sprite": "yourmod:modonomicon/themes/eldritch/content/buttons/category_button_normal",
    "width": 24,
    "height": 24
  },
  "hover": {
    "sprite": "yourmod:modonomicon/themes/eldritch/content/buttons/category_button_hover",
    "width": 24,
    "height": 24
  },
  "pressed": {
    "sprite": "yourmod:modonomicon/themes/eldritch/content/buttons/category_button_pressed",
    "width": 24,
    "height": 24
  }
}
```

### **entry_to_open** (Identifier, _optional_)

The entry to directly open when this category is opened. If not set, no entry will be opened.

### **open_entry_to_open_only_once** (Boolean, _optional_)
Defaults to `true`.

If true, the entry_to_open will only be opened the first time the category is opened. If false, the entry_to_open will be opened every time the category is opened.


## Usage Examples

`/data/<mod_id>/modonomicon/books/<book_id>/categories/features.json`:

```json 
{
  "background": "modonomicon:textures/gui/dark_slate_seamless.png",
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
