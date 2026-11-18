/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import com.klikli_dev.modonomicon.research.hook.TriggerContext;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * @param hookIndexesByType hook indices per trigger type, see {@link ResearchHookIndex}
 * @param graphFactIds facts grouped by graph id
 * @param graphNodeIds nodes grouped by graph id
 * @param graphValueIds values grouped by graph id
 * @param factToasts toast definitions indexed by fact id
 * @param valueToasts toast definitions indexed by value id
 * @param nodeToasts toast definitions indexed by node id
 * @param stageToasts toast definitions indexed by stage id
 * @param stageLocations maps stage id to its owning node and index for fast lookup
 * @param stageRulesByLocation stage advancement rule per node and stage index
 * @param stageIdsByLocation stage id per node and stage index, the reverse of stageLocations
 * @param stageCountsByNode number of stages per node id
 * @param factDefinitions fact definitions indexed by fact id
 * @param valueDefinitions value definitions indexed by value id
 * @param nodeDefinitions node definitions indexed by node id
 * @param categoryDefinitions requirement category definitions indexed by category id, including built-ins
 * @param triggerCategoryIds category id claiming each trigger type by default, indexed by trigger type id
 */
public record ResearchData(
        Set<Identifier> factIds,
        Set<Identifier> nodeIds,
        Set<Identifier> valueIds,
        Set<Identifier> stageIds,
        List<NodeRule> nodeRules,
        List<NodeStageRule> nodeStageRules,
        Map<TriggerType<?, ?>, ResearchHookIndex<?>> hookIndexesByType,
        Map<Identifier, Set<Identifier>> graphFactIds,
        Map<Identifier, Set<Identifier>> graphNodeIds,
        Map<Identifier, Set<Identifier>> graphValueIds,
        Map<Identifier, ResearchToastDefinition> factToasts,
        Map<Identifier, ResearchToastDefinition> valueToasts,
        Map<Identifier, ResearchToastDefinition> nodeToasts,
        Map<Identifier, ResearchToastDefinition> stageToasts,
        Map<Identifier, StageLocation> stageLocations,
        Map<StageLocation, NodeStageRule> stageRulesByLocation,
        Map<StageLocation, Identifier> stageIdsByLocation,
        Map<Identifier, Integer> stageCountsByNode,
        Map<Identifier, ResearchFactDefinition> factDefinitions,
        Map<Identifier, ResearchValueDefinition> valueDefinitions,
        Map<Identifier, ResearchNodeDefinition> nodeDefinitions,
        Map<Identifier, ResearchCategoryDefinition> categoryDefinitions,
        Map<Identifier, Identifier> triggerCategoryIds
) {

    /**
     * Returns the category that claims the given trigger type by default, if any.
     */
    public Optional<ResearchCategoryDefinition> categoryForTriggerType(Identifier triggerTypeId) {
        var categoryId = this.triggerCategoryIds.get(triggerTypeId);
        return categoryId != null ? Optional.ofNullable(this.categoryDefinitions.get(categoryId)) : Optional.empty();
    }

    /**
     * Returns hooks for a specific trigger type that match the given context.
     * Uses the handler's indexKey to only check hooks that target the context, see {@link ResearchHookIndex}.
     */
    public <TTarget, TContext extends TriggerContext> List<ResearchHookDefinition<TTarget>> hooksFor(
            TriggerType<TTarget, TContext> type,
            TContext context
    ) {
        var index = this.hookIndex(type);
        var handler = type.handler();
        var candidates = index.candidatesFor(handler.indexKey(context));
        return candidates.stream()
                .filter(h -> handler.matches(h.triggerTarget(), context))
                .toList();
    }

    /**
     * Returns all hooks for a specific trigger type.
     */
    public <TTarget> List<ResearchHookDefinition<TTarget>> hooksForType(TriggerType<TTarget, ?> type) {
        return this.hookIndex(type).all();
    }

    /**
     * Returns all hooks of all trigger types.
     */
    public List<ResearchHookDefinition<?>> allHooks() {
        var result = new ArrayList<ResearchHookDefinition<?>>();
        for (var index : this.hookIndexesByType.values()) {
            result.addAll(index.all());
        }
        return List.copyOf(result);
    }

    /**
     * Returns the number of stages of the given node, 0 for unknown nodes.
     */
    public int stageCount(Identifier nodeId) {
        return this.stageCountsByNode.getOrDefault(nodeId, 0);
    }

    @SuppressWarnings("unchecked")
    private <TTarget> ResearchHookIndex<TTarget> hookIndex(TriggerType<TTarget, ?> type) {
        return (ResearchHookIndex<TTarget>) this.hookIndexesByType.getOrDefault(type, ResearchHookIndex.empty());
    }

    /**
     * Returns the ids of all known graphs, i.e. every graph that contributes at least one fact, node or value.
     * A graph does not need a {@code nodes.json} to be known.
     */
    public Set<Identifier> graphIds() {
        var graphIds = new HashSet<Identifier>();
        graphIds.addAll(this.graphFactIds.keySet());
        graphIds.addAll(this.graphNodeIds.keySet());
        graphIds.addAll(this.graphValueIds.keySet());
        return Set.copyOf(graphIds);
    }

    public static ResearchData validate(
            List<ResearchFactDefinition> facts,
            List<ResearchNodeDefinition> nodes,
            List<ResearchValueDefinition> values,
            List<ResearchHookDefinition<?>> hooks,
            Map<Identifier, Set<Identifier>> graphFactIds,
            Map<Identifier, Set<Identifier>> graphNodeIds,
            Map<Identifier, Set<Identifier>> graphValueIds,
            List<ResearchCategoryDefinition> categories
    ) {
        var factIds = uniqueIds(facts.stream().map(ResearchFactDefinition::id).toList(), "fact");
        var nodeIds = uniqueIds(nodes.stream().map(ResearchNodeDefinition::id).toList(), "node");
        var valueIds = uniqueIds(values.stream().map(ResearchValueDefinition::id).toList(), "value");
        ensureUniqueIds(hooks.stream().map(ResearchHookDefinition::id).toList(), "hook");

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
                        stage.id(),
                        stage.completion(),
                        List.copyOf(stage.requiredFacts()),
                        List.copyOf(stage.requiredValues()),
                        List.copyOf(stage.rewards())
                ));
            }

            nodeRules.add(new NodeRule(
                    node.id(),
                    List.copyOf(node.requiredFacts()),
                    List.copyOf(node.requiredValues()),
                    List.copyOf(node.requiredStages())
            ));
        }

        // Group all hooks by trigger type and index them
        Map<TriggerType<?, ?>, ResearchHookIndex<?>> hookIndexesByType = new HashMap<>();
        Map<TriggerType<?, ?>, List<ResearchHookDefinition<?>>> grouped = new HashMap<>();
        for (var hook : hooks) {
            grouped.computeIfAbsent(hook.triggerType(), k -> new ArrayList<>())
                    .add(hook);
        }
        for (var entry : grouped.entrySet()) {
            hookIndexesByType.put(entry.getKey(), ResearchHookIndex.of(entry.getKey(), entry.getValue()));
        }

        var stageRulesByLocation = new HashMap<StageLocation, NodeStageRule>();
        for (var rule : nodeStageRules) {
            stageRulesByLocation.put(new StageLocation(rule.nodeId(), rule.stageIndex()), rule);
        }
        var stageIdsByLocation = new HashMap<StageLocation, Identifier>();
        stageLocations.forEach((stageId, location) -> stageIdsByLocation.put(location, stageId));
        var stageCountsByNode = new HashMap<Identifier, Integer>();
        for (var rule : nodeStageRules) {
            stageCountsByNode.merge(rule.nodeId(), 1, Integer::sum);
        }
        var categoryDefinitions = validateCategories(categories);
        var triggerCategoryIds = triggerCategories(categoryDefinitions);

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
                Map.copyOf(hookIndexesByType),
                graphFactIds,
                graphNodeIds,
                graphValueIds,
                Map.copyOf(factToasts),
                Map.copyOf(valueToasts),
                Map.copyOf(nodeToasts),
                Map.copyOf(stageToasts),
                Map.copyOf(stageLocations),
                Map.copyOf(stageRulesByLocation),
                Map.copyOf(stageIdsByLocation),
                Map.copyOf(stageCountsByNode),
                facts.stream().collect(Collectors.toUnmodifiableMap(ResearchFactDefinition::id, Function.identity())),
                values.stream().collect(Collectors.toUnmodifiableMap(ResearchValueDefinition::id, Function.identity())),
                nodes.stream().collect(Collectors.toUnmodifiableMap(ResearchNodeDefinition::id, Function.identity())),
                categoryDefinitions,
                triggerCategoryIds
        );
    }

    /**
     * Merges the built-in categories with the given ones. Given categories replace built-ins with the same id.
     * Returns the categories by id, ordered by sort order then id.
     */
    private static Map<Identifier, ResearchCategoryDefinition> validateCategories(List<ResearchCategoryDefinition> categories) {
        var byId = new LinkedHashMap<Identifier, ResearchCategoryDefinition>();
        for (var category : ResearchCategories.builtins()) {
            byId.put(category.id(), category);
        }
        ensureUniqueIds(categories.stream().map(ResearchCategoryDefinition::id).toList(), "category");
        for (var category : categories) {
            byId.put(category.id(), category);
        }
        var sorted = new ArrayList<>(byId.values());
        sorted.sort(Comparator.comparingInt(ResearchCategoryDefinition::sortOrder).thenComparing(category -> category.id().toString()));

        var result = new LinkedHashMap<Identifier, ResearchCategoryDefinition>();
        sorted.forEach(category -> result.put(category.id(), category));
        return Collections.unmodifiableMap(result);
    }

    private static Map<Identifier, Identifier> triggerCategories(Map<Identifier, ResearchCategoryDefinition> categories) {
        var result = new HashMap<Identifier, Identifier>();
        for (var category : categories.values()) {
            for (var triggerType : category.triggerTypes()) {
                var existing = result.putIfAbsent(triggerType, category.id());
                if (existing != null && !existing.equals(category.id())) {
                    Modonomicon.LOG.warn("Trigger type '{}' is claimed by categories '{}' and '{}', using '{}'", triggerType, existing, category.id(), existing);
                }
            }
        }
        return Collections.unmodifiableMap(result);
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
     * and all required values have reached their thresholds. Manual stages never advance automatically,
     * see {@link ResearchStageCompletion}.
     */
    public record NodeStageRule(
            Identifier nodeId,
            int stageIndex,
            Identifier stageId,
            ResearchStageCompletion completion,
            List<Identifier> requiredFactIds,
            List<ResearchNodeDefinition.ValueRequirement> requiredValueRequirements,
            List<ResearchEffect> rewards
    ) {
    }

    /**
     * Maps a stage id to its owning node and 0-based index within that node.
     */
    public record StageLocation(Identifier nodeId, int stageIndex) {
    }
}
