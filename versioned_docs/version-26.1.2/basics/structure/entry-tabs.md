---
sidebar_position: 45
---

# Entry Tabs

Entry tabs are bookmarks on the left or right edge of an entry. Clicking a tab opens its page as a single page over the book, for example a recipe or a short note, without leaving the entry. Tabs are not part of the entry's regular pages, so they do not change page numbering.

## Behaviour

- Tabs are shown on the edges of the entry screen. Tabs that are not visible are not shown at all.
- Clicking a tab dims the book and shows the tab's page over the open spread. Clicking the same tab again closes it. Clicking another tab switches to it.
- The close button in the top right corner of the tab page closes it. A left click anywhere outside the tab page also closes it. That click is consumed, so it does not also close the entry or the book.
- Right-click, `Escape` and `Backspace` close the tab.
- While a tab is open, the page arrows are hidden and pages cannot be turned.
- If the tab stops being visible while it is open, for example because its condition is no longer met, it closes.
- Tabs are only shown on the double-page entry screen. Leaflets have no tabs.

## Tab Files

Tabs are stored in a `tabs` folder next to the entry's `pages` folder. The file name is the tab id:

```
data/<mod_id>/modonomicon/books/<book_id>/entries/
  <category_id>/
    <entry_name>.json            ← entry definition
    <entry_name>/
      pages/
        <page_id>.json           ← page files
      tabs/
        <tab_id>.json            ← tab files
```

Tabs can also be written inline with a `tabs` array in the entry JSON. Each element has the same attributes as a tab file.

When both exist, a tab file replaces the inline tab with the same id. Tab files without a matching inline tab are added after the inline tabs, in ascending `sort_number` order.

## Attributes

### **id** (String, _optional_)

The id of the tab. For tab files the file name is the id. For inline tabs it is the value of `id`. Tab ids must be unique within the entry.

### **page** (Page, _mandatory_)

The page shown when the tab is opened. It is a normal page object, see [Page Types](../page-types/page-types.md). In a tab file, the page's `id` is set from the tab id if it has none.

### **icon** (Identifier or Item JSON Object, _optional_)

The icon of the tab, given like the icon of an entry (see [Entries](./entries.md#icon-identifier-mandatory)).

If omitted, the icon is derived from the page: recipe pages show their output and spotlight pages show their item. Other pages show no icon, so only the bookmark is visible.

### **title** (Text Component, _optional_)

The title of the tab, shown as a tooltip when hovering the tab. If omitted, the tooltip shows the name of the item the icon is derived from, if there is one.

### **side** (String, _optional_)

Default value: `"right"`. The edge the tab is attached to: `"left"` or `"right"`.

### **anchor** (String, _optional_)

The `id` of a page of the same entry. If set, the tab is only visible while that page is on the open spread. This includes the continuation of the page when it was split. If no page with that id is open, the tab is hidden.

### **condition** (Condition, _optional_)

If the condition is not met, the tab is not shown. See [Unlock Conditions](../unlock-conditions/unlock-conditions.md).

### **sort_number** (Integer, _optional_)

Default value: `-1`. Only tab files are ordered by this value, in ascending order. Tabs with the same value keep their file name order.

Tabs on the same side are stacked in their final order from top to bottom, so inline tabs come first, in array order. Datagen sets `sort_number` automatically in declaration order.

### **load_condition** (Condition, _optional_)

If the load condition is not met, the tab is removed when the book is loaded, instead of only being hidden. This works the same as for pages, see [Load Conditions](../unlock-conditions/load-conditions.md). In datagen use `BookEntryTabModel.withLoadCondition(...)`.

## Visibility

A tab is shown when all of these are true:

- its `condition` is met,
- its page is visible to the player,
- if it has an `anchor`, the anchored page is on the open spread.

Tabs without `anchor` are visible on every spread of the entry.

## Example

The demo book (`features/entry_tabs`) has a tab file pinned to a page, and a tab on the left edge that is visible for the whole entry:

`entries/features/entry_tabs/tabs/anchored.json`:

```json
{
  "anchor": "tabs",
  "condition": {
    "type": "modonomicon:none"
  },
  "icon": {
    "id": "minecraft:apple"
  },
  "id": "anchored",
  "page": {
    "type": "modonomicon:spotlight",
    "condition": {
      "type": "modonomicon:none"
    },
    "id": "anchored",
    "item": "minecraft:apple",
    "text": "",
    "title": ""
  },
  "sort_number": 0,
  "title": {
    "translate": "book.modonomicon.demo.features.entry_tabs.tab.anchored.title"
  }
}
```

`entries/features/entry_tabs/tabs/field_notes.json`:

```json
{
  "condition": {
    "type": "modonomicon:none"
  },
  "icon": {
    "id": "minecraft:writable_book"
  },
  "id": "field_notes",
  "page": {
    "type": "modonomicon:text",
    "condition": {
      "type": "modonomicon:none"
    },
    "id": "field_notes",
    "show_title_separator": true,
    "text": "book.modonomicon.demo.features.entry_tabs.tab.field_notes.text",
    "title": "book.modonomicon.demo.features.entry_tabs.tab.field_notes.title"
  },
  "side": "left",
  "sort_number": 3,
  "title": {
    "translate": "book.modonomicon.demo.features.entry_tabs.tab.field_notes.title"
  }
}
```

Inline tabs are written in the entry JSON in the same way:

```json
"tabs": [
  {
    "id": "field_notes",
    "side": "left",
    "icon": {
      "id": "minecraft:writable_book"
    },
    "page": {
      "type": "modonomicon:text",
      "text": "book.modonomicon.demo.features.entry_tabs.tab.field_notes.text",
      "title": "book.modonomicon.demo.features.entry_tabs.tab.field_notes.title"
    }
  }
]
```

## Datagen

Tabs are declared in an `EntryProvider`, next to the pages:

- `this.tab(id, () -> ...)` declares a tab that is pinned to the page declared most recently with `this.page(...)`. It is only visible while that page is on the open spread. Several tabs declared after the same page are all pinned to it. Calling `this.tab(...)` before any page throws an `IllegalStateException`.
- `this.entryTab(id, () -> ...)` declares a tab that is visible on every spread of the entry.
- Call `withAnchor(pageId)` on the model to pin a tab to a different page. An explicit anchor is kept.
- `withSide(EntryTabSide.LEFT)` puts the tab on the left edge. The default is `EntryTabSide.RIGHT`.

```java
this.page("tabs", () -> BookTextPageModel.create()
        .withTitle(this.context().pageTitle())
        .withText(this.context().pageText())
);
this.pageTitle("Pinned Tabs");

this.tab("anchored", () -> BookEntryTabModel.create()
        .withPage(BookSpotlightPageModel.create().withItem(Ingredient.of(Items.APPLE)).withAssociatedItem(Items.APPLE))
        .withIcon(Items.APPLE)
        .withTitle(this.tabTitleKey())
);
this.tabTitle("Anchored spotlight");

this.entryTab("field_notes", () -> BookEntryTabModel.create()
        .withSide(EntryTabSide.LEFT)
        .withIcon(Items.WRITABLE_BOOK)
        .withPage(BookTextPageModel.create()
                .withTitle(this.tabTitleKey())
                .withText(this.tabTextKey()))
        .withTitle(this.tabTitleKey())
);
this.tabTitle("Field notes");
this.tabText("Tabs on the left edge work the same way as tabs on the right edge.");
```

Tab titles and texts use the translation keys `<entry>.tab.<tab_id>.title` and `<entry>.tab.<tab_id>.text`. `tabTitleKey()` and `tabTextKey()` return them for the current tab. They are valid inside the supplier lambda, as in the example above. `tabTitle(...)` and `tabText(...)` add the text itself to the language file.

Tabs are written as files by default. Use `generateTabsAsFiles(false)` in the entry provider to write them inline in the entry JSON instead.

`EntryProvider` validates the tabs of an entry when it is built and throws an `IllegalStateException` when:

- two tabs of the same entry have the same id,
- a tab is anchored to a page id that the entry does not have.

A tab without a page also fails when the book is built.

## Associated Items

Associated items on the page of a tab are indexed like those of any other page (see [Entries](./entries.md#associated_items-itemstacktemplate--ingredient-optional)). Hovering a matching item shows the linked-page tooltip. Holding the open key opens the tab on the spread where it is visible. If the tab cannot currently be opened, for example because its condition is not met, the association is skipped in favour of other matches.

## Theme

The tab sprites and the dimming of the book behind an open tab are set by the theme. See [Theme.json](./theme.md#entry-tab-and-tab-page-sprites).

## Further reading

- [Entries](./entries.md#tabs-tab-optional) — the entry-level `tabs` field
- [Page Types](../page-types/page-types.md) — the pages that tabs can show
- [Theme.json](./theme.md) — tab sprites and `tab_overlay_dim_color`
