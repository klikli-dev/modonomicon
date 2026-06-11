---
sidebar_position: 15
---

# Research Scenarios

Common patterns for modeling progression through the research system.

:::note Research and book are separate classes
Research content is authored in a `SingleResearchSubProvider` subclass (e.g. `DemoResearch`), while book entries and categories are authored in a `BookProvider` subclass (e.g. `DemoBook`). They cannot directly reference each other's local variables.

The standard pattern is to declare `public static final` refs on the research class and import them in the book class:

```java
// In your ResearchSubProvider — declare static refs
public static final ResearchNodeRef MY_NODE = node("mymod/my_node");

// In your BookProvider — import and use
entry.withCondition(MyResearch.MY_NODE);
```

All scenarios below follow this pattern. See the [demo research](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java) for a complete working example.
:::

## Advancement gating

Gate an entry behind a vanilla advancement by creating a research fact that is granted when the advancement is earned.

```java
// In your ResearchSubProvider:
public static final ResearchNodeRef ADVANCEMENT_GATE = node("mymod/advancement_gate");

@Override
protected void generateResearch() {
    var fact = this.ingress()
        .onAdvancementEarned(this.mcLoc("story/mine_stone"))
        .declareFact("mymod/advancement_gate_fact");

    this.node(ADVANCEMENT_GATE, fact);
}
```

```java
// In your BookProvider — import the static ref:
entry.withCondition(MyResearch.ADVANCEMENT_GATE);
```

**Demo:** [`DemoResearch.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java) — `ADVANCEMENT_MINE_STONE` node, and [`ConditionAdvancementEntry.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/features/ConditionAdvancementEntry.java) for the book side.

## Item crafted gating

Gate an entry behind crafting a specific item.

```java
// In your ResearchSubProvider:
public static final ResearchNodeRef CRAFTED_GATE = node("mymod/crafted_gate");

@Override
protected void generateResearch() {
    var fact = this.ingress()
        .onItemCrafted(new ItemStackTemplate(Items.DIAMOND_SWORD))
        .declareFact("mymod/crafted_gate_fact");

    this.node(CRAFTED_GATE, fact);
}
```

```java
// In your BookProvider:
entry.withCondition(MyResearch.CRAFTED_GATE);
```

With component matching (checks data components on the item):

```java
this.ingress()
    .onItemCrafted(new ItemStackTemplate(Items.DIAMOND_SWORD))
    .grantFact("mymod/crafted_hook", factRef);
```

**Demo:** [`DemoResearch.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java) — `CRAFTING_STICK` node, and [`CraftingCategory.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/CraftingCategory.java).

## Item acquired gating

Gate an entry behind acquiring a specific item (inventory change detection).

```java
// In your ResearchSubProvider:
public static final ResearchNodeRef ACQUIRED_GATE = node("mymod/acquired_gate");

@Override
protected void generateResearch() {
    var fact = this.ingress()
        .onItemAcquired(new ItemStackTemplate(Items.NETHER_STAR))
        .declareFact("mymod/acquired_gate_fact");

    this.node(ACQUIRED_GATE, fact);
}
```

```java
// In your BookProvider:
entry.withCondition(MyResearch.ACQUIRED_GATE);
```

**Demo:** [`DemoResearch.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java) — `ACQUIRE_COBBLESTONE` node, and [`AcquiringCategory.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/AcquiringCategory.java).

## Numeric progression with values

Track cumulative progress using research values instead of boolean facts.

```java
// In your ResearchSubProvider:
public static final ResearchNodeRef MINED_STONE = node("mymod/mined_stone");

@Override
protected void generateResearch() {
    var value = this.value("mymod/mined_stone");

    // Increment when stone is crafted
    this.ingress()
        .onItemCrafted(new ItemStackTemplate(Items.COBBLESTONE))
        .incrementValue("mymod/stone_hook", value, 1);

    // Node requires 64 cobblestone mined
    this.node(MINED_STONE, List.of(), List.of(
        new ResearchNodeSpec.ValueRequirement(value, 64)
    ));
}
```

```java
// In your BookProvider:
entry.withCondition(MyResearch.MINED_STONE);
```

**Demo:** [`DemoResearch.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java) — `COLLECTOR_COMPLETE` node (requires viewing 3 collector entries), and [`ValuesCategory.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/ValuesCategory.java).

## Multi-stage progression

Break a single node into multiple stages with separate requirements.

```java
// In your ResearchSubProvider:
public static final ResearchNodeRef BREWING_PROGRESS = node("mymod/brewing_progress");

@Override
protected void generateResearch() {
    var stage1Ref = this.stageRef("mymod/brewing_stage_1");
    var stage2Ref = this.stageRef("mymod/brewing_stage_2");
    var stage3Ref = this.stageRef("mymod/brewing_stage_3");

    var fact1 = this.ingress()
        .onEntryViewedOnce(this.modLoc("entries/brewing_basic"))
        .declareFact("mymod/brewing_basic_fact");
    var fact2 = this.ingress()
        .onEntryViewedOnce(this.modLoc("entries/brewing_intermediate"))
        .declareFact("mymod/brewing_intermediate_fact");
    var fact3 = this.ingress()
        .onEntryViewedOnce(this.modLoc("entries/brewing_advanced"))
        .declareFact("mymod/brewing_advanced_fact");

    this.nodeBuilder(BREWING_PROGRESS)
        .withStage(ResearchStageSpec.factsOnly(stage1Ref, List.of(fact1)))
        .withStage(ResearchStageSpec.factsOnly(stage2Ref, List.of(fact2)))
        .withStage(ResearchStageSpec.factsOnly(stage3Ref, List.of(fact3)))
        .build();
}
```

```java
// In your BookProvider — gate behind stage 2 completion:
entry.withCondition(MyResearch.BREWING_PROGRESS, stage2Ref);
```

**Demo:** [`DemoResearch.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java) — `STAGES_DEMO` node (3 value-based stages) and `STAGES_DEPENDENT` (stage dependency), and [`StagesCategory.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/demo/StagesCategory.java).

## Toast notifications

Show a toast when a node is fully completed.

```java
var toast = new ResearchToastDefinition(
    "research_toast.mymod.node_unlocked",      // title key
    List.of(),                                  // title args
    "research_toast.mymod.description",         // description key
    new BookIcon(new ItemStackTemplate(Items.DIAMOND))  // icon
);

this.nodeBuilder(this.node("mymod/my_node"))
    .withToast(toast)
    .build();
```

Toasts can also be attached to individual stages:

```java
this.nodeBuilder(this.node("mymod/my_node"))
    .withStage(ResearchStageSpec.valuesOnly(stage1Ref, List.of(
        new ResearchNodeSpec.ValueRequirement(someValue, 10)
    )).toast(new ResearchToastDefinition(
        "research_toast.mymod.stage_progress",
        List.of(),
        "research_toast.mymod.stage_progress.desc",
        new BookIcon(new ItemStackTemplate(Items.IRON_INGOT))
    )))
    .build();
```

**Demo:** [`DemoResearch.java`](https://github.com/klikli-dev/modonomicon/blob/-/common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java) — `CRAFTING_STICK` node toast and `STAGES_DEMO` stage toasts.

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

## Custom trigger types

Need a trigger type not covered above (e.g. entity killed, biome entered)?
See [Custom Research Hooks](../../advanced/custom-hooks).
