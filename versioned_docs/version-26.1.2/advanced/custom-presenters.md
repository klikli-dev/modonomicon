---
sidebar_position: 46
---

# Custom Presenters

Presenters decide how a research element is drawn in a requirements block. There is one registry per kind of element, and all three share the same shape:

| Registry | Presents | Interface | Method |
|----------|----------|-----------|--------|
| `TriggerRequirementPresenterRegistry` | A hook of a trigger type, as a requirement row | `TriggerRequirementPresenter` | `present(hook, context)` |
| `ResearchEffectPresenterRegistry` | An effect of a stage reward or action, as a row | `ResearchEffectPresenter` | `present(effect, context)` |
| `ResearchCostPresenterRegistry` | A cost entry, as a row and as a tooltip line | `ResearchCostPresenter` | `row(entry, context, forceMet)`, `line(entry)` |

Each registry has the same three methods:

- `registerDefaults()` registers the presenters of the built-in types. Modonomicon calls it during client setup.
- `register(typeId, presenter)` sets the presenter of one type. It replaces any presenter registered for that id before.
- `get(typeId)` returns the presenter of a type, or a generic fallback if none is registered.

Unknown types never break a block. They show a generic row with a book icon and a "special" title, so a missing presenter is visible but harmless.

## Trigger presenters

A trigger presenter turns a hook into a row. Only the icon, title, details and click action matter. The met state comes from the requirement itself. The category of the row is assigned by the caller, see [Requirement Categories](../basics/research/categories.md#where-a-row-goes).

```java
TriggerRequirementPresenterRegistry.register(MyModTriggers.ENTITY_KILLED.id(), (hook, context) ->
        RequirementRow.of(
                List.of(RequirementIcon.of(new ItemStack(Items.IRON_SWORD))),
                Component.translatable("mymod.requirement.entity_killed", entityName(hook)))
);
```

## Effect presenters

An effect presenter describes an effect that is shown as a reward or as part of an action. It returns a row:

```java
ResearchEffectPresenterRegistry.register(MyModEffects.GRANT_MANA.id(), new ResearchEffectPresenter() {
    @Override
    public RequirementRow present(ResearchEffect effect, RequirementContext context) {
        var mana = (GrantManaEffect) effect;
        return RequirementRow.of(
                List.of(RequirementIcon.of(new ItemStack(MyModItems.MANA_CRYSTAL))),
                Component.translatable("mymod.reward.mana", mana.amount()));
    }
});
```

The built-in effect presenters are in `DefaultResearchEffectPresenters`.

## Cost presenters

A cost presenter describes one cost entry in two places: the requirement row of a manual stage, and the tooltip line of an action. Both come from the same presenter, so each entry is described in one place.

```java
ResearchCostPresenterRegistry.register(MyModCosts.MANA.id(), new ResearchCostPresenter() {
    @Override
    public RequirementRow row(ResearchCostEntry entry, RequirementContext context, boolean forceMet) {
        return RequirementRow.of(
                List.of(RequirementIcon.of(new ItemStack(MyModItems.MANA_CRYSTAL))),
                entry.describe());
    }
});
```

`line(entry)` is the tooltip line. It defaults to the entry's own `describe()`, so a cost type that describes itself well needs no override. The `row` is what the player sees in the stage requirements, and the caller adds the "consumed on completion" detail.

The built-in cost presenters are in `DefaultResearchCostPresenters`: item costs, durability costs and level costs.

## Registration

Register presenters on the client, in the client setup of your loader, after Modonomicon's own client setup has run. The built-in types are registered by `registerDefaults()`, which overwrites a presenter registered earlier for the same id. Presenters of your own types can be registered at any time before the book opens.

```java
// NeoForge and Forge: client setup event
public void onClientSetup(FMLClientSetupEvent event) {
    MyModPresenters.register();
}

// Fabric: client initializer
@Override
public void onInitializeClient() {
    MyModPresenters.register();
}
```

Presenters run on the client only. They must not change any state, since they may run every frame the requirement block is drawn.

## Custom types

Presenters are keyed by the id of the type, so the type must exist. For effect and cost types see [Custom Costs and Effects](./custom-effects-and-costs.md). For trigger types see [Custom Research Hooks](./custom-hooks.md).
