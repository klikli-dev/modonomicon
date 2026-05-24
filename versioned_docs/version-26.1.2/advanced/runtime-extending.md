---
sidebar_position: 35
---

# Runtime Extending

Modonomicon can be extended at runtime through a small additive API.

This is meant for mods that want to add categories, entries, or pages in code, for example to account for dynamic content.

## When to call it

The most common use case is addition in the "common setup" phase (after you registered your custom content types, if any). 
However, the same API can also be used later at runtime. In that case Modonomicon rebuilds and re-syncs the affected books automatically.

The recommended usage pattern is:

- open a batch (in common setup)
- add all runtime content you want
- finish the batch

If the target book is not loaded yet, Modonomicon queues the additions and applies them once the book data is available.


## Entry point

Use `ModonomiconAPI.get().openRuntimeContentBatch()`.

The batch is selector-based:

- `book(bookId)`
- `addCategory(category)`
- `category(categoryId)`
- `addEntry(entry)`
- `entry(entryId)`
- `addPage(page)`
- `finish()`

The batch also implements `AutoCloseable`, so try-with-resources works well.

## Example

```java
try (var batch = ModonomiconAPI.get().openRuntimeContentBatch()) {
    batch.book(bookId).addCategory(category);

    batch.book(bookId)
            .category(categoryId)
            .addEntry(entry);

    batch.book(bookId)
            .category(categoryId)
            .entry(entryId)
            .addPage(page);
}
```

The objects you pass in are regular fully constructed `BookCategory`, `BookEntry`, and `BookPage` instances.

## Behavior and limitations

- additive only: no remove or replace support
- server-authoritative: clients receive the synced result from the server. Additions in "common" code are save (and recommended), but the server side lifecycle is the authority (client side will be discarded at sync).
- runtime content is not persisted; register it again on every startup
- batches may touch multiple books
- missing parents fail fast instead of being created implicitly

## Common setup notes

Calling the API from common setup is safe even though common setup also runs on the client side.

The useful effect still comes from the server lifecycle: Modonomicon queues the additions during startup and applies them when the server-side book data is loaded.

That means you do **not** need a separate sync call or a special “startup done” call.

## Demo reference

See the runtime demo used in Modonomicon itself:

- [`DemoRuntimeBookContent`](https://github.com/klikli-dev/modonomicon/blob/HEAD/common/src/main/java/com/klikli_dev/modonomicon/book/runtime/DemoRuntimeBookContent.java)

That class shows:

- adding a whole category at runtime
- adding an entry through the runtime API
- appending a page to an existing entry
