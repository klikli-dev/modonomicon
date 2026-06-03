# Item Acquired Trigger — Comprehensive Inventory Change Detection

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the current item-pickup-only "item acquired" research trigger with a comprehensive solution that detects ALL ways items enter a player's inventory (pickup, trading, commands, loot, crafting output, etc.), matching vanilla's `InventoryChangeTrigger` coverage.

**Architecture:** Add a common mixin into `AbstractContainerMenu.triggerSlotListeners` that intercepts every slot change in every container menu. When a slot in a player's inventory gains items (empty→non-empty or same item type with increased count), fire the `onItemAcquired` research hook. Remove the now-redundant item-pickup-only hooks from Fabric (`MixinItemEntity`) and NeoForge (`ItemEntityPickupEvent.Post`).

**Tech Stack:** Mixin (common module), NeoForge events, Fabric API

---

## Context: How Vanilla `InventoryChangeTrigger` Works

The vanilla `InventoryChangeTrigger` (used for the `inventory_changed` advancement trigger) fires through this chain:

1. `AbstractContainerMenu.broadcastChanges()` — called every tick from `ServerPlayer.tick()`, and explicitly from commands, `ServerPlayer.take()`, etc.
2. For each changed slot, calls `triggerSlotListeners(i, current, currentCopy)` (private method, line ~228 of `AbstractContainerMenu`)
3. `triggerSlotListeners` compares `lastSlots.get(i)` (old) vs `current` (new) — **both values are available here**
4. If different, calls `containerListener.slotChanged(this, i, newItem)` on all registered `ContainerListener`s
5. `ServerPlayer` registers its private `containerListener` (anonymous class) on every container via `initMenu()` (line 557)
6. The `slotChanged` implementation fires `CriteriaTriggers.INVENTORY_CHANGED.trigger(ServerPlayer.this, ServerPlayer.this.getInventory(), changedItem)`

**Key fields for our mixin:**
- `AbstractContainerMenu.lastSlots` — `private NonNullList<ItemStack>` — previous slot states
- `AbstractContainerMenu.slots` — `public final NonNullList<Slot>` — slot definitions
- `Slot.container` — `public final Container` — the backing container
- `Inventory.player` — `public final Player` — the owning player

---

## File Map

| File | Action | Purpose |
|------|--------|---------|
| `common/src/main/java/com/klikli_dev/modonomicon/mixin/MixinAbstractContainerMenu.java` | **CREATE** | Common mixin intercepting `triggerSlotListeners` |
| `common/src/main/resources/modonomicon.mixins.json` | **MODIFY** | Register the new common mixin |
| `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinItemEntity.java` | **DELETE** | Remove redundant item-pickup-only hook |
| `fabric/src/main/resources/modonomicon.fabric.mixins.json` | **MODIFY** | Remove `MixinItemEntity` from mixin list |
| `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java` | **MODIFY** | Remove `ItemEntityPickupEvent.Post` listener |

---

## Task 1: Create Common Mixin for `AbstractContainerMenu.triggerSlotListeners`

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/mixin/MixinAbstractContainerMenu.java`

- [ ] **Step 1: Create the mixin class**

```java
/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu {

    @Shadow
    @Final
    private NonNullList<ItemStack> lastSlots;

    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Inject(
        method = "triggerSlotListeners",
        at = @At("HEAD")
    )
    private void modonomicon$onSlotChanged(int i, ItemStack current, Supplier<ItemStack> currentCopy, CallbackInfo ci) {
        ItemStack oldItem = this.lastSlots.get(i);
        if (ItemStack.matches(oldItem, current)) {
            return;
        }

        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        Slot slot = self.slots.get(i);

        if (slot.container instanceof Inventory inventory && inventory.player instanceof ServerPlayer player) {
            if (modonomicon$isAcquisition(oldItem, current)) {
                ItemStack acquired = currentCopy.get();
                if (ResearchServices.hooks().onItemAcquired(player, acquired)) {
                    ResearchStateManager.get().syncFor(player);
                    BookVisualStateManager.get().syncFor(player);
                }
            }
        }
    }

    private static boolean modonomicon$isAcquisition(ItemStack oldItem, ItemStack newItem) {
        if (oldItem.isEmpty() && !newItem.isEmpty()) {
            return true;
        }
        if (!oldItem.isEmpty() && !newItem.isEmpty()
                && ItemStack.isSameItem(oldItem, newItem)
                && newItem.getCount() > oldItem.getCount()) {
            return true;
        }
        return false;
    }
}
```

- [ ] **Step 2: Verify the mixin compiles**

Run: `./gradlew.bat :common:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/mixin/MixinAbstractContainerMenu.java
git commit -m "feat(research): add common mixin for comprehensive item acquired detection

Hook into AbstractContainerMenu.triggerSlotListeners to detect ALL ways items
enter a player's inventory, not just item entity pickup. This matches vanilla
InventoryChangeTrigger coverage (pickup, trading, commands, loot, etc.)."
```

---

## Task 2: Register the Common Mixin

**Files:**
- Modify: `common/src/main/resources/modonomicon.mixins.json`

- [ ] **Step 1: Add `MixinAbstractContainerMenu` to the common mixin config**

Update the file to include the new mixin:

```json
{
  "required": true,
  "minVersion": "0.8",
  "package": "com.klikli_dev.modonomicon.mixin",
  "refmap": "${mod_id}.refmap.json",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
    "MixinAbstractContainerMenu"
  ],
  "client": [
  ],
  "server": [
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

- [ ] **Step 2: Verify the mixin loads correctly on Fabric**

Run: `./gradlew.bat :fabric:runClient`
Expected: Game starts without mixin errors

- [ ] **Step 3: Verify the mixin loads correctly on NeoForge**

Run: `./gradlew.bat :neo:runClient`
Expected: Game starts without mixin errors

- [ ] **Step 4: Commit**

```bash
git add common/src/main/resources/modonomicon.mixins.json
git commit -m "feat(research): register common MixinAbstractContainerMenu in mixin config"
```

---

## Task 3: Remove Redundant Fabric Item Pickup Hook

**Files:**
- Delete: `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinItemEntity.java`
- Modify: `fabric/src/main/resources/modonomicon.fabric.mixins.json`

- [ ] **Step 1: Delete MixinItemEntity.java**

Remove the file from the repository.

- [ ] **Step 2: Remove `MixinItemEntity` from Fabric mixin config**

Remove `"MixinItemEntity"` from the `"mixins"` array in `fabric/src/main/resources/modonomicon.fabric.mixins.json`:

```json
{
  "required": true,
  "minVersion": "0.8",
  "package": "com.klikli_dev.modonomicon.mixin",
  "refmap": "${mod_id}.refmap.json",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
    "MixinPlayerAdvancements",
    "MixinResultSlot"
  ],
  "client": [
    "MixinClientPacketListener",
    "MixinGameRenderer",
    "MixinModelManager"
  ],
  "server": [
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

- [ ] **Step 3: Verify Fabric builds and runs**

Run: `./gradlew.bat :fabric:runClient`
Expected: Game starts without errors

- [ ] **Step 4: Commit**

```bash
git rm fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinItemEntity.java
git add fabric/src/main/resources/modonomicon.fabric.mixins.json
git commit -m "feat(research): remove redundant Fabric MixinItemEntity item pickup hook

The new common MixinAbstractContainerMenu handles all inventory changes including
item pickup via AbstractContainerMenu.broadcastChanges."
```

---

## Task 4: Remove Redundant NeoForge Item Pickup Event

**Files:**
- Modify: `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`

- [ ] **Step 1: Remove the `ItemEntityPickupEvent.Post` listener**

Remove the following block from `ModonomiconNeo.java`:

```java
//Item acquired event handling for research progression
NeoForge.EVENT_BUS.addListener((ItemEntityPickupEvent.Post e) -> {
    if(!(e.getPlayer() instanceof ServerPlayer player))
        return;

    var originalStack = e.getOriginalStack();
    if (!originalStack.isEmpty()) {
        if (ResearchServices.hooks().onItemAcquired(player, originalStack)) {
            ResearchStateManager.get().syncFor(player);
            BookVisualStateManager.get().syncFor(player);
        }
    }
});
```

Also remove the now-unused import:
```java
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
```

- [ ] **Step 2: Verify NeoForge builds and runs**

Run: `./gradlew.bat :neo:runClient`
Expected: Game starts without errors

- [ ] **Step 3: Commit**

```bash
git add neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java
git commit -m "feat(research): remove redundant NeoForge ItemEntityPickupEvent listener

The new common MixinAbstractContainerMenu handles all inventory changes including
item pickup via AbstractContainerMenu.broadcastChanges."
```

---

## Task 5: End-to-End Testing

- [ ] **Step 1: Test item pickup from ground**

1. Run client
2. Use the demo research that has an `onItemAcquired` trigger for cobblestone
3. Drop cobblestone on the ground and pick it up
4. Verify research progression fires

- [ ] **Step 2: Test `/give` command**

1. In-game, run `/give @s minecraft:diamond 1`
2. Verify research progression fires for diamond acquisition

- [ ] **Step 3: Test trading with villager**

1. Set up a villager that trades items you have a research hook for
2. Complete a trade
3. Verify research progression fires

- [ ] **Step 4: Test crafting output (if applicable)**

1. Craft an item that has a research hook
2. Verify research progression fires

- [ ] **Step 5: Verify no double-firing**

1. Pick up an item from the ground
2. Verify the hook fires exactly once (not twice from old+new hooks)

- [ ] **Step 6: Run data generators to verify no breakage**

Run: `./gradlew.bat :common:runData`
Expected: Datagen completes without errors

---

## Edge Cases & Notes

1. **False positives from internal inventory moves**: Moving an item from slot A to slot B within the player's inventory will trigger a false "acquisition" for slot B. This is acceptable because:
   - The hook only fires for registered items, so random inventory shuffling won't trigger research
   - This matches vanilla `INVENTORY_CHANGED` trigger behavior
   - The performance overhead is negligible

2. **Timing**: `broadcastChanges` is called every tick from `ServerPlayer.tick()`. Most inventory changes (commands, trading) call it explicitly immediately. Item pickup calls it from `ServerPlayer.take()`. The maximum latency is ~1 tick.

3. **Interaction with `onItemCrafted`**: The new mixin will also fire for crafting output (since crafting output goes into the player's inventory). The separate `onItemCrafted` hook remains available for crafting-specific logic. There's no conflict — both hooks fire independently.

4. **Mixin compatibility**: The `triggerSlotListeners` method is `private` in `AbstractContainerMenu`. Mixin can inject into private methods. The method signature is stable across Minecraft versions (it's part of the container menu system).
