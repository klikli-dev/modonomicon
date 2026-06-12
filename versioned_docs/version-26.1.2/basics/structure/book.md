---
sidebar_position: 20
---

# Book.json

`book.json` defines the content, behavior, and non-visual settings for a book. It is placed in `/data/<mod_id>/modonomicon/books/<book_id>/book.json`.

:::info

Book appearance is no longer configured in `book.json`.

Things like colors, text offsets, frame styling, page textures, and other theme-related settings now live in **[theme.json](./theme)**.

:::

## Attributes

### **name** (DescriptionId, _mandatory_)

The book name. Will not parse markdown.

### **description** (DescriptionId or Component JSON, _optional_)

The book description. Can be styled using markdown.
Will be displayed on the first page when opening the book if it is in index mode.

### **display_mode** (String, _optional_)

Default value: `node`.
The display mode of the book. Can be `node` or `index`.

Index mode ("patchouli-style") displays all categories in a list.
Node mode ("thaumonomicon-style") displays a tree / quest / progress view of the book.
A book in node mode can still contain categories in index mode.

### **tooltip** (DescriptionId, _optional_)

The book tooltip, shown when the item is hovered. Will not parse markdown.

### **generate_book_item** (Boolean, _optional_)

Default value: `true`.
If true, a book item will automatically be generated for this book.
If false, no book item will be generated and you need to provide a custom item that opens this book.

:::caution

If `generate_book_item` is `false`, `custom_book_item` needs to be set.

:::

### **model** (Identifier, _optional_)

Default value: `modonomicon:modonomicon_purple`.
The item model to use for the generated book item.

Builtin always-loaded models:

- `modonomicon:modonomicon_purple`
- `modonomicon:modonomicon_blue`
- `modonomicon:modonomicon_green`
- `modonomicon:modonomicon_red`

:::caution

This cannot just be a texture. It must be a **loaded** model in `/assets/<mod_id>/models/item/<model_file>.json`.
That means it either needs to belong to an existing item, or it must be explicitly loaded by your platform setup.

:::

### **custom_book_item** (Identifier, _mandatory_ if `generate_book_item` is `false`)

The Identifier for your custom book item.
This is your custom item instance that will open the book.

### **creative_tab** (String, _optional_)

Default value: `misc`.
The creative tab for the automatically generated book item.
Ignored if `generate_book_item` is `false`.

### **font** (Identifier, _optional_)

Default value: `modonomicon:default`.
The font to use in the book.
Must be a valid and loaded Minecraft font.

:::tip

The default font is based on https://github.com/latvian-dev/slightly-improved-font and is more readable than `minecraft:default`.

:::

### **page_display_mode** (String, _optional_)

Default value: `double_page`.
Can be `single_page` or `double_page`.

This controls how entries are rendered.
Categories and books in index mode always use double page display.

### **turn_page_sound** (Identifier, _optional_)

Default value: `minecraft:turn_page`.
The sound event to play when turning a page.
The sound must be a loaded sound event defined in `/assets/<mod_id>/sounds.json`.

### **leaflet_entry** (Identifier, _optional_)

If set, the book will ignore its normal navigation structure and directly display this one entry.
The entry still needs to be part of a valid category, even if that category is not shown.

Leaflets are treated like index-mode books.

See also [Leaflets](/docs/advanced/leaflets).

### **allow_open_book_with_invalid_links** (Boolean, _optional_)

Default value: `false`.

If set to `true`, invalid links do not stop the book from opening.
Instead, the broken link is rendered in red with a tooltip explaining that it is invalid.

This is mainly useful when translations may temporarily reference outdated entry ids.

### **show_recently_unlocked** (Boolean, _optional_)

Default value: `true`.

If set to `true`, a **Recently Unlocked** button is shown in the book sidebar.
It opens a paginated view of unlocked entries, with unread entries first and then by most recent unlock time.

Set it to `false` to hide the button.

## Usage Example

`/data/<mod_id>/modonomicon/books/<book_id>/book.json`:

```json
{
  "name": "book.example.name",
  "description": "book.example.description",
  "tooltip": "book.example.tooltip",
  "display_mode": "node",
  "generate_book_item": true,
  "model": "modonomicon:modonomicon_purple",
  "creative_tab": "misc",
  "font": "modonomicon:default",
  "page_display_mode": "double_page",
  "turn_page_sound": "minecraft:turn_page",
  "allow_open_book_with_invalid_links": false,
  "show_recently_unlocked": true
}
```
