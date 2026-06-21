---
sidebar_position: 55
---

# Unlock Conditions

Conditions can be used to keep pages, entries or whole categories hidden until the condition is met. This is useful to give players a sense of progression.

Most progression is now handled through the [Research System](../research). The condition types below cover the remaining non-research conditions.

Conditions are JSON Objects that can be set as value for the "condition" field on page, entry or category JSONs as follows:

```json
"condition": {
    "type": "<type>",
    ... //condition specific fields
},
``` 

Note that only one condition can be supplied per entry or category. If you want to combine multiple conditions, you can use the [`modonomicon:and`](./logic-conditions#and-condition) or [`modonomicon:or`](./logic-conditions#or-condition) condition types.

## Available Condition Types

The following condition types are available:

- [`modonomicon:research_node_unlocked`](../research/conditions#research-node-unlocked) — Gates behind a completed research node
- [`modonomicon:research_stage_completed`](../research/conditions#research-stage-completed) — Gates behind a specific stage of a research node
- [`modonomicon:mod_loaded`](./mod-loaded-condition) — Gates behind a loaded mod
- [`modonomicon:category_has_visible_entries`](./category-has-visible-entries-condition) — Gates behind category visibility
- [`modonomicon:and`](./logic-conditions#and-condition) / [`modonomicon:or`](./logic-conditions#or-condition) — Combine conditions
- [`modonomicon:true`](./logic-conditions#true-condition) / [`modonomicon:false`](./logic-conditions#false-condition) — Debug/placeholder

## Common Attributes

The following attributes are available for all condition types:

### **type** (String, _mandatory_)

The type of condition, it determines which loader is used to load the json data.
Needs to be fully qualified `domain:name`, e.g. `modonomicon:research_node_unlocked`. 

### **tooltip** (DescriptionId or Component JSON, _optional_)

Default Value: *A tooltip that explains what needs to be done to unlock the entry based on the condition type.*  
The tooltip to display when hovering over the entry locked by this condition. Will only display while the entry is still locked.
