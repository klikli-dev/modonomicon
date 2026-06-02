/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.research.state.PlayerResearchState;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ResearchData(Set<Identifier> factIds, Set<Identifier> nodeIds, List<NodeRule> nodeRules,
                           Map<Identifier, List<ResearchHookDefinition>> entryViewedOnceHooks,
                           Map<Identifier, List<AdvancementResearchHookDefinition>> advancementHooks) {

    public static ResearchData validate(List<ResearchFactDefinition> facts, List<ResearchNodeDefinition> nodes,
                                         List<ResearchHookDefinition> hooks,
                                         List<AdvancementResearchHookDefinition> advancementHooks) {
        var factIds = uniqueIds(facts.stream().map(ResearchFactDefinition::id).toList(), "fact");
        var nodeIds = uniqueIds(nodes.stream().map(ResearchNodeDefinition::id).toList(), "node");
        ensureUniqueIds(hooks.stream().map(ResearchHookDefinition::id).toList(), "hook");
        ensureUniqueIds(advancementHooks.stream().map(AdvancementResearchHookDefinition::id).toList(), "advancement hook");

        for (var node : nodes) {
            for (var factId : node.requiredFacts()) {
                if (!factIds.contains(factId)) {
                    throw new IllegalArgumentException("Unknown fact '" + factId + "' referenced by node '" + node.id() + "'");
                }
            }
        }

        for (var hook : hooks) {
            if (!factIds.contains(hook.factId())) {
                throw new IllegalArgumentException("Unknown fact '" + hook.factId() + "' referenced by hook '" + hook.id() + "'");
            }
        }

        for (var hook : advancementHooks) {
            if (!factIds.contains(hook.factId())) {
                throw new IllegalArgumentException("Unknown fact '" + hook.factId() + "' referenced by advancement hook '" + hook.id() + "'");
            }
        }

        var nodeRules = nodes.stream().map(node -> new NodeRule(node.id(), node.requiredFacts())).toList();
        var groupedHooks = hooks.stream().collect(Collectors.groupingBy(ResearchHookDefinition::triggerTargetId));
        var groupedAdvancementHooks = advancementHooks.stream().collect(Collectors.groupingBy(AdvancementResearchHookDefinition::advancementId));
        return new ResearchData(Set.copyOf(factIds), Set.copyOf(nodeIds), nodeRules, groupedHooks, groupedAdvancementHooks);
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

    public record NodeRule(Identifier nodeId, List<Identifier> requiredFactIds) {
    }
}
