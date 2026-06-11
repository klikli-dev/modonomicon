---
sidebar_position: 10
---

# Research Node Unlocked Condition

**Condition type:** `modonomicon:research_node_unlocked`

This condition will be met if the specified research node is fully completed (all stages finished) for the player.

When using datagen, `BookModel.withGenerateEntryHierarchyResearch(true)` automatically generates this condition for entries that have parents.

## Attributes

### **node_id** (ResourceLocation, _mandatory_)

The ResourceLocation of the research node that must be fully completed to unlock.

### **tooltip** (DescriptionId or Component JSON, _optional_)

The tooltip to display when hovering over the entry locked by this condition. If omitted, a tooltip is auto-generated from the node's description ID.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:research_node_unlocked",
      "node_id": "mymod:features/condition_root"
  },
  ...
}
```
