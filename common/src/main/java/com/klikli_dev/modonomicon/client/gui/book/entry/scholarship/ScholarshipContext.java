/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.research.data.ResearchData;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.state.PlayerResearchState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Everything a requirement presenter needs to evaluate and describe requirements for one player.
 * Create a new context whenever the research state or the research data changes.
 */
public final class ScholarshipContext {
    private final BookConditionContext conditionContext;
    private final Player player;
    private final PlayerResearchState state;
    private final ResearchData data;
    private final Map<Identifier, List<ResearchHookDefinition<?>>> hooksByFact;
    private final Map<Identifier, List<ResearchHookDefinition<?>>> hooksByValue;

    /**
     * @param conditionContext the book/entry context used to test conditions
     * @param player           the player the requirements are evaluated for
     * @param state            the research state of the player
     * @param data             the research data the state is evaluated against
     */
    public ScholarshipContext(BookConditionContext conditionContext, Player player, PlayerResearchState state, ResearchData data) {
        this.conditionContext = conditionContext;
        this.player = player;
        this.state = state;
        this.data = data;
        this.hooksByFact = data.allHooks().stream()
                .filter(hook -> hook.factId() != null)
                .collect(Collectors.groupingBy(ResearchHookDefinition::factId, Collectors.toCollection(ArrayList::new)));
        this.hooksByValue = data.allHooks().stream()
                .filter(hook -> hook.valueId() != null)
                .collect(Collectors.groupingBy(ResearchHookDefinition::valueId, Collectors.toCollection(ArrayList::new)));
    }

    public BookConditionContext conditionContext() {
        return this.conditionContext;
    }

    public Player player() {
        return this.player;
    }

    public PlayerResearchState state() {
        return this.state;
    }

    public ResearchData data() {
        return this.data;
    }

    /**
     * All hooks that grant the given fact.
     */
    public List<ResearchHookDefinition<?>> hooksGranting(Identifier factId) {
        return this.hooksByFact.getOrDefault(factId, List.of());
    }

    /**
     * All hooks that increment the given value.
     */
    public List<ResearchHookDefinition<?>> hooksIncrementing(Identifier valueId) {
        return this.hooksByValue.getOrDefault(valueId, List.of());
    }
}
