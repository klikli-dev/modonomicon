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
        int needed = this.count;
        for (int slot = 0; slot < plan.size() && needed > 0; slot++) {
            if (plan.free(slot) <= 0 || !this.item.matches(plan.stack(slot))) {
                continue;
            }
            int take = Math.min(needed, plan.free(slot));
            plan.take(this, slot, take);
            needed -= take;
        }
        return needed == 0;
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
        return Component.translatable(Gui.REQUIREMENTS_COST_ITEM, this.count, this.item.displayName());
    }
}
