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
Example: `minecraft:chest[facing=east]`

See https://minecraft.fandom.com/wiki/Commands/setblock for more information
  

## Block Matcher

**Type:** `modonomicon:block`

Block matchers match Blocks or BlockState and optionally display a different Block or BlockState.

**Attributes**:

* **block** (Block or BlockState, _mandatory_)

The Block or BlockState to match against when checking if a given block fits this matcher.

:::info

If you provide a Block, Modonomicon will match against it's default BlockState. 
This means in most cases you should use `"strict": false` or omit `strict` entirely, otherwise e.g. stairs will only match if they are in their default rotation.

:::

* **strict** (Boolean, _optional_)

Defaults to `false`.  
When `true` the matcher will only match blocks that have the exact block state specified in the `block` attribute.

* **display** (Block or BlockState, _optional_)

Defaults to the value of the `block` property.   
The block to display in the multiblock preview. 

:::info

If you provide a Block, Modonomicon will display it's default BlockState.

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

**Example 2:** Matching only west-facing chests, but displaying an east-facing chest

```json
{
    "type": "modonomicon:block",
    "display": "minecraft:chest[facing=east]",
    "block": "minecraft:chest[facing=west]",
    "strict": true
}
``` 