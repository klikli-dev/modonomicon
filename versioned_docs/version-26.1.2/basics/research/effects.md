---
sidebar_position: 14
---

# Effects

An effect is something applied to the player on the server: it changes the player's research state or inventory, or gives experience. An effect is granted once when a stage of an entry research completes (a stage effect), or runs as part of an action, see [Actions, Costs and Effects](./actions.md). Effects only run on the server.

Stage effects are a list of effects:

```json
"effects": [
  { "type": "modonomicon:add_value", "value": "modonomicon:demo/insight", "amount": 1 },
  { "type": "modonomicon:grant_fact", "fact": "modonomicon:demo/research/notes_read" }
]
```

## Built-in effect types

| Type | Fields | Effect |
|------|--------|--------|
| `modonomicon:grant_fact` | `fact` | Grants a research fact |
| `modonomicon:add_value` | `value`, `amount` (optional, default `1`) | Adds to a research value |
| `modonomicon:give_item` | `stack`, `require_absent` (optional, default `false`) | Gives an item stack. With `require_absent`, the effect is only available while the player does not hold an equal stack |
| `modonomicon:experience` | `amount`, `levels` (optional, default `false`) | Gives experience points, or levels if `levels` is true |
| `modonomicon:function` | `function` | Runs a function as the player |

A `give_item` with `require_absent` hands out an item once, e.g. a research note. For an action, the action is only available while the effect is.

## Datagen

`ResearchEffectModel` has one factory per built-in type:

```java
this.stage("synthesis", s -> s
        .consume(Items.IRON_INGOT, 2)
        .title("Synthesis")
        .effect(ResearchEffectModel.giveItem(Items.EMERALD))
        .effect(ResearchEffectModel.function(SYNTHESIS_FUNCTION)));
```

| Factory | Effect |
|---------|--------|
| `giveItem(item)`, `giveItem(stack)`, `giveItem(stack, requireAbsent)` | `give_item` |
| `experience(points)`, `levels(levels)` | `experience` |
| `grantFact(fact)` | `grant_fact` |
| `addValue(value, amount)` | `add_value` |
| `function(id)` | `function` |
| `of(effect)` | Any effect, e.g. one of a third-party type |

`function` runs `data/<namespace>/function/<path>.mcfunction` as the player, e.g. `modonomicon:demo/synthesis_effect`.

Stage effects are granted once, in the order they are declared. Effects on an action run in the order of the action's `effects`.

## Changes

Every effect reports whether it changed something: research state, inventory or experience. Actions use this result. If the action grants its fact, it is performed and paid even without effects. An action that grants no fact and whose effects changed nothing is unavailable and costs nothing, see [Consumption order](./actions.md#consumption-order). Built-in effects report it as follows. `grant_fact` and `add_value` report whether the fact was newly granted or the stored value changed. `give_item`, `experience` and `function` always report a change when they run.

## Effect rows

A stage shows its effects as rows of the effects group, see [Scholarship Groups](../scholarship/groups.md). An effect row is met once its stage is complete, and until then it says it is granted when the stage is completed. Each effect type has a presenter that describes it, see [Custom Presenters](../scholarship/custom-presenters.md). Effect types without a presenter show a generic special effect row.

## Custom effect types

Mods add effect types through `ResearchEffectTypeRegistry.register`. See [Custom Costs and Effects](../../advanced/custom-effects-and-costs.md).
