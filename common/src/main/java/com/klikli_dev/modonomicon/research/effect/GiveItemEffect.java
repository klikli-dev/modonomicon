/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.effect;

import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * Gives the player an item stack, the stack is placed in the inventory or dropped if it is full.
 * <p>
 * With {@code requireAbsent}, the effect is only available while the player does not hold an equal stack. This is how
 * an action can hand out a research note once, e.g. a note that satisfies a fact via an item acquired hook.
 *
 * @param stack the item stack to give, including its data components
 * @param requireAbsent if true, the effect is only available while the player does not hold an equal stack
 */
public record GiveItemEffect(ItemStackTemplate stack, boolean requireAbsent) implements ResearchEffect {

    public static final MapCodec<GiveItemEffect> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("stack").forGetter(GiveItemEffect::stack),
            Codec.BOOL.optionalFieldOf("require_absent", false).forGetter(GiveItemEffect::requireAbsent)
    ).apply(instance, GiveItemEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GiveItemEffect> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStackTemplate.STREAM_CODEC,
                    GiveItemEffect::stack,
                    ByteBufCodecs.BOOL,
                    GiveItemEffect::requireAbsent,
                    GiveItemEffect::new
            );

    @Override
    public ResearchEffectType<?> type() {
        return ResearchEffectTypeRegistry.GIVE_ITEM;
    }

    @Override
    public boolean isAvailable(ServerPlayer player, ResearchEffectContext context) {
        if (!this.requireAbsent) {
            return true;
        }
        ItemStack created = this.stack.create();
        return !created.isEmpty() && !player.getInventory().contains(created);
    }

    @Override
    public boolean apply(ServerPlayer player, ResearchEffectContext context) {
        player.getInventory().placeItemBackInInventory(this.stack.create(), Prediction.SERVER_ONLY);
        return true;
    }
}
