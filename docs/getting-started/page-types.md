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

## Text Page

![Text Page](/img/docs/getting-started/page-types/text-page.png)

Page type: **"modonomicon:text"**

The simplest page type, displays markdown text, optionally with a title and 
<!-- TODO: Describe text page -->

**Attributes**:

* **title** (DescriptionId or Component JSON, _optional_)

The page title. By default this will not parse markdown and use the default title color as defined in the `book.json`.
<!-- TODO: link to book settings here -->

* **use_markdown_title** (Boolean, _optional_)
  
Defaults to `false`. If true the default title style will not be applied and instead markdown in the title will be parsed.

* **show_title_separator** (Boolean, _optional_)

Defaults to `true`. If true a separator will be rendered below the title.
<!-- TODO: link to book styling here and note the UV coordinates -->

* **text** (DescriptionId or Component JSON, _optional_)

The page text. Can be styled using markdown.

### Example Usage

`<entry>.json`:

```json
{
  ...
  "pages": [
    {
      "type": "modonomicon:text",
      "title": "modonomicon.testbook.test_category.test_entry.page0.title",
      "use_markdown_title": true,
      "show_title_separator": true,
      "text": "modonomicon.testbook.test_category.test_entry.page0.title"
    }
  ]
}
```  

`/lang/*.json`:

```json
{
  "modonomicon.test.sections.test_category.test_entry.page0.text": "This is a **test** text.\nWe have a newline here.\n- List item\n- List item 2\n- List item 3\n\nAnd this is a super long line where we hope it will be automatically wrapped into a new line otherwise that is super-bad.\n",
  "modonomicon.test.sections.test_category.test_entry.page0.title": "**Bold**"
}
```

