<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research Authoring Sugar Design

Date: 2026-05-31
Branch: `feat/research-system/main`

## Summary

This document defines the next research-system slice after the runtime cutover work.

The slice adds research-side datagen glue and authoring sugar only. Its purpose is to make explicit research resources easier and safer to author without changing runtime semantics, resource shapes, or the research/book architecture boundary.

The slice introduces typed authoring refs, builder-style research definitions, and provider glue that compile to the same canonical explicit resources already used by the runtime:

- `facts.json`
- `nodes.json`
- `hooks.json`
- `advancement_hooks.json`

This is an authoring-convenience slice, not a runtime slice, not a trigger-expansion slice, and not a book-side glue slice.

## Goals

- Improve ergonomics for authoring research data in datagen code.
- Add typed refs for research-side authoring safety.
- Add builder/glue APIs for facts, nodes, `entry_viewed_once` hooks, and advancement hooks.
- Keep the emitted datapack resources fully explicit and canonical.
- Prove the new API by migrating current demo research authoring to it.
- Preserve the current runtime model and validation model unchanged.

## Non-Goals

- Do not add book-side glue or book-first authoring in this slice.
- Do not add new runtime trigger families such as `item_crafted`.
- Do not change runtime research semantics, persistence, or validation rules.
- Do not add inferred ids or silent generation from book structure.
- Do not add patch/merge semantics or datapack composition rules.
- Do not add a full bridge/compiler layer beyond the narrow research-side datagen glue needed here.

## Architectural Constraints

This slice must preserve the revised research-system architecture:

- research remains the authoritative durable progression layer
- books remain downstream consumers of research
- the explicit canonical model remains the only runtime target language
- authoring convenience must compile to explicit authored ids/resources
- typed refs must not become runtime authority or hidden lookup semantics

Because of those constraints, the slice must stay strictly on the authoring/datagen side.

## Slice Boundary

This slice covers exactly these changes:

1. add typed research refs for datagen-time safety
2. add builder-style authoring APIs for research resources
3. add provider glue that compiles builder input into the existing explicit resource files
4. migrate existing demo research authoring to the new API as the proof case

This slice does not cover:

- book condition/helper glue
- automatic entry/category-to-research linking
- new trigger families
- value-focused runtime expansion
- changes to research data loading or runtime execution semantics

## Canonical Output Rule

The output of this slice must remain the same canonical explicit resource model already defined by the revised research-system spec.

The authoring layer may become nicer, but the generated resources must still be explicit definitions with author-chosen ids.

The intended output remains:

- explicit fact definitions
- explicit node definitions
- explicit `entry_viewed_once` hook definitions
- explicit advancement hook definitions

No generated output may depend on hidden runtime conventions.

## Typed Ref Model

This slice introduces typed refs as authoring-time safety only.

Expected ref directions include:

- `ResearchFactRef`
- `ResearchNodeRef`
- optionally `ResearchGraphRef` if needed by the builder surface

These refs should:

- wrap explicit ids
- prevent accidental cross-use where a fact id is passed where a node id is required
- improve readability at authoring sites
- stay usable in plain datagen/provider code without a heavy framework

These refs must not:

- become runtime objects with progression semantics
- create registry-style global lookup behavior
- replace canonical ids in generated resources

The rule is:

> typed refs exist to make authoring safer, then compile away to explicit ids

## Authoring API Shape

The slice should add a small research-side datagen API that makes explicit research definitions less error-prone.

The API should cover at least:

- fact definition authoring
- node definition authoring
- `entry_viewed_once` hook definition authoring
- advancement hook definition authoring

The preferred style is small builder/glue objects rather than a broad DSL.

The builder layer should stay close to the existing runtime concepts so authored intent remains obvious.

## Node Authoring Rules

Node authoring helpers must preserve the current explicit model.

That means the API should continue to represent:

- node ids explicitly
- graph membership explicitly
- dependency predicates as node-oriented relationships
- unlock predicates as fact/value-oriented relationships

The builder API may reduce boilerplate, but it must not blur the architectural distinction between dependency predicates and unlock predicates.

## Hook Authoring Rules

Hook authoring helpers must remain explicit.

For `entry_viewed_once` hooks, the authoring surface must still make the author specify:

- the hook id
- the trigger target entry id
- the mutated fact or value target

For advancement hooks, the authoring surface must still make the author specify:

- the hook id
- the advancement id
- the mutated fact target

This slice may improve ergonomics for those definitions, but it must not infer them from book structure or introduce hidden mappings.

## Provider Glue

`ResearchDataProvider` should become the compilation point from typed/builder authoring input into the existing explicit resource output.

The intended model is:

1. author research content in Java using typed refs and narrow builders
2. normalize that authoring input into the existing explicit definition records
3. emit the same JSON resource files the runtime already loads

The provider glue may centralize repeated boilerplate and shared authoring helpers, but it should not create a second conceptual model that diverges from the runtime resource shapes.

## Migration Strategy

The proof case for this slice is migrating existing demo research authoring to the new research-side API.

That includes the current demo facts, nodes, `entry_viewed_once` hooks, and advancement hooks already authored in datagen.

The migration goal is not to preserve two equally first-class authoring styles forever.

The intended target state after this slice is:

- demo research content authors through the new typed/builder API
- internal low-level records may still exist as the compiler target
- new research datagen examples should prefer the new API

## Validation Posture

Runtime/datapack validation remains authoritative and unchanged.

Typed refs and builders are an earlier safety layer, not a replacement for runtime validation.

After this slice:

- authoring code should catch some mistakes earlier through typed APIs
- generated resources should still pass the same explicit research validation as before
- no validation rule should be weakened because the authoring API is nicer

## Manual Verification

Manual verification for this slice should confirm:

1. the demo research datagen code builds successfully through the new API
2. generated `facts.json` remains explicit and valid
3. generated `nodes.json` remains explicit and valid
4. generated `hooks.json` remains explicit and valid
5. generated `advancement_hooks.json` remains explicit and valid
6. runtime behavior for the demo research flows is unchanged
7. no book-side glue or inferred progression behavior was introduced accidentally

## Completion Criteria

This slice is complete when:

- research-side typed refs exist for the intended authoring surface
- builder/glue APIs exist for facts, nodes, `entry_viewed_once` hooks, and advancement hooks
- `ResearchDataProvider` compiles those authoring inputs into the same explicit canonical resources
- current demo research authoring is migrated to the new API
- generated resources remain explicit and runtime-compatible
- no runtime semantics change and no book-side glue is introduced

## Recommended Next Step After This Slice

Once this slice is complete, the next useful slice should be book-side glue that consumes the research-side authoring model more cleanly without introducing runtime authority on the book side.

Only after those authoring ergonomics slices are proven should the project resume trigger expansion such as `item_crafted`.
