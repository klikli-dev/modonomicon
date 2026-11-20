---
sidebar_position: 20
---

# Research Datagen

Research content is authored via datagen. The `ResearchProvider` orchestrator collects bundles from `ResearchSubProvider`s and writes JSON files.

## DataGenerators wiring

`DataGenerators.gatherData` requires a shared `LanguageProviderCache` and `ResearchCache`:

```java
var langCache = new LanguageProviderCache("en_us");
var researchCache = new ResearchCache();

generator.addProvider(true, NeoBookProvider.of(event, langCache, researchCache,
    new DemoBook(),
    new DemoIndexBook(),
    new DemoLeaflet()
));
generator.addProvider(true, NeoResearchProvider.of(event, langCache, researchCache, new DemoResearch(Modonomicon.MOD_ID)));
generator.addProvider(true, new EnUsProvider(generator.getPackOutput(), langCache));
```

`NeoBookProvider.of()` (and platform equivalents) take `LanguageProviderCache` and `ResearchCache` parameters. `NeoResearchProvider`, `FabricResearchProvider`, and `ForgeResearchProvider` are the platform-specific research providers.

## Key classes

| Class | Purpose |
|-------|---------|
| `ResearchProvider` | Top-level orchestrator, collects bundles from sub-providers |
| `SingleResearchSubProvider` | Abstract base for authoring one research bundle |
| `ResearchDataBuilder` | Low-level collector for facts, values, nodes, hooks |
| `ResearchNodeBuilder` | Fluent builder for complex research nodes |
| `ResearchIngressHelper` | Fluent helper for research ingress from external events |
| `ResearchCache` | Shared cache for merging book-generated and authored research |

## Declaring facts and values

```java
var fact = this.fact("mymod/my_fact");
var value = this.value("mymod/my_counter");
```

## Creating nodes

```java
// Simple node requiring a fact
var node = this.node("mymod/my_node", fact);

// Node via fluent builder
this.nodeBuilder(this.node("mymod/my_node"))
    .withFact(fact)
    .withValue(new ResearchNodeSpec.ValueRequirement(value, 10))
    .build();
```

## Research ingress

`this.ingress()` returns a `ResearchIngressHelper`. Each trigger it starts is a `ResearchIngress<T>`, bound to one trigger type and one target, such as an entry id, an advancement id or an item matcher. All of them share the same methods:

| Method | Effect |
|--------|--------|
| `declareFact(String path)` | Declares a new fact and authors a hook that grants it. |
| `declareFact(ResearchFactRef ref)` | Same, for a ref that has **not been declared yet**, e.g. a constant created with `ResearchFactRef.of(...)` or a ref in another namespace. Refs returned by `this.fact(...)` are already declared: pass them to `grant(ref)` instead. |
| `grant(ResearchFactRef ref)` | Authors a hook that grants an already declared fact. Several triggers may grant the same fact. |
| `grantFact(String hookPath, ResearchFactRef ref)` | Like `grant`, with an explicit hook id. |
| `declareValue(String path, int increment)` / `declareValue(ResearchValueRef ref, int increment)` | Declares a new value and authors a hook that increments it. |
| `increment(ResearchValueRef ref, int amount)` | Authors a hook that increments an already declared value. |
| `incrementValue(String hookPath, ResearchValueRef ref, int increment)` | Like `increment`, with an explicit hook id. |

The hook ids of `declareFact`, `grant`, `declareValue` and `increment` are derived: the ref's path plus `_hook`, prefixed with the ref's namespace if it differs from the bundle's namespace. If another hook already uses that id, `_2`, `_3`, ... is appended. Use `grantFact` or `incrementValue` with an explicit path if you need a fixed hook id.

```java
var fact = this.fact("mymod/my_fact");
var value = this.value("mymod/my_counter");

// When an entry is viewed, grant an already declared fact
this.ingress().onEntryViewedOnce(entryId).grant(fact);

// When an item is crafted, increment a value with an explicit hook id
this.ingress().onItemCrafted(stackTemplate)
    .incrementValue("mymod/my_hook", value, 1);

// When an advancement is earned, declare a fact and grant it in one step
this.ingress().onAdvancementEarned(advancementId)
    .declareFact("mymod/advancement_fact");
```

### Errors

Authoring mistakes throw an `IllegalArgumentException` while the research datagen runs, so they fail the build:

- Declaring the same fact, value, node or hook id twice in one bundle throws `"... is already declared"`. For example, a fact declared with `this.fact(...)` must not be declared again with `declareFact`.
- `grant` and `increment` need a ref that was declared in the same bundle. Otherwise they throw `"... was never declared in this builder"`. Declare it with `this.fact(...)` or `this.value(...)`, or use `declareFact` / `declareValue`.
- `toast(...)` on a fact, value or node that was not declared in the same bundle throws.

:::tip

Empty `facts.json`, `values.json`, `nodes.json` and `hooks.json` files are not written. Missing files are read as empty lists.

:::

### Deprecated methods

The following methods still compile, but are marked `@Deprecated(forRemoval = true)`. Replace them with:

| Deprecated | Replacement |
|------------|-------------|
| `grantFactOnEntryViewedOnce(path, entryId, ref)` | `this.ingress().onEntryViewedOnce(entryId).grantFact(path, ref)` |
| `incrementValueOnEntryViewedOnce(path, entryId, ref, n)` | `this.ingress().onEntryViewedOnce(entryId).incrementValue(path, ref, n)` |
| `grantFactOnAdvancementEarned(path, advancementId, ref)` | `this.ingress().onAdvancementEarned(advancementId).grantFact(path, ref)` |
| `incrementValueOnAdvancementEarned(path, advancementId, ref, n)` | `this.ingress().onAdvancementEarned(advancementId).incrementValue(path, ref, n)` |
| `grantFactOnItemCrafted(path, template, ref)` | `this.ingress().onItemCrafted(template).grantFact(path, ref)` |
| `incrementValueOnItemCrafted(path, template, ref, n)` | `this.ingress().onItemCrafted(template).incrementValue(path, ref, n)` |
| `grantFactOnItemAcquired(path, template, ref)` | `this.ingress().onItemAcquired(template).grantFact(path, ref)` |
| `incrementValueOnItemAcquired(path, template, ref, n)` | `this.ingress().onItemAcquired(template).incrementValue(path, ref, n)` |
| `node(ref, List, List, List, List[, toast])` | `this.nodeBuilder(ref)` with `withFact`, `withValue`, `withStage` and `withToast` |

### Generic Ingress

For custom trigger types, use the generic ingress entry point with typed targets:

```java
this.ingress().on(myTriggerType, target)
    .declareFact("mymod/my_hook");
```

The target type must match the trigger type's target type (e.g. `Identifier` for entry/advancement triggers, `ResearchItemMatcher` for item triggers, see below).

### Item triggers and tags

`onItemCrafted` and `onItemAcquired` accept an `ItemStackTemplate`, an `ItemLike`, a `TagKey<Item>` or a `ResearchItemMatcher`:

```java
// Exact item, the components of the template are checked too
this.ingress().onItemCrafted(new ItemStackTemplate(Items.DIAMOND_SWORD))
    .declareFact("mymod/sword_crafted");

// Any item of a tag
this.ingress().onItemCrafted(ItemTags.PLANKS)
    .declareFact("mymod/planks_crafted");

this.ingress().onItemAcquired(ResearchItemMatcher.tag(itemTag))
    .declareFact("mymod/ingot_acquired");
```

In JSON, a plain item template is written as `{"id": "minecraft:stick"}`, and a tag as `{"tag": "minecraft:planks"}`:

```json
"trigger_target": { "tag": "minecraft:planks" }
```

Tag hooks have no item index key, so they are checked on every event of their trigger type, not only on events for one item.

See [Custom Research Hooks](../../advanced/custom-hooks) for details.

## Entry markers

Entries can show an attention dot until a condition completes (see `marker_condition` in [Entries](../structure/entries.md#marker_condition-condition-optional)).
For the common cases the backing hook + fact + node research is generated automatically during book compilation,
either on the entry model or through the `ConditionHelper`:

```java
// In your CategoryProvider — model-based, research generated at compile time:
this.add(new MyEntry(this).generate()
        .markerUntilEntryViewed(otherEntry))
        .withParent(otherEntry);

this.add(new MyCraftingEntry(this).generate()
        .markerUntilCrafted(Items.DIAMOND_SWORD));

this.add(new MyAcquiringEntry(this).generate()
        .markerUntilAcquired(new ItemStackTemplate(Items.NETHER_STAR)));
```

```java
// ... or helper-based, combined with an explicit marker condition:
entry.withMarkerCondition(this.condition().markerUntilAcquired(Items.NETHER_STAR));
```

`markerUntilCrafted` / `markerUntilAcquired` accept `ItemStackTemplate`, `Item` or `ItemLike`.
Matching follows the item trigger semantics: the template's item must match, and every component defined
on the template must be present on the crafted/acquired stack. There is no `markerUntilAdvancement` shorthand;
advancement-backed markers use the manual hook + fact + node pattern (see [Research Conditions](./conditions.md#entry-markers)).

Generated research lands in the book's `generated/<book_id>` bundle alongside the hierarchy research:

- entry viewed: facts in `generated/<book_id>/facts/entry_viewed_once/<entry_path>`. The parent-hierarchy nodes are in `generated/<book_id>/nodes/entry_viewed_once/<entry_path>`, and explicit requirement nodes in `generated/<book_id>/nodes/entry_viewed_once/required/<entry_path>`. In both, `<entry_path>` is the entry path with `/` replaced by `_`.
- item crafted: facts in `generated/<book_id>/facts/item_crafted/<item_namespace>/<item_path>[_<components_hash>]` for item templates, or `generated/<book_id>/facts/item_crafted/tag_<tag_namespace>/<tag_path>` for tags. Nodes are in `generated/<book_id>/nodes/item_crafted/...` with the same key.
- item acquired: same scheme under `.../facts/item_acquired/...` and `.../nodes/item_acquired/...`

Node ids are deterministic from the target, so repeated requests for the same target are deduplicated into a single fact + node.
All generated paths are built by `GeneratedResearchPaths`. Fact and node ids are stored in player save data, so the scheme is kept stable: changing an existing target would create new ids and reset the progress of players who unlocked the old ones.
An explicit marker condition (`withMarkerCondition` / `withMarker`) combined with `markerUntil*` intents on the same entry
is merged with `and`.

**Demo:** [`MarkersCategory.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/MarkersCategory.java) exercises the explicit, entry-viewed, crafted and acquired variants; [`IndexMarkersCategory.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/IndexMarkersCategory.java) covers markers in the index mode book.

## Multi-stage nodes

```java
var stage1Ref = this.stageRef("mymod/stage1");
var stage2Ref = this.stageRef("mymod/stage2");

this.nodeBuilder(this.node("mymod/my_node"))
    .withFact(someFact)
    .withStage(ResearchStageSpec.valuesOnly(stage1Ref, List.of(
        new ResearchNodeSpec.ValueRequirement(someValue, 10)
    )))
    .withStage(ResearchStageSpec.valuesOnly(stage2Ref, List.of(
        new ResearchNodeSpec.ValueRequirement(someValue, 25)
    )))
    .build();
```

## Toast notifications

```java
var toast = new ResearchToastDefinition(
    "research_toast.mymod.node_unlocked",  // title key
    List.of(),                              // title args
    "research_toast.modonomicon.research.node", // description key
    someIcon                                // optional icon
);

this.nodeBuilder(this.node("mymod/my_node"))
    .withToast(toast)
    .build();
```

## Config

- `ClientConfig.showResearchToasts` (default: `true`) — controls toast notifications for research events
