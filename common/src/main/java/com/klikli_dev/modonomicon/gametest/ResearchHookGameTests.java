/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.ITEM_USED_FACT;
import static com.klikli_dev.modonomicon.gametest.ResearchGameTestData.freshPlayer;

public class ResearchHookGameTests {

    /**
     * The item_used hook consumes two apples, so a stack of one apple does not apply and the use is not handled.
     */
    public static void itemUsedWithTooFewItemsIsNotHandled(GameTestHelper helper) {
        var player = freshPlayer(helper);

        var outcome = ResearchServices.hooks().onItemUsed(player, new ItemStack(Items.APPLE, 1));

        helper.assertTrue(!outcome.handled(), "Use with one apple should not be handled");
        helper.assertTrue(!outcome.changed(), "Use with one apple should not change research");
        helper.assertTrue(outcome.consume() == 0, "Nothing should be consumed");
        helper.assertTrue(!ResearchServices.state().getStateFor(player).hasFact(ITEM_USED_FACT), "Fact should not be granted");
        helper.succeed();
    }

    /**
     * With two apples the hook applies, grants its fact and reports the two items it consumes.
     */
    public static void itemUsedWithEnoughItemsIsHandled(GameTestHelper helper) {
        var player = freshPlayer(helper);

        var outcome = ResearchServices.hooks().onItemUsed(player, new ItemStack(Items.APPLE, 2));

        helper.assertTrue(outcome.handled(), "Use with two apples should be handled");
        helper.assertTrue(outcome.changed(), "Use with two apples should change research");
        helper.assertTrue(outcome.consume() == 2, "Two apples should be consumed, was " + outcome.consume());
        helper.assertTrue(ResearchServices.state().getStateFor(player).hasFact(ITEM_USED_FACT), "Fact should be granted");
        helper.succeed();
    }
}
