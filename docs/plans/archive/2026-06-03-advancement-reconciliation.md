# Advancement Reconciliation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** After a research reset (or on player login), replay all completed advancement-backed research hooks so that advancement-earned facts/values are restored.

**Architecture:** Add a `replayAll` method to `AdvancementResearchHookService` that iterates all advancement hook definitions, checks the player's `PlayerAdvancements` for completion, and re-grants facts/increments values. Call it (a) after the reset command and (b) on player join when advancement-backed facts are missing from the loaded state.

**Tech Stack:** Java, Minecraft server APIs (`PlayerAdvancements`, `ServerAdvancementManager`), existing mod hook infrastructure.

---

## File Changes

| # | File | Action |
|---|------|--------|
| 1 | `common/.../research/hook/AdvancementResearchHookService.java` | Modify — add `replayAll` method |
| 2 | `common/.../command/ResetResearchCommand.java` | Modify — call replay after reset |
| 3 | `neo/.../ModonomiconNeo.java` | Modify — call replay on join |
| 4 | `forge/.../ModonomiconForge.java` | Modify — call replay on join |
| 5 | `fabric/.../ModonomiconFabric.java` | Modify — call replay on join |
| 6 | `docs/spec/2026-05-28-research-system-revised-design.md` | Modify — update implementation status |

---

## Task 1: Add `replayAll` to `AdvancementResearchHookService`

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java`

- [ ] **Step 1: Add the `replayAll` method**

Add after the existing `onAdvancement` method:

```java
/**
 * Replays all advancement-backed hooks against the player's current advancement progress.
 * Used after research reset and on player login to restore advancement-backed research state.
 *
 * @return true if any research state changed
 */
public boolean replayAll(ServerPlayer player) {
    var before = BookDataManager.get().getBooks().values().stream()
            .collect(java.util.stream.Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
    boolean changed = false;
    var serverAdvancements = player.level().getServer().getAdvancements();
    var playerAdvancements = player.getAdvancements();
    for (var entry : ResearchDataManager.get().data().advancementHooks().entrySet()) {
        var advancementId = entry.getKey();
        var hooks = entry.getValue();
        var holder = serverAdvancements.get(advancementId);
        if (holder == null) {
            continue; // advancement removed from datapack, skip
        }
        var progress = playerAdvancements.getOrStartProgress(holder);
        if (!progress.isDone()) {
            continue; // advancement not yet completed, skip
        }
        for (var hook : hooks) {
            if (hook.factId() != null) {
                changed |= this.stateManager.grantFact(player, hook.factId());
            } else if (hook.valueId() != null) {
                changed |= this.stateManager.incrementValue(player, hook.valueId(), hook.increment());
            }
        }
    }
    if (changed) {
        changed |= this.stateManager.reevaluate(player);
        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }
    }
    return changed;
}
```

- [ ] **Step 2: Add `needsAdvancementReplay` helper method**

Add after `replayAll`:

```java
/**
 * Checks whether the player's research state is missing any advancement-backed facts.
 * If so, the state was likely reset and advancement hooks need to be replayed.
 */
public boolean needsAdvancementReplay(ServerPlayer player) {
    var state = this.stateManager.getStateFor(player);
    for (var hooks : ResearchDataManager.get().data().advancementHooks().values()) {
        for (var hook : hooks) {
            if (hook.factId() != null && !state.hasFact(hook.factId())) {
                return true;
            }
        }
    }
    return false;
}
```

- [ ] **Step 3: Compile check**

Run: `./gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/hook/AdvancementResearchHookService.java
git commit -m "feat(research): add advancement hook replay for post-reset recovery"
```

---

## Task 2: Call replay after research reset command

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/command/ResetResearchCommand.java`

- [ ] **Step 1: Add replay call after reset**

In `ResetResearchCommand.run()`, after `ResearchServices.state().resetFor(player)` and before the sync calls, add:

```java
// Replay advancement-backed hooks so advancement-earned research is restored immediately.
ResearchServices.advancements().replayAll(player);
```

The full `run` method becomes:

```java
@Override
public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    var player = context.getSource().getPlayer();
    var before = BookDataManager.get().getBooks().values().stream().collect(java.util.stream.Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
    ResearchServices.state().resetFor(player);
    // Replay advancement-backed hooks so advancement-earned research is restored immediately.
    ResearchServices.advancements().replayAll(player);
    for (var book : BookDataManager.get().getBooks().values()) {
        BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
    }
    ResearchServices.state().syncFor(player);
    BookVisualStateManager.get().syncFor(player);
    context.getSource().sendSuccess(() -> Component.translatable(Command.SUCCESS_RESET_RESEARCH), true);
    return 1;
}
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/command/ResetResearchCommand.java
git commit -m "feat(research): replay advancement hooks after reset command"
```

---

## Task 3: Call replay on player join (NeoForge)

**Files:**
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`

- [ ] **Step 1: Add replay in `EntityJoinLevelEvent` handler**

After the existing `ResearchStateManager.get().onDatapackSync(player)` call, add the replay:

```java
NeoForge.EVENT_BUS.addListener((EntityJoinLevelEvent e) -> {
    if (e.getEntity() instanceof ServerPlayer player) {
        BookVisualStateManager.get().syncFor(player);
        ResearchStateManager.get().onDatapackSync(player);
        // Replay advancement-backed hooks if research state is stale (e.g. reset while offline).
        if (ResearchServices.advancements().needsAdvancementReplay(player)) {
            ResearchServices.advancements().replayAll(player);
            ResearchStateManager.get().syncFor(player);
            BookVisualStateManager.get().syncFor(player);
        }
    }
});
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat :neo:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java
git commit -m "feat(research): replay advancement hooks on player join (NeoForge)"
```

---

## Task 4: Call replay on player join (Forge)

**Files:**
- Modify: `forge/src/main/java/com/klikli_dev/modonomicon/ModonomiconForge.java`

- [ ] **Step 1: Add replay in `EntityJoinLevelEvent` handler**

Same pattern as NeoForge:

```java
EntityJoinLevelEvent.BUS.addListener((EntityJoinLevelEvent e) -> {
    if (e.getEntity() instanceof ServerPlayer player) {
        BookVisualStateManager.get().syncFor(player);
        ResearchStateManager.get().onDatapackSync(player);
        // Replay advancement-backed hooks if research state is stale (e.g. reset while offline).
        if (ResearchServices.advancements().needsAdvancementReplay(player)) {
            ResearchServices.advancements().replayAll(player);
            ResearchStateManager.get().syncFor(player);
            BookVisualStateManager.get().syncFor(player);
        }
    }
});
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat :forge:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add forge/src/main/java/com/klikli_dev/modonomicon/ModonomiconForge.java
git commit -m "feat(research): replay advancement hooks on player join (Forge)"
```

---

## Task 5: Call replay on player join (Fabric)

**Files:**
- Modify: `fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java`

- [ ] **Step 1: Add replay in `ServerPlayConnectionEvents.JOIN` handler**

```java
ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
    BookVisualStateManager.get().syncFor(handler.getPlayer());
    ResearchStateManager.get().onDatapackSync(handler.getPlayer());
    // Replay advancement-backed hooks if research state is stale (e.g. reset while offline).
    if (handler.getPlayer() instanceof ServerPlayer player) {
        if (ResearchServices.advancements().needsAdvancementReplay(player)) {
            ResearchServices.advancements().replayAll(player);
            ResearchStateManager.get().syncFor(player);
            BookVisualStateManager.get().syncFor(player);
        }
    }
});
```

- [ ] **Step 2: Add missing import**

Add to imports:
```java
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.server.level.ServerPlayer;
```

- [ ] **Step 3: Compile check**

Run: `./gradlew.bat :fabric:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add fabric/src/main/java/com/klikli_dev/modonomicon/ModonomiconFabric.java
git commit -m "feat(research): replay advancement hooks on player join (Fabric)"
```

---

## Task 6: Update spec implementation status

**Files:**
- Modify: `docs/spec/2026-05-28-research-system-revised-design.md`

- [ ] **Step 1: Move advancement reconciliation from "Not completed" to "Completed"**

In the "Not completed yet" section, remove:
```
- advancement reconciliation on reset/login (replay already-completed advancements)
```

In the "Completed so far" section, add after the `item_acquired` demo scenario line:
```
- advancement reconciliation: replay completed advancement hooks after reset and on player login
```

- [ ] **Step 2: Commit**

```bash
git add docs/spec/2026-05-28-research-system-revised-design.md
git commit -m "docs(spec): mark advancement reconciliation as completed"
```

---

## Manual Verification

1. Run `./gradlew.bat runData` — datagen succeeds
2. Run client, earn an advancement that has a research hook (e.g. mine stone) — research node unlocks
3. Run `/modonomicon research reset` — research clears, then immediately re-grants the advancement-backed fact and re-unlocks the node
4. Disconnect and reconnect — research state persists (replay runs but detects no missing facts, so no-op)
5. (Manual save edit or command) Clear research state while offline, reconnect — advancement-backed research is restored on join
