---
sidebar_position: 35
---

# Research System

The research system tracks player progression through facts, values, nodes, and hooks. Research content is authored via datagen and compiled into JSON files under `data/<namespace>/modonomicon/research/<bundle_path>/`.

## Core Concepts

### Facts

A fact is a boolean flag. It is either granted or not. Facts are declared with a path and referenced by a `ResearchFactRef`.

### Values

A value is a numeric counter. Values are incremented by hooks and checked against thresholds in nodes. Declared with a path and referenced by a `ResearchValueRef`.

### Nodes

A node represents a milestone in the research tree. Nodes require facts and/or values to be completed. Nodes can have stages for multi-step progression.

- **Required facts** — all listed facts must be granted
- **Required values** — all listed value thresholds must be met
- **Stages** — ordered steps within the node, each with their own requirements
- **Required stages** — dependencies on other nodes reaching specific stages

### Hooks

Hooks connect external events to research progress. Each hook triggers on an event and either grants a fact or increments a value.

| Hook Type | Event |
|-----------|-------|
| `entry_viewed_once` | A book entry is viewed for the first time |
| `item_crafted` | A specific item is crafted |
| `item_acquired` | A specific item appears in inventory |
| `advancement` | A vanilla advancement is earned |

### Toast Notifications

Research facts, values, nodes, and stages can show toast notifications when triggered. Toasts are defined with a title key, optional description key, optional icon, and optional static arguments.

## Generated JSON Structure

Research datagen produces these files per bundle:

- `facts.json` — array of `{ "id": "namespace/path" }`
- `values.json` — array of `{ "id": "namespace/path" }`
- `nodes.json` — node definitions with required_facts, required_values, stages, required_stages, and optional toast
- `hooks.json` — merged entry_viewed_once, item_crafted, and item_acquired hooks
- `advancement_hooks.json` — advancement-triggered hooks

## Runtime

The `ResearchStateManager` tracks per-player research state. Research hooks fire automatically via dedicated services (e.g. `AdvancementResearchHookService`). Toast notifications are sent from server to client via `ResearchToastMessage`.

## Datagen

Research content is authored via `ResearchSubProvider` or `SingleResearchSubProvider`. The `ResearchProvider` orchestrator collects bundles and writes the JSON files. `BookProvider` can also generate research for entry hierarchies when `withGenerateEntryHierarchyResearch(true)` is set.

### DataGenerators wiring

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

### Parent-to-child progression

When using datagen, `BookModel.withGenerateEntryHierarchyResearch(true)` tells the `BookProvider` to automatically generate research nodes and `entry_viewed_once` hooks for entries that have parents. This produces the correct `BookResearchNodeUnlockedCondition` on each entry without manual condition work.

If you need fine-grained control, create research nodes and hooks manually in your `ResearchSubProvider`.

### Research datagen API

The research datagen API lives in `com.klikli_dev.modonomicon.api.datagen.research`. Key classes:

| Class | Purpose |
|-------|---------|
| `ResearchProvider` | Top-level orchestrator, collects bundles from sub-providers |
| `SingleResearchSubProvider` | Abstract base for authoring one research bundle |
| `ResearchDataBuilder` | Low-level collector for facts, values, nodes, hooks |
| `ResearchNodeBuilder` | Fluent builder for complex research nodes |
| `ResearchIngressHelper` | Fluent helper for research ingress from external events |
| `ResearchCache` | Shared cache for merging book-generated and authored research |

#### Declaring facts and values

```java
var fact = this.fact("mymod/my_fact");
var value = this.value("mymod/my_counter");
```

#### Creating nodes

```java
// Simple node requiring a fact
var node = this.node("mymod/my_node", fact);

// Node via fluent builder
this.nodeBuilder(this.node("mymod/my_node"))
    .withFact(fact)
    .withValue(new ResearchNodeSpec.ValueRequirement(value, 10))
    .build();
```

#### Research ingress

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

#### Multi-stage nodes

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

#### Toast notifications

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

- [Research Datagen API changes](../../../docs/research-datagen-api-changes.md) — full API reference
