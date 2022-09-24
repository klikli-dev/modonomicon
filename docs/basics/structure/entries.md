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

* **condition** (Condition, _optional_)

Entries, like Categories, can be hidden until an Unlock Condition is fulfilled. Conditions are JSON objects.  
See [Unlock Conditions](../unlock-conditions/unlock-conditions) for details.

* **parents** (Parent[], _optional_)

Entry Parents are JSON Objects that define Entries this Entry should be connected to. See [Parents](#parents) for details.
A parent connection does not imply an unlock condition or any logical dependency, by default it is just a visual connection, however it can be used to automatically define unlock conditions.

<!-- TODO: link to the book setting that creates read connections -->

## Parents 

<!-- TODO Parents -->

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
      "type": "modonomicon:multiblock",
      "multiblock_name": "modonomicon.test.sections.test_category.multiblock.page0.multiblock_name",
      "multiblock_id": "modonomicon:blockentity",
      "text": "modonomicon.test.sections.test_category.multiblock.page0.text",
      "show_visualize_button": true
    }
  ],
  "parents": [
    {
      "entry": "modonomicon:test_category/test_entry"
    }
  ]
}
```