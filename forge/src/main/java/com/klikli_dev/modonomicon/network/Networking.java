/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.networking.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.function.BiConsumer;

public class Networking {
    public static final SimpleChannel INSTANCE = ChannelBuilder
            .named(new ResourceLocation(Modonomicon.MOD_ID, "main"))
            .clientAcceptedVersions((a, b) -> true)
            .serverAcceptedVersions((a, b) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {

        INSTANCE.messageBuilder(BookEntryReadMessage.class)
                .encoder(BookEntryReadMessage::encode)
                .decoder(BookEntryReadMessage::new)
                .consumerNetworkThread((BiConsumer<BookEntryReadMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ClickCommandLinkMessage.class)
                .encoder(ClickCommandLinkMessage::encode)
                .decoder(ClickCommandLinkMessage::new)
                .consumerNetworkThread((BiConsumer<ClickCommandLinkMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ClickReadAllButtonMessage.class)
                .encoder(ClickReadAllButtonMessage::encode)
                .decoder(ClickReadAllButtonMessage::new)
                .consumerNetworkThread((BiConsumer<ClickReadAllButtonMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SaveBookStateMessage.class)
                .encoder(SaveBookStateMessage::encode)
                .decoder(SaveBookStateMessage::new)
                .consumerNetworkThread((BiConsumer<SaveBookStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SaveCategoryStateMessage.class)
                .encoder(SaveCategoryStateMessage::encode)
                .decoder(SaveCategoryStateMessage::new)
                .consumerNetworkThread((BiConsumer<SaveCategoryStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(SaveEntryStateMessage.class)
                .encoder(SaveEntryStateMessage::encode)
                .decoder(SaveEntryStateMessage::new)
                .consumerNetworkThread((BiConsumer<SaveEntryStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SendUnlockCodeToClientMessage.class)
                .encoder(SendUnlockCodeToClientMessage::encode)
                .decoder(SendUnlockCodeToClientMessage::new)
                .consumerNetworkThread((BiConsumer<SendUnlockCodeToClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SendUnlockCodeToServerMessage.class)
                .encoder(SendUnlockCodeToServerMessage::encode)
                .decoder(SendUnlockCodeToServerMessage::new)
                .consumerNetworkThread((BiConsumer<SendUnlockCodeToServerMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(RequestAdvancementMessage.class)
                .encoder(RequestAdvancementMessage::encode)
                .decoder(RequestAdvancementMessage::new)
                .consumerNetworkThread((BiConsumer<RequestAdvancementMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookDataMessage.class)
                .encoder(SyncBookDataMessage::encode)
                .decoder(SyncBookDataMessage::new)
                .consumerNetworkThread((BiConsumer<SyncBookDataMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookUnlockStatesMessage.class)
                .encoder(SyncBookUnlockStatesMessage::encode)
                .decoder(SyncBookUnlockStatesMessage::new)
                .consumerNetworkThread((BiConsumer<SyncBookUnlockStatesMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookVisualStatesMessage.class)
                .encoder(SyncBookVisualStatesMessage::encode)
                .decoder(SyncBookVisualStatesMessage::new)
                .consumerNetworkThread((BiConsumer<SyncBookVisualStatesMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncMultiblockDataMessage.class)
                .encoder(SyncMultiblockDataMessage::encode)
                .decoder(SyncMultiblockDataMessage::new)
                .consumerNetworkThread((BiConsumer<SyncMultiblockDataMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ReloadResourcesOnClientMessage.class)
                .encoder(ReloadResourcesOnClientMessage::encode)
                .decoder(ReloadResourcesOnClientMessage::new)
                .consumerNetworkThread((BiConsumer<ReloadResourcesOnClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SendAdvancementToClientMessage.class)
                .encoder(SendAdvancementToClientMessage::encode)
                .decoder(SendAdvancementToClientMessage::new)
                .consumerNetworkThread((BiConsumer<SendAdvancementToClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ReloadResourcesDoneMessage.class)
                .encoder(ReloadResourcesDoneMessage::encode)
                .decoder(ReloadResourcesDoneMessage::new)
                .consumerNetworkThread((BiConsumer<ReloadResourcesDoneMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(RequestSyncBookStatesMessage.class)
                .encoder(RequestSyncBookStatesMessage::encode)
                .decoder(RequestSyncBookStatesMessage::new)
                .consumerNetworkThread((BiConsumer<RequestSyncBookStatesMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();
    }

    public static <T> void sendToSplit(ServerPlayer player, T message) {
        if (player.connection == null) {
            //workaround for https://github.com/klikli-dev/modonomicon/issues/46 / https://github.com/klikli-dev/modonomicon/issues/62
            //we should never get here unless some other mod interferes with networking
            Modonomicon.LOG.warn("Tried to send message of type {} to player without connection. Id: {}, Name: {}.", player.getStringUUID(), player.getName().getString(), message.getClass().getName());
            return;
        }
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }

    public static <T> void sendTo(ServerPlayer player, T message) {
        if (player.connection == null) {
            //workaround for https://github.com/klikli-dev/modonomicon/issues/46 / https://github.com/klikli-dev/modonomicon/issues/62
            //we should never get here unless some other mod interferes with networking
            Modonomicon.LOG.warn("Tried to send message of type {} to player without connection. Id: {}, Name: {}.", player.getStringUUID(), player.getName().getString(), message.getClass().getName());
            return;
        }
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }

    public static <T> void sendToServer(T message) {
        INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }
}
