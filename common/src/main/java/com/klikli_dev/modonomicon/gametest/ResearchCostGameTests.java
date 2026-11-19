/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gametest;

import com.klikli_dev.modonomicon.research.cost.DurabilityCostEntry;
import com.klikli_dev.modonomicon.research.cost.ItemCostEntry;
import com.klikli_dev.modonomicon.research.cost.LevelsCostEntry;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.List;

public class ResearchCostGameTests {

    /**
     * A tag entry and one of its items can share the stacks of the player if the player has enough items for both.
     */
    public static void overlappingItemsShareStacks(GameTestHelper helper) {
        var cost = new ResearchCost(List.of(
                new ItemCostEntry(ResearchItemMatcher.tag(ItemTags.LOGS), 1),
                new ItemCostEntry(ResearchItemMatcher.of(Items.OAK_LOG), 1)
        ));
        var player = playerWith(helper, new ItemStack(Items.OAK_LOG), new ItemStack(Items.BIRCH_LOG));

        helper.assertTrue(cost.canAfford(player), "Oak log and birch log should cover a logs tag and an oak log");
        helper.succeed();
    }

    /**
     * Overlapping entries that need more items than the player holds fail, even if the tag matches enough stacks in total.
     */
    public static void insufficientOverlappingItemsFail(GameTestHelper helper) {
        var cost = new ResearchCost(List.of(
                new ItemCostEntry(ResearchItemMatcher.tag(ItemTags.LOGS), 1),
                new ItemCostEntry(ResearchItemMatcher.of(Items.OAK_LOG), 1)
        ));
        var player = playerWith(helper, new ItemStack(Items.OAK_LOG));

        helper.assertTrue(!cost.canAfford(player), "A single oak log should not cover two entries");
        helper.succeed();
    }

    /**
     * Durability is planned before items, so an item entry cannot take the only damageable stack a durability entry needs.
     */
    public static void durabilityPlannedBeforeItems(GameTestHelper helper) {
        var shears = ResearchItemMatcher.of(Items.SHEARS);
        var itemEntry = new ItemCostEntry(shears, 1);
        var durabilityEntry = new DurabilityCostEntry(shears, 1);
        var itemAndDurability = new ResearchCost(List.of(itemEntry, durabilityEntry));
        var durabilityOnly = new ResearchCost(List.of(new DurabilityCostEntry(shears, 1)));

        // One stack fails either way, so check which entry is missing: durability planned first claims the shears,
        // leaving only the item entry unpaid. Items planned first would leave the durability entry unpaid instead.
        var missing = itemAndDurability.missing(playerWith(helper, new ItemStack(Items.SHEARS)));
        helper.assertTrue(missing.size() == 1 && missing.getFirst().equals(itemEntry),
                "Durability should claim the only shears first, leaving only the item entry unpaid, but missing was " + missing);
        helper.assertTrue(durabilityOnly.canAfford(playerWith(helper, new ItemStack(Items.SHEARS))),
                "One pair of shears should pay durability");
        helper.succeed();
    }

    /**
     * Levels are only paid if the player has enough levels.
     */
    public static void levelsMustBeCovered(GameTestHelper helper) {
        var cost = new ResearchCost(List.of(new LevelsCostEntry(3)));
        var player = playerWith(helper);

        helper.assertTrue(!cost.canAfford(player), "Player with no levels should not afford three levels");
        player.giveExperienceLevels(3);
        helper.assertTrue(cost.canAfford(player), "Player with three levels should afford three levels");
        helper.succeed();
    }

    /**
     * Creative players have infinite materials and pay nothing, even for costs they could never afford.
     */
    public static void creativePlayersPayNothing(GameTestHelper helper) {
        var cost = new ResearchCost(List.of(new LevelsCostEntry(100)));
        var player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        helper.assertTrue(cost.canAfford(player), "Creative player should pay nothing");
        helper.succeed();
    }

    /**
     * A plan stays payable only while the reserved stack still holds the reserved count.
     */
    public static void stalePlanIsDetected(GameTestHelper helper) {
        var cost = new ResearchCost(List.of(new ItemCostEntry(ResearchItemMatcher.of(Items.OAK_LOG), 2)));
        var player = playerWith(helper, new ItemStack(Items.OAK_LOG, 2));

        var plan = cost.plan(player).orElseThrow();
        helper.assertTrue(plan.isStillValid(), "Plan should be valid before the stack changes");

        player.getInventory().removeItem(0, 1);
        helper.assertTrue(!plan.isStillValid(), "Plan should be stale after one reserved item was removed");
        helper.succeed();
    }

    private static Player playerWith(GameTestHelper helper, ItemStack... stacks) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int slot = 0; slot < stacks.length; slot++) {
            player.getInventory().setItem(slot, stacks[slot]);
        }
        return player;
    }
}
