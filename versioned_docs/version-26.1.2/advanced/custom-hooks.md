---
sidebar_position: 45
---

# Custom Research Hooks

Mods can add custom research hook trigger types that fire from arbitrary game events
and translate them into research progression (fact grants or value increments).

To this end you need to:

1. Create a `ResourceLocation` ID for the new trigger type (e.g. `mymod:entity_killed`)
2. Define a target type and a context type for your trigger
3. Implement `TriggerHandler` to resolve hooks, match targets, and optionally support replay
4. Register the trigger type via `TriggerTypeRegistry.register()`
5. Bootstrap from your mod initializer
6. Wire your platform event to `ResearchServices.hooks().fire()`
7. Author research hooks via datagen using the generic ingress API

:::tip
The trigger type ID is used in the `hooks.json` research data file as the
`trigger_type` field. It is NOT the same as individual hook IDs.
:::

## Built-in Trigger Types

Modonomicon ships with four trigger types:

| Trigger Type ID | Event | Target Type | Context Type | Notes |
|----------------|-------|-------------|-------------|-------|
| `modonomicon:entry_viewed_once` | A book entry is viewed for the first time | `Identifier` | `EntryViewedContext` | |
| `modonomicon:item_crafted` | A specific item is crafted | `ItemStackTemplate` | `ItemCraftedContext` | Component matching via template |
| `modonomicon:item_acquired` | A specific item appears in inventory | `ItemStackTemplate` | `ItemAcquiredContext` | Component matching via template |
| `modonomicon:advancement` | A vanilla advancement is earned | `Identifier` | `AdvancementContext` | Supports replay on login |

## TriggerHandler Interface

Implement `TriggerHandler` to define how your trigger type resolves hooks and
optionally supports replay:

```java
public interface TriggerHandler<TTarget, TContext extends TriggerContext> {
    /**
     * Resolve hooks matching a specific event context.
     */
    List<ResearchHookDefinition<TTarget>> resolve(
        TriggerType<TTarget, TContext> type,
        ServerPlayer player,
        TContext context
    );

    /**
     * Check whether a single authored target matches a given runtime context.
     */
    boolean matches(TTarget target, TContext context);

    /**
     * Return a stable index key for fast lookup, or null for full scan.
     */
    default @Nullable Object indexKey(TTarget target) { return null; }
    default @Nullable Object indexKey(TContext context) { return null; }

    /**
     * Replay all hooks of this trigger type (optional).
     * Default returns false (no-op).
     */
    default boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        return false;
    }
}
```

### resolve()

Called when a concrete event fires. The handler compares each candidate target
against the context using `matches()` and returns all hooks whose target condition
is met.

### matches()

The core matching logic. For item triggers, this calls `ItemStackTemplate.matches(stack)`
which automatically checks both the item type and any components specified in the template.

### indexKey() (optional)

Return a stable key for fast index-based lookup. When both the target's index key
and the context's index key are non-null and equal, the system skips the full scan.
For example, item triggers use the item ID as the index key.

### replayAll() (optional)

Called on player login and after research reset to restore research state from
past events. The default implementation returns false (no-op).

Only implement this if your trigger type represents a persistent state that can
change outside of the live event stream (e.g. advancements, which can be completed
at any time and need to be re-checked on login).

**Simple trigger (no replay):**

```java
public class EntityKilledTriggerHandler
    implements TriggerHandler<Identifier, EntityKilledContext> {

    @Override
    public List<ResearchHookDefinition<Identifier>> resolve(
            TriggerType<Identifier, EntityKilledContext> type,
            ServerPlayer player,
            EntityKilledContext context
    ) {
        return ResearchDataManager.get().hooksForType(type).stream()
            .filter(h -> matches(h.triggerTarget(), context))
            .toList();
    }

    @Override
    public boolean matches(Identifier target, EntityKilledContext context) {
        return target.equals(context.entityTypeId());
    }

    @Override
    public @Nullable Object indexKey(Identifier target) {
        return target;
    }

    @Override
    public @Nullable Object indexKey(EntityKilledContext context) {
        return context.entityTypeId();
    }
}
```

**Stateful trigger (with replay):**

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
    public boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        boolean changed = false;
        var serverAdvancements = player.level().getServer().getAdvancements();
        var playerAdvancements = player.getAdvancements();
        for (var hook : ResearchDataManager.get().hooksForType(TriggerTypeRegistry.ADVANCEMENT)) {
            var holder = serverAdvancements.get(hook.triggerTarget());
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

## Registration

Create a dedicated registry class. Use `public static final` fields initialized
via `TriggerTypeRegistry.register(...)`, and provide an empty `bootstrap()` method:

```java
public final class MyModTriggerRegistry {

    public static final TriggerType<Identifier, EntityKilledContext> ENTITY_KILLED =
        TriggerTypeRegistry.register(
            Identifier.fromNamespaceAndPath("mymod", "entity_killed"),
            Identifier.CODEC.fieldOf(""),
            Identifier.STREAM_CODEC.cast(),
            new EntityKilledTriggerHandler()
        );

    private MyModTriggerRegistry() {
    }

    public static void bootstrap() {
    }
}
```

Then call `bootstrap()` during mod initialization:

```java
// NeoForge
public void onCommonSetup(FMLCommonSetupEvent event) {
    MyModTriggerRegistry.bootstrap();
}

// Fabric
@Override
public void onInitialize() {
    MyModTriggerRegistry.bootstrap();
}
```

## Wiring Platform Events

Use `ResearchServices.hooks().fire(triggerType, context)` from your
platform event handler. The context carries the runtime event data.

**NeoForge:**

```java
NeoForge.EVENT_BUS.addListener((MyModEntityKillEvent e) -> {
    var player = (ServerPlayer) e.getEntity();
    var context = new EntityKilledContext(player, e.getEntityId());
    if (ResearchServices.hooks().fire(MyModTriggerRegistry.ENTITY_KILLED, context)) {
        ResearchStateManager.get().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
    }
});
```

**Fabric:**

```java
MyModEvents.ENTITY_KILLED.register((player, entityId) -> {
    if (player instanceof ServerPlayer serverPlayer) {
        var context = new EntityKilledContext(serverPlayer, entityId);
        if (ResearchServices.hooks().fire(MyModTriggerRegistry.ENTITY_KILLED, context)) {
            ResearchStateManager.get().syncFor(serverPlayer);
            BookVisualStateManager.get().syncFor(serverPlayer);
        }
    }
});
```

**Mixin:**

```java
@Inject(at = @At("TAIL"), method = "onKillEntity")
private void onKillEntity(Entity target, CallbackInfo ci) {
    var entityId = target.getType().registryName();
    var context = new EntityKilledContext(this.getPlayer(), entityId);
    if (ResearchServices.hooks().fire(MyModTriggerRegistry.ENTITY_KILLED, context)) {
        ResearchStateManager.get().syncFor(this.getPlayer());
        BookVisualStateManager.get().syncFor(this.getPlayer());
    }
}
```

## Datagen: Authoring Hooks

Use the generic ingress API in your `ResearchSubProvider`:

```java
this.ingress().on(MyModTriggerRegistry.ENTITY_KILLED, entityId)
    .declareFact("mymod/entity_killed_hook");
```

### Convenience methods

For built-in trigger types, convenience sugar is available:

```java
// Entry viewed once
this.ingress().onEntryViewedOnce(entryId)
    .declareFact("mymod/entry_hook");

// Item crafted — component matching is implicit in the template
this.ingress().onItemCrafted(new ItemStackTemplate(Items.DIAMOND_SWORD))
    .declareFact("mymod/crafting_hook");

// Item crafted with specific components
var template = new ItemStackTemplate(Items.DIAMOND_SWORD);
// Add component predicates to the template as needed
this.ingress().onItemCrafted(template)
    .declareFact("mymod/specific_crafting_hook");

// Item acquired
this.ingress().onItemAcquired(new ItemStackTemplate(Items.NETHER_STAR))
    .declareFact("mymod/acquisition_hook");

// Advancement earned
this.ingress().onAdvancementEarned(advancementId)
    .declareFact("mymod/advancement_hook");
```

## JSON Format

All hooks are stored in a single `hooks.json` file per research bundle.
Item hooks store their `ItemStackTemplate` as the primary target:

```json
[
  {
    "id": "mymod:entity_killed_hook",
    "trigger_type": "mymod:entity_killed",
    "trigger_target": "minecraft:ender_dragon",
    "fact_id": "mymod:dragon_killed"
  },
  {
    "id": "mymod:crafting_hook",
    "trigger_type": "modonomicon:item_crafted",
    "trigger_target": {
      "item": "minecraft:diamond_sword"
    },
    "fact_id": "mymod:made_sword"
  }
]
```

## Best Practices

- **Keep trigger handler logic minimal.** The handler should only resolve hooks
  and optionally replay. Complex event filtering belongs in your platform event code.
- **Use replay only when needed.** If your trigger represents a one-shot event
  (like crafting), the default no-op replay is correct.
- **Use the generic ingress for custom types.** The convenience sugar is for
  built-in types only. Third-party types use `this.ingress().on(triggerType, target)`.
- **Sync after every fire.** Always check the return value of `fire()` and sync
  both `ResearchStateManager` and `BookVisualStateManager` when true.
- **Use ItemStackTemplate for item triggers.** Component matching is implicit —
  if your template includes components, they will be checked automatically.

See also [Research Conditions](../basics/research/conditions) for how to gate
entries behind research nodes.
