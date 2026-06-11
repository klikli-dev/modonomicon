---
sidebar_position: 40
---

# Logic Conditions

Logic conditions allow you to combine or invert other conditions to create complex unlock logic.

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
              "type": "modonomicon:advancement",
              "advancement_id": "occultism:occultism/craft_dimensional_matrix"
          },
          {
              "type": "modonomicon:entry_unlocked",
              "entry_id": "modonomicon:features/condition_root"
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
              "type": "modonomicon:advancement",
              "advancement_id": "occultism:occultism/craft_dimensional_matrix"
          },
          {
              "type": "modonomicon:entry_unlocked",
              "entry_id": "modonomicon:features/condition_root"
          }
      ]
  },
  ...
}
```
