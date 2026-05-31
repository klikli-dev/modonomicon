<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research Ingress Helper Design

Date: 2026-05-31
Branch: `feat/research-system/main`

## Summary

This slice refines the research-side authoring API so explicit research ingress reads in terms of the research ontology rather than in terms of low-level hook record construction.

The current research datagen API exposes methods such as `entryViewedOnce(...)` and `advancement(...)` directly on the research provider/builder surface.

Those methods are structurally correct, but they are not clear enough about what they actually do:

- they author research ingress mappings
- they grant research facts
- they do not directly unlock nodes
- they do not directly mutate book-side progression state

This slice introduces a dedicated ingress helper object and fluent syntax so the authored meaning becomes explicit.

## Goals

- Make research ingress authoring read in terms of research semantics.
- Keep the event source and the research-side effect visually separate.
- Allow compact authoring for the common case where an ingress event grants a newly-declared fact.
- Preserve canonical explicit output resources and runtime behavior unchanged.
- Keep the API ready for future additional trigger families.

## Non-Goals

- Do not change runtime hook semantics.
- Do not make ingress helpers unlock nodes directly.
- Do not auto-create research nodes from ingress helpers.
- Do not introduce book-side glue in this slice.
- Do not add trigger families beyond the currently supported ingress kinds.

## Problem Statement

The current authoring methods are too close to the raw output shape and not clear enough about ontology.

For example, this:

```java
this.entryViewedOnce("demo/condition_root_viewed_once", this.modLoc("features/condition_root"), conditionRootViewed);
```

does not clearly communicate that:

1. a book event is acting only as ingress
2. the actual research-side mutation is granting a fact
3. research nodes unlock later from that granted fact

Similarly, `advancement(...)` does not clearly communicate that the advancement is ingress into research rather than a first-class research milestone by itself.

## Design Principle

Research authoring should read in three layers:

1. define research primitives such as facts and nodes
2. define ingress from external/book-side events
3. map ingress to primitive research mutations

That means the API should visually separate:

- the event source
- the fact grant side effect

## Recommended Syntax

The recommended syntax is an event-first fluent helper object:

```java
var rootViewed = this.ingress()
    .onEntryViewedOnce(this.modLoc("features/condition_root"))
    .declareFact("demo/condition_root_viewed");

this.node("demo/condition_level_1", rootViewed);
```

and:

```java
var mineStoneCompleted = this.ingress()
    .onAdvancementEarned(Identifier.parse("minecraft:story/mine_stone"))
    .declareFact("demo/advancement_mine_stone_completed");
```

This makes the meaning explicit:

- `ingress()` = external event entering research
- `onEntryViewedOnce(...)` / `onAdvancementEarned(...)` = event source
- `declareFact(...)` = create a research fact and map the ingress to granting it

## Fact-Declaring Sugar Rule

The helper should support the common case where the granted fact is being introduced at the same authoring site.

In that case:

- the helper declares the fact
- the helper authors the corresponding ingress hook
- the helper returns the created `ResearchFactRef`

This is authoring sugar only.

The canonical output must still contain:

- an explicit fact definition
- an explicit hook definition

## Canonical Id Rule

The author should provide one fact stem/path such as:

- `demo/condition_root_viewed`

From that single authored stem, the helper should derive:

- fact id: `modonomicon:demo/condition_root_viewed`
- hook id: `modonomicon:demo/condition_root_viewed_hook`

The helper should return a `ResearchFactRef` for the fact id.

This keeps authoring compact while preserving two explicit canonical resources.

## Why Hook Ids Should Still Differ From Fact Ids

Even if the author writes one stem, the canonical resources should still use distinct ids for facts and hooks.

The recommended convention is:

- fact: `<stem>`
- hook: `<stem>_hook`

This is clearer for diagnostics, future tooling, and debugging than reusing the same id for two different resource kinds.

## API Shape

The provider/base layer should expose:

```java
protected ResearchIngressHelper ingress()
```

The helper should provide event-specific entry points such as:

```java
onEntryViewedOnce(Identifier entryId)
onAdvancementEarned(Identifier advancementId)
```

Those entry points should return trigger-specific step objects that support at least:

```java
declareFact(String factPath)
grantFact(String hookPath, ResearchFactRef factRef)
```

`declareFact(...)` is the compact sugar path.

`grantFact(...)` remains available when the fact already exists or when the author wants full explicit control over the hook path.

## Explicit Fallback Path

The API should preserve a more explicit path for cases where the fact is already declared elsewhere.

Example:

```java
var rootViewed = this.fact("demo/condition_root_viewed");

this.ingress()
    .onEntryViewedOnce(this.modLoc("features/condition_root"))
    .grantFact("demo/condition_root_viewed_hook", rootViewed);
```

This keeps the sugar additive rather than mandatory.

## Ontology Rule

Ingress helpers may grant facts.

Ingress helpers may not:

- unlock nodes directly
- declare nodes implicitly
- bypass the research model

The intended flow remains:

1. ingress event occurs
2. ingress grants a research fact
3. research nodes unlock from fact predicates

## Extensibility Rule

The helper object should be shaped so future trigger families can slot in without changing the conceptual API.

For example, future additions should read like:

- `onItemCrafted(...)`
- `onItemAcquired(...)`

The ingress namespace should therefore be trigger-family-oriented, not book-oriented.

## Validation and Output

This slice does not change validation semantics.

Generated output must remain canonical explicit resources and should still validate through the same runtime research validation path.

## Completion Criteria

This slice is complete when:

- research authoring exposes a dedicated `ingress()` helper
- the helper supports event-first fluent syntax for current ingress types
- `declareFact(...)` can create a fact and its corresponding hook together
- generated hook ids use a deterministic suffix such as `_hook`
- the helper returns the created `ResearchFactRef`
- a more explicit `grantFact(...)` path remains available
- canonical generated resources and runtime semantics remain unchanged
