/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.cost;

import com.klikli_dev.modonomicon.registry.ResearchCostTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * One part of a {@link ResearchCost}, e.g. items, durability or experience levels.
 * Mods add entry types via {@link ResearchCostTypeRegistry#register}.
 * <p>
 * An entry plans its payment against a {@link ResearchCostPlan} and applies the reservations it made. Entries of one cost
 * are planned together, so they cannot claim the same stack. Creative players (infinite materials) pay nothing.
 */
public interface ResearchCostEntry {

    ResearchCostType<?> type();

    /**
     * The planning rank of this entry within a cost, lower ranks plan first. Entries of equal rank keep their declaration order.
     * Durability plans first (rank 0), so an item entry cannot take the stack a durability entry needs, e.g. shears.
     * Other entries use rank 1.
     */
    default int planRank() {
        return 1;
    }

    /**
     * What the player holds for this entry: matching items, usable stacks for durability, or the experience level for levels.
     * Used for progress display, not for payment.
     */
    int heldCount(Player player);

    /**
     * Reserves what this entry needs from the plan. Returns false if the plan cannot cover it. Must not change the player.
     */
    boolean plan(ResearchCostPlan plan);

    /**
     * Applies the reservations the plan made for this entry. Only called after the whole cost was planned successfully, on the server.
     */
    void apply(ServerPlayer player, ResearchCostPlan plan);

    /**
     * Server-safe text describing the entry, e.g. "2x Paper". Client presenters use it for tooltips.
     */
    Component describe();

    /**
     * True if the player could pay this entry alone. Costs with several entries are checked with {@link ResearchCost#canAfford}.
     */
    default boolean canPay(Player player) {
        return player.hasInfiniteMaterials() || this.plan(new ResearchCostPlan(player));
    }

    /**
     * Pays this entry alone. Returns false without any change if the player cannot pay it.
     */
    default boolean pay(ServerPlayer player) {
        if (player.hasInfiniteMaterials()) {
            return true;
        }
        var plan = new ResearchCostPlan(player);
        if (!this.plan(plan)) {
            return false;
        }
        this.apply(player, plan);
        player.getInventory().setChanged();
        return true;
    }
}
