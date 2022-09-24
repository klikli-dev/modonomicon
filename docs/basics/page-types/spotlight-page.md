---
sidebar_position: 60
---

# Spotlight Page

![Spotlight Page](/img/docs/basics/page-types/spotlight-page.png)

**Page type:** `modonomicon:spotlight`

Displays an Ingredient and optionally a title and text.

## Attributes

* **title** (DescriptionId or Component JSON, _optional_)

The page title. Will not parse markdown, instead it uses the default title color as defined in the `book.json`.   

:::tip

If ommited, the ingredients name will be used. If the ingredient is not an item, the first matching item's name will be used.

:::

<!-- TODO: link to book settings here -->

* **item** (Ingredient, _mandatory_)

The Ingredient to display. Uses the forge ingredient system, so any valid recipe ingredient JSON object can be used.

* **text** (DescriptionId or Component JSON, _optional_)

The page text. Can be styled using markdown.

## Usage Examples

`<entry>.json`:

```json
{
  ...
  "pages": [
     {
      "type": "modonomicon:spotlight",
      "anchor": "",
      "item": {
        "item": "minecraft:apple"
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
