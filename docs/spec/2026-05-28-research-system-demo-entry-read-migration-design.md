<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research System Demo `entry_read` Migration Design

Date: 2026-05-28
Branch: `feat/research-system/main`

## Summary

This document defines the second implementation slice for the revised research system.

The slice migrates all `entry_read`-based progression in the demo book to research-backed progression while keeping the legacy unlock/read system available for safety. This is a migration slice, not a removal slice.

The purpose is to move demo-book progression semantics away from legacy read-state conditions and onto explicit research facts, nodes, and hooks, without yet introducing new trigger families or authoring sugar.

## Goals

- Migrate all demo-book uses of `entry_read` to research-backed progression.
- Cover direct and nested/composite `entry_read` usage.
- Keep the migration contained to the demo book.
- Preserve the old runtime systems so rollback and comparison remain possible.
- Continue proving that research is the intended progression authority.

## Non-Goals

- Do not migrate non-demo content.
- Do not remove legacy `entry_read` support from runtime.
- Do not add new trigger families such as `item_crafted` or `item_acquired`.
- Do not add values unless a concrete demo-book case requires them.
- Do not add typed refs, generated authoring sugar, or bridge/compiler features.
- Do not remove existing legacy save structures in this slice.

## Slice Boundary

This slice covers exactly these changes:

1. identify every demo-book condition that currently depends on `entry_read`
2. author equivalent research facts, nodes, and hooks for those demo flows
3. replace demo-book `entry_read` conditions with research-backed conditions
4. preserve legacy runtime support for non-demo and fallback usage

## Current Demo-Book `entry_read` Usage To Migrate

At the start of this slice, the demo book still uses `entry_read` in these areas:

- `FormattingCategory`
  - `advanced`
  - `link`
- `DemoBook`
  - `conditional` category
- `FeaturesCategory`
  - `spotlight`
  - `component_icon`
  - `empty`
  - `image`
  - `custom_icon`
  - `two_parents` via nested composite conditions

This slice migrates all of them.

## Migration Rule

For demo-book progression in this slice:

- do not use `entry_read(...)` as the authored progression condition
- use `entry_viewed_once` hooks to grant research facts
- unlock research nodes from those facts
- gate book visibility with research-backed conditions

The logical progression shape should remain the same from a player perspective.

## Authoring Model For This Slice

This slice continues using the explicit canonical model introduced in slice 1.

That means:

- explicit fact ids
- explicit node ids
- explicit hook definitions
- explicit book conditions querying research ids

No inference from book structure is added here.

## Conversion Pattern

Each migrated demo-book `entry_read` dependency should be converted using this pattern:

1. define a fact representing that the source entry was viewed once
2. define a node representing the progression milestone that should replace the old `entry_read` dependency
3. define a hook from `entry_viewed_once(source entry)` to the fact
4. replace the old `entry_read(source entry)` condition with `research_node_unlocked(target node)`

For nested/composite conditions, replace only the `entry_read(...)` leaf conditions and preserve the surrounding composition.

## Condition Composition Rule

If a demo-book condition currently uses:

- `and(entry_read(A), entry_read(B))`

then the migrated condition should remain structurally equivalent, such as:

- `and(research_node_unlocked(node_for_A), research_node_unlocked(node_for_B))`

Do not collapse unrelated conditions or redesign composition semantics in this slice.

## Category Migration

This slice includes migration of the demo `conditional` category.

Its old gate:

- `entry_read(features/condition_root)`

should be replaced by a research-backed gate tied to the same progression event that was already introduced in slice 1.

## Expected Runtime Semantics

After this slice:

- demo-book progression should be authored through research
- the demo book should no longer rely on legacy `entry_read` conditions for progression semantics
- legacy runtime support should still exist, but it should no longer be the authored progression source for demo-book flows covered by this slice

## Validation Requirements

This slice extends only the existing phase-1 validation posture.

Validate that:

- all newly authored hook targets resolve
- all newly authored facts resolve
- all newly authored node references resolve
- all migrated book conditions reference valid research nodes
- nested/composite migrated conditions remain structurally valid

## Manual Verification

Manual verification for this slice should confirm:

1. formatting progression still works from `basic` to `advanced` to `link`
2. features progression still works from `recipe` to `spotlight` to later chained entries
3. `two_parents` still unlocks only when both required demo progress milestones are satisfied
4. the `conditional` category unlocks through research-backed progression
5. already migrated slice-1 condition entries still work
6. demo-book progression no longer depends on authored `entry_read` conditions
7. legacy non-demo behavior remains unchanged
8. research reset correctly relocks migrated demo-book content

## Completion Criteria

This slice is complete when:

- all demo-book `entry_read` conditions have been migrated to research-backed conditions
- composite demo-book `entry_read` usage is also migrated
- the `conditional` category is migrated
- no demo-book authored progression still depends on `entry_read`
- legacy runtime support remains present for safety

## Recommended Next Slice After This One

Once demo-book `entry_read` migration is complete, the next smallest useful slice should be one of:

1. add the first second-phase trigger family such as `item_crafted`
2. add a concrete value-based progression scenario if a real demo use case needs it
3. begin introducing authoring helpers only after the explicit demo migration path is proven stable

The preferred next step after this slice is adding one new explicit trigger family, most likely `item_crafted`.
