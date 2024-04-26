/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.function.Function;

public class ServerMessageHandler<T extends Message> implements ServerPlayNetworking.PlayPayloadHandler<T> {

    public ServerMessageHandler() {
    }

    @Override
    public void receive(T payload, ServerPlayNetworking.Context context) {
        context.player().getServer().execute(() -> {
            payload.onServerReceived(context.player().getServer(), context.player());
        });
    }
}
