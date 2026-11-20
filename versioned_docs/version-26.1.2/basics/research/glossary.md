---
sidebar_position: 19
---

# Glossary

The research and progression terms used across these docs.

**Trigger**: A kind of game event the research system listens to, identified by a trigger type id, e.g. `modonomicon:item_crafted`. A trigger has a target type (what it matches) and a context type (what happened). Built-in triggers are listed in [Research System](./research.md#hooks).

**Hook**: One authored link from a trigger target to a fact or value, e.g. "crafting a stick grants the stick fact". Hooks are stored in `hooks.json`. Authors create them with the ingress API, see [Research Datagen](./datagen.md).

**Fact**: A boolean research flag. It is granted once. Requirements of stages are facts.

**Value**: A numeric research counter. Hooks increment it, and requirements check a threshold.

**Node**: A research milestone that unlocks when its required facts and values are met. A node can have stages. An entry progression is a node.

**Stage**: One ordered step of a node. A stage has requirements and a completion, automatic or manual. Its rewards are granted when it completes.

**Requirement**: One thing a stage needs, shown as a row of a requirements block: a fact, a value, or an action-backed fact. Requirements are grouped into [categories](./categories.md).

**Cost**: What a player pays for an action or a manual stage completion. A cost is a list of typed entries, e.g. items, durability or levels. See [Actions, Costs and Effects](./actions.md#costs).

**Effect**: Something applied to the player on the server, e.g. granting a fact, adding to a value or giving an item. Effects are the building blocks of rewards and actions. See [Rewards and Effects](./rewards.md).

**Action**: A thing the player does to satisfy a fact: a cost, a list of effects, and the flags `grants_fact` and `repeatable`. An action is attached to a fact through its display. See [Actions, Costs and Effects](./actions.md).

**Reward**: An effect granted once when a stage completes. Stage rewards are a list of effects.

**Book condition**: A condition on a book element, such as an entry, category or page, that decides whether it is unlocked or visible. Research conditions such as `modonomicon:research_node_unlocked` are book conditions backed by research, see [Research Conditions](./conditions.md).

**Entry progression**: A staged progression that belongs to one entry. Its stages gate the pages and tabs of the entry and show a requirements block. Children of the entry unlock when the progression completes. See [Entry Progression](./entry-progression.md).

**Requirement category**: A group of requirement rows with its own icon, title and sort order, e.g. Items or Knowledge. See [Requirement Categories](./categories.md).

**Presenter**: The client-side code that draws a trigger, effect or cost as a row. See [Custom Presenters](../../advanced/custom-presenters.md).

**Display data**: The optional icon, title, description, category and action of a research element. See [Display Data](./display.md).
