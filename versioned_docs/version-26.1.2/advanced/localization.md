---
sidebar_position: 10
---

# Localization

All in-game texts should be supplied as DescriptionIds (= Translation Keys) with a corresponding entry in the language file. This enables translation and uses the full markdown formatting system.

## Key Principles

1. **Always use DescriptionIds** for page texts (title, text, name, etc.) — never inline raw strings in JSON.
2. **One language file per locale** — place it in `/assets/<mod_id>/lang/<locale>.json`.
3. **Datagen recommended** — use `AbstractModonomiconLanguageProvider` to generate translations with access to formatting helpers.

## How It Works

When a page references a DescriptionId like `"book.example.entry.page0.text"`, Modonomicon looks up the corresponding value in the language file at runtime. The value can contain markdown formatting.

```json
{
  "book.example.entry.page0.text": "This is **bold** and _italic_ text."
}
```

## Datagen Approach

In datagen, use `this.add(helper.pageText(), "my.text.key", "The actual text content");` to both register the text and add it to the language provider cache.

For non-English languages, use:
- `this.add(this.lang("ru_ru"), "my.text.key", "Русский текст");`
- Or create additional language provider caches.

## Tips

- Use CrowdIn or similar tools for community translations.
- The translation key format is up to you, but following a consistent pattern like `<modid>.book.<book_id>.<category>.<entry>.<page>.<field>` makes maintenance easier.
- Translatable content can be nested in markdown texts using the `<t>my.description.id</t>` syntax. See [Formatting](../basics/formatting#translatable-content-nested-in-markdown-texts) for details.

## Reference

See [AdvancedFormattingEntry.java](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/formatting/AdvancedFormattingEntry.java) for a complete datagen example.
