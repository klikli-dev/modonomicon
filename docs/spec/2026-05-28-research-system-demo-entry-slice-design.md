<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research System Demo Entry Slice Design

Date: 2026-05-28
Branch: `feat/research-system-revised`

## Summary

This document defines the first implementation slice for the revised research system.

The slice adds a new research-backed progression path alongside the existing book unlock system, without removing the old system. It is intentionally limited to the demo book's existing entry chain:

- `modonomicon:features/condition_root`
- `modonomicon:features/condition_level_1`
- `modonomicon:features/condition_level_2`

Only these entries move to research-backed visibility in this slice. The rest of the book system continues to use the legacy unlock/read model unchanged.

## Goals

- Prove that research can drive real book visibility without deleting the old system.
- Keep the migration boundary tiny and manually verifiable.
- Reuse the existing demo-book condition chain as the first end-to-end proof.
- Introduce the smallest useful vertical slice of the new architecture.

## Non-Goals

- Do not migrate the `conditional` category in this slice.
- Do not remove or disable legacy book unlock state.
- Do not migrate any non-demo content.
- Do not add item, advancement, or other new trigger families.
- Do not add authoring sugar, inferred ids, or generated research helpers.
- Do not add a global switchover from old visibility conditions to research conditions.

## Slice Boundary

This slice covers exactly four capabilities:

1. minimal durable research state for facts and unlocked nodes
2. one hook trigger: `entry_viewed_once`
3. one research-backed visibility condition: `research_node_unlocked`
4. conversion of the three demo entries listed above to the new condition path

The slice does not require values. Facts and nodes are sufficient for this demo chain.

## Existing Demo Content Used for the Slice

The slice reuses the existing demo-book progression chain already authored in the features category.

Current legacy flow:

- `condition_root` is always visible
- `condition_level_1` is gated by `entry_read(condition_root)`
- `condition_level_2` is gated by `entry_read(condition_level_1)`

The new slice preserves that player-facing progression shape, but changes the authority behind it:

- reading `condition_root` grants a research fact
- a research node unlocks from that fact
- `condition_level_1` becomes visible because its condition queries research
- reading `condition_level_1` grants the next research fact
- another research node unlocks
- `condition_level_2` becomes visible because its condition queries research

## Architecture for This Slice

### Legacy System

The existing book unlock/read flow remains active and unchanged for the rest of the codebase.

Legacy responsibilities that remain in place for now:

- existing read/unlock persistence
- existing visual/read markers
- existing visibility conditions outside the demo slice

### New Research System

The new research system is added beside the legacy system.

For this slice, research owns only:

- granted fact ids
- unlocked research node ids

The research runtime must be able to:

- load authored research resources
- validate references
- mutate facts from hooks
- reevaluate node unlock state
- answer visibility queries from book conditions

### Integration Rule

The migration boundary is explicit and content-local.

Only entries authored with the new research-backed condition participate in the new path. Everything else continues to behave exactly as before.

## Authored Research Model for the Slice

This slice uses explicit authored ids only.

### Facts

- `modonomicon:demo/condition_root_viewed`
- `modonomicon:demo/condition_level_1_viewed`

### Nodes

- `modonomicon:demo/condition_level_1_unlocked`
- `modonomicon:demo/condition_level_2_unlocked`

### Hook Definitions

- `entry_viewed_once(modonomicon:features/condition_root)` grants fact `modonomicon:demo/condition_root_viewed`
- `entry_viewed_once(modonomicon:features/condition_level_1)` grants fact `modonomicon:demo/condition_level_1_viewed`

### Node Predicates

- `condition_level_1_unlocked` unlocks when fact `condition_root_viewed` is present
- `condition_level_2_unlocked` unlocks when fact `condition_level_1_viewed` is present

No dependency graph complexity is needed for this slice beyond simple single-fact unlock predicates.

## Book-Side Changes

Only three entries are updated.

### `condition_root`

- remains visible by default
- participates in research only as the hook event source
- does not require a research visibility condition itself

### `condition_level_1`

- remove the legacy `entry_read(condition_root)` gate from this entry
- replace it with a research-backed visibility condition checking `modonomicon:demo/condition_level_1_unlocked`

### `condition_level_2`

- remove the legacy `entry_read(condition_level_1)` gate from this entry
- replace it with a research-backed visibility condition checking `modonomicon:demo/condition_level_2_unlocked`

No category-level migration is included.

## Runtime Semantics

The runtime flow for the slice is:

1. player opens `condition_root`
2. existing book interaction flow records the read/view event as it already does
3. the new hook path also observes the authored `entry_viewed_once` trigger
4. the configured fact is granted in research state
5. research reevaluates authored nodes
6. `condition_level_1_unlocked` becomes unlocked
7. visibility for `condition_level_1` is recomputed from the research condition
8. player opens `condition_level_1`
9. the second hook grants the second fact
10. research reevaluates again
11. `condition_level_2_unlocked` becomes unlocked
12. visibility for `condition_level_2` is recomputed from the research condition

Hooks never unlock nodes directly.

## Validation Requirements

This slice needs only narrow validation.

Validate that:

- hook event ids resolve to supported trigger types
- hook target facts resolve
- research-node ids resolve in book conditions
- node predicate fact references resolve
- hooks cannot target nodes directly
- duplicate ids are rejected clearly

Validation errors should identify the bad resource, the bad reference, and the violated rule.

## Manual Verification

Manual verification for this slice should confirm:

1. `condition_root` is visible at the start
2. `condition_level_1` is hidden at the start
3. `condition_level_2` is hidden at the start
4. opening `condition_root` grants the first research fact
5. opening `condition_root` unlocks the node for `condition_level_1`
6. `condition_level_1` becomes visible after research reevaluation
7. opening `condition_level_1` grants the second research fact
8. opening `condition_level_1` unlocks the node for `condition_level_2`
9. `condition_level_2` becomes visible after research reevaluation
10. unrelated old-condition demo content behaves unchanged
11. resetting research state removes only research progression for this slice
12. visual/read state remains separate from research progression

## Phase Breakdown

### Phase 1a: Passive Research Foundation

Add the minimal data/runtime pieces without changing demo entry behavior yet:

- research state persistence
- research resource loading
- minimal validation
- node reevaluation service
- debug/admin inspection or reset support

Exit criteria:

- authored research data loads successfully
- research state can be observed and reset
- no visible regression in the demo book

### Phase 1b: Hook Wiring

Add the first trigger path:

- `entry_viewed_once`
- fact-grant mutation only

Wire it into the existing entry interaction/read flow without removing legacy handling.

Exit criteria:

- opening a configured entry grants the configured fact once
- existing legacy read behavior still works

### Phase 1c: Research Visibility Condition

Add one book condition type:

- `research_node_unlocked`

Do not migrate content yet beyond any temporary local testing.

Exit criteria:

- book visibility can query unlocked research nodes
- old visibility conditions still work unchanged

### Phase 1d: Demo Entry Conversion

Convert only the three demo entries to the new path:

- author the explicit demo facts, nodes, and hooks
- move `condition_level_1` to `research_node_unlocked(condition_level_1_unlocked)`
- move `condition_level_2` to `research_node_unlocked(condition_level_2_unlocked)`

Exit criteria:

- the demo chain works end to end from real book interaction
- no other content requires migration

## Completion Criteria for the Slice

The slice is complete when:

- the new research runtime exists beside the old system
- the three demo entries use the new research-backed path successfully
- the rest of the book system still uses the old path unchanged
- the slice can be manually verified from a clean save without special scaffolding

## Next Slice After This One

If this slice succeeds, the next smallest follow-up should be one of:

1. migrate the `conditional` demo category to research-backed visibility
2. add value support if needed by a concrete follow-up scenario
3. add a second explicit trigger family while preserving the same architecture boundary

The preferred next step is migrating the `conditional` category only after the entry slice is proven stable.
