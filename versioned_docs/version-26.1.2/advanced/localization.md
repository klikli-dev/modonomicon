---
sidebar_position: 10
---

# Localization

All in-game texts should be supplied as DescriptionIds (= Translation Keys) with a corresponding entry in the language file. This enables translation and uses the full markdown formatting system.

## Key Principles

1. **Offer primary language texts directly in the provider** — use `this.pageTitle()`, `this.pageText()`, `this.entryName()`, etc. to register English texts inline. This is the most convenient approach and keeps text together with the content it belongs to.
2. **Provide translations via language files** — for other languages, add entries to `/assets/<mod_id>/lang/<locale>.json` (or use additional language provider caches).
3. **Never inline raw strings in JSON** — always use DescriptionIds so the translation system can pick up the text.

## How It Works

When a page references a DescriptionId like `"book.example.entry.page0.text"`, Modonomicon looks up the corresponding value in the language file at runtime. The value can contain markdown formatting.

```json
{
  "book.example.entry.page0.text": "This is **bold** and _italic_ text."
}
```

## Datagen Approach (Recommended)

In datagen, provide your primary language (English) texts directly in the entry/category provider using helper methods. The texts are automatically registered as DescriptionIds and added to the language provider cache.

Here is an example from the demo book:

```java
@Override
protected void generatePages() {
    this.page("page1", () -> BookTextPageModel.create()
            .withTitle(this.context().pageTitle())
            .withText(this.context().pageText())
    );

    this.pageTitle("Basic Formatting");
    // \s tells java to keep the spaces at the end of the line.
    // Due to markdown using multiple spaces to indicate a line break, we need to keep the spaces.
    this.pageText("""
            **This is bold**    \s 
            *This is italics*    \s
            ++This is underlined++
            """);
}
```

`this.pageTitle()` and `this.pageText()` both register the text as a DescriptionId **and** return it for use in the page model. The same pattern applies to `this.entryName()` and `this.entryDescription()` for entry-level texts.

### Providing Translations

For non-English languages, use:
- `this.add(this.lang("ru_ru"), "my.text.key", "Русский текст");`
- Or create additional language provider caches and hand them to the book sub provider.

## Tips

- Use CrowdIn or similar tools for community translations.
- The translation key format is up to you, but following a consistent pattern like `<modid>.book.<book_id>.<category>.<entry>.<page>.<field>` makes maintenance easier.
- Translatable content can be nested in markdown texts using the `<t>my.description.id</t>` syntax. See [Formatting](../basics/formatting#translatable-content-nested-in-markdown-texts) for details.

## Reference

See [AdvancedFormattingEntry.java](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/formatting/AdvancedFormattingEntry.java) and [BasicFormattingEntry.java](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/formatting/BasicFormattingEntry.java) for complete datagen examples.
