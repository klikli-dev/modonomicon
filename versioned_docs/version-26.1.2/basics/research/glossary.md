---
sidebar_position: 19
---

# Glossary

The terms used across these docs. The first section explains the three layers that most other terms belong to.

## Condition, research and scholarship

Modonomicon splits the logic of a book into three layers. Knowing which layer a term belongs to tells you where to look for it.

- **Condition**: the unlocking of categories, entries and pages. A condition decides whether the element is unlocked or visible, e.g. `modonomicon:research_node_unlocked` or `modonomicon:and`. See [Unlock Conditions](../unlock-conditions/unlock-conditions.md) and [Research Conditions](../unlock-conditions/research-conditions.md).
- **Research**: the data graph and the server logic. This covers facts, values, nodes, stages, hooks and triggers, actions, costs, effects, research groups, toasts and commands, and the link between an entry and its research. See [Research](./research.md).
- **Scholarship**: the UI and the player interaction on book pages. This covers the scholarship block and its rows, click interactions, icons, presenters, the scholarship display mode, the scholarship page type, feedback messages, and their lang keys and textures. See [Scholarship](../scholarship/scholarship.md).

A research node can unlock a condition, and a scholarship block shows the research of an entry. Neither one replaces the other: a condition checks research, and scholarship shows it.

## Terms

**Trigger**: The event type the research system listens to, identified by a trigger type id in the `trigger_type` field, e.g. `modonomicon:item_crafted`. A trigger type has a target type (what it matches) and a context type (what happened). Built-in triggers are listed in [Research](./research.md#hooks).

**Hook**: One data definition in `hooks.json` that connects a trigger type and a target to a fact or value, e.g. "crafting a stick grants the stick fact". A hook uses a trigger type, but a trigger type is not a hook: many hooks can use one trigger. Authors create hooks with `this.hook()`, see [Research Datagen](./datagen.md).

**Fact**: A boolean research flag. It is granted once. Requirements of stages are facts.

**Value**: A numeric research counter. Hooks increment it, and requirements check a threshold.

**Node**: A research milestone that unlocks when its required facts and values are met. A node can have stages. The research of an entry is a node.

**Stage**: One ordered step of a node. A stage has requirements and a completion, automatic or manual. Its effects are granted when it completes.

**Requirement**: One thing a stage needs: a fact, a value, or an action-backed fact. In the scholarship block, each requirement is shown as a row.

**Entry research**: The staged research that belongs to one entry. Its stages gate the pages and tabs of the entry and show a scholarship block. Children of the entry unlock when the research completes. See [Entry Research](./entry-research.md).

**Research group**: A named bucket of research elements with its own icon, title and sort order, e.g. Items or Knowledge. It is shown in the scholarship block as a scholarship group. See [Research Groups](./groups.md) and [Scholarship Groups](../scholarship/groups.md).

**Display data**: The optional icon, title, description, group and action of a research element. See [Display Data](./display.md).

**Action**: A thing the player does to satisfy a fact: a cost, a list of effects, and the flags `grants_fact` and `repeatable`. An action is attached to a fact through its display. See [Actions, Costs and Effects](./actions.md).

**Cost**: What a player pays for an action or a manual stage completion. A cost is a list of typed entries, e.g. items, durability or levels. See [Actions, Costs and Effects](./actions.md#costs).

**Effect**: Something applied to the player on the server, e.g. granting a fact, adding to a value or giving an item. Effects are the building blocks of stage effects and actions. See [Effects](./effects.md).

**Stage effect**: An effect granted once when a stage completes. Stage effects are a list of effects.

**Scholarship block**: The box that lists the rows of the current stage of an entry research. It appears where the [scholarship display](../scholarship/display.md) of the entry says.

**Scholarship row**: One line of a scholarship block, with an icon, a title, details and a met or not met state. Rows are grouped into [scholarship groups](../scholarship/groups.md).

**Scholarship group**: The section of a scholarship block that collects rows of one research group.

**Presenter**: The client-side code that draws a trigger, effect or cost as a scholarship row. See [Custom Presenters](../scholarship/custom-presenters.md).

**Book condition**: A condition on a book element, such as an entry, category or page, that decides whether it is unlocked or visible. Research conditions such as `modonomicon:research_node_unlocked` are book conditions backed by research, see [Research Conditions](../unlock-conditions/research-conditions.md).
