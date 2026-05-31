/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class RequestSyncResearchStateMessage implements Message {

    public static final RequestSyncResearchStateMessage INSTANCE = new RequestSyncResearchStateMessage();
    public static final Type<RequestSyncResearchStateMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "request_sync_research_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestSyncResearchStateMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RequestSyncResearchStateMessage() {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ResearchStateManager.get().syncFor(player);
    }
}
