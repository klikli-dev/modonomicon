---
sidebar_position: 80
---

# Category Has Visible Entries Condition

**Condition type:** `modonomicon:category_has_visible_entries`

This condition will be met, if the category with the specified ID has any visible (locked & unlocked, but not hidden) entries or no entries at all.

## Attributes

### **category_id** (Identifier, _mandatory_)

The Identifier of the category that needs to have any visible (locked & unlocked, but not hidden) entries or no entries at all, to unlock this entry.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:category_has_visible_entries",
      "category_id": "spectrum:equipment"
  },
  ...
}
```
