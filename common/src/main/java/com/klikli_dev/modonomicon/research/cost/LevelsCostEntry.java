/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.cost;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchCostTypeRegistry;
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
 * Takes experience levels from the player.
 *
 * @param amount the number of levels taken
 */
public record LevelsCostEntry(int amount) implements ResearchCostEntry {

    public static final MapCodec<LevelsCostEntry> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(LevelsCostEntry::amount)
    ).apply(instance, LevelsCostEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LevelsCostEntry> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            LevelsCostEntry::amount,
            LevelsCostEntry::new
    );

    @Override
    public ResearchCostType<?> type() {
        return ResearchCostTypeRegistry.LEVELS;
    }

    @Override
    public int heldCount(Player player) {
        return player.experienceLevel;
    }

    @Override
    public boolean plan(ResearchCostPlan plan) {
        if (plan.levels() < this.amount) {
            return false;
        }
        plan.takeLevels(this, this.amount);
        return true;
    }

    @Override
    public void apply(ServerPlayer player, ResearchCostPlan plan) {
        for (var reservation : plan.reservationsOf(this)) {
            player.giveExperienceLevels(-reservation.amount());
        }
    }

    @Override
    public Component describe() {
        return Component.translatable(Gui.REQUIREMENTS_COST_LEVELS, this.amount);
    }
}
