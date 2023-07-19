/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import com.klikli_dev.modonomicon.platform.services.NetworkHelper;
import net.minecraft.server.level.ServerPlayer;

public class ForgeNetworkHelper implements NetworkHelper {
    @Override
    public <T extends Message> void sendTo(ServerPlayer player, T message) {
        Networking.sendTo(player, message);
    }

    @Override
    public <T extends Message> void sendToSplit(ServerPlayer player, T message) {
        Networking.sendToSplit(player, message);
    }

    @Override
    public <T extends Message> void sendToServer(T message) {
        Networking.sendToServer(message);
    }
}
