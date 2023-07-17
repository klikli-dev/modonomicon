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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.function.Function;

public class ServerMessageHandler<T extends Message> implements ServerPlayNetworking.PlayChannelHandler {

    private final Function<FriendlyByteBuf, T> decoder;

    public ServerMessageHandler(Function<FriendlyByteBuf, T> decoder) {
        this.decoder = decoder;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        var packet = this.decoder.apply(buf);

        server.execute(() -> {
            packet.onServerReceived(server, player);
        });
    }
}
