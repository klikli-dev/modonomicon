/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import com.klikli_dev.modonomicon.research.state.PlayerResearchState;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

public class ResearchStateGameTests {

    private static final Identifier NODE = Identifier.fromNamespaceAndPath("modonomicon_gametest", "gametest/state_node");

    /**
     * A node that was never started has no completed stages and no stage in progress.
     */
    public static void stageProgressNotStarted(GameTestHelper helper) {
        var state = new PlayerResearchState();

        helper.assertTrue(!state.isNodeStarted(NODE), "Node should not be started");
        helper.assertTrue(state.completedStageCount(NODE) == 0, "No stage should be completed");
        helper.assertTrue(state.currentStageIndex(NODE) == 0, "Current stage should be the first stage");
        helper.assertTrue(!state.isStageCompleted(NODE, 0), "First stage should not be completed");
        helper.succeed();
    }

    /**
     * Starting a node puts its first stage in progress, completing it moves progress to the second stage.
     */
    public static void stageProgressAdvances(GameTestHelper helper) {
        var state = new PlayerResearchState();

        helper.assertTrue(state.startNodeIfNotStarted(NODE), "Node should be started by the first call");
        helper.assertTrue(!state.startNodeIfNotStarted(NODE), "Node should not be started twice");
        helper.assertTrue(state.completedStageCount(NODE) == 0, "Started stage should not count as completed");

        state.completeCurrentStage(NODE);

        helper.assertTrue(state.completedStageCount(NODE) == 1, "One stage should be completed");
        helper.assertTrue(state.currentStageIndex(NODE) == 1, "Second stage should be in progress");
        helper.assertTrue(state.isStageCompleted(NODE, 0), "First stage should be completed");
        helper.assertTrue(!state.isStageCompleted(NODE, 1), "Second stage should not be completed");
        helper.succeed();
    }

    /**
     * The stored stage index is 1-based: a raw value of 3 means two stages are completed and the third is in progress.
     */
    public static void stageIndexIsOneBased(GameTestHelper helper) {
        var state = new PlayerResearchState();

        state.setNodeStageIndex(NODE, 3);

        helper.assertTrue(state.completedStageCount(NODE) == 2, "Raw index 3 should mean two completed stages");
        helper.assertTrue(state.isStageCompleted(NODE, 0), "First stage should be completed");
        helper.assertTrue(state.isStageCompleted(NODE, 1), "Second stage should be completed");
        helper.assertTrue(!state.isStageCompleted(NODE, 2), "Third stage should be in progress");
        helper.succeed();
    }
}
