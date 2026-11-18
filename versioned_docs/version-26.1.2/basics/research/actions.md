---
sidebar_position: 13
---

# Actions, Costs and Effects

An action is something the player does on a requirement to satisfy it: paying to learn a fact, taking an item from the book, or completing a manual stage. An action is one record with four parts:

- **cost**: what the player pays, a list of typed entries, see [Costs](#costs).
- **effects**: what the action does, a list of effects applied in order, see [Rewards](./rewards.md) for the effect types.
- **grants_fact**: whether performing the action grants the fact it is attached to ("pay to learn").
- **repeatable**: whether the action stays available after its fact is granted and after its stage is complete.

An action is attached to a requirement fact through the display of the fact. It is shown as a clickable row, and its tooltip lists the cost.

## Example: pay to learn

```java
ResearchActionModel.learn(ResearchCostModel.create().item(Items.PAPER))
```

`learn` is `create().cost(cost).grantsFact()`. The player pays one paper and the fact is granted.

## Example: the take, process and submit loop

The Scholarship entry in the demo uses one fact for its whole loop. Its stage `transcription` requires the fact `demo/scholarship/note_studied`:

1. **Take.** The fact's action hands out a blank research note. It costs one paper and wears down the shears. It is repeatable, so the player can take more notes later.
2. **Process.** A crafting recipe turns the blank note and an ink sac into a completed research note.
3. **Submit.** Using the completed note in the air grants the fact. The note is consumed, see [item_used](./item-used.md).

In datagen, the action is set on the fact's display, and the fact's hook is an `item_used` ingress:

```java
var noteStudied = this.ingress()
        .onItemUsed(RESEARCH_NOTE)
        .consume(1)
        .declareFact(SCHOLARSHIP_NOTE_STUDIED.id().getPath());
this.research().display(noteStudied, ResearchDisplayModel.create()
        .withTitle(SCHOLARSHIP_NOTE_TITLE)
        .withAction(ResearchActionModel.create()
                .cost(ResearchCostModel.create().item(Items.PAPER).durability(Items.SHEARS, 1))
                .effect(ResearchEffectModel.giveItem(BLANK_RESEARCH_NOTE, false))
                .repeatable()));
```

The action is not available to a blank note, only the completed note submits the fact. Plain paper also processes into a completed note, because vanilla recipes ignore the marker component. A real mod would use its own item or a machine.

The same fact is also reachable from the JSON side. In `hooks.json` the item template is the target and `consume` is set on the hook:

```json
{
  "id": "modonomicon:demo/scholarship/note_studied_hook",
  "fact_id": "modonomicon:demo/scholarship/note_studied",
  "trigger_type": "modonomicon:item_used",
  "trigger_target": {
    "id": "minecraft:paper",
    "components": {
      "minecraft:custom_data": { "modonomicon_demo": "research_note" },
      "minecraft:custom_name": "Research Note"
    }
  },
  "consume": 1
}
```

## Example: manual completion

The last stage of the same entry is completed by hand, and its cost is two iron ingots:

```java
this.stage("synthesis", s -> s
        .fact(DemoResearch.SCHOLARSHIP_NOTE_STUDIED)
        .consume(Items.IRON_INGOT, 2)
        .title("Synthesis")
        .reward(ResearchEffectModel.giveItem(Items.EMERALD)));
```

`consume` makes the stage manual. The Iron Ingot row counts what the player holds. The stage consumes the iron on completion, and the button is enabled once the player holds enough.

## Repeatable actions

A repeatable action:

- stays clickable after its fact is granted and after its stage is complete,
- pays its cost on every performance,
- is only checked against the stage of its fact: the node must have reached that stage. A complete node has reached all stages, so the action stays available.

A non-repeatable action is only offered while its fact is missing and its stage is the current one.

## Consumption order

When the player performs an action, the steps run in this order:

1. The node must be unlocked and the fact must be a requirement of the current stage (or of a reached stage, for repeatable actions).
2. The effects must all be available. Effects like `give_item` with `require_absent` check the inventory here.
3. The cost must be affordable. The cost is checked as a whole, see [Costs](#costs).
4. The change is applied in one state change: the fact is granted first if the action grants it, then the effects run in order.
5. Only after the change succeeded, the cost is paid.

If an effect throws, nothing is paid. Manual stage completion follows the same order: the cost is checked, the stage completes, then the cost is paid.

## Costs

A cost is a list of typed entries. Each entry is paid from the player's inventory or experience:

| Type | Pays | Notes |
|------|------|-------|
| `modonomicon:item` | `count` items matching an item or tag matcher | Takes from any matching stacks |
| `modonomicon:durability` | `amount` durability from one matching damageable stack | Damages the stack, it is not consumed. Any usable stack is enough |
| `modonomicon:levels` | experience levels | |

JSON:

```json
"cost": [
  { "type": "modonomicon:item", "item": { "id": "minecraft:paper" } },
  { "type": "modonomicon:durability", "item": { "id": "minecraft:shears" }, "amount": 1 }
]
```

Datagen builds the same list with `ResearchCostModel`:

```java
ResearchCostModel.create()
        .item(Items.PAPER)
        .durability(Items.SHEARS, 1)
        .levels(2)
```

`levels` replaces the levels set before, so the last call wins.

### Planning order

All entries of one cost are planned together, so two entries cannot claim the same stack. Entries are planned by rank, then in declaration order:

1. **Durability** entries plan first, so an item entry cannot take the stack that a durability entry needs, e.g. shears that are also held as items.
2. All other entries, items and levels, plan next, in declaration order.

The cost is checked with that plan. If the plan fails, the cost is missing and nothing changes. Creative players (infinite materials) pay nothing.

Any tooltip or row of the cost lists what is held, so the player sees what is missing.

## Datagen summary

| Method | Use |
|--------|-----|
| `ResearchActionModel.create()` | An action with a cost, effects and flags |
| `ResearchActionModel.learn(cost)` | Pay to learn the fact |
| `ResearchActionModel.giveItem(stack, cost)` | Pay for an item |
| `.cost(...)`, `.effect(...)`, `.grantsFact()`, `.repeatable()` | Set the parts of an action |
| `ResearchCostModel.item/durability/levels/entry(...)` | Add cost entries, `entry` takes a custom type |

See [Custom Costs and Effects](../../advanced/custom-effects-and-costs.md) for cost types of your own.
