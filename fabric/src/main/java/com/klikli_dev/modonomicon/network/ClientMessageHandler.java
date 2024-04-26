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

public class ClientMessageHandler<T extends Message> implements ClientPlayNetworking.PlayPayloadHandler<T> {


    public ClientMessageHandler() {
    }

    @Override
    public void receive(T payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            payload.onClientReceived(context.client(), context.player());
        });
    }
}