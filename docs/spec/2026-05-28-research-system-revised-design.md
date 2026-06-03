<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Revised Research System Design

Date: 2026-05-28
Branch: conceptual target for a fresh implementation from `version/26.1.2`

## Summary

This spec revises the research-system design around a stricter layered model and an explicitly phased rollout.

Research is the only authoritative durable progression system. Books are the presentation layer. Book visibility is computed from conditions. Book visual state remains separate from progression. Explicit interaction hooks bridge authored events into research fact/value mutations.

The design intentionally keeps phase 1 narrow, explicit, and manually verifiable. Authoring sugar, book-first generation helpers, datagen bridge/compiler layers, and datapack patch/merge semantics are preserved as later roadmap phases rather than foundation scope.

## Goals

- Make research the authoritative persistent progression system.
- Keep books as visualization and navigation only.
- Remove separate persisted book unlock state from the architecture.
- Keep visibility computed rather than persisted.
- Keep unread/new visuals separate from progression.
- Keep research predicates fully internal to research state.
- Support explicit event-to-progression wiring through interaction hooks.
- Deliver the system in small, careful, manually verifiable phases.
- Preserve room for later authoring sugar without making it part of phase 1.

## Non-Goals

- Do not add a separate research graph UI.
- Do not preserve legacy book-local progression semantics.
- Do not introduce book-defined research helpers in phase 1.
- Do not implement datapack patch/merge semantics in phase 1.
- Do not add automated test requirements to this spec.

## Design Principles

1. Research owns durable progression.
2. Books own presentation and visual state only.
3. Visibility is computed, not persisted.
4. External events must resolve into research facts or values before predicates observe them.
5. Phase 1 should optimize for clarity and correctness, not ergonomics.
6. Later convenience features must compile to the same explicit canonical model introduced in phase 1.

## Core Architecture

Runtime is split into three layers.

### 1. Research Layer

Research is the only authoritative durable progression layer.

It owns:

- unlocked research nodes
- research facts
- research values

Research nodes are first-class authored milestones. Facts and values are primitive progression inputs.

### 2. Book Layer

Books are the presentation and navigation layer.

They own only visual and UI state such as:

- unread/new markers
- navigation and local interaction state

Books do not own durable progression.

### 3. Interaction-Hook Layer

Interaction hooks are a separate runtime dataset.

They map explicit events to research primitive mutations.

Examples:

- `entry_viewed_once -> grant fact`
- `entry_viewed_once -> increment value`

Hooks never unlock nodes directly. They mutate facts or values, then research reevaluates node unlocks from predicates.

## State Model

### Durable Research State

Persist only:

- unlocked node ids
- fact ids
- values

Viewed-once exists only as a research-side concept when progression needs it. In that case it is represented as a fact or value mutation target, not as general book state.

### Book Visual State

Book state is limited to visual/UI concerns such as unread/new markers and navigation state.

This state is not authoritative progression.

### Computed State

Do not persist:

- book visibility
- node `available` state

Both are computed from current research state and book conditions.

### Removed Concept: Book Unlock State

The revised design does not include a separate persisted book unlock state.

Any responsibility that used to fall into that bucket must instead belong to one of:

- research state
- computed visibility
- book visual/UI state

## Research Model

### Research Graphs

Research graphs remain minimal grouping and validation resources.

They exist to:

- group related research content
- provide a validation boundary
- support organization and future tooling

In phase 1, graphs do not define visualization, layout, or stronger runtime semantics.

### Research Nodes

Research nodes are first-class durable progression milestones.

Each node has:

- a global id
- a graph id
- a dependency predicate
- an unlock predicate
- optional presentation metadata for future use

Nodes remain distinct from facts and values.

### Node States

A node has three logical states:

- `locked`
- `available`
- `unlocked`

Only `unlocked` is persisted.

`available` is always computed.

### Facts and Values

Facts and values are the primitive durable progression inputs.

They are the only primitive mutation targets for hooks and external APIs.

## Predicate and Dependency Rules

### Research Predicates

Research predicates may depend only on:

- research nodes
- research facts
- research values

Research predicates may not depend on:

- book visibility
- unread/new state
- navigation/UI state
- generic book interaction history
- raw external gameplay inputs

If an external event matters for progression, it must first be converted into a fact or value mutation.

### Dependency vs Unlock Predicates

Each node has two authored predicates.

#### Dependency Predicate

- references research nodes only
- expresses graph ordering and reachability

#### Unlock Predicate

- references research facts and values
- expresses how the node is earned once dependencies are satisfied

Unlock semantics are:

`dependency predicate && unlock predicate`

### Book Conditions

Books keep a single `condition` field for visibility/display.

Book conditions may query:

- research nodes
- research facts
- research values
- book-local display predicates

Books should normally gate progression-facing visibility on research nodes.

Facts and values are allowed for special cases, but they are lower-level inputs rather than the normal visibility API.

## Book and Research Boundary

The core invariant is:

- research is upstream
- books are downstream

Books may depend on research through conditions.
Research may not depend on book state.

Runtime book objects do not carry a second progression-authority field such as a runtime `research_node` or unlock reference.

Optional book↔research associations are acceptable only as tooling or datagen concepts in later phases. They are excluded from runtime semantics.

## Interaction Hooks

Interaction hooks provide the only supported path from authored events into research primitive mutations.

An interaction hook definition must answer:

- what event happened
- which fact or value is mutated
- the explicit trigger target id
- when the event fires

Interaction hooks are not:

- a generic query system over book interactions
- an implicit runtime convention derived from entry ids
- a hidden transport layer inside runtime book objects

### Phase 1 Trigger Set

Phase 1 includes only:

- `entry_viewed_once`

This trigger may:

- grant facts
- mutate values

It may not unlock nodes directly.

### Planned Later Trigger Phases

These are part of the roadmap, but not phase 1.

#### Phase 2

- `item_crafted`
- `item_acquired`

#### Phase 3+

- `dimension_visited` or similar world-progress triggers
- `custom_api_trigger`

## Viewed-Once Semantics

Viewed-once is not a general book-state concept.

It exists only when explicitly authored as progression input.

If progression needs viewed-once semantics, an interaction hook mutates a research fact or value.

If something is only needed for visual presentation, it remains book visual state and does not become durable progression.

## Reset Semantics

There are two separate reset domains.

### Research Reset

Research reset clears only research-owned durable state:

- unlocked nodes
- facts
- values
- viewed-once progression state when represented as research facts or values

### Book Visual Reset

Book visual reset is a separate optional admin/debug concern.

It clears only book visual/UI state.

It does not alter research progression.

### Visibility Reset

Visibility is never reset directly. It is recomputed from current research state and book conditions.

## Authoring Model

### Phase 1 Canonical Model

Phase 1 uses fully explicit, author-chosen ids and whole-resource definitions.

Authors explicitly define:

- research graphs
- research nodes
- research facts
- research values
- interaction hooks
- book conditions that query research ids

This explicit model is the canonical target language for the system.

Later sugar must compile to it.

### Phase 1 Ergonomics Rule

Phase 1 should include minimal sugar only.

It should not include book-first auto-generation, inferred ids, or helper systems that silently create research data from book structure.

### Later-Phase Authoring Rule

Later authoring convenience should start on the research side first.

The preferred order is:

1. research-side datagen glue and authoring sugar
2. book-side glue only after the research-side sugar is proven

Typed refs should be part of that later authoring-convenience work.

They are an authoring-time/datagen-time safety feature only and must compile down to the same explicit canonical ids/resources.

They must not become runtime progression authority or hidden runtime lookup semantics.

## Implementation Status

Status as of 2026-06-03 on `feat/research-system/main`.

Completed so far:

- Phase 1 foundation for facts, nodes, hooks, persistence, and validation
- trigger type `entry_viewed_once`
- trigger type registry (extensible trigger type infrastructure)
- trigger type `item_crafted` (manual crafting only, output item ID)
- trigger type `item_acquired` (inventory change, item ID)
- data component matching for item triggers (partial matching via ItemStackTemplate)
- book condition type `research_node_unlocked`
- demo entry slice for:
  - `modonomicon:features/condition_root`
  - `modonomicon:features/condition_level_1`
  - `modonomicon:features/condition_level_2`
- full demo-book `entry_read` migration for:
  - formatting progression
  - features progression chain
  - composite `two_parents`
  - conditional category migration
- nested admin reset command: `modonomicon research reset`
- condition-surface cutover for legacy progression conditions:
  - `entry_read` removed
  - `entry_unlocked` removed
  - `advancement` removed
- explicit advancement-backed research ingress
- computed visibility/access cutover for categories, entries, and pages
- removal of persisted book unlock state as progression authority
- legacy advancement networking/config cleanup
- research progress button replacing the old read-all progression shortcut
- research values (numeric counters) with hook increment and node threshold support
- values demo scenario (collector: 3 incrementing entries + 1 threshold-gated entry)
- item crafted demo scenario (crafting stick → unlock entry)
- item acquired demo scenario (acquire cobblestone → unlock entry)
- advancement reconciliation: replay completed advancement hooks after reset and on player login
- typed refs: `ResearchFactRef`, `ResearchNodeRef`, `ResearchValueRef`
- research-side datagen builder API (`ResearchDataBuilder`, `ResearchIngressHelper`)
- `ingress()` fluent helper with `onEntryViewedOnce`, `onAdvancementEarned`, `onItemCrafted`, `onItemAcquired`, `declareFact`, `grantFact`
- `ResearchProvider` / `ResearchSubProvider` / `SingleResearchSubProvider` datagen API
- platform datagen wrappers and registrations
- elimination of `ModonomiconDataGenSetup` orchestrator; direct cache passing
- book-side research glue: `withCondition(ResearchNodeRef)` on entries, categories, and pages
- `ConditionHelper` with `researchNodeUnlocked` typed-ref methods
- `BookHierarchyResearchCompiler` for per-book opt-in generated entry-hierarchy progression
- expanded research commands: `reset book`, `reset graph`, `grant fact`, `revoke fact`, `unlock node`, `lock node`, `set value`
- graph index built from resource folder structure for graph-scoped operations
- research suggestion providers for all argument types
- moderator permission level on all research commands

Not completed yet:

- datapack patch/merge semantics
- research-side optional-dependency predicate for optional dependency mods
- sanctioned research skip/bypass mechanism for narrow scenarios

## Roadmap

### Phase 1: Foundation

Phase 1 proves the architectural foundation only.

Included:

- research as authoritative progression
- books as presentation only
- no separate book unlock state
- computed visibility
- research nodes, facts, and values
- persist only unlocked node state
- explicit interaction-hook dataset
- trigger type `entry_viewed_once`
- strict validation for phase-1 resources only

Current status:

- completed for the initial demo-entry slice
- completed for the full demo-book `entry_read` migration slice
- completed for the legacy progression-condition cutover and advancement-backed replacement path
- completed for the runtime visibility/access cutover; book visibility is now computed rather than progression-persisted
- completed for the immediate legacy-advancement cleanup and post-cutover research-progress button follow-up
- completed for research values with collector demo scenario
- completed for research-side datagen glue, typed refs, and ingress helper
- completed for `ResearchProvider` / subprovider datagen API
- completed for book-side research glue and `BookHierarchyResearchCompiler`
- completed for elimination of `ModonomiconDataGenSetup` orchestrator

Not part of phase 1:

- datapack patch/merge semantics

Completed post-phase-1 slices (completed out of original order):

- research-side datagen glue and authoring sugar with typed refs
- `ResearchProvider` / subprovider datagen API restructuring
- book-side research glue and generated entry-hierarchy progression compiler
- research values runtime + demo scenario
- trigger type registry infrastructure
- `ModonomiconDataGenSetup` elimination
- expanded research commands with book/graph/fact/node/value operations

### Phase 2: Explicit Trigger Expansion

Phase 2 broadens the explicit model without introducing major sugar.

Included:

- trigger type `item_crafted`
- trigger type `item_acquired`
- expanded explicit validation and diagnostics for hooks/resources
- any admin/debug improvements that naturally extend the explicit runtime model

Current status:

- completed for `item_crafted` trigger type (manual crafting only, output item ID)
- completed for `item_acquired` trigger type (inventory change, item ID)
- completed for platform wiring (NeoForge events, Forge events, Fabric mixins)
- completed for datagen hook specs, ingress helpers, and builder methods
- completed for demo scenarios (crafting stick, acquiring cobblestone)
- completed for data component matching (partial matching via ItemStackTemplate, matchComponents flag)

Not part of phase 2 unless strictly needed:

- bridge/compiler authoring layer
- book-first generated progression
- merge/patch semantics

### Phase 3+: Authoring Convenience and Broader Extensibility

Only after the explicit runtime model is proven should later convenience arrive.

Completed:

- typed references (`ResearchFactRef`, `ResearchNodeRef`, `ResearchValueRef`) for authoring/datagen safety
- datagen bridge/compiler layer (`BookHierarchyResearchCompiler`)
- optional book-first generated progression (per-book opt-in entry-hierarchy progression)
- `ResearchProvider` / subprovider datagen API

Still planned:

- convenience helpers around viewed-once and later triggers
- broader trigger families beyond Phase 2 (e.g. `dimension_visited`, `custom_api_trigger`)
- datapack patch/merge semantics

All later convenience must compile to the same canonical explicit model introduced in phase 1.

## Additional Future Work Note

A future slice may add a research-side optional-dependency predicate or function for research content that belongs to an optional dependency mod.

That is not part of the current condition cutover and does not change the rule that `mod_loaded` remains a book-local/environment condition.

A future slice may also need a sanctioned way to skip or bypass research in narrowly-defined scenarios where mandatory research progression is not desirable.

That work must be designed explicitly as a research-owned mechanism and must not revive legacy advancement-locking behavior or reintroduce book-local progression authority.

## Validation

Validation grows by phase.

### Phase 1 Validation

Validate only the explicit phase-1 model:

- graph ids resolve
- node/fact/value references resolve
- research dependency references resolve
- dependency cycles are rejected
- interaction hooks reference valid events and valid research primitive targets
- hooks target only facts/values, never nodes directly
- book conditions referencing research ids resolve cleanly

Errors should identify:

- the resource being loaded
- the failing reference or invalid field
- the violated rule

### Later-Phase Validation

Add later, with the feature that needs it:

- trigger-family-specific validation
- bridge/compiler validation
- generated-resource conflict validation
- datapack patch/merge validation

## Manual Verification

Implementation should proceed in small, careful, manually verifiable phases.

### Phase 1 Manual Verification

Manual verification should confirm:

- facts and values mutate correctly
- research reevaluates correctly
- nodes unlock from predicates only
- `entry_viewed_once` hooks mutate only configured facts/values
- book visibility follows research state
- visual state remains separate from progression
- research reset and book visual reset remain separate

### Phase 2 Manual Verification

Manual verification should confirm:

- each new trigger type works independently
- shared hook machinery still respects the fact/value-only mutation boundary

### Later Phases

Manual verification should confirm:

- authoring sugar compiles to the same canonical explicit model
- convenience features do not introduce a second runtime authority

## Failure-Handling Posture

When authored data is invalid or incomplete:

- fail fast during validation/load where possible
- do not silently infer progression semantics
- do not invent ids at runtime
- do not fall back to hidden book-local progression behavior

The guiding rule is:

> explicit authored progression either loads correctly or errors clearly

## Scope Discipline

If a convenience feature weakens the architecture boundary, defer it.

If a generic abstraction is not needed for the current phase, defer it.

Phase 1 should optimize for correctness and clarity, not ergonomics.

## Result

This revised design keeps the original goal of research as authoritative progression, but tightens the architecture around a stricter state model, explicit interaction hooks, and a phased delivery plan.

The intended implementation path is:

1. prove the explicit runtime model
2. expand the explicit trigger model
3. add authoring convenience only after the foundation is proven

## Advancement Event-Only Caveat

An advancement-backed research ingress path may use pure server-side advancement-earned events without an immediate replay or reconciliation pass for already-completed advancements.

That keeps the runtime architecture cleaner, but it means some operations such as research reset may not automatically reconstruct advancement-backed research state from the player's current advancement progress.

If that gap proves user-visible, a later slice should add a narrow research-owned reconciliation path for already-completed advancements on the relevant lifecycle boundaries such as reset or login.
