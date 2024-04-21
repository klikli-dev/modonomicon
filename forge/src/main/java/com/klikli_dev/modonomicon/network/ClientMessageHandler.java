/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.function.Supplier;

public class ClientMessageHandler {

    public static <T extends NeoMessageWrapper> void handleClient(T message, PlayPayloadContext ctx) {
        var minecraft = Minecraft.getInstance();
        message.message().onClientReceived(minecraft, minecraft.player);
    }
}
