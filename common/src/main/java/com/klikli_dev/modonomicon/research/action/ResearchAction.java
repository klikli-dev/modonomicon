/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.action;

import com.klikli_dev.modonomicon.registry.ResearchActionTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * An action a player can perform on a requirement fact to satisfy it, e.g. paying to learn the fact.
 * Attached to a fact via the fact's display data. Every action has a cost that is paid before {@link #execute}.
 * Use {@link ResearchActions#perform} to run actions, it validates everything on the server first.
 */
public interface ResearchAction {

    Codec<ResearchAction> CODEC = ResearchActionTypeRegistry.codec()
            .dispatch("type", ResearchAction::type, ResearchActionType::codec);

    StreamCodec<RegistryFriendlyByteBuf, ResearchAction> STREAM_CODEC = ResearchActionTypeRegistry.streamCodec()
            .dispatch(ResearchAction::type, ResearchActionType::streamCodec);

    ResearchActionType<?> type();

    ResearchCost cost();

    /**
     * Whether the action stays available after its fact is granted and after its stage is completed, so it can be
     * performed again. Every performance still pays the cost. Actions that cannot repeat return false.
     */
    boolean repeatable();

    /**
     * Type specific checks that must pass before the action can be performed, e.g. the player must not already hold the item.
     * Costs are checked separately. Actions without extra checks return true.
     * <p>
     * This is deliberately not a default method: a default method makes the JVM initialize this interface together with
     * the implementing records, which registers their codecs before they are set.
     */
    boolean isAvailable(ServerPlayer player, Identifier factId);

    /**
     * Performs the action for the given fact. Called after the cost was paid, inside the research state change pipeline.
     */
    void execute(ServerPlayer player, Identifier factId);
}
