---
sidebar_position: 40
---

# Image Page

![Image Page](/img/docs/basics/page-types/image-page.png)

**Page type:** `modonomicon:image`

Displays an image and optionally a title and text.

## Attributes

* **title** (DescriptionId or Component JSON, _optional_)

The page title. Will not parse markdown, instead it uses the default title color as defined in the `book.json`.
<!-- TODO: link to book settings here -->

* **images** (ResourceLocation[], _mandatory_)

Array of ResourceLocations of the images to display.

* **text** (DescriptionId or Component JSON, _optional_)

The page text. Can be styled using markdown.

* **border** (Boolean, _optional_)

Defaults to `true`. If true, render a border around the image.

## Usage Examples

`<entry>.json`:

```json
{
  ...
  "pages": [
    {
      "type": "modonomicon:image",
      "border": true,
      "images": [
        "modonomicon:textures/gui/default_background.png",
        "modonomicon:textures/gui/dark_slate_seamless.png"
      ],
      "text": "book.modonomicon.demo.features.image.image.text",
      "title": "book.modonomicon.demo.features.image.image.title"
    }
  ]
}
```  

`/lang/*.json`:

```json
{
  "book.modonomicon.demo.features.image.image.text": "A sample text for the sample image.",
  "book.modonomicon.demo.features.image.image.title": "Sample image!",
}
```
