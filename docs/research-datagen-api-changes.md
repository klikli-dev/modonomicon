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
| `(..., ResearchCache, LanguageProviderCache)` | Also with language cache for translation injection |

### Generated Files Per Bundle
- `facts.json` - All fact definitions
- `values.json` - All value definitions
- `nodes.json` - All node definitions
- `hooks.json` - Merged hook definitions (entry_viewed_once + item_crafted + item_acquired)
- `advancement_hooks.json` - Advancement hook definitions

---

## `BookProvider`

**Path:** `com.klikli_dev.modonomicon.api.datagen.BookProvider`

Top-level datagen orchestrator for book content. Collects bundles from `BookSubProvider`s, writes book/category/entry/page JSON files, and compiles entry hierarchy research.

### Constructors
| Constructor | Description |
|-------------|-------------|
| `BookProvider(PackOutput, CompletableFuture<HolderLookup.Provider>, String modId, List<BookSubProvider>)` | Basic constructor — research is written directly to disk |
| `(..., LanguageProviderCache langCache, ResearchCache researchCache)` | Full constructor — book-generated research is forwarded to the `ResearchCache` for merging with authored research |

### Research Orchestration
During `run()`, the provider:
1. Calls each `BookSubProvider` to collect `BookModel`s.
2. For each book, runs `BookHierarchyResearchCompiler.compile(book)` to produce a `CompiledBookResearch` (if `generateEntryHierarchyResearch` is true).
3. If a `ResearchCache` was provided, forwards the compiled research to `researchCache.accept(bundleId, research)`. Otherwise writes it directly to disk via `writeResearchBundle()`.
4. Writes book, category, entry, page, and command JSON files.

### Generated Files Per Book
- Book JSON, category JSONs, entry JSONs, page JSONs, command JSONs
- Research bundle (if `generateEntryHierarchyResearch` is true and no `ResearchCache`): `facts.json`, `hooks.json`, `nodes.json`

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

## `SingleResearchSubProvider`

**Path:** `com.klikli_dev.modonomicon.api.datagen.research.SingleResearchSubProvider`

Abstract base class for authoring one research bundle. Mirrors `SingleBookSubProvider` on the book side. Extend this instead of implementing `ResearchSubProvider` directly when you author a single research bundle.

### Constructor
| Parameter | Description |
|-----------|-------------|
| `String researchId` | The path segment for this research bundle (e.g. `"demo"`) |
| `String modId` | The mod ID namespace |

### Key Methods
| Method | Description |
|--------|-------------|
| `researchId()` | Returns the research ID passed to the constructor |
| `generateResearch()` | **Abstract** — subclasses implement this to author facts, nodes, and ingress mappings |
| Inherited from `ResearchProviderBase` | `fact()`, `value()`, `node()`, `nodeBuilder()`, `ingress()`, `lang()`, `add()`, `modLoc()`, `mcLoc()`, `stageRef()`, `researchNodeName()`, `researchStageName()` |

The `generate()` method (from `ResearchSubProvider`) creates a `ResearchDataBuilder`, calls `this.research(research)` to inject it, then calls `this.generateResearch()`, and submits the resulting `ResearchBundle` under the key `modLoc(researchId)`.

---

## Book Condition Changes

### Removed Condition Types

The following condition types and their datagen models have been **deleted entirely**. They will throw at deserialization time if encountered in saved data.

#### `BookEntryReadCondition` (DELETED)
- **Was:** `com.klikli_dev.modonomicon.book.conditions.BookEntryReadCondition`
- **Condition type ID:** `modonomicon:entry_read`
- **Was used for:** Checking if a specific book entry had been read by the player.
- **Runtime behavior was:** `BookUnlockStateManager.get().isReadFor(player, entry)`
- **Replacement:** Use `BookResearchNodeUnlockedCondition` backed by a research node that is triggered via an `entry_viewed_once` hook. If you were using `autoAddReadConditions`, see the `BookHierarchyResearchCompiler` section below.

#### `BookEntryReadConditionModel` (DELETED)
- **Was:** `com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel`
- **Methods lost:** `withEntry(Identifier)`, `withEntry(String)`

#### `BookEntryUnlockedCondition` (DELETED)
- **Was:** `com.klikli_dev.modonomicon.book.conditions.BookEntryUnlockedCondition`
- **Condition type ID:** `modonomicon:entry_unlocked`
- **Was used for:** Checking if a specific book entry was unlocked (visible, regardless of read status).
- **Runtime behavior was:** `BookUnlockStateManager.get().isUnlockedFor(player, entry)`
- **Replacement:** Use `BookResearchNodeUnlockedCondition` backed by the research node that governs that entry's unlock milestone.

#### `BookEntryUnlockedConditionModel` (DELETED)
- **Was:** `com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryUnlockedConditionModel`
- **Methods lost:** `withEntry(Identifier)`, `withEntry(String)`

#### `BookAdvancementCondition` (DELETED)
- **Was:** `com.klikli_dev.modonomicon.book.conditions.BookAdvancementCondition`
- **Condition type ID:** `modonomicon:advancement`
- **Was used for:** Checking if a vanilla advancement was completed.
- **Runtime behavior was:** `serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()`
- **Replacement:** Use `AdvancementHookSpec` in your research datagen to grant a fact when the advancement is earned, then gate the entry on a `BookResearchNodeUnlockedCondition` backed by a node requiring that fact.

#### `BookAdvancementConditionModel` (DELETED)
- **Was:** `com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel`
- **Methods lost:** `withAdvancementId(Identifier)`, `withAdvancementId(String)`, `withAdvancement(AdvancementHolder)`

### `BookCondition.fromJson` — Hard Rejection of Removed Types

**Path:** `com.klikli_dev.modonomicon.book.conditions.BookCondition`

`fromJson` now throws an `IllegalArgumentException` with a descriptive message if it encounters any of the removed condition type IDs:
- `modonomicon:entry_read` → _"Book condition type 'modonomicon:entry_read' is no longer supported. Model this progression through explicit research hooks and research nodes instead."_
- `modonomicon:entry_unlocked` → _"Book condition type 'modonomicon:entry_unlocked' is no longer supported. Replace it with the authoritative research node for the referenced progression milestone."_
- `modonomicon:advancement` → _"Book condition type 'modonomicon:advancement' is no longer supported. Model this progression through explicit research hooks and research nodes instead."_

### New Condition Types

#### `BookResearchNodeUnlockedCondition` (runtime, NEW)
**Path:** `com.klikli_dev.modonomicon.book.conditions.BookResearchNodeUnlockedCondition`
- Condition type ID: `modonomicon:research_node_unlocked`
- Handles multi-stage nodes: checks if `stageIndex >= totalStages` for full completion.
- Auto-generates tooltip using `Util.makeDescriptionId("research_node", nodeId)` if none provided.

#### `BookResearchNodeUnlockedConditionModel` (datagen, NEW)
**Path:** `com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel`
- Factory: `BookResearchNodeUnlockedConditionModel.create()`
- `withNode(Identifier nodeId)` — set the node to check.
- `withNode(String nodeId)` — parse-and-set overload.
- `withNodeName()` — auto-sets tooltip from the node's description ID translation key.

#### `BookResearchStageCompletedCondition` (runtime, NEW)
**Path:** `com.klikli_dev.modonomicon.book.conditions.BookResearchStageCompletedCondition`
- Condition type ID: `modonomicon:research_stage_completed`
- Requires both `node_id` and `stage_id`.
- Auto-generates tooltip using description IDs for both node and stage.

#### `BookResearchStageCompletedConditionModel` (datagen, NEW)
**Path:** `com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchStageCompletedConditionModel`
- Factory: `BookResearchStageCompletedConditionModel.create()`
- `withNode(Identifier)`, `withStage(Identifier)`
- `withStageName()` — auto-sets tooltip from the stage's description ID translation key.

### `ConditionHelper`

**Path:** `com.klikli_dev.modonomicon.api.datagen.ConditionHelper`

#### Removed Methods
| Old Method | Replacement |
|------------|-------------|
| `advancement(Identifier)` | Use `AdvancementHookSpec` in research datagen, then gate with `researchNodeUnlocked(...)` |
| `advancementBuilder(Identifier)` | Same as above |
| `entryRead(Identifier)` | `researchNodeUnlocked(Identifier)` or `BookEntryModel.withCondition(ResearchNodeRef)` |
| `entryReadBuilder(Identifier)` | Same as above |
| `entryRead(BookEntryModel)` | `researchNodeUnlocked(...)` or `BookEntryModel.withCondition(ResearchNodeRef)` |
| `entryReadBuilder(BookEntryModel)` | Same as above |
| `andBuilder(...)` | `and(...)` (builder variant removed; `and(...)` still exists) |
| `orBuilder(...)` | `or(...)` (builder variant removed; `or(...)` still exists) |

#### Added Methods
| New Method | Description |
|------------|-------------|
| `researchNodeUnlocked(Identifier nodeId)` | Condition: research node is fully completed |
| `researchNodeUnlocked(ResearchNodeRef nodeRef)` | Same, typed ref variant |
| `researchStageCompleted(Identifier nodeId, Identifier stageId)` | Condition: specific stage of a node is completed |
| `researchStageCompleted(ResearchNodeRef, ResearchStageRef)` | Same, typed ref variant |

### `BookCategoryModel`

**Path:** `com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel`

#### Added Methods
| New Method | Description |
|------------|-------------|
| `withCondition(ResearchNodeRef nodeRef)` | Shorthand for `BookResearchNodeUnlockedConditionModel` — gates the category behind a research node |

### `BookPageModel`

**Path:** `com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel`

#### Added Methods
| New Method | Description |
|------------|-------------|
| `withCondition(ResearchNodeRef nodeRef)` | Shorthand for `BookResearchNodeUnlockedConditionModel` — gates the page behind a research node |

### `BookEntryModel`

**Path:** `com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel`

#### Removed Behavior: `autoAddReadConditions`
The old `effectiveCondition()` method would, if no explicit condition was set and `autoAddReadConditions` was true, automatically wrap parent entries in `BookEntryReadCondition` instances:
```java
// OLD behavior (now removed):
// effectiveCondition() returned:
//   BookEntryReadConditionModel.create().withEntry(parents.get(0).getEntryId())
// or:
//   BookAndConditionModel with all parents as BookEntryReadCondition
```

This logic has been removed. `effectiveCondition()` now returns `BookNoneConditionModel.create()` when no explicit condition is set. Parent-to-child progression is instead modeled through the research system (`BookHierarchyResearchCompiler`).

#### Added Methods
| New Method | Description |
|------------|-------------|
| `withCondition(ResearchNodeRef nodeRef)` | Shorthand for `BookResearchNodeUnlockedConditionModel` |
| `withCondition(ResearchNodeRef, ResearchStageRef)` | Shorthand for `BookResearchStageCompletedConditionModel` |
| `hasCondition()` | Returns `true` if an explicit condition has been set |

### `BookModel`

**Path:** `com.klikli_dev.modonomicon.api.datagen.book.BookModel`

#### Removed
- Field: `autoAddReadConditions` (boolean)
- Method: `autoAddReadConditions()`
- Method: `withAutoAddReadConditions(boolean)`

#### Added
- Field: `generateEntryHierarchyResearch` (boolean)
- Method: `generateEntryHierarchyResearch()`
- Method: `withGenerateEntryHierarchyResearch(boolean)` — When true, the book provider will generate canonical research for eligible entry parent links via `BookHierarchyResearchCompiler`.

### `BookUnlockStateManager` (DELETED)

**Was:** `com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager`

The entire class has been removed. The old entry-level unlock/read state tracking system is replaced by the research system's `ResearchStateManager`. Key methods that no longer exist:
- `isReadFor(Player, BookEntry)`
- `isUnlockedFor(Player, BookEntry)`
- `isUnlockedFor(Player, BookCategory)`
- `getUnlockCodeFor(Player, Book)` / `applyUnlockCodeFor(ServerPlayer, String)`
- `readFor(ServerPlayer, BookEntry)`
- `resetFor(ServerPlayer, Book)`
- `onAdvancement(ServerPlayer)`

All of these are now handled through research facts, values, hooks, and node state.

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

## Platform Registration (NeoForge / Fabric)

### `NeoBookProvider`

**Path:** `com.klikli_dev.modonomicon.api.datagen.NeoBookProvider`

NeoForge-specific factory for `BookProvider`. The `of()` method now requires `LanguageProviderCache` and `ResearchCache` parameters:

```java
public static BookProvider of(GatherDataEvent event, LanguageProviderCache langCache,
        ResearchCache researchCache, BookSubProvider... subProviders)
```

### `NeoResearchProvider`

**Path:** `com.klikli_dev.modonomicon.api.datagen.NeoResearchProvider`

NeoForge-specific factory for `ResearchProvider`. **New class** in this branch.

```java
public static ResearchProvider of(GatherDataEvent event, LanguageProviderCache langCache,
        ResearchCache researchCache, ResearchSubProvider... subProviders)
```

### `FabricBookProvider`

**Path:** `com.klikli_dev.modonomicon.fabric.api.datagen.FabricBookProvider`

Fabric equivalent of `NeoBookProvider`. Same signature change — requires `LanguageProviderCache` and `ResearchCache`.

### `FabricResearchProvider`

**Path:** `com.klikli_dev.modonomicon.fabric.api.datagen.FabricResearchProvider`

**New class** — Fabric equivalent of `NeoResearchProvider`.

### `ForgeBookProvider`

**Path:** `com.klikli_dev.modonomicon.forge.api.datagen.ForgeBookProvider`

Forge equivalent — same signature change.

### `ForgeResearchProvider`

**Path:** `com.klikli_dev.modonomicon.forge.api.datagen.ForgeResearchProvider`

**New class** — Forge equivalent.

### DataGenerators Wiring

The `DataGenerators.gatherData` method on each platform now:
1. Creates a shared `LanguageProviderCache` and `ResearchCache`.
2. Passes both caches to `NeoBookProvider.of(...)` (or platform equivalent).
3. Registers a `NeoResearchProvider.of(...)` (or platform equivalent) with the same caches.
4. Registers the language provider (e.g. `EnUsProvider`) **after** the book provider so it can read texts added by the book provider from the cache.

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

---

## Config Changes

### Removed: `ServerConfig.disableAdvancementLocking`

The server config option `unlock.disableAdvancementLocking` has been **deleted**. The old option made advancement-based conditions always return true. Since the research system replaces advancement conditions entirely, this bypass is no longer relevant.

### Added: `ClientConfig.showResearchToasts`

**Path:** `com.klikli_dev.modonomicon.config.ClientConfig` → `qolCategory.showResearchToasts`

New client config option (default: `true`). Controls whether toast notifications are shown when research facts are granted, values are incremented, or nodes/stages are unlocked.

---

## Generated JSON File Structure

Research datagen produces the following JSON files per bundle under `data/<namespace>/modonomicon/research/<bundle_path>/`:

### `facts.json`
Array of fact definitions:
```json
[
  { "id": "mymod/my_fact" },
  { "id": "mymod/another_fact" }
]
```

### `values.json`
Array of value definitions:
```json
[
  { "id": "mymod/my_counter" }
]
```

### `nodes.json`
Array of node definitions:
```json
[
  {
    "id": "mymod/my_node",
    "required_facts": ["mymod/my_fact"],
    "required_values": [
      { "value_id": "mymod/my_counter", "threshold": 5 }
    ],
    "stages": [
      {
        "id": "mymod/my_node_stage_1",
        "required_values": [
          { "value_id": "mymod/my_counter", "threshold": 1 }
        ]
      }
    ],
    "required_stages": [
      { "node_id": "mymod/other_node", "stage_id": "mymod/other_node_stage_1" }
    ],
    "toast": {
      "title": "research_toast.mymod.node_unlocked",
      "description": "research_toast.modonomicon.research.node",
      "icon": { "id": "minecraft:diamond" }
    }
  }
]
```

### `hooks.json`
Merged array of all non-advancement hooks (entry_viewed_once + item_crafted + item_acquired):
```json
[
  {
    "id": "mymod/my_hook",
    "trigger_type": "modonomicon:entry_viewed_once",
    "event_target_id": "mymod:some_entry",
    "fact_id": "mymod/my_fact"
  },
  {
    "id": "mymod/item_hook",
    "trigger_type": "modonomicon:item_crafted",
    "event_target_id": "minecraft:stick",
    "event_target_item": { "id": "minecraft:stick" },
    "value_id": "mymod/my_counter"
  }
]
```

### `advancement_hooks.json`
Array of advancement-triggered hooks:
```json
[
  {
    "id": "mymod/advancement_hook",
    "advancement_id": "minecraft:story/mine_stone",
    "fact_id": "mymod/my_fact"
  }
]
```

### Book-Generated Research

When `generateEntryHierarchyResearch` is true on a `BookModel` and a `ResearchCache` is provided, the `BookProvider` writes generated research under `data/<namespace>/modonomicon/research/generated/<book_path>/` with auto-generated entry_viewed_once hooks and nodes for parent→child entry progression. If no `ResearchCache` is provided, these files are written directly to disk by the `BookProvider`.

---

## Migration Guide

### Users on `autoAddReadConditions` (most common case)

If you had `withAutoAddReadConditions(true)` on your `BookModel`, the old system auto-wrapped every parent link in a `BookEntryReadCondition`. The migration path:

**Before (old):**
```java
BookModel.create(bookId, name)
    .withAutoAddReadConditions(true);
```

**After (new):**
```java
BookModel.create(bookId, name)
    .withGenerateEntryHierarchyResearch(true);
```

The `BookHierarchyResearchCompiler` will automatically:
1. Generate `entry_viewed_once` hooks for each entry with parents.
2. Generate research nodes gated by those hooks.
3. Produce the correct `BookResearchNodeUnlockedCondition` on each entry.

No manual condition work is needed for simple parent→child progression.

### Users with explicit `entryRead` conditions on entries

If you were manually setting `entryRead` conditions via `ConditionHelper` or `BookEntryReadConditionModel`:

**Before (old):**
```java
entry.withCondition(
    this.conditionHelper().entryRead(parentEntry.getId())
);
// or:
entry.withCondition(
    BookEntryReadConditionModel.create().withEntry(parentEntry.getId())
);
```

**After (new) — Option A: automatic hierarchy**
If the condition just gates on "parent was read", use `withGenerateEntryHierarchyResearch(true)` on the book and remove the explicit condition entirely. The hierarchy compiler handles it.

**After (new) — Option B: explicit research node**
If you need fine-grained control, create a research node + hook manually:
```java
// In your ResearchSubProvider:
var fact = researchData.fact("mymod/my_gate");
researchData.grantFactOnEntryViewedOnce("mymod/my_hook", parentEntryId, fact);
var node = researchData.node("mymod/my_gate_node", fact);

// In your entry datagen:
entry.withCondition(node);
```

### Users with `entryUnlocked` conditions

**Before (old):**
```java
entry.withCondition(
    BookEntryUnlockedConditionModel.create().withEntry(someEntry.getId())
);
```

**After (new):**
Identify the research node that represents the unlock milestone for that entry, then:
```java
entry.withCondition(researchNodeRef);
// or for a specific stage:
entry.withCondition(researchNodeRef, researchStageRef);
```

### Users with `advancement` conditions

**Before (old):**
```java
entry.withCondition(
    this.conditionHelper().advancement(advancementId)
);
// or:
entry.withCondition(
    BookAdvancementConditionModel.create().withAdvancementId(advancementId)
);
```

**After (new):**
Model the advancement gate through the research system:
```java
// In your ResearchSubProvider:
var fact = researchData.fact("mymod/advancement_gate");
researchData.grantFactOnAdvancementEarned("mymod/advancement_hook", advancementId, fact);
var node = researchData.node("mymod/advancement_gate_node", fact);

// In your entry datagen:
entry.withCondition(node);
```

### Users with complex `and`/`or` condition combinations

The `and(...)` and `or(...)` combinators on `ConditionHelper` still exist. You can combine research-based conditions the same way:

```java
entry.withCondition(
    this.conditionHelper().and(
        this.conditionHelper().researchNodeUnlocked(nodeA),
        this.conditionHelper().researchStageCompleted(nodeB, stageB)
    )
);
```

Note: `andBuilder(...)` and `orBuilder(...)` have been removed. Use `and(...)` / `or(...)` directly.

### `BookUnlockStateManager` API consumers

If your mod directly called `BookUnlockStateManager.get()` methods (e.g. `isReadFor`, `isUnlockedFor`, `resetFor`), these no longer exist. Use `ResearchStateManager` instead:

| Old API | New API |
|---------|---------|
| `BookUnlockStateManager.get().isReadFor(player, entry)` | Check if the entry's corresponding research node is completed via `ResearchStateManager` |
| `BookUnlockStateManager.get().isUnlockedFor(player, entry)` | Check if the entry's research node is unlocked |
| `BookUnlockStateManager.get().resetFor(player, book)` | `ResetBookResearchCommand` or `ResearchStateManager` reset |
| `BookUnlockStateManager.get().onAdvancement(player)` | Research hooks fire automatically via `AdvancementResearchHookService` |

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
