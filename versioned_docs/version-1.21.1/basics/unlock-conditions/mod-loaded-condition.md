---
sidebar_position: 31
---

# Mod Loaded Condition

**Condition type:** `modonomicon:mod_loaded`

This condition will be met, if the mod with the given ID is loaded.   

:::tip

To completely remove content if a mod is not loaded, use this condition as [Load Condition](./load-conditions.md) (`load_condition`). The regular `condition` only hides the content, but it is still loaded, which may cause errors if it references content of the missing mod.

:::

## Attributes

### **mod_id** (String, _mandatory_)

The id of the mod that needs to be loaded for this condition to be met.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "load_condition": {
      "type": "modonomicon:mod_loaded",
      "mod_id": "theurgy"
  },
  ...
}
```
