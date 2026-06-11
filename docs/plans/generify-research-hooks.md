# Plan: Generify the Research Hook System

## Problem Statement

The research hook system has 4 built-in trigger types (`entry_viewed_once`, `item_crafted`, `item_acquired`, `advancement`), but:

1. **Advancement is a special snowflake** — it has its own definition record (`AdvancementResearchHookDefinition`), its own service class (`AdvancementResearchHookService`), its own JSON file (`advancement_hooks.json`), and its own datagen spec (`AdvancementHookSpec`). All other hooks share `ResearchHookDefinition` but are still hardcoded throughout.

2. **`ResearchData` has per-type maps** — `entryViewedOnceHooks`, `itemCraftedHooks`, `itemAcquiredHooks`, `advancementHooks` are all separate record fields. The `validate()` method hardcodes filtering by `TriggerType` constant using `==` identity comparison.

3. **`ResearchHookService` has hardcoded methods** — `onEntryViewedOnce()`, `onItemCrafted()`, `onItemAcquired()` are copy-pasted with identical logic differing only in lookup function.

4. **No extension point** — a modder cannot add a new trigger type (e.g. "entity killed", "biome entered") without modifying modonomicon itself across 6+ files.

## Design Goals

1. **Unify the data model** — all hook types share one definition record, one JSON file, one validation path.
2. **Make `TriggerType` the extension point** — third-party mods register new trigger types the same way they register page types or condition types.
3. **Fold advancement into the unified model** — no separate definition, service, or JSON file.
4. **Keep the convenience API working** — existing `grantFactOnItemCrafted(...)` etc. methods stay as sugar.
5. **Follow existing registry patterns** — match `BookPageTypeRegistry` / `BookConditionTypeRegistry` conventions exactly.

## Decisions

- **No backward compatibility** — clean break, no reading old `advancement_hooks.json`
- **Keep convenience sugar** — existing `grantFactOnItemCrafted(...)` etc. stay; add a generic `on(triggerType, targetId)` ingress for third-party types
- **Keep both `canProgress` methods** — specific `canProgressEntryViewedOnce()` delegates to generic `canProgress(player, type, targetId)`
- **Keep individual hook specs** — type safety and discoverability win over fewer classes
- **Generify replay** — `TriggerHandler.replayAll()` is the extension point; only trigger types that need it implement it

---

## Phase 1: Unify the Data Model

### 1a. Delete `AdvancementResearchHookDefinition.java`

Advancement hooks become `ResearchHookDefinition` with `triggerType = ADVANCEMENT`, `triggerTargetId = advancementId`. The existing `targetItem` / `matchComponents` fields are simply unused (same as they're unused for `entry_viewed_once` today).

**Files:**
- **Delete:** `common/src/main/java/com/klikli_dev/modonomicon/research/data/AdvancementResearchHookDefinition.java`
- **Update:** `AdvancementHookSpec.toDefinition()` → return `ResearchHookDefinition` with `triggerType = ADVANCEMENT`

### 1b. Unify `ResearchData` hook storage

**File:** `ResearchData.java`

Replace:
```java
Map<Identifier, List<ResearchHookDefinition>> entryViewedOnceHooks,
Map<Identifier, List<ResearchHookDefinition>> itemCraftedHooks,
Map<Identifier, List<ResearchHookDefinition>> itemAcquiredHooks,
Map<Identifier, List<AdvancementResearchHookDefinition>> advancementHooks,
```

With:
```java
Map<TriggerType, Map<Identifier, List<ResearchHookDefinition>>> hooksByTypeAndTarget,
```

Add accessor:
```java
public List<ResearchHookDefinition> hooksFor(TriggerType type, Identifier targetId) {
    return hooksByTypeAndTarget.getOrDefault(type, Map.of())
        .getOrDefault(targetId, List.of());
}
```

Update `validate()` — replace the 4 hardcoded filter+groupBy blocks with one generic loop:
```java
var grouped = new HashMap<TriggerType, Map<Identifier, List<ResearchHookDefinition>>>();
for (var hook : allHooks) {
    grouped.computeIfAbsent(hook.triggerType(), k -> new HashMap<>())
        .computeIfAbsent(hook.triggerTargetId(), k -> new ArrayList<>())
        .add(hook);
}
```

Validation of `factId`/`valueId` is unified too — one loop over all hooks instead of two.

### 1c. Update `ResearchDataManager` loading

**File:** `ResearchDataManager.java`

- Remove separate `advancement_hooks` parsing and the `advancementHooks` list
- Remove `entryViewedOnceHooksFor()`, `itemCraftedHooksFor()`, `itemAcquiredHooksFor()`
- Add generic: `public List<ResearchHookDefinition> hooksFor(TriggerType type, Identifier targetId)` — delegates to `data.hooksFor(type, targetId)`
- For item hooks with component matching: the lookup function registered in `TriggerTypeRegistry` handles filtering (see Phase 2)

### 1d. Update `ResearchProvider`

**File:** `ResearchProvider.java`

- Remove `AdvancementResearchHookDefinition` import and separate `advancement_hooks.json` write
- Merge all hook definitions (including advancement) into one `hooks.json` write:
```java
var allHooks = new ArrayList<ResearchHookDefinition>();
allHooks.addAll(data.hookDefinitions());
futures.add(this.save(cache, ResearchHookDefinition.CODEC, allHooks, base.resolve("hooks.json")));
```

### 1e. Update `ResearchDataBuilder`

**File:** `ResearchDataBuilder.java`

- Remove separate `advancementHooks` list
- Add a unified `List<ResearchHookSpec> hooks` list (or keep individual lists and merge in `hookDefinitions()`)
- `hookDefinitions()` now returns ALL hooks including advancement
- Remove `advancementHookDefinitions()` (or deprecate)
- Keep all existing convenience methods (`grantFactOnAdvancementEarned` etc.) — they add to the unified list

---

## Phase 2: Make `TriggerType` the Extension Point

### 2a. Create `TriggerHandler` interface

**New file:** `common/src/main/java/com/klikli_dev/modonomicon/research/hook/TriggerHandler.java`

```java
public interface TriggerHandler {
    /**
     * Resolve hooks matching a specific event target.
     * Called by the hook service when a concrete event fires.
     */
    List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier targetId);

    /**
     * Enumerate ALL hooks of this trigger type and fire those whose
     * target condition is met for the given player. Used for replay
     * on login / research reset.
     *
     * The default returns false (no-op) — trigger types that don't
     * need replay get this for free.
     */
    default boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        return false;
    }
}
```

### 2b. Expand `TriggerTypeRegistry`

**File:** `TriggerTypeRegistry.java`

Add a handler map:
```java
private static final Map<TriggerType, TriggerHandler> HANDLERS = new HashMap<>();

public static TriggerType register(Identifier id, MapCodec<?> codec,
        StreamCodec<RegistryFriendlyByteBuf, ?> streamCodec) {
    return register(id, codec, streamCodec, null);
}

public static TriggerType register(Identifier id, MapCodec<?> codec,
        StreamCodec<RegistryFriendlyByteBuf, ?> streamCodec,
        @Nullable TriggerHandler handler) {
    var type = TYPES.register(id, new TriggerType(id, codec, streamCodec));
    if (handler != null) {
        HANDLERS.put(type, handler);
    }
    return type;
}

public static TriggerHandler handler(TriggerType type) {
    return HANDLERS.getOrDefault(type, (player, targetId) -> List.of());
}
```

Register built-in types with handlers:
```java
public static final TriggerType ENTRY_VIEWED_ONCE = register(
    Modonomicon.loc("entry_viewed_once"), ..., new EntryViewedOnceTriggerHandler());

public static final TriggerType ITEM_CRAFTED = register(
    Modonomicon.loc("item_crafted"), ..., new ItemCraftedTriggerHandler());

public static final TriggerType ITEM_ACQUIRED = register(
    Modonomicon.loc("item_acquired"), ..., new ItemAcquiredTriggerHandler());

public static final TriggerType ADVANCEMENT = register(
    Modonomicon.loc("advancement"), ..., new AdvancementTriggerHandler());
```

### 2c. Create built-in trigger handler implementations

**New files** (one per trigger type, small focused classes):

**`handlers/EntryViewedOnceTriggerHandler.java`:**
```java
public class EntryViewedOnceTriggerHandler implements TriggerHandler {
    @Override
    public List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier entryId) {
        return ResearchDataManager.get().hooksFor(TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId);
    }
    // replayAll: no-op (default) — no meaningful replay for entry views
}
```

**`handlers/ItemCraftedTriggerHandler.java`:**
```java
public class ItemCraftedTriggerHandler implements TriggerHandler {
    @Override
    public List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier itemId) {
        return ResearchDataManager.get().hooksFor(TriggerTypeRegistry.ITEM_CRAFTED, itemId)
            .stream()
            .filter(h -> !h.matchComponents() || h.targetItem() == null
                || matchesComponents(player, h))
            .toList();
    }
    // replayAll: no-op — crafting is a one-shot event
}
```

**`handlers/ItemAcquiredTriggerHandler.java`:**
```java
// Same pattern as ItemCraftedTriggerHandler
```

**`handlers/AdvancementTriggerHandler.java`:**
```java
public class AdvancementTriggerHandler implements TriggerHandler {
    @Override
    public List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier advancementId) {
        return ResearchDataManager.get().hooksFor(TriggerTypeRegistry.ADVANCEMENT, advancementId);
    }

    @Override
    public boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        boolean changed = false;
        var serverAdvancements = player.level().getServer().getAdvancements();
        var playerAdvancements = player.getAdvancements();
        for (var hook : ResearchDataManager.get().hooksForType(TriggerTypeRegistry.ADVANCEMENT)) {
            var holder = serverAdvancements.get(hook.triggerTargetId());
            if (holder == null) continue;
            var progress = playerAdvancements.getOrStartProgress(holder);
            if (!progress.isDone()) continue;
            if (hook.factId() != null) {
                changed |= stateManager.grantFact(player, hook.factId());
            } else if (hook.valueId() != null) {
                changed |= stateManager.incrementValue(player, hook.valueId(), hook.increment());
            }
        }
        return changed;
    }
}
```

---

## Phase 3: Unify the Hook Service

### 3a. Rewrite `ResearchHookService`

**File:** `ResearchHookService.java`

Remove the three hardcoded lookup functions and methods. Replace with generic dispatch:

```java
public class ResearchHookService {
    private final ResearchStateManager stateManager;

    public ResearchHookService(ResearchStateManager stateManager) {
        this.stateManager = stateManager;
    }

    // --- Generic API ---

    /** Fire hooks for a specific event. */
    public boolean fire(ServerPlayer player, TriggerType triggerType, Identifier targetId) {
        var handler = TriggerTypeRegistry.handler(triggerType);
        return applyHooks(player, handler.resolve(player, targetId));
    }

    /** Replay all hooks of a trigger type (for login / reset). */
    public boolean replayAll(ServerPlayer player, TriggerType triggerType) {
        var handler = TriggerTypeRegistry.handler(triggerType);
        return handler.replayAll(player, this.stateManager);
    }

    /** Check if any hook of this type can still progress for this target. */
    public boolean canProgress(ServerPlayer player, TriggerType triggerType, Identifier targetId) {
        var handler = TriggerTypeRegistry.handler(triggerType);
        var hooks = handler.resolve(player, targetId);
        var state = this.stateManager.getStateFor(player);
        for (var hook : hooks) {
            if (hook.factId() != null && !state.hasFact(hook.factId())) {
                return true;
            }
        }
        return false;
    }

    // --- Convenience sugar (delegate to generic) ---

    public boolean onEntryViewedOnce(ServerPlayer player, Identifier entryId) {
        return fire(player, TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId);
    }

    public boolean onItemCrafted(ServerPlayer player, ItemStack itemStack) {
        var itemId = itemStack.getItem().builtInRegistryHolder().unwrapKey()
            .map(ResourceKey::identifier).orElse(null);
        if (itemId == null) return false;
        return fire(player, TriggerTypeRegistry.ITEM_CRAFTED, itemId);
    }

    public boolean onItemAcquired(ServerPlayer player, ItemStack itemStack) {
        var itemId = itemStack.getItem().builtInRegistryHolder().unwrapKey()
            .map(ResourceKey::identifier).orElse(null);
        if (itemId == null) return false;
        return fire(player, TriggerTypeRegistry.ITEM_ACQUIRED, itemId);
    }

    public boolean canProgressEntryViewedOnce(Player player, Identifier entryId) {
        return canProgress(player, TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId);
    }

    // --- Shared apply logic (extracted from current copy-pasted methods) ---

    private boolean applyHooks(ServerPlayer player, List<ResearchHookDefinition> hooks) {
        var before = BookDataManager.get().getBooks().values().stream()
            .collect(Collectors.toMap(Book::getId, b -> BookVisibilitySnapshots.collect(player, b)));
        ResearchStateManager.beginToastCollection();
        boolean changed = false;
        for (var hook : hooks) {
            if (hook.factId() != null) {
                changed |= this.stateManager.grantFact(player, hook.factId());
            } else if (hook.valueId() != null) {
                changed |= this.stateManager.incrementValue(player, hook.valueId(), hook.increment());
            }
        }
        changed |= this.stateManager.reevaluate(player);
        var triggers = ResearchStateManager.endToastCollection();
        if (!triggers.isEmpty()) {
            Services.NETWORK.sendTo(player, new ResearchToastMessage(triggers));
        }
        if (changed) {
            for (var book : BookDataManager.get().getBooks().values()) {
                BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book,
                    before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
            }
        }
        return changed;
    }
}
```

### 3b. Delete `AdvancementResearchHookService.java`

### 3c. Update `ResearchServices.java`

```java
public final class ResearchServices {
    private static final ResearchStateManager STATE = ResearchStateManager.get();
    private static final ResearchHookService HOOKS = new ResearchHookService(STATE);

    public static ResearchStateManager state() { return STATE; }
    public static ResearchHookService hooks() { return HOOKS; }
    // advancements() removed
}
```

---

## Phase 4: Update Callers

### 4a. Platform event handlers

| File | Change |
|------|--------|
| `ModonomiconNeo.java` | `AdvancementEvent` handler: `ResearchServices.hooks().fire(player, TriggerTypeRegistry.ADVANCEMENT, advancementId)` |
| `ModonomiconNeo.java` | Player join replay: `ResearchServices.hooks().replayAll(player, TriggerTypeRegistry.ADVANCEMENT)` |
| `ModonomiconForge.java` | Same changes as NeoForge |
| `ModonomiconFabric.java` | Same changes |
| `MixinPlayerAdvancements.java` | Same changes |

### 4b. Networking / mixins

No changes needed — `BookEntryReadMessage`, `ClickResearchProgressButtonMessage`, `MixinAbstractContainerMenu`, `MixinResultSlot` all call `ResearchServices.hooks().onEntryViewedOnce(...)` / `onItemCrafted(...)` / `onItemAcquired(...)` which are preserved as convenience methods.

---

## Phase 5: Update Datagen Layer

### 5a. Keep individual hook specs

All four specs (`EntryViewedOnceHookSpec`, `ItemCraftedHookSpec`, `ItemAcquiredHookSpec`, `AdvancementHookSpec`) stay. Only change:

- `AdvancementHookSpec.toDefinition()` now returns `ResearchHookDefinition` (unified) instead of `AdvancementResearchHookDefinition`:
```java
public ResearchHookDefinition toDefinition() {
    return new ResearchHookDefinition(
        this.id,
        TriggerTypeRegistry.ADVANCEMENT,
        this.advancementId,
        this.factRef != null ? this.factRef.id() : null,
        this.valueRef != null ? this.valueRef.id() : null,
        this.increment,
        null,  // no targetItem
        false  // no matchComponents
    );
}
```

### 5b. Add generic ingress to `ResearchIngressHelper`

**File:** `ResearchIngressHelper.java`

Add a generic ingress for third-party trigger types:

```java
public GenericIngress on(TriggerType triggerType, Identifier targetId) {
    return new GenericIngress(this.research, triggerType, targetId);
}

public static final class GenericIngress {
    private final ResearchDataBuilder research;
    private final TriggerType triggerType;
    private final Identifier targetId;

    // constructor...

    public ResearchFactRef declareFact(String factPath) {
        var factRef = this.research.fact(factPath);
        this.grantFact(factPath + "_hook", factRef);
        return factRef;
    }

    public void grantFact(String hookPath, ResearchFactRef factRef) {
        this.research.grantFact(hookPath, this.triggerType, this.targetId, factRef);
    }

    public ResearchValueRef declareValue(String valuePath, int increment) {
        var valueRef = this.research.value(valuePath);
        this.incrementValue(valuePath + "_hook", valueRef, increment);
        return valueRef;
    }

    public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment) {
        this.research.incrementValue(hookPath, this.triggerType, this.targetId, valueRef, increment);
    }
}
```

This requires adding generic `grantFact()` / `incrementValue()` methods to `ResearchDataBuilder`:
```java
public void grantFact(String hookPath, TriggerType triggerType, Identifier targetId, ResearchFactRef factRef) {
    this.hooks.add(new ResearchHookSpec(hookPath, triggerType, targetId, factRef, null, 1, null, false));
}
```

The existing convenience methods (`grantFactOnItemCrafted(...)` etc.) stay as sugar and delegate to the same internal list.

---

## Phase 6: Documentation

All documentation changes are in `J:\Projects\Minecraft\modonomicon-docs\versioned_docs\version-26.1.2`.

### 6a. New page: `advanced/custom-hooks.md`

Create a new doc page following the same structure and style as `custom-conditions.md`. Sidebar position 45 (after custom-conditions at 40).

**Full content outline:**

```markdown
---
sidebar_position: 45
---

# Custom Research Hooks

Mods can add custom research hook trigger types that fire from arbitrary game events
and translate them into research progression (fact grants or value increments).

To this end you need to:

1. Create a `ResourceLocation` ID for the new trigger type (e.g. `mymod:entity_killed`)
2. Implement `TriggerHandler` to resolve hooks and optionally support replay
3. Register the trigger type via `TriggerTypeRegistry.register()`
4. Bootstrap from your mod initializer
5. Wire your platform event to `ResearchServices.hooks().fire()`
6. Author research hooks via datagen using the generic ingress API

:::tip
The trigger type ID is used in the `hooks.json` research data file as the
`trigger_type` field. It is NOT the same as individual hook IDs.
:::

## Built-in Trigger Types

Modonomicon ships with four trigger types:

| Trigger Type ID | Event | Target ID | Notes |
|----------------|-------|-----------|-------|
| `modonomicon:entry_viewed_once` | A book entry is viewed for the first time | Book entry `ResourceLocation` | |
| `modonomicon:item_crafted` | A specific item is crafted | Item `ResourceLocation` | Supports component matching |
| `modonomicon:item_acquired` | A specific item appears in inventory | Item `ResourceLocation` | Supports component matching |
| `modonomicon:advancement` | A vanilla advancement is earned | Advancement `ResourceLocation` | Supports replay on login |

## TriggerHandler Interface

Implement `TriggerHandler` to define how your trigger type resolves hooks and
optionally supports replay:

[Code example showing the interface with resolve() and replayAll()]

### resolve()

Called when a concrete event fires. Return all hooks whose target condition is met.
For simple trigger types this is a direct map lookup via `ResearchDataManager.get().hooksFor(type, targetId)`.

### replayAll() (optional)

Called on player login and after research reset to restore research state from
past events. The default implementation returns false (no-op).

Only implement this if your trigger type represents a persistent state that can
change outside of the live event stream (e.g. advancements, which can be completed
at any time and need to be re-checked on login).

[Code example: simple trigger (no replay) vs. stateful trigger (with replay)]

## Registration

Create a dedicated registry class. Use `public static final` fields initialized
via `TriggerTypeRegistry.register(...)`, and provide an empty `bootstrap()` method:

[Code example of registry class, matching the custom-conditions.md pattern]

Then call `bootstrap()` during mod initialization:

[Code example for NeoForge and Fabric, matching custom-conditions.md]

## Wiring Platform Events

Use `ResearchServices.hooks().fire(player, triggerType, targetId)` from your
platform event handler:

[Code example: NeoForge event listener]
[Code example: Fabric event]
[Code example: Mixin]

After firing, sync research state and book visuals if the result is true:

[Code showing the sync pattern]

## Datagen: Authoring Hooks

Use the generic ingress API in your `ResearchSubProvider`:

[Code example using this.ingress().on(triggerType, targetId).declareFact(...)]

### Convenience methods

For built-in trigger types, convenience sugar is available:

[Code examples for onEntryViewedOnce, onItemCrafted, onItemAcquired, onAdvancementEarned]

## JSON Format

All hooks are stored in a single `hooks.json` file per research bundle:

[JSON example showing a hook with trigger_type field]

## Best Practices

- **Keep trigger handler logic minimal.** The handler should only resolve hooks
  and optionally replay. Complex event filtering belongs in your platform event code.
- **Use replay only when needed.** If your trigger represents a one-shot event
  (like crafting), the default no-op replay is correct.
- **Use the generic ingress for custom types.** The convenience sugar is for
  built-in types only. Third-party types use `this.ingress().on(triggerType, targetId)`.
- **Sync after every fire.** Always check the return value of `fire()` and sync
  both `ResearchStateManager` and `BookVisualStateManager` when true.

See also [Research Conditions](../basics/research/conditions) for how to gate
entries behind research nodes.
```

### 6b. Update `advanced/advanced.md`

Add entry for the new page:

```markdown
- **[Custom Research Hooks](./custom-hooks)** — Register your own research hook trigger types
```

### 6c. Update `basics/research/research.md`

Add to the "Further reading" section:

```markdown
- [Custom Research Hooks](../../advanced/custom-hooks) — registering new trigger types for third-party mods
```

### 6d. Update `basics/research/datagen.md`

Add a section documenting the generic ingress API alongside the existing convenience methods:

```markdown
## Generic Ingress

For custom trigger types, use the generic ingress entry point:

\`\`\`java
this.ingress().on(myTriggerType, targetId)
    .declareFact("mymod/my_hook");
\`\`\`

See [Custom Research Hooks](../../advanced/custom-hooks) for details.
```

### 6e. Update `basics/research/scenarios.md`

The existing advancement scenario already uses convenience sugar which stays.
Add a note at the bottom pointing to custom hooks:

```markdown
## Custom trigger types

Need a trigger type not covered above (e.g. entity killed, biome entered)?
See [Custom Research Hooks](../../advanced/custom-hooks).
```

---

## File Change Summary

| Action | File |
|--------|------|
| **Delete** | `AdvancementResearchHookDefinition.java` |
| **Delete** | `AdvancementResearchHookService.java` |
| **New** | `TriggerHandler.java` |
| **New** | `handlers/EntryViewedOnceTriggerHandler.java` |
| **New** | `handlers/ItemCraftedTriggerHandler.java` |
| **New** | `handlers/ItemAcquiredTriggerHandler.java` |
| **New** | `handlers/AdvancementTriggerHandler.java` |
| **New** | `advanced/custom-hooks.md` (docs) |
| **Edit** | `advanced/advanced.md` (docs) — add custom-hooks entry |
| **Edit** | `basics/research/research.md` (docs) — add further reading link |
| **Edit** | `basics/research/datagen.md` (docs) — add generic ingress section |
| **Edit** | `basics/research/scenarios.md` (docs) — add custom hooks note |
| **Major edit** | `ResearchData.java` — unified hook map |
| **Major edit** | `ResearchHookService.java` — generic dispatch |
| **Major edit** | `TriggerTypeRegistry.java` — handler registration |
| **Major edit** | `ResearchDataManager.java` — unified loading |
| **Major edit** | `ResearchProvider.java` — single hooks.json |
| **Major edit** | `ResearchDataBuilder.java` — unified hook list |
| **Edit** | `ResearchServices.java` — remove advancements() |
| **Edit** | `AdvancementHookSpec.java` — toDefinition() returns unified type |
| **Edit** | `ResearchIngressHelper.java` — add generic on() |
| **Edit** | `ModonomiconNeo.java` — use hooks().fire() |
| **Edit** | `ModonomiconForge.java` — use hooks().fire() |
| **Edit** | `ModonomiconFabric.java` — use hooks().fire() |
| **Edit** | `MixinPlayerAdvancements.java` — use hooks().fire() |
