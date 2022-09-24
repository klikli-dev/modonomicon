---
sidebar_position: 70
---

# Recipe Pages

![Recipe Pages](/img/docs/basics/page-types/recipe-pages.png)

**Page types:** 

* `modonomicon:crafting_recipe`
* `modonomicon:smelting_recipe`
* `modonomicon:blasting_recipe`
* `modonomicon:smoking_recipe`
* `modonomicon:campfire_cooking_recipe`
* `modonomicon:stonecutting_recipe`
* `modonomicon:smithing_recipe`

Displays one or two recipes, optionally titles for each recipe and an optional text.

:::tip

Each page type only supports recipes of the matching recipe type. If you want to display recipes of mixed types, use multiple recipe pages.

:::

## Attributes

* **title1** (DescriptionId or Component JSON, _optional_)

The title for the first recipe. Will not parse markdown, instead it uses the default title color as defined in the `book.json`.   

:::tip

If ommited, the recipe output's name will be used.

:::

<!-- TODO: link to book settings here -->

* **recipe_id_1** (ResourceLocation, _mandatory_)

The ResourceLocation of the first recipe to display. Make sure to match the recipe type to the page type.

* **title2** (DescriptionId or Component JSON, _optional_)

The title for the second recipe. Will not parse markdown, instead it uses the default title color as defined in the `book.json`.   

:::tip

If ommited, the recipe output's name will be used.

:::

<!-- TODO: link to book settings here -->

* **recipe_id_2** (ResourceLocation, _optional_)

The ResourceLocation of the second recipe to display. Make sure to match the recipe type to the page type.

* **text** (DescriptionId or Component JSON, _optional_)

The page text. Can be styled using markdown.

## Usage Examples

`<entry>.json`:

```json
{
  ...
  "pages": [
     {
      "type": "modonomicon:crafting_recipe",
      "anchor": "",
      "recipe_id_1": "minecraft:crafting_table",
      "recipe_id_2": "minecraft:oak_planks",
      "text": "book.modonomicon.demo.features.recipe.crafting.text",
      "title1": "",
      "title2": ""
    },
    {
      "type": "modonomicon:smelting_recipe",
      "anchor": "",
      "recipe_id_1": "minecraft:charcoal",
      "recipe_id_2": "minecraft:cooked_beef",
      "text": "",
      "title1": "",
      "title2": ""
    },
    {
      "type": "modonomicon:smoking_recipe",
      "anchor": "",
      "recipe_id_1": "minecraft:cooked_beef_from_smoking",
      "text": "book.modonomicon.demo.features.recipe.smoking.text",
      "title1": "",
      "title2": ""
    },
    {
      "type": "modonomicon:blasting_recipe",
      "anchor": "",
      "recipe_id_2": "minecraft:iron_ingot_from_blasting_iron_ore",
      "text": "",
      "title1": "",
      "title2": ""
    },
    {
      "type": "modonomicon:campfire_cooking_recipe",
      "anchor": "",
      "recipe_id_1": "minecraft:cooked_beef_from_campfire_cooking",
      "text": "",
      "title1": "",
      "title2": ""
    },
    {
      "type": "modonomicon:stonecutting_recipe",
      "anchor": "",
      "recipe_id_1": "minecraft:andesite_slab_from_andesite_stonecutting",
      "text": "",
      "title1": "",
      "title2": ""
    },
    {
      "type": "modonomicon:smithing_recipe",
      "anchor": "",
      "recipe_id_1": "minecraft:netherite_axe_smithing",
      "text": "",
      "title1": "",
      "title2": ""
    }
  ]
}
```  

`/lang/*.json`:

```json
{
  "book.modonomicon.demo.features.recipe.crafting.text": "A sample recipe page.",
  "book.modonomicon.demo.features.recipe.smoking.text": "A smoking recipe page with one recipe and some text.",
}
```
