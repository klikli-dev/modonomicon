---
sidebar_position: 40
---

# Entries

Entries are defined in json files placed in the `/data/<mod_id>/modonomicons/<book_id>/entries/<category_id>/` folder. 

## Attributes

* **category** (ResourceLocation, _mandatory_)

The ResourceLocation of the category this entry should be placed in. 

:::tip

The file hierarchy of the entry's json file does not actually assign the entry to the category, only this field does!

::: 

* **name** (DescriptionId, _mandatory_)

The entry name, will be shown in **bold** when hovering over the Entry. Will not parse markdown.

* **description** (DescriptionId, _optional_)

The entry description, will be shown below the name when hovering over the Entry. Will not parse markdown.

* **icon** (ResourceLocation, _mandatory_)

**Either** an item/block ResourceLocation that should be used as icon. E.g.:  `minecraft:nether_star` or `minecraft:chest`.  
**Or** the ResourceLocation to a texture. The texture must be 16x16 pixels. E.g.:  `modonomicon:textures/gui/some_random_icon.png`. 

:::tip

To use a texture make sure the ResourceLocation includes the file endinge `.png` as seen in the example above.

::: 

* **x** (Integer, _mandatory_)

The x coordinate (horizontal) of the entry in the category.

* **y** (Integer, _mandatory_)

The y coordinate (vertical) of the entry in the category.

* **hide_while_locked** (Boolean, _optional_)

Default value: `false`. If true, this entry will not be shown as greyed out while it is locked, instead it will be entirely hidden.

:::tip

Usually the default value is what you want. Regardless of this setting, locked entries will be hidden fully until the entry just before them (= the immediate parent) becomes unlocked, and only then show greyed out. `true` will cause the entry to stay hidden even then.

::: 

* **background_u_index** (Integer, _optional_)

Default value: `0`. Use this to select a different entry background from the `entry_textures` texture configured in the [Category](./categories#attributes). `u` represents the Y Axis (vertical). The index is zero-based, so the first entry background is `0`, the second is `1`, etc.

* **background_v_index** (Integer, _optional_)

Default value: `0`. Use this to select a different entry background from the `entry_textures` texture configured in the [Category](./categories#attributes). `v` represents the X Axis (horizontal). The index is zero-based, so the first entry background is `0`, the second is `1`, etc.

* **condition** (Condition, _optional_)

Entries, like Categories, can be hidden until an Unlock Condition is fulfilled. Conditions are JSON objects.  
See [Unlock Conditions](../unlock-conditions) for details.

* **parents** (Parent[], _optional_)

Entry Parents are JSON Objects that define Entries this Entry should be connected to. See [Parents](#parents) for details.
A parent connection does not imply an unlock condition or any logical dependency, by default it is just a visual connection, however it can be used to automatically define unlock conditions. See `auto_add_read_conditions` in [Book.json](../structure/book#attributes).

<!-- TODO: link to the book setting that creates read connections -->

* **pages** (Page[], _optional_)

Pages are JSON Objects that define the actual book content. See [Page Types](../page-types/page-types.md) for the available types of pages.

## Parents 

Entry Parents define which entry comes visually before this entry in the book, as in, which entry points an arrow at this entry.

### Attributes

* **entry** (ResourceLocation, _mandatory_)

The ResourceLocation of the parent entry. This is the main attribute.

* **draw_arrow** (Boolean, _optional_)

Default value: `true`. If false, the line connecting parent and this entry will **not** have an arrow at the end.

* **line_enabled** (Boolean, _optional_)

Default value: `true`. If false, there will be no connecting line. This is useful if you want to use the parent connection to define an automatic unlock condition, but don't want to show the line. <!-- TODO: link to the book setting that creates read connections -->

* **line_reversed** (Boolean, _optional_)

Default value: `false`. If true, the line will be drawn from this entry to the parent entry and the arrow will point to the parent, instead of the other way around.

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