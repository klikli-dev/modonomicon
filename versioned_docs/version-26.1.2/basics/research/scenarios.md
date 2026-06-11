---
sidebar_position: 15
---

# Research Scenarios

Common patterns for modeling progression through the research system.

## Advancement gating

Gate an entry behind a vanilla advancement by creating a research fact that is granted when the advancement is earned.

```java
// In your ResearchSubProvider:
var fact = researchData.fact("mymod/advancement_gate");
researchData.grantFactOnAdvancementEarned("mymod/advancement_hook", advancementId, fact);
var node = researchData.node("mymod/advancement_gate_node", fact);

// In your entry datagen:
entry.withCondition(node);
```

## Item crafted gating

Gate an entry behind crafting a specific item.

```java
var fact = researchData.fact("mymod/crafted_gate");
researchData.grantFactOnItemCrafted("mymod/crafted_hook", Items.DIAMOND_SWORD, fact);
var node = researchData.node("mymod/crafted_gate_node", fact);

entry.withCondition(node);
```

With component matching (checks data components on the item):

```java
researchData.grantFactOnItemCrafted("mymod/crafted_hook",Items.DIAMOND_SWORD, fact, true);
```

## Item acquired gating

Gate an entry behind acquiring a specific item (inventory change detection).

```java
var fact = researchData.fact("mymod/acquired_gate");
researchData.grantFactOnItemAcquired("mymod/acquired_hook", Items.NETHER_STAR, fact);
var node = researchData.node("mymod/acquired_gate_node", fact);

entry.withCondition(node);
```

## Numeric progression with values

Track cumulative progress using research values instead of boolean facts.

```java
var value = researchData.value("mymod/mined_stone");

// Increment when stone is crafted
researchData.incrementValueOnItemCrafted("mymod/stone_hook", Items.COBBLESTONE, value, 1);

// Node requires 64 cobblestone mined
var node = researchData.node("mymod/mined_stone_node",
    List.of(),  // no fact requirements
    List.of(new ResearchNodeSpec.ValueRequirement(value, 64))
);

entry.withCondition(node);
```

## Multi-stage progression

Break a single node into multiple stages with separate requirements.

```java
var stage1Ref = researchData.stageRef("mymod/brewing_stage_1");
var stage2Ref = researchData.stageRef("mymod/brewing_stage_2");
var stage3Ref = researchData.stageRef("mymod/brewing_stage_3");

var fact1 = researchData.fact("mymod/brewing_basic");
var fact2 = researchData.fact("mymod/brewing_intermediate");
var fact3 = researchData.fact("mymod/brewing_advanced");

researchData.grantFactOnEntryViewedOnce("mymod/hook1", basicEntryId, fact1);
researchData.grantFactOnEntryViewedOnce("mymod/hook2", intermediateEntryId, fact2);
researchData.grantFactOnEntryViewedOnce("mymod/hook3", advancedEntryId, fact3);

var node = researchData.nodeBuilder(researchData.node("mymod/brewing_progress"))
    .withStage(ResearchStageSpec.factsOnly(stage1Ref, List.of(fact1)))
    .withStage(ResearchStageSpec.factsOnly(stage2Ref, List.of(fact2)))
    .withStage(ResearchStageSpec.factsOnly(stage3Ref, List.of(fact3)))
    .build();

// Gate entry behind stage 2 completion
entry.withCondition(node, stage2Ref);
```

## Toast notifications

Show a toast when a node is fully completed.

```java
var toast = new ResearchToastDefinition(
    "research_toast.mymod.node_unlocked",
    List.of(),
    "research_toast.modonomicon.research.node",
    Items.DIAMOND
);

researchData.nodeBuilder(researchData.node("mymod/my_node"))
    .withToast(toast)
    .build();
```

## Combining research conditions

While it is possible to combine multiple research conditions with `and`/`or`, it is usually better to model the combined logic as a single research node with multiple required facts or values. This keeps the condition tree simpler and gives players a single progress point to track.

```json
{
  "condition": {
      "type": "modonomicon:and",
      "children": [
          {
              "type": "modonomicon:research_node_unlocked",
              "node_id": "mymod:features/advanced_progression"
          },
          {
              "type": "modonomicon:research_stage_completed",
              "node_id": "mymod:features/my_node",
              "stage_id": "mymod:features/my_node/stage_1"
          }
      ]
  }
}
```
