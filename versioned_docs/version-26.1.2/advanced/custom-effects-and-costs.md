---
sidebar_position: 47
---

# Custom Costs and Effects

Mods add their own cost and effect types, so that actions and stage rewards can do or take things Modonomicon does not know about. A type has a codec for its JSON parameters, a stream codec for syncing to the client, and a presenter for the book (see [Custom Presenters](../basics/scholarship/custom-presenters.md)).

- **Effect types** implement `ResearchEffect` and are registered with `ResearchEffectTypeRegistry.register`.
- **Cost types** implement `ResearchCostEntry` and are registered with `ResearchCostTypeRegistry.register`.

Both registries work the same way: the `type` field of the JSON picks the type, and a duplicate id throws.

## Registration

Register types in common setup, before research data is loaded. Common setup runs on both the client and the server, which is needed because research data and its costs and effects are synced to the client with stream codecs. The client must know every type to read the book.

Research data is loaded when the datapacks reload, which happens after common setup. Built-in types are registered in the same common setup step by Modonomicon itself.

```java
// NeoForge and Forge: common setup event, runs on client and server
public void onCommonSetup(FMLCommonSetupEvent event) {
    MyModResearchTypes.bootstrap();
}

// Fabric: main initializer, runs on client and server
@Override
public void onInitialize() {
    MyModResearchTypes.bootstrap();
}
```

```java
public final class MyModResearchTypes {

    public static final ResearchEffectType<GrantManaEffect> GRANT_MANA = ResearchEffectTypeRegistry.register(
            MyMod.loc("grant_mana"),
            GrantManaEffect.MAP_CODEC,
            GrantManaEffect.STREAM_CODEC
    );

    public static final ResearchCostType<ManaCostEntry> MANA = ResearchCostTypeRegistry.register(
            MyMod.loc("mana"),
            ManaCostEntry.MAP_CODEC,
            ManaCostEntry.STREAM_CODEC
    );

    private MyModResearchTypes() {
    }

    public static void bootstrap() {
    }
}
```

The `bootstrap()` method only makes sure the static fields are initialized.

Presenters are different: they only run on the client, and they are registered in client setup, see [Custom Presenters](../basics/scholarship/custom-presenters.md#registration).

## Effect types

An effect is applied on the server. `apply` returns whether the effect changed something: research state, inventory or experience. An action uses the result. An action that grants no fact and whose effects changed nothing is unavailable and costs nothing. An action that grants its fact is performed and paid even if no effect changed anything. `isAvailable` says whether the effect can run right now. An action is only available if all of its effects are.

```java
public record GrantManaEffect(int amount) implements ResearchEffect {

    public static final MapCodec<GrantManaEffect> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(GrantManaEffect::amount)
    ).apply(instance, GrantManaEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrantManaEffect> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, GrantManaEffect::amount, GrantManaEffect::new);

    @Override
    public ResearchEffectType<?> type() {
        return MyModResearchTypes.GRANT_MANA;
    }

    @Override
    public boolean apply(ServerPlayer player, ResearchEffectContext context) {
        return MyModMana.add(player, this.amount) > 0;
    }
}
```

`MyModMana.add` stands for your own mod code, and returns how much mana was actually added. The effect reports a change only if some mana was added, so a full mana bar makes an action that grants mana unavailable.

Use it in datagen with `ResearchEffectModel.of(...)`, or in JSON with the type id:

```java
ResearchEffectModel.of(new GrantManaEffect(5))
```

```json
{ "type": "mymod:grant_mana", "amount": 5 }
```

## Cost types

A cost entry is planned and paid in three steps. Planning must not change the player, because it runs before the state change, and it may run again when the plan is stale:

1. `plan(plan)` reserves what it needs from the `ResearchCostPlan`, e.g. a stack or levels. It returns false if the plan cannot cover it.
2. `apply(player, plan)` applies the reservations of the plan. It runs only after the state change succeeded, and it uses the plan that was made last, which may be a re-plan.
3. `describe()` returns the text for tooltips, e.g. "5 mana".

Also implement:

- `heldCount(player)` tells how much the player holds, for progress display only.
- `planRank()` orders the planning. Lower ranks plan first. The default is 1, and durability uses 0 so it plans before items. Item entries are planned together as one group, see [Planning order](../basics/research/actions.md#planning-order). Custom types plan one at a time, in the same order.

```java
public record ManaCostEntry(int amount) implements ResearchCostEntry {

    public static final MapCodec<ManaCostEntry> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(ManaCostEntry::amount)
    ).apply(instance, ManaCostEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ManaCostEntry> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, ManaCostEntry::amount, ManaCostEntry::new);

    @Override
    public ResearchCostType<?> type() {
        return MyModResearchTypes.MANA;
    }

    @Override
    public int heldCount(Player player) {
        return MyModMana.get(player);
    }

    @Override
    public boolean plan(ResearchCostPlan plan) {
        return MyModMana.get(plan.player()) >= this.amount;
    }

    @Override
    public void apply(ServerPlayer player, ResearchCostPlan plan) {
        MyModMana.remove(player, this.amount);
    }

    @Override
    public Component describe() {
        return Component.literal(this.amount + " mana");
    }
}
```

`ResearchCostPlan` only tracks reservations for inventory stacks and experience levels. It has no reservation for a custom resource such as this mana counter, so `plan` reads the full mana total every time, and `apply` removes the amount. Do not repeat this cost type within one cost: two `ManaCostEntry` entries of 5 mana would both pass planning against 5 mana and then both remove mana. Merge them into one entry with the summed amount instead. Costs that take inventory stacks should reserve them in `plan`: `plan.take(this, slot, count)` reserves items of a slot, `plan.claim(this, slot, amount)` reserves a whole damageable stack, and `plan.takeLevels(this, amount)` reserves levels. Reserving keeps two entries of one cost from claiming the same stack. `ItemCostEntry` and `DurabilityCostEntry` in `research/cost` are the reference implementations.

Creative players (infinite materials) pay nothing. The cost skips them before `plan` runs, so your cost does not need to handle them.

## Rules for both kinds

- `plan` must not change the player. Only the `apply` methods change it, and they run after the state change.
- Effects and costs run on the server only. Presenters run on the client.
- Keep the stream codec in step with the map codec. Both sides need the same type.
- A cost re-plans if the reserved stacks change during an action, so `apply` must only use the reservations of the plan it receives.

See [Actions, Costs and Effects](../basics/research/actions.md#consumption-order) for the consumption order, and [Effects](../basics/research/effects.md) for the built-in effect types.
