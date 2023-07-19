/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class Networking {

    public static void registerMessages() {

        ServerPlayNetworking.registerGlobalReceiver(BookEntryReadMessage.ID, new ServerMessageHandler<>(BookEntryReadMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(ClickCommandLinkMessage.ID, new ServerMessageHandler<>(ClickCommandLinkMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(ClickReadAllButtonMessage.ID, new ServerMessageHandler<>(ClickReadAllButtonMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(SaveBookStateMessage.ID, new ServerMessageHandler<>(SaveBookStateMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(SaveCategoryStateMessage.ID, new ServerMessageHandler<>(SaveCategoryStateMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(SaveEntryStateMessage.ID, new ServerMessageHandler<>(SaveEntryStateMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SendUnlockCodeToClientMessage.ID, new ClientMessageHandler<>(SendUnlockCodeToClientMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(SendUnlockCodeToServerMessage.ID, new ServerMessageHandler<>(SendUnlockCodeToServerMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SyncBookDataMessage.ID, new ClientMessageHandler<>(SyncBookDataMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SyncBookUnlockStatesMessage.ID, new ClientMessageHandler<>(SyncBookUnlockStatesMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SyncBookVisualStatesMessage.ID, new ClientMessageHandler<>(SyncBookVisualStatesMessage::new));
        ClientPlayNetworking.registerGlobalReceiver(SyncMultiblockDataMessage.ID, new ClientMessageHandler<>(SyncMultiblockDataMessage::new));
    }
}
