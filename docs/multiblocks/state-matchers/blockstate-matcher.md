---
sidebar_position: 30
---


# Block State Matcher

**Type:** `modonomicon:blockstate`

BlockState matchers will check for the exact BlockState properties provided.

## Attributes

* **block** (BlockState, _mandatory_)

  BlockState to match against when checking if a given block fits this matcher.

  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will match against it's default BlockState. 

  :::

* **display** (BlockState, _optional_)

  Defaults to the value of the `block` property.   
  The block to display in the multiblock preview. 

  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will display the Block's default BlockState.

  :::


## Usage Examples

**Example:** Matching only west-facing chests, but displaying an east-facing chest

```json
{
    "type": "modonomicon:blockstate",
    "display": "minecraft:chest[facing=east]",
    "block": "minecraft:chest[facing=west]"
}
``` 

