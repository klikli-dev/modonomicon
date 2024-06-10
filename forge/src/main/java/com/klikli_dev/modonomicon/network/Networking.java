/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.networking.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Networking {
    public static final SimpleChannel INSTANCE = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "main"))
            .clientAcceptedVersions((a, b) -> true)
            .serverAcceptedVersions((a, b) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static <MSG> BiConsumer<MSG, FriendlyByteBuf> encoder(StreamCodec<RegistryFriendlyByteBuf, MSG> codec) {
        return (msg, buf) -> {
            var rbuf = RegistryFriendlyByteBuf.decorator(getRegistryAccess()).apply(buf);
            codec.encode(rbuf, msg);
        };
    }

    public static <MSG> Function<FriendlyByteBuf, MSG> decoder(StreamCodec<RegistryFriendlyByteBuf, MSG> codec) {
        return (buf) -> {
            var rbuf = RegistryFriendlyByteBuf.decorator(getRegistryAccess()).apply(buf);
            return codec.decode(rbuf);
        };
    }

    public static RegistryAccess getRegistryAccess() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return DistHelper.getRegistryAccess();
        }
        return ServerLifecycleHooks.getCurrentServer().registryAccess();
    }

    public static void registerMessages() {
        INSTANCE.messageBuilder(BookEntryReadMessage.class)
                .encoder(encoder(BookEntryReadMessage.STREAM_CODEC))
                .decoder(decoder(BookEntryReadMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<BookEntryReadMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ClickCommandLinkMessage.class)
                .encoder(encoder(ClickCommandLinkMessage.STREAM_CODEC))
                .decoder(decoder(ClickCommandLinkMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<ClickCommandLinkMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ClickReadAllButtonMessage.class)
                .encoder(encoder(ClickReadAllButtonMessage.STREAM_CODEC))
                .decoder(decoder(ClickReadAllButtonMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<ClickReadAllButtonMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SaveBookStateMessage.class)
                .encoder(encoder(SaveBookStateMessage.STREAM_CODEC))
                .decoder(decoder(SaveBookStateMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SaveBookStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SaveCategoryStateMessage.class)
                .encoder(encoder(SaveCategoryStateMessage.STREAM_CODEC))
                .decoder(decoder(SaveCategoryStateMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SaveCategoryStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(SaveEntryStateMessage.class)
                .encoder(encoder(SaveEntryStateMessage.STREAM_CODEC))
                .decoder(decoder(SaveEntryStateMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SaveEntryStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SendUnlockCodeToClientMessage.class)
                .encoder(encoder(SendUnlockCodeToClientMessage.STREAM_CODEC))
                .decoder(decoder(SendUnlockCodeToClientMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SendUnlockCodeToClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SendUnlockCodeToServerMessage.class)
                .encoder(encoder(SendUnlockCodeToServerMessage.STREAM_CODEC))
                .decoder(decoder(SendUnlockCodeToServerMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SendUnlockCodeToServerMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(RequestAdvancementMessage.class)
                .encoder(encoder(RequestAdvancementMessage.STREAM_CODEC))
                .decoder(decoder(RequestAdvancementMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<RequestAdvancementMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookDataMessage.class)
                .encoder(encoder(SyncBookDataMessage.STREAM_CODEC))
                .decoder(decoder(SyncBookDataMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SyncBookDataMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookUnlockStatesMessage.class)
                .encoder(encoder(SyncBookUnlockStatesMessage.STREAM_CODEC))
                .decoder(decoder(SyncBookUnlockStatesMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SyncBookUnlockStatesMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookVisualStatesMessage.class)
                .encoder(encoder(SyncBookVisualStatesMessage.STREAM_CODEC))
                .decoder(decoder(SyncBookVisualStatesMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SyncBookVisualStatesMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncMultiblockDataMessage.class)
                .encoder(encoder(SyncMultiblockDataMessage.STREAM_CODEC))
                .decoder(decoder(SyncMultiblockDataMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SyncMultiblockDataMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ReloadResourcesOnClientMessage.class)
                .encoder(encoder(ReloadResourcesOnClientMessage.STREAM_CODEC))
                .decoder(decoder(ReloadResourcesOnClientMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<ReloadResourcesOnClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SendAdvancementToClientMessage.class)
                .encoder(encoder(SendAdvancementToClientMessage.STREAM_CODEC))
                .decoder(decoder(SendAdvancementToClientMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SendAdvancementToClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ReloadResourcesDoneMessage.class)
                .encoder(encoder(ReloadResourcesDoneMessage.STREAM_CODEC))
                .decoder(decoder(ReloadResourcesDoneMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<ReloadResourcesDoneMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(RequestSyncBookStatesMessage.class)
                .encoder(encoder(RequestSyncBookStatesMessage.STREAM_CODEC))
                .decoder(decoder(RequestSyncBookStatesMessage.STREAM_CODEC))
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

    public static class DistHelper {
        public static RegistryAccess getRegistryAccess() {
            return Minecraft.getInstance().level.registryAccess();
        }
    }
}
