---
sidebar_position: 60
---

# Research System

The research system tracks player progression through facts, values, nodes, and hooks. Research content is authored via datagen and compiled into JSON files under `data/<namespace>/modonomicon/research/<bundle_path>/`.

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

Hooks connect external events to research progress. Each hook triggers on an event and either grants a fact or increments a value. Item hooks match either an item template (the item plus the components of the template) or an item tag.

| Hook Type | Event | Target Type |
|-----------|-------|-------------|
| `entry_viewed_once` | A book entry is viewed for the first time | `Identifier` (entry id) |
| `item_crafted` | A matching item is crafted | `ResearchItemMatcher`: item template (`{"id": ...}`) or tag (`{"tag": ...}`) |
| `item_acquired` | A matching item appears in inventory | `ResearchItemMatcher`: item template (`{"id": ...}`) or tag (`{"tag": ...}`) |
| `item_used` | A matching item is used in the air, see [Item Used](./item-used.md) | `ResearchItemMatcher`: item template (`{"id": ...}`) or tag (`{"tag": ...}`) |
| `advancement` | A vanilla advancement is earned | `Identifier` (advancement id) |

Third-party mods can register custom trigger types with arbitrary target and context types via `TriggerTypeRegistry.register()`.
See [Custom Research Hooks](../../advanced/custom-hooks) for details.

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
- [Research Scenarios](./scenarios) — common patterns for advancement gating, item crafting, and more
- [Research Datagen](./datagen) — authoring research content via datagen
- [Custom Research Hooks](../../advanced/custom-hooks) — registering new trigger types for third-party mods
