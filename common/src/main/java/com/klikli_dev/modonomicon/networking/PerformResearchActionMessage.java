/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.scholarship.ScholarshipFeedback;
import com.klikli_dev.modonomicon.research.action.ResearchActions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Client request to perform the research action attached to a requirement fact of a node.
 * Validation happens on the server in {@link ResearchActions#perform}.
 */
public class PerformResearchActionMessage implements Message {

    public static final Type<PerformResearchActionMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "perform_research_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PerformResearchActionMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.nodeId,
            Identifier.STREAM_CODEC,
            (m) -> m.factId,
            PerformResearchActionMessage::new
    );

    public Identifier nodeId;
    public Identifier factId;

    public PerformResearchActionMessage(Identifier nodeId, Identifier factId) {
        this.nodeId = nodeId;
        this.factId = factId;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var result = ResearchActions.perform(player, this.nodeId, this.factId);
        switch (result) {
            case PERFORMED -> {
                BookVisualStateManager.get().syncFor(player);
                ScholarshipFeedback.performed(player);
            }
            case UNAFFORDABLE -> ResearchActions.actionFor(this.factId)
                    .ifPresentOrElse(action -> ScholarshipFeedback.missing(player, action.cost().missing(player)),
                            () -> ScholarshipFeedback.invalid(player));
            case UNAVAILABLE -> ScholarshipFeedback.unavailable(player);
            case INVALID -> ScholarshipFeedback.invalid(player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
