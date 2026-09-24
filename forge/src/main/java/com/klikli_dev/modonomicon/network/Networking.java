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

    /**
     * Encoder for messages sent from the client to the server. Uses the client's registry access.
     */
    public static <MSG> BiConsumer<MSG, FriendlyByteBuf> clientEncoder(StreamCodec<RegistryFriendlyByteBuf, MSG> codec) {
        return (msg, buf) -> {
            var rbuf = RegistryFriendlyByteBuf.decorator(getClientRegistryAccess()).apply(buf);
            codec.encode(rbuf, msg);
        };
    }

    /**
     * Encoder for messages sent from the server to the client. Uses the server's registry access.
     * This must be used for clientbound messages: during login the integrated server encodes on the
     * server thread while the client level is still null, so reading the client level here throws an NPE.
     */
    public static <MSG> BiConsumer<MSG, FriendlyByteBuf> serverEncoder(StreamCodec<RegistryFriendlyByteBuf, MSG> codec) {
        return (msg, buf) -> {
            var rbuf = RegistryFriendlyByteBuf.decorator(getServerRegistryAccess()).apply(buf);
            codec.encode(rbuf, msg);
        };
    }

    public static <MSG> Function<FriendlyByteBuf, MSG> decoder(StreamCodec<RegistryFriendlyByteBuf, MSG> codec) {
        return (buf) -> {
            var rbuf = RegistryFriendlyByteBuf.decorator(getRegistryAccess()).apply(buf);
            return codec.decode(rbuf);
        };
    }

    /**
     * Decoder for messages received on the client. Uses the client's registry access.
     */
    public static <MSG> Function<FriendlyByteBuf, MSG> clientDecoder(StreamCodec<RegistryFriendlyByteBuf, MSG> codec) {
        return (buf) -> {
            var rbuf = RegistryFriendlyByteBuf.decorator(getClientRegistryAccess()).apply(buf);
            return codec.decode(rbuf);
        };
    }

    /**
     * Decoder for messages received on the server. Uses the server's registry access.
     */
    public static <MSG> Function<FriendlyByteBuf, MSG> serverDecoder(StreamCodec<RegistryFriendlyByteBuf, MSG> codec) {
        return (buf) -> {
            var rbuf = RegistryFriendlyByteBuf.decorator(getServerRegistryAccess()).apply(buf);
            return codec.decode(rbuf);
        };
    }

    public static RegistryAccess getRegistryAccess() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return DistHelper.getRegistryAccess();
        }
        return getServerRegistryAccess();
    }

    public static RegistryAccess getClientRegistryAccess() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return DistHelper.getRegistryAccess();
        }
        return getServerRegistryAccess();
    }

    public static RegistryAccess getServerRegistryAccess() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.registryAccess();
        }
        if (FMLEnvironment.dist == Dist.CLIENT) {
            // Should not happen when encoding/decoding on the logical server, but fall back
            // to the client instead of throwing an NPE.
            return DistHelper.getRegistryAccess();
        }
        throw new IllegalStateException("No server available to provide registry access.");
    }

    public static void registerMessages() {
        // Client -> server messages: encoded on the client, decoded on the server
        INSTANCE.messageBuilder(BookEntryReadMessage.class)
                .encoder(clientEncoder(BookEntryReadMessage.STREAM_CODEC))
                .decoder(serverDecoder(BookEntryReadMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<BookEntryReadMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ClickCommandLinkMessage.class)
                .encoder(clientEncoder(ClickCommandLinkMessage.STREAM_CODEC))
                .decoder(serverDecoder(ClickCommandLinkMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<ClickCommandLinkMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ClickReadAllButtonMessage.class)
                .encoder(clientEncoder(ClickReadAllButtonMessage.STREAM_CODEC))
                .decoder(serverDecoder(ClickReadAllButtonMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<ClickReadAllButtonMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SaveBookStateMessage.class)
                .encoder(clientEncoder(SaveBookStateMessage.STREAM_CODEC))
                .decoder(serverDecoder(SaveBookStateMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SaveBookStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SaveCategoryStateMessage.class)
                .encoder(clientEncoder(SaveCategoryStateMessage.STREAM_CODEC))
                .decoder(serverDecoder(SaveCategoryStateMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SaveCategoryStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(SaveEntryStateMessage.class)
                .encoder(clientEncoder(SaveEntryStateMessage.STREAM_CODEC))
                .decoder(serverDecoder(SaveEntryStateMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SaveEntryStateMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        // Server -> client messages: encoded on the server, decoded on the client.
        // The server side must use the server's registry access, because on an integrated
        // server the client level is still null while login packets are encoded.
        INSTANCE.messageBuilder(SendUnlockCodeToClientMessage.class)
                .encoder(serverEncoder(SendUnlockCodeToClientMessage.STREAM_CODEC))
                .decoder(clientDecoder(SendUnlockCodeToClientMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SendUnlockCodeToClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(OpenBookOnClientMessage.class)
                .encoder(serverEncoder(OpenBookOnClientMessage.STREAM_CODEC))
                .decoder(clientDecoder(OpenBookOnClientMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<OpenBookOnClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SendUnlockCodeToServerMessage.class)
                .encoder(clientEncoder(SendUnlockCodeToServerMessage.STREAM_CODEC))
                .decoder(serverDecoder(SendUnlockCodeToServerMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SendUnlockCodeToServerMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(RequestAdvancementMessage.class)
                .encoder(clientEncoder(RequestAdvancementMessage.STREAM_CODEC))
                .decoder(serverDecoder(RequestAdvancementMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<RequestAdvancementMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookDataMessage.class)
                .encoder(serverEncoder(SyncBookDataMessage.STREAM_CODEC))
                .decoder(clientDecoder(SyncBookDataMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SyncBookDataMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookUnlockStatesMessage.class)
                .encoder(serverEncoder(SyncBookUnlockStatesMessage.STREAM_CODEC))
                .decoder(clientDecoder(SyncBookUnlockStatesMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SyncBookUnlockStatesMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncBookVisualStatesMessage.class)
                .encoder(serverEncoder(SyncBookVisualStatesMessage.STREAM_CODEC))
                .decoder(clientDecoder(SyncBookVisualStatesMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SyncBookVisualStatesMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SyncMultiblockDataMessage.class)
                .encoder(serverEncoder(SyncMultiblockDataMessage.STREAM_CODEC))
                .decoder(clientDecoder(SyncMultiblockDataMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SyncMultiblockDataMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ReloadResourcesOnClientMessage.class)
                .encoder(serverEncoder(ReloadResourcesOnClientMessage.STREAM_CODEC))
                .decoder(clientDecoder(ReloadResourcesOnClientMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<ReloadResourcesOnClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(SendAdvancementToClientMessage.class)
                .encoder(serverEncoder(SendAdvancementToClientMessage.STREAM_CODEC))
                .decoder(clientDecoder(SendAdvancementToClientMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<SendAdvancementToClientMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(ReloadResourcesDoneMessage.class)
                .encoder(clientEncoder(ReloadResourcesDoneMessage.STREAM_CODEC))
                .decoder(serverDecoder(ReloadResourcesDoneMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<ReloadResourcesDoneMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();


        INSTANCE.messageBuilder(RequestSyncBookStatesMessage.class)
                .encoder(clientEncoder(RequestSyncBookStatesMessage.STREAM_CODEC))
                .decoder(serverDecoder(RequestSyncBookStatesMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<RequestSyncBookStatesMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(AddBookmarkMessage.class)
                .encoder(clientEncoder(AddBookmarkMessage.STREAM_CODEC))
                .decoder(serverDecoder(AddBookmarkMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<AddBookmarkMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();

        INSTANCE.messageBuilder(RemoveBookmarkMessage.class)
                .encoder(clientEncoder(RemoveBookmarkMessage.STREAM_CODEC))
                .decoder(serverDecoder(RemoveBookmarkMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<RemoveBookmarkMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
                .add();
                
        INSTANCE.messageBuilder(BookClosedMessage.class)
                .encoder(clientEncoder(BookClosedMessage.STREAM_CODEC))
                .decoder(serverDecoder(BookClosedMessage.STREAM_CODEC))
                .consumerNetworkThread((BiConsumer<BookClosedMessage, CustomPayloadEvent.Context>) MessageHandler::handle)
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
            var level = Minecraft.getInstance().level;
            if (level != null) {
                return level.registryAccess();
            }
            // During login the client level is not created yet (e.g. when the integrated
            // server encodes clientbound sync messages), so fall back to the connection's
            // registries and finally to the server's registries instead of throwing an NPE.
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                return connection.registryAccess();
            }
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                return server.registryAccess();
            }
            throw new IllegalStateException("No registry access available: client level, connection and server are all null.");
        }
    }
}
