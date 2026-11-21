---
sidebar_position: 6
---

# Custom Presenters

Presenters decide how a research element is drawn in a scholarship block. They are the client side of the scholarship layer: they turn a hook, an effect or a cost into a row. There is one registry per kind of element, and all three share the same shape:

| Registry | Presents | Interface | Method |
|----------|----------|-----------|--------|
| `ScholarshipTriggerPresenterRegistry` | A hook of a trigger type, as a scholarship row | `ScholarshipTriggerPresenter` | `present(hook, context)` |
| `ScholarshipEffectPresenterRegistry` | An effect of a stage or action, as a row | `ScholarshipEffectPresenter` | `present(effect, context)` |
| `ScholarshipCostPresenterRegistry` | A cost entry, as a row and as a tooltip line | `ScholarshipCostPresenter` | `row(entry, context, forceMet)`, `line(entry)` |

Each registry has the same three methods:

- `registerDefaults()` registers the presenters of the built-in types. Modonomicon calls it in its client setup.
- `register(typeId, presenter)` sets the presenter of one type. It replaces any presenter registered for that id before.
- `get(typeId)` returns the presenter of a type, or a generic fallback if none is registered.

Unknown types never break a block. They show a generic row with a book icon and a "special" title, so a missing presenter is visible but harmless.

## Trigger presenters

A trigger presenter turns a hook into a row. Only the icon, title, details and click action matter. The met state comes from the research element itself. The group of the row is assigned by the caller, see [Scholarship Groups](./groups.md#where-a-row-goes). The built-in trigger presenters are in `DefaultScholarshipTriggerPresenters`.

Any scholarship icon can carry a small badge with `ScholarshipIcon.withBadge(texture)`. The badge is drawn at 8x8 pixels in the bottom right corner of the 16x16 icon. The built-in presenters do not use badges, because they would cover the item and its count.

```java
ScholarshipTriggerPresenterRegistry.register(MyModTriggers.ENTITY_KILLED.id(), (hook, context) ->
        ScholarshipRow.of(
                List.of(ScholarshipIcon.of(new ItemStack(Items.IRON_SWORD))),
                Component.translatable("mymod.trigger.entity_killed", entityName(hook)))
);
```

## Effect presenters

An effect presenter describes an effect that is shown as a stage effect or as part of an action. It returns a row:

```java
ScholarshipEffectPresenterRegistry.register(MyModResearchTypes.GRANT_MANA.id(), new ScholarshipEffectPresenter() {
    @Override
    public ScholarshipRow present(ResearchEffect effect, ScholarshipContext context) {
        var mana = (GrantManaEffect) effect;
        return ScholarshipRow.of(
                List.of(ScholarshipIcon.of(new ItemStack(MyModItems.MANA_CRYSTAL))),
                Component.translatable("mymod.effect.mana", mana.amount()));
    }
});
```

The built-in effect presenters are in `DefaultScholarshipEffectPresenters`.

## Cost presenters

A cost presenter describes one cost entry in two places: the scholarship row of a manual stage, and the tooltip line of an action. Both come from the same presenter, so each entry is described in one place.

```java
ScholarshipCostPresenterRegistry.register(MyModResearchTypes.MANA.id(), new ScholarshipCostPresenter() {
    @Override
    public ScholarshipRow row(ResearchCostEntry entry, ScholarshipContext context, boolean forceMet) {
        return ScholarshipRow.of(
                List.of(ScholarshipIcon.of(new ItemStack(MyModItems.MANA_CRYSTAL))),
                entry.describe());
    }
});
```

`line(entry)` is the tooltip line. It defaults to the entry's own `describe()`, so a cost type that describes itself well needs no override. The `row` is what the player sees in the scholarship block of the stage, and the caller adds the "consumed on completion" detail.

The built-in cost presenters are in `DefaultScholarshipCostPresenters`: item costs, durability costs and level costs.

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

Presenters must not change any state. The scholarship block may draw them often while the book is open.

## Custom types

Presenters are keyed by the id of the type, so the type must exist. Register the type in common setup, see [Custom Costs and Effects](../../advanced/custom-effects-and-costs.md#registration). For trigger types see [Custom Research Triggers](../../advanced/custom-hooks.md).
