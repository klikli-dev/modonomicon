---
sidebar_position: 40
---

# Image Page

![Image Page](/img/docs/basics/page-types/image-page.png)

**Page type:** `modonomicon:image`

Displays an image and optionally a title and text.

## Attributes

### **images** (Identifier[], _mandatory_)

Array of Identifiers of the textures to display.   
By default an image of any size can be used and will be rendered entirely. See `use_legacy_rendering` for more information.


### **use_legacy_rendering** (Boolean, _optional_)
Defaults to `false`. 

If true, the image will be rendered using the legacy rendering setup that matches the behaviour in Patchouli.    
The files should be 256px by 256px. The upper left 200px by 200px will be rendered.   
Larger images work too but the top left box matching the relative size of the 200px by 200px box will be rendered.   

### **text** (DescriptionId or Component JSON, _optional_)

The page text. Can be styled using markdown.

### **border** (Boolean, _optional_)

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
