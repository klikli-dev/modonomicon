/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class RequestAdvancementMessage implements Message {

    public static final Type<RequestAdvancementMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "request_advancement"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestAdvancementMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            (m) -> m.advancementId,
            RequestAdvancementMessage::new
    );

    public ResourceLocation advancementId;

    public RequestAdvancementMessage(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var adv = minecraftServer.getAdvancements().get(this.advancementId);

        if (adv != null) {
            Services.NETWORK.sendTo(player, new SendAdvancementToClientMessage(adv));
        } else {
            Modonomicon.LOG.warn("Requested Advancement {} from server, but not found", this.advancementId);
        }
    }
}
