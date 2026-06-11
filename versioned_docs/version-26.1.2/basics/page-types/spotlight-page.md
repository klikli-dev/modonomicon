---
sidebar_position: 60
---

# Spotlight Page

![Spotlight Page](/img/docs/basics/page-types/spotlight-page.png)

**Page type:** `modonomicon:spotlight`

Displays an Ingredient and optionally a title and text.

## Attributes

### **item** (Ingredient OR ItemStack, _mandatory_)

The Ingredient to display. Uses the vanilla ingredient system, so any valid recipe ingredient JSON object can be used.   
Alternatively, an ItemStack JSON can be used. Both `id` and `item` are valid keys for the item resource location.   
If the display item uses components the ItemStack JSON should be used, as ingredients do not support components.   

### **text** (DescriptionId or Component JSON, _optional_)

The page text. Can be styled using markdown.

## Usage Examples

`<entry>.json`:

```json
{
  ...
  "pages": [
     {
      "type": "modonomicon:spotlight",
      "item": {
        "item": "minecraft:apple"
      },
      "text": "book.modonomicon.demo.features.spotlight.spotlight1.text",
      "title": "book.modonomicon.demo.features.spotlight.spotlight1.title"
    }
  ]
}
```

```json
{
  ...
  "pages": [
     {
      "type": "modonomicon:spotlight",
      "item": {
         "components": {
          "minecraft:dyed_color": {
            "rgb": 1481884,
            "show_in_tooltip": false
          }
        },
        "item": "minecraft:leather_helmet"
      },
      "text": "book.modonomicon.demo.features.spotlight.spotlight1.text",
      "title": "book.modonomicon.demo.features.spotlight.spotlight1.title"
    }
  ]
}
```  

`/lang/*.json`:

```json
{
  "book.modonomicon.demo.features.spotlight.spotlight1.text": "A sample spotlight page with custom title.",
  "book.modonomicon.demo.features.spotlight.spotlight1.title": "Custom Title"
}
```
