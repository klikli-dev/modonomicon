/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.action;

import com.klikli_dev.modonomicon.registry.ResearchActionTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * Pays the cost and gives the player an item stack, e.g. a research note that satisfies the fact via an item acquired hook.
 * The fact itself is not granted by this action.
 *
 * @param stack the item stack to give, including its data components
 * @param requireAbsent if true, the action is only available while the player does not hold an equal stack
 * @param cost the cost the player pays
 * @param repeatable if true, the action stays available after its fact is granted and its stage is completed
 */
public record GiveItemAction(ItemStackTemplate stack, boolean requireAbsent, ResearchCost cost, boolean repeatable) implements ResearchAction {

    public GiveItemAction(ItemStackTemplate stack, boolean requireAbsent, ResearchCost cost) {
        this(stack, requireAbsent, cost, false);
    }

    public static final MapCodec<GiveItemAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("stack").forGetter(GiveItemAction::stack),
            Codec.BOOL.optionalFieldOf("require_absent", true).forGetter(GiveItemAction::requireAbsent),
            ResearchCost.CODEC.optionalFieldOf("cost", ResearchCost.EMPTY).forGetter(GiveItemAction::cost),
            Codec.BOOL.optionalFieldOf("repeatable", false).forGetter(GiveItemAction::repeatable)
    ).apply(instance, GiveItemAction::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GiveItemAction> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStackTemplate.STREAM_CODEC,
                    GiveItemAction::stack,
                    ByteBufCodecs.BOOL,
                    GiveItemAction::requireAbsent,
                    ResearchCost.STREAM_CODEC,
                    GiveItemAction::cost,
                    ByteBufCodecs.BOOL,
                    GiveItemAction::repeatable,
                    GiveItemAction::new
            );

    @Override
    public ResearchActionType<?> type() {
        return ResearchActionTypeRegistry.GIVE_ITEM;
    }

    @Override
    public boolean isAvailable(ServerPlayer player, Identifier factId) {
        if (!this.requireAbsent) {
            return true;
        }
        ItemStack created = this.stack.create();
        return !created.isEmpty() && !player.getInventory().contains(created);
    }

    @Override
    public void execute(ServerPlayer player, Identifier factId) {
        player.getInventory().placeItemBackInInventory(this.stack.create());
    }
}
