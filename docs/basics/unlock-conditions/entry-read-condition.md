---
sidebar_position: 10
---

# Entry Read Condition

**Condition type:** `modonomicon:entry_read`

This condition will be met, if the entry with the specified ID has been read by the player.

This condition type will be automatically generated for all entries if `auto_add_read_conditions` is set to `true` in `book.json`. See [Book.json](../structure/book#attributes) for details.

## Attributes

* **entry_id** (ResourceLocation, _mandatory_)

The ResourceLocation of the entry that needs to be read to unlock.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:entry_read",
      "entry_id": "modonomicon:features/condition_root"
  },
  ...
}
```
