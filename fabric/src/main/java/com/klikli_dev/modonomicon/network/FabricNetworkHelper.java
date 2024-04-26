/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import com.klikli_dev.modonomicon.platform.services.NetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkHelper implements NetworkHelper {
    @Override
    public <T extends Message> void sendTo(ServerPlayer player, T message) {
        ServerPlayNetworking.send(player, message);
    }

    @Override
    public <T extends Message> void sendToSplit(ServerPlayer player, T message) {
        //TODO: Fabric: Implement split packets if needed
        ServerPlayNetworking.send(player, message);
    }

    @Override
    public <T extends Message> void sendToServer(T message) {
        ClientPlayNetworking.send(message);
    }
}
