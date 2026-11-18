---
sidebar_position: 46
---

# Custom Presenters

Presenters decide how a research element is drawn in a requirements block. There is one registry per kind of element, and all three share the same shape:

| Registry | Presents | Interface | Method |
|----------|----------|-----------|--------|
| `ResearchTriggerPresenterRegistry` | A hook of a trigger type, as a requirement row | `ResearchTriggerPresenter` | `present(hook, context)` |
| `ResearchEffectPresenterRegistry` | An effect of a stage reward or action, as a row | `ResearchEffectPresenter` | `present(effect, context)` |
| `ResearchCostPresenterRegistry` | A cost entry, as a row and as a tooltip line | `ResearchCostPresenter` | `row(entry, context, forceMet)`, `line(entry)` |

Each registry has the same three methods:

- `registerDefaults()` registers the presenters of the built-in types. Modonomicon calls it in its client setup.
- `register(typeId, presenter)` sets the presenter of one type. It replaces any presenter registered for that id before.
- `get(typeId)` returns the presenter of a type, or a generic fallback if none is registered.

Unknown types never break a block. They show a generic row with a book icon and a "special" title, so a missing presenter is visible but harmless.

## Trigger presenters

A trigger presenter turns a hook into a row. Only the icon, title, details and click action matter. The met state comes from the requirement itself. The category of the row is assigned by the caller, see [Requirement Categories](../basics/research/categories.md#where-a-row-goes). The built-in trigger presenters are in `DefaultResearchTriggerPresenters`.

```java
ResearchTriggerPresenterRegistry.register(MyModTriggers.ENTITY_KILLED.id(), (hook, context) ->
        RequirementRow.of(
                List.of(RequirementIcon.of(new ItemStack(Items.IRON_SWORD))),
                Component.translatable("mymod.requirement.entity_killed", entityName(hook)))
);
```

## Effect presenters

An effect presenter describes an effect that is shown as a reward or as part of an action. It returns a row:

```java
ResearchEffectPresenterRegistry.register(MyModResearchTypes.GRANT_MANA.id(), new ResearchEffectPresenter() {
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
ResearchCostPresenterRegistry.register(MyModResearchTypes.MANA.id(), new ResearchCostPresenter() {
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

Presenters run on the client only, so register them on the client.

- **NeoForge and Forge**: the client setup event (`FMLClientSetupEvent`). Modonomicon's own client setup calls `registerDefaults()` there.
- **Fabric**: `onInitializeClient`. Modonomicon's client initializer calls `registerDefaults()` there.

Presenters of your own types can be registered from your client setup at any time before the book opens, because the registries are plain maps keyed by type id. The order of two listeners of the same client setup event is not defined, so do not rely on it when you register a presenter for a built-in type.

`registerDefaults()` replaces a presenter registered earlier for the same built-in id. To replace a built-in presenter, register yours later, from a client event that runs after Modonomicon's client setup. Replacing built-in presenters is rarely needed; prefer giving your own types their own presenters.

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

Presenters must not change any state. The requirement block may draw them often while the book is open.

## Custom types

Presenters are keyed by the id of the type, so the type must exist. Register the type in common setup, see [Custom Costs and Effects](./custom-effects-and-costs.md#registration). For trigger types see [Custom Research Hooks](./custom-hooks.md).
