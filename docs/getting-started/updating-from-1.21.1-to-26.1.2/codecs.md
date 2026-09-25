---
sidebar_position: 5
title: Codec and content type changes
---

# Codec and content type changes

26.1.2 moves book content loading onto codecs.
If you maintain custom page types, custom loaders, or hand-written JSON content, this is the migration you need.

## Summary

The old loader-based setup was replaced by typed registries backed by codecs and stream codecs.
In practice that means:

- custom page types are now registered with [`BookPageTypeRegistry`](https://github.com/klikli-dev/modonomicon/blob/HEAD/common/src/main/java/com/klikli_dev/modonomicon/registry/BookPageTypeRegistry.java)
- (builtin and custom) page classes now declare `ID`, `CODEC`, and `STREAM_CODEC`
- page instances now return a `BookPageType<?>` from `type()` instead of an id from `getType()`
- raw JSON content now uses more explicit ids and types

## Custom page registration changed

If you previously registered custom pages through `LoaderRegistry.registerPageLoader(...)`, that registration path is gone.
Instead, create a small registry content container class and register your page type with `BookPageTypeRegistry.register(...)`.

See these real migrations:

- [`OccultismModonomiconPageTypeRegistry`](https://github.com/klikli-dev/occultism/blob/HEAD/src/main/java/com/klikli_dev/occultism/integration/modonomicon/OccultismModonomiconPageTypeRegistry.java)
- [`TheurgyModonomiconPageTypeRegistry`](https://github.com/klikli-dev/theurgy/blob/HEAD/src/main/java/com/klikli_dev/theurgy/integration/modonomicon/TheurgyModonomiconPageTypeRegistry.java)

The pattern is:

1. create a static `BookPageType<T>` field per custom page
2. register it with page `ID`, `CODEC`, and `STREAM_CODEC`
3. call your registry class' `bootstrap()` during common setup so the class loads

That replaces the old `PageLoaders` class entirely.

## Custom page classes changed

Each custom page class now owns its serialization setup.

See these examples:

- Occultism: [`BookSpiritFireRecipePage`](https://github.com/klikli-dev/occultism/blob/HEAD/src/main/java/com/klikli_dev/occultism/integration/modonomicon/pages/BookSpiritFireRecipePage.java), [`BookBindingCraftingRecipePage`](https://github.com/klikli-dev/occultism/blob/HEAD/src/main/java/com/klikli_dev/occultism/integration/modonomicon/pages/BookBindingCraftingRecipePage.java)
- Theurgy: [`BookAccumulationRecipePage`](https://github.com/klikli-dev/theurgy/blob/HEAD/src/main/java/com/klikli_dev/theurgy/integration/modonomicon/page/accumulation/BookAccumulationRecipePage.java), [`BookCalcinationRecipePage`](https://github.com/klikli-dev/theurgy/blob/HEAD/src/main/java/com/klikli_dev/theurgy/integration/modonomicon/page/calcination/BookCalcinationRecipePage.java)

Typical changes:

- add a static `ID`
- add a static `CODEC`
- add a static `STREAM_CODEC`
- remove old `fromJson(...)` and `fromNetwork(...)` helpers
- replace `getType()` with `type()` and return your registered `BookPageType<?>`
- if you override `toNetwork(...)`, delegate to your `STREAM_CODEC`

For built-in reference, see [`BookPageType`](https://github.com/klikli-dev/modonomicon/blob/HEAD/common/src/main/java/com/klikli_dev/modonomicon/data/BookPageType.java).

## Datagen page models changed

If you generate custom pages in code, update your page models as well.

[`BookRecipePageModel`](https://github.com/klikli-dev/modonomicon/blob/HEAD/common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/page/BookRecipePageModel.java) now expects subclasses to construct the final runtime page through `createPage(...)`.

See these migrations:

- Occultism: [`BookSpiritFireRecipePageModel`](https://github.com/klikli-dev/occultism/blob/HEAD/src/main/java/com/klikli_dev/occultism/integration/modonomicon/pages/BookSpiritFireRecipePageModel.java), [`BookRitualRecipePageModel`](https://github.com/klikli-dev/occultism/blob/HEAD/src/main/java/com/klikli_dev/occultism/integration/modonomicon/pages/BookRitualRecipePageModel.java)
- Theurgy: [`BookAccumulationRecipePageModel`](https://github.com/klikli-dev/theurgy/blob/HEAD/src/main/java/com/klikli_dev/theurgy/integration/modonomicon/page/accumulation/BookAccumulationRecipePageModel.java), [`BookLiquefactionRecipePageModel`](https://github.com/klikli-dev/theurgy/blob/HEAD/src/main/java/com/klikli_dev/theurgy/integration/modonomicon/page/liquefaction/BookLiquefactionRecipePageModel.java)

In most cases the change is small:

- keep using your page `ID` as the model type id
- implement `createPage(BookRecipePage.JsonDataHolder common)`
- return the concrete runtime page instance from that method

## Raw JSON content is now more explicit

The built-in demo content is a good reference for the new format:

- [`book.json`](https://github.com/klikli-dev/modonomicon/blob/HEAD/neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/book.json)
- [`categories/features.json`](https://github.com/klikli-dev/modonomicon/blob/HEAD/neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/categories/features.json)
- [`entries/features/recipe.json`](https://github.com/klikli-dev/modonomicon/blob/HEAD/neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/recipe.json)
- [`entries/features/redirect.json`](https://github.com/klikli-dev/modonomicon/blob/HEAD/neo/src/generated/resources/data/modonomicon/modonomicon/books/demo/entries/features/redirect.json)

The main JSON-facing changes are:

- entries now have an explicit top-level `type`
- content entries now have an explicit top-level `id`
- ids that used to be written as relative paths should now generally be fully qualified, for example `modonomicon:features/recipe`
- references such as `category`, `entry`, `entry_id`, `category_to_open`, `multiblock_id`, and `leaflet_entry` should use namespaced ids
- many fields with default values are no longer emitted in generated JSON

So if your old JSON used values like `features`, `features/recipe`, or `demo_block_entity`, update them to full ids such as `modonomicon:features`, `modonomicon:features/recipe`, and `modonomicon:demo_block_entity`.

## Multiblock matcher code changed too

If you interact with multiblock matchers in code, note that downstream integrations also had to update matcher type checks.
See these examples:

- [`RitualSatchelItem`](https://github.com/klikli-dev/occultism/blob/HEAD/src/main/java/com/klikli_dev/occultism/common/item/tool/ritual_satchel/RitualSatchelItem.java)
- [`MultiBlockRitualSatchelItem`](https://github.com/klikli-dev/occultism/blob/HEAD/src/main/java/com/klikli_dev/occultism/common/item/tool/ritual_satchel/MultiBlockRitualSatchelItem.java)
- [`StateMatcher`](https://github.com/klikli-dev/modonomicon/blob/HEAD/common/src/main/java/com/klikli_dev/modonomicon/api/multiblock/StateMatcher.java)
- [`StateMatcherTypeRegistry`](https://github.com/klikli-dev/modonomicon/blob/HEAD/common/src/main/java/com/klikli_dev/modonomicon/registry/StateMatcherTypeRegistry.java)

Use `stateMatcher().type()` and compare against registry entries like `StateMatcherTypeRegistry.ANY` or `StateMatcherTypeRegistry.DISPLAY`.
Do not rely on the old `getType()` plus matcher-class constants pattern.

## Recommended migration checklist

1. Delete old custom `PageLoaders` usage.
2. Register each custom page with `BookPageTypeRegistry.register(...)`.
3. Add `ID`, `CODEC`, and `STREAM_CODEC` to each custom page class.
4. Replace `getType()` with `type()`.
5. Update datagen page models to implement `createPage(...)`.
6. Review hand-written JSON for explicit `type` and namespaced ids.
7. If you touch multiblock matcher code, switch to `stateMatcher().type()` and the new registry constants.

After that, re-run datagen and compare your output against the built-in demo content.
