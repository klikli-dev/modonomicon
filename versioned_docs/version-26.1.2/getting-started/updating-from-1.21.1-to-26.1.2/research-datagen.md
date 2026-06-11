---
sidebar_position: 15
title: Research system and datagen
---

# Research system and datagen

26.1.2 introduces a research system that provides research nodes with multi-stage progression, numeric research values, toast notifications, and multiple trigger types for research ingress.

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

## Parent-to-child progression

When using datagen, `BookModel.withGenerateEntryHierarchyResearch(true)` tells the `BookProvider` to automatically generate research nodes and `entry_viewed_once` hooks for entries that have parents. This produces the correct `BookResearchNodeUnlockedCondition` on each entry without manual condition work.

If you need fine-grained control, create research nodes and hooks manually in your `ResearchSubProvider`.

## Unlock conditions via research

Entry and category conditions use research nodes:

- `modonomicon:research_node_unlocked` — gates behind a completed research node
- `modonomicon:research_stage_completed` — gates behind a specific stage of a research node

Advancement-based gating is modeled through research hooks:

```java
// In your ResearchSubProvider:
var fact = researchData.fact("mymod/advancement_gate");
researchData.grantFactOnAdvancementEarned("mymod/advancement_hook", advancementId, fact);
var node = researchData.node("mymod/advancement_gate_node", fact);

// In your entry datagen:
entry.withCondition(node);
```

## Research datagen API

The research datagen API lives in `com.klikli_dev.modonomicon.api.datagen.research`. Key classes:

| Class | Purpose |
|-------|---------|
| `ResearchProvider` | Top-level orchestrator, collects bundles from sub-providers |
| `SingleResearchSubProvider` | Abstract base for authoring one research bundle |
| `ResearchDataBuilder` | Low-level collector for facts, values, nodes, hooks |
| `ResearchNodeBuilder` | Fluent builder for complex research nodes |
| `ResearchIngressHelper` | Fluent helper for research ingress from external events |
| `ResearchCache` | Shared cache for merging book-generated and authored research |

### Declaring facts and values

```java
var fact = this.fact("mymod/my_fact");
var value = this.value("mymod/my_counter");
```

### Creating nodes

```java
// Simple node requiring a fact
var node = this.node("mymod/my_node", fact);

// Node via fluent builder
this.nodeBuilder(this.node("mymod/my_node"))
    .withFact(fact)
    .withValue(new ResearchNodeSpec.ValueRequirement(value, 10))
    .build();
```

### Research ingress

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

### Multi-stage nodes

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

### Toast notifications

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

## Further reading

- [Research Datagen API changes](../../../../docs/research-datagen-api-changes.md) — full API reference
