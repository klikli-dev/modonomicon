/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.networking.Message;
import com.klikli_dev.modonomicon.platform.services.NetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkHelper implements NetworkHelper {
    @Override
    public <T extends Message> void sendTo(ServerPlayer player, T message) {
        if (!canSendTo(player, message)) {
            return;
        }
        ServerPlayNetworking.send(player, message);
    }

    @Override
    public <T extends Message> void sendToSplit(ServerPlayer player, T message) {
        //TODO: Fabric: Implement split packets if needed
        if (!canSendTo(player, message)) {
            return;
        }
        ServerPlayNetworking.send(player, message);
    }

    @Override
    public <T extends Message> void sendToServer(T message) {
        ClientPlayNetworking.send(message);
    }

    private static <T extends Message> boolean canSendTo(ServerPlayer player, T message) {
        if (player.connection == null) {
            //should never happen unless some other mod interferes with networking
            Modonomicon.LOG.warn("Tried to send message of type {} to player without connection. Id: {}, Name: {}.",
                    message.getClass().getName(), player.getStringUUID(), player.getName().getString());
            return false;
        }
        //Fake players (e.g. in game tests) and clients without the mod have no channel for our payloads.
        if (!ServerPlayNetworking.canSend(player, message.type())) {
            Modonomicon.LOG.debug("Skipping message of type {} to player {} because the connection has no channel for it.",
                    message.getClass().getName(), player.getStringUUID());
            return false;
        }
        return true;
    }
}
