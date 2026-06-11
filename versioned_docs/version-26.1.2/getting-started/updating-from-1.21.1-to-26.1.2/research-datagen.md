---
sidebar_position: 15
title: Research system and datagen
---

# Research system and datagen

26.1.2 introduces a research system that provides research nodes with multi-stage progression, numeric research values, toast notifications, and multiple trigger types for research ingress.

See [Research System](../../basics/research-system) for full documentation on facts, values, nodes, hooks, datagen API, and configuration.

## Migration notes

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

### Unlock conditions via research

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
