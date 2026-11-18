/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.reward;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ResearchRewardTypeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;

/**
 * Runs a function (mcfunction) as the player, silently and with game master permissions like tick functions.
 * Missing functions are logged and skipped.
 *
 * @param functionId the id of the function to run
 */
public record FunctionReward(Identifier functionId) implements ResearchReward {

    public static final MapCodec<FunctionReward> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("function").forGetter(FunctionReward::functionId)
    ).apply(instance, FunctionReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FunctionReward> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    FunctionReward::functionId,
                    FunctionReward::new
            );

    @Override
    public ResearchRewardType<?> type() {
        return ResearchRewardTypeRegistry.FUNCTION;
    }

    @Override
    public void grant(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        var functions = server.getFunctions();
        var function = functions.get(this.functionId);
        if (function.isEmpty()) {
            Modonomicon.LOG.warn("Research reward references unknown function '{}'", this.functionId);
            return;
        }

        CommandSourceStack source = player.createCommandSourceStack()
                .withPermission(LevelBasedPermissionSet.GAMEMASTER)
                .withSuppressedOutput();
        functions.execute(function.get(), source);
    }
}
