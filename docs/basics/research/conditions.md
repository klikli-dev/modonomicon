---
sidebar_position: 10
---

# Research Conditions

Entries and categories can be gated behind research nodes using condition types. These conditions check the player's research state.

## Research Node Unlocked

**Condition type:** `modonomicon:research_node_unlocked`

This condition is met when the specified research node is fully completed (all stages finished) for the player.

### Attributes

#### **node_id** (Identifier, _mandatory_)

The Identifier of the research node that must be fully completed.

#### **tooltip** (DescriptionId or Component JSON, _optional_)

The tooltip to display when hovering over the locked entry. If omitted, a tooltip is auto-generated from the node's description ID.

### Example

```json
{
  "condition": {
      "type": "modonomicon:research_node_unlocked",
      "node_id": "mymod:features/condition_root"
  }
}
```

## Research Stage Completed

**Condition type:** `modonomicon:research_stage_completed`

This condition is met when a specific stage within a research node is completed for the player. Use this to gate entries behind partial progress in multi-stage nodes.

### Attributes

#### **node_id** (Identifier, _mandatory_)

The Identifier of the research node containing the stage.

#### **stage_id** (Identifier, _mandatory_)

The Identifier of the stage that must be completed.

#### **tooltip** (DescriptionId or Component JSON, _optional_)

The tooltip to display when hovering over the locked entry. If omitted, a tooltip is auto-generated from the stage's description ID.

### Example

```json
{
  "condition": {
      "type": "modonomicon:research_stage_completed",
      "node_id": "mymod:features/my_node",
      "stage_id": "mymod:features/my_node/stage_1"
  }
}
```

## Datagen shortcuts

When using datagen, `BookEntryModel`, `BookCategoryModel`, and `BookPageModel` provide shorthand methods:

```java
// Gate behind a completed node
entry.withCondition(researchNodeRef);

// Gate behind a specific stage
entry.withCondition(researchNodeRef, researchStageRef);

// Gate a category
category.withCondition(researchNodeRef);
```

## Parent-to-child progression

When using datagen, `BookModel.withGenerateEntryHierarchyResearch(true)` automatically generates `entry_viewed_once` hooks and research nodes for entries that have parents. This produces the correct `BookResearchNodeUnlockedCondition` on each entry without manual condition work.

If you need fine-grained control, create research nodes and hooks manually in your `ResearchSubProvider`.
