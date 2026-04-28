---
sidebar_position: 31
---

# Mod Loaded Condition

**Condition type:** `modonomicon:mod_loaded`

This condition will be met, if the mod with the given ID is loaded.   

## Attributes

### **mod_id** (String, _mandatory_)

The id of the mod that needs to be loaded for this condition to be met.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:mod_loaded",
      "mod_id": "theurgy"
  },
  ...
}
```
