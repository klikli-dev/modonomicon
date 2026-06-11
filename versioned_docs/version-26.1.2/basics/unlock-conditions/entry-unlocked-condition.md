---
sidebar_position: 20
---

# Research Stage Completed Condition

**Condition type:** `modonomicon:research_stage_completed`

This condition will be met if a specific stage within a research node is completed for the player.
This allows gating entries behind partial progress in multi-stage research nodes.

## Attributes

### **node_id** (ResourceLocation, _mandatory_)

The ResourceLocation of the research node containing the stage.

### **stage_id** (ResourceLocation, _mandatory_)

The ResourceLocation of the stage that must be completed.

### **tooltip** (DescriptionId or Component JSON, _optional_)

The tooltip to display when hovering over the entry locked by this condition. If omitted, a tooltip is auto-generated from the stage's description ID.

## Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:research_stage_completed",
      "node_id": "mymod:features/my_node",
      "stage_id": "mymod:features/my_node/stage_1"
  },
  ...
}
```
