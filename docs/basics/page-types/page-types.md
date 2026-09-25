---
sidebar_position: 50
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

### Long texts: scaling and splitting

If a page text does not fit on the page, Modonomicon shrinks it until it fits.
If you prefer, you can instead let the overflow flow onto additional pages at full size.

The following attributes are available on all pages that show a text (text, spotlight, image, entity, multiblock and recipe pages).
Book-wide defaults for both can be set in [book.json](../structure/book).

#### **auto_scale** (Boolean, _optional_)

Defaults to the book-wide `default_auto_scale` setting (`true` if not set).
If `true`, text that is too long is scaled down until it fits on the page.

Set it to `false` to keep the text at full size.
If splitting is also disabled, overflowing text is simply cut off, so only disable scaling when you are sure the text fits, or when you enable splitting.

#### **allow_page_split** (Boolean, _optional_)

Defaults to the book-wide `default_allow_page_split` setting (`false` if not set).
If `true`, text that is too long flows onto additional pages instead of shrinking.

Set it to `true` on pages with long, variable-length texts (such as translations):

```json
{
  "type": "modonomicon:text",
  "title": "my.book.my_entry.my_page.title",
  "text": "my.book.my_entry.my_page.text",
  "allow_page_split": true
}
```

What to expect when splitting is enabled:

- The first page keeps its title, the overflow continues on text-only pages at full size.
- Splitting takes precedence over scaling: a page with `"allow_page_split": true` never shrinks, even if scaling is enabled.
- Splitting is calculated separately for each language, so long translations automatically get as many continuation pages as they need. You do not need to author the extra pages yourself.
- Links, lists and line breaks keep working across the split pages, and links to the page (by page number or page id) still point at the original page.
