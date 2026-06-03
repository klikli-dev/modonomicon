# Plan: Research Values — Runtime + Demo Scenario

## Goal
Add research values as primitive durable progression inputs. Values are numeric counters that hooks can increment and nodes can require reaching a threshold. Prove the system with a demo scenario using 3 entries that each increment the same value, with a 4th entry unlocking when the threshold is reached.

## Backwards Compat
None. This adds new capabilities; no existing hooks or nodes use values yet.

---

## Design Decisions
- Hooks are single-purpose: a hook either grants a fact OR increments a value, never both
- Node unlock requires ALL facts present AND ALL values at threshold
- Value increment amount is authored per hook (default 1)
- Value threshold is authored per node requirement

---

## File Changes

### 1. `common/.../research/data/ResearchValueDefinition.java` — **NEW**

Canonical definition for one research value.

```java
public record ResearchValueDefinition(Identifier id) {
    // CODEC: single "id" field
}
```

Emitted to `values.json`.

---

### 2. `common/.../research/data/ResearchHookDefinition.java` — **MODIFY**

Make `factId` optional. Add `valueId` (nullable) and `increment` (default 1).

```java
public record ResearchHookDefinition(
    Identifier id,
    TriggerType triggerType,
    Identifier triggerTargetId,
    Identifier factId,          // nullable now
    Identifier valueId,         // nullable, new
    int increment               // new, default 1
)
```

JSON shape:
```json
{
  "id": "...",
  "trigger_type": "entry_viewed_once",
  "event_target_id": "...",
  "fact_id": null,
  "value_id": "modonomicon:demo/collector_count",
  "increment": 1
}
```

Validation (in `ResearchData.validate`): exactly one of `factId` or `valueId` must be non-null.

---

### 3. `common/.../research/data/ResearchNodeDefinition.java` — **MODIFY**

Add `requiredValues` field.

```java
public record ResearchNodeDefinition(
    Identifier id,
    List<Identifier> requiredFacts,
    List<ValueRequirement> requiredValues   // new
) {
    public record ValueRequirement(Identifier valueId, int threshold) {}
}
```

JSON shape:
```json
{
  "id": "modonomicon:demo/collector_complete",
  "required_facts": [],
  "required_values": [
    { "value_id": "modonomicon:demo/collector_count", "threshold": 3 }
  ]
}
```

---

### 4. `common/.../research/data/ResearchData.java` — **MODIFY**

- Add `Set<Identifier> valueIds` to the record
- Add value rules to node rules
- In `validate`:
  - Validate value ids are unique
  - Validate node value requirements reference known values
  - Validate hook value targets reference known values
- Update `NodeRule` to include value requirements

---

### 5. `common/.../research/data/ResearchDataManager.java` — **MODIFY**

- Parse `values.json` files during `apply()`
- Pass value definitions to `ResearchData.validate()`

---

### 6. `common/.../research/state/PlayerResearchState.java` — **MODIFY**

Add value storage:

```java
private final Map<Identifier, Integer> values;  // new

public int incrementValue(Identifier valueId, int amount)
public int getValue(Identifier valueId)
```

Update CODEC and STREAM_CODEC to serialize/deserialize the values map.

---

### 7. `common/.../research/state/ResearchStateManager.java` — **MODIFY**

- Add `incrementValue(ServerPlayer, Identifier, int)` method
- Update `reevaluate` to also check value thresholds

---

### 8. `common/.../research/hook/ResearchHookService.java` — **MODIFY**

Update `onEntryViewedOnce` to also process value-increment hooks, dispatching based on whether the hook targets a fact or a value.

---

### 9. `common/.../api/datagen/research/ResearchValueRef.java` — **NEW**

Typed ref for value ids, matching the pattern of `ResearchFactRef` and `ResearchNodeRef`.

---

### 10. `common/.../api/datagen/research/ResearchDataBuilder.java` — **MODIFY**

Add value authoring:

```java
public ResearchValueRef value(String path)
public ResearchNodeRef node(String path, ResearchFactRef[] requiredFacts, ValueRequirement... requiredValues)
public List<ResearchValueDefinition> valueDefinitions()
```

---

### 11. `common/.../api/datagen/research/ResearchIngressHelper.java` — **MODIFY**

Add value mutation to ingress steps:

```java
public ResearchValueRef declareValue(String valuePath, int increment)
public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment)
```

---

### 12. `common/.../api/datagen/research/ResearchProvider.java` — **MODIFY**

Add `values.json` to the save list in `run()`.

---

### 13. `common/.../api/datagen/research/ResearchBundle.java` — **MODIFY**

Add `valueDefinitions()` to the bundle record.

---

### 14. `common/.../datagen/research/DemoResearch.java` — **MODIFY**

Add value demo scenario: 3 entry-viewed hooks that increment the same value, 1 node that requires the value at threshold 3.

---

### 15. `common/.../datagen/book/demo/values/` — **NEW DIRECTORY**

4 demo entries: `CollectorAEntry`, `CollectorBEntry`, `CollectorCEntry` (always visible, each increments value), `CollectorCompleteEntry` (gated by value threshold node).

---

### 16. `common/.../datagen/book/DemoBook.java` — **MODIFY**

Register the new values category.

---

## Demo Scenario

| Entry | Research Effect | Visibility |
|---|---|---|
| `values/collector_a` | increment `collector_count` by 1 | always visible |
| `values/collector_b` | increment `collector_count` by 1 | always visible |
| `values/collector_c` | increment `collector_count` by 1 | always visible |
| `values/collector_complete` | none | visible when `collector_count >= 3` |

---

## Validation Rules

1. Value ids are unique
2. Node value requirements reference known values
3. Hook value targets reference known values
4. Each hook has exactly one of `factId` or `valueId` non-null
5. Hook increment amount > 0
6. Node value threshold > 0

---

## Summary: ~20 file changes (17 files + 4 new entry files + 1 spec)
