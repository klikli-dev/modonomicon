---
sidebar_position: 20
---

# State Matchers

Each state matcher represents one block at one specific position in the multiblock, and depending on the type of matcher it may only allow one specific block, or a wide range of blocks in that position. 

State matchers are defined [in the `mappings` part of the multiblock definition](../defining-multiblocks/defining-multiblocks.md#mappings).

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
