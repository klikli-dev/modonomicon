---
sidebar_position: 15
---

# Item Used

The `item_used` trigger fires when a player uses an item in the air, e.g. right-clicking a book or a note without a block target. It lets a research fact depend on an item the player uses, not only on crafting or acquiring it.

Use it with `ResearchIngressHelper.onItemUsed` in datagen, or with the `modonomicon:item_used` trigger type in `hooks.json`. The target is an item template or an item tag, like `item_crafted` and `item_acquired`. Components listed in a template must match the used stack.

## Consume

A hook can consume items of the used stack with its `consume` field. The stack loses that many items, but only if the hook changes research state:

- a fact the player does not have yet is granted, or
- a value is incremented.

A hook that does nothing, because its fact is already granted, does not consume. If several hooks change state, the largest `consume` is used.

```java
this.ingress()
        .onItemUsed(RESEARCH_NOTE)
        .consume(1)
        .declareFact("demo/scholarship/note_studied");
```

In `hooks.json` this is the `consume` field of the hook:

```json
{
  "id": "modonomicon:demo/scholarship/note_studied_hook",
  "fact_id": "modonomicon:demo/scholarship/note_studied",
  "trigger_type": "modonomicon:item_used",
  "trigger_target": { "id": "minecraft:paper" },
  "consume": 1
}
```

If no hook changes state, the use does nothing: nothing is consumed and the item is used as normal.

## Behavior

When the player uses an item, the runtime:

1. Ignores empty stacks, spectators and stacks on cooldown.
2. Applies the `item_used` hooks that match the stack, collecting the consumption of the hooks that change state.
3. If anything changed, consumes the stack, syncs the research state and the book, shows the learned feedback and cancels the vanilla use.

Use the consume only for items that the player is meant to spend. A completed research note consumed by the Scholarship entry is one example.

## Requirements

`item_used` is one of the built-in trigger types, see [Research System](./research.md#hooks). Its rows show as "Not used yet" until the fact is granted, and they belong to the knowledge category by default, see [Requirement Categories](./categories.md).

To use an item as a requirement of a stage, write `use(...)` in the stage builder. It consumes nothing unless you pass a count:

```java
this.stage("submit", s -> s.use(RESEARCH_NOTE, 1).title("Submit"));
```
