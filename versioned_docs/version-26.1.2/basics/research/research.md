---
sidebar_position: 5
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

Hooks connect external events to research progress. Each hook triggers on an event and either grants a fact or increments a value.

| Hook Type | Event |
|-----------|-------|
| `entry_viewed_once` | A book entry is viewed for the first time |
| `item_crafted` | A specific item is crafted |
| `item_acquired` | A specific item appears in inventory |
| `advancement` | A vanilla advancement is earned |

### Toast Notifications

Research facts, values, nodes, and stages can show toast notifications when triggered. Toasts are defined with a title key, optional description key, optional icon, and optional static arguments.

## Generated JSON Structure

Research datagen produces these files per bundle:

- `facts.json` — array of `{ "id": "namespace/path" }`
- `values.json` — array of `{ "id": "namespace/path" }`
- `nodes.json` — node definitions with required_facts, required_values, stages, required_stages, and optional toast
- `hooks.json` — merged entry_viewed_once, item_crafted, and item_acquired hooks
- `advancement_hooks.json` — advancement-triggered hooks

## Runtime

The `ResearchStateManager` tracks per-player research state. Research hooks fire automatically via dedicated services (e.g. `AdvancementResearchHookService`). Toast notifications are sent from server to client via `ResearchToastMessage`.

## Further reading

- [Research Conditions](./conditions) — unlocking entries and categories via research nodes
- [Research Scenarios](./scenarios) — common patterns for advancement gating, item crafting, and more
- [Research Datagen](./datagen) — authoring research content via datagen
