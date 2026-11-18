/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.reward;

import com.klikli_dev.modonomicon.registry.ResearchRewardTypeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * Gives the player an item stack, the stack is placed in the inventory or dropped if it is full.
 *
 * @param stack the item stack to give, including its data components
 */
public record ItemReward(ItemStackTemplate stack) implements ResearchReward {

    public static final MapCodec<ItemReward> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("stack").forGetter(ItemReward::stack)
    ).apply(instance, ItemReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemReward> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStackTemplate.STREAM_CODEC,
                    ItemReward::stack,
                    ItemReward::new
            );

    @Override
    public ResearchRewardType<?> type() {
        return ResearchRewardTypeRegistry.ITEM;
    }

    @Override
    public void grant(ServerPlayer player) {
        player.getInventory().placeItemBackInInventory(this.stack.create());
    }
}
