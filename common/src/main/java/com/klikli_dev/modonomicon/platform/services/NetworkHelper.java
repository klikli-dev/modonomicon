/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.platform.services;

import com.klikli_dev.modonomicon.networking.Message;
import net.minecraft.server.level.ServerPlayer;

public interface NetworkHelper {
    <T extends Message> void sendTo(ServerPlayer player, T message);

    <T extends Message> void sendToSplit(ServerPlayer player, T message);


    <T extends Message> void sendToServer(T message);
}