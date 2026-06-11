---
sidebar_position: 30
---

# Advancement Gating via Research

Advancement-based gating is modeled through the research system. A research fact is granted when a vanilla advancement is earned, and the entry is gated on a research node requiring that fact.

## Datagen Example

```java
// In your ResearchSubProvider:
var fact = researchData.fact("mymod/advancement_gate");
researchData.grantFactOnAdvancementEarned("mymod/advancement_hook", advancementId, fact);
var node = researchData.node("mymod/advancement_gate_node", fact);

// In your entry datagen:
entry.withCondition(node);
```

## Generated JSON

The generated condition uses `modonomicon:research_node_unlocked`:

```json
{
  "condition": {
      "type": "modonomicon:research_node_unlocked",
      "node_id": "mymod:advancement_gate_node"
  }
}
```

## Available Hook Types

Research hooks support the following triggers:

| Hook | Description |
|------|-------------|
| `grantFactOnAdvancementEarned` | Grants a fact when a vanilla advancement is earned |
| `incrementValueOnAdvancementEarned` | Increments a value when a vanilla advancement is earned |
| `grantFactOnEntryViewedOnce` | Grants a fact when a book entry is viewed |
| `incrementValueOnEntryViewedOnce` | Increments a value when a book entry is viewed |
| `grantFactOnItemCrafted` | Grants a fact when an item is crafted |
| `incrementValueOnItemCrafted` | Increments a value when an item is crafted |
| `grantFactOnItemAcquired` | Grants a fact when an item is acquired |
| `incrementValueOnItemAcquired` | Increments a value when an item is acquired |
