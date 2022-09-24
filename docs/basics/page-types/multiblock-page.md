---
sidebar_position: 30
---

# Multiblock Page

![Multiblock Page](/img/docs/basics/page-types/multiblock-page.png)

**Page type:** `modonomicon:multiblock`

Displays a multiblock, optionally the multiblock's name and a text.

## Attributes

* **multiblock_name** (DescriptionId or Component JSON, _optional_)

The multiblock name. Will not parse markdown, instead it uses the default title color as defined in the `book.json`.
<!-- TODO: link to book settings here -->

* **multiblock_id** (ResourceLocation, _mandatory_)

The ResourceLocation to the multiblock to display.

* **text** (DescriptionId or Component JSON, _optional_)

The page text. Can be styled using markdown.

* **show_visualize_button** (Boolean, _optional_)

Defaults to `true`. If true, the visualize button will be enabled for this page, allowing the player to preview the multiblock in-world.

## Usage Examples

`<entry>.json`:

```json
{
  ...
  "pages": [
    {
      "type": "modonomicon:multiblock",
      "multiblock_name": "modonomicon.testbook.test_category.multiblock.page0.multiblock_name",
      "multiblock_id": "modonomicon:dense",
      "text": "modonomicon.testbook.test_category.multiblock.page0.text"
    }
  ]
}
```  

`/lang/*.json`:

```json
{
  "modonomicon.testbook.test_category.multiblock.page0.multiblock_name": "Test Multiblock",
  "modonomicon.testbook.test_category.multiblock.page0.text": "A page with multiblock!"
}
```
