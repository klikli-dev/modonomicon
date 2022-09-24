---
sidebar_position: 30
---

# Advancement Condition

**Condition type:** `modonomicon:advancement`

This condition will be met, if the player has the specified advancement. 

## Attributes

* **advancement_id** (ResourceLocation, _mandatory_)

The ResourceLocation of the advancement that the player needs to have to unlock this entry.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:advancement",
      "advancement_id": "occultism:occultism/craft_dimensional_matrix"
  },
  ...
}
```
