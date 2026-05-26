---
sidebar_position: 20
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

### A note on texts

See also [Localization](../../advanced/localization).

Whenever a page supports texts there are two options: 

- **supply a DescriptionId** (= Translation Key) with corresponding value in the `/lang/*.json` file.  
  In many cases that value can contain markdown styling instructions.
- supply a vanilla component JSON (not recommended). This can contain untranslated texts and will **not** support markdown styling.

:::tip

It is highly recommend to only use DescriptionIds (= Translation Keys) whenever you supply text for a page, and provide the actual content and (markdown) formatting via corresponding entry in the language file.

:::
