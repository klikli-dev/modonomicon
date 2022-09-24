---
sidebar_position: 50
---

# Or Condition

**Condition type:** `modonomicon:or`

This condition will be met, if at least one of the conditions in the `children` array is met.
Can be chained with [`modonomicon:and`](./and-condition) to create complex unlock logic.

## Attributes

* **children** (Condition[], _mandatory_)

The conditions that need to be met alternatively to unlock this entry.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:or",
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
