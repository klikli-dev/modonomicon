---
sidebar_position: 30
---

# Unlock Conditions

Conditions can be used to keep entries or whole categories hidden until the condition is met. This is useful to give players a sense of progression.

Conditions are JSON Objects that can be set as value for the "condition" field on entry or category JSONs as follows:

```json
"condition": {
    "type": "<type>",
    ... //condition specific fields
},
``` 

Note that only one condition can be supplied per entry or category. If you want to combine multiple conditions, you can use the [`modonomicon:and`](./and-condition) or [`modonomicon:or`](./or-condition) condition types.

## Common Attributes

The following attributes are available for all condition types:

### **type** (String, _mandatory_)

The type of condition, it determines which loader is used to load the json data.
Needs to be fully qualified `domain:name`, e.g. `modonomicon:read_entry`. 

### **tooltip** (DescriptionId or Component JSON, _optional_)

Default Value: *A tooltip that explains what needs to be done to unlock the entry based on the condition type.*  
The tooltip to display when hovering over the entry locked by this condition. Will only display while the entry is still locked.