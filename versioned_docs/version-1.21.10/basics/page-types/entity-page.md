---
sidebar_position: 50
---

# Entity Page

![Entity Page](/img/docs/basics/page-types/entity-page.png)

**Page type:** `modonomicon:entity`

Displays an Entity and optionally a custom name and text.

## Attributes

### **name** (DescriptionId or Component JSON, _optional_)

The entity name. Will not parse markdown, instead it uses the default title color as defined in the `book.json`.    
See [Book.json](../structure/book) for details.

:::tip

If ommited, the entity's default name will be used.

:::

### **entity_id** (ResourceLocation, _mandatory_)

ResourceLocations of the entity to display.

:::tip

Supports appending entity NBT in the format `minecraft:sheep{<nbt>}`, like the minecraft `data` command.

:::

### **text** (DescriptionId or Component JSON, _optional_)

The page text. Can be styled using markdown.

### **scale** (Float, _optional_)

Defaults to `1.0`. Render scale for the entity.

### **offset** (Float, _optional_)

Defaults to `0.0`. Offset to move entity up/down on the page.

### **rotate** (Boolean, _optional_)

Defaults to `true`. If true, slowly rotate entity.

### **default_rotation** (Float, _optional_)

Defaults to `-45.0`. Default rotation to show the entity at (in degrees).

## Usage Examples

`<entry>.json`:

```json
{
  ...
  "pages": [
    {
      "type": "modonomicon:entity",
      "anchor": "",
      "default_rotation": -45.0,
      "entity_id": "minecraft:ender_dragon",
      "name": "book.modonomicon.demo.features.entity.entity1.title",
      "offset": 0.0,
      "rotate": true,
      "scale": 0.5,
      "text": ""
    }
  ]
}
```  

`/lang/*.json`:

```json
{
  "book.modonomicon.demo.features.entity.entity1.title": "Custom Name"
}
```
