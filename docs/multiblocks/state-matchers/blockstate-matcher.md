---
sidebar_position: 30
---


# Block State Matcher

**Type:** `modonomicon:blockstate`

BlockState matchers will check for **all existing properties for a Block** BlockState. 

:::warn

Block State Matchers  match against the full blockstate (= all blockstate properties the block has). 
If you do not provide a value for any of the properties, it will check against the default value.

:::

:::tip

In most use cases you will want to check only against the properties you actually provide, and ignore all other properties.
For this behaviour, see **[BlockStateProperty Matcher](./blockstate-property-matcher)**.

:::

## Attributes

### **block** (BlockState, _mandatory_)

  BlockState to match against when checking if a given block fits this matcher.

  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will match against it's default BlockState. 

  :::

### **display** (BlockState, _optional_)

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