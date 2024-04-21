/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MessageHandler {

    public static <T extends NeoMessageWrapper> void handle(T message, PlayPayloadContext ctx) {
        if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
            ctx.workHandler().submitAsync(() -> {
                handleServer(message, ctx);
            });
        } else {
            ctx.workHandler().submitAsync(() -> {
                //separate class to avoid loading client code on server.
                //Using OnlyIn on a method in this class would work too, but is discouraged
                ClientMessageHandler.handleClient(message, ctx);
            });
        }
    }

    public static <T extends NeoMessageWrapper> void handleServer(T message, PlayPayloadContext ctx) {
        MinecraftServer server = ctx.level().get().getServer();
        message.message().onServerReceived(server, (ServerPlayer) ctx.player().get());
    }
}
