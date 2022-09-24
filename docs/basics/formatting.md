---
sidebar_position: 30
---

# Formatting

Modonomicon texts support a subset of [Markdown](https://www.markdownguide.org/cheat-sheet/), with some quirks due to how minecraft text rendering works.
Additionally there is some non-standard functionality to support minecraft-specific use cases.

## General

### Bold 

Format: `**bold**`

### Italics 

Format: `*italics*` or `_italics_`

### Underline

Format: `++underline++`

### Strikethrough

Format: `~~stricken through~~`

### Newline

Newlines work like in markdown, meaning a linebreak only renders as newline if it is preceded by three spaces.
In markdown linebreak can also be forced with a backslash `\`. In both Java and JSON you need to escape it: `\\`.


:::tip

If using Java Text Blocks to datagen texts java actually trims spaces at the end of lines so you need to use forced line breaks!
Alternatively you can place `\s` at the end of the line after the three spaces to prevent java from trimming the spaces.

:::

<!-- TODO Mention the book/render setting that makes soft linebreaks act as hard line breaks -->

## Lists

Lists work like in markdown using `-` or `*` for unordered lists and `1.`, `2.`, ... for ordered lists.

:::caution

A newline/empty line should be placed after a list, otherwise the parser will add content of the line to the last list item.

:::

:::caution

Nested lists only have limited support and may not correctly wrap to the next line. If there are issues reduce nesting level or force a line break with `//`.

:::

## Headings

Markdown headings are not supported. Page titles are a separate JSON element.

## Images

Like headings, images are not supported. There are specific page types to display images.

## Translatable Content nested in Markdown Texts

To included the translated contents from a DescriptionId contained in `lang.json` simply use `<t>my.description.id</t>`. 

:::tip

The translated contents will not be sent through the markdown renderer, however you can use this to e.g. have an item (or any vanilla/modded object type that uses the minecraft translation system) name automatically be translated.

:::

## Links 

Markdown links use the following syntax: `[text](url)`.

### HTTP Links

HTTP URLs will be handled like in the minecraft chat, which means clicking them will show an approval dialogue window and will then open the URL in an external browser. 

### Book Links

Book links are special links between different pages of the same book, or even to other books.
There are three types:

* **Book Link**

Open another book on it's default page.
Syntax: `[display text](book://<book-id>)`.

* **Category Link**

Open a category (in the same book, or in another book).
Syntax: `[display text](book://<book-id>/<category-id>)`.

* **Entry Link**

Opens an entry (in the same book, or in another book), optionally at either a given page number or page anchor.
Format: `[display text](book://<book-id>/<entry-id>[#page-number][@page-anchor])`.

*Note*: The entry id is the full path of the entry within the `/entries/` folder, that often includes the category id, if it is used as a subdirectory, e.g. for `/entries/my-category/my-entry` the entry id is `my-category/my-entry`.

### Item / Block Link

Item links are links that cannot be clicked, but that will show the (translated) item or block name if hovered, and if not provided with a link text, will also automatically have the translated name as rendered text.

Syntax: 
- `[optional display text](item://minecraft:apple)`
- `[](item://minecraft:chest)`

## Non-Standard Markdown

In order to provide a bit more flexibility, the markdown parser supports a few non-standard instructions.

### Text Color 

Color instructions (ab)use the link syntax as follows: to start a colored region use `[#](<hexcode>)`, to reset to the default color use `[#]()`.
Example:

```markdown
[#](ff0000)Red text [#](00ff00)from here on green [#](0000ff)now blue [#]()and finally back to default color.
```