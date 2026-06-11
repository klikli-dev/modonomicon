---
sidebar_position: 40
---

# Logic Conditions

Logic conditions allow you to combine or invert other conditions to create complex unlock logic.

:::tip

While it is possible to combine multiple research conditions with `and`/`or`, it is usually better to model the combined logic as a single research node with multiple required facts or values. This keeps the condition tree simpler and gives players a single progress point to track.

:::

## True Condition

**Condition type:** `modonomicon:true`

This condition will always be met. This exists mostly for debug or placeholder purposes.

### Attributes

None.

### Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:true"
  },
  ...
}
```

## False Condition

**Condition type:** `modonomicon:false`

This condition will never be met. This exists mostly for debug or placeholder purposes.

### Attributes

None.

### Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:false"
  },
  ...
}
```

## And Condition

**Condition type:** `modonomicon:and`

This condition will be met, if all conditions in the `children` array are met.
Can be chained with [`modonomicon:or`](#or-condition) to create complex unlock logic.

### Attributes

#### **children** (Condition[], _mandatory_)

The conditions that need to be met cumulatively to unlock this entry.

### Usage Examples

`<my-entry>.json` 
```json
{
  ...
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
  },
  ...
}
```

## Or Condition

**Condition type:** `modonomicon:or`

This condition will be met, if at least one of the conditions in the `children` array is met.
Can be chained with [`modonomicon:and`](#and-condition) to create complex unlock logic.

### Attributes

#### **children** (Condition[], _mandatory_)

The conditions that need to be met alternatively to unlock this entry.

### Usage Examples

`<my-entry>.json` 
```json
{
  ...
  "condition": {
      "type": "modonomicon:or",
      "children": [
          {
              "type": "modonomicon:research_node_unlocked",
              "node_id": "mymod:features/path_a"
          },
          {
              "type": "modonomicon:research_node_unlocked",
              "node_id": "mymod:features/path_b"
          }
      ]
  },
  ...
}
```
