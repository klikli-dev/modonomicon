/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.network.messages.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

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
    }

    public static <T> void sendTo(ServerPlayer player, T message) {
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
