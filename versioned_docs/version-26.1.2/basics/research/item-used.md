---
sidebar_position: 15
---

# Item Used

The `item_used` trigger fires when a player uses an item in the air, e.g. right-clicking a book or a note without a block target. It lets a research fact or value depend on an item the player uses, not only on crafting or acquiring it.

Use it with `ResearchHookHelper.onItemUsed` in datagen, or with the `modonomicon:item_used` trigger type in `hooks.json`. The target is an item template, an item tag or a matcher, like `item_crafted` and `item_acquired`. Components listed in a template must match the used stack.

## Hooks that count, handle and consume

A hook of this trigger does one of two things when the item is used:

- A **fact hook** counts if it grants a fact the player does not have yet. Counting handles the use: the vanilla use is cancelled and the item is consumed, if the hook has a `consume`.
- A **value hook** counts if it increments its value and has a positive `consume`. Counting handles the use the same way.

A value hook without `consume` does not count, but it still increments its value and the research state is synced. It does not handle the use, so the vanilla use continues. Food is still eaten, potions are still drunk and buckets still fill. Use this for counters of things the player does with an item anyway.

```java
this.hook()
        .onItemUsed(RESEARCH_NOTE)
        .consume(1)
        .declareFact("demo/research/note_studied");
```

The same hook in `hooks.json`:

```json
{
  "id": "modonomicon:demo/research/note_studied_hook",
  "fact_id": "modonomicon:demo/research/note_studied",
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

A value hook without consume only counts uses, and it does not interfere with the item:

```json
{
  "id": "modonomicon:demo/drinks_hook",
  "trigger_type": "modonomicon:item_used",
  "trigger_target": { "id": "minecraft:potion" },
  "value_id": "modonomicon:demo/drinks",
  "consume": 0
}
```

## Consume

`consume` is the number of items of the used stack that a handled use takes. It is the largest `consume` of the counting hooks. If the used stack holds fewer items than that, the counting hooks do not apply: the use is not handled, nothing is consumed, the vanilla use runs, and only hooks that do not count still apply. Hooks that do not count consume nothing.

A negative `consume` is rejected. The hook codec only accepts zero or more, and `consume(...)` in datagen throws an `IllegalArgumentException`.

## Behavior

When the player uses an item, the runtime:

1. Ignores empty stacks, spectators and stacks on cooldown.
2. Applies the `item_used` hooks that match the stack. If the stack is smaller than the largest `consume` of the counting hooks, only the hooks that do not count apply. If the research state changed, it syncs the research state and the book, so counters and facts appear at once.
3. If a hook handled the use, takes the consumed items, plays the learning feedback and cancels the vanilla use. Otherwise the vanilla use runs unchanged.

## Requirements

`item_used` is one of the built-in trigger types, see [Research System](./research.md#hooks). Its rows show as "Not used yet" until the fact is granted, and they belong to the knowledge category by default, see [Requirement Categories](./categories.md).

To use an item as a requirement of a stage, write `use(...)` in the stage builder. It consumes nothing unless you pass a count:

```java
this.stage("submit", s -> s.use(RESEARCH_NOTE, 1).title("Submit"));
```
