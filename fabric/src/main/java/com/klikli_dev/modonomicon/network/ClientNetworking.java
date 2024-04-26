/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ClientNetworking {

    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(SendUnlockCodeToClientMessage.TYPE, new ClientMessageHandler<>());
        ClientPlayNetworking.registerGlobalReceiver(SyncBookDataMessage.TYPE, new ClientMessageHandler<>());
        ClientPlayNetworking.registerGlobalReceiver(SyncBookUnlockStatesMessage.TYPE, new ClientMessageHandler<>());
        ClientPlayNetworking.registerGlobalReceiver(SyncBookVisualStatesMessage.TYPE, new ClientMessageHandler<>());
        ClientPlayNetworking.registerGlobalReceiver(SyncMultiblockDataMessage.TYPE, new ClientMessageHandler<>());
        ClientPlayNetworking.registerGlobalReceiver(ReloadResourcesOnClientMessage.TYPE, new ClientMessageHandler<>());
        ClientPlayNetworking.registerGlobalReceiver(SendAdvancementToClientMessage.TYPE, new ClientMessageHandler<>());
    }
}
