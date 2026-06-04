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
 * @param factToasts toast definitions indexed by fact id
 * @param valueToasts toast definitions indexed by value id
 * @param nodeToasts toast definitions indexed by node id
 * @param stageToasts toast definitions indexed by stage id
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
        Map<Identifier, ResearchToastDefinition> factToasts,
        Map<Identifier, ResearchToastDefinition> valueToasts,
        Map<Identifier, ResearchToastDefinition> nodeToasts,
        Map<Identifier, ResearchToastDefinition> stageToasts,
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
        var stageToasts = new HashMap<Identifier, ResearchToastDefinition>();

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
            // Validate stage requirements and build stage rules
            for (int i = 0; i < node.stages().size(); i++) {
                var stage = node.stages().get(i);
                stageLocations.put(stage.id(), new ResearchData.StageLocation(node.id(), i));
                if (stage.toast().isPresent()) {
                    stageToasts.put(stage.id(), stage.toast().get());
                }
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

        // Build toast lookup maps
        var factToasts = facts.stream()
                .filter(f -> f.toast().isPresent())
                .collect(Collectors.toMap(f -> f.id(), f -> f.toast().get()));
        var valueToasts = values.stream()
                .filter(v -> v.toast().isPresent())
                .collect(Collectors.toMap(v -> v.id(), v -> v.toast().get()));
        var nodeToasts = nodes.stream()
                .filter(n -> n.toast().isPresent())
                .collect(Collectors.toMap(n -> n.id(), n -> n.toast().get()));

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
                Map.copyOf(factToasts),
                Map.copyOf(valueToasts),
                Map.copyOf(nodeToasts),
                Map.copyOf(stageToasts),
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
