# Plan: Item Crafted & Item Acquired Triggers (Phase 2)

## Goal
Add two new trigger types: `item_crafted` (manual crafting only, tracked by output item ID) and `item_acquired` (inventory change, tracked by item ID). Prove both with a narrow demo scenario.

## Backwards Compat
None. This adds new trigger types; no existing hooks use them yet.

---

## Design Decisions
- `item_crafted` tracks the output item ID (e.g. `minecraft:stick`), not the recipe ID
- `item_crafted` fires only for manual crafting (player crafting table / 2x2 grid), not machine crafting
- `item_acquired` fires on any inventory change (broadest interpretation)
- Trigger target is the item's registry ID for both triggers
- NeoForge/Forge: use loader events (`PlayerEvent.ItemCraftedEvent`, `PlayerEvent.ItemAcquiredEvent`)
- Fabric: mixin into `ServerPlayer` at the same injection points where Minecraft fires `RECIPE_CRAFTED` and `INVENTORY_CHANGED` advancement triggers
- Follow the `entry_viewed_once` pattern: use `ResearchHookDefinition` with trigger type dispatch, grouped by trigger target ID
- Datagen follows `entry_viewed_once` pattern: new hook specs, ingress helper inner classes, builder methods

---

## File Changes

### 1. `common/.../registry/TriggerTypeRegistry.java` — **MODIFY**

Register two new trigger types:

```java
public static final TriggerType ITEM_CRAFTED = register(
        Modonomicon.loc("item_crafted"),
        Identifier.CODEC.fieldOf(""),
        Identifier.STREAM_CODEC.cast()
);

public static final TriggerType ITEM_ACQUIRED = register(
        Modonomicon.loc("item_acquired"),
        Identifier.CODEC.fieldOf(""),
        Identifier.STREAM_CODEC.cast()
);
```

---

### 2. `common/.../research/data/ResearchData.java` — **MODIFY**

Add two new fields to the record:

```java
Map<Identifier, List<ResearchHookDefinition>> itemCraftedHooks,
Map<Identifier, List<ResearchHookDefinition>> itemAcquiredHooks
```

In `validate()`, after existing hook validation:
- Filter `hooks` where `triggerType == TriggerTypeRegistry.ITEM_CRAFTED`, group by `triggerTargetId` → `itemCraftedHooks`
- Filter `hooks` where `triggerType == TriggerTypeRegistry.ITEM_ACQUIRED`, group by `triggerTargetId` → `itemAcquiredHooks`
- Pass both maps to the `ResearchData` constructor

Update Javadoc to document the new fields.

---

### 3. `common/.../research/data/ResearchDataManager.java` — **MODIFY**

Add two lookup methods:

```java
public List<ResearchHookDefinition> itemCraftedHooksFor(Identifier itemId) {
    return this.data.itemCraftedHooks().getOrDefault(itemId, List.of());
}

public List<ResearchHookDefinition> itemAcquiredHooksFor(Identifier itemId) {
    return this.data.itemAcquiredHooks().getOrDefault(itemId, List.of());
}
```

---

### 4. `common/.../research/hook/ResearchHookService.java` — **MODIFY**

Add two new fields for lookup functions:

```java
private final Function<Identifier, List<ResearchHookDefinition>> itemCraftedLookup;
private final Function<Identifier, List<ResearchHookDefinition>> itemAcquiredLookup;
```

Update constructors to wire `ResearchDataManager.get()::itemCraftedHooksFor` and `ResearchDataManager.get()::itemAcquiredHooksFor`.

Add two handler methods following the `onEntryViewedOnce` pattern:

```java
public boolean onItemCrafted(ServerPlayer player, Identifier itemId) {
    var before = BookDataManager.get().getBooks().values().stream()
            .collect(Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
    boolean changed = false;
    for (var hook : this.itemCraftedLookup.apply(itemId)) {
        if (hook.factId() != null) {
            changed |= this.stateManager.grantFact(player, hook.factId());
        } else if (hook.valueId() != null) {
            changed |= this.stateManager.incrementValue(player, hook.valueId(), hook.increment());
        }
    }
    changed |= this.stateManager.reevaluate(player);
    if (changed) {
        for (var book : BookDataManager.get().getBooks().values()) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
        }
    }
    return changed;
}

public boolean onItemAcquired(ServerPlayer player, Identifier itemId) {
    // Same pattern as onItemCrafted, using itemAcquiredLookup
}
```

---

### 5. `neo/.../ModonomiconNeo.java` — **MODIFY**

Add event listeners after the advancement event listener:

```java
NeoForge.EVENT_BUS.addListener((PlayerEvent.ItemCraftedEvent e) -> {
    ServerPlayer player = (ServerPlayer) e.getEntity();
    Identifier itemId = e.getRecipe().getResultItem(null).getItem().builtInRegistryHolder().key().location();
    if (ResearchServices.hooks().onItemCrafted(player, itemId)) {
        ResearchStateManager.get().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
    }
});

NeoForge.EVENT_BUS.addListener((PlayerEvent.ItemAcquiredEvent e) -> {
    ServerPlayer player = (ServerPlayer) e.getEntity();
    Identifier itemId = e.getItem().getItem().builtInRegistryHolder().key().location();
    if (ResearchServices.hooks().onItemAcquired(player, itemId)) {
        ResearchStateManager.get().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
    }
});
```

Add imports for `PlayerEvent.ItemCraftedEvent` and `PlayerEvent.ItemAcquiredEvent`.

---

### 6. `forge/.../ModonomiconForge.java` — **MODIFY**

Same event wiring as NeoForge. Forge provides equivalent events at `net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent` and `ItemAcquiredEvent`.

---

### 7. `fabric/.../mixin/MixinServerPlayer.java` — **NEW**

Mixin into `ServerPlayer` to intercept crafting and inventory changes.

Find the method that calls `CriteriaTriggers.RECIPE_CRAFTED.trigger(...)` (line ~1530) and inject at TAIL:

```java
@Inject(at = @At("TAIL"), method = "<crafting method signature>")
private void onCraftRecipe(<params>, CallbackInfoReturnable<?> info) {
    // Extract result item ID from the crafting result
    // Call ResearchServices.hooks().onItemCrafted(...)
}
```

Find the method that calls `CriteriaTriggers.INVENTORY_CHANGED.trigger(...)` (line ~342) and inject at TAIL:

```java
@Inject(at = @At("TAIL"), method = "<inventory change method signature>")
private void onInventoryChanged(<params>, CallbackInfoReturnable<?> info) {
    // Extract changed item ID
    // Call ResearchServices.hooks().onItemAcquired(...)
}
```

Register mixin in `fabric/src/main/resources/modonomicon.fabric.mixins.json`.

---

### 8. `common/.../api/datagen/research/ItemCraftedHookSpec.java` — **NEW**

Authoring-time hook spec, mirrors `EntryViewedOnceHookSpec`:

```java
public record ItemCraftedHookSpec(
        Identifier id,
        Identifier itemId,
        ResearchFactRef factRef,
        ResearchValueRef valueRef,
        int increment
) {
    public static ItemCraftedHookSpec grantFact(Identifier id, Identifier itemId, ResearchFactRef factRef) {
        return new ItemCraftedHookSpec(id, itemId, factRef, null, 1);
    }

    public static ItemCraftedHookSpec incrementValue(Identifier id, Identifier itemId, ResearchValueRef valueRef, int increment) {
        return new ItemCraftedHookSpec(id, itemId, null, valueRef, increment);
    }

    public ResearchHookDefinition toDefinition() {
        return new ResearchHookDefinition(
                this.id,
                TriggerTypeRegistry.ITEM_CRAFTED,
                this.itemId,
                this.factRef != null ? this.factRef.id() : null,
                this.valueRef != null ? this.valueRef.id() : null,
                this.increment
        );
    }
}
```

---

### 9. `common/.../api/datagen/research/ItemAcquiredHookSpec.java` — **NEW**

Same structure as `ItemCraftedHookSpec`, using `TriggerTypeRegistry.ITEM_ACQUIRED`.

---

### 10. `common/.../api/datagen/research/ResearchDataBuilder.java` — **MODIFY**

Add fields:

```java
private final List<ItemCraftedHookSpec> itemCraftedHooks = new ArrayList<>();
private final List<ItemAcquiredHookSpec> itemAcquiredHooks = new ArrayList<>();
```

Add builder methods:

```java
public void grantFactOnItemCrafted(String path, Identifier itemId, ResearchFactRef factRef) {
    this.itemCraftedHooks.add(ItemCraftedHookSpec.grantFact(
            Identifier.fromNamespaceAndPath(this.namespace, path), itemId, factRef));
}

public void incrementValueOnItemCrafted(String path, Identifier itemId, ResearchValueRef valueRef, int increment) {
    this.itemCraftedHooks.add(ItemCraftedHookSpec.incrementValue(
            Identifier.fromNamespaceAndPath(this.namespace, path), itemId, valueRef, increment));
}

public void grantFactOnItemAcquired(String path, Identifier itemId, ResearchFactRef factRef) {
    this.itemAcquiredHooks.add(ItemAcquiredHookSpec.grantFact(
            Identifier.fromNamespaceAndPath(this.namespace, path), itemId, factRef));
}

public void incrementValueOnItemAcquired(String path, Identifier itemId, ResearchValueRef valueRef, int increment) {
    this.itemAcquiredHooks.add(ItemAcquiredHookSpec.incrementValue(
            Identifier.fromNamespaceAndPath(this.namespace, path), itemId, valueRef, increment));
}
```

Add getters:

```java
public List<ResearchHookDefinition> itemCraftedHookDefinitions() {
    return this.itemCraftedHooks.stream().map(ItemCraftedHookSpec::toDefinition).toList();
}

public List<ResearchHookDefinition> itemAcquiredHookDefinitions() {
    return this.itemAcquiredHooks.stream().map(ItemAcquiredHookSpec::toDefinition).toList();
}
```

---

### 11. `common/.../api/datagen/research/ResearchIngressHelper.java` — **MODIFY**

Add entry points:

```java
public ItemCraftedIngress onItemCrafted(Identifier itemId) {
    return new ItemCraftedIngress(this.research, itemId);
}

public ItemAcquiredIngress onItemAcquired(Identifier itemId) {
    return new ItemAcquiredIngress(this.research, itemId);
}
```

Add inner class `ItemCraftedIngress`:

```java
public static final class ItemCraftedIngress {
    private final ResearchDataBuilder research;
    private final Identifier itemId;

    public ItemCraftedIngress(ResearchDataBuilder research, Identifier itemId) {
        this.research = research;
        this.itemId = itemId;
    }

    public ResearchFactRef declareFact(String factPath) {
        var factRef = this.research.fact(factPath);
        this.grantFact(factPath + "_hook", factRef);
        return factRef;
    }

    public void grantFact(String hookPath, ResearchFactRef factRef) {
        this.research.grantFactOnItemCrafted(hookPath, this.itemId, factRef);
    }

    public ResearchValueRef declareValue(String valuePath, int increment) {
        var valueRef = this.research.value(valuePath);
        this.incrementValue(valuePath + "_hook", valueRef, increment);
        return valueRef;
    }

    public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment) {
        this.research.incrementValueOnItemCrafted(hookPath, this.itemId, valueRef, increment);
    }
}
```

Add inner class `ItemAcquiredIngress` — same structure, delegating to `grantFactOnItemAcquired` / `incrementValueOnItemAcquired`.

---

### 12. `common/.../api/datagen/research/ResearchProvider.java` — **MODIFY**

Add item crafted and item acquired hooks to the save list in `run()`. These hooks go into the same `hooks.json` file (trigger type dispatch handles routing at load time):

```java
// Merge all hook types into a single list for hooks.json
var allHooks = new ArrayList<ResearchHookDefinition>();
allHooks.addAll(data.hookDefinitions());       // entry_viewed_once
allHooks.addAll(data.itemCraftedHookDefinitions());
allHooks.addAll(data.itemAcquiredHookDefinitions());
futures.add(this.save(cache, ResearchHookDefinition.CODEC, allHooks, base.resolve("hooks.json")));
```

---

### 13. `common/.../api/datagen/research/ResearchBundle.java` — **MODIFY**

Add two fields to the bundle record:

```java
List<ResearchHookDefinition> itemCraftedHooks,
List<ResearchHookDefinition> itemAcquiredHooks
```

Update constructor and usages.

---

### 14. `common/.../datagen/research/DemoResearch.java` — **MODIFY**

Add node refs:

```java
public static final ResearchNodeRef CRAFTING_STICK = node("demo/crafting_stick");
public static final ResearchNodeRef ACQUIRE_COBBLESTONE = node("demo/acquire_cobblestone");
```

In `generateResearch()`:

```java
var stickCrafted = this.ingress()
        .onItemCrafted(this.mcLoc("stick"))
        .declareFact("demo/stick_crafted");
this.node(CRAFTING_STICK, stickCrafted);

var cobbleAcquired = this.ingress()
        .onItemAcquired(this.mcLoc("cobblestone"))
        .declareFact("demo/cobblestone_acquired");
this.node(ACQUIRE_COBBLESTONE, cobbleAcquired);
```

---

### 15. `common/.../datagen/book/demo/CraftingCategory.java` — **NEW**

```java
public class CraftingCategory extends CategoryProvider {
    public static final String ID = "crafting";

    public CraftingCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void configureLayout(CategoryLayout layout) {
        layout.entry(CraftingIntroEntry.ID).at(0, 0);
        layout.entry(CraftingStickEntry.ID).at(1, 0);
    }

    @Override
    protected void generateEntries() {
        var introEntry = this.add(new CraftingIntroEntry(this).generate());
        this.add(new CraftingStickEntry(this).generate()
                .withCondition(DemoResearch.CRAFTING_STICK))
                .withParent(introEntry);
    }

    @Override
    protected String categoryName() {
        return "Crafting Demo";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.CRAFTING_TABLE);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
```

---

### 16. `common/.../datagen/book/demo/crafting/CraftingIntroEntry.java` — **NEW**

```java
public class CraftingIntroEntry extends EntryProvider {
    public static final String ID = "crafting_intro";

    public CraftingIntroEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crafting");
        this.pageText("Crafting items triggers research progression. Craft a stick to unlock the next entry.");
    }

    @Override
    protected String entryName() {
        return "Crafting";
    }

    @Override
    protected String entryDescription() {
        return "Introduction to crafting progression.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CRAFTING_TABLE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
```

---

### 17. `common/.../datagen/book/demo/crafting/CraftingStickEntry.java` — **NEW**

```java
public class CraftingStickEntry extends EntryProvider {
    public static final String ID = "crafting_stick";

    public CraftingStickEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Stick Crafted");
        this.pageText("You crafted a stick! This entry unlocked because the item_crafted trigger fired for sticks.");
    }

    @Override
    protected String entryName() {
        return "Stick Crafted";
    }

    @Override
    protected String entryDescription() {
        return "Unlocked when you craft a stick.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.STICK);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
```

---

### 18. `common/.../datagen/book/demo/AcquiringCategory.java` — **NEW**

```java
public class AcquiringCategory extends CategoryProvider {
    public static final String ID = "acquiring";

    public AcquiringCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected void configureLayout(CategoryLayout layout) {
        layout.entry(AcquiringIntroEntry.ID).at(0, 0);
        layout.entry(AcquiringCobblestoneEntry.ID).at(1, 0);
    }

    @Override
    protected void generateEntries() {
        var introEntry = this.add(new AcquiringIntroEntry(this).generate());
        this.add(new AcquiringCobblestoneEntry(this).generate()
                .withCondition(DemoResearch.ACQUIRE_COBBLESTONE))
                .withParent(introEntry);
    }

    @Override
    protected String categoryName() {
        return "Acquiring Demo";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.CHEST);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
```

---

### 19. `common/.../datagen/book/demo/acquiring/AcquiringIntroEntry.java` — **NEW**

```java
public class AcquiringIntroEntry extends EntryProvider {
    public static final String ID = "acquiring_intro";

    public AcquiringIntroEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Acquiring");
        this.pageText("Acquiring items triggers research progression. Pick up a cobblestone to unlock the next entry.");
    }

    @Override
    protected String entryName() {
        return "Acquiring";
    }

    @Override
    protected String entryDescription() {
        return "Introduction to acquiring progression.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CHEST);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
```

---

### 20. `common/.../datagen/book/demo/acquiring/AcquiringCobblestoneEntry.java` — **NEW**

```java
public class AcquiringCobblestoneEntry extends EntryProvider {
    public static final String ID = "acquiring_cobblestone";

    public AcquiringCobblestoneEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Cobblestone Acquired");
        this.pageText("You acquired cobblestone! This entry unlocked because the item_acquired trigger fired.");
    }

    @Override
    protected String entryName() {
        return "Cobblestone Acquired";
    }

    @Override
    protected String entryDescription() {
        return "Unlocked when you acquire cobblestone.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.CONDITION;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.COBBLESTONE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
```

---

### 21. `common/.../datagen/book/DemoBook.java` — **MODIFY**

In `generateCategories()`, add:

```java
var craftingCategory = this.add(new CraftingCategory(this).generate());
var acquiringCategory = this.add(new AcquiringCategory(this).generate());
```

Add imports for `CraftingCategory` and `AcquiringCategory`.

---

### 22. `fabric/src/main/resources/modonomicon.fabric.mixins.json` — **MODIFY**

Add `MixinServerPlayer` to the mixin list.

---

## Demo Scenario

| Trigger | Target Item | Research Effect | Book Effect |
|---------|-------------|-----------------|-------------|
| `item_crafted` | `minecraft:stick` | grant fact `demo/stick_crafted` → unlock node `demo/crafting_stick` | `crafting/crafting_stick` entry visible |
| `item_acquired` | `minecraft:cobblestone` | grant fact `demo/cobblestone_acquired` → unlock node `demo/acquire_cobblestone` | `acquiring/acquiring_cobblestone` entry visible |

## Category Layout

### Crafting (`crafting`)
```
[crafting_intro] → [crafting_stick]
     (0,0)              (1,0)
```

### Acquiring (`acquiring`)
```
[acquiring_intro] → [acquiring_cobblestone]
     (0,0)                 (1,0)
```

## Validation Rules
(Extended from existing `ResearchData.validate()` rules)
1. Item crafted/acquired hooks reference known facts or values (already covered by existing hook validation)
2. Each hook has exactly one of `factId` or `valueId` non-null (already exists)
3. Hook increment amount > 0 (already exists)

No new validation needed — existing rules apply to all hook types.

## Summary: ~22 file changes (14 modifications + 8 new files)

| # | File | Action |
|---|------|--------|
| 1 | `TriggerTypeRegistry.java` | Modify — register 2 new trigger types |
| 2 | `ResearchData.java` | Modify — add 2 new hook maps + grouping logic |
| 3 | `ResearchDataManager.java` | Modify — add 2 lookup methods |
| 4 | `ResearchHookService.java` | Modify — add 2 handler methods + lookup fields |
| 5 | `ModonomiconNeo.java` | Modify — wire 2 NeoForge events |
| 6 | `ModonomiconForge.java` | Modify — wire 2 Forge events |
| 7 | `MixinServerPlayer.java` (fabric) | New — 2 mixin injections |
| 8 | `ItemCraftedHookSpec.java` | New — datagen spec |
| 9 | `ItemAcquiredHookSpec.java` | New — datagen spec |
| 10 | `ResearchDataBuilder.java` | Modify — 4 methods + 2 lists + 2 getters |
| 11 | `ResearchIngressHelper.java` | Modify — 2 entry points + 2 inner classes |
| 12 | `ResearchProvider.java` | Modify — merge hooks into hooks.json |
| 13 | `ResearchBundle.java` | Modify — add 2 fields |
| 14 | `DemoResearch.java` | Modify — add 2 nodes + 2 ingress hooks |
| 15 | `CraftingCategory.java` | New — category provider |
| 16 | `CraftingIntroEntry.java` | New — entry provider |
| 17 | `CraftingStickEntry.java` | New — entry provider |
| 18 | `AcquiringCategory.java` | New — category provider |
| 19 | `AcquiringIntroEntry.java` | New — entry provider |
| 20 | `AcquiringCobblestoneEntry.java` | New — entry provider |
| 21 | `DemoBook.java` | Modify — register 2 new categories |
| 22 | `modonomicon.fabric.mixins.json` | Modify — add MixinServerPlayer |

## Manual Verification
1. Run `./gradlew.bat runData` — datagen produces valid hook JSON with `trigger_type: "modonomicon:item_crafted"` and `modonomicon:item_acquired`
2. Run client, craft a stick in crafting table — `crafting_stick` entry becomes visible
3. Run client, pick up cobblestone from ground — `acquiring_cobblestone` entry becomes visible
4. Run `/modonomicon research reset` — both gated entries become hidden again
5. Craft stick again — entry reappears
6. No regressions in existing `entry_viewed_once` or advancement hooks
