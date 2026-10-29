---
sidebar_position: 20
---

# Load Conditions

Load conditions decide whether a category, entry or page is **loaded at all**. If a load condition is not met when the book is loaded (on world load and `/reload`), the element is removed entirely, as if it was never defined. It is never parsed, so it cannot cause errors, e.g. due to items or recipes that do not exist.

This is different from the regular [`condition`](./unlock-conditions.md), which only controls whether the element is *shown* to a player.

Typical use cases:

- Content for optional mod integrations, using [`modonomicon:mod_loaded`](./mod-loaded-condition).
- Modpacks removing content via datapack, e.g. a page that shows a recipe that was removed, using [`modonomicon:false`](./logic-conditions#false-condition).

## Usage

Load conditions are set via the `load_condition` field on category, entry or page JSONs (both page files and inline pages):

```json
"load_condition": {
    "type": "<type>",
    ... //condition specific fields
},
```

When a load condition is not met:

- for a **category**, all its entries and their pages are removed as well.
- for an **entry**, all its pages are removed as well.
- for a **page file** that has an `id`, an inline page with the same `id` in the entry JSON is removed as well.

## Supported Condition Types

Only conditions that do not depend on a player or world can be used as load condition:

- [`modonomicon:mod_loaded`](./mod-loaded-condition)
- [`modonomicon:true`](./logic-conditions#true-condition) / [`modonomicon:false`](./logic-conditions#false-condition)
- [`modonomicon:and`](./logic-conditions#and-condition) / [`modonomicon:or`](./logic-conditions#or-condition) / [`modonomicon:not`](./logic-conditions#not-condition), if all their children are supported as well.

Using any other condition type (e.g. research conditions) as load condition is reported as an error and the element is not loaded.

## Removing Content via Datapack

To remove content of an existing book (e.g. from another mod), override the JSON file of the element in a datapack, using the same path. The file only needs to contain the load condition:

`data/<book-namespace>/modonomicon/books/<book>/entries/<category>/<entry>/pages/<page-id>.json`
```json
{
  "id": "<page-id>",
  "load_condition": {
    "type": "modonomicon:false"
  }
}
```

The same works for category files (`categories/<category>.json`) and entry files (`entries/<category>/<entry>.json`).

If the page you want to remove is defined inline in the entry JSON (in the `pages` array), you do not need to override the whole entry: create a page file as shown above with the `id` of the inline page, and the inline page will be removed.

## Datagen

Use `withLoadCondition(...)` on `BookCategoryModel`, `BookEntryModel` and page models:

```java
entry.withLoadCondition(BookModLoadedConditionModel.create().withModId("theurgy"));
```

## Custom Condition Types

Custom condition types can support load conditions by overriding `BookCondition#supportsLoadTime()` to return `true` and implementing `BookCondition#testOnLoad(BookLoadConditionContext)`. See [Custom Conditions](../../advanced/custom-conditions).
