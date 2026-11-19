/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import com.klikli_dev.modonomicon.book.conditions.BookResearchNodeUnlockedCondition;
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.gametest.framework.GameTestHelper;

import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.MULTI_STAGE_NODE;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.MULTI_STAGE_ONE_FACT;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.MULTI_STAGE_TWO_FACT;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.freshPlayer;

public class ResearchConditionGameTests {

    /**
     * A multi-stage node counts as complete only after all of its stages, completing the first stage is not enough.
     */
    public static void multiStageNodeNeedsAllStages(GameTestHelper helper) {
        var player = freshPlayer(helper);
        helper.assertTrue(!BookResearchNodeUnlockedCondition.isNodeComplete(player, MULTI_STAGE_NODE), "Node should not be complete before any stage");

        ResearchServices.state().grantFact(player, MULTI_STAGE_ONE_FACT);
        ResearchServices.state().reevaluate(player);
        helper.assertTrue(ResearchServices.state().getStateFor(player).completedStageCount(MULTI_STAGE_NODE) == 1, "First stage should be completed");
        helper.assertTrue(!BookResearchNodeUnlockedCondition.isNodeComplete(player, MULTI_STAGE_NODE), "Node should not be complete after only the first stage");

        ResearchServices.state().grantFact(player, MULTI_STAGE_TWO_FACT);
        ResearchServices.state().reevaluate(player);
        helper.assertTrue(BookResearchNodeUnlockedCondition.isNodeComplete(player, MULTI_STAGE_NODE), "Node should be complete after all stages");
        helper.succeed();
    }
}
