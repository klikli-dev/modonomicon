/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.networking.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class Networking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(Modonomicon.MOD_ID);

        registrar.playToServer(BookEntryReadMessage.TYPE, BookEntryReadMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(BookCategoryReadMessage.TYPE, BookCategoryReadMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(ClickCommandLinkMessage.TYPE, ClickCommandLinkMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(ClickResearchProgressButtonMessage.TYPE, ClickResearchProgressButtonMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(SaveBookStateMessage.TYPE, SaveBookStateMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(SaveCategoryStateMessage.TYPE, SaveCategoryStateMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(SaveEntryStateMessage.TYPE, SaveEntryStateMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(ReloadResourcesDoneMessage.TYPE, ReloadResourcesDoneMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(RequestSyncResearchStateMessage.TYPE, RequestSyncResearchStateMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(AddBookmarkMessage.TYPE, AddBookmarkMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(RemoveBookmarkMessage.TYPE, RemoveBookmarkMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(BookClosedMessage.TYPE, BookClosedMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToServer(RequestSyncBookStatesMessage.TYPE, RequestSyncBookStatesMessage.STREAM_CODEC, MessageHandler::handle);

        registrar.playToClient(SyncBookDataMessage.TYPE, SyncBookDataMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SyncResearchDataMessage.TYPE, SyncResearchDataMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SyncResearchStateMessage.TYPE, SyncResearchStateMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SyncBookVisualStatesMessage.TYPE, SyncBookVisualStatesMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(SyncMultiblockDataMessage.TYPE, SyncMultiblockDataMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(ReloadResourcesOnClientMessage.TYPE, ReloadResourcesOnClientMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(OpenBookOnClientMessage.TYPE, OpenBookOnClientMessage.STREAM_CODEC, MessageHandler::handle);
        registrar.playToClient(ResearchToastMessage.TYPE, ResearchToastMessage.STREAM_CODEC, MessageHandler::handle);


    }

    public static <T extends Message> void sendToSplit(ServerPlayer player, T message) {
        if (!canSendTo(player, message)) {
            return;
        }
        PacketDistributor.sendToPlayer(player, message);
    }

    public static <T extends Message> void sendTo(ServerPlayer player, T message) {
        if (!canSendTo(player, message)) {
            return;
        }
        PacketDistributor.sendToPlayer(player, message);
    }

    private static <T extends Message> boolean canSendTo(ServerPlayer player, T message) {
        if (player.connection == null) {
            //should never happen unless some other mod interferes with networking
            Modonomicon.LOG.warn("Tried to send message of type {} to player without connection. Id: {}, Name: {}.",
                    message.getClass().getName(), player.getStringUUID(), player.getName().getString());
            return false;
        }
        //Fake players (e.g. in game tests) and clients without the mod have no channel for our payloads.
        //NeoForge throws when sending to them, so skip the message instead.
        if (!player.connection.hasChannel(message.type())) {
            Modonomicon.LOG.debug("Skipping message of type {} to player {} because the connection has no channel for it.",
                    message.getClass().getName(), player.getStringUUID());
            return false;
        }
        return true;
    }

    public static <T extends Message> void sendToServer(T message) {
        ClientPacketDistributor.sendToServer(message);
    }
}
