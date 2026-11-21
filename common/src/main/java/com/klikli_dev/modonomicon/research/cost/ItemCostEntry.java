/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.cost;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchCostTypeRegistry;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pays {@code count} items accepted by the matcher, taken from any matching stacks in the player's inventory.
 *
 * @param item the item or tag accepted, components of a stack template must match
 * @param count the number of items consumed
 */
public record ItemCostEntry(ResearchItemMatcher item, int count) implements ResearchCostEntry {

    public static final MapCodec<ItemCostEntry> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResearchItemMatcher.CODEC.fieldOf("item").forGetter(ItemCostEntry::item),
            Codec.intRange(1, 6400).optionalFieldOf("count", 1).forGetter(ItemCostEntry::count)
    ).apply(instance, ItemCostEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemCostEntry> STREAM_CODEC = StreamCodec.composite(
            ResearchItemMatcher.STREAM_CODEC,
            ItemCostEntry::item,
            ByteBufCodecs.VAR_INT,
            ItemCostEntry::count,
            ItemCostEntry::new
    );

    @Override
    public ResearchCostType<?> type() {
        return ResearchCostTypeRegistry.ITEM;
    }

    @Override
    public int heldCount(Player player) {
        var inventory = player.getInventory();
        int held = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (this.item.matches(stack)) {
                held += stack.getCount();
            }
        }
        return held;
    }

    @Override
    public boolean plan(ResearchCostPlan plan) {
        return planAll(List.of(this), plan).isEmpty();
    }

    /**
     * Plans item entries together, so that overlapping matchers (e.g. a tag and one of its items) can share the stacks of the player.
     * Entries that cannot be covered are returned and reserve nothing; the others reserve their items in the plan.
     *
     * @return the entries the plan cannot cover, in the given order
     */
    public static List<ItemCostEntry> planAll(List<ItemCostEntry> entries, ResearchCostPlan plan) {
        var assignment = new Assignment(entries, plan);
        var missing = new ArrayList<ItemCostEntry>();
        for (int entry = 0; entry < entries.size(); entry++) {
            int needed = entries.get(entry).count;
            int covered = 0;
            while (covered < needed && assignment.augment(entry)) {
                covered++;
            }
            if (covered < needed) {
                assignment.release(entry);
                missing.add(entries.get(entry));
            }
        }
        for (int entry = 0; entry < entries.size(); entry++) {
            for (int slot = 0; slot < plan.size(); slot++) {
                if (assignment.taken[entry][slot] > 0) {
                    plan.take(entries.get(entry), slot, assignment.taken[entry][slot]);
                }
            }
        }
        return missing;
    }

    /**
     * Assigns item units of the entries to the free slots of the plan, growing the assignment by augmenting paths:
     * an entry may take a unit from a slot that another entry holds, if that entry can move to another free slot.
     */
    private static final class Assignment {
        private final List<ItemCostEntry> entries;
        private final boolean[][] matches;
        private final int[][] taken;
        private final int[] remaining;

        private Assignment(List<ItemCostEntry> entries, ResearchCostPlan plan) {
            this.entries = entries;
            this.remaining = new int[plan.size()];
            this.matches = new boolean[entries.size()][plan.size()];
            this.taken = new int[entries.size()][plan.size()];
            for (int slot = 0; slot < this.remaining.length; slot++) {
                this.remaining[slot] = plan.free(slot);
                for (int entry = 0; entry < entries.size(); entry++) {
                    this.matches[entry][slot] = this.remaining[slot] > 0 && entries.get(entry).item.matches(plan.stack(slot));
                }
            }
        }

        private boolean augment(int entry) {
            int slots = this.remaining.length;
            int[] reachedBy = new int[slots];
            Arrays.fill(reachedBy, -1);
            int[] leftSlot = new int[this.entries.size()];
            boolean[] visited = new boolean[this.entries.size()];
            visited[entry] = true;
            var queue = new ArrayDeque<Integer>();
            for (int slot = 0; slot < slots; slot++) {
                if (this.matches[entry][slot]) {
                    reachedBy[slot] = entry;
                    queue.add(slot);
                }
            }
            while (!queue.isEmpty()) {
                int slot = queue.poll();
                if (this.remaining[slot] > 0) {
                    this.remaining[slot]--;
                    int enter = reachedBy[slot];
                    this.taken[enter][slot]++;
                    while (enter != entry) {
                        int from = leftSlot[enter];
                        this.taken[enter][from]--;
                        enter = reachedBy[from];
                        this.taken[enter][from]++;
                    }
                    return true;
                }
                for (int other = 0; other < this.entries.size(); other++) {
                    if (visited[other] || this.taken[other][slot] == 0) {
                        continue;
                    }
                    visited[other] = true;
                    leftSlot[other] = slot;
                    for (int next = 0; next < slots; next++) {
                        if (reachedBy[next] == -1 && this.matches[other][next]) {
                            reachedBy[next] = other;
                            queue.add(next);
                        }
                    }
                }
            }
            return false;
        }

        private void release(int entry) {
            for (int slot = 0; slot < this.remaining.length; slot++) {
                this.remaining[slot] += this.taken[entry][slot];
                this.taken[entry][slot] = 0;
            }
        }
    }

    @Override
    public void apply(ServerPlayer player, ResearchCostPlan plan) {
        var inventory = player.getInventory();
        for (var reservation : plan.reservationsOf(this)) {
            inventory.removeItem(reservation.slot(), reservation.amount());
        }
    }

    @Override
    public Component describe() {
        return Component.translatable(Gui.SCHOLARSHIP_COST_ITEM, this.count, this.item.displayName());
    }
}
