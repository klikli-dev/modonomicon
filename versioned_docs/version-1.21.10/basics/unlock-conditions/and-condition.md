---
sidebar_position: 40
---

# And Condition

**Condition type:** `modonomicon:and`

This condition will be met, if all conditions in the `children` array are met.
Can be chained with [`modonomicon:or`](./or-condition) to create complex unlock logic.

## Attributes

### **children** (Condition[], _mandatory_)

The conditions that need to be met cumulatively to unlock this entry.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:and",
      "children": [
          {
              "type": "modonomicon:advancement",
              "advancement_id": "occultism:occultism/craft_dimensional_matrix"
          },
          {
              "type": "modonomicon:entry_unlocked",
              "entry_id": "modonomicon:features/condition_root"
          }
      ]
  },
  ...
}
```
