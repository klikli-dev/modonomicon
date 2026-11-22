/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.effect;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
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
public record FunctionEffect(Identifier functionId) implements ResearchEffect {

    public static final MapCodec<FunctionEffect> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("function").forGetter(FunctionEffect::functionId)
    ).apply(instance, FunctionEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FunctionEffect> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    FunctionEffect::functionId,
                    FunctionEffect::new
            );

    @Override
    public ResearchEffectType<?> type() {
        return ResearchEffectTypeRegistry.FUNCTION;
    }

    @Override
    public boolean apply(ServerPlayer player, ResearchEffectContext context) {
        MinecraftServer server = player.level().getServer();
        var functions = server.getFunctions();
        var function = functions.get(this.functionId);
        if (function.isEmpty()) {
            Modonomicon.LOG.warn("Research effect references unknown function '{}'", this.functionId);
            return false;
        }

        CommandSourceStack source = player.createCommandSourceStack()
                .withPermission(LevelBasedPermissionSet.GAMEMASTER)
                .withSuppressedOutput();
        functions.execute(function.get(), source);
        return true;
    }
}
