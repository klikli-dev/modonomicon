---
sidebar_position: 17
---

# Display Data

Display data is the research data that describes how a research element looks in a scholarship block: its icon, title, description, research group and, for facts, an action. Every part is optional. A missing part falls back to a translation key, and then to the data of the hook that grants the element. The block itself is described in [Scholarship](../scholarship/scholarship.md).

## Fallbacks

For a fact row, the parts are chosen in this order:

| Part | Order |
|------|-------|
| Title | Display title, then the translation key `research_fact.<namespace>.<path>`, then the title of the hook, then the path of the fact |
| Icon | Display icon, then the icons of the hooks that grant the fact |
| Description | Display description, then the details of the hook |
| Group | Display group, then the trigger-type default and fallback, see [Scholarship Groups](../scholarship/groups.md#where-a-row-goes) |

Values work the same way with the `research_value.` prefix, but their rows take their icon from the hooks that increment them and have no description from hooks. Stages and nodes use `research_stage.` and `research_node.` keys for the names shown in condition tooltips, see the [Lang Key Reference](./lang-keys.md).

Where a row still has no icon of its own, the scholarship presenters use the default icon textures instead, see [Icons and Textures](../scholarship/icons-and-textures.md).

## Datagen

Use `ResearchDisplayModel` and attach it to a fact, value or node in your `ResearchSubProvider`:

```java
this.research().display(oakLog, ResearchDisplayModel.create()
        .withIcon(Items.OAK_LOG)
        .withTitle(OAK_LOG_TITLE));
```

`withTitle` takes a translation key or a component. `withIcon` takes an item, an item stack, a texture or a `BookIconModel`. `withGroup` and `withAction` set the group and the action.

Each `display(...)` call merges into the existing display. Only the parts you set change, so the icon survives a later call that only sets the title:

```java
this.research().display(dirt, ResearchDisplayModel.create()
        .withIcon(Items.DIRT)
        .withTitle(DIRT_TITLE));
```

To edit a display in place, pass an operator. It receives a copy of the existing display, or an empty one:

```java
this.research().display(dirt, d -> d.withDescription("Dirt you picked up"));
```

Calling `display` on a fact, value or node that was never declared throws an `IllegalArgumentException`.

The specs and builders that declare research (`ResearchFactSpec`, `ResearchValueSpec`, `ResearchNodeSpec`, `ResearchStageSpec` and `ResearchNodeBuilder`) take the display in the same way with `withDisplay(model)` or `withDisplay(operator)`. The bundle builder `this.research()` keeps `display(ref, ...)` for elements that are already declared.

Stage displays are part of the stage. In the entry research they are set by the builder, `title(...)`, `description(...)` and `icon(...)`, see [Entry Research](./entry-research.md#datagen).

## Merge

`ResearchDisplayModel.merge(other)` copies every part that `other` sets and keeps the others. `display(ref, model)` is `merge` on the existing display. So:

```java
// first call: icon and title
this.research().display(fact, ResearchDisplayModel.create().withIcon(Items.DIRT).withTitle("title.key"));
// second call: only the group, icon and title stay
this.research().display(fact, ResearchDisplayModel.create().withGroup(MY_GROUP));
```

Use `copy()` to get an independent model before changing a shared one.

## JSON

A display is written in the research bundle with the same parts. Titles and descriptions are text components, so translation keys are written as `translate` objects:

```json
{
  "id": "modonomicon:demo/research/paper_collected",
  "display": {
    "icon": { "id": "minecraft:paper" },
    "title": { "translate": "modonomicon.demo.research.paper_collected.title" },
    "group": "modonomicon:progress"
  }
}
```

Facts and values can carry an `action`, see [Actions, Costs and Effects](./actions.md) (values have no action in practice, since an action is only offered for facts). Nodes and stages take the same display data.

## Where display data appears

- Scholarship rows of stages, grouped into [scholarship groups](../scholarship/groups.md).
- Tooltips of conditions in [Research Conditions](../unlock-conditions/research-conditions.md), through the node and stage names.
- Toasts use their own toast definition, see [Research System](./research.md#toast-notifications).
