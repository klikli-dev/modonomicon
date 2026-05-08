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

### **node** (JSON Object, _optional_)

Controls how node-screen entry connections are rendered.

#### **connection_renderer** (String, _optional_)
Default value: `"sprite"`.

Selects which renderer should be used for node-screen connections.

Valid values:

- `sprite` — the default routed sprite-based connection renderer
- `direct` — the direct line renderer

#### **direct_connections** (JSON Object, _optional_)

Configures the direct line renderer.

#### **width** (Float, _optional_)
Default value: `2.25`.

Sets the line width used by direct connections.

#### **opacity** (Float, _optional_)
Default value: `1.0`.

Multiplies the alpha of direct connections after their state color is chosen.

#### **brightness** (Float, _optional_)
Default value: `1.75`.

Multiplies the RGB intensity of direct connections to improve visibility on darker backgrounds.

#### **oscillation** (Boolean, _optional_)
Default value: `true`.

Enables the animated offset effect for unsettled direct connections.

#### **oscillation_amplitude** (Float, _optional_)
Default value: `5.0`.

Controls the peak pixel offset used by the animated offset effect.

#### **oscillation_speed** (Float, _optional_)
Default value: `1.0`.

Controls how quickly the animated offset effect advances over time.

#### **connected_color** (Integer, _optional_)
Default value: `-1073741825`.

Base ARGB color for settled direct connections.

#### **available_color** (Integer, _optional_)
Default value: `-16711936`.

Base ARGB color used when the parent is available but the child is not yet settled.

#### **discovered_color** (Integer, _optional_)
Default value: `-16776961`.

Base ARGB color used when both ends are visible but not yet settled.

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
  },
  "node": {
    "connection_renderer": "direct",
    "direct_connections": {
      "width": 2.25,
      "opacity": 0.75,
      "brightness": 1.75,
      "oscillation": true,
      "oscillation_amplitude": 5.0,
      "oscillation_speed": 1.0,
      "connected_color": -1073741825,
      "available_color": -16711936,
      "discovered_color": -16776961
    }
  }
}
```

## Datagen Example

```java
new BookThemeModel()
    .withId(Identifier.fromNamespaceAndPath("yourmod", "eldritch"))
    .withType(ModonomiconConstants.Data.Theme.DEFAULT_THEME_TYPE)
    .withNode(node -> node
        .withConnectionRenderer(NodeConnectionRendererType.DIRECT)
        .withDirectConnections(directConnections -> directConnections
            .withWidth(2.25F)
            .withOpacity(0.75F)
            .withBrightness(1.75F)
            .withOscillation(true)
            .withOscillationAmplitude(5.0F)
            .withOscillationSpeed(1.0F)
            .withConnectedColor(0xBFFFFFFF)
            .withAvailableColor(0xFF00FF00)
            .withDiscoveredColor(0xFF0000FF)));
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

## FAQ

### My frame overlays are the right size, but they render in the wrong place. How do I fix that?

The default theme uses built-in overlay offsets that were tuned for the original frame overlay sprites.
If you replace `frame/top_overlay.png`, `frame/bottom_overlay.png`, `frame/left_overlay.png`, or `frame/right_overlay.png` with art that has different dimensions or padding, you may also need to adjust those offsets.

See [Frame overlay offsets in custom theme types](#frame-overlay-offsets-in-custom-theme-types) for what `frameXOffset` and `frameYOffset` mean.

For the current default frame overlay positioning, these are the offset values used:

```java
this.topOverlay = new GuiFrameOverlay(this.sprite("frame/top_overlay.png", -1, -1), 0, 8);
this.bottomOverlay = new GuiFrameOverlay(this.sprite("frame/bottom_overlay.png", -1, -1), 0, -10);
this.leftOverlay = new GuiFrameOverlay(this.sprite("frame/left_overlay.png", -1, -1), 8, 0);
this.rightOverlay = new GuiFrameOverlay(this.sprite("frame/right_overlay.png", -1, -1), -8, 0);
```

In practice, moving an overlay further inside the frame means:

- increase the top overlay `frameYOffset`
- decrease the bottom overlay `frameYOffset`
- increase the left overlay `frameXOffset`
- decrease the right overlay `frameXOffset`

If you need offsets different from the built-in default theme behavior, create a custom `BookTheme` implementation or extend the default one and override the `GuiFrameOverlay` setup.

## Frame overlay offsets in custom theme types

`GuiFrameOverlay` is defined as:

```java
public record GuiFrameOverlay(GuiSprite sprite, int frameXOffset, int frameYOffset)
```

- `frameXOffset` moves the overlay horizontally relative to its anchored frame edge
- `frameYOffset` moves the overlay vertically relative to its anchored frame edge

For the built-in frame anchors this means:

- top overlay: larger `frameYOffset` moves it down
- bottom overlay: smaller `frameYOffset` moves it up
- left overlay: larger `frameXOffset` moves it right
- right overlay: smaller `frameXOffset` moves it left

Inside a custom theme implementation, overlay offsets are defined when constructing `GuiFrameOverlay` instances:

```java
GuiFrameOverlay topOverlay = new GuiFrameOverlay(topSprite, 0, 8);
GuiFrameOverlay bottomOverlay = new GuiFrameOverlay(bottomSprite, 0, -10);
GuiFrameOverlay leftOverlay = new GuiFrameOverlay(leftSprite, 8, 0);
GuiFrameOverlay rightOverlay = new GuiFrameOverlay(rightSprite, -8, 0);
```

Brief datagen example for using that custom theme type:

```java
new BookThemeModel()
    .withId(Identifier.fromNamespaceAndPath("yourmod", "eldritch"))
    .withType(Identifier.fromNamespaceAndPath("yourmod", "eldritch_theme"));
```

The datagen model selects the custom theme type. The actual overlay offsets are defined in that theme class, not in `theme.json`.

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
