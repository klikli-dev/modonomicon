---
sidebar_position: 60
---

# Research System

Research is the data graph and the server logic behind the book: it tracks what a player has achieved through facts, values, nodes, stages and hooks, and applies effects when things are completed. Research content is authored via datagen and compiled into JSON files under `data/<namespace>/modonomicon/research/<bundle_path>/`.

Research does not decide what the player sees by itself. Book conditions such as `research_node_unlocked` read research to unlock content (see [Research Conditions](../unlock-conditions/research-conditions.md)), and the scholarship layer draws research on book pages (see [Scholarship](../scholarship/scholarship.md)). The [Glossary](./glossary.md#condition-research-and-scholarship) explains the three.

## Core Concepts

### Facts

A fact is a boolean flag. It is either granted or not. Facts are declared with a path and referenced by a `ResearchFactRef`.

### Values

A value is a numeric counter. Values are incremented by hooks and checked against thresholds in nodes. Declared with a path and referenced by a `ResearchValueRef`.

### Nodes

A node represents a milestone in the research tree. Nodes require facts and/or values to be completed. Nodes can have stages for multi-step progression.

- **Required facts** — all listed facts must be granted
- **Required values** — all listed value thresholds must be met
- **Stages** — ordered steps within the node, each with their own requirements
- **Required stages** — dependencies on other nodes reaching specific stages

### Hooks

A hook is the data definition that connects an external event to research progress. Each hook is one entry of `hooks.json`: it names a trigger type (`trigger_type`), a target, and what it does (grant a fact or increment a value). Item hooks match either an item template (the item plus the components of the template) or an item tag.

A trigger is the event type a hook listens to, e.g. `item_crafted`. Many hooks can share one trigger type, and a trigger type fires every hook of that type whose target matches the event.

| Trigger Type | Event | Target Type |
|--------------|-------|-------------|
| `entry_viewed_once` | A book entry is viewed for the first time | `Identifier` (entry id) |
| `item_crafted` | A matching item is crafted | `ResearchItemMatcher`: item template (`{"id": ...}`) or tag (`{"tag": ...}`) |
| `item_acquired` | A matching item appears in inventory | `ResearchItemMatcher`: item template (`{"id": ...}`) or tag (`{"tag": ...}`) |
| `item_used` | A matching item is used in the air, see [Item Used](./item-used.md) | `ResearchItemMatcher`: item template (`{"id": ...}`) or tag (`{"tag": ...}`) |
| `advancement` | A vanilla advancement is earned | `Identifier` (advancement id) |

Third-party mods can register custom trigger types with arbitrary target and context types via `TriggerTypeRegistry.register()`.
See [Custom Research Triggers](../../advanced/custom-hooks) for details.

### Toast Notifications

Research facts, values, nodes, and stages can show toast notifications when triggered. Toasts are defined with a title key, optional description key, optional icon, and optional static arguments.

## Generated JSON Structure

Research datagen produces these files per bundle:

- `facts.json` — array of `{ "id": "namespace/path" }`
- `values.json` — array of `{ "id": "namespace/path" }`
- `nodes.json` — node definitions with required_facts, required_values, stages, required_stages, and optional toast
- `hooks.json` — all hooks (all trigger types in one file)

## Runtime pipeline

Research events are applied in batches. Each event goes through the same steps:

1. **Event.** Gameplay code reports what happened, e.g. an item was crafted, an advancement was earned or an entry was viewed, through `ResearchServices.hooks()`.
2. **Hook resolution.** The event is matched against the hooks of its trigger type. Hooks are indexed when the data loads, so an event only checks the hooks that can match it.
3. **Facts and values.** Each matching hook grants its fact or increments its value. Changes are collected per batch.
4. **Reevaluate.** Nodes whose required facts, values and stages are met unlock and start. Their stages advance while their requirements are met.
5. **Toasts.** The toasts for the facts, values, nodes and stages that changed are sent to the player together.
6. **Listeners.** Other systems are told whether the batch changed anything. The book uses this to update its visibility and unread state.

Changes that happen outside a hook batch, such as admin commands, are saved with the player's research state. The state is then sent to the client with `SyncResearchStateMessage`.

### Stages

A node's stages are completed in order. The next stage becomes the current one once the current stage is completed. In JSON and datagen, stages are referenced by their id: `required_stages` of a dependency uses `stage_id`, and the `research_stage_completed` condition uses `stage_id`. The 0-based stage index is internal to the player's research state. Only the `stage` command takes an index, as `stage <player> <node> <stageIndex>`.

## Further reading

- [Research Conditions](../unlock-conditions/research-conditions) — unlocking entries and categories via research nodes
- [Entry Research](./entry-research) — the staged research that belongs to one entry
- [Actions, Costs and Effects](./actions) and [Effects](./effects) — what research does when a player acts or a stage completes
- [Research Groups](./groups) — grouping research elements for display
- [Research Scenarios](./scenarios) — common patterns for advancement gating, item crafting, and more
- [Research Datagen](./datagen) — authoring research content via datagen
- [Custom Research Triggers](../../advanced/custom-hooks) — registering new trigger types for third-party mods
