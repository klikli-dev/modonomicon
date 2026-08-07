/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.networking.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;

public class Networking {
    public static final Channel<CustomPacketPayload> INSTANCE = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "main"))
            .clientAcceptedVersions((a, b) -> true)
            .serverAcceptedVersions((a, b) -> true)
            .networkProtocolVersion(1)
            .payloadChannel()
            .play()
            .serverbound()
            .addMain(BookEntryReadMessage.TYPE, BookEntryReadMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(BookCategoryReadMessage.TYPE, BookCategoryReadMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(ClickCommandLinkMessage.TYPE, ClickCommandLinkMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(ClickResearchProgressButtonMessage.TYPE, ClickResearchProgressButtonMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(SaveBookStateMessage.TYPE, SaveBookStateMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(SaveCategoryStateMessage.TYPE, SaveCategoryStateMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(SaveEntryStateMessage.TYPE, SaveEntryStateMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(ReloadResourcesDoneMessage.TYPE, ReloadResourcesDoneMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(RequestSyncResearchStateMessage.TYPE, RequestSyncResearchStateMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(AddBookmarkMessage.TYPE, AddBookmarkMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(RemoveBookmarkMessage.TYPE, RemoveBookmarkMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(BookClosedMessage.TYPE, BookClosedMessage.STREAM_CODEC, MessageHandler::handle)
            .clientbound()
            .addMain(SyncBookDataMessage.TYPE, SyncBookDataMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(SyncResearchDataMessage.TYPE, SyncResearchDataMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(SyncResearchStateMessage.TYPE, SyncResearchStateMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(SyncBookVisualStatesMessage.TYPE, SyncBookVisualStatesMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(SyncMultiblockDataMessage.TYPE, SyncMultiblockDataMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(ReloadResourcesOnClientMessage.TYPE, ReloadResourcesOnClientMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(OpenBookOnClientMessage.TYPE, OpenBookOnClientMessage.STREAM_CODEC, MessageHandler::handle)
            .addMain(ResearchToastMessage.TYPE, ResearchToastMessage.STREAM_CODEC, MessageHandler::handle)
            .build();

    public static void registerMessages() {
        //messages are registered when INSTANCE is built
    }

    public static <T extends Message> void sendToSplit(ServerPlayer player, T message) {
        if (player.connection == null) {
            //workaround for https://github.com/klikli-dev/modonomicon/issues/46 / https://github.com/klikli-dev/modonomicon/issues/62
            //we should never get here unless some other mod interferes with networking
            Modonomicon.LOG.warn("Tried to send message of type {} to player without connection. Id: {}, Name: {}.", player.getStringUUID(), player.getName().getString(), message.getClass().getName());
            return;
        }
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }

    public static <T extends Message> void sendTo(ServerPlayer player, T message) {
        if (player.connection == null) {
            //workaround for https://github.com/klikli-dev/modonomicon/issues/46 / https://github.com/klikli-dev/modonomicon/issues/62
            //we should never get here unless some other mod interferes with networking
            Modonomicon.LOG.warn("Tried to send message of type {} to player without connection. Id: {}, Name: {}.", player.getStringUUID(), player.getName().getString(), message.getClass().getName());
            return;
        }
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }

    public static <T extends Message> void sendToServer(T message) {
        INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }
}
