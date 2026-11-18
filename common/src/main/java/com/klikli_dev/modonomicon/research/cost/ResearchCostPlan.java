/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.cost;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks what the entries of one cost reserve from a player, so that two entries cannot claim the same stack or the same levels.
 * Entries plan in declaration order, then apply their reservations in the same order.
 * <p>
 * Free counts start at the stack sizes of the player's inventory. Claiming a stack for durability sets its free count to zero,
 * so no later entry can take it as items.
 */
public final class ResearchCostPlan {

    /**
     * The slot of a reservation that is experience levels rather than a stack.
     */
    public static final int LEVELS_SLOT = -1;

    /**
     * A reservation made by an entry: the inventory slot and amount (items, durability points or levels).
     */
    public record Reservation(int slot, int amount) {
    }

    private final Player player;
    private final int[] free;
    private int levels;
    private final Map<ResearchCostEntry, List<Reservation>> reservations = new IdentityHashMap<>();

    public ResearchCostPlan(Player player) {
        this.player = player;
        Inventory inventory = player.getInventory();
        this.free = new int[inventory.getContainerSize()];
        for (int slot = 0; slot < this.free.length; slot++) {
            this.free[slot] = inventory.getItem(slot).getCount();
        }
        this.levels = player.experienceLevel;
    }

    public int size() {
        return this.free.length;
    }

    public Player player() {
        return this.player;
    }

    /**
     * The stack in the slot, as currently in the inventory. Entries read stacks through this to check what they accept.
     */
    public ItemStack stack(int slot) {
        return this.player.getInventory().getItem(slot);
    }

    /**
     * The number of items of the slot that no entry reserved yet. Zero for slots outside the main inventory.
     */
    public int free(int slot) {
        return slot < 0 || slot >= this.free.length ? 0 : this.free[slot];
    }

    /**
     * The number of levels that no entry reserved yet.
     */
    public int levels() {
        return this.levels;
    }

    /**
     * Reserves items from the slot.
     */
    public void take(ResearchCostEntry entry, int slot, int count) {
        this.free[slot] -= count;
        this.add(entry, new Reservation(slot, count));
    }

    /**
     * Reserves a whole stack for the entry, e.g. a damageable stack that pays durability. No other entry can use it as items.
     */
    public void claim(ResearchCostEntry entry, int slot, int amount) {
        this.free[slot] = 0;
        this.add(entry, new Reservation(slot, amount));
    }

    /**
     * Reserves experience levels for the entry.
     */
    public void takeLevels(ResearchCostEntry entry, int amount) {
        this.levels -= amount;
        this.add(entry, new Reservation(LEVELS_SLOT, amount));
    }

    /**
     * The reservations the plan made for the entry, in the order they were made.
     */
    public List<Reservation> reservationsOf(ResearchCostEntry entry) {
        return this.reservations.getOrDefault(entry, List.of());
    }

    private void add(ResearchCostEntry entry, Reservation reservation) {
        this.reservations.computeIfAbsent(entry, key -> new ArrayList<>()).add(reservation);
    }
}
