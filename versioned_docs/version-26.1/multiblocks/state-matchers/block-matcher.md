---
sidebar_position: 20
---


# Block Matcher

**Type:** `modonomicon:block`

Block matchers will ignore the BlockState and check only if the placed block fits the configured block.

## Attributes

### **block** (Block, _mandatory_)
  
  The Block to match against when checking if a given block fits this matcher.


### **display** (BlockState, _optional_)

  Defaults to the default BlockState of the `block` property.   
  The BlockState to display in the multiblock preview. 

  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will display the Block's default BlockState.

  :::


## Usage Examples

**Example 1:** Matching (and displaying) a stone block

```json
{
  "type": "modonomicon:block",
  "block": "minecraft:stone"
}
```

**Example 2:** Matching any chest, but displaying a west-facing chest

```json
{
    "type": "modonomicon:block",
    "display": "minecraft:chest[facing=west]",
    "block": "minecraft:chest"
}
``` 