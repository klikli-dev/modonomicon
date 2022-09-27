---
sidebar_position: 40
---

# Display (Only) Matcher

**Type:** `modonomicon:display`

Display matchers will render the configured block in the multiblock book page, but will match any blocks, including air.
Display matchers will not render in the in-world preview to avoid confusing players. 
Their main use is to provide background blocks for the multiblock when rendering in the book for better contrast and visibility.


## Attributes


* **display** (BlockState, _optional_)
   
  The BlockState to display in the multiblock preview. 

  :::info

  If you omit the BlockState properties (`[key=value]`), Modonomicon will display the Block's default BlockState.

  :::


## Usage Examples

```json
{
    "type": "modonomicon:display",
    "display": "minecraft:chest[facing=west]",
}
``` 