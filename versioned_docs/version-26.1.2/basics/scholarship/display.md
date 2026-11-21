---
sidebar_position: 2
---

# Scholarship Display

The scholarship display decides where the scholarship block of an entry appears. It is set per entry, or once for the whole book as the default. The block is described in [Scholarship](./scholarship.md).

## Modes

| `scholarship_display` | Where the block appears |
|-----------------------|-------------------------|
| `first_page` | At the bottom of the first page. The default. |
| `trailing_page` | On a page after the last page of the entry. |
| `pages` | On the scholarship pages you place between the content. |
| `none` | Nowhere. The stages still gate the content. |

Declaring a scholarship page in datagen switches the entry to `pages`, unless you set the display explicitly.

## Setting the display

Per entry, the display is part of the `research` object of the entry, next to the `node`:

```json
{
  "id": "modonomicon:scholarship/trailing_page",
  "research": {
    "node": "modonomicon:generated/demo/nodes/entry_research/scholarship_trailing_page",
    "scholarship_display": "trailing_page"
  }
}
```

For the whole book, set `default_scholarship_display` in `book.json`. Entries without their own display use it.

In datagen, use `EntryProvider.scholarshipDisplay(ScholarshipDisplay.TRAILING_PAGE)` for an entry, and `BookModel.withDefaultScholarshipDisplay(...)` for the book.

## Stage browsing

Below the scholarship rows, the stage line shows the stage that is required. Its arrows let the player look at the stages they have already reached, so a completed stage stays readable.

## Demo

- `scholarship/first_page_display`: the default display, the block on the first page.
- `scholarship/trailing_page`: `trailing_page`, with value bars.
- `scholarship/placed_pages`: scholarship pages placed between the content.
- `scholarship/hand_authored`: `none`, gating only.

See [Entry Research](../research/entry-research.md) for how stages gate the content.
