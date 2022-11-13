---
sidebar_position: 31
---


# Block State Property Matcher

**Type:** `modonomicon:blockstateproperty`

BlockStateProperty matchers will check for **only the properties you provide**, not all existing BlockState properties for that block.

:::tip

This matcher check only against the properties you actually provide, and ignore all other properties.
E.g.: If you provide a `facing` property, but the block you are checking against also has a `waterlogged` property, it will match as long as the `facing` property is correct, and ignore the value of the `waterlogged` property.

:::


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

**Example:** Matches lit candles, inependent of the amount of candles, and if they are waterlogged or not (although the latter of course has the in-game constraint that waterlogged will unlight the candle ...).

```json
{
    "type": "modonomicon:blockstateproperty",
    "display": "minecraft:white_candle[lit=true]",
    "block": "minecraft:white_candle[lit=true]"
}
``` 

