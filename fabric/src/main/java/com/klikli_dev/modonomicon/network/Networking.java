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
        ServerPlayNetworking.registerGlobalReceiver(SendUnlockCodeToServerMessage.ID, new ServerMessageHandler<>(SendUnlockCodeToServerMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(ReloadResourcesDoneMessage.ID, new ServerMessageHandler<>(ReloadResourcesDoneMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(RequestSyncBookStatesMessage.ID, new ServerMessageHandler<>(RequestSyncBookStatesMessage::new));
        ServerPlayNetworking.registerGlobalReceiver(RequestAdvancementMessage.ID, new ServerMessageHandler<>(RequestAdvancementMessage::new));
    }
}
