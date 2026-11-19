/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.data;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ResearchCostTypeRegistry;
import com.klikli_dev.modonomicon.research.cost.ItemCostEntry;
import com.klikli_dev.modonomicon.research.cost.ResearchCostEntry;
import com.klikli_dev.modonomicon.research.cost.ResearchCostPlan;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * What a player pays for a paid research action or a manually completed stage: a list of typed entries, see {@link ResearchCostEntry}.
 * The entries are planned together, so they cannot claim the same stack. Planning order is by {@link ResearchCostEntry#planRank}:
 * durability first, then items, then levels, so an item entry never takes the stack a durability entry needs.
 * Item entries are planned as one group, so overlapping item entries (e.g. a tag and one of its items) find a valid payment if one exists.
 * Creative players (infinite materials) pay nothing.
 * <p>
 * Payment order: the cost is planned with {@link #plan} <b>before</b> the state change, and the plan is paid with {@link #pay} after the change
 * succeeded. Paying checks the plan again: if the reserved stacks or levels changed during the change, the cost is planned again.
 * If that fails too, nothing is paid and a warning is logged, because the change already happened.
 * <p>
 * JSON shape: a list of entries, each with a {@code type} such as {@code modonomicon:item}.
 *
 * @param entries the entries, in declaration order; planned by rank and paid in that order
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
     * The entries in planning order: by {@link ResearchCostEntry#planRank}, declaration order within a rank.
     */
    private List<ResearchCostEntry> planOrder() {
        return this.entries.stream().sorted(Comparator.comparingInt(ResearchCostEntry::planRank)).toList();
    }

    /**
     * Plans the entries in planning order and returns the ones that cannot be covered. Item entries are planned together at the
     * position of the first item entry, so overlapping item entries can share the player's stacks.
     */
    private List<ResearchCostEntry> planEntries(ResearchCostPlan plan) {
        var missing = new ArrayList<ResearchCostEntry>();
        var items = this.entries.stream()
                .filter(ItemCostEntry.class::isInstance)
                .map(ItemCostEntry.class::cast)
                .toList();
        boolean itemsPlanned = false;
        for (var entry : this.planOrder()) {
            if (entry instanceof ItemCostEntry) {
                if (!itemsPlanned) {
                    itemsPlanned = true;
                    missing.addAll(ItemCostEntry.planAll(items, plan));
                }
            } else if (!entry.plan(plan)) {
                missing.add(entry);
            }
        }
        return missing;
    }

    /**
     * The entries the player cannot pay, planned in order. Empty if the player can afford the whole cost.
     */
    public List<ResearchCostEntry> missing(Player player) {
        if (player.hasInfiniteMaterials()) {
            return List.of();
        }
        return this.planEntries(new ResearchCostPlan(player));
    }

    public boolean canAfford(Player player) {
        return this.missing(player).isEmpty();
    }

    /**
     * Plans the payment against the player's current inventory and levels, without changing anything. Empty if the player
     * cannot afford the whole cost. Creative players can always pay.
     * <p>
     * Callers plan before their state change, then pay the plan with {@link #pay} after the change succeeded.
     */
    public Optional<ResearchCostPlan> plan(Player player) {
        var plan = new ResearchCostPlan(player);
        if (player.hasInfiniteMaterials()) {
            return Optional.of(plan);
        }
        return this.planEntries(plan).isEmpty() ? Optional.of(plan) : Optional.empty();
    }

    /**
     * Pays a plan made by {@link #plan} before a state change. If the reserved stacks or levels changed during the state change,
     * the cost is planned again against the current state. If that fails too, nothing is paid and a warning is logged,
     * because the state change already happened and the player keeps it for free.
     *
     * @return true if the cost was paid or the player has infinite materials, false if it could not be paid
     */
    public boolean pay(ServerPlayer player, ResearchCostPlan reserved) {
        if (player.hasInfiniteMaterials()) {
            return true;
        }
        var plan = reserved.isStillValid() ? Optional.of(reserved) : this.plan(player);
        if (plan.isEmpty()) {
            Modonomicon.LOG.warn("Research cost {} could not be paid after its state change for player {}", this.entries, player.getGameProfile().name());
            return false;
        }
        for (var entry : this.planOrder()) {
            entry.apply(player, plan.get());
        }
        player.getInventory().setChanged();
        return true;
    }
}
