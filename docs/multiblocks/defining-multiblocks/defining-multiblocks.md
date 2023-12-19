---
sidebar_position: 10
---

# Defining Multiblocks

## Multiblock Files 

Multiblocks are defined in `.json` files in `/resources/data/<modid>/modonomicon/multiblocks/*.json`. The file name is the ID of the multiblock.


## Multiblock Types

The exact structure of the multiblock file depends on the type of multiblock: **dense** or **sparse**. 
They differ in how the *pattern*, that is, their shape, is defined. See [Dense Multiblocks](./dense-multiblocks) and [Sparse Multiblocks](./sparse-multiblocks) for details.

What they have in common is how the *mapping* is defined, that is, how each block in the pattern is matched to a block in the world.

The type is defined via the `type` field in the multiblock definition JSON file:

```json
{
  "type": "<modonomicon:dense>|<modonomicon:sparse>",
  "pattern": [
     //...
  ],
  "mapping": {
    //...
  }
}
```

## Mapping

The mapping is defined in the `mapping` field of the multiblock definition. It is a JSON object that consists of key-value pairs. They key is a character that will be used in the pattern to represent the block, and the value is a [Block Matcher](../state-matchers/) that defines how the block is matched.

You can use any character you want as a key, however you may only use one character per key, not multiple. Additionally you should be careful with [Default Mappings and Special Characters](#default-mappings-and-special-characters).

A mapping can look something like this: 

```json

"mapping": {
    "N": {
      "type": "modonomicon:blockstate",
      "block": "minecraft:oak_stairs[facing=north]"
    },
    "S":  {
      "type": "modonomicon:blockstate",
      "block": "minecraft:oak_stairs[facing=south]"
    },
    //...
}
```

In this case the character `N` will be used to represent a north-facing oak stair, and `S` will be used to represent a south-facing oak stair in the pattern. Both will be matched based on their BlockState, meaning that BlockState properties, such as in this case the facing, will be checked. If you place a stair in the world that is facing the wrong direction, the multiblock will not be recognized as valid.

Make sure to familiarize yourself with the available **[State Matchers](../state-matchers/)** to find the best way to define your multiblock.

:::tip

Different State Matcher types have different use cases. For example, the "Display" state matcher can be used to e.g. show ground blocks in a multiblock preview in the book for better visibility, but these blocks will not be checked against the multiblock defition, meaning *any* block can be used in the world instead. 

:::

## Default Mappings and Special Characters

Some characters have a default mapping or a special meaning.

### Multiblock Center: `0`

The center of the multiblock is defined by the character `0`. 
It is used to determine the position of the multiblock in the world.

By default `0` is mapped to ["Air"](#air--space-character).
If you want the center of the multiblock to be any other block, simply create your own entry in the `mapping` field, such as:

```json
 "0": {
    "type": "modonomicon:block",
    "block": "minecraft:skeleton_skull"
}
```

In this case a Skeleton Skull would become our center.

### Air: <code>&nbsp;</code> (space character)

By default "Air" is represented by the space character <code>&nbsp;</code>.   
Air means that this block must be empty in the multiblock. Players must remove any blocks that may exist in this space to get a valid multiblock.
The in-world multiblock preview will display a small transparent red block in this space to indicate that it must be removed.


:::info 

You can also map other characters to "Air" using the [Air Predicate Matcher Type](../state-matchers/predicate-matcher#air-predicate), or map <code>&nbsp;</code> to a different block, but this is generally not recommended.

:::

### Any: `_`

"Any" block is represented by the underscore `_`.  
This means that Air, or any other Block may be used in this space.

:::info 

Just like Air, you can also map other characters to "Any" using the [Any Matcher](../state-matchers/any-matcher), or map `_` to a different block, but again, this is generally not recommended.

:::