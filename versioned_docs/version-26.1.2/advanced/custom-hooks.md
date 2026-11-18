---
sidebar_position: 45
---

# Custom Research Hooks

Mods can add custom research hook trigger types that fire from arbitrary game events
and translate them into research progression (fact grants or value increments).

To this end you need to:

1. Create an `Identifier` ID for the new trigger type (e.g. `mymod:entity_killed`)
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
| `modonomicon:item_crafted` | A matching item is crafted | `ResearchItemMatcher` | `ItemCraftedContext` | Item template (`{"id": ...}`, components checked) or item tag (`{"tag": ...}`) |
| `modonomicon:item_acquired` | A matching item appears in inventory | `ResearchItemMatcher` | `ItemAcquiredContext` | Item template (`{"id": ...}`, components checked) or item tag (`{"tag": ...}`) |
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

The core matching logic. For item triggers, this calls `ResearchItemMatcher.matches(stack)`: an item template matches the item and the components listed in the template, and a tag matches any item in the tag.

### Index keys (optional)

`indexKey(target)` and `indexKey(context)` let the runtime skip hooks that cannot match an event.
When the research data loads, the hooks of each trigger type are grouped by the index key of their target.
When an event fires, only the hooks with the event's key are checked, together with the unkeyed hooks.
The hooks keep their authored order.

- A context with a key checks the hooks with that key, and the unkeyed hooks.
- A context without a key (`indexKey(context)` returns `null`) checks all hooks of the trigger type.
- A target without a key (`indexKey(target)` returns `null`, e.g. a tag matcher) is checked for every event of its trigger type.

A keyed target can only match contexts with the same key. If a target can match contexts with different keys, return `null` for it, otherwise it is never checked for those contexts.
For example, item templates use the item id as their key, and tag matchers return `null`.

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
        return ResearchDataManager.get().hooksFor(type, context);
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
        return ResearchDataManager.get().hooksFor(type, context);
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
            Identifier.CODEC,
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

## Advanced: Listening to research changes

:::tip

Most mods do not need this. It is an extension point for systems that have to react to research changes, such as the book's visibility and unread state. Modonomicon uses it for exactly that.

:::

Research does not depend on the book. Code that needs to react to research changes registers a `ResearchStateChangeListener` through `ResearchServices.hooks()`:

```java
public interface ResearchStateChangeListener {
    Completion beforeChange(ServerPlayer player);

    interface Completion {
        void afterChange(boolean changed);
    }
}
```

Listeners wrap every hook batch, in this order:

1. `beforeChange(player)` runs for every listener, in registration order. Take your snapshot here.
2. The facts and values of the batch are changed, the nodes are reevaluated and the toasts are collected.
3. The collected toasts are sent to the player.
4. The `Completion` returned by each listener runs with `changed`, which is `true` if any research state changed.

Register the listener during mod initialization, before research events fire:

```java
ResearchServices.hooks().addStateChangeListener(new MyModResearchListener());
```

```java
public class MyModResearchListener implements ResearchStateChangeListener {
    @Override
    public Completion beforeChange(ServerPlayer player) {
        var before = MyModState.snapshot(player);
        return changed -> {
            if (changed) {
                MyModState.refresh(player, before);
            }
        };
    }
}
```

Listeners only run for hook batches. Changes made directly to the research state, such as by admin commands, do not call them. Keep listeners cheap, because they run for every batch of hooks.

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

// Item crafted: an item template, matching its item and the components it lists
this.ingress().onItemCrafted(new ItemStackTemplate(Items.DIAMOND_SWORD))
    .declareFact("mymod/crafting_hook");

// Item crafted: any item of a tag
this.ingress().onItemCrafted(ItemTags.PLANKS)
    .declareFact("mymod/planks_hook");

// Item acquired
this.ingress().onItemAcquired(new ItemStackTemplate(Items.NETHER_STAR))
    .declareFact("mymod/acquisition_hook");

// Advancement earned
this.ingress().onAdvancementEarned(advancementId)
    .declareFact("mymod/advancement_hook");
```

`onItemCrafted` and `onItemAcquired` accept an `ItemStackTemplate`, an `ItemLike`, a `TagKey<Item>` or a `ResearchItemMatcher`. See [Research Datagen](../basics/research/datagen.md#item-triggers-and-tags).

## JSON Format

All hooks are stored in a single `hooks.json` file per research bundle.
Item hooks store their `ResearchItemMatcher` as the target: an item template (`{"id": ...}`) or a tag (`{"tag": ...}`):

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
      "id": "minecraft:diamond_sword"
    },
    "fact_id": "mymod:made_sword"
  },
  {
    "id": "mymod:planks_hook",
    "trigger_type": "modonomicon:item_crafted",
    "trigger_target": {
      "tag": "minecraft:planks"
    },
    "fact_id": "mymod:made_planks"
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
- **Use item templates or tags for item triggers.** A `ResearchItemMatcher` is either an item template, whose components are checked automatically, or an item tag.

See also [Research Conditions](../basics/research/conditions) for how to gate
entries behind research nodes.

