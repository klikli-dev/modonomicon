---
sidebar_position: 40
toc_max_heading_level: 5
---

# Entries

Entries are defined in json files placed in the `/data/<mod_id>/modonomicon/books/<book_id>/entries/<category_id>/` folder. 

## Attributes

### **category** (ResourceLocation, _mandatory_)

The ResourceLocation of the category this entry should be placed in. 

:::tip

The file hierarchy of the entry's json file does not actually assign the entry to the category, only this field does!

::: 

### **name** (DescriptionId, _mandatory_)

The entry name, will be shown in **bold** when hovering over the Entry. Will not parse markdown.

### **description** (DescriptionId, _optional_)

The entry description, will be shown below the name when hovering over the Entry. Will not parse markdown.

### **icon** (ResourceLocation, _mandatory_)

**Either** an item/block ResourceLocation that should be used as icon. E.g.:  `minecraft:nether_star` or `minecraft:chest`.  
**Or** the ResourceLocation to a texture. The texture must be 16x16 pixels. E.g.:  `modonomicon:textures/gui/some_random_icon.png`. 

:::tip

To use a texture make sure the ResourceLocation includes the file endinge `.png` as seen in the example above.

::: 

### **x** (Integer, _mandatory_)

The x coordinate (horizontal) of the entry in the category.

### **y** (Integer, _mandatory_)

The y coordinate (vertical) of the entry in the category.

### **sort_number** (Integer, _optional_)
Defaults to `-1`.

If the category of this entry is in "index" mode, then the sort number will be used to order the entries (instead of using x/y to place the entry on the 2d node grid).   
When using datagen and no sort nubmer is provided, the CategorProvider will automatically assign a sort number based on the order the entries are added when using `.add()`.

### **hide_while_locked** (Boolean, _optional_)

Default value: `false`. If true, this entry will not be shown as greyed out while it is locked, instead it will be entirely hidden.

:::tip

Usually the default value is what you want. Regardless of this setting, locked entries will be hidden fully until the entry just before them (= the immediate parent) becomes unlocked, and only then show greyed out. `true` will cause the entry to stay hidden even then.

::: 

### **show_when_any_parent_unlocked** (Boolean, _optional_)

Default value: `false`.  
If true, the entry will show (locked) as soon as any **one** parent is unlocked.
If false, the entry will only show (locked) as soon as **all** parents are unlocked.

:::tip

If you want this true or false depends on how "discoverable" you want your book to be. If `true` the entry will be visible to the player earlier, and the tooltip will indicate that they will need another entry to unlock it.  
If `false` the entry will only show once all parents of it are unlocked, effectively making the player aware much later.

::: 

### **background** (Sprite JSON Object, _optional_)

Defines the sprite used as the entry background.

Default value:

```json
{
  "sprite": "modonomicon:modonomicon/themes/default/node/entry_backgrounds/square_gold",
  "width": 26,
  "height": 26
}
```

#### Attributes

- `sprite` (ResourceLocation, mandatory): The sprite to use for the entry background.
- `width` (Integer, optional): The background width. If omitted, defaults to `-1`.
- `height` (Integer, optional): The background height. If omitted, defaults to `-1`.

As a shorthand, `background` can also be a single ResourceLocation string instead of an object. In that case it is treated as the `sprite` value and `width` / `height` default to `-1`.

#### Default datagen entry backgrounds

When generating entries in code, [`BookEntryModel#withEntryBackground(...)`](https://github.com/klikli-dev/modonomicon/blob/main/common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/BookEntryModel.java) accepts any `GuiSprite`, but the default theme also exposes convenience constants in `EntryBackground` for the built-in node entry backgrounds:

- `EntryBackground.SQUARE_GOLD`
- `EntryBackground.SQUARE_GRAY`
- `EntryBackground.SQUARE_PURPLE`
- `EntryBackground.STAR_GOLD`
- `EntryBackground.STAR_GRAY`
- `EntryBackground.STAR_PURPLE`
- `EntryBackground.CIRCLE_GOLD`
- `EntryBackground.CIRCLE_GRAY`
- `EntryBackground.CIRCLE_PURPLE`
- `EntryBackground.HEXAGON_GOLD`
- `EntryBackground.HEXAGON_GRAY`
- `EntryBackground.HEXAGON_PURPLE`

Example:

```java
BookEntryModel.create(id("my_entry"), "example.book.my_entry")
    .withCategory(category)
    .withIcon(Items.BOOK)
    .withLocation(0, 0)
    .withEntryBackground(EntryBackground.HEXAGON_PURPLE);
```

`EntryBackground.DEFAULT` still points to `EntryBackground.SQUARE_GOLD`.

### **condition** (Condition, _optional_)

Entries, like Categories, can be hidden until an Unlock Condition is fulfilled. Conditions are JSON objects.  
See **[Unlock Conditions](../unlock-conditions)** for details.

### **parents** (Parent[], _optional_)

Entry Parents are JSON Objects that define Entries this Entry should be connected to. See [Parents](#parents) for details.
A parent connection does not imply an unlock condition or any logical dependency, by default it is just a visual connection, however it can be used to automatically define unlock conditions. See `auto_add_read_conditions` in [Book.json](../structure/book#attributes).

<!-- TODO: link to the book setting that creates read connections -->

### **pages** (Page[], _optional_)

Pages are JSON Objects that define the actual book content. See [Page Types](../page-types/page-types.md) for the available types of pages.

### **category_to_open** (ResourceLocation, _optional_)

The resource location to the category that should be opened when this entry is clicked. 
If this is set, the entry will never show it's pages, but instead open the category directly.

:::tip

This allows to create "sub-category" that does not show up in the category navigation menu, if combined with `"show_category_button" : false` setting for the target category. See also [Categories](./categories#attributes).

::: 

### **command_to_run_on_first_read** (ResourceLocation, _optional_)

The resource location to the command that should be opened when this entry is read/opened for the first time.
See [Commands](./commands.md) on how to create the command.

:::tip

This can be used to e.g. give rewards to players for reaching a certain part of the book.

::: 

### **entry_to_open** (ResourceLocation, _optional_)

The resource location to the entry that should be opened when this entry is clicked. This allows to place one entry in multiple categories by referring to it multiple times.
If this is set, the entry will never show it's pages, but instead open the target entry.

## Parents 

Entry Parents define which entry comes visually before this entry in the book, as in, which entry points an arrow at this entry.

### Attributes

#### **entry** (ResourceLocation, _mandatory_)

The ResourceLocation of the parent entry. This is the main attribute.

#### **draw_arrow** (Boolean, _optional_)

Default value: `true`. If false, the line connecting parent and this entry will **not** have an arrow at the end.

#### **line_enabled** (Boolean, _optional_)

Default value: `true`. If false, there will be no connecting line. This is useful if you want to use the parent connection to define an automatic unlock condition, but don't want to show the line. <!-- TODO: link to the book setting that creates read connections -->

#### **line_reversed** (Boolean, _optional_)

Default value: `false`.  
If true, the line will be drawn in reverse, which means the curve is mirrored. This allows you to determine if the curve goes left or right / up or down to get nice symmetrical graphs.
This has no effect on straight lines.   
This does not affect the direction of the arrow.

## Usage Examples

```json 
{
  "name": "multiblock",
  "description": "modonomicon.test.entries.test_category.multiblock.description",
  "icon": "minecraft:chest",
  "x": 6,
  "y": 2,
  "category": "modonomicon:test_category",
  "pages": [
    {
      "type": "modonomicon:text",
      ...
    },
    {
      "type": "modonomicon:multiblock",
      ...
    }
  ],
  "parents": [
    {
      "entry": "modonomicon:test_category/test_entry"
    }
  ]
}
```
