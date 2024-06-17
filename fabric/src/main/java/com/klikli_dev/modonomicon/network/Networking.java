/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class Networking {

    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(BookEntryReadMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(ClickCommandLinkMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(ClickReadAllButtonMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(SaveBookStateMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(SaveCategoryStateMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(SaveEntryStateMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(SendUnlockCodeToServerMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(ReloadResourcesDoneMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(RequestSyncBookStatesMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(RequestAdvancementMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(AddBookmarkMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(RemoveBookmarkMessage.TYPE, new ServerMessageHandler<>());
    }

    public static void registerMessages() {
        //to server
        PayloadTypeRegistry.playC2S().register(BookEntryReadMessage.TYPE, BookEntryReadMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClickCommandLinkMessage.TYPE, ClickCommandLinkMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClickReadAllButtonMessage.TYPE, ClickReadAllButtonMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SaveBookStateMessage.TYPE, SaveBookStateMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SaveCategoryStateMessage.TYPE, SaveCategoryStateMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SaveEntryStateMessage.TYPE, SaveEntryStateMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SendUnlockCodeToServerMessage.TYPE, SendUnlockCodeToServerMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ReloadResourcesDoneMessage.TYPE, ReloadResourcesDoneMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSyncBookStatesMessage.TYPE, RequestSyncBookStatesMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestAdvancementMessage.TYPE, RequestAdvancementMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(AddBookmarkMessage.TYPE, AddBookmarkMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RemoveBookmarkMessage.TYPE, RemoveBookmarkMessage.STREAM_CODEC);

        //to client
        PayloadTypeRegistry.playS2C().register(SendUnlockCodeToClientMessage.TYPE, SendUnlockCodeToClientMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncBookDataMessage.TYPE, SyncBookDataMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncBookUnlockStatesMessage.TYPE, SyncBookUnlockStatesMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncBookVisualStatesMessage.TYPE, SyncBookVisualStatesMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncMultiblockDataMessage.TYPE, SyncMultiblockDataMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ReloadResourcesOnClientMessage.TYPE, ReloadResourcesOnClientMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SendAdvancementToClientMessage.TYPE, SendAdvancementToClientMessage.STREAM_CODEC);
    }
}
