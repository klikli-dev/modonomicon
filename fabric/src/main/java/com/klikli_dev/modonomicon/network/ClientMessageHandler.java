/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public class ClientMessageHandler<T extends Message> implements ClientPlayNetworking.PlayChannelHandler {

    private final Function<FriendlyByteBuf, T> decoder;

    public ClientMessageHandler(Function<FriendlyByteBuf, T> decoder) {
        this.decoder = decoder;
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        var packet = this.decoder.apply(buf);

        client.execute(() -> {
            packet.onClientReceived(client, client.player);
        });
    }
}