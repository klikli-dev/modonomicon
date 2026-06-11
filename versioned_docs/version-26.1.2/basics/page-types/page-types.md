---
sidebar_position: 10
---

# Page Types

## Common Attributes

The following attributes are available for all page types

### **type** (String, _mandatory_)

The type of page, it determines which loader is used to load the json data and how the page will be displayed.
Needs to be fully qualified `domain:name`, e.g. `modonomicon:text`.

### **id** (String, _mandatory_)

A string to uniquely identify the page within the entry it belongs to.
This is used when pages are defined as separate files, when page files are merged into inline pages, and when linking to a specific page.
Using page ids is more robust than using the page's array index when pages are reordered or removed.

### **condition** (Condition, _optional_)

Like entries and categories, pages can be hidden until an Unlock Condition is fulfilled.
See **[Unlock Conditions](../unlock-conditions)** for details.

### **title / name / multiblock_name** (DescriptionId or Component JSON, _optional_)

Page titles will not parse markdown by default; they use the default title color as defined in [theme.json](../structure/theme). Some page types offer a `use_markdown_title` option to override this behavior.

**Fallback names:** When a title or name attribute is omitted, the page's primary content name is used as fallback (e.g., the ingredient name for spotlight pages, the entity name for entity pages, the recipe output name for recipe pages).

### A note on texts

See also [Localization](../../advanced/localization).

Whenever a page supports texts there are two options: 

- **supply a DescriptionId** (= Translation Key) with corresponding value in the `/lang/*.json` file.  
  In many cases that value can contain markdown styling instructions.
- supply a vanilla component JSON (not recommended). This can contain untranslated texts and will **not** support markdown styling.

:::tip

It is highly recommend to only use DescriptionIds (= Translation Keys) whenever you supply text for a page, and provide the actual content and (markdown) formatting via corresponding entry in the language file.

:::
