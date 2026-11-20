---
sidebar_position: 17
---

# Display Data

Display data controls how a research element looks in a requirements block: its icon, title, description, requirement category and, for facts, an action. Every part is optional. A missing part falls back to a translation key, and then to the data of the hook that grants the element.

## Fallbacks

For a fact row, the parts are chosen in this order:

| Part | Order |
|------|-------|
| Title | Display title, then the translation key `research_fact.<namespace>.<path>`, then the title of the hook, then the path of the fact |
| Icon | Display icon, then the icons of the hooks that grant the fact |
| Description | Display description, then the details of the hook |
| Category | See [Requirement Categories](./categories.md#where-a-row-goes) |

Values work the same way with the `research_value.` prefix, but their rows take their icon from the hooks that increment them and have no description from hooks. Stages and nodes use `research_stage.` and `research_node.` keys for the names shown in condition tooltips, see the [Lang Key Reference](./lang-keys.md).

Where a row still has no icon of its own, the built-in presenters use the default research icon textures instead, in `assets/modonomicon/textures/gui/sprites/modonomicon/research_icons/`. These are used for levels costs (`cost_levels.png`), experience rewards (`effect_experience.png`), value rewards (`effect_add_value.png`), granted-fact rewards (`effect_grant_fact.png`) and similar fallbacks. Each texture can be replaced by a file with the same name in the `modonomicon` assets, e.g. by a resource pack. Dynamic data, such as the item of an item cost, is shown instead of the texture.

## Datagen

Use `ResearchDisplayModel` and attach it to a fact, value or node in your `ResearchSubProvider`:

```java
this.research().display(requirementsOakLog, ResearchDisplayModel.create()
        .withIcon(Items.OAK_LOG)
        .withTitle(REQUIREMENTS_OAK_LOG_TITLE));
```

`withTitle` takes a translation key or a component. `withIcon` takes an item, an item stack, a texture or a `BookIconModel`. `withCategory` and `withAction` set the category and the action.

Each `display(...)` call merges into the existing display. Only the parts you set change, so the icon survives a later call that only sets the title:

```java
this.research().display(progressionDirt, ResearchDisplayModel.create()
        .withIcon(Items.DIRT)
        .withTitle(PROGRESSION_DIRT_TITLE));
```

To edit a display in place, pass an operator. It receives a copy of the existing display, or an empty one:

```java
this.research().display(progressionDirt, d -> d.withDescription("Dirt you picked up"));
```

Calling `display` on a fact, value or node that was never declared throws an `IllegalArgumentException`.

The specs and builders that declare research (`ResearchFactSpec`, `ResearchValueSpec`, `ResearchNodeSpec`, `ResearchStageSpec` and `ResearchNodeBuilder`) take the display in the same way with `withDisplay(model)` or `withDisplay(operator)`. The bundle builder `this.research()` keeps `display(ref, ...)` for elements that are already declared.

Stage displays are part of the stage. In the entry progression they are set by the builder, `title(...)`, `description(...)` and `icon(...)`, see [Entry Progression](./entry-progression.md#datagen).

## Merge

`ResearchDisplayModel.merge(other)` copies every part that `other` sets and keeps the others. `display(ref, model)` is `merge` on the existing display. So:

```java
// first call: icon and title
this.research().display(fact, ResearchDisplayModel.create().withIcon(Items.DIRT).withTitle("title.key"));
// second call: only the category, icon and title stay
this.research().display(fact, ResearchDisplayModel.create().withCategory(MY_CATEGORY));
```

Use `copy()` to get an independent model before changing a shared one.

## JSON

A display is written in the research bundle with the same parts. Titles and descriptions are text components, so translation keys are written as `translate` objects:

```json
{
  "id": "modonomicon:demo/progression_dirt_collected",
  "display": {
    "icon": { "id": "minecraft:dirt" },
    "title": { "translate": "modonomicon.demo.progression.dirt.title" },
    "category": "modonomicon:progress"
  }
}
```

Facts and values can carry an `action`, see [Actions, Costs and Effects](./actions.md) (values have no action in practice, since an action is only offered for facts). Nodes and stages take the same display data.

## Where display data appears

- Requirement rows of stages, grouped into [categories](./categories.md).
- Tooltips of conditions in [Research Conditions](./conditions.md), through the node and stage names.
- Toasts use their own toast definition, see [Research System](./research.md#toast-notifications).
