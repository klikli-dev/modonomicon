/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.action.ResearchActions;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.ACTIONS_NODE;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.GRANT_ACTION_FACT;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.REPEATABLE_ACTION_FACT;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.UNAFFORDABLE_ACTION_FACT;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.UNREQUIRED_ACTION_FACT;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.freshPlayer;

public class ResearchActionGameTests {

    /**
     * A one-time action grants its fact, counts as a change and charges its cost.
     */
    public static void actionGrantsFactAndCharges(GameTestHelper helper) {
        var player = freshPlayer(helper);
        player.getInventory().add(new ItemStack(Items.APPLE, 3));

        var result = ResearchActions.perform(player, ACTIONS_NODE, GRANT_ACTION_FACT);

        helper.assertTrue(result == ResearchActions.Result.PERFORMED, "Action should be performed, was " + result);
        helper.assertTrue(ResearchServices.state().getStateFor(player).hasFact(GRANT_ACTION_FACT), "Fact should be granted");
        helper.assertTrue(player.getInventory().countItem(Items.APPLE) == 2, "One apple should be charged");
        helper.succeed();
    }

    /**
     * A repeatable action without effects changes nothing, so it is unavailable and the cost is not charged.
     */
    public static void unchangedActionIsUnavailableAndFree(GameTestHelper helper) {
        var player = freshPlayer(helper);
        player.getInventory().add(new ItemStack(Items.APPLE, 3));

        var result = ResearchActions.perform(player, ACTIONS_NODE, REPEATABLE_ACTION_FACT);

        helper.assertTrue(result == ResearchActions.Result.UNAVAILABLE, "Action without change should be unavailable, was " + result);
        helper.assertTrue(player.getInventory().countItem(Items.APPLE) == 3, "No apple should be charged for an unavailable action");
        helper.succeed();
    }

    /**
     * An action the player cannot pay is not performed, nothing is granted and nothing is taken.
     */
    public static void unaffordableActionIsNotPerformed(GameTestHelper helper) {
        var player = freshPlayer(helper);
        player.getInventory().add(new ItemStack(Items.APPLE, 3));

        var result = ResearchActions.perform(player, ACTIONS_NODE, UNAFFORDABLE_ACTION_FACT);

        helper.assertTrue(result == ResearchActions.Result.UNAFFORDABLE, "Action should be unaffordable, was " + result);
        helper.assertTrue(!ResearchServices.state().getStateFor(player).hasFact(UNAFFORDABLE_ACTION_FACT), "Fact should not be granted");
        helper.assertTrue(player.getInventory().countItem(Items.APPLE) == 3, "Nothing should be charged");
        helper.succeed();
    }

    /**
     * A fact that the current stage does not require is rejected as invalid.
     */
    public static void factNotRequiredByStageIsInvalid(GameTestHelper helper) {
        var player = freshPlayer(helper);

        var result = ResearchActions.perform(player, ACTIONS_NODE, UNREQUIRED_ACTION_FACT);

        helper.assertTrue(result == ResearchActions.Result.INVALID, "Fact outside the stage should be invalid, was " + result);
        helper.succeed();
    }

    /**
     * Once a one-time action granted its fact, performing it again is invalid and charges nothing.
     */
    public static void grantedFactCannotBeGrantedAgain(GameTestHelper helper) {
        var player = freshPlayer(helper);
        player.getInventory().add(new ItemStack(Items.APPLE, 3));
        ResearchActions.perform(player, ACTIONS_NODE, GRANT_ACTION_FACT);

        var result = ResearchActions.perform(player, ACTIONS_NODE, GRANT_ACTION_FACT);

        helper.assertTrue(result == ResearchActions.Result.INVALID, "Granted fact should be invalid, was " + result);
        helper.assertTrue(player.getInventory().countItem(Items.APPLE) == 2, "Only the first performance should charge");
        helper.succeed();
    }
}
