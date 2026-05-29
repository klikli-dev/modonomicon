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
        ServerPlayNetworking.registerGlobalReceiver(BookCategoryReadMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(ClickCommandLinkMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(ClickReadAllButtonMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(SaveBookStateMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(SaveCategoryStateMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(SaveEntryStateMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(SendUnlockCodeToServerMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(ReloadResourcesDoneMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(RequestSyncBookStatesMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(AddBookmarkMessage.TYPE, new ServerMessageHandler<>());
        ServerPlayNetworking.registerGlobalReceiver(RemoveBookmarkMessage.TYPE, new ServerMessageHandler<>());
    }

    public static void registerMessages() {
        //to server
        PayloadTypeRegistry.serverboundPlay().register(BookEntryReadMessage.TYPE, BookEntryReadMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(BookCategoryReadMessage.TYPE, BookCategoryReadMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ClickCommandLinkMessage.TYPE, ClickCommandLinkMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ClickReadAllButtonMessage.TYPE, ClickReadAllButtonMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SaveBookStateMessage.TYPE, SaveBookStateMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SaveCategoryStateMessage.TYPE, SaveCategoryStateMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SaveEntryStateMessage.TYPE, SaveEntryStateMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SendUnlockCodeToServerMessage.TYPE, SendUnlockCodeToServerMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ReloadResourcesDoneMessage.TYPE, ReloadResourcesDoneMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RequestSyncBookStatesMessage.TYPE, RequestSyncBookStatesMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(AddBookmarkMessage.TYPE, AddBookmarkMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RemoveBookmarkMessage.TYPE, RemoveBookmarkMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(BookClosedMessage.TYPE, BookClosedMessage.STREAM_CODEC);

        //to client
        PayloadTypeRegistry.clientboundPlay().register(SendUnlockCodeToClientMessage.TYPE, SendUnlockCodeToClientMessage.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncBookDataMessage.TYPE, SyncBookDataMessage.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncBookUnlockStatesMessage.TYPE, SyncBookUnlockStatesMessage.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncBookVisualStatesMessage.TYPE, SyncBookVisualStatesMessage.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncMultiblockDataMessage.TYPE, SyncMultiblockDataMessage.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ReloadResourcesOnClientMessage.TYPE, ReloadResourcesOnClientMessage.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenBookOnClientMessage.TYPE, OpenBookOnClientMessage.STREAM_CODEC);
    }
}
