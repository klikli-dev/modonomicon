/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.research.ResearchFeedback;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.stage.ResearchStages;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Client request to complete the current manual stage of a node.
 * Validation happens on the server in {@link ResearchStages#completeManual}.
 */
public class CompleteResearchStageMessage implements Message {

    public static final Type<CompleteResearchStageMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "complete_research_stage"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompleteResearchStageMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.nodeId,
            CompleteResearchStageMessage::new
    );

    public Identifier nodeId;

    public CompleteResearchStageMessage(Identifier nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (ResearchStages.completeManual(player, this.nodeId)) {
            BookVisualStateManager.get().syncFor(player);
            return;
        }
        ResearchServices.state().readyManualStage(player, this.nodeId)
                .ifPresentOrElse(rule -> ResearchFeedback.missing(player, rule.completion().cost().missing(player)),
                        () -> ResearchFeedback.invalid(player));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
