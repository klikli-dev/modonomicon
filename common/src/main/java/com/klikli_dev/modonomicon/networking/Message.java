/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface Message {
    void encode(FriendlyByteBuf buf);

    void decode(FriendlyByteBuf buf);

    /**
     * The id of the message for mod loaders that don't use incremental numeric ids, ie Fabric.
     */
    ResourceLocation getId();

    default void onClientReceived(Minecraft minecraft, Player player) {

    }

    default void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {

    }
}
