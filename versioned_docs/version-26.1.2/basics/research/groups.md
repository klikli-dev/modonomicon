---
sidebar_position: 16
---

# Research Groups

A research group is a named bucket of research elements. Each group is defined in the research data and shows up in the scholarship block as one group row, with its own icon, title and sort order (see [Scholarship Groups](../scholarship/groups.md) for how rows are placed into groups). The built-in groups cover the common trigger types, and you can add your own.

## Built-in groups

| Group | Sort order | Rows it collects by default |
|-------|-----------:|-----------------------------|
| `modonomicon:research` | 0 | Facts without another group, and hooks whose trigger type no group claims |
| `modonomicon:items` | 10 | `item_crafted`, `item_acquired`, and item and durability costs |
| `modonomicon:knowledge` | 20 | `entry_viewed_once`, `advancement`, `item_used` |
| `modonomicon:progress` | 30 | Values, e.g. collected counts and their progress bars, and level costs |
| `modonomicon:effects` | 100 | Stage effects, shown as met once the stage is complete |

## Declaring a group

Use `ResearchGroupModel` in your `ResearchSubProvider`. The group is added to the research bundle, and the display of an element selects it with `withGroup`:

```java
public static final Identifier DEMO_SMITHING = Modonomicon.loc("demo_smithing");

this.research().group(ResearchGroupModel.create(DEMO_SMITHING)
        .withIcon(DEMO_SMITHING_ICON)
        .withTitle(DEMO_SMITHING_TITLE)
        .withDescription(DEMO_SMITHING_DESCRIPTION)
        .withSortOrder(15));

this.research().display(IRON_INGOT_ACQUIRED,
        ResearchDisplayModel.create().withGroup(DEMO_SMITHING));
```

The demo gives the iron ingot of its staged research its own smithing group, while the other elements stay in their trigger groups.

`withTriggerTypes(...)` or `withTriggerTypeIds(...)` makes the group the default for those trigger types. The group then collects every hook row of those types that does not set its own display group. A group with the same id as a built-in one replaces it.

## JSON

A group is an entry in the `groups.json` of the research bundle. Its icon is a texture identifier, and its title and description are translation components. The display of a fact or value selects it with `group`:

```json
[
  {
    "id": "modonomicon:demo_smithing",
    "icon": "modonomicon:modonomicon/scholarship/groups/demo_smithing",
    "sort_order": 15,
    "title": { "translate": "modonomicon.demo.group.smithing.title" },
    "description": { "translate": "modonomicon.demo.group.smithing.description" }
  }
]
```

```json
{
  "display": {
    "group": "modonomicon:demo_smithing"
  }
}
```

## Translation keys

The title key of a group is `research_group.<namespace>.<path>`, e.g. `research_group.modonomicon.items`. The description uses the same key with a `.description` suffix. The built-in groups ship these translations. A custom group uses whatever key you pass to `withTitle` and `withDescription`, so add those keys to your language file.

See [Lang Keys](./lang-keys.md) for the other research keys.
