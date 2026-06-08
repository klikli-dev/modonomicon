# Research System Datagen API Changes

## Overview

This branch introduces a complete research system with datagen support, including research nodes with multi-stage progression, research values (numeric counters), toast notifications, and multiple trigger types for research ingress.

---

## New Types (Authoring-Time Refs)

Typed authoring-time references prevent mixing up different research element IDs at compile time.

### `ResearchNodeRef`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef`
- Record wrapping an `Identifier` for a research node.
- Factory: `ResearchNodeRef.of(Identifier id)`

### `ResearchFactRef`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchFactRef`
- Record wrapping an `Identifier` for a research fact.
- Factory: `ResearchFactRef.of(Identifier id)`

### `ResearchValueRef`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchValueRef`
- Record wrapping an `Identifier` for a research value.
- Factory: `ResearchValueRef.of(Identifier id)`

### `ResearchStageRef`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef`
- Record wrapping an `Identifier` for a research stage within a node.
- Factory: `ResearchStageRef.of(Identifier id)`

---

## New Types (Authoring-Time Specs)

Specs are the authoring-time representation of research elements. They compile into the canonical definition records.

### `ResearchNodeSpec`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeSpec`
- Record: `(ref, requiredFacts, requiredValues, stages, requiredStages, toast)`
- `requiredFacts`: `List<ResearchFactRef>` - fact refs that must be granted for unlock
- `requiredValues`: `List<ValueRequirement>` - value thresholds that must be met
- `stages`: `List<ResearchStageSpec>` - ordered stages defining node progression
- `requiredStages`: `List<StageDependencySpec>` - dependencies on other nodes reaching specific stages
- `toast`: `Optional<ResearchToastDefinition>` - optional toast on full node completion
- Nested records:
  - `ValueRequirement(ResearchValueRef valueRef, int threshold)` - a value + threshold pair
  - `StageDependencySpec(ResearchNodeRef nodeId, ResearchStageRef stageId)` - dependency on another node's stage
- Factory: `ResearchNodeSpec.factsOnly(ref, requiredFacts)` for simple nodes
- Method: `toast(ResearchToastDefinition)` - returns new spec with toast added

### `ResearchStageSpec`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchStageSpec`
- Record: `(ref, requiredFacts, requiredValues, toast)`
- Represents one stage within a multi-stage research node.
- Factories:
  - `ResearchStageSpec.factsOnly(ref, requiredFacts)` - stage requiring facts
  - `ResearchStageSpec.valuesOnly(ref, requiredValues)` - stage requiring value thresholds
  - `ResearchStageSpec.none(ref)` - stage with no requirements (auto-completes)
- Method: `toast(ResearchToastDefinition)` - returns new spec with toast added

### `ResearchFactSpec`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchFactSpec`
- Record: `(ref, toast)`
- Compiles to `ResearchFactDefinition`.
- Factory: `ResearchFactSpec.of(ref)`

### `ResearchValueSpec`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchValueSpec`
- Record: `(ref, toast)`
- Compiles to `ResearchValueDefinition`.
- Factory: `ResearchValueSpec.of(ref)`

---

## Hook Specs (Trigger Declarations)

Each hook spec represents one research ingress trigger. Hooks either grant a fact OR increment a value, never both.

### `EntryViewedOnceHookSpec`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.EntryViewedOnceHookSpec`
- Record: `(id, entryId, factRef, valueRef, increment)`
- Triggers when a book entry is viewed for the first time.
- Factories:
  - `grantFact(id, entryId, factRef)` - grants a fact on first view
  - `incrementValue(id, entryId, valueRef, increment)` - increments a value on first view

### `ItemCraftedHookSpec`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ItemCraftedHookSpec`
- Record: `(id, targetItem, factRef, valueRef, increment, matchComponents)`
- Triggers when a specific item is crafted.
- `matchComponents` (default `false`): when true, also checks data components on the item.
- Factories:
  - `grantFact(id, targetItem, factRef)` / `grantFact(id, targetItem, factRef, matchComponents)`
  - `incrementValue(id, targetItem, valueRef, increment)` / with `matchComponents` variant

### `ItemAcquiredHookSpec`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ItemAcquiredHookSpec`
- Record: `(id, targetItem, factRef, valueRef, increment, matchComponents)`
- Triggers when a specific item is acquired (inventory change detection).
- Same factory pattern as `ItemCraftedHookSpec`.

### `AdvancementHookSpec`
**Path:** `com.klikli_dev.modonomicon.api.datagen.research.AdvancementHookSpec`
- Record: `(id, advancementId, factRef, valueRef, increment)`
- Triggers when a vanilla advancement is earned.
- Factories:
  - `grantFact(id, advancementId, factRef)`
  - `incrementValue(id, advancementId, valueRef, increment)`

---

## Toast System

### `ResearchToastDefinition`
**Path:** `com.klikli_dev.modonomicon.research.data.ResearchToastDefinition`
- Record: `(title, titleArgs, description, icon)`
- `title`: translatable key for the toast title (second line)
- `titleArgs`: `List<Component>` - static component arguments (values auto-append current count)
- `description`: translatable key for the toast category/description (first line), null uses default
- `icon`: `BookIcon` - optional icon to render; null means no icon
- Has `CODEC` and `STREAM_CODEC` for serialization.

### `ResearchToastTrigger`
**Path:** `com.klikli_dev.modonomicon.research.networking.ResearchToastTrigger`
- Record: `(type, elementId, currentValue)`
- Enum `ToastTriggerType`: `FACT_GRANTED`, `VALUE_INCREMENTED`, `NODE_UNLOCKED`, `NODE_STAGE_COMPLETED`
- Sent from server to client to trigger toast display.

### `ResearchToastMessage`
**Path:** `com.klikli_dev.modonomicon.networking.ResearchToastMessage`
- Network message containing a list of `ResearchToastTrigger`s.
- Client-side handler resolves toast definitions and displays `ResearchToast`.

---

## Core Builder: `ResearchDataBuilder`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchDataBuilder`

The low-level collector that compiles authored input into canonical definition records.

### Element Declaration Methods
| Method | Returns | Description |
|--------|---------|-------------|
| `fact(String path)` | `ResearchFactRef` | Declares a research fact |
| `value(String path)` | `ResearchValueRef` | Declares a research value |
| `node(String path, ResearchFactRef... requiredFacts)` | `ResearchNodeRef` | Declares a simple fact-only node |
| `node(ResearchNodeRef ref, ResearchFactRef... requiredFacts)` | `ResearchNodeRef` | Declares node using existing ref |
| `node(ResearchNodeRef ref, List<ResearchFactRef>, List<ValueRequirement>)` | `ResearchNodeRef` | Declares node with facts + values |
| `node(ResearchNodeRef ref, List<ResearchFactRef>, List<ValueRequirement>, List<ResearchStageSpec>, List<StageDependencySpec>)` | `ResearchNodeRef` | Declares node with stages + deps |
| `node(ResearchNodeRef ref, ..., ResearchToastDefinition toast)` | `ResearchNodeRef` | Full overload with toast |
| `stageRef(String path)` | `ResearchStageRef` | Creates a typed stage ref |
| `nodeBuilder(ResearchNodeRef ref)` | `ResearchNodeBuilder` | Returns fluent builder |

### Hook Declaration Methods
| Method | Description |
|--------|-------------|
| `grantFactOnEntryViewedOnce(path, entryId, factRef)` | Hook: entry view grants fact |
| `incrementValueOnEntryViewedOnce(path, entryId, valueRef, increment)` | Hook: entry view increments value |
| `grantFactOnItemCrafted(path, targetItem, factRef)` | Hook: item crafted grants fact |
| `grantFactOnItemCrafted(path, targetItem, factRef, matchComponents)` | Same, with component matching |
| `incrementValueOnItemCrafted(path, targetItem, valueRef, increment)` | Hook: item crafted increments value |
| `incrementValueOnItemCrafted(path, targetItem, valueRef, increment, matchComponents)` | Same, with component matching |
| `grantFactOnItemAcquired(path, targetItem, factRef)` | Hook: item acquired grants fact |
| `grantFactOnItemAcquired(path, targetItem, factRef, matchComponents)` | Same, with component matching |
| `incrementValueOnItemAcquired(path, targetItem, valueRef, increment)` | Hook: item acquired increments value |
| `incrementValueOnItemAcquired(path, targetItem, valueRef, increment, matchComponents)` | Same, with component matching |
| `grantFactOnAdvancementEarned(path, advancementId, factRef)` | Hook: advancement grants fact |
| `incrementValueOnAdvancementEarned(path, advancementId, valueRef, increment)` | Hook: advancement increments value |

### Toast Attachment Methods
| Method | Description |
|--------|-------------|
| `toast(ResearchFactRef, ResearchToastDefinition)` | Attach toast to an existing fact |
| `toast(ResearchValueRef, ResearchToastDefinition)` | Attach toast to an existing value |
| `toast(ResearchNodeRef, ResearchToastDefinition)` | Attach toast to an existing node |

### Output Methods
| Method | Returns | Description |
|--------|---------|-------------|
| `factDefinitions()` | `List<ResearchFactDefinition>` | Compiled fact definitions |
| `valueDefinitions()` | `List<ResearchValueDefinition>` | Compiled value definitions |
| `nodeDefinitions()` | `List<ResearchNodeDefinition>` | Compiled node definitions |
| `hookDefinitions()` | `List<ResearchHookDefinition>` | Compiled entry_viewed_once hooks |
| `itemCraftedHookDefinitions()` | `List<ResearchHookDefinition>` | Compiled item_crafted hooks |
| `itemAcquiredHookDefinitions()` | `List<ResearchHookDefinition>` | Compiled item_acquired hooks |
| `advancementHookDefinitions()` | `List<AdvancementResearchHookDefinition>` | Compiled advancement hooks |

---

## Fluent Builder: `ResearchNodeBuilder`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeBuilder`

Fluent API for authoring complex research nodes. Created via `ResearchDataBuilder.nodeBuilder(ref)`.

### Methods
| Method | Description |
|--------|-------------|
| `withFact(ResearchFactRef)` | Add a fact requirement |
| `withFacts(List<ResearchFactRef>)` | Add multiple fact requirements |
| `withValue(ValueRequirement)` | Add a value requirement |
| `withValues(List<ValueRequirement>)` | Add multiple value requirements |
| `withStage(ResearchStageSpec)` | Add a stage |
| `withStages(List<ResearchStageSpec>)` | Add multiple stages |
| `withStageDependency(ResearchNodeRef, ResearchStageRef)` | Add dependency on another node's stage |
| `withStageDependency(StageDependencySpec)` | Add stage dependency directly |
| `withStageDependencies(List<StageDependencySpec>)` | Add multiple stage dependencies |
| `withToast(ResearchToastDefinition)` | Set toast for full node completion |
| `build()` | Register node and return `ResearchNodeRef` |

---

## Ingress Helper: `ResearchIngressHelper`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchIngressHelper`

Fluent helper for authoring research ingress from external events.

### Top-Level Methods
| Method | Returns | Description |
|--------|---------|-------------|
| `onEntryViewedOnce(Identifier entryId)` | `EntryViewedOnceIngress` | Start entry-view ingress |
| `onAdvancementEarned(Identifier advancementId)` | `AdvancementEarnedIngress` | Start advancement ingress |
| `onItemCrafted(ItemStackTemplate targetItem)` | `ItemCraftedIngress` | Start item-crafted ingress |
| `onItemAcquired(ItemStackTemplate targetItem)` | `ItemAcquiredIngress` | Start item-acquired ingress |

### Trigger-Specific Ingress Methods (shared pattern)
Each trigger-specific ingress class provides:
| Method | Description |
|--------|-------------|
| `declareFact(String factPath)` | Declare a new fact AND create a hook that grants it on trigger. Returns the `ResearchFactRef`. |
| `grantFact(String hookPath, ResearchFactRef)` | Create a hook that grants an existing fact on trigger. |
| `declareValue(String valuePath, int increment)` | Declare a new value AND create a hook that increments it on trigger. Returns the `ResearchValueRef`. |
| `incrementValue(String hookPath, ResearchValueRef, int increment)` | Create a hook that increments an existing value on trigger. |

---

## `ResearchProviderBase`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchProviderBase`

Abstract base class for research datagen providers. Extend this to author research content.

### Key Methods
| Method | Description |
|--------|-------------|
| `modLoc(String path)` | Create identifier under mod namespace |
| `mcLoc(String path)` | Create identifier under minecraft namespace |
| `fact(String path)` | Declare a fact via active `ResearchDataBuilder` |
| `value(String path)` | Declare a value via active `ResearchDataBuilder` |
| `node(String path, ResearchFactRef...)` | Declare a simple node |
| `node(ResearchNodeRef, ...)` | Declare node with existing ref (multiple overloads) |
| `stageRef(String path)` | Create a typed stage ref |
| `nodeBuilder(ResearchNodeRef)` | Get a fluent `ResearchNodeBuilder` |
| `ingress()` | Get a `ResearchIngressHelper` |
| `lang()` | Get injected `ModonomiconLanguageProvider` |
| `add(String key, String value)` | Add translation via injected lang provider |
| `researchNodeDescriptionId(ResearchNodeRef)` | Get description ID for node tooltip |
| `researchStageDescriptionId(ResearchStageRef)` | Get description ID for stage tooltip |
| `researchNodeName(ResearchNodeRef, String)` | Convenience: register node display name |
| `researchStageName(ResearchStageRef, String)` | Convenience: register stage display name |

---

## `ResearchProvider`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchProvider`

Top-level datagen orchestrator. Collects bundles from `ResearchSubProvider`s and writes research resource files.

### Constructors
| Constructor | Description |
|-------------|-------------|
| `ResearchProvider(PackOutput, CompletableFuture<Provider>, String modId, List<ResearchSubProvider>)` | Basic constructor |
| `(..., ResearchCache)` | With research cache for merging book-generated research |
| `(..., ResearchCache, LanguageProviderCache)` | Also with language cache |

### Generated Files Per Bundle
- `facts.json` - All fact definitions
- `values.json` - All value definitions
- `nodes.json` - All node definitions
- `hooks.json` - Merged hook definitions (entry_viewed_once + item_crafted + item_acquired)
- `advancement_hooks.json` - Advancement hook definitions

---

## `ResearchCache`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchCache`

Shared cache for research bundles during datagen. Used by `BookProvider` to contribute book-generated research (entry hierarchy) that merges with authored research.

| Method | Description |
|--------|-------------|
| `accept(Identifier bundleId, ResearchDataBuilder)` | Add a bundle (throws on duplicate) |
| `merge(Identifier bundleId, ResearchDataBuilder)` | Add/override a bundle |
| `build()` | Returns unmodifiable map of all bundles |

---

## `ResearchBundle`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchBundle`

Simple record wrapping a `ResearchDataBuilder`. Emitted by `ResearchSubProvider`s.

---

## `ResearchSubProvider`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.ResearchSubProvider`

Interface for research datagen extension seam.

```java
void generate(BiConsumer<Identifier, ResearchBundle> consumer, HolderLookup.Provider registries);
```

---

## Book Condition Changes

### `BookResearchNodeUnlockedCondition` (runtime)
**Path:** `com.klikli_dev.modonomicon.book.conditions.BookResearchNodeUnlockedCondition`
- Now handles multi-stage nodes: checks if `stageIndex >= totalStages` for full completion.
- Auto-generates tooltip using `Util.makeDescriptionId("research_node", nodeId)` if none provided.

### `BookResearchNodeUnlockedConditionModel` (datagen)
**Path:** `com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel`
- Added `withNodeName()` - auto-sets tooltip from node's description ID translation key.
- Added `withNode(String nodeId)` overload accepting a string.

### `BookResearchStageCompletedCondition` (runtime)
**Path:** `com.klikli_dev.modonomicon.book.conditions.BookResearchStageCompletedCondition`
- New condition type: `research_stage_completed`.
- Requires both `node_id` and `stage_id`.
- Auto-generates tooltip using description IDs for both node and stage.

### `BookResearchStageCompletedConditionModel` (datagen)
**Path:** `com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel`
- New condition model.
- Methods: `withNode(Identifier)`, `withStage(Identifier)`, `withStageName()` (auto-tooltip).

### `ConditionHelper`
**Path:** `com.klikli_dev.modonomicon.api.datagen.ConditionHelper`
- Added `researchNodeUnlocked(ResearchNodeRef)` - accepts typed ref.
- Added `researchStageCompleted(ResearchNodeRef, ResearchStageRef)` - new stage condition.

### `BookEntryModel`
**Path:** `com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel`
- Added `withCondition(ResearchNodeRef)` - convenience for node-unlocked condition.
- Added `withCondition(ResearchNodeRef, ResearchStageRef)` - convenience for stage-completed condition.

---

## `BookHierarchyResearchCompiler`

**Path:** `com.klikli_dev.modonomicon.api.datagen.BookHierarchyResearchCompiler`

Compiles book entry hierarchies into research data. Automatically generates `entry_viewed_once` hooks and research nodes for entries with parents.

- Uses `ResearchIngressHelper` for fluent hook authoring.
- Produces `CompiledBookResearch` record containing the bundle ID and `ResearchDataBuilder`.

---

## Translation Keys

New i18n keys added (in `en_us.json`):
- `research_toast.modonomicon.research.fact` - Default toast description for fact grants
- `research_toast.modonomicon.research.value` - Default toast description for value increments
- `research_toast.modonomicon.research.node` - Default toast description for node unlocks
- `research_toast.modonomicon.research.stage` - Default toast description for stage completions
- Various research node/stage display names for demo content
- Condition tooltip translations for research-based conditions

---

## Example Usage

### Simple Node with Ingress
```java
// Declare a fact
var fact = this.fact("demo/crafting_stick");

// Create ingress: when entry is viewed, grant the fact
this.ingress().onEntryViewedOnce(entryId).grantFact("hook_path", fact);

// Create a node requiring that fact
var node = this.node("demo/node", fact);
```

### Multi-Stage Node with Fluent Builder
```java
var stage1Ref = this.stageRef("demo/stage1");
var stage2Ref = this.stageRef("demo/stage2");

this.nodeBuilder(myNode)
    .withFact(someFact)
    .withStage(ResearchStageSpec.valuesOnly(stage1Ref, List.of(
        new ResearchNodeSpec.ValueRequirement(someValue, 10)
    )))
    .withStage(ResearchStageSpec.valuesOnly(stage2Ref, List.of(
        new ResearchNodeSpec.ValueRequirement(someValue, 25)
    )))
    .withToast(new ResearchToastDefinition("title.key", List.of(), null, someIcon))
    .build();
```

### Item Crafted Trigger with Component Matching
```java
this.ingress().onItemCrafted(stackTemplate)
    .declareFact("demo/item_crafted_fact");
```

### Advancement Trigger
```java
this.ingress().onAdvancementEarned(advancementId)
    .declareFact("demo/advancement_fact");
```

### Stage Condition on Book Entry
```java
entry.withCondition(myNodeRef, myStageRef);
```
