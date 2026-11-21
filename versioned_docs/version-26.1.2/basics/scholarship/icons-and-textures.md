---
sidebar_position: 4
---

# Icons and Textures

The scholarship block uses a set of built-in textures for its icons, group icons, decorations and buttons. You can replace any of them with a file of the same name, e.g. in a resource pack, or override the icon of a single element with its display data.

## Icons

Where a row has no icon of its own, the scholarship presenters use the default icon textures, in `assets/modonomicon/textures/gui/sprites/modonomicon/scholarship/icons/`:

| Texture | Used for |
|---------|----------|
| `condition_node_unlocked.png`, `condition_stage_completed.png` | Rows of research conditions |
| `trigger_advancement.png`, `trigger_entry_viewed.png` | Rows of advancement and entry-viewed hooks |
| `cost_levels.png` | Level costs |
| `effect_add_value.png`, `effect_experience.png`, `effect_function.png`, `effect_give_item.png`, `effect_grant_fact.png` | Effects of stages and actions |
| `stage_auto.png`, `stage_manual.png` | Automatic and manual stages |

Dynamic data, such as the item of an item cost, is shown instead of the texture. Icons of a row can also be set in its display data, see [Display Data](../research/display.md).

## Scholarship groups

The icons of the built-in [scholarship groups](./groups.md) are in `assets/modonomicon/textures/gui/sprites/modonomicon/scholarship/groups/`: `research.png`, `items.png`, `knowledge.png`, `progress.png` and `effects.png`. A custom research group sets its own icon as a texture identifier, see [Research Groups](../research/groups.md).

## Decorations and buttons

| Texture |
|---------|
| `themes/default/content/decorations/book/scholarship_row_line.png` |
| `themes/default/content/decorations/book/scholarship_separator.png` |
| `themes/default/content/icons/book/scholarship_check.png` |
| `themes/default/content/buttons/scholarship/complete_button_normal.png`, `..._hover.png`, `..._disabled.png` |

These are sprites of the `modonomicon` namespace, so the sprite id is for example `modonomicon:modonomicon/themes/default/content/buttons/scholarship/complete_button_normal`.
