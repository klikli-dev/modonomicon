<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research Progress Button Design

Date: 2026-05-30
Branch: `feat/research-system/slice-5`

## Summary

This change repurposes the existing book-side `ReadAllButton` into a research-progress action.

The button will no longer mutate visual unread state or historical read state. Instead, it will replay `entry_viewed_once` research hooks in bulk so authored progression can advance without manually opening each entry.

The two existing interaction modes remain:

- normal click applies only to currently visible entries
- shift-click applies to all entries in the current book

Unread markers remain untouched by the button itself.

## Goals

- Remove the remaining conceptual coupling between the button and visual unread/read state.
- Keep a two-mode bulk action in the same UI slot.
- Make the button advance research only through `entry_viewed_once` semantics.
- Preserve downstream visual unread markers unless research/visibility changes independently cause newly visible content to become unread.
- Rename the UI types, variables, and strings so they match the new purpose.

## Non-Goals

- Do not add a separate visual-state reset button in this slice.
- Do not make the button mutate historical read state.
- Do not add new research trigger families beyond replaying the existing `entry_viewed_once` hook.
- Do not redesign the broader book side-button layout.

## Problem Statement

After the research visibility cutover, the old `ReadAllButton` semantics no longer fit the architecture.

Its original job was tied to book-side read/unread mutation:

- normal click marked unlocked entries as read
- shift-click marked all entries as read

That no longer matches the desired model:

- progression should be research-owned
- unread markers should remain downstream visual state
- bulk progression should operate by replaying research hook ingress, not by mutating visual interaction state

Keeping the old naming and button behavior would continue to blur research progression and visual state.

## User-Facing Behavior

### Normal Click

Normal click will:

- inspect the current book
- collect entries that are currently visible to the player through `BookServices.visibility()`
- replay `entry_viewed_once` research progression for those entries only

It will not:

- clear unread markers
- mark entries as historically read
- mark categories as read

### Shift-Click

Shift-click will:

- replay `entry_viewed_once` research progression for all entries in the current book, including currently hidden ones

It will still not touch visual unread/read state directly.

### Visual State Rules

The button itself must not directly change:

- `readEntries`
- `readCategories`
- entry unread flags
- category unread flags

However, if replayed research hooks unlock new research and therefore make additional book content visible, the existing downstream visibility-delta flow may still mark newly visible content unread. That remains correct because it is a consequence of research changing, not a direct side effect of the button.

## Architecture

### UI Layer

The current `ReadAllButton` type will be renamed to reflect research intent.

The new button should expose two research-oriented availability states:

- at least one currently visible entry can be progressed
- at least one entry anywhere in the book can be progressed

The parent screens should stop computing `hasUnreadUnlockedEntries`-style state for this button. Instead they should compute research-progress state using current visibility and the chosen mode semantics.

### Networking

The current `ClickReadAllButtonMessage` should be renamed and repurposed rather than continuing to imply visual read-state mutation.

The payload still only needs:

- current book id
- mode flag distinguishing visible-only vs all-book behavior

The server handler should:

1. collect a before-visibility snapshot for the book
2. choose the target entry set based on mode
3. replay `ResearchServices.hooks().onEntryViewedOnce(...)` for each target entry
4. if research changed, apply the existing visibility-delta unread/timestamp update flow
5. sync research state and visual state if needed

The handler should not call book interaction read APIs.

### Research Semantics

This button is not a new research primitive. It is only a bulk replay of the already-authored `entry_viewed_once` ingress path.

That means:

- entries with no `entry_viewed_once` hooks do nothing
- entries with hooks behave exactly as if they had been viewed individually for research purposes
- the action remains compatible with future research refactors because it stays inside the research service boundary

## Naming Changes

The rename should remove “read all” terminology from:

- button class name
- button variable names in parent screens
- packet/message class name
- tooltip/title text
- sprite/semantic helper names where practical and local to this button flow

Suggested naming direction:

- `ReadAllButton` → `ResearchProgressButton`
- `ClickReadAllButtonMessage` → `ClickResearchProgressButtonMessage`
- `hasUnreadUnlockedEntries`-style inputs → research-progress-oriented names

Exact field names can follow local code style, but they must describe research scope rather than unread/read mutation.

## Tooltip And Text Behavior

The button text and tooltips should describe research progression, not read-state cleanup.

Expected semantics:

- normal tooltip: progress currently visible entries
- shift tooltip: progress all entries in the current book
- inactive tooltip: no progress action available

The wording should avoid implying that entries will be marked read or that unread markers will clear.

## Screen-Level Impact

The button remains in the same place on:

- `BookParentNodeScreen`
- `BookParentIndexScreen`

No larger layout redesign is needed.

The screens only need to provide the renamed button with the correct research-availability suppliers and callback reset behavior for local button state.

## Error Handling

- If the book cannot be resolved, do nothing.
- If an entry no longer exists, skip it.
- If replaying hooks produces no research changes, do not perform unnecessary syncs beyond what the button state requires.
- Do not silently mutate visual read/unread state as a fallback.

## Validation Requirements

Implementation should verify:

- no direct read-state mutation remains in the renamed button flow
- normal click only targets currently visible entries
- shift-click targets all entries in the book
- newly unlocked content can still become unread through visibility-delta logic
- existing unread markers are otherwise left alone
- tooltips and names no longer say “read all” for this action

## Manual Verification

Manual verification for the demo book should confirm:

1. normal click progresses only currently visible conditional-entry chains
2. shift-click progresses hidden later entries as well
3. clicking the button does not clear existing unread markers by itself
4. any unread changes that do occur come only from newly visible content after research changes
5. the button tooltip and icon semantics match research progression rather than read-state cleanup

## Completion Criteria

This slice is complete when:

- the old read-all button concept is fully replaced with a research-progress concept
- the button no longer mutates visual unread/read state directly
- normal and shift modes follow the approved visible-only vs all-book semantics
- the code and UI naming reflect the new behavior
