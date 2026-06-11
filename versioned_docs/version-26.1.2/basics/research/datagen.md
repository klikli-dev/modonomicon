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

For custom trigger types, use the generic ingress entry point:

```java
this.ingress().on(myTriggerType, targetId)
    .declareFact("mymod/my_hook");
```

See [Custom Research Hooks](../../advanced/custom-hooks) for details.

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
