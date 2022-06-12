/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public interface Message {
    void encode(FriendlyByteBuf buf);

    void decode(FriendlyByteBuf buf);

    default void onClientReceived(Minecraft minecraft, Player player, NetworkEvent.Context context) {

    }

    default void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player,
                                  NetworkEvent.Context context) {

    }
}
