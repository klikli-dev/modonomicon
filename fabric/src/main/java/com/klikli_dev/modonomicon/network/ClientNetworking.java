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

    public static void registerMessages() {
        ClientPlayNetworking.registerGlobalReceiver(SendUnlockCodeToClientMessage.ID, new ClientMessageHandler<>(SendUnlockCodeToClientMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SyncBookDataMessage.ID, new ClientMessageHandler<>(SyncBookDataMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SyncBookUnlockStatesMessage.ID, new ClientMessageHandler<>(SyncBookUnlockStatesMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SyncBookVisualStatesMessage.ID, new ClientMessageHandler<>(SyncBookVisualStatesMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SyncMultiblockDataMessage.ID, new ClientMessageHandler<>(SyncMultiblockDataMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(ReloadResourcesOnClientMessage.ID, new ClientMessageHandler<>(ReloadResourcesOnClientMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SendAdvancementToClientMessage.ID, new ClientMessageHandler<>(SendAdvancementToClientMessage::new));
    }
}
