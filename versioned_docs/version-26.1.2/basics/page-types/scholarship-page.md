---
sidebar_position: 65
---

# Scholarship Page

A scholarship page shows the scholarship block of a stage of an entry research. It is a page of the entry, placed between the content, so the player can see what the next stage needs and check off what is done. Pages of this type are never gated by their stage, so they stay readable after the stage is complete.

Scholarship pages are described in [Entry Research](../research/entry-research.md#scholarship-pages). The block that the page draws is described in [Scholarship](../scholarship/scholarship.md).

**Page type:** `modonomicon:scholarship`

## Attributes

Common attributes like `id`, `condition` and `associated_items` are described in [Page Types](./page-types.md).

#### **title** (DescriptionId or Component JSON, _optional_)

The title of the page. If omitted, the block is titled by its stage, e.g. the stage title.

#### **text** (DescriptionId or Component JSON, _optional_)

A text shown above the block. Like other page texts, it can be styled with markdown when given as a DescriptionId.

#### **stage** (Identifier, _optional_)

The id of the research stage to show. If omitted, the next stage of the entry research is shown.

## Example

```json
{
  "type": "modonomicon:scholarship",
  "id": "next",
  "title": "my.book.my_entry.next.title",
  "text": "my.book.my_entry.next.text"
}
```

Set `stage` to show a specific stage of another research node:

```json
{
  "type": "modonomicon:scholarship",
  "id": "stage_one",
  "stage": "mymod:features/my_node/stage_1"
}
```

## Datagen

Use `EntryProvider.scholarshipPage(...)` to declare the page. It shows the next stage declared after it:

```java
this.scholarshipPage("next");
this.scholarshipPage("after", "Stage requirements", "The requirements of the next stage.");
```

`BookScholarshipPageModel.withStage(...)` sets the stage explicitly.
