---
sidebar_position: 10
title: Page Types
---

## Common Attributes
<!-- TODO: Attributes shared by all pages -->

The following attributes are available for all page types

* **type** (String, _mandatory_)

The type of page, it determines which loader is used to load the json data and how the page will be displayed.
Needs to be fully qualified `domain:name`, e.g. `modonomicon:text`. 

* **anchor** (String, _optional_)

A string to uniquely identify the page within the entry it belongs to. Allows to link to specific pages even if the number of pages changes.

### A note on texts

See also [Localization](../advanced/localization).

Whenever a page supports texts there are two options: 

- **supply a DescriptionId** (= Translation Key) with corresponding value in the `/lang/*.json` file.  
  In most cases that value can contain markdown styling instructions.
- supply a vanilla component JSON (not recommended). This can contain untranslated texts and will **not** support markdown styling.

:::tip

It is highly recommend to only use DescriptionIds (= Translation Keys) whenever you supply text for a page, and provide the actual content and (markdown) formatting via corresponding entry in the language file.

:::
