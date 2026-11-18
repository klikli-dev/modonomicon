---
sidebar_position: 47
---

# Custom Costs and Effects

Mods add their own cost and effect types, so that actions and stage rewards can do or take things Modonomicon does not know about. A type has a codec for its JSON parameters, a stream codec for syncing to the client, and a presenter for the book (see [Custom Presenters](./custom-presenters.md)).

- **Effect types** implement `ResearchEffect` and are registered with `ResearchEffectTypeRegistry.register`.
- **Cost types** implement `ResearchCostEntry` and are registered with `ResearchCostTypeRegistry.register`.

Both registries work the same way: the `type` field of the JSON picks the type, and a duplicate id throws.

## Registration

Register types before research data is loaded, e.g. during mod setup. Register them on both sides: the research state and the cost and effect types are synced with stream codecs, so the client needs the same types to read the book.

```java
// NeoForge and Forge: common setup event
public void onCommonSetup(FMLCommonSetupEvent event) {
    MyModResearchTypes.bootstrap();
}

// Fabric: initializer
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

## Effect types

An effect is applied on the server. `apply` returns true if research state changed. `isAvailable` says whether the effect can run right now. An action is only available if all of its effects are.

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
        MyModMana.add(player, this.amount);
        return false;
    }
}
```

`MyModMana.add` stands for your own mod code. The effect returns false here because it does not change research state. Return true if it grants a fact or changes a value.

Use it in datagen with `ResearchEffectModel.of(...)`, or in JSON with the type id:

```java
ResearchEffectModel.of(new GrantManaEffect(5))
```

```json
{ "type": "mymod:grant_mana", "amount": 5 }
```

## Cost types

A cost entry is paid in three steps, and it must not change the player while planning:

1. `plan(plan)` reserves what it needs from the `ResearchCostPlan`, e.g. a stack or levels. It returns false if the plan cannot cover it.
2. `apply(player, plan)` applies the reservations of the plan. It runs only after the whole cost was planned successfully.
3. `describe()` returns the text for tooltips, e.g. "5 mana".

Also implement:

- `heldCount(player)` tells how much the player holds, for progress display only.
- `planRank()` orders the planning. Lower ranks plan first. The default is 1, and durability uses 0 so it plans before items.

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

This cost takes mana from a player-owned counter that no other entry reserves, so planning only reads it. Costs that take inventory stacks should reserve them: `plan.take(this, slot, count)` reserves items of a slot, `plan.claim(this, slot, amount)` reserves a whole damageable stack, and `plan.takeLevels(this, amount)` reserves levels. Reserving keeps two entries of one cost from claiming the same stack. `ItemCostEntry` and `DurabilityCostEntry` in `research/cost` are the reference implementations.

Creative players (infinite materials) pay nothing: the cost checks skip them before `plan` runs, so your cost does not need to handle them.

## Rules for both kinds

- `plan`, `canPay` and `missing` must not change the player. Only `apply` and the effect's `apply` change it.
- Effects and costs run on the server only. Presenters run on the client.
- Keep the stream codec in step with the map codec. Both sides need the same type.

See [Actions, Costs and Effects](../basics/research/actions.md) for the consumption order, and [Rewards](../basics/research/rewards.md) for the built-in effect types.
