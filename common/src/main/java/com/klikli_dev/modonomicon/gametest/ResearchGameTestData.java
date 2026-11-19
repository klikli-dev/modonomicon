/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import static com.klikli_dev.modonomicon.gametest.GameTestCatalog.DATA_NAMESPACE;

/**
 * Ids of the test research graph in {@code data/modonomicon_gametest/modonomicon/research/gametest} and helpers to set up players.
 */
final class ResearchGameTestData {

    static final Identifier ACTIONS_NODE = graph("actions");
    static final Identifier MULTI_STAGE_NODE = graph("multi_stage");

    static final Identifier GRANT_ACTION_FACT = graph("grant_action_fact");
    static final Identifier REPEATABLE_ACTION_FACT = graph("repeatable_action_fact");
    static final Identifier UNAFFORDABLE_ACTION_FACT = graph("unaffordable_action_fact");
    static final Identifier UNREQUIRED_ACTION_FACT = graph("unrequired_action_fact");
    static final Identifier ITEM_USED_FACT = graph("item_used_fact");
    static final Identifier MULTI_STAGE_ONE_FACT = graph("multi_stage_one_fact");
    static final Identifier MULTI_STAGE_TWO_FACT = graph("multi_stage_two_fact");

    private ResearchGameTestData() {
    }

    private static Identifier graph(String path) {
        return Identifier.fromNamespaceAndPath(DATA_NAMESPACE, "gametest/" + path);
    }

    /**
     * A mock server player with an empty research state, unlocking the nodes that have no requirements.
     * The research operations need a real {@link ServerPlayer}, which only the deprecated mock helper provides in 26.1.2.
     */
    @SuppressWarnings("removal")
    static ServerPlayer freshPlayer(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        // the mock player is created in creative mode, which would make every cost free
        player.getAbilities().instabuild = false;
        ResearchServices.hooks().applyStateChange(player, () -> false);
        return player;
    }
}
