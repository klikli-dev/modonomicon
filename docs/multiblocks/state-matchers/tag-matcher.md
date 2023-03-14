---
sidebar_position: 60
---

# Tag Matcher

**Type:** `modonomicon:tag`

Tag matchers will check if the placed block is part of the provided tag. Additionally BlockState properties can be provided to match against, in that case the matcher will only match if the block is in the tag, and has the provided properties.

## Attributes

* **tag** (Tag, _mandatory_)
  
  The Tag to match against when checking if a given block fits this matcher.   
  Tag string should be in the format `#namespace:tag_name` (note the `#`").   
  BlockState properties can be provided in square brackets: `[key=value]`


* **display** (BlockState, _optional_)
  The BlockState to display in the multiblock preview.   
  If none is provided, Modonomicon will cycle through the blocks in the tag.


  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will display the Block's default BlockState.

  :::


## Usage Examples

**Example 1:** Matching (and displaying) any planks

```json
{
  "type": "modonomicon:tag",
  "tag": "#minecraft:planks"
}
```

**Example 2:** Matching any wooden stairs facing west, but displaying a west-facing oak stair

```json
{
    "type": "modonomicon:block",
    "display": "minecraft:oak_stairs[facing=west]",
    "tag": "#minecraft:wooden_stairs[facing=west]"
}
``` 