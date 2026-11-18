/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.registry.ResearchCostTypeRegistry;
import com.klikli_dev.modonomicon.research.cost.ResearchCostEntry;
import com.klikli_dev.modonomicon.research.cost.ResearchCostPlan;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * What a player pays for a paid research action or a manually completed stage: a list of typed entries, see {@link ResearchCostEntry}.
 * The entries are planned together, so they cannot claim the same stack. Creative players (infinite materials) pay nothing.
 * <p>
 * JSON shape: a list of entries, each with a {@code type} such as {@code modonomicon:item}.
 *
 * @param entries the entries, planned and paid in order
 */
public record ResearchCost(List<ResearchCostEntry> entries) {

    public static final ResearchCost EMPTY = new ResearchCost(List.of());

    public ResearchCost {
        entries = List.copyOf(entries);
    }

    public static final Codec<ResearchCost> CODEC = ResearchCostTypeRegistry.CODEC.listOf()
            .xmap(ResearchCost::new, ResearchCost::entries);

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchCost> STREAM_CODEC =
            ResearchCostTypeRegistry.STREAM_CODEC.apply(ByteBufCodecs.list())
                    .map(ResearchCost::new, ResearchCost::entries);

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    /**
     * The entries the player cannot pay, planned in order. Empty if the player can afford the whole cost.
     */
    public List<ResearchCostEntry> missing(Player player) {
        var missing = new ArrayList<ResearchCostEntry>();
        if (player.hasInfiniteMaterials()) {
            return missing;
        }
        var plan = new ResearchCostPlan(player);
        for (var entry : this.entries) {
            if (!entry.plan(plan)) {
                missing.add(entry);
            }
        }
        return missing;
    }

    public boolean canAfford(Player player) {
        return this.missing(player).isEmpty();
    }

    /**
     * Pays the whole cost if the player can afford it, all entries together. Does nothing and returns false otherwise.
     * Callers that need the cost paid only after a state change call this last.
     */
    public boolean consume(ServerPlayer player) {
        if (player.hasInfiniteMaterials()) {
            return true;
        }
        var plan = new ResearchCostPlan(player);
        for (var entry : this.entries) {
            if (!entry.plan(plan)) {
                return false;
            }
        }
        for (var entry : this.entries) {
            entry.apply(player, plan);
        }
        player.getInventory().setChanged();
        return true;
    }

    /**
     * The number of matching items the player holds for each entry, in entry order. See {@link ResearchCostEntry#heldCount}.
     */
    public List<Integer> held(Player player) {
        return this.entries.stream().map(entry -> entry.heldCount(player)).toList();
    }
}
