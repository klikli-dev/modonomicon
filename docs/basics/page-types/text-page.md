---
sidebar_position: 20
---

# Text Page

![Text Page](/img/docs/basics/page-types/text-page.png)

**Page type:** `modonomicon:text`

The simplest page type, displays markdown text, optionally with a title.

## Attributes

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

## Usage Examples

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
      "text": "modonomicon.testbook.test_category.test_entry.page0.text"
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
