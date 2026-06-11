---
sidebar_position: 15
title: Research system and datagen
---

# Research system and datagen

26.1.2 introduces a research system that provides research nodes with multi-stage progression, numeric research values, toast notifications, and multiple trigger types for research ingress.

See [Research System](../../basics/research-system) for full documentation on facts, values, nodes, hooks, datagen API, configuration, and DataGenerators wiring.

## Migration notes

### Unlock conditions

Entry and category conditions now use research nodes instead of the old `entry_read`, `entry_unlocked`, and `advancement` condition types. See [Unlock Conditions](../../basics/unlock-conditions) for the available condition types and [Advancement Gating via Research](../../basics/unlock-conditions/advancement-condition) for the advancement pattern.
