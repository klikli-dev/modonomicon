<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research Command Expansion

Date: 2026-06-03
Branch: feat/research-system/main

## Summary

Expand `/modonomicon research` to support reset/unlock/set for individual research items (node, fact, value), books, and graphs. Clean up dead stub commands. Set all research commands to moderator permission level.

## Command Tree

```
/modonomicon
  research
    reset                                    — reset ALL research for a player
    reset book <book_id>                     — reset research for a specific book
    reset graph <graph_id>                   — reset research for a specific graph
    grant fact <fact_id>                     — grant a fact
    revoke fact <fact_id>                    — revoke a fact
    unlock node <node_id>                    — force-unlock a node
    lock node <node_id>                      — re-lock a node
    set value <value_id> <amount>            — set a value to an absolute number
  reload                                     — reload book data on client
```

All research commands require `LEVEL_MODERATOR` (permission level 2).

## Design Decisions

### Book-scoped reset

At command time, iterate the book's entries and extract node IDs from their conditions:

1. Look up the book from `BookDataManager`
2. Iterate `book.getEntries()`
3. For each entry, check `entry.getCondition()`
4. If it is a `BookResearchNodeUnlockedCondition`, extract `nodeId`
5. Recurse into `BookAndCondition`/`BookOrCondition` children for composite conditions
6. Collect all referenced node IDs
7. For each node, look up its `NodeRule` to find required facts/values
8. Revoke those facts, reset those values, lock those nodes
9. `reevaluate()` + sync

Shared facts between books are not a concern — reset is blind.

### Graph support

Graph IDs are derived from the folder structure of research data resources at load time:

- `modonomicon:research_data/demo/facts.json` → graph `modonomicon:demo`
- `modonomicon:research_data/generated/demo/facts.json` → strip `generated/` prefix → graph `modonomicon:demo`

The `generated/` prefix is a datagen output convention (from `BookHierarchyResearchCompiler`) and is treated as reserved. At load time, `ResearchDataManager` builds a graph index mapping graph IDs to their node/fact/value IDs.

No subfolders beyond the bundle level exist in practice — each bundle is a flat folder with the standard set of files.

### Info command

Deferred — not included in this slice.

### Permission level

All research commands use `LEVEL_MODERATOR`. The existing `reset` command will be changed from `LEVEL_ALL` to `LEVEL_MODERATOR`.

## Implementation Steps

### Step 1: Delete stub commands

Delete these files (not wired into `CommandRegistry`, just return "removed" messages):

- `common/src/main/java/com/klikli_dev/modonomicon/command/ResetBookUnlocksCommand.java`
- `common/src/main/java/com/klikli_dev/modonomicon/command/SaveUnlocksCommand.java`
- `common/src/main/java/com/klikli_dev/modonomicon/command/LoadUnlocksCommand.java`

### Step 2: Add public getter to `BookResearchNodeUnlockedCondition`

Add `public Identifier nodeId()` to expose the protected `nodeId` field. Needed for book-scoped reset to extract node IDs from entry conditions.

File: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java`

### Step 3: Add mutation methods to `PlayerResearchState`

Add three new methods to `PlayerResearchState`:

- `revokeFact(Identifier factId) → boolean` — removes from `factIds`, returns true if was present
- `lockNode(Identifier nodeId) → boolean` — removes from `unlockedNodeIds`, returns true if was present
- `setValue(Identifier valueId, int amount) → int` — sets absolute value, returns new value

File: `common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java`

### Step 4: Add corresponding methods to `ResearchStateManager`

Expose the new `PlayerResearchState` methods with dirty-tracking:

- `revokeFact(ServerPlayer, Identifier) → boolean`
- `lockNode(ServerPlayer, Identifier) → boolean`
- `setValue(ServerPlayer, Identifier, int) → int`

File: `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java`

### Step 5: Build graph index in `ResearchDataManager`

At load time in `apply()`:

1. For each resource location, extract the folder path (everything before the filename)
2. Strip the `generated/` prefix if present
3. The remainder is the graph ID (namespaced)
4. Build `Map<Identifier, Set<Identifier>>` for graph→nodeIds, graph→factIds, graph→valueIds
5. Expose via `ResearchData` record (add optional graph index fields)

Files:
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java`
- `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`

### Step 6: Create suggestion providers

Create static suggestion providers (same pattern as existing `SUGGEST_BOOK`):

- `SUGGEST_FACT` — from `ResearchDataManager.get().data().factIds()`
- `SUGGEST_NODE` — from `ResearchDataManager.get().data().nodeIds()`
- `SUGGEST_VALUE` — from `ResearchDataManager.get().data().valueIds()`
- `SUGGEST_BOOK` — migrate from `ResetBookUnlocksCommand` to shared location
- `SUGGEST_GRAPH` — from graph index keys

### Step 7: Create new command classes

All in `com.klikli_dev.modonomicon.command`:

| Class | Arguments | Behavior |
|-------|-----------|----------|
| `ResetBookResearchCommand` | `<book_id>` with SUGGEST_BOOK | Iterate book entries, extract node IDs from conditions, revoke related facts/values, lock nodes, reevaluate, sync |
| `ResetGraphResearchCommand` | `<graph_id>` with SUGGEST_GRAPH | Look up graph's nodes/facts/values from index, revoke/reset/lock, reevaluate, sync |
| `GrantFactCommand` | `<fact_id>` with SUGGEST_FACT | Validate ID exists, grantFact, reevaluate, sync |
| `RevokeFactCommand` | `<fact_id>` with SUGGEST_FACT | Validate ID exists, revokeFact, reevaluate, sync |
| `UnlockNodeCommand` | `<node_id>` with SUGGEST_NODE | Validate ID exists, unlockNode (bypass rules), sync |
| `LockNodeCommand` | `<node_id>` with SUGGEST_NODE | Validate ID exists, lockNode, sync |
| `SetValueCommand` | `<value_id>` with SUGGEST_VALUE, `<amount>` int | Validate ID exists, setValue, reevaluate, sync |

Each command follows the established visual-state-snapshot pattern:

1. Snapshot book visibility before
2. Perform mutation
3. Call `reevaluate()` (for fact/value/node changes that affect unlocks)
4. Update book visual state (unread indicators)
5. Sync research state and visual state

### Step 8: Create `ResearchCommand` parent class

A new `ResearchCommand.java` that builds the `research` literal node and wires all subcommands. Simplifies `CommandRegistry`.

### Step 9: Update `CommandRegistry`

Replace inline `research` literal with `ResearchCommand.register(dispatcher)`:

```java
Commands.literal(Modonomicon.MOD_ID)
    .then(ResearchCommand.register(dispatcher))
    .then(ReloadBooksCommand.register(dispatcher))
```

### Step 10: Add I18n constants and lang entries

Add to `ModonomiconConstants.I18n.Command`:

- `ERROR_UNKNOWN_FACT`
- `ERROR_UNKNOWN_NODE`
- `ERROR_UNKNOWN_VALUE`
- `SUCCESS_GRANT_FACT`
- `SUCCESS_REVOKE_FACT`
- `SUCCESS_UNLOCK_NODE`
- `SUCCESS_LOCK_NODE`
- `SUCCESS_SET_VALUE`

Add corresponding entries to `en_us.json` (and placeholder entries for other lang files).

### Step 11: Change existing `reset` permission

Change `ResetResearchCommand` from `Commands.LEVEL_ALL` to `Commands.LEVEL_MODERATOR`.

## Files Modified

| File | Change |
|------|--------|
| `command/ResetBookUnlocksCommand.java` | DELETE |
| `command/SaveUnlocksCommand.java` | DELETE |
| `command/LoadUnlocksCommand.java` | DELETE |
| `command/ResetResearchCommand.java` | Change permission to LEVEL_MODERATOR |
| `command/ResearchCommand.java` | NEW — parent command |
| `command/ResetBookResearchCommand.java` | NEW |
| `command/ResetGraphResearchCommand.java` | NEW |
| `command/GrantFactCommand.java` | NEW |
| `command/RevokeFactCommand.java` | NEW |
| `command/UnlockNodeCommand.java` | NEW |
| `command/LockNodeCommand.java` | NEW |
| `command/SetValueCommand.java` | NEW |
| `registry/CommandRegistry.java` | Wire new commands |
| `research/state/PlayerResearchState.java` | Add revokeFact, lockNode, setValue |
| `research/state/ResearchStateManager.java` | Add corresponding methods |
| `research/data/ResearchDataManager.java` | Build graph index in apply() |
| `research/data/ResearchData.java` | Add graph index fields |
| `book/conditions/BookResearchNodeUnlockedCondition.java` | Add public nodeId() getter |
| `api/ModonomiconConstants.java` | Add I18n constants |
| `assets/modonomicon/lang/en_us.json` (+ other langs) | Add translation entries |

## Verification

- `/modonomicon research reset` — verify full reset still works
- `/modonomicon research reset book modonomicon:demo` — verify book-scoped reset removes only that book's nodes/facts/values
- `/modonomicon research reset graph modonomicon:demo` — verify graph-scoped reset matches book reset for generated research
- `/modonomicon research grant fact <id>` — verify fact is granted and reevaluate unlocks dependent nodes
- `/modonomicon research revoke fact <id>` — verify fact is revoked and dependent nodes re-lock
- `/modonomicon research unlock node <id>` — verify node is force-unlocked regardless of predicates
- `/modonomicon research lock node <id>` — verify node is re-locked
- `/modonomicon research set value <id> <n>` — verify value is set and threshold-gated nodes unlock
- Tab-completion works for all argument types
- Permission enforcement: non-moderator players cannot run research commands
