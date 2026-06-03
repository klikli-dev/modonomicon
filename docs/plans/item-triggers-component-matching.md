# Item Triggers with Data Component Matching

## Problem

Item crafted and item acquired triggers currently match only on item identifier (e.g., `minecraft:stick`). They should support matching on data components (e.g., enchantments, custom name) so mod authors can trigger research based on specific item variants.

## Design Decisions

- **Partial component matching**: Only components specified in the template are checked; unspecified components are ignored.
- **Count ignored**: Matching is item type + components only; count is irrelevant.
- **`match_components` flag**: Per-hook boolean (default `false`). When `false`, only item type is checked. When `true`, specified components are also checked.
- **Lookup strategy**: Group hooks by item ID for O(1) map lookup, then check components on matched hooks if `match_components` is true.

## Changes

### 1. `ResearchHookDefinition` - add fields for component matching

**File**: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchHookDefinition.java`

Add two fields to the record:
- `@Nullable ItemStackTemplate targetItem` - the full item stack template with components (null means match by item ID only)
- `boolean matchComponents` - whether to check components when `targetItem` is set

Update the `CODEC`:
- `event_target_id` stays as `Identifier` (item ID, used for grouping/lookup)
- Add optional `event_target_item` field using `ItemStackTemplate.CODEC` (nullable)
- Add optional `match_components` field (boolean, defaults to false)

### 2. `ResearchDataManager` - update lookup methods

**File**: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java`

Change lookup method signatures:
- `itemCraftedHooksFor(Identifier itemId)` -> `itemCraftedHooksFor(ItemStack itemStack)`
- `itemAcquiredHooksFor(Identifier itemId)` -> `itemAcquiredHooksFor(ItemStack itemStack)`

The new lookup logic:
1. Extract item ID from the ItemStack
2. Look up hooks by item ID from the existing map (fast path)
3. For each hook, if `matchComponents` is true and `targetItem` is not null, check if the event's ItemStack components are a superset of the template's components (partial matching)
4. Return only matching hooks

Add a private helper `matchesComponents(ItemStack stack, ItemStackTemplate template)`.

### 3. `ResearchData` - update grouping in `validate()`

**File**: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`

In the `validate` method where hooks are grouped by `triggerTargetId` (lines 119-124), update to handle the case where `targetItem` is set: use `targetItem.item()` to extract the item ID for grouping when it's more appropriate.

### 4. `ResearchHookService` - accept `ItemStack` instead of `Identifier`

**File**: `common/src/main/java/com/klikli_dev/modonomicon/research/hook/ResearchHookService.java`

Change method signatures:
- `onItemCrafted(ServerPlayer player, Identifier itemId)` -> `onItemCrafted(ServerPlayer player, ItemStack itemStack)`
- `onItemAcquired(ServerPlayer player, Identifier itemId)` -> `onItemAcquired(ServerPlayer player, ItemStack itemStack)`

Update the `Function` fields to accept `ItemStack`.

### 5. NeoForge event handlers - pass full `ItemStack`

**File**: `neo/src/main/java/com/klikli_dev/modonomicon/ModonomiconNeo.java`

**ItemCraftedEvent** (line 148): Pass `e.getCrafting()` directly.
**ItemEntityPickupEvent.Post** (line 162): Pass `e.getOriginalStack()` directly.

### 6. Fabric mixins - pass full `ItemStack`

**File**: `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinResultSlot.java`
Pass `carried` (the ItemStack) directly.

**File**: `fabric/src/main/java/com/klikli_dev/modonomicon/mixin/MixinItemEntity.java`
Pass `itemStack` (the full stack with correct count) directly.

### 7. HookSpec classes - accept `ItemStackTemplate`

**File**: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ItemCraftedHookSpec.java`
**File**: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ItemAcquiredHookSpec.java`

Add `@Nullable ItemStackTemplate targetItem` and `boolean matchComponents` fields.
Update `toDefinition()` to pass these to `ResearchHookDefinition`.

### 8. `ResearchDataBuilder` - add overloads for component-aware hooks

**File**: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java`

Add new methods:
- `grantFactOnItemCrafted(String path, ItemStackTemplate targetItem, boolean matchComponents, ResearchFactRef factRef)`
- `incrementValueOnItemCrafted(String path, ItemStackTemplate targetItem, boolean matchComponents, ResearchValueRef valueRef, int increment)`
- Same for `itemAcquired`

### 9. `ResearchIngressHelper` - add overloads for component-aware triggers

**File**: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchIngressHelper.java`

Add new entry points:
- `onItemCrafted(ItemStackTemplate template, boolean matchComponents)`
- `onItemAcquired(ItemStackTemplate template, boolean matchComponents)`

### 10. Demo datagen - update usage

**File**: `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java`

Update the item crafted/acquired demo hooks to demonstrate component matching (optional).

## Files to Modify

| # | File | Change |
|---|------|--------|
| 1 | `ResearchHookDefinition.java` | Add `targetItem` + `matchComponents` fields, update CODEC |
| 2 | `ResearchDataManager.java` | Change lookup methods to accept `ItemStack`, add component matching logic |
| 3 | `ResearchData.java` | Update `validate()` grouping if needed |
| 4 | `ResearchHookService.java` | Change method signatures to accept `ItemStack` |
| 5 | `ModonomiconNeo.java` | Pass full `ItemStack` in event handlers |
| 6 | `MixinResultSlot.java` | Pass full `ItemStack` |
| 7 | `MixinItemEntity.java` | Pass full `ItemStack` |
| 8 | `ItemCraftedHookSpec.java` | Add `targetItem` + `matchComponents` fields |
| 9 | `ItemAcquiredHookSpec.java` | Add `targetItem` + `matchComponents` fields |
| 10 | `ResearchDataBuilder.java` | Add overloads for component-aware hooks |
| 11 | `ResearchIngressHelper.java` | Add overloads for component-aware triggers |
| 12 | `DemoResearch.java` | Update demo usage (optional) |

## Component Matching Algorithm

```
matchesComponents(ItemStack stack, ItemStackTemplate template):
    for each (type, value) in template.components():
        if stack.get(type) != value:
            return false
    return true
```

This is partial matching: only components present in the template are checked. Components not in the template are ignored.
