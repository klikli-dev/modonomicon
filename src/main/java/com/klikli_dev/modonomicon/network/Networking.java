/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.network.messages.*;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.filters.VanillaPacketSplitter;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;

public class Networking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Modonomicon.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE.registerMessage(nextID(),
                SyncBookDataMessage.class,
                SyncBookDataMessage::encode,
                SyncBookDataMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                SyncMultiblockDataMessage.class,
                SyncMultiblockDataMessage::encode,
                SyncMultiblockDataMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                SyncBookUnlockCapabilityMessage.class,
                SyncBookUnlockCapabilityMessage::encode,
                SyncBookUnlockCapabilityMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                BookEntryReadMessage.class,
                BookEntryReadMessage::encode,
                BookEntryReadMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                SendUnlockCodeToClientMessage.class,
                SendUnlockCodeToClientMessage::encode,
                SendUnlockCodeToClientMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                SendUnlockCodeToServerMessage.class,
                SendUnlockCodeToServerMessage::encode,
                SendUnlockCodeToServerMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                SyncBookStateCapabilityMessage.class,
                SyncBookStateCapabilityMessage::encode,
                SyncBookStateCapabilityMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                SaveEntryStateMessage.class,
                SaveEntryStateMessage::encode,
                SaveEntryStateMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                SaveCategoryStateMessage.class,
                SaveCategoryStateMessage::encode,
                SaveCategoryStateMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                SaveBookStateMessage.class,
                SaveBookStateMessage::encode,
                SaveBookStateMessage::new,
                MessageHandler::handle);

        INSTANCE.registerMessage(nextID(),
                ClickReadAllButtonMessage.class,
                ClickReadAllButtonMessage::encode,
                ClickReadAllButtonMessage::new,
                MessageHandler::handle);
    }

    public static <T> void sendToSplit(ServerPlayer player, T message) {
        Packet<?> vanillaPacket = INSTANCE.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT);
        var packets = new ArrayList<Packet<?>>();
        VanillaPacketSplitter.appendPackets(ConnectionProtocol.PLAY, PacketFlow.CLIENTBOUND, vanillaPacket, packets);
        packets.forEach(player.connection::send);
    }

    public static <T> void sendTo(ServerPlayer player, T message) {
        if(player.connection == null){
            //workaround for https://github.com/klikli-dev/modonomicon/issues/46 / https://github.com/klikli-dev/modonomicon/issues/62
            //we should never get here unless some other mod interferes with networking
            Modonomicon.LOGGER.warn("Tried to send message of type {} to player without connection. Id: {}, Name: {}.", player.getStringUUID(), player.getName().getString(), message.getClass().getName());
            return;
        }
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <T> void sendToTracking(Entity entity, T message) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    public static <T> void sendToDimension(ResourceKey<Level> dimensionKey, T message) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dimensionKey), message);
    }

    public static <T> void sendToServer(T message) {
        INSTANCE.sendToServer(message);
    }
}
