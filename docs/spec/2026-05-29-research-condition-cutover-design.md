<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research Condition Cutover Design

Date: 2026-05-29
Branch: `feat/research-system/main`

## Summary

This document defines the next research-system slice after the demo `entry_read` migration.

The slice prepares a broader progression cutover by classifying all registered book condition types and making the progression-facing subset align with the revised research architecture.

The purpose is not to add authoring sugar or a generalized bridge from books into research. The purpose is to remove the remaining legacy progression condition shape that no longer fits the architecture, define how legacy unlock-style conditions map onto research-backed authoring, and introduce the smallest explicit research replacement needed for advancement-backed progression.

This slice is still a boundary-cleanup slice, not a convenience slice.

## Goals

- Inventory all currently registered book condition types as cutover inputs.
- Remove `entry_read` entirely from the authored and runtime condition surface.
- Define the node-first replacement rule for `entry_unlocked`.
- Add an explicit research-backed replacement path for `advancement`-based progression.
- Keep clearly book-local or environment-local conditions outside the research cutover.
- Preserve the revised architecture boundary where research owns progression and books consume research through conditions.

## Non-Goals

- Do not add book-first generation helpers.
- Do not add typed refs, bridge/compiler behavior, or inferred ids.
- Do not convert book-local display/environment conditions into durable research state.
- Do not add optional-dependency research predicates in this slice.
- Do not redesign the full trigger roadmap beyond the explicit advancement-backed addition needed here.
- Do not remove `mod_loaded` as a book condition.

## Architectural Constraints From The Revised Design

This slice must stay within these already-agreed rules:

- research is the authoritative durable progression layer
- books are downstream consumers of research
- books should normally gate visibility on research nodes
- fact/value queries are secondary and only valid for special cases
- external events must become explicit fact/value mutations before research predicates observe them
- research may not depend on book visibility, unread state, or generic book interaction history
- no hidden runtime bridge from book structure to research state may be introduced

Because of those rules, the slice must prefer node-targeting cutovers over direct book-local semantic emulation.

## Inventory Of Registered Book Condition Types

At the start of this slice, the registered condition types are:

1. `none`
2. `advancement`
3. `entry_unlocked`
4. `entry_read`
5. `or`
6. `and`
7. `true`
8. `false`
9. `mod_loaded`
10. `category_has_visible_entries`
11. `research_node_unlocked`

This slice treats that registered set as the cutover input, even if some conditions are rare or unused in current authored content.

## Cutover Classification

### Conditions That Stay Valid As Research-Facing Book Conditions

These remain part of the supported progression-facing surface:

- `research_node_unlocked`
- `and`
- `or`

`research_node_unlocked` remains the primary visibility API.

`and` and `or` remain structural combinators only. Their role is unchanged: they compose other conditions but do not create a separate progression authority.

## Conditions That Remain Book-Only

These conditions are intentionally outside the research cutover surface:

- `mod_loaded`
- `category_has_visible_entries`
- `none`
- `true`
- `false`

### `mod_loaded`

`mod_loaded` is an environment gate, not durable player progression.

It must remain a book condition.

This slice does not convert it into a research predicate.

### `category_has_visible_entries`

`category_has_visible_entries` depends on downstream book visibility and therefore sits on the wrong side of the research/book boundary.

It must remain book-local.

### `none`, `true`, and `false`

These are sentinel/display/default conditions, not progression primitives.

They remain book-local and out of scope for research cutover.

## Condition Removed Entirely: `entry_read`

`entry_read` must be removed entirely.

That means:

- it is no longer a supported authored condition type
- it is no longer a supported runtime condition type
- validation should reject new authored `entry_read` usage
- existing content must be migrated away rather than translated mechanically

The replacement rule is not “create a research-flavored `entry_read` condition”.

The replacement rule is:

1. represent the relevant viewed-once event explicitly through research hooks
2. mutate research facts or values from that event
3. unlock a research node from explicit research rules
4. gate book visibility through research-backed conditions, primarily nodes

This preserves the agreed architecture that viewed/read history only becomes progression when explicitly modeled as research state.

## Node-Targeting Cutover Rule: `entry_unlocked`

`entry_unlocked` must not become a new durable research primitive.

Instead, it must cut over to the same research milestone as the referenced entry.

The rule for authored migration is:

- if a condition currently depends on `entry_unlocked(target_entry)`
- then the migrated condition should depend on the same research node that authoritatively unlocks `target_entry`

This keeps nodes as the primary book visibility API and avoids introducing a second runtime authority such as “entry unlocked state inside research”.

### Required Authoring Invariant

For this mapping to be valid, any entry referenced through the cutover path must have an explicit authoritative research milestone.

If an entry does not have one, migration must fail clearly rather than inferring a hidden bridge.

### No Runtime Book-To-Research Lookup Magic

This slice may use authored/datagen metadata to connect an entry to its authoritative research node.

It must not add a hidden runtime rule that treats every book entry id as if it implicitly owned a research node.

The mapping must stay explicit.

## New Explicit Research Replacement: `advancement`

`advancement` is progression-facing, but its current implementation reads raw external progression state directly from the book condition layer.

That does not fit the revised architecture.

This slice therefore introduces an explicit research-backed replacement path for advancement-based progression.

### Required Shape

The replacement must follow the existing research model:

1. advancement progress is observed through an explicit research ingress path
2. that ingress path grants a fact or mutates a value
3. research reevaluates explicit node rules
4. books gate visibility primarily through `research_node_unlocked`

### Smallest Useful Addition

The smallest acceptable addition is one explicit advancement-backed research trigger family or equivalent explicit research ingress mechanism dedicated to advancement progress.

This slice does not need to solve every possible gameplay trigger. It only needs to replace the current direct advancement condition path with an architecture-consistent research path.

### Secondary Research-Facing Conditions

The revised design already allows books to query research facts and values for special cases.

This slice may define secondary research-facing book conditions such as fact/value predicates only if they are needed for a concrete advancement-backed scenario and node-only authoring would be awkward.

Even if such secondary conditions are introduced, the normal target for progression-facing visibility remains research nodes.

## Target State Per Registered Condition Type

### `research_node_unlocked`

- keep
- remains primary progression-facing visibility condition

### `and`

- keep
- composition only

### `or`

- keep
- composition only

### `entry_unlocked`

- remove as a standalone semantic from the final cutover surface
- migrate usages to the authoritative research node for the referenced entry

### `entry_read`

- remove entirely
- reject authored/runtime support after cutover

### `advancement`

- remove as a standalone book-local progression condition from the final cutover surface
- replace with explicit research ingress plus research-backed visibility

### `mod_loaded`

- keep as book-only

### `category_has_visible_entries`

- keep as book-only

### `none`

- keep as book-only/default sentinel

### `true`

- keep as book-only/default sentinel

### `false`

- keep as book-only/default sentinel

## Validation Requirements

This slice should add validation for the cutover rules.

Validate that:

- authored `entry_read` usage is rejected
- `entry_unlocked` cutover references resolve to entries that have an explicit authoritative research milestone
- advancement-backed research ingress definitions are explicit and resolve cleanly
- research-backed replacements still obey the rule that hooks/events mutate only facts or values, not nodes directly
- progression-facing authored conditions continue to prefer nodes over lower-level fact/value checks except where explicitly justified

Validation failures should be explicit and should not silently fall back to old semantics.

## Manual Verification

Manual verification for this slice should confirm:

1. no authored progression still depends on `entry_read`
2. `entry_unlocked` migrations resolve through the correct authoritative research milestone
3. advancement-backed progression works through explicit research, not direct book-side advancement checks
4. `mod_loaded` still behaves as a book/environment gate
5. `category_has_visible_entries` still behaves as a downstream book visibility helper
6. node-first visibility remains the dominant authored pattern

## Completion Criteria

This slice is complete when:

- `entry_read` has been removed entirely from the supported condition surface
- `entry_unlocked` has a defined node-targeting cutover rule
- `advancement` has an explicit research-backed replacement path
- book-only conditions are explicitly documented and preserved as outside the research cutover
- validation rules enforce the new boundary clearly

## Future Work Note For The Revised Design

After this slice, it may become useful to add a research-side optional-dependency predicate or function for content that belongs to an optional dependency mod.

That possibility should be documented as future work only.

It is not part of this slice and must not be used to turn `mod_loaded` itself into a research-owned progression condition.

## Recommended Next Step After This Spec

After this spec is accepted, the implementation plan should be written for a slice that:

1. removes `entry_read`
2. defines and applies the `entry_unlocked` node-targeting cutover rule
3. adds the explicit advancement-backed research replacement
4. leaves book-only conditions untouched except for validation/documentation boundary cleanup
