# Pages as Separate Files

**Date:** 2026-05-25
**Branch:** `feature/pages-as-files`

## Overview

Allow book entry pages to be stored as separate JSON files in a `pages/` subfolder named after the entry, alongside the existing inline pages array. File pages take precedence over inline pages by matching page ID.

## Problem

Currently, all pages are embedded inline within the entry JSON's `"pages"` array. This makes it difficult for mod pack authors to override individual pages without duplicating the entire entry. It also makes large entries with many pages hard to maintain.

## Design Decisions

- **Page ID replaces anchor:** Pages get a required string `id` field that replaces the optional `anchor` field. The `id` serves as both the unique identifier within the entry and the named reference for cross-entry navigation.
- **File structure:** `entries/<category>/<entry-id>/pages/<page-id>.json`
- **Precedence:** File pages replace inline pages with the same ID. File pages with no inline match are inserted at their `sort_number` position (or appended at end if `-1`/absent).
- **Hard break:** No backwards compatibility for `anchor`. Consumers update on mod version bump.

## File Structure

```
modonomicon/
  my_book/
    book.json
    entries/
      getting_started/
        introduction.json           # entry with inline pages only
        crafting.json               # entry with mixed inline + file pages
          pages/
            basics.json             # overrides inline page with id "basics"
            advanced.json           # new page, no inline match
```

### Entry JSON (with inline pages)

```json
{
  "type": "content",
  "id": "my_mod:getting_started/crafting",
  "category": "my_mod:getting_started",
  "name": "mod.my_mod.my_book.getting_started.crafting.name",
  "pages": [
    { "id": "basics", "type": "text", "title": "...", "text": "..." },
    { "id": "tips", "type": "text", "title": "...", "text": "..." }
  ]
}
```

### Page File

```json
{
  "id": "basics",
  "type": "text",
  "title": "mod.my_mod.my_book.getting_started.crafting.basics.title",
  "text": "mod.my_mod.my_book.getting_started.crafting.basics.text"
}
```

**Result:** Entry has pages `[basics (from file), tips (inline)]`. The `basics` page was replaced by the file version.

## Changes

### 1. `BookPage` — Anchor to ID

| Field | Before | After |
|---|---|---|
| `anchor` | `String` (optional) | Removed |
| `id` | — | `String` (required) |

- Constructor changes from `BookPage(String anchor, BookCondition condition)` to `BookPage(String id, BookCondition condition)`
- `getAnchor()` → `getId()`
- `getPageNumberForAnchor(String)` → `getPageNumberForId(String)` in `BookContentEntry`

### 2. Codec Changes

**`BookPage.CODEC`** — wrapper codec that parses `id` at the page level, then dispatches inner type:

```java
// Before: dispatch on "type" field directly
Codec.lazyInitialized(() -> BookPageTypeRegistry.codec().dispatch(
    "type", BookPage::type, BookPageType::codec
))

// After: wrapper that extracts "id" then dispatches on "type"
RecordCodecBuilder.mapCodec(instance -> instance.group(
    Codec.STRING.fieldOf("id").forGetter(BookPage::getId),
    BookPageTypeRegistry.codec().dispatch(
        "type", BookPage::type, BookPageType::codec
    ).forGetter(page -> page)
))
```

Each concrete page codec loses its `anchor` field.

**`BookContentEntry.CODEC`** — `pages` field becomes optional (defaults to empty list):

```java
BookPage.CODEC.listOf().optionalFieldOf("pages").forGetter(
    entry -> Optional.ofNullable(entry.pages)
)
```

**`BookPage.STREAM_CODEC`** — add `id` field to composite, remove `anchor`.

### 3. `BookDataManager` — Loader

New path category in `categorizeContent()`:

```
case "pages" -> { pageJsons.put(entry.getKey(), entry.getValue().getAsJsonObject()); }
```

After entries are loaded, new merge step:

```
for each entry in loaded entries that is a BookContentEntry:
  entryPath = entry's source path (e.g., "getting_started/crafting")
  pageFiles = pageJsons filtered by parent path matching entryPath + "/pages/"
  for each pageFile:
    page = BookPage.fromJson(entryId, pageFile, provider)
    if entry has inline page with same id:
      replace inline page with file page (preserve position)
    else:
      add to newPages list
  sort newPages by sort_number (stable sort, -1/absent goes to end)
  insert newPages into entry's page list at their sort_number positions
  reindex all page numbers (0, 1, 2, ...)
```

### 4. `BookPage` — Sort Number

Add optional `sortNumber` field to page files for ordering new pages:

```json
{
  "id": "advanced",
  "sort_number": 1,
  "type": "text",
  ...
}
```

- Default: `-1` (append at end)
- Used only for file pages that don't match an inline page
- For replacement pages, position is determined by the inline page they replace

### 5. Datagen Changes

**`BookPageModel`:**
- `anchor` field → `id` field (required)
- `withAnchor(String)` → `withId(String)`
- Constructor requires `id` parameter
- `toJson(Identifier entryId, ...)` — `entryId` parameter kept for error context, no longer needed for anchor generation

**`BookTextPageModel`, `BookImagePageModel`, etc.:**
- Constructor signature changes to accept `id` instead of `anchor`
- `toBookPage()` passes `id` to `BookPage` constructor

**`BookContextHelper`:**
- No changes — `pageId()` already exists and is used for translation key generation

**`EntryProvider.page(String pageId, Supplier<T>):`**
- Already sets `context().page(pageId)` — now the supplied model's `id` is wired from this context
- The `pageId` parameter becomes the page's `id` field

### 6. Cross-Entry References

Anywhere that referenced `anchor` now references `id`:
- `BookContentEntry.getPageNumberForAnchor()` → `getPageNumberForId()`
- URL/command protocols that use anchor for page targeting
- `BookAddress` stays using integer page index internally (no change needed)

## Files Modified

| File | Change |
|---|---|
| `BookPage.java` | `anchor` → `id`, add `sortNumber`, update codecs |
| `BookContentEntry.java` | `getPageNumberForAnchor()` → `getPageNumberForId()`, optional pages |
| `BookTextPage.java` | Remove `anchor` from codec |
| `BookImagePage.java` | Remove `anchor` from codec |
| `BookEmptyPage.java` | Remove `anchor` from codec |
| `BookSpotlightPage.java` | Remove `anchor` from codec |
| `BookEntityPage.java` | Remove `anchor` from codec |
| `BookMultiblockPage.java` | Remove `anchor` from codec |
| `BookRecipePage.java` (and subclasses) | Remove `anchor` from codec |
| `BookDataManager.java` | New `pages` path category, merge logic |
| `BookPageModel.java` | `anchor` → `id` |
| `BookTextPageModel.java` | Constructor takes `id` |
| `BookImagePageModel.java` | Constructor takes `id` |
| `BookEmptyPageModel.java` | Constructor takes `id` |
| `BookSpotlightPageModel.java` | Constructor takes `id` |
| `BookEntityPageModel.java` | Constructor takes `id` |
| `BookMultiblockPageModel.java` | Constructor takes `id` |
| `BookRecipePageModel.java` (and subclasses) | Constructor takes `id` |
| `EntryProvider.java` | Wire `pageId` context to model's `id` |
| `BookAddress.java` | No changes (uses int index) |

## Backwards Compatibility

**Breaking change.** The `anchor` field is removed and replaced with the required `id` field. Mod pack authors need to:
1. Rename `"anchor"` to `"id"` in their page JSONs
2. Ensure all pages have a unique `id` within their entry

No migration codec is provided. This is a clean break.

## Testing

1. Entry with only inline pages — works as before
2. Entry with only file pages — all pages loaded from files
3. Entry with mixed inline + file pages — file pages override inline by ID
4. File page with `sort_number` — inserts at correct position
5. File page without matching inline — appended at end (or at sort_number)
6. Cross-entry navigation via page ID — works correctly
7. Datagen generates correct JSON with `id` field
8. Network sync includes `id` field
