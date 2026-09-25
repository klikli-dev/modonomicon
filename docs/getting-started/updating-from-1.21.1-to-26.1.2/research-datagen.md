---
sidebar_position: 15
title: Research system and datagen
---

# Research system and datagen

26.1.2 introduces a research system that provides research nodes with multi-stage progression, numeric research values, toast notifications, and multiple trigger types for research ingress.

See [Research System](../../basics/research) for full documentation.

## Migration notes

### Unlock conditions

Progression is now handled through the research system rather than standalone condition types. The old `entry_read`, `entry_unlocked`, and `advancement` condition types are replaced by research nodes and hooks:

- `modonomicon:research_node_unlocked` — gates behind a completed research node
- `modonomicon:research_stage_completed` — gates behind a specific stage of a research node

Advancement-based gating is modeled by creating a research fact that is granted when the advancement is earned, then gating the entry on a research node requiring that fact. See [Research Scenarios](../../basics/research/scenarios#advancement-gating) for the pattern.

Entry and category conditions now largely go through the research system. The [Unlock Conditions](../../basics/unlock-conditions) page lists the remaining non-research condition types (mod_loaded, category_has_visible_entries, logic combinators).
