---
sidebar_position: 5
---

# Book Crafting Recipes

## Recipe Result

To craft your book, you need a recipe that outputs an item of the type `modonomicon:modonomicon` with the NBT tag `"modonomicon:book_id":"<your_mod_id>:<your_book_id>"`:

```json 
{
  ...
  "result": {
    "id": "modonomicon:modonomicon",
    "components": {
      "modonomicon:book_id": "<your_mod_id>:<your_book_id>"
    }
  }
  ...
}
```

## Usage Examples

**Example:** A shapeless recipe for Modonomicon's demo book

```json 
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [
    {
      "item": "minecraft:book"
    },
    {
      "item": "minecraft:nether_star"
    }
  ],
  "result": {
    "id": "modonomicon:modonomicon",
    "components": {
      "modonomicon:book_id": "modonomicon:demo"
    }
  }
}
```` 