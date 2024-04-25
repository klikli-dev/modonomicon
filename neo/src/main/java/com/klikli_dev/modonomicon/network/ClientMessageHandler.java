/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientMessageHandler {

    public static <T extends Message> void handleClient(T message, IPayloadContext ctx) {
        var minecraft = Minecraft.getInstance();
        message.onClientReceived(minecraft, minecraft.player);
    }
}
