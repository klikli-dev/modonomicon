/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageHandler {

    public static <T extends Message> void handle(T message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
            ctx.get().enqueueWork(() -> {
                handleServer(message, ctx);
            });
        } else {
            ctx.get().enqueueWork(() -> {
                //separate class to avoid loading client code on server.
                //Using OnlyIn on a method in this class would work too, but is discouraged
                ClientMessageHandler.handleClient(message, ctx);
            });
        }
        ctx.get().setPacketHandled(true);
    }

    public static <T extends Message> void handleServer(T message, Supplier<NetworkEvent.Context> ctx) {
        MinecraftServer server = ctx.get().getSender().level().getServer();
        message.onServerReceived(server, ctx.get().getSender());
    }
}
