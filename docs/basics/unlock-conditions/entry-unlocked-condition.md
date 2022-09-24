---
sidebar_position: 20
---

# Entry Unlocked Condition

**Condition type:** `modonomicon:entry_unlocked`

This condition will be met, if the entry with the specified ID has been unlocked for the player, meaning that that entry's condition has been met.
This allows to lock one entry behind a specific condition, and "chain" other entries to unlock together with that entry, effectively unlocking multiple entries at once.

## Attributes

* **entry_id** (ResourceLocation, _mandatory_)

The ResourceLocation of the entry that needs to be unlocked to unlock this entry.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:entry_unlocked",
      "entry_id": "modonomicon:features/condition_root"
  },
  ...
}
```
