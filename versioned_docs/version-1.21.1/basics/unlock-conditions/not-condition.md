---
sidebar_position: 55
---

# Not Condition

**Condition type:** `modonomicon:not`

This condition will be met, if the child condition is not met.
Can be combined with [`modonomicon:and`](./and-condition) and [`modonomicon:or`](./or-condition) to create complex unlock logic.

:::tip

The child's tooltip is not forwarded, as it would describe the non-negated requirement (e.g. "Requires X"), which is misleading once the logic is inverted. Set an explicit `tooltip` if one is needed.

:::

:::warning

Unlocks are permanent: entries, categories and pages are only ever added to the unlocked set, never removed. A runtime `not(...)` condition therefore latches — e.g. an entry with `not(entry_unlocked X)` unlocks while X is still locked and stays unlocked afterwards. `not` is fully precise as [`load_condition`](./load-conditions.md) (evaluated once at load) and for static children such as [`mod_loaded`](./mod-loaded-condition.md).

:::

## Attributes

### **child** (Condition, _mandatory_)

The condition to invert.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:not",
      "child": {
          "type": "modonomicon:advancement",
          "advancement_id": "minecraft:story/mine_diamond"
      }
  },
  ...
}
```
