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

```java
public interface TriggerHandler {
    /**
     * Resolve hooks matching a specific event target.
     */
    List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier targetId);

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

Called when a concrete event fires. Return all hooks whose target condition is met.
For simple trigger types this is a direct map lookup via `ResearchDataManager.get().hooksFor(type, targetId)`.

### replayAll() (optional)

Called on player login and after research reset to restore research state from
past events. The default implementation returns false (no-op).

Only implement this if your trigger type represents a persistent state that can
change outside of the live event stream (e.g. advancements, which can be completed
at any time and need to be re-checked on login).

**Simple trigger (no replay):**

```java
public class EntityKilledTriggerHandler implements TriggerHandler {
    @Override
    public List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier entityId) {
        return ResearchDataManager.get().hooksFor(MyTriggers.ENTITY_KILLED, entityId);
    }
    // replayAll: no-op (default) — kills are tracked by the kill counter
}
```

**Stateful trigger (with replay):**

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

## Registration

Create a dedicated registry class. Use `public static final` fields initialized
via `TriggerTypeRegistry.register(...)`, and provide an empty `bootstrap()` method:

```java
public final class MyModTriggerRegistry {

    public static final TriggerType ENTITY_KILLED = TriggerTypeRegistry.register(
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

Use `ResearchServices.hooks().fire(player, triggerType, targetId)` from your
platform event handler:

**NeoForge:**

```java
NeoForge.EVENT_BUS.addListener((MyModEntityKillEvent e) -> {
    var player = (ServerPlayer) e.getEntity();
    if (ResearchServices.hooks().fire(player, MyModTriggerRegistry.ENTITY_KILLED, e.getEntityId())) {
        ResearchStateManager.get().syncFor(player);
        BookVisualStateManager.get().syncFor(player);
    }
});
```

**Fabric:**

```java
MyModEvents.ENTITY_KILLED.register((player, entityId) -> {
    if (player instanceof ServerPlayer serverPlayer) {
        if (ResearchServices.hooks().fire(serverPlayer, MyModTriggerRegistry.ENTITY_KILLED, entityId)) {
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
    if (ResearchServices.hooks().fire(this.getPlayer(), MyModTriggerRegistry.ENTITY_KILLED, entityId)) {
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

// Item crafted
this.ingress().onItemCrafted(new ItemStackTemplate(Items.DIAMOND_SWORD))
    .declareFact("mymod/crafting_hook");

// Item acquired
this.ingress().onItemAcquired(new ItemStackTemplate(Items.NETHER_STAR))
    .declareFact("mymod/acquisition_hook");

// Advancement earned
this.ingress().onAdvancementEarned(advancementId)
    .declareFact("mymod/advancement_hook");
```

## JSON Format

All hooks are stored in a single `hooks.json` file per research bundle:

```json
[
  {
    "id": "mymod:my_hook",
    "trigger_type": "mymod:entity_killed",
    "event_target_id": "minecraft:ender_dragon",
    "fact_id": "mymod:dragon_killed"
  }
]
```

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
