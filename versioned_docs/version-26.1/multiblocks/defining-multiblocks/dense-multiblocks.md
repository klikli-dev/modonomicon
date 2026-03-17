---
sidebar_position: 10
---

# Dense Multiblocks

**Multiblock type:** `modonomicon:dense`

For Dense multiblocks, every block they are made up of has to be defined in the pattern, including air blocks and blocks that can be any block. 
The advantage of Dense multiblocks is that they are very intuitive to define, however for very large multiblocks the performance can suffer.

:::tip 

In almost all scenarios Dense Multiblocks are the best choice. Only if you have multiblocks that are exceedingly big - say, more than 32x32x32 blocks - you should consider using Sparse Multiblocks instead.

::: 


## Pattern 

Dense multiblocks are defined as a JSON 3D array of Strings in the `pattern` field of the multiblock definition JSON file.
They are basically a view of the multiblock, layer by layer. 

It could look something like this:

```json 
"pattern": [

    //this is the top layer of the multiblock, or y=1
    [
      " WWW ",
      "N G S",
      "NG0GS",
      "N G S",
      " EEE "
    ],

    //This is the bottom layer of the multiblock, or y=0
    [
      "DDDDD",
      "DDDDD",
      "DDDDD",
      "DDDDD",
      "DDDDD"
    ]
  ],
```


## Usage Examples

<!-- TODO: link to state matchers -->

The Multiblock with the id `<modid>:blockentity` would be placed in `resources/data/<modid>/modonomicon/multiblocks/blockentity.json` as follows:
```json
{
  "type": "modonomicon:dense",
  "pattern": [
    [
      " WWW ",
      "N G S",
      "NG0GS",
      "N G S",
      " EEE "
    ],
    [
      "DDDDD",
      "DDDDD",
      "DDDDD",
      "DDDDD",
      "DDDDD"
    ]
  ],
  "mapping": {
    "N": {
      "type": "modonomicon:blockstate",
      "block": "minecraft:oak_stairs[facing=north]"
    },
    "S":  {
      "type": "modonomicon:blockstate",
      "block": "minecraft:oak_stairs[facing=south]"
    },
    "W":  {
      "type": "modonomicon:blockstate",
      "block": "minecraft:oak_stairs[facing=west]"
    },
    "E":  {
      "type": "modonomicon:blockstate",
      "block": "minecraft:oak_stairs[facing=east]"
    },
    "G":  {
      "type": "modonomicon:block",
      "block": "minecraft:grass"
    },
    "D":  {
      "type": "modonomicon:display",
      "display": "minecraft:stone"
    },
    "0":  {
      "type": "modonomicon:block",
      "block": "minecraft:skeleton_skull"
    }
  }
}
```