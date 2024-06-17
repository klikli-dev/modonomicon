/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.networking.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class Networking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(Modonomicon.MOD_ID);

        registrar.playToServer(BookEntryReadMessage.TYPE, BookEntryReadMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(ClickCommandLinkMessage.TYPE, ClickCommandLinkMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(ClickReadAllButtonMessage.TYPE, ClickReadAllButtonMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(SaveBookStateMessage.TYPE, SaveBookStateMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(SaveCategoryStateMessage.TYPE, SaveCategoryStateMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(SaveEntryStateMessage.TYPE, SaveEntryStateMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(ReloadResourcesDoneMessage.TYPE, ReloadResourcesDoneMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(RequestSyncBookStatesMessage.TYPE, RequestSyncBookStatesMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(RequestAdvancementMessage.TYPE, RequestAdvancementMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(SendUnlockCodeToServerMessage.TYPE, SendUnlockCodeToServerMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(AddBookmarkMessage.TYPE, AddBookmarkMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(RemoveBookmarkMessage.TYPE, RemoveBookmarkMessage.STREAM_CODEC, MessageHandler::handle);

        registrar.playToClient(SendUnlockCodeToClientMessage.TYPE, SendUnlockCodeToClientMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SendAdvancementToClientMessage.TYPE, SendAdvancementToClientMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SyncBookDataMessage.TYPE, SyncBookDataMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SyncBookUnlockStatesMessage.TYPE, SyncBookUnlockStatesMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SyncBookVisualStatesMessage.TYPE, SyncBookVisualStatesMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SyncMultiblockDataMessage.TYPE, SyncMultiblockDataMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(ReloadResourcesOnClientMessage.TYPE, ReloadResourcesOnClientMessage.STREAM_CODEC, MessageHandler::handle);


    }

    public static <T extends Message> void sendToSplit(ServerPlayer player, T message) {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static <T extends Message> void sendTo(ServerPlayer player, T message) {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static <T extends Message> void sendToServer(T message) {
        PacketDistributor.sendToServer(message);
    }
}
