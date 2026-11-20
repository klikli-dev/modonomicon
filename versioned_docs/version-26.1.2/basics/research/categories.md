---
sidebar_position: 16
---

# Requirement Categories

Requirement rows in a requirements block are grouped into categories. Each category is a row of the block, with its own icon, title and sort order. The built-in categories cover the common trigger types, and you can add your own.

## Built-in categories

| Category | Sort order | Rows it collects by default |
|----------|-----------:|-----------------------------|
| `modonomicon:research` | 0 | Facts without another category, and triggers without a claimed trigger type |
| `modonomicon:items` | 10 | `item_crafted`, `item_acquired`, and item and durability costs |
| `modonomicon:knowledge` | 20 | `entry_viewed_once`, `advancement`, `item_used` |
| `modonomicon:progress` | 30 | Values, e.g. collected counts and their progress bars, and level costs |
| `modonomicon:rewards` | 100 | Stage rewards, shown as met once the stage is complete |

Rewards always use the rewards category. Cost types without a presenter fall back to the items category.

## Where a row goes

A row is placed in the first category that matches, in this order:

1. **The display category.** A `category` set in the display data of the research element, e.g. the fact or value (see [Display Data](./display.md)).
2. **The trigger-type default.** The category that claims the trigger type of the row's hook. For example, a fact granted by an `item_crafted` hook goes to `items` unless another category claims that trigger.
3. **The fallback** of the row kind: research for facts, progress for values, and the rewards category for rewards.

Unknown category ids fall back to the research category and log a warning once.

## Declaring a category

Use `ResearchCategoryModel` in your `ResearchSubProvider`. The category is added to the research bundle, and the display of an element selects it:

```java
public static final Identifier DEMO_RITUALS = Modonomicon.loc("demo_rituals");

this.research().category(ResearchCategoryModel.create(DEMO_RITUALS)
        .withIcon(DEMO_RITUALS_ICON)
        .withTitle(DEMO_RITUALS_TITLE)
        .withDescription(DEMO_RITUALS_DESCRIPTION)
        .withSortOrder(15));

this.research().display(requirementsIronIngot,
        ResearchDisplayModel.create().withCategory(DEMO_RITUALS));
```

The demo gives the iron ingot of its staged requirements its own Rituals row, while the other requirements stay in their trigger categories.

`withTriggerTypes(...)` or `withTriggerTypeIds(...)` makes the category the default for those trigger types. The category then collects every hook row of those types that does not set its own display category. A category with the same id as a built-in one replaces it.

## JSON

A category is an entry in the `categories.json` of the research bundle. Its icon is a texture identifier, and its title and description are translation components. The display of a fact or value selects it with `category`:

```json
[
  {
    "id": "modonomicon:demo_rituals",
    "icon": "modonomicon:modonomicon/requirement_categories/demo_rituals",
    "sort_order": 15,
    "title": { "translate": "modonomicon.demo.category.rituals.title" },
    "description": { "translate": "modonomicon.demo.category.rituals.description" }
  }
]
```

```json
{
  "display": {
    "category": "modonomicon:demo_rituals"
  }
}
```

## Translation keys

The title key of a category is `research_category.<namespace>.<path>`, e.g. `research_category.modonomicon.items`. The description uses the same key with a `.description` suffix. The built-in categories ship these translations. A custom category uses whatever key you pass to `withTitle` and `withDescription`, so add those keys to your language file.
