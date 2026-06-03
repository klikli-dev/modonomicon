# Research Stages Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add stages as an optional, ordered sequence of sub-goals within a research node, supporting node-to-node stage dependencies so players progressively unlock more content as they advance.

**Architecture:** Stages are defined inline in `ResearchNodeDefinition`. Each stage has its own fact/value requirements. Nodes can depend on other nodes reaching specific stages. Automatic advancement on requirement satisfaction. New `BookResearchStageCompletedCondition` gates book entries on stage completion.

**Tech Stack:** Java 21, Minecraft 26.x NeoForge/Fabric, Mojang Codec/StreamCodec, fastutil collections

---

## File Map

| File | Responsibility |
|---|---|
| `research/data/ResearchStageDefinition.java` | **NEW** — Stage data model (id, requiredFacts, requiredValues) |
| `research/data/ResearchNodeDefinition.java` | ADD `stages` + `requiredStages` fields, codec, StageDependency record |
| `research/data/ResearchData.java` | ADD `nodeStageRules`, modify `NodeRule` with `requiredStageDependencies`, validation, `stageLocations` |
| `research/state/PlayerResearchState.java` | ADD `nodeStageIndexes` map + accessor/mutator methods |
| `research/state/ResearchStateManager.java` | ADD stage advancement in `reevaluate()`, `isStageCompleted()`, `setNodeStage()` |
| `research/data/ResearchDataManager.java` | Update default instance for new ResearchData fields |
| `book/conditions/BookResearchStageCompletedCondition.java` | **NEW** — Condition gating entries on stage completion |
| `book/conditions/BookResearchNodeUnlockedCondition.java` | MODIFY to check all stages complete for multi-stage nodes |
| `registry/BookConditionTypeRegistry.java` | ADD registration of stage completed condition |
| `api/datagen/research/ResearchStageRef.java` | **NEW** — Typed authoring ref for stages |
| `api/datagen/research/ResearchStageSpec.java` | **NEW** — Authoring-time stage declaration |
| `api/datagen/research/ResearchNodeSpec.java` | ADD `stages` field, `requiredStages` field, `toDefinition()` update |
| `api/datagen/research/ResearchDataBuilder.java` | ADD `stageRef()`, `node()` overloads for stages |
| `api/datagen/research/ResearchProviderBase.java` | ADD `stageRef()` and `node()` with stages convenience methods |
| `api/datagen/book/condition/BookResearchStageCompletedConditionModel.java` | **NEW** — Datagen model for stage condition |
| `api/ModonomiconConstants.java` | ADD tooltip + command i18n keys |
| `command/SetStageCommand.java` | **NEW** — Command to set node stage index |
| `command/ResearchCommand.java` | ADD `set stage` subcommand |
| `datagen/research/DemoResearch.java` | ADD stages demo scenario |

---

## Task 1: ResearchStageDefinition — Stage Data Model

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchStageDefinition.java`

A stage is an ordered sub-goal within a node. Each stage has its own fact/value requirements. When met, the player auto-advances.

- [ ] **Step 1: Create ResearchStageDefinition record**

```java
/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Defines one sequential stage within a multi-stage research node.
 *
 * @param id unique identifier for this stage (used by conditions and stage dependencies)
 * @param requiredFacts fact ids that must be granted to advance past this stage
 * @param requiredValues value requirements that must be met to advance past this stage
 */
public record ResearchStageDefinition(
        Identifier id,
        List<Identifier> requiredFacts,
        List<ResearchNodeDefinition.ValueRequirement> requiredValues
) {
    public static final Codec<ResearchStageDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchStageDefinition::id),
            Identifier.CODEC.listOf().optionalFieldOf("required_facts", List.of()).forGetter(ResearchStageDefinition::requiredFacts),
            ResearchNodeDefinition.ValueRequirement.CODEC.listOf().optionalFieldOf("required_values", List.of()).forGetter(ResearchStageDefinition::requiredValues)
    ).apply(instance, ResearchStageDefinition::new));
}
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS (no errors)

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchStageDefinition.java
git commit -m "feat(research): add ResearchStageDefinition data model"
```

---

## Task 2: ResearchNodeDefinition — Add Stages and Stage Dependencies

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchNodeDefinition.java`

Nodes gain two new fields: `stages` (ordered sub-goals) and `requiredStages` (dependencies on other nodes reaching specific stages). A `StageDependency` references another node's stage.

- [ ] **Step 1: Replace ResearchNodeDefinition with stage-aware version**

```java
/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Defines a research node that unlocks when all required facts are present
 * AND all required values have reached their thresholds AND all required stage
 * dependencies on other nodes are satisfied.
 *
 * Multi-stage nodes have an ordered list of stages. The node activates when base
 * requirements are met, then progresses through stages automatically. The node is
 * fully complete only when all stages are done.
 *
 * @param id unique identifier for this node
 * @param requiredFacts fact ids that must be granted for this node to activate
 * @param requiredValues value requirements that must be met for this node to activate
 * @param stages ordered stages; empty list means single-stage (node activates = complete)
 * @param requiredStages dependencies on other nodes reaching specific stages
 */
public record ResearchNodeDefinition(
        Identifier id,
        List<Identifier> requiredFacts,
        List<ValueRequirement> requiredValues,
        List<ResearchStageDefinition> stages,
        List<StageDependency> requiredStages
) {

    /**
     * Dependency on another node reaching a specific stage.
     *
     * @param nodeId the node that must reach the specified stage
     * @param stageId the stage within that node that must be completed
     */
    public record StageDependency(Identifier nodeId, Identifier stageId) {
        public static final Codec<StageDependency> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("node_id").forGetter(StageDependency::nodeId),
                Identifier.CODEC.fieldOf("stage_id").forGetter(StageDependency::stageId)
        ).apply(instance, StageDependency::new));
    }

    public record ValueRequirement(Identifier valueId, int threshold) {
        public static final Codec<ValueRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("value_id").forGetter(ValueRequirement::valueId),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("threshold").forGetter(ValueRequirement::threshold)
        ).apply(instance, ValueRequirement::new));
    }

    public static final Codec<ResearchNodeDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ResearchNodeDefinition::id),
            Identifier.CODEC.listOf().optionalFieldOf("required_facts", List.of()).forGetter(ResearchNodeDefinition::requiredFacts),
            ValueRequirement.CODEC.listOf().optionalFieldOf("required_values", List.of()).forGetter(ResearchNodeDefinition::requiredValues),
            ResearchStageDefinition.CODEC.listOf().optionalFieldOf("stages", List.of()).forGetter(ResearchNodeDefinition::stages),
            StageDependency.CODEC.listOf().optionalFieldOf("required_stages", List.of()).forGetter(ResearchNodeDefinition::requiredStages)
    ).apply(instance, ResearchNodeDefinition::new));
}
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchNodeDefinition.java
git commit -m "feat(research): add stages and stage dependencies to ResearchNodeDefinition"
```

---

## Task 3: ResearchData — Stage Rules, Stage Locations, and Validation

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java`

Add `NodeStageRule` for stage advancement rules, `StageDependency` reference in `NodeRule`, `StageLocation` for fast stage ID lookup, and validation for stage IDs and cross-references.

- [ ] **Step 1: Replace ResearchData with stage-aware version**

```java
/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Validated research data loaded from JSON resources.
 *
 * @param factIds set of all known fact ids
 * @param nodeIds set of all known node ids
 * @param valueIds set of all known value ids
 * @param stageIds set of all known stage ids
 * @param nodeRules node activation rules (facts + values + stage dependencies)
 * @param nodeStageRules stage advancement rules per node
 * @param entryViewedOnceHooks hooks grouped by trigger target entry id
 * @param itemCraftedHooks hooks grouped by item id
 * @param itemAcquiredHooks hooks grouped by item id
 * @param advancementHooks hooks grouped by advancement id
 * @param graphFactIds facts grouped by graph id
 * @param graphNodeIds nodes grouped by graph id
 * @param graphValueIds values grouped by graph id
 * @param stageLocations maps stage id to its owning node and index for fast lookup
 */
public record ResearchData(
        Set<Identifier> factIds,
        Set<Identifier> nodeIds,
        Set<Identifier> valueIds,
        Set<Identifier> stageIds,
        List<NodeRule> nodeRules,
        List<NodeStageRule> nodeStageRules,
        Map<Identifier, List<ResearchHookDefinition>> entryViewedOnceHooks,
        Map<Identifier, List<ResearchHookDefinition>> itemCraftedHooks,
        Map<Identifier, List<ResearchHookDefinition>> itemAcquiredHooks,
        Map<Identifier, List<AdvancementResearchHookDefinition>> advancementHooks,
        Map<Identifier, Set<Identifier>> graphFactIds,
        Map<Identifier, Set<Identifier>> graphNodeIds,
        Map<Identifier, Set<Identifier>> graphValueIds,
        Map<Identifier, StageLocation> stageLocations
) {

    public static ResearchData validate(
            List<ResearchFactDefinition> facts,
            List<ResearchNodeDefinition> nodes,
            List<ResearchValueDefinition> values,
            List<ResearchHookDefinition> hooks,
            List<AdvancementResearchHookDefinition> advancementHooks,
            Map<Identifier, Set<Identifier>> graphFactIds,
            Map<Identifier, Set<Identifier>> graphNodeIds,
            Map<Identifier, Set<Identifier>> graphValueIds
    ) {
        var factIds = uniqueIds(facts.stream().map(ResearchFactDefinition::id).toList(), "fact");
        var nodeIds = uniqueIds(nodes.stream().map(ResearchNodeDefinition::id).toList(), "node");
        var valueIds = uniqueIds(values.stream().map(ResearchValueDefinition::id).toList(), "value");
        ensureUniqueIds(hooks.stream().map(ResearchHookDefinition::id).toList(), "hook");
        ensureUniqueIds(advancementHooks.stream().map(AdvancementResearchHookDefinition::id).toList(), "advancement hook");

        // Collect and validate stage ids
        var allStageIds = new ArrayList<Identifier>();
        for (var node : nodes) {
            for (var stage : node.stages()) {
                allStageIds.add(stage.id());
            }
        }
        var stageIds = uniqueIds(allStageIds, "stage");

        // Validate hooks have exactly one of factId or valueId
        for (var hook : hooks) {
            boolean hasFact = hook.factId() != null;
            boolean hasValue = hook.valueId() != null;
            if (!hasFact && !hasValue) {
                throw new IllegalArgumentException("Hook '" + hook.id() + "' must have exactly one of fact_id or value_id");
            }
            if (hasFact && hasValue) {
                throw new IllegalArgumentException("Hook '" + hook.id() + "' cannot have both fact_id and value_id");
            }
            if (hasFact && !factIds.contains(hook.factId())) {
                throw new IllegalArgumentException("Unknown fact '" + hook.factId() + "' referenced by hook '" + hook.id() + "'");
            }
            if (hasValue && !valueIds.contains(hook.valueId())) {
                throw new IllegalArgumentException("Unknown value '" + hook.valueId() + "' referenced by hook '" + hook.id() + "'");
            }
        }

        // Validate advancement hooks
        for (var hook : advancementHooks) {
            boolean hasFact = hook.factId() != null;
            boolean hasValue = hook.valueId() != null;
            if (!hasFact && !hasValue) {
                throw new IllegalArgumentException("Advancement hook '" + hook.id() + "' must have exactly one of fact_id or value_id");
            }
            if (hasFact && hasValue) {
                throw new IllegalArgumentException("Advancement hook '" + hook.id() + "' cannot have both fact_id and value_id");
            }
            if (hasFact && !factIds.contains(hook.factId())) {
                throw new IllegalArgumentException("Unknown fact '" + hook.factId() + "' referenced by advancement hook '" + hook.id() + "'");
            }
            if (hasValue && !valueIds.contains(hook.valueId())) {
                throw new IllegalArgumentException("Unknown value '" + hook.valueId() + "' referenced by advancement hook '" + hook.id() + "'");
            }
        }

        // Validate node requirements and build rules
        var nodeRules = new ArrayList<NodeRule>();
        var nodeStageRules = new ArrayList<NodeStageRule>();
        var stageLocations = new HashMap<Identifier, ResearchData.StageLocation>();

        for (var node : nodes) {
            // Validate base fact requirements
            for (var factId : node.requiredFacts()) {
                if (!factIds.contains(factId)) {
                    throw new IllegalArgumentException("Unknown fact '" + factId + "' referenced by node '" + node.id() + "'");
                }
            }
            // Validate base value requirements
            for (var valueReq : node.requiredValues()) {
                if (!valueIds.contains(valueReq.valueId())) {
                    throw new IllegalArgumentException("Unknown value '" + valueReq.valueId() + "' referenced by node '" + node.id() + "'");
                }
            }
            // Validate stage dependencies
            for (var stageDep : node.requiredStages()) {
                if (!nodeIds.contains(stageDep.nodeId())) {
                    throw new IllegalArgumentException("Unknown node '" + stageDep.nodeId() + "' referenced by stage dependency in node '" + node.id() + "'");
                }
                if (!stageIds.contains(stageDep.stageId())) {
                    throw new IllegalArgumentException("Unknown stage '" + stageDep.stageId() + "' referenced by stage dependency in node '" + node.id() + "'");
                }
            }
            // Validate stage requirements
            for (int i = 0; i < node.stages().size(); i++) {
                var stage = node.stages().get(i);
                stageLocations.put(stage.id(), new ResearchData.StageLocation(node.id(), i));
                for (var factId : stage.requiredFacts()) {
                    if (!factIds.contains(factId)) {
                        throw new IllegalArgumentException("Unknown fact '" + factId + "' referenced by stage '" + stage.id() + "' in node '" + node.id() + "'");
                    }
                }
                for (var valueReq : stage.requiredValues()) {
                    if (!valueIds.contains(valueReq.valueId())) {
                        throw new IllegalArgumentException("Unknown value '" + valueReq.valueId() + "' referenced by stage '" + stage.id() + "' in node '" + node.id() + "'");
                    }
                }
                nodeStageRules.add(new NodeStageRule(
                        node.id(),
                        i,
                        List.copyOf(stage.requiredFacts()),
                        List.copyOf(stage.requiredValues())
                ));
            }

            nodeRules.add(new NodeRule(
                    node.id(),
                    List.copyOf(node.requiredFacts()),
                    List.copyOf(node.requiredValues()),
                    List.copyOf(node.requiredStages())
            ));
        }

        var groupedEntryViewedOnceHooks = hooks.stream()
                .filter(h -> h.triggerType() == TriggerTypeRegistry.ENTRY_VIEWED_ONCE)
                .collect(Collectors.groupingBy(ResearchHookDefinition::triggerTargetId));
        var groupedItemCraftedHooks = hooks.stream()
                .filter(h -> h.triggerType() == TriggerTypeRegistry.ITEM_CRAFTED)
                .collect(Collectors.groupingBy(ResearchHookDefinition::triggerTargetId));
        var groupedItemAcquiredHooks = hooks.stream()
                .filter(h -> h.triggerType() == TriggerTypeRegistry.ITEM_ACQUIRED)
                .collect(Collectors.groupingBy(ResearchHookDefinition::triggerTargetId));
        var groupedAdvancementHooks = advancementHooks.stream().collect(Collectors.groupingBy(AdvancementResearchHookDefinition::advancementId));

        return new ResearchData(
                Set.copyOf(factIds),
                Set.copyOf(nodeIds),
                Set.copyOf(valueIds),
                Set.copyOf(stageIds),
                nodeRules,
                nodeStageRules,
                groupedEntryViewedOnceHooks,
                groupedItemCraftedHooks,
                groupedItemAcquiredHooks,
                groupedAdvancementHooks,
                graphFactIds,
                graphNodeIds,
                graphValueIds,
                Map.copyOf(stageLocations)
        );
    }

    private static Set<Identifier> uniqueIds(List<Identifier> ids, String type) {
        ensureUniqueIds(ids, type);
        return Set.copyOf(ids);
    }

    private static void ensureUniqueIds(List<Identifier> ids, String type) {
        var duplicates = ids.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Duplicate " + type + " id(s): " + duplicates);
        }
    }

    /**
     * Node activation rule: node activates when all required facts are present,
     * all required values have reached their thresholds, and all required stage
     * dependencies on other nodes are satisfied.
     */
    public record NodeRule(
            Identifier nodeId,
            List<Identifier> requiredFactIds,
            List<ResearchNodeDefinition.ValueRequirement> requiredValueRequirements,
            List<ResearchNodeDefinition.StageDependency> requiredStageDependencies
    ) {
    }

    /**
     * Stage advancement rule: stage advances when all required facts are present
     * and all required values have reached their thresholds.
     */
    public record NodeStageRule(
            Identifier nodeId,
            int stageIndex,
            List<Identifier> requiredFactIds,
            List<ResearchNodeDefinition.ValueRequirement> requiredValueRequirements
    ) {
    }

    /**
     * Maps a stage id to its owning node and 0-based index within that node.
     */
    public record StageLocation(Identifier nodeId, int stageIndex) {
    }
}
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS (ResearchDataManager default instance will need updating next)

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchData.java
git commit -m "feat(research): add stage rules, stage locations, and validation to ResearchData"
```

---

## Task 4: PlayerResearchState — Stage Index Tracking

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java`

Track per-node stage progress. `nodeStageIndexes` maps node ID to stage index: 0 = not started, N = stage N-1 completed, stages.size()+1 = fully complete.

- [ ] **Step 1: Replace PlayerResearchState with stage-aware version**

```java
/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.Map;
import java.util.Set;

public class PlayerResearchState {

    public static final Codec<PlayerResearchState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.set(Identifier.CODEC).optionalFieldOf("facts", Set.of()).forGetter(PlayerResearchState::factIds),
            Codecs.set(Identifier.CODEC).optionalFieldOf("unlocked_nodes", Set.of()).forGetter(PlayerResearchState::unlockedNodeIds),
            Codecs.identifierIntMap().optionalFieldOf("values", Map.of()).forGetter(PlayerResearchState::valuesMap),
            Codecs.identifierIntMap().optionalFieldOf("node_stages", Map.of()).forGetter(PlayerResearchState::nodeStagesMap)
    ).apply(instance, PlayerResearchState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerResearchState> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final Set<Identifier> factIds;
    private final Set<Identifier> unlockedNodeIds;
    private final Object2IntOpenHashMap<Identifier> internalValues;
    private final Object2IntOpenHashMap<Identifier> nodeStageIndexes;

    public PlayerResearchState() {
        this(Set.of(), Set.of(), Map.of(), Map.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds) {
        this(factIds, unlockedNodeIds, Map.of(), Map.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds, Map<Identifier, Integer> values) {
        this(factIds, unlockedNodeIds, values, Map.of());
    }

    public PlayerResearchState(Set<Identifier> factIds, Set<Identifier> unlockedNodeIds, Map<Identifier, Integer> values, Map<Identifier, Integer> nodeStages) {
        this.factIds = new ObjectOpenHashSet<>(factIds);
        this.unlockedNodeIds = new ObjectOpenHashSet<>(unlockedNodeIds);
        this.internalValues = new Object2IntOpenHashMap<>(values);
        this.nodeStageIndexes = new Object2IntOpenHashMap<>(nodeStages);
    }

    public boolean grantFact(Identifier factId) {
        return this.factIds.add(factId);
    }

    public boolean revokeFact(Identifier factId) {
        return this.factIds.remove(factId);
    }

    public boolean hasFact(Identifier factId) {
        return this.factIds.contains(factId);
    }

    public boolean unlockNode(Identifier nodeId) {
        return this.unlockedNodeIds.add(nodeId);
    }

    public boolean lockNode(Identifier nodeId) {
        return this.unlockedNodeIds.remove(nodeId);
    }

    public boolean isNodeUnlocked(Identifier nodeId) {
        return this.unlockedNodeIds.contains(nodeId);
    }

    public int setValue(Identifier valueId, int amount) {
        this.internalValues.put(valueId, amount);
        return amount;
    }

    public int incrementValue(Identifier valueId, int amount) {
        int newValue = this.internalValues.getInt(valueId) + amount;
        this.internalValues.put(valueId, newValue);
        return newValue;
    }

    public int getValue(Identifier valueId) {
        return this.internalValues.getInt(valueId);
    }

    /**
     * Returns the current stage index for a node.
     * 0 = node not started, N = stage N-1 completed (currently on stage N).
     * A value greater than the node's stage count means the node is fully complete.
     */
    public int getNodeStageIndex(Identifier nodeId) {
        return this.nodeStageIndexes.getInt(nodeId);
    }

    /**
     * Sets the stage index for a node.
     */
    public void setNodeStageIndex(Identifier nodeId, int stageIndex) {
        this.nodeStageIndexes.put(nodeId, stageIndex);
    }

    /**
     * Returns true if the node has completed the given stage index (0-based).
     * Stage index 0 completed means nodeStageIndex > 0.
     */
    public boolean isStageCompleted(Identifier nodeId, int stageIndex) {
        return this.nodeStageIndexes.getInt(nodeId) > stageIndex;
    }

    /**
     * Resets all research state including stage indexes.
     */
    public void reset() {
        this.factIds.clear();
        this.unlockedNodeIds.clear();
        this.internalValues.clear();
        this.nodeStageIndexes.clear();
    }

    public Set<Identifier> factIds() {
        return Set.copyOf(this.factIds);
    }

    public Set<Identifier> unlockedNodeIds() {
        return Set.copyOf(this.unlockedNodeIds);
    }

    public Map<Identifier, Integer> valuesMap() {
        var copy = new java.util.HashMap<Identifier, Integer>();
        this.internalValues.forEach((k, v) -> copy.put(k, v));
        return Map.copyOf(copy);
    }

    public Map<Identifier, Integer> nodeStagesMap() {
        var copy = new java.util.HashMap<Identifier, Integer>();
        this.nodeStageIndexes.forEach((k, v) -> copy.put(k, v));
        return Map.copyOf(copy);
    }
}
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/state/PlayerResearchState.java
git commit -m "feat(research): add node stage index tracking to PlayerResearchState"
```

---

## Task 5: ResearchStateManager — Stage Advancement and Dependency Checking

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java`

Extend `reevaluate()` to handle stage advancement and stage dependencies. Add `isStageCompleted()` that resolves stage ID to index via `stageLocations`. Add `setNodeStage()` for commands.

- [ ] **Step 1: Replace ResearchStateManager with stage-aware version**

```java
/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.state;

import com.klikli_dev.modonomicon.networking.RequestSyncResearchStateMessage;
import com.klikli_dev.modonomicon.networking.SyncResearchStateMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchData;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ResearchStateManager {

    private static final ResearchStateManager INSTANCE = new ResearchStateManager();

    public ResearchStatesSaveData saveData;

    public static ResearchStateManager get() {
        return INSTANCE;
    }

    public PlayerResearchState getStateFor(Player player) {
        this.getSaveDataIfNecessary(player);
        return this.saveData.getFor(player.getUUID());
    }

    public boolean grantFact(ServerPlayer player, Identifier factId) {
        boolean changed = this.getStateFor(player).grantFact(factId);
        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    public boolean revokeFact(ServerPlayer player, Identifier factId) {
        boolean changed = this.getStateFor(player).revokeFact(factId);
        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    public boolean incrementValue(ServerPlayer player, Identifier valueId, int amount) {
        var state = this.getStateFor(player);
        state.incrementValue(valueId, amount);
        this.saveData.setDirty();
        return true;
    }

    public int setValue(ServerPlayer player, Identifier valueId, int amount) {
        var state = this.getStateFor(player);
        int newValue = state.setValue(valueId, amount);
        this.saveData.setDirty();
        return newValue;
    }

    public boolean lockNode(ServerPlayer player, Identifier nodeId) {
        boolean changed = this.getStateFor(player).lockNode(nodeId);
        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    /**
     * Sets the stage index for a node. Used by admin commands.
     */
    public void setNodeStage(ServerPlayer player, Identifier nodeId, int stageIndex) {
        var state = this.getStateFor(player);
        state.setNodeStageIndex(nodeId, stageIndex);
        this.saveData.setDirty();
    }

    /**
     * Returns true if the player has completed the given stage of a node.
     * Resolves the stage id to its location via stageLocations.
     */
    public boolean isStageCompleted(Player player, Identifier nodeId, Identifier stageId) {
        var state = this.getStateFor(player);
        var location = ResearchDataManager.get().data().stageLocations().get(stageId);
        if (location == null || !location.nodeId().equals(nodeId)) {
            return false;
        }
        return state.isStageCompleted(nodeId, location.stageIndex());
    }

    public boolean reevaluate(ServerPlayer player) {
        var state = this.getStateFor(player);
        var data = ResearchDataManager.get().data();
        boolean changed = false;

        for (var rule : data.nodeRules()) {
            // Check base fact requirements
            boolean factsMet = true;
            for (var factId : rule.requiredFactIds()) {
                if (!state.hasFact(factId)) {
                    factsMet = false;
                    break;
                }
            }
            if (!factsMet) {
                continue;
            }

            // Check base value requirements
            boolean valuesMet = true;
            for (var valueReq : rule.requiredValueRequirements()) {
                if (state.getValue(valueReq.valueId()) < valueReq.threshold()) {
                    valuesMet = false;
                    break;
                }
            }
            if (!valuesMet) {
                continue;
            }

            // Check stage dependencies on other nodes
            boolean stageDepsMet = true;
            for (var stageDep : rule.requiredStageDependencies()) {
                var location = data.stageLocations().get(stageDep.stageId());
                if (location == null || !location.nodeId().equals(stageDep.nodeId())) {
                    stageDepsMet = false;
                    break;
                }
                if (!state.isStageCompleted(location.nodeId(), location.stageIndex())) {
                    stageDepsMet = false;
                    break;
                }
            }
            if (!stageDepsMet) {
                continue;
            }

            // Base requirements met - activate the node
            changed |= state.unlockNode(rule.nodeId());

            // Set initial stage index if not started
            if (state.getNodeStageIndex(rule.nodeId()) == 0) {
                state.setNodeStageIndex(rule.nodeId(), 1);
                changed = true;
            }

            // Advance through stages
            changed |= advanceStages(state, rule.nodeId(), data.nodeStageRules());
        }

        if (changed) {
            this.saveData.setDirty();
        }
        return changed;
    }

    private boolean advanceStages(PlayerResearchState state, Identifier nodeId, java.util.List<ResearchData.NodeStageRule> stageRules) {
        boolean advanced;
        do {
            advanced = false;
            int currentStage = state.getNodeStageIndex(nodeId);
            int nextStageIndex = currentStage - 1;
            ResearchData.NodeStageRule nextRule = null;
            for (var rule : stageRules) {
                if (rule.nodeId().equals(nodeId) && rule.stageIndex() == nextStageIndex) {
                    nextRule = rule;
                    break;
                }
            }
            if (nextRule == null) {
                break;
            }

            // Check stage fact requirements
            boolean factsMet = true;
            for (var factId : nextRule.requiredFactIds()) {
                if (!state.hasFact(factId)) {
                    factsMet = false;
                    break;
                }
            }
            if (!factsMet) {
                break;
            }

            // Check stage value requirements
            boolean valuesMet = true;
            for (var valueReq : nextRule.requiredValueRequirements()) {
                if (state.getValue(valueReq.valueId()) < valueReq.threshold()) {
                    valuesMet = false;
                    break;
                }
            }
            if (!valuesMet) {
                break;
            }

            // Advance to next stage
            state.setNodeStageIndex(nodeId, currentStage + 1);
            advanced = true;
        } while (advanced);
        return advanced;
    }

    public boolean isNodeUnlocked(Player player, Identifier nodeId) {
        return this.getStateFor(player).isNodeUnlocked(nodeId);
    }

    public void resetFor(ServerPlayer player) {
        this.getStateFor(player).reset();
        this.saveData.setDirty();
    }

    public void syncFor(ServerPlayer player) {
        Services.NETWORK.sendTo(player, new SyncResearchStateMessage(this.getStateFor(player)));
    }

    public void installClientState(Player player, PlayerResearchState state) {
        this.saveData = new ResearchStatesSaveData(Object2ObjectMaps.singleton(player.getUUID(), state));
    }

    public void clearCachedSaveData() {
        this.saveData = null;
    }

    public void onDatapackSync(Player player) {
        this.getSaveDataIfNecessary(player);
    }

    public void onServerTickEnd(MinecraftServer server) {
    }

    private void getSaveDataIfNecessary(Player player) {
        if (this.saveData == null) {
            if (player instanceof ServerPlayer serverPlayer) {
                this.saveData = serverPlayer.level().getServer().overworld().getDataStorage().computeIfAbsent(ResearchStatesSaveData.TYPE);
            } else {
                this.saveData = new ResearchStatesSaveData();
                Services.NETWORK.sendToServer(RequestSyncResearchStateMessage.INSTANCE);
            }
        }
    }
}
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/state/ResearchStateManager.java
git commit -m "feat(research): implement stage advancement and stage dependency checking"
```

---

## Task 6: BookResearchStageCompletedCondition — New Condition Type

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchStageCompletedCondition.java`

Gates book entries on a specific stage of a node being completed.

- [ ] **Step 1: Create the condition class**

```java
/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class BookResearchStageCompletedCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("research_stage_completed");
    public static final MapCodec<BookResearchStageCompletedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            Codecs.STRICT_IDENTIFIER.fieldOf("node_id").forGetter(condition -> condition.nodeId),
            Codecs.STRICT_IDENTIFIER.fieldOf("stage_id").forGetter(condition -> condition.stageId)
    ).apply(instance, (tooltip, nodeId, stageId) -> new BookResearchStageCompletedCondition(tooltip.orElse(null), nodeId, stageId)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookResearchStageCompletedCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            Identifier.STREAM_CODEC, condition -> condition.nodeId,
            Identifier.STREAM_CODEC, condition -> condition.stageId,
            (tooltip, nodeId, stageId) -> new BookResearchStageCompletedCondition(tooltip.orElse(null), nodeId, stageId)
    );

    protected Identifier nodeId;
    protected Identifier stageId;

    public Identifier nodeId() {
        return this.nodeId;
    }

    public Identifier stageId() {
        return this.stageId;
    }

    public BookResearchStageCompletedCondition(Component tooltip, Identifier nodeId, Identifier stageId) {
        super(tooltip);
        this.nodeId = nodeId;
        this.stageId = stageId;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.RESEARCH_STAGE_COMPLETED;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (!ResearchDataManager.get().data().stageIds().contains(this.stageId)) {
            throw new IllegalArgumentException("Unknown stage '" + this.stageId + "' referenced by book condition '" + ID + "'.");
        }
        return ResearchServices.state().isStageCompleted(player, this.nodeId, this.stageId);
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext) {
            this.tooltip = Component.translatable(Tooltips.CONDITION_RESEARCH_STAGE_COMPLETED, Component.literal(this.nodeId.toString()), Component.literal(this.stageId.toString()));
        }
        return super.getTooltip(player, context);
    }
}
```

- [ ] **Step 2: Commit** (with registry change in same commit as Task 7)

Deferred until Task 7.

---

## Task 7: Register Stage Condition and Update Node Condition

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java`
- Commit: `common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchStageCompletedCondition.java`

Register the new condition type. Update `BookResearchNodeUnlockedCondition` to check all stages are complete for multi-stage nodes.

- [ ] **Step 1: Register the new condition type**

In `BookConditionTypeRegistry.java`, add import and registration after `RESEARCH_NODE_UNLOCKED`:

Add import:
```java
import com.klikli_dev.modonomicon.book.conditions.BookResearchStageCompletedCondition;
```

Add registration:
```java
public static final BookConditionType<BookResearchStageCompletedCondition> RESEARCH_STAGE_COMPLETED = register(BookResearchStageCompletedCondition.ID, BookResearchStageCompletedCondition.CODEC, BookResearchStageCompletedCondition.STREAM_CODEC);
```

- [ ] **Step 2: Update BookResearchNodeUnlockedCondition**

Update the `test()` method to check all stages complete for multi-stage nodes:

```java
@Override
public boolean test(BookConditionContext context, Player player) {
    if (!ResearchDataManager.get().data().nodeIds().contains(this.nodeId)) {
        throw new IllegalArgumentException("Unknown research node '" + this.nodeId + "' referenced by book condition '" + ID + "'.");
    }
    var state = ResearchServices.state().getStateFor(player);
    if (!state.isNodeUnlocked(this.nodeId)) {
        return false;
    }
    // For multi-stage nodes, all stages must be complete
    var data = ResearchDataManager.get().data();
    int totalStages = 0;
    for (var rule : data.nodeStageRules()) {
        if (rule.nodeId().equals(this.nodeId)) {
            totalStages++;
        }
    }
    if (totalStages > 0) {
        // Multi-stage: stageIndex > totalStages means all stages complete
        return state.getNodeStageIndex(this.nodeId) > totalStages;
    }
    // Single-stage: unlocked = complete
    return true;
}
```

- [ ] **Step 3: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS

- [ ] **Step 4: Commit all three files**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchStageCompletedCondition.java \
        common/src/main/java/com/klikli_dev/modonomicon/registry/BookConditionTypeRegistry.java \
        common/src/main/java/com/klikli_dev/modonomicon/book/conditions/BookResearchNodeUnlockedCondition.java
git commit -m "feat(research): add BookResearchStageCompletedCondition and update node condition for stages"
```

---

## Task 8: ResearchDataManager — Update Default Instance

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java`

Update the default `ResearchData` instance to include the new fields.

- [ ] **Step 1: Update the default data instance**

In `ResearchDataManager.java`, replace the default instance:

```java
private ResearchData data = new ResearchData(Set.of(), Set.of(), Set.of(), Set.of(), List.of(), List.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/research/data/ResearchDataManager.java
git commit -m "fix(research): update ResearchDataManager default instance for stage-aware ResearchData"
```

---

## Task 9: Datagen API — Stage Refs, Specs, and Builder Methods

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchStageRef.java`
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchStageSpec.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeSpec.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProviderBase.java`

- [ ] **Step 1: Create ResearchStageRef**

```java
/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.resources.Identifier;

/**
 * Typed authoring-time reference to a research stage id.
 *
 * @param id the canonical stage identifier used in generated research resources
 */
public record ResearchStageRef(Identifier id) {
    public static ResearchStageRef of(Identifier id) {
        return new ResearchStageRef(id);
    }
}
```

- [ ] **Step 2: Create ResearchStageSpec**

```java
/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchStageDefinition;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Authoring-time stage declaration that compiles into the canonical stage definition.
 *
 * @param ref the typed stage ref being declared
 * @param requiredFacts the fact refs required to advance past this stage
 * @param requiredValues the value requirements required to advance past this stage
 */
public record ResearchStageSpec(
        ResearchStageRef ref,
        List<ResearchFactRef> requiredFacts,
        List<ResearchNodeSpec.ValueRequirement> requiredValues
) {
    public static ResearchStageSpec factsOnly(ResearchStageRef ref, List<ResearchFactRef> requiredFacts) {
        return new ResearchStageSpec(ref, requiredFacts, List.of());
    }

    public static ResearchStageSpec valuesOnly(ResearchStageRef ref, List<ResearchNodeSpec.ValueRequirement> requiredValues) {
        return new ResearchStageSpec(ref, List.of(), requiredValues);
    }

    public static ResearchStageSpec none(ResearchStageRef ref) {
        return new ResearchStageSpec(ref, List.of(), List.of());
    }

    public ResearchStageDefinition toDefinition() {
        return new ResearchStageDefinition(
                this.ref.id(),
                this.requiredFacts.stream().map(ResearchFactRef::id).toList(),
                this.requiredValues.stream().map(req ->
                        new ResearchNodeDefinition.ValueRequirement(req.valueRef.id(), req.threshold())
                ).toList()
        );
    }
}
```

- [ ] **Step 3: Replace ResearchNodeSpec**

```java
/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Authoring-time node declaration that compiles into the canonical research node resource shape.
 *
 * @param ref the typed node ref being declared
 * @param requiredFacts the fact refs that act as this node's fact unlock requirements
 * @param requiredValues the value requirements that must be met for this node to unlock
 * @param stages ordered stage specs; empty means single-stage
 * @param requiredStages dependencies on other nodes reaching specific stages
 */
public record ResearchNodeSpec(
        ResearchNodeRef ref,
        List<ResearchFactRef> requiredFacts,
        List<ValueRequirement> requiredValues,
        List<ResearchStageSpec> stages,
        List<StageDependencySpec> requiredStages
) {
    public record ValueRequirement(ResearchValueRef valueRef, int threshold) {
    }

    /**
     * Dependency on another node reaching a specific stage.
     */
    public record StageDependencySpec(ResearchNodeRef nodeId, ResearchStageRef stageId) {
        public static StageDependencySpec of(ResearchNodeRef nodeId, ResearchStageRef stageId) {
            return new StageDependencySpec(nodeId, stageId);
        }
    }

    public static ResearchNodeSpec factsOnly(ResearchNodeRef ref, List<ResearchFactRef> requiredFacts) {
        return new ResearchNodeSpec(ref, requiredFacts, List.of(), List.of(), List.of());
    }

    public static ResearchNodeSpec of(
            ResearchNodeRef ref,
            List<ResearchFactRef> requiredFacts,
            List<ValueRequirement> requiredValues,
            List<ResearchStageSpec> stages,
            List<StageDependencySpec> requiredStages
    ) {
        return new ResearchNodeSpec(ref, requiredFacts, requiredValues, stages, requiredStages);
    }

    public ResearchNodeDefinition toDefinition() {
        return new ResearchNodeDefinition(
                this.ref.id(),
                this.requiredFacts.stream().map(ResearchFactRef::id).toList(),
                this.requiredValues.stream().map(req ->
                        new ResearchNodeDefinition.ValueRequirement(req.valueRef.id(), req.threshold())
                ).toList(),
                this.stages.stream().map(ResearchStageSpec::toDefinition).toList(),
                this.requiredStages.stream().map(dep ->
                        new ResearchNodeDefinition.StageDependency(dep.nodeId().id(), dep.stageId().id())
                ).toList()
        );
    }
}
```

- [ ] **Step 4: Add stage methods to ResearchDataBuilder**

Add these methods to `ResearchDataBuilder`:

```java
/**
 * Creates a typed stage ref for use in node stage declarations.
 * The stage itself is defined inline in the node spec.
 */
public ResearchStageRef stageRef(String path) {
    return ResearchStageRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
}

/**
 * Declares a research node with stages and stage dependencies.
 */
public ResearchNodeRef node(
        ResearchNodeRef ref,
        List<ResearchFactRef> requiredFacts,
        List<ResearchNodeSpec.ValueRequirement> requiredValues,
        List<ResearchStageSpec> stages,
        List<ResearchNodeSpec.StageDependencySpec> requiredStages
) {
    this.nodes.add(ResearchNodeSpec.of(ref, requiredFacts, requiredValues, stages, requiredStages));
    return ref;
}
```

- [ ] **Step 5: Add stage convenience to ResearchProviderBase**

Add these methods:

```java
/**
 * Creates a typed stage ref for use in node stage declarations.
 */
protected ResearchStageRef stageRef(String path) {
    return this.research.stageRef(path);
}

/**
 * Declares a research node with stages and stage dependencies.
 */
protected ResearchNodeRef node(
        ResearchNodeRef ref,
        List<ResearchFactRef> requiredFacts,
        List<ResearchNodeSpec.ValueRequirement> requiredValues,
        List<ResearchStageSpec> stages,
        List<ResearchNodeSpec.StageDependencySpec> requiredStages
) {
    return this.research.node(ref, requiredFacts, requiredValues, stages, requiredStages);
}
```

- [ ] **Step 6: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchStageRef.java \
        common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchStageSpec.java \
        common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchNodeSpec.java \
        common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchDataBuilder.java \
        common/src/main/java/com/klikli_dev/modonomicon/api/datagen/research/ResearchProviderBase.java
git commit -m "feat(research): add stage datagen API with typed refs and specs"
```

---

## Task 10: Datagen Condition Model for Stage Completed

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookResearchStageCompletedConditionModel.java`

- [ ] **Step 1: Create the condition model**

```java
/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookResearchStageCompletedCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookResearchStageCompletedConditionModel extends BookConditionModel<BookResearchStageCompletedConditionModel> {
    protected Identifier nodeId;
    protected Identifier stageId;

    protected BookResearchStageCompletedConditionModel() {
        super(BookResearchStageCompletedCondition.ID);
    }

    public static BookResearchStageCompletedConditionModel create() {
        return new BookResearchStageCompletedConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookResearchStageCompletedCondition(this.tooltipComponent(), this.nodeId, this.stageId);
    }

    public BookResearchStageCompletedConditionModel withNode(Identifier nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public BookResearchStageCompletedConditionModel withStage(Identifier stageId) {
        this.stageId = stageId;
        return this;
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/datagen/book/condition/BookResearchStageCompletedConditionModel.java
git commit -m "feat(research): add BookResearchStageCompletedConditionModel for datagen"
```

---

## Task 11: I18n Keys

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java`

- [ ] **Step 1: Add i18n keys**

In `ModonomiconConstants.I18n.Tooltips`, add after `CONDITION_RESEARCH_NODE_UNLOCKED`:
```java
public static final String CONDITION_RESEARCH_STAGE_COMPLETED = CONDITION_PREFIX + "research_stage_completed";
```

In `ModonomiconConstants.I18n.Command.Error`, add:
```java
public static final String ERROR_UNKNOWN_STAGE = ERROR_PREFIX + "unknown_stage";
```

- [ ] **Step 2: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/api/ModonomiconConstants.java
git commit -m "chore(i18n): add stage condition and command error keys"
```

---

## Task 12: Commands — Set Stage

**Files:**
- Create: `common/src/main/java/com/klikli_dev/modonomicon/command/SetStageCommand.java`
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/command/ResearchCommand.java`

- [ ] **Step 1: Create SetStageCommand**

```java
/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.command;

import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SetStageCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("stage")
                .then(Commands.argument("player", com.mojang.brigadier.arguments.StringArgumentType.word())
                        .suggests(ResearchSuggestions::suggestPlayers)
                        .then(Commands.argument("node", com.mojang.brigadier.arguments.StringArgumentType.word())
                                .suggests(ResearchSuggestions::suggestNodes)
                                .then(Commands.argument("stageIndex", IntegerArgumentType.integer(0))
                                        .executes(SetStageCommand::run))));
    }

    private static int run(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var playerName = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "player");
        var nodeId = Identifier.parse(com.mojang.brigadier.arguments.StringArgumentType.getString(context, "node"));
        var stageIndex = IntegerArgumentType.getInteger(context, "stageIndex");

        if (!ResearchDataManager.get().data().nodeIds().contains(nodeId)) {
            source.sendFailure(Component.translatable("modonomicon.command.error.unknown_node", nodeId.toString()));
            return 0;
        }

        var player = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            source.sendFailure(Component.translatable("commands.player.unknown"));
            return 0;
        }

        ResearchServices.state().setNodeStage(player, nodeId, stageIndex);
        ResearchServices.state().reevaluate(player);
        ResearchServices.state().syncFor(player);

        source.sendSuccess(() -> Component.translatable("modonomicon.command.stage.set", playerName, nodeId.toString(), stageIndex), false);
        return 1;
    }
}
```

- [ ] **Step 2: Update ResearchCommand to register set stage**

Restructure the `set` literal to include both `value` and `stage`:

```java
public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
    var resetCmd = new ResetResearchCommand();
    return Commands.literal("research")
            .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))
            .then(Commands.literal("reset")
                    .executes(resetCmd)
                    .then(Commands.literal("book")
                            .then(ResetBookResearchCommand.register(dispatcher)))
                    .then(Commands.literal("graph")
                            .then(ResetGraphResearchCommand.register(dispatcher))))
            .then(Commands.literal("grant")
                    .then(Commands.literal("fact")
                            .then(GrantFactCommand.register(dispatcher))))
            .then(Commands.literal("revoke")
                    .then(Commands.literal("fact")
                            .then(RevokeFactCommand.register(dispatcher))))
            .then(Commands.literal("unlock")
                    .then(Commands.literal("node")
                            .then(UnlockNodeCommand.register(dispatcher))))
            .then(Commands.literal("lock")
                    .then(Commands.literal("node")
                            .then(LockNodeCommand.register(dispatcher))))
            .then(Commands.literal("set")
                    .then(Commands.literal("value")
                            .then(SetValueCommand.register(dispatcher)))
                    .then(Commands.literal("stage")
                            .then(SetStageCommand.register(dispatcher))));
}
```

- [ ] **Step 3: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/command/SetStageCommand.java \
        common/src/main/java/com/klikli_dev/modonomicon/command/ResearchCommand.java
git commit -m "feat(research): add /modonomicon research set stage command"
```

---

## Task 13: Demo Content — Stages Scenario

**Files:**
- Modify: `common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java`

Add a demo scenario: a multi-stage node where each stage has different requirements, plus a second node that depends on reaching a specific stage of the first.

- [ ] **Step 1: Add stages demo to DemoResearch**

Add new node refs at the top:
```java
public static final ResearchNodeRef STAGES_DEMO = node("demo/stages_demo");
public static final ResearchNodeRef STAGES_DEPENDENT = node("demo/stages_dependent");
```

Add to `generateResearch()`:
```java
// Stage refs for the demo multi-stage node
var stagesDemoStage1 = stageRef("demo/stages_demo_stage_1");
var stagesDemoStage2 = stageRef("demo/stages_demo_stage_2");
var stagesDemoStage3 = stageRef("demo/stages_demo_stage_3");

// Facts granted by viewing stage content entries
var stagesFact1 = this.ingress()
        .onEntryViewedOnce(this.modLoc("stages/stage_1_content"))
        .declareFact("demo/stages_stage_1_viewed");
var stagesFact2 = this.ingress()
        .onEntryViewedOnce(this.modLoc("stages/stage_2_content"))
        .declareFact("demo/stages_stage_2_viewed");

// Value incremented by viewing collector entries
var stagesCollector = this.value("demo/stages_collector");
this.ingress()
        .onEntryViewedOnce(this.modLoc("stages/collector_entry"))
        .incrementValue("demo/stages_collector_hook", stagesCollector, 1);

// Multi-stage node: requires conditionRootViewed to start, then 3 stages
this.node(STAGES_DEMO, List.of(conditionRootViewed), List.of(), List.of(
        new ResearchStageSpec(stagesDemoStage1, List.of(stagesFact1), List.of()),
        new ResearchStageSpec(stagesDemoStage2, List.of(stagesFact2), List.of()),
        new ResearchStageSpec(stagesDemoStage3, List.of(), List.of(
                new ResearchNodeSpec.ValueRequirement(stagesCollector, 2)
        ))
), List.of());

// Node that depends on stages_demo reaching stage 2
this.node(STAGES_DEPENDENT, List.of(), List.of(), List.of(), List.of(
        ResearchNodeSpec.StageDependencySpec.of(STAGES_DEMO, stagesDemoStage2)
));
```

Add helper method at bottom:
```java
static ResearchStageRef stageRef(String path) {
    return ResearchStageRef.of(Modonomicon.loc(path));
}
```

- [ ] **Step 2: Compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/klikli_dev/modonomicon/datagen/research/DemoResearch.java
git commit -m "feat(research): add stages demo scenario"
```

---

## Task 14: Language Files

**Files:**
- Modify: `common/src/main/resources/data/modonomicon/lang/en_us.json` (or equivalent generated lang file)

- [ ] **Step 1: Add i18n strings**

Add to the English language file:
```json
{
  "tooltip.modonomicon.condition.research_stage_completed": "Requires stage %2$s of research node %1$s to be completed",
  "modonomicon.command.stage.set": "Set stage of node %2$s for player %1$s to %3$d"
}
```

- [ ] **Step 2: Commit**

```bash
git add common/src/main/resources/data/modonomicon/lang/en_us.json
git commit -m "chore(i18n): add stage condition and command translations"
```

---

## Task 15: Integration Compile and Verify

**Files:** All modified files

- [ ] **Step 1: Full compile check**

Run: `./gradlew.bat compileJava`
Expected: PASS with no errors

- [ ] **Step 2: Run data generation**

Run: `./gradlew.bat runData`
Expected: Completes without errors, generates research JSON with stages

- [ ] **Step 3: Verify generated research JSON**

Check that the generated research JSON files include:
- `stages` arrays in node definitions with `id`, `required_facts`, `required_values`
- `required_stages` arrays with `node_id` and `stage_id` entries

- [ ] **Step 4: Run client briefly**

Run: `./gradlew.bat runClient`
Expected: Client starts without crashes, demo book loads

- [ ] **Step 5: Final commit**

```bash
git add -A
git commit -m "feat(research): finalize stages implementation with demo content"
```

---

## Self-Review

### Spec Coverage
- [x] Stages as ordered sub-goals within nodes
- [x] Automatic advancement on requirement satisfaction
- [x] Nodes can depend on specific stages of other nodes
- [x] Book entries gated on stage completion via conditions
- [x] Backward compatible (nodes without stages work as before)
- [x] Data model, runtime, conditions, networking, datagen API, commands, demo

### Placeholder Scan
- No TBDs, TODOs, or vague instructions found
- All code blocks contain complete, compilable code
- All file paths are exact

### Type Consistency
- `ResearchStageDefinition` reuses `ResearchNodeDefinition.ValueRequirement` (consistent with existing pattern)
- `ResearchNodeDefinition.StageDependency` uses `Identifier` for both nodeId and stageId
- `NodeRule.requiredStageDependencies` uses `ResearchNodeDefinition.StageDependency`
- `PlayerResearchState.nodeStageIndexes` uses `Object2IntOpenHashMap<Identifier>` (consistent with `internalValues`)
- `ResearchData.stageLocations` maps `Identifier` (stageId) to `StageLocation(nodeId, stageIndex)`
- Datagen `ResearchStageSpec` compiles to `ResearchStageDefinition`
- Datagen `ResearchNodeSpec.StageDependencySpec` compiles to `ResearchNodeDefinition.StageDependency`

### Notes
- `BookResearchNodeUnlockedCondition` now checks all stages complete for multi-stage nodes. Backward compatible for single-stage nodes.
- Stage advancement is automatic during `reevaluate()`, called after any fact/value mutation.
- The `stageLocations` map enables O(1) stage ID to `(nodeId, stageIndex)` lookup for dependency and condition checking.
