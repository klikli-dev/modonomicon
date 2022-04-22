---
sidebar_position: 30
title: State Matchers
---

Each state matcher represents one block at one specific position in the multiblock, and depending on the type of matcher it may only allow one specific block, or a wide range of blocks in that position. 

State matchers are defined [in the `mappings` part of the multiblock definition](./defining-multiblocks#mappings).

## Common Attributes

All state matchers need to have the following attributes:

* **type** (State Matcher Type, _mandatory_)

A ResourceLocation identifying the type of state matcher to use.   
Example: `modonomicon:block` 

## Attribute Types

Besides standard JSON types, state matchers support the following attributes:

* **Block** (String)

  A ResourceLocation for a block, in the format `modid:block`.  
  Example: `minecraft:stone` 

* **BlockState** (String)

  A BlockState string as used in the Minecraft `setblock` command.   
  Example: `minecraft:chest[facing=east]`.  

  The block state properties can be omitted, in which case the default BlockState will be used.   
  Example: `minecraft:chest`.

  See https://minecraft.fandom.com/wiki/Commands/setblock for more information
  
* **Tag** (String)

  A Tag string that is based on the BlockState string as used in the `setblock` command, but prefixed with `#`.   
  Example: `#forge:chests[facing=east]`

  The block state properties can be omitted, in which case the block state properties will be ignored when matching.   
  Example: `#forge:chests`.


## Block Matcher

**Type:** `modonomicon:block`

Block matchers will ignore the BlockState and check only if the placed block fits the configured block.

### Attributes

* **block** (Block, _mandatory_)
  
  The Block to match against when checking if a given block fits this matcher.


* **display** (BlockState, _optional_)

  Defaults to the default BlockState of the `block` property.   
  The BlockState to display in the multiblock preview. 

  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will display the Block's default BlockState.

  :::


### Usage Examples

**Example 1:** Matching (and displaying) a stone block

```json
{
  "type": "modonomicon:block",
  "block": "minecraft:stone"
}
```

**Example 3:** Matching any chest, but displaying a west-facing chest

```json
{
    "type": "modonomicon:block",
    "display": "minecraft:chest[facing=west]",
    "block": "minecraft:chest"
}
``` 

## Block State Matcher

**Type:** `modonomicon:blockstate`

BlockState matchers will check for the exact BlockState properties provided.

### Attributes

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


### Usage Examples

**Example:** Matching only west-facing chests, but displaying an east-facing chest

```json
{
    "type": "modonomicon:blockstate",
    "display": "minecraft:chest[facing=east]",
    "block": "minecraft:chest[facing=west]"
}
``` 

