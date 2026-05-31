<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Book Research Glue and Generated Entry-Hierarchy Progression Design

Date: 2026-05-31
Branch: `feat/research-system/main`

## Summary

This document defines the next authoring-convenience slice after the research-side authoring sugar work.

The slice adds two closely related book-side authoring features:

1. book-side glue for authoring research-backed visibility from typed research refs
2. a narrow book-to-research compiler that can auto-generate canonical research for entry parent→child relationships

The generated progression model is intentionally narrow.

When enabled for a book, a parent entry being viewed once becomes explicit research ingress that can unlock its child entries through ordinary generated research facts, hooks, and nodes.

This slice is still an authoring/datagen slice only.
It does not change runtime semantics, progression authority, persistence rules, or the research/book architecture boundary.

## Goals

- Improve book-side ergonomics for research-backed visibility authoring.
- Let book datagen consume typed `ResearchNodeRef` values directly.
- Allow a modder to opt a book into auto-generated entry-hierarchy progression with one book/provider-level setting.
- Emit normal canonical research resources for the generated progression.
- Keep generated progression limited to entry parent→child relationships proven through `entry_viewed_once` ingress.
- Preserve explicit authoring as the higher-precedence path.
- Keep runtime behavior inspectable and debuggable through ordinary emitted resources.

## Non-Goals

- Do not add category or page auto-generation in this slice.
- Do not make books a runtime progression authority.
- Do not add new runtime trigger families.
- Do not infer research from arbitrary book structure beyond entry parent→child links.
- Do not introduce hidden runtime mappings or non-canonical generated state.
- Do not add merge/patch semantics for generated research in this slice.
- Do not remove or replace the explicit research-side authoring APIs.

## Architectural Constraints

This slice must preserve the revised research-system architecture:

- research remains the only authoritative durable progression layer
- books remain downstream consumers of research
- all convenience compiles to the same explicit canonical model already used at runtime
- typed refs remain authoring-time safety only
- generated book-derived progression must become ordinary research resources before runtime sees it

The core rule remains:

> books may author or generate research at datagen time, but books may not become progression authority at runtime

## Slice Boundary

This slice covers exactly these changes:

1. add typed-ref-based helpers for research-backed book conditions
2. add a per-book/provider opt-in for generated entry-hierarchy progression
3. derive canonical research facts, hooks, and nodes from entry parent→child relationships
4. apply generated research-node-backed visibility conditions to child entries that participate in the generated model

This slice does not cover:

- category visibility generation
- page visibility generation
- automatic generation from category hierarchy
- value-based generated progression
- addon merge rules across multiple providers/books
- runtime changes to research services or visibility services

## Problem Statement

The research-side authoring API is becoming safer and clearer, but book-side authoring still exposes rough edges.

Today, a book author who wants research-backed visibility must manually:

- author explicit research facts, hooks, and nodes on the research side
- keep those ids aligned with book content ids
- reference the resulting node ids from book conditions manually

That explicit path is correct, but it leaves two authoring gaps:

1. book datagen lacks a clean typed glue layer for research-backed conditions
2. the very common pattern of “view parent entry once to unlock child entry” is still fully manual even though the book model already contains the parent→child relationship

This slice fills those gaps without weakening the research/book boundary.

## Feature 1: Book-Side Research Glue

Book datagen should gain narrow helper APIs that accept typed research refs rather than requiring authors to spell raw research ids into condition builders.

The intended effect is ergonomics only.

Examples of the desired authoring style include shapes such as:

```java
this.visibleWhen(nodeRef);
this.visibleWhenUnlocked(nodeRef);
```

The exact method names may differ, but the semantics should remain narrow:

- accept a `ResearchNodeRef`
- produce the existing canonical research-backed book condition model
- avoid inventing a second condition system

The generated book JSON must remain semantically equivalent to today’s explicit `research_node_unlocked` condition output.

## Feature 2: Generated Entry-Hierarchy Progression

### Opt-In Model

Generated entry-hierarchy progression is enabled by a single book/provider-level setting.

This should live on the existing book datagen authoring path, such as a book model or single-book-provider setup hook, so a modder can opt an entire book in with one setting or function call.

The intended semantics are:

- disabled by default
- when enabled, applies to the book’s entry parent→child relationships
- entries without parent links are unaffected

### Scope Rule

The generated model applies only to entry parent→child relationships.

It does not derive progression for:

- categories
- pages
- arbitrary links or redirects
- non-parent structural relationships

This keeps the slice narrow and proves one clear convenience path first.

## Generated Semantics

When generated entry-hierarchy progression is enabled, each child entry with one or more parent entries participates in the following explicit research model.

For each parent entry of the child:

1. generate a fact representing that parent entry having been viewed once
2. generate an `entry_viewed_once` hook that grants that fact when that specific parent entry is viewed

For the child entry:

3. generate a research node representing the child entry unlock milestone
4. generate or apply a child entry visibility condition that depends on that generated node

The intended progression flow is therefore:

1. the player views a parent entry once
2. the generated hook grants the parent-viewed fact
3. the generated child node unlocks when its authored predicate becomes true
4. the child entry becomes visible through an ordinary research-backed book condition

## Multiple Parent Semantics

If a child entry has multiple parent entries, the generated unlock rule uses `AND` semantics.

That means:

- each parent contributes its own viewed-once fact
- the child node unlock predicate requires all generated parent-viewed facts for that child

This makes the generated model match the authored parent set rather than silently weakening it to “any parent”.

## Generated Node Predicate Rule

To preserve the research architecture cleanly, generated child unlock nodes should keep dependency and unlock semantics distinct.

For this slice, generated child nodes should use:

- a minimal dependency predicate
- an unlock predicate that requires the generated parent-viewed facts for that child

The parent→child relationship in this generated path is therefore modeled primarily as explicit unlock requirements driven by viewed-once ingress, not as a second inferred runtime authority on the book side.

This keeps the compiler narrow and avoids over-inventing graph semantics in the first generated slice.

## Canonical Output Rule

This slice must emit ordinary canonical resources.

Generated entry-hierarchy progression must become the same explicit resource kinds already used by the runtime:

- fact definitions
- `entry_viewed_once` hook definitions
- node definitions
- ordinary book conditions backed by research nodes

No hidden compiler-only progression model may survive into runtime.

The purpose of the compiler is to produce canonical research, not to introduce a second runtime system.

## Deterministic Id Rule

All generated ids must be deterministic and derived from stable book/entry identity.

They should be generated from the book root and entry paths rather than from incidental ordering.

The generated ids should be predictable enough that authors and diagnostics can trace:

- which entry-parent relationship created a viewed-once fact or hook
- which child entry a generated unlock node belongs to

The exact naming convention can be chosen during implementation, but it must satisfy these constraints:

- stable across datagen runs when source ids do not change
- clearly namespaced to avoid collisions with unrelated authored content
- different resource kinds keep different ids where appropriate

## Explicit Authoring Wins Rule

Explicit authoring remains authoritative at authoring time.

If a child entry already has an explicit visibility condition, generated entry-hierarchy progression must skip generation for that entry.

This rule avoids hidden condition merging and keeps the generated path safely additive.

In this slice, auto-generation should not:

- override an explicit child condition
- AND-merge with an explicit child condition automatically
- error merely because explicit authoring exists

The rule is simply:

> explicit child entry conditions opt that entry out of generated hierarchy progression

## Compiler Boundary

The compiler in this slice should remain narrow and datagen-only.

Its job is:

1. inspect the authored book entry hierarchy for one book
2. identify child entries that are eligible for generated hierarchy progression
3. derive generated research facts, hooks, and nodes for those entries
4. attach or emit the corresponding research-backed child visibility conditions
5. hand the resulting canonical research definitions into the existing research datagen output path

The compiler should not:

- change runtime loading
- create hidden runtime lookups
- attempt cross-book reasoning
- infer broader research graphs from unrelated structure

## Relationship To The Explicit Research API

The explicit research-side API remains first-class.

This slice adds a second authoring lane for a narrow common case, not a replacement for authored research.

Authors must still be free to:

- author research explicitly through the research provider API
- reference explicit `ResearchNodeRef` values from book datagen
- mix explicit research usage with generated hierarchy progression at the book level, subject to the explicit-wins rule per entry

The intended model is:

- use explicit research authoring when progression is bespoke
- use generated hierarchy progression when the standard parent-entry-read pattern is sufficient

## Validation Posture

Runtime validation remains authoritative and unchanged.

This slice may add authoring-time/datagen-time checks for the generated path, but it must not weaken runtime validation.

Generated output must still validate as ordinary research data.

Useful datagen-time checks for this slice include:

- deterministic generated id conflicts fail clearly
- generated resources do not collide with explicit resources in the same output namespace
- entries with explicit visibility conditions are skipped predictably
- generated child conditions target generated or otherwise valid research node ids

## Failure Handling

The compiler should stay strict and unsurprising.

If generated ids collide or generated output would be ambiguous, datagen should fail clearly rather than silently rewriting author intent.

If an entry is ineligible because it already has an explicit condition, datagen should skip generation for that entry without treating it as an error.

The guiding rule is:

> convenience should either produce canonical explicit progression or stay out of the way

## Manual Verification

Manual verification for this slice should confirm:

1. book datagen can author research-backed conditions from typed node refs
2. enabling the book/provider setting generates canonical research for eligible entry parent→child links
3. generated facts, hooks, and nodes are deterministic and inspectable
4. a child entry with one parent unlocks after that parent entry is viewed once
5. a child entry with two parents unlocks only after both parent entries are viewed once
6. a child entry with an explicit visibility condition is skipped by the generator
7. runtime behavior still flows through the ordinary research hook and node services
8. no category/page auto-generation was introduced accidentally

## Completion Criteria

This slice is complete when:

- book datagen exposes narrow research-backed condition glue based on typed research refs
- a book/provider-level opt-in exists for generated entry-hierarchy progression
- eligible entry parent→child links compile to canonical fact, hook, and node definitions
- generated child entry visibility uses ordinary research-backed book conditions
- multi-parent child entries use `AND` semantics
- child entries with explicit conditions are skipped by the generator
- runtime semantics remain unchanged
- emitted resources remain canonical and inspectable

## Recommended Next Step After This Slice

Once this slice is proven, the next logical follow-up is incremental polish rather than broader magic.

Likely next steps include:

1. expanding book-side research glue ergonomics further for repeated condition patterns
2. deciding whether category or page-level generated progression is actually warranted
3. only then resuming broader trigger expansion such as `item_crafted`
