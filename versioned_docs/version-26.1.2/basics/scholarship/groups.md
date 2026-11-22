---
sidebar_position: 3
---

# Scholarship Groups

The scholarship block of a page groups its rows into research groups. Each group is one section of the block, with its icon, title and sort order. The groups themselves are declared as research data, see [Research Groups](../research/groups.md). This page describes how rows are placed into them when the block is drawn.

## Where a row goes

A row is placed in the first group that matches, in this order:

1. **The display group.** A `group` set in the display data of the research element, e.g. the fact or value (see [Display Data](../research/display.md)).
2. **The trigger-type default.** The group that claims the trigger type of the row's hook. For example, a fact granted by an `item_crafted` hook goes to `items` unless another group claims that trigger.
3. **The fallback** of the row kind: research for facts, progress for values, and the effects group for effects.

Unknown group ids fall back to the research group and log a warning once.

Effects always use the effects group. Cost types without a presenter fall back to the items group.

## Rendering

- The rows are grouped by their group. Groups are ordered by their sort order, and groups with the same sort order by their id.
- The stage line and rows of a page's scholarship block are described in [Scholarship](./scholarship.md).

## Built-in groups

The built-in groups are `modonomicon:research`, `items`, `knowledge`, `progress` and `effects`. Their icons are in `assets/modonomicon/textures/gui/sprites/modonomicon/scholarship/groups/`, see [Icons and Textures](./icons-and-textures.md).

Custom presenters decide the group of their rows only through the caller, see [Custom Presenters](./custom-presenters.md#trigger-presenters).
