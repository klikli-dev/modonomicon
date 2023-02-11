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

Newlines *generally* work like in markdown, meaning a linebreak only renders as newline if it is preceded by three spaces.

:::warning

If using Java Text Blocks to datagen texts java actually trims spaces at the end of lines so you need to use forced line breaks!
Alternatively you can place `\s` at the end of the line after the three spaces to prevent java from trimming the spaces.

:::

In markdown linebreak can also be forced with a backslash `\`. In modonomicon, due to the underlying techonology you need to escape the backslash in both Java and JSON by simply doing two backslashes: `\\`.

#### Empty Line

Because in many cases a line of text does not perfectly and at the end of the maximum length a line can have in the book, you need to use a little "trick" to force a fully empty line. 

You need two escaped backslashes (= 4 backslashes) like so `\\\\`, which will tell the markdown parser to do two line breaks. The first linebreak ends the previous text line, the second line then is the empty line.

As seen in the code sample below, you can also spread the two escaped backslashes over two lines for better readability:

```JAVA
        this.add(helper.pageText(),
                """
                    Chalk is used to draw pentacle runes and define the pentacle shape. Different types of chalk are used for different purposes, as outlined on the next pages.
                    \\
                    \\
                    The different runes are purely decorative.
                    """);

```

or 

```JAVA
        this.add(helper.pageText(),
                """
                    Chalk is used to draw pentacle runes and define the pentacle shape. Different types of chalk are used for different purposes, as outlined on the next pages.\\
                    \\
                    The different runes are purely decorative.
                    """);

```

:::info

This is only necessary in modonomicon (but not in "normal" markdown editors) because of the above-mentioned behaviour of java to strip trailing spaces. 
In normal markdown you would simply add three spaces at the end of your text line, and then insert the forced line break with a backslash.

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
Syntax: 
- [display text](book://<book-id>)` 

* **Category Link**

Open a category (in the same book, or in another book).
Syntax: 
- `[display text](book://<book-id>/<category-id>)`
- `[display text](book://<category-id>)` 

:::tip 

If `<book-id>` is ommitted the current book is assumed.

::: 
  
* **Entry Link**

Opens an entry (in the same book, or in another book), optionally at either a given page number or page anchor.
Syntax: 
- `[display text](book://<book-id>/<entry-id>[#page-number][@page-anchor])`.
- `[display text](book://<entry-id>[#page-number][@page-anchor])`.

:::tip 

If `<book-id>` is ommitted the current book is assumed.

::: 

:::tip

The entry id is the full path of the entry within the `/entries/` folder, that often includes the category id, if it is used as a subdirectory, e.g. for `/entries/my-category/my-entry` the entry id is `my-category/my-entry`.

:::

### Item / Block Link

Item links are links that cannot be clicked, but that will show the (translated) item or block name if hovered, and if not provided with a link text, will also automatically have the translated name as rendered text.

Syntax: 
- `[optional display text](item://minecraft:apple)`
- `[](item://minecraft:chest)`

### Patchouli Links

Patchouli links are special links that can be used to link to a particular patchouli entry and open it on click.

Syntax: 
  - `[display text](patchouli://<mod_id>:<patchouli_book_id>//<entry_id>#<page_number>)`.
  - `[display text](patchouli://<mod_id>:<patchouli_book_id>//<entry_id>)`.

Example:
- `[Link to a Patchouli Entry](patchouli://occultism:dictionary_of_spirits//misc/books_of_calling)`

:::caution 

Note the double `//` separating the book id from the entry id. This is required, because both book and entry ids may contain one or multile  `/` characters if the files are in subdirectories.
::: 

:::tip 

The entry id is the full path of the entry within the `/entries/` folder in `patchouli_books`, the path you would also use for links within patchouli with the `$(l:<entry_id>)` syntax.

::: 

#### Translations

On hover the link will attempt to display the name of the patchouli page that will be opened on click.
Patchouli does not have a standard DescriptionId format for entry names (in fact, entry names can be provided without using the translation system at all), so you need to manually include the translation for the link text in your `lang.json` file. 

The DescriptionId used for hover texts is `patchouli.<patchouli_book_id>.<entry_id>.name`, e.g. `patchouli.occultism.dictionary_of_spirits.misc.books_of_calling.name`. Make sure to provide a translation for this DescriptionId in your `lang.json` file (or better, in your language datagen), otherwise the hover text will show the DescriptionId itself.

:::tip 

The `<patchouli_book_id>` will include the mod id, the `<entry_id>` will not.

::: 


## Non-Standard Markdown

In order to provide a bit more flexibility, the markdown parser supports a few non-standard instructions.

### Text Color 

Color instructions (ab)use the link syntax as follows: to start a colored region use `[#](<hexcode>)`, to reset to the default color use `[#]()`.
Example:

```markdown
[#](ff0000)Red text [#](00ff00)from here on green [#](0000ff)now blue [#]()and finally back to default color.
```