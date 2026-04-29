---
sidebar_position: 21
---

# Theme.json

`theme.json` defines the visual theme for a book. It is placed in `/data/<mod_id>/modonomicon/books/<book_id>/theme.json`.

If this file is missing, Modonomicon uses the default theme.

## Attributes

### **id** (ResourceLocation, _optional_)

Default value: `modonomicon:default`.

This is the **theme id**. It controls where themed sprite files are looked up.

For a theme id like `yourmod:eldritch`, Modonomicon looks for theme files in:

```text
/assets/yourmod/textures/gui/sprites/modonomicon/themes/eldritch/
```

If the id path contains folders, those folders are preserved.

### **type** (ResourceLocation, _optional_)

Default value: `modonomicon:default`.

This is the **theme type** (or theme class id).
It selects the Java theme implementation from the theme registry.

Use `modonomicon:default` for normal sprite-based custom themes.

See below [Advanced: Custom theme types](#advanced-custom-theme-types) for using custom theme types.

### **layout** (JSON Object, _optional_)

Controls layout offsets and icon scale.

#### **book_text_offset_x** (Integer, _optional_)
Default value: `0`.
Adds a left text offset.

#### **book_text_offset_y** (Integer, _optional_)
Default value: `0`.
Adds a top text offset.

#### **book_text_offset_width** (Integer, _optional_)
Default value: `0`.
Adjusts available text width.
Negative values reduce width.

#### **book_text_offset_height** (Integer, _optional_)
Default value: `0`.
Adjusts available text height.
Negative values reduce height.

#### **category_button_x_offset** (Integer, _optional_)
Default value: `0`.
Moves category buttons horizontally.

#### **category_button_y_offset** (Integer, _optional_)
Default value: `0`.
Moves category buttons vertically.

#### **search_button_x_offset** (Integer, _optional_)
Default value: `0`.
Moves the search button horizontally.

#### **search_button_y_offset** (Integer, _optional_)
Default value: `0`.
Moves the search button vertically.

#### **read_all_button_y_offset** (Integer, _optional_)
Default value: `0`.
Moves the read-all button vertically.

#### **category_button_icon_scale** (Float, _optional_)
Default value: `1.0`.
Scales category icons.

### **palette** (JSON Object, _optional_)

Controls theme colors.

#### **default_title_color** (Integer, _optional_)
Default value: `-16777216`.
The default title color used by the active theme.

#### **default_text_color** (Integer, _optional_)
Default value: `-16777216`.
The default text color used by the active theme.

:::tip

These are Java integer color values. Generated JSON commonly uses signed decimal values such as `-16777216` for black.

:::

## Usage Example

`/data/<mod_id>/modonomicon/books/<book_id>/theme.json`:

```json
{
  "id": "yourmod:eldritch",
  "type": "modonomicon:default",
  "layout": {
    "book_text_offset_x": 5,
    "book_text_offset_y": 0,
    "book_text_offset_width": -5,
    "book_text_offset_height": 0,
    "category_button_x_offset": 0,
    "category_button_y_offset": 0,
    "search_button_x_offset": 0,
    "search_button_y_offset": 0,
    "read_all_button_y_offset": 0,
    "category_button_icon_scale": 1.0
  },
  "palette": {
    "default_title_color": -1,
    "default_text_color": -16777216
  }
}
```

## Simple custom themes

For most custom themes you do **not** need a custom Java class.

Use this setup:

- set `id` to your own theme id
- set `type` to `modonomicon:default`
- place your theme textures in the folder matching that id

For example, with:

```json
{
  "id": "yourmod:eldritch",
  "type": "modonomicon:default"
}
```

place your override textures in:

```text
/assets/yourmod/textures/gui/sprites/modonomicon/themes/eldritch/
```

Example files inside that folder might look like:

```text
content/backgrounds/book/double_page_background.png
content/buttons/navigation/next_page_button_normal.png
content/buttons/navigation/next_page_button_hover.png
content/pages/recipes/crafting_grid.png
content/pages/recipes/crafting_slot.png
content/pages/recipes/crafting_arrow.png
frame/frame.png
frame/top_overlay.png
node/connections/right_arrow.png
```

Recipe page themes can also override dedicated crafting recipe sprites such as `content/pages/recipes/crafting_grid.png`, `content/pages/recipes/crafting_slot.png`, and `content/pages/recipes/crafting_arrow.png`.

### Partial overrides are supported

You do **not** need to provide every single file.

If a themed file is missing, Modonomicon falls back to the default built-in theme for that file.
So you can override only the files you care about and keep the default look for everything else.

This also means you can start with a tiny theme and expand it over time.

## Advanced: Custom theme types

If the default sprite-based theme is not enough, create your own `BookTheme` implementation and register it with a custom theme type.

Use a custom `type` in `theme.json`, for example:

```json
{
  "id": "yourmod:eldritch",
  "type": "yourmod:eldritch_theme"
}
```

Then register that type in Java:

```java
ThemeRegistry.registerTheme(
    Identifier.fromNamespaceAndPath("yourmod", "eldritch_theme"),
    EldritchBookTheme::new
);
```

Register it during common setup before books are used.

Use this option if you need behavior that cannot be expressed through the default sprite path conventions, layout values, and palette values alone. For example if you want to use any custom rendering logic, or sprites that are of a different size than the default theme sprites.
