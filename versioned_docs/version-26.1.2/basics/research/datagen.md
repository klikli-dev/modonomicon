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

```java
// When an entry is viewed, grant a fact
this.ingress().onEntryViewedOnce(entryId).grantFact("mymod/my_hook", fact);

// When an item is crafted, increment a value
this.ingress().onItemCrafted(stackTemplate)
    .incrementValue("mymod/my_hook", value, 1);

// When an advancement is earned, grant a fact
this.ingress().onAdvancementEarned(advancementId)
    .declareFact("mymod/advancement_fact");
```

### Generic Ingress

For custom trigger types, use the generic ingress entry point with typed targets:

```java
this.ingress().on(myTriggerType, target)
    .declareFact("mymod/my_hook");
```

The target type must match the trigger type's target type (e.g. `Identifier` for entry/advancement triggers, `ItemStackTemplate` for item triggers).

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

- entry viewed: facts in `generated/<book_id>/facts/entry_viewed_once/<entry_path>`, nodes in `generated/<book_id>/nodes/entry_viewed_once/...`
- item crafted: facts in `generated/<book_id>/facts/item_crafted/<namespace>_<item_path>[_<components_hash>]`, nodes in `generated/<book_id>/nodes/item_crafted/...`
- item acquired: same scheme under `.../facts/item_acquired/...` and `.../nodes/item_acquired/...`

Node ids are deterministic from the target, so repeated requests for the same target are deduplicated into a single fact + node.
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
