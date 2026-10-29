---
sidebar_position: 25
---

# Load Conditions

Load conditions decide whether a category, entry or page is **loaded at all**. If a load condition is not met when the book is loaded (on world load and `/reload`), the element is removed entirely, as if it was never defined. It is never parsed, so it cannot cause errors, e.g. due to items or recipes that do not exist.

This is different from the regular [`condition`](./unlock-conditions.md), which only controls whether the element is *shown* to a player.

Typical use cases:

- Content for optional mod integrations, using [`modonomicon:mod_loaded`](./mod-loaded-condition).
- Modpacks removing content via datapack, e.g. a page that shows a recipe that was removed, using [`modonomicon:false`](./false-condition).

## Usage

Load conditions are set via the `load_condition` field on category, entry or page JSONs (both file-based pages and inline pages):

```json
"load_condition": {
    "type": "<type>",
    ... //condition specific fields
},
```

When a load condition is not met:

- for a **category**, all its entries and their pages are removed as well.
- for an **entry**, all its pages are removed as well.
- for a **file-based page** with a target, the targeted inline page in the entry JSON is removed as well. See [File-based Pages](../structure/entries.md#file-based-pages).

## Supported Condition Types

Only conditions that do not depend on a player or world can be used as load condition:

- [`modonomicon:mod_loaded`](./mod-loaded-condition)
- [`modonomicon:true`](./true-condition) / [`modonomicon:false`](./false-condition)
- [`modonomicon:and`](./and-condition) / [`modonomicon:or`](./or-condition) / [`modonomicon:not`](./not-condition), if all their children are supported as well.

Using any other condition type (e.g. advancement conditions) as load condition is reported as an error and the element is not loaded.

:::tip

For backwards compatibility the regular `condition` is still evaluated at load time with its historic behavior as well, so books made for older versions keep working. New content should use `load_condition`, which properly applies to all content types, including inline pages.

:::

## Removing Content via Datapack

To remove content of an existing book (e.g. from another mod), you do not need to override whole files. For pages, add a file-based page with a target and a failing load condition, using the same `pages/` path scheme. The file only needs the target and the load condition:

`data/<book-namespace>/modonomicon/books/<book>/pages/<category>/.../<entry>/<any-name>.json`
```json
{
  "id": "<page-id>",
  "load_condition": {
    "type": "modonomicon:false"
  }
}
```

Instead of `id` the page can also be targeted by `anchor` or `index`. See [File-based Pages](../structure/entries.md#file-based-pages) for the targeting rules.

The same works for category files (`categories/<category>.json`) and entry files (`entries/<category>/<entry>.json`) by overriding them with just a `load_condition` — categories and entries have no file-based patches, so those files do need to override the whole JSON.

Live samples that override and remove pages of the demo book entry `modonomicon:features/file_pages`:
[demo page files on GitHub](https://github.com/klikli-dev/modonomicon/tree/version/1.21.1/common/src/main/resources/data/modonomicon/modonomicon/books/demo/pages/features/file_pages)

## Datagen

Use `withLoadCondition(...)` on `BookCategoryModel`, `BookEntryModel` and page models:

```java
entry.withLoadCondition(BookModLoadedConditionModel.create().withModId("theurgy"));
```

## Custom Condition Types

Custom conditions can support load conditions by overriding `BookCondition#supportsLoadTime()` to return `true` and implementing `BookCondition#testOnLoad(BookLoadConditionContext)`. See [Custom Conditions](../../advanced/custom-conditions).
