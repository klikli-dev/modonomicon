---
sidebar_position: 20
---

# Sparse Multiblock

**Multiblock type:** `modonomicon:sparse`

Unlike Dense multiblocks, sparse multiblocks only define blocks that need to be of a specific type, all other blocks are considered [`any` blocks](../state-matchers/any-matcher). They are somewhat less intuitive to define, but offer great performance. 

:::tip 

In almost all scenarios Dense Multiblocks are the best choice. Only if you have multiblocks that are exceedingly big - say, more than 32x32x32 blocks - you should consider using Sparse Multiblocks instead.

::: 

## Pattern 

Sparse multiblocks are defined as key-value pairs in the `pattern` field of the multiblock definition JSON file, similar to how `mappings` are defined.
The key is the character corresponding to the `mappings` entry, and the value is a set of 3D coordinates representing the location of the block in the multiblock, relative to the center of the multiblock (which is represented by [`0`](./#multiblock-center-0)).

It could look something like this:

```json 
 "pattern": {
    "N": [
      [-1, 0, -2], [0, 0, -2], [1, 0, -2]
    ],
    "S": [
      [-1, 0, 2], [0, 0, 2], [1, 0, 2]
    ],
    "W": [
      [-2, 0, -1], [-2, 0, 0], [-2, 0, 1]
    ],
    "E": [
      [2, 0, -1], [2, 0, 0], [2, 0, 1]
    ]
  },
```


## Usage Examples


The Multiblock with the id `<modid>:sparse_test` would be placed in `resources/data/<modid>/modonomicon_multiblocks/sparse_test.json` as follows:
```json
{
  "type": "modonomicon:sparse",
  "pattern": {
    "N": [
      [-1, 0, -2], [0, 0, -2], [1, 0, -2]
    ],
    "S": [
      [-1, 0, 2], [0, 0, 2], [1, 0, 2]
    ],
    "W": [
      [-2, 0, -1], [-2, 0, 0], [-2, 0, 1]
    ],
    "E": [
      [2, 0, -1], [2, 0, 0], [2, 0, 1]
    ]
  },
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
    }
  }
}
```