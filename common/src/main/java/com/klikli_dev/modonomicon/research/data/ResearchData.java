/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.state.PlayerResearchState;
import net.minecraft.resources.Identifier;

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
 * @param nodeRules node unlock rules (facts + values)
 * @param entryViewedOnceHooks hooks grouped by trigger target entry id
 * @param itemCraftedHooks hooks grouped by item id
 * @param itemAcquiredHooks hooks grouped by item id
 * @param advancementHooks hooks grouped by advancement id
 * @param graphFactIds graph index for facts by graph id
 * @param graphNodeIds graph index for nodes by graph id
 * @param graphValueIds graph index for values by graph id
 * @param factToasts toast definitions indexed by fact id
 * @param valueToasts toast definitions indexed by value id
 * @param nodeToasts toast definitions indexed by node id
 */
public record ResearchData(
        Set<Identifier> factIds,
        Set<Identifier> nodeIds,
        Set<Identifier> valueIds,
        List<NodeRule> nodeRules,
        Map<Identifier, List<ResearchHookDefinition>> entryViewedOnceHooks,
        Map<Identifier, List<ResearchHookDefinition>> itemCraftedHooks,
        Map<Identifier, List<ResearchHookDefinition>> itemAcquiredHooks,
        Map<Identifier, List<AdvancementResearchHookDefinition>> advancementHooks,
        Map<Identifier, Set<Identifier>> graphFactIds,
        Map<Identifier, Set<Identifier>> graphNodeIds,
        Map<Identifier, Set<Identifier>> graphValueIds,
        Map<Identifier, ResearchToastDefinition> factToasts,
        Map<Identifier, ResearchToastDefinition> valueToasts,
        Map<Identifier, ResearchToastDefinition> nodeToasts
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

        // Validate advancement hooks have exactly one of factId or valueId
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

        // Validate node requirements reference known facts and values
        for (var node : nodes) {
            for (var factId : node.requiredFacts()) {
                if (!factIds.contains(factId)) {
                    throw new IllegalArgumentException("Unknown fact '" + factId + "' referenced by node '" + node.id() + "'");
                }
            }
            for (var valueReq : node.requiredValues()) {
                if (!valueIds.contains(valueReq.valueId())) {
                    throw new IllegalArgumentException("Unknown value '" + valueReq.valueId() + "' referenced by node '" + node.id() + "'");
                }
            }
        }

        var nodeRules = nodes.stream().map(node -> new NodeRule(
                node.id(),
                List.copyOf(node.requiredFacts()),
                List.copyOf(node.requiredValues())
        )).toList();
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
                nodeRules,
                groupedEntryViewedOnceHooks,
                groupedItemCraftedHooks,
                groupedItemAcquiredHooks,
                groupedAdvancementHooks,
                graphFactIds,
                graphNodeIds,
                graphValueIds,
                Map.copyOf(factToasts),
                Map.copyOf(valueToasts),
                Map.copyOf(nodeToasts)
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
     * Node unlock rule: node unlocks when all required facts are present
     * AND all required values have reached their thresholds.
     *
     * @param nodeId the node this rule applies to
     * @param requiredFactIds fact ids that must be granted
     * @param requiredValueRequirements value requirements that must be met
     */
    public record NodeRule(
            Identifier nodeId,
            List<Identifier> requiredFactIds,
            List<ResearchNodeDefinition.ValueRequirement> requiredValueRequirements
    ) {
    }
}
