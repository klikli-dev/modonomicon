---
sidebar_position: 20
sidebar_label: Formatting
title: Formatting
---

## Links 

Markdown links use the following syntax: `[text](url)`.

### HTTP Links

HTTP URLs will be handled like in the minecraft chat, which means clicking them will show an approval dialogue window and will then open the URL in an external browser. 

### Book Links

Book links are special links between different pages of the same book, or even to other books.
There are three types:

* **Book Link**

Open another book on it's default page.
Format: `[display text](book://<book-id>)`.

* **Category Link**

Open a category (in the same book, or in another book).
Format: `[display text](book://<book-id>/<category-id>)`.

* **Entry Link**

Opens an entry (in the same book, or in another book), optionally at a given page number / page anchor.
Format: `[display text](book://<book-id>/<entry-id>[#page-number][@page-anchor])`.

*Note*: The entry id is the full path of the entry within the `/entries/` folder, that often includes the category id, if it is used as a subdirectory, e.g. for `/entries/my-category/my-entry` the entry id is `my-category/my-entry`.

## Non-Standard Markdown

In order to provide a bit more flexibility, the markdown parser supports a few non-standard instructions.

### Text Color 

Color instructions (ab)use the link syntax as follows: to start a colored region use `[#](<hexcode>)`, to reset to the default color use `[#]()`.
Example:

```markdown
[#](ff0000)Red text [#](00ff00)from here on green [#](0000ff)now blue [#]()and finally back to default color.
```