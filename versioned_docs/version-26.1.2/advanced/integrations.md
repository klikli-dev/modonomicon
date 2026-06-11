---
sidebar_position: 30
---

# Integrations with other Mods

## Patchouli

Modonomicon can provide links to Patchouli pages and open them on click. This allows your book to reference content from mods that still use Patchouli.

### Creating a Patchouli Link

Use the Patchouli link syntax in your markdown texts:

```
[display text](patchouli://<mod_id>:<patchouli_book_id>//<entry_id>#<page_number>)
```

Example:
```
[Link to a Patchouli Entry](patchouli://occultism:dictionary_of_spirits//misc/books_of_calling)
```

:::caution
Note the double `//` separating the book id from the entry id. This is required because both book and entry ids may contain `/` characters for subdirectories.
:::

### Translations

On hover, the link displays the Patchouli entry name. You need to provide the translation for this in your language file:

DescriptionId format: `patchouli.<patchouli_book_id>.<entry_id>.name`

Example: `patchouli.occultism.dictionary_of_spirits.misc.books_of_calling.name`

:::tip
The `<patchouli_book_id>` includes the mod id, but the `<entry_id>` does not.
:::

## JEI / REI / EMI

Modonomicon books automatically integrate with recipe viewers. Recipe pages display correctly in JEI, REI, and EMI without additional configuration.

## Other Mods

Mods can extend Modonomicon with custom page types, conditions, and more. See [Custom Conditions](./custom-conditions) and [Runtime Extending](./runtime-extending) for details.
