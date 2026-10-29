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
