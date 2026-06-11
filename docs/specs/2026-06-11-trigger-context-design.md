# Typed Trigger Context Design

> **For agentic workers:** This spec defines a refactoring of the research trigger
> system to use properly typed targets and runtime contexts instead of bare
> `Identifier`-only dispatch.

## Problem Statement

The current research trigger system conflates two distinct concepts into a single
`Identifier triggerTargetId` field:

1. **Authored target** — the data-driven thing stored in `hooks.json` that says
   what a hook wants to match.
2. **Runtime context** — the actual game event that fires and should be compared
   against the authored target.

This works for simple triggers (entry viewed once, advancement earned) but is
broken for item-based triggers, where the authored target is an `ItemStackTemplate`
(with optional component matching) and the runtime context is an `ItemStack`.

Current item-specific hacks to work around this:

- `ResearchHookDefinition.targetItem` — optional `ItemStackTemplate` stored
  alongside the `Identifier triggerTargetId`.
- `ResearchHookDefinition.matchComponents` — boolean flag controlling whether
  components are compared.
- `ResearchDataManager.itemHooksFor()` — orphaned helper with component-matching
  logic that is never called from any handler.
- `ItemCraftedTriggerHandler` / `ItemAcquiredTriggerHandler` — call
  `hooksFor(type, itemId)` with no component filtering at all.

Additionally:

- `TriggerType` carries `MapCodec<?>` and `StreamCodec<?>` that are never
  consulted during deserialization.
- Built-in trigger type fields in `TriggerTypeRegistry` do not pass handler
  instances to the `register()` call, so `TriggerTypeRegistry.handler()` always
  returns the no-op default.

## Design Goals

1. **Typed targets** — each trigger type defines a strongly typed authored target
   (e.g. `Identifier` for entry viewed once, `ItemStackTemplate` for item
   crafted).
2. **Typed runtime contexts** — each trigger type defines a strongly typed context
   record carrying the event data (e.g. `ItemStack` for item crafted).
3. **Handler-owned matching** — the handler decides how a target matches a context,
   eliminating the `matchComponents` flag and `targetItem` sidecar.
4. **Modder extensibility** — registering a new trigger type for vanilla or modded
   systems requires no changes to the core hook data model.
5. **Clean indexable lookup** — common built-in triggers remain efficient via
   handler-defined index keys; arbitrary triggers use a scan fallback.
6. **Backward-compatible `hooks.json`** — item triggers migrate to storing their
   `ItemStackTemplate` as the primary target (no more dual field); entry and
   advancement triggers remain `Identifier`-based.

## Spec

### 1. `TriggerContext` interface

All runtime event payloads implement this marker interface.

```java
package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.server.level.ServerPlayer;

public interface TriggerContext {
    ServerPlayer player();
}
```

Built-in context records:

```java
public record EntryViewedContext(
    ServerPlayer player,
    Identifier entryId
) implements TriggerContext {}

public record AdvancementContext(
    ServerPlayer player,
    Identifier advancementId
) implements TriggerContext {}

public record ItemCraftedContext(
    ServerPlayer player,
    ItemStack stack
) implements TriggerContext {}

public record ItemAcquiredContext(
    ServerPlayer player,
    ItemStack stack
) implements TriggerContext {}
```

Modders add their own:

```java
public record MachineRecipeCompletedContext(
    ServerPlayer player,
    Identifier machineId,
    Identifier recipeId,
    ItemStack output
) implements TriggerContext {}
```

### 2. `TriggerType<TTarget, TContext extends TriggerContext>`

Replaces the current flat record. The generic parameters bind the type to its
authored target and runtime context.

```java
public record TriggerType<TTarget, TContext extends TriggerContext>(
    Identifier id,
    MapCodec<TTarget> targetCodec,
    StreamCodec<RegistryFriendlyByteBuf, TTarget> targetStreamCodec,
    TriggerHandler<TTarget, TContext> handler
) {}
```

If a trigger type does not need network sync, use a no-op stream codec
(placeholder constant or null). If it does not need custom JSON decoding of its
target (e.g. a type that is only ever fired programmatically and never authored),
the target codec can be `null`.

### 3. `TriggerHandler<TTarget, TContext extends TriggerContext>`

Replaces the current `TriggerHandler` interface.

```java
public interface TriggerHandler<TTarget, TContext extends TriggerContext> {

    /**
     * Resolve hooks matching the given runtime context.
     * The handler compares each candidate target against the context.
     */
    List<ResearchHookDefinition<TTarget>> resolve(
        ServerPlayer player,
        TContext context
    );

    /**
     * Check whether a single target matches a given context.
     * Called by the default scan-based resolve implementation.
     */
    boolean matches(TTarget target, TContext context);

    /**
     * Return a stable index key for this target, or null if no fast-path
     * index is available.
     *
     * When both the target's indexKey and the context's indexKey are non-null
     * and equal, the handler can skip full matches for candidates with that key.
     */
    default @Nullable Object indexKey(TTarget target) {
        return null;
    }

    /**
     * Return a stable index key for this context, or null if no fast-path
     * index is available.
     */
    default @Nullable Object indexKey(TContext context) {
        return null;
    }

    /**
     * Enumerate ALL hooks of this trigger type and fire those whose target
     * condition is met for the given player. Used for replay on login /
     * research reset.
     *
     * The default returns false (no-op).
     */
    default boolean replayAll(
        ServerPlayer player,
        ResearchStateManager stateManager
    ) {
        return false;
    }
}
```

### 4. Default `resolve` implementation

To reduce boilerplate, provide a base that scans `hooksForType` and applies
`matches`. Only handlers that need custom resolution override it.

```java
// In TriggerHandler default or abstract base:
default List<ResearchHookDefinition<TTarget>> resolve(
    ServerPlayer player,
    TContext context
) {
    return ResearchDataManager.get()
        .hooksForType(/* triggerType passed implicitly or registered */)
        .stream()
        .filter(h -> matches(h.triggerTarget(), context))
        .toList();
}
```

Because `TriggerType` now stores its handler and `TriggerHandler` has no
reference back to its `TriggerType`, the hooks-for-type lookup needs a way to
find the owning type. Two options:

**Option A (recommended):** `ResearchDataManager` stores hooks per type and the
handler receives the type at resolution time via a wrapper.

**Option B:** The handler holds a back-reference to its `TriggerType`, set at
registration time.

Option B is simpler. Updated `TriggerType`:

```java
public record TriggerType<TTarget, TContext extends TriggerContext>(
    Identifier id,
    MapCodec<TTarget> targetCodec,
    StreamCodec<RegistryFriendlyByteBuf, TTarget> targetStreamCodec,
    TriggerHandler<TTarget, TContext> handler
) {
    public List<ResearchHookDefinition<TTarget>> resolveFor(
        ServerPlayer player,
        TContext context
    ) {
        return this.handler.resolve(this, player, context);
    }
}
```

And `TriggerHandler.resolve` takes the type:

```java
List<ResearchHookDefinition<TTarget>> resolve(
    TriggerType<TTarget, TContext> type,
    ServerPlayer player,
    TContext context
);
```

### 5. `ResearchHookDefinition<TTarget>`

Replaces the current flat record. No more `triggerTargetId`, `targetItem`, or
`matchComponents`.

```java
public record ResearchHookDefinition<TTarget>(
    Identifier id,
    TriggerType<TTarget, ?> triggerType,
    TTarget triggerTarget,
    Identifier factId,
    Identifier valueId,
    int increment
) {}
```

### 6. Indexing strategy

`ResearchData` currently stores:

```java
Map<TriggerType, Map<Identifier, List<ResearchHookDefinition>>>
```

This must change to support arbitrary target types.

**New structure:**

```java
Map<TriggerType<?>, List<ResearchHookDefinition<?>>>
```

One flat list per trigger type. For built-in types that support index keys,
the handler's `indexKey(target)` and `indexKey(context)` methods enable O(1)
candidate filtering. For types without index keys, the full list is scanned.

During `ResearchData.validate()`, build an optional secondary index:

```java
Map<TriggerType<?>, Map<Object, List<ResearchHookDefinition<?>>>>
```

populated from `handler.indexKey(target)` for each hook whose handler returns a
non-null key. At resolve time:

- if `handler.indexKey(context)` is non-null, look up candidates from the index
- fall back to the full list if the key is null or no index exists
- apply `matches()` to the candidate set

This keeps item-based lookups O(1) while allowing arbitrary targets to work
correctly.

### 7. Built-in trigger registrations

```java
// Entry viewed once — Identifier target, Identifier context
public static final TriggerType<Identifier, EntryViewedContext> ENTRY_VIEWED_ONCE =
    register(
        Modonomicon.loc("entry_viewed_once"),
        Identifier.CODEC.fieldOf(""),
        Identifier.STREAM_CODEC.cast(),
        new EntryViewedOnceTriggerHandler()
    );

// Item crafted — ItemStackTemplate target, ItemCraftedContext (contains ItemStack)
public static final TriggerType<ItemStackTemplate, ItemCraftedContext> ITEM_CRAFTED =
    register(
        Modonomicon.loc("item_crafted"),
        ItemStackTemplate.CODEC.fieldOf(""),
        ItemStackTemplate.STREAM_CODEC.cast(),
        new ItemCraftedTriggerHandler()
    );

// Item acquired — same shape
public static final TriggerType<ItemStackTemplate, ItemAcquiredContext> ITEM_ACQUIRED =
    register(
        Modonomicon.loc("item_acquired"),
        ItemStackTemplate.CODEC.fieldOf(""),
        ItemStackTemplate.STREAM_CODEC.cast(),
        new ItemAcquiredTriggerHandler()
    );

// Advancement — Identifier target, AdvancementContext
public static final TriggerType<Identifier, AdvancementContext> ADVANCEMENT =
    register(
        Modonomicon.loc("advancement"),
        Identifier.CODEC.fieldOf(""),
        Identifier.STREAM_CODEC.cast(),
        new AdvancementTriggerHandler()
    );
```

### 8. Handler implementations

**`EntryViewedOnceTriggerHandler`**

```java
public class EntryViewedOnceTriggerHandler
    implements TriggerHandler<Identifier, EntryViewedContext> {

    @Override
    public List<ResearchHookDefinition<Identifier>> resolve(
        TriggerType<Identifier, EntryViewedContext> type,
        ServerPlayer player,
        EntryViewedContext context
    ) {
        return ResearchDataManager.get().hooksFor(type, context.entryId());
    }

    @Override
    public boolean matches(Identifier target, EntryViewedContext context) {
        return target.equals(context.entryId());
    }

    @Override
    public @Nullable Object indexKey(Identifier target) {
        return target;
    }

    @Override
    public @Nullable Object indexKey(EntryViewedContext context) {
        return context.entryId();
    }
}
```

**`ItemCraftedTriggerHandler`**

```java
public class ItemCraftedTriggerHandler
    implements TriggerHandler<ItemStackTemplate, ItemCraftedContext> {

    @Override
    public List<ResearchHookDefinition<ItemStackTemplate>> resolve(
        TriggerType<ItemStackTemplate, ItemCraftedContext> type,
        ServerPlayer player,
        ItemCraftedContext context
    ) {
        return ResearchDataManager.get().hooksForType(type).stream()
            .filter(h -> matches(h.triggerTarget(), context))
            .toList();
    }

    @Override
    public boolean matches(ItemStackTemplate target, ItemCraftedContext context) {
        return target.matches(context.stack());
    }

    @Override
    public @Nullable Object indexKey(ItemStackTemplate target) {
        return target.item().unwrapKey().map(ResourceKey::identifier).orElse(null);
    }

    @Override
    public @Nullable Object indexKey(ItemCraftedContext context) {
        return context.stack().getItem().builtInRegistryHolder().unwrapKey()
            .map(ResourceKey::identifier).orElse(null);
    }
}
```

The `matches` call here is the critical change: `ItemStackTemplate.matches(stack)`
does the full component comparison automatically. No separate `matchComponents`
flag.

**`AdvancementTriggerHandler`**

```java
public class AdvancementTriggerHandler
    implements TriggerHandler<Identifier, AdvancementContext> {

    @Override
    public List<ResearchHookDefinition<Identifier>> resolve(
        TriggerType<Identifier, AdvancementContext> type,
        ServerPlayer player,
        AdvancementContext context
    ) {
        return ResearchDataManager.get().hooksFor(type, context.advancementId());
    }

    @Override
    public boolean matches(Identifier target, AdvancementContext context) {
        return target.equals(context.advancementId());
    }

    @Override
    public @Nullable Object indexKey(Identifier target) {
        return target;
    }

    @Override
    public @Nullable Object indexKey(AdvancementContext context) {
        return context.advancementId();
    }

    @Override
    public boolean replayAll(
        ServerPlayer player,
        ResearchStateManager stateManager
    ) {
        // Same logic as current AdvancementTriggerHandler.replayAll
        // iterates all advancement hooks, checks progress, grants facts/increments
        ...
    }
}
```

### 9. `ResearchHookService` updates

Replace `fire(player, triggerType, Identifier)` with typed dispatch:

```java
public <TTarget, TContext extends TriggerContext> boolean fire(
    TriggerType<TTarget, TContext> triggerType,
    TContext context
) {
    var handler = triggerType.handler();
    var hooks = triggerType.resolveFor(context.player(), context);
    return this.applyHooks(context.player(), hooks);
}
```

Convenience methods update to construct contexts:

```java
public boolean onEntryViewedOnce(ServerPlayer player, Identifier entryId) {
    return this.fire(TriggerTypeRegistry.ENTRY_VIEWED_ONCE,
        new EntryViewedContext(player, entryId));
}

public boolean onItemCrafted(ServerPlayer player, ItemStack stack) {
    return this.fire(TriggerTypeRegistry.ITEM_CRAFTED,
        new ItemCraftedContext(player, stack));
}

public boolean onItemAcquired(ServerPlayer player, ItemStack stack) {
    return this.fire(TriggerTypeRegistry.ITEM_ACQUIRED,
        new ItemAcquiredContext(player, stack));
}

public boolean onAdvancement(ServerPlayer player, Identifier advancementId) {
    return this.fire(TriggerTypeRegistry.ADVANCEMENT,
        new AdvancementContext(player, advancementId));
}
```

### 10. Datagen / authoring updates

**`ResearchIngressHelper`** becomes type-safe:

```java
public <TTarget> GenericIngress<TTarget> on(
    TriggerType<TTarget, ?> triggerType,
    TTarget target
) {
    return new GenericIngress<>(this.research, triggerType, target);
}
```

The type-specific helpers keep their current signatures but delegate correctly:

```java
public ItemCraftedIngress onItemCrafted(ItemStackTemplate targetItem) { ... }
```

### 11. Modder authoring example

A modder adding a "machine recipe completed" trigger:

```java
// 1. Define target and context
record MachineRecipeTarget(Identifier machineId, Identifier recipeId) {}
record MachineRecipeCompletedContext(
    ServerPlayer player,
    Identifier machineId,
    Identifier recipeId,
    ItemStack output
) implements TriggerContext {}

// 2. Define handler
class MachineRecipeHandler
    implements TriggerHandler<MachineRecipeTarget, MachineRecipeCompletedContext> {

    @Override
    public List<ResearchHookDefinition<MachineRecipeTarget>> resolve(...) { ... }

    @Override
    public boolean matches(MachineRecipeTarget target, MachineRecipeCompletedContext ctx) {
        return target.machineId().equals(ctx.machineId())
            && target.recipeId().equals(ctx.recipeId());
    }

    @Override
    public @Nullable Object indexKey(MachineRecipeTarget target) {
        return target.machineId();
    }
}

// 3. Register
public static final TriggerType<MachineRecipeTarget, MachineRecipeCompletedContext>
    MACHINE_RECIPE_COMPLETED = TriggerTypeRegistry.register(
        Identifier.fromNamespaceAndPath("mymod", "machine_recipe_completed"),
        MachineRecipeTarget.CODEC,
        MachineRecipeTarget.STREAM_CODEC,
        new MachineRecipeHandler()
    );

// 4. Wire platform events
public static void onMachineRecipeCompleted(ServerPlayer player,
    Identifier machineId, Identifier recipeId, ItemStack output) {
    ResearchServices.hooks().fire(
        MACHINE_RECIPE_COMPLETED,
        new MachineRecipeCompletedContext(player, machineId, recipeId, output)
    );
}

// 5. Author hooks in datagen
ingress().on(MACHINE_RECIPE_COMPLETED, new MachineRecipeTarget(machineId, recipeId))
    .declareFact("processed_recipes");
```

## Migration Plan

### Phase 1: Core model changes

1. Create `TriggerContext` interface and built-in context records.
2. Make `TriggerType` generic: `TriggerType<TTarget, TContext>`.
3. Make `TriggerHandler` generic: `TriggerHandler<TTarget, TContext>`.
4. Make `ResearchHookDefinition` generic: `ResearchHookDefinition<TTarget>`.
5. Remove `triggerTargetId`, `targetItem`, `matchComponents` from
   `ResearchHookDefinition`.
6. Update `TriggerTypeRegistry` to pass handlers in all built-in registrations.
7. Update all four handler implementations with typed `matches` + index keys.

### Phase 2: Data layer

1. Update `ResearchData` to store hooks as
   `Map<TriggerType<?>, List<ResearchHookDefinition<?>>>`.
2. Build optional secondary index from `handler.indexKey()`.
3. Update `ResearchData.validate()` to iterate typed hooks.
4. Update `ResearchDataManager` to work with the new data structure.
5. Update `hooks.json` codec to use `TriggerType.targetCodec` for decoding the
   target.

### Phase 3: Service layer

1. Update `ResearchHookService.fire()` to accept `TriggerContext`.
2. Update convenience methods to construct typed contexts.
3. Update `canProgress` / `canProgressByType` to work with typed targets.
4. Update `replayAll` / `replayAdvancements` to use typed contexts.

### Phase 4: Datagen layer

1. Update `ResearchHookSpec` to carry typed target instead of
   `triggerTargetId + targetItem`.
2. Update `ResearchIngressHelper` to accept typed targets.
3. Update all built-in hook spec classes.
4. Update `ResearchDataBuilder` convenience methods.

### Phase 5: Cleanup

1. Remove orphaned `ResearchDataManager.itemHooksFor()`.
2. Remove `matchComponents` from `ResearchHookSpec` and hook spec classes.
3. Update JSON format: item hooks now store `ItemStackTemplate` as the primary
   target field (with a migration path for existing `hooks.json` files if
   needed).

## Open Questions

1. **JSON format migration**: Should existing `hooks.json` files with the old
   dual-field format (`event_target_id` + `event_target_item` + `match_components`)
   be migrated automatically, or is a breaking format change acceptable?

2. **Network sync**: Do custom trigger targets need `StreamCodec` support for
   datapack sync, or is JSON-only sufficient for all built-in and expected custom
   triggers?

3. **Null targets**: `needsAdvancementReplay` currently passes `null` as targetId.
   Should `TriggerHandler.resolve` accept nullable context, or should that
   method use a separate code path (e.g. `replayAll`)?

4. **`ResearchHookDefinition` codec**: Since `TriggerType` now carries the target
   codec, the hook definition codec needs to be dynamic (dispatched on trigger
   type). Is a `Codec<ResearchHookDefinition<?>>` acceptable, or do we need
   per-type typed hooks in the codec layer too?
