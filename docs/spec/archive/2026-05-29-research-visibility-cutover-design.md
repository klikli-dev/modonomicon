<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research Visibility Cutover Design

Date: 2026-05-29
Branch: `feat/research-system/main`

## Summary

This document defines the slice that finishes the core authority cutover from legacy book unlock state to the revised research architecture.

The slice removes category, entry, and page unlock state as persisted progression authority. Book visibility and access become computed results derived from research state and authored book conditions. Book-side persisted state remains only for downstream visual and interaction concerns.

This is a big, clean cutover slice. It is not a trigger-expansion slice, a values slice, or a policy slice.

## Goals

- Make research the only durable progression authority at runtime.
- Remove persisted book unlock state for categories, entries, and pages.
- Make category, entry, and page visibility computed rather than persisted.
- Cut client/runtime access checks over to a computed visibility/access service.
- Keep book-side persisted state only for visual and interaction concerns.
- Remove the legacy unlock snapshot sync and recompute pipeline.
- Keep the architecture aligned with the active revised research-system spec.

## Non-Goals

- Do not add new trigger families such as `item_crafted`.
- Do not add a value-based authored scenario in this slice.
- Do not design or implement a sanctioned research bypass mechanism in this slice.
- Do not add authoring sugar, typed refs, bridge/compiler behavior, or generation helpers.
- Do not silently preserve legacy unlock-state behavior behind a compatibility shim.

## Architectural Target

After this slice:

- research is the only durable progression authority
- category, entry, and page visibility are computed from research and book conditions
- book-side state is persisted only for visual/interaction concerns
- no separate persisted book unlock state exists for progression-facing visibility

The core invariant becomes:

- research is upstream
- computed visibility/access is the runtime projection of research into books
- visual state is downstream and never progression-authoritative

## Current Problem Statement

The current implementation has already moved facts, nodes, hooks, and migrated demo progression onto research-backed semantics, but the live book runtime still relies heavily on legacy unlock-state machinery.

That legacy layer still persists and syncs unlocked categories, entries, and pages, and many runtime/UI callers still use those stored unlock flags as the source of truth for visibility and access.

This conflicts with the revised design, which requires that:

- book visibility is computed, not persisted
- books consume research state rather than storing a second progression authority
- book-side state remains limited to visual/UI concerns

## Scope Boundary

This slice covers a full progression-authority cutover for:

- categories
- entries
- pages

It is not limited to category and entry visibility only. Page unlock state is included in the same cutover so the runtime shape is coherent and there is no second follow-up slice needed just to finish the same architectural move.

## Target State: Delete, Move, Keep

### Delete As Progression Authority

These must stop existing as persisted/synced progression state:

- unlocked categories
- unlocked entries
- unlocked pages
- the idea of a synced book unlock snapshot as the client/runtime authority for visibility
- legacy recompute passes whose purpose is to materialize visibility into stored unlock flags

The cutover therefore aims to remove the progression role of:

- legacy unlocked-category storage
- legacy unlocked-entry storage
- legacy unlocked-page storage
- legacy unlock snapshot sync messages
- lifecycle hooks whose primary purpose is recomputing and syncing that unlock snapshot

### Keep, But Reclassify As Non-Progression State

These concerns may remain persisted on the book side, but only as visual or interaction state:

- read entries
- read categories
- unread markers
- bookmarks
- navigation/open-page state
- used command tracking
- recently-visible or recent-unlock presentation metadata such as timestamps

These concerns must no longer be bundled conceptually as “unlock state”.

### Keep Conceptually

These remain valid parts of the architecture:

- research state and research hook services
- research-backed book conditions such as `research_node_unlocked`
- book-local non-progression conditions such as `mod_loaded` and `category_has_visible_entries`
- visual-state storage and sync as a downstream concern

## Runtime Model After The Cutover

The authoritative runtime flow becomes:

1. an authored event happens
2. a research hook mutates fact or value state
3. research reevaluates node unlocks
4. book visibility/access services compute category, entry, and page access from:
   - research nodes, facts, and values
   - book-local downstream display conditions where allowed
5. visual state reacts downstream, such as marking newly visible content unread

There is no longer an unlock pass that writes unlocked category, entry, or page flags into persisted book state.

Instead, runtime depends on a computed visibility/access layer.

## Access And Visibility Cutover

The core runtime service boundary should become:

- research services answer progression facts
- visibility/access services answer whether a category, entry, or page is currently visible/available
- visual-state services answer unread/bookmark/navigation concerns only

Client and server callers that currently ask legacy unlock-state storage whether something is unlocked must be redirected to the computed visibility/access layer.

That includes:

- category index visibility
- search filtering
- link handling
- page access checks
- entry button/category button locked-state rendering
- recently visible or unread behavior that depends on content appearing due to research progression

## Migration Strategy

Although this is a single clean cutover slice architecturally, implementation should proceed in this order:

1. finish or introduce a visibility/access service that can answer category, entry, and page access directly from current research plus conditions
2. cut UI and runtime callers over to that service
3. move newly-visible/unread handling to compare computed visibility against downstream visual state rather than a stored unlock snapshot
4. remove persisted unlocked-category, unlocked-entry, and unlocked-page fields and their sync path
5. simplify lifecycle hooks so research sync and visual sync remain, but unlock recompute/sync goes away

This ordering keeps the end state clean while making the cutover implementable.

## Persistence Model After The Cutover

### Research Persistence

Research persistence remains the durable progression layer and continues to store only research-owned progression state.

### Book Visual/Interaction Persistence

Book persistence remains allowed only for downstream concerns such as:

- unread/read markers
- bookmarks
- navigation/open-page state
- command-use tracking
- recent presentation metadata

### Removed Persistence

Do not persist:

- unlocked category state
- unlocked entry state
- unlocked page state
- any snapshot whose purpose is mirroring current computed visibility

## Networking And Sync Target

After this slice, the networking model should no longer include a packet whose purpose is syncing a legacy unlock snapshot.

The client should instead rely on:

- synced research state where needed for progression-backed visibility decisions
- synced visual/interaction state for unread/bookmark/navigation concerns
- local computed visibility/access queries over those inputs

This slice does not require a hidden compatibility message that reconstructs old unlock flags on the client.

## Failure Handling

The cutover must remain strict.

If a category, entry, or page condition is invalid:

- fail during load or validation where possible
- do not silently fall back to legacy unlock-state behavior
- do not rebuild a hidden unlock snapshot as a compatibility layer

If some caller still depends on removed unlock fields after the cutover, that should fail clearly in development rather than being papered over at runtime.

## Validation Requirements

Validation/runtime checks after this slice should confirm:

- no persisted progression fields remain for unlocked categories, entries, or pages
- no packet or service remains whose purpose is syncing a legacy unlock snapshot
- category, entry, and page visibility can be answered through the computed visibility/access layer
- research-backed conditions continue to resolve correctly
- book-only display conditions still work on the downstream side
- visual-state persistence remains separate from progression authority

## Manual Verification

Manual verification for this slice should confirm:

1. category visibility changes correctly when research progression changes
2. entry visibility changes correctly when research progression changes
3. page visibility changes correctly when research progression changes
4. UI screens and link handlers use computed visibility rather than synced unlock flags
5. unread/new behavior still works when content becomes newly visible
6. bookmarks and navigation state still work
7. research reset no longer depends on rebuilding a legacy unlock snapshot
8. no legacy unlock-sync packet is needed for normal runtime behavior

## Completion Criteria

This slice is complete when:

- category, entry, and page availability are computed rather than persisted
- legacy book unlock state no longer acts as progression authority
- legacy unlock snapshot sync/recompute is removed
- client/runtime access flows use the computed visibility/access service
- only visual/interaction state remains persisted on the book side
- research reset no longer depends on rebuilding legacy unlock snapshots

## Recommended Next Step After This Slice

Once this slice is complete, the core runtime shape matches the revised research-system design much more closely.

At that point the most logical next slice becomes either:

1. `item_crafted` as the first explicit new Phase 2 trigger family
2. a concrete value-based authored scenario, if value-backed progression should be proven before trigger expansion

Neither of those should be started before this core authority cutover is complete if the priority is to finish the core first.
