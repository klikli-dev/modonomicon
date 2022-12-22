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
    "item": "modonomicon:modonomicon",
    "nbt": {
      "modonomicon:book_id": "<your_mod_id>:<your_book_id>"
    }
  }
  ...
}
```

## Usage Examples

**Example:** A shapeless recipe for Theurgy's "The Hermetica":

```json 
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [
    {
      "item": "minecraft:book"
    },
    {
      "tag": "forge:sand"
    },
    {
      "tag": "forge:sand"
    }
  ],
  "result": {
    "item": "modonomicon:modonomicon",
    "nbt": {
      "modonomicon:book_id": "theurgy:the_hermetica"
    }
  }
}
```` 