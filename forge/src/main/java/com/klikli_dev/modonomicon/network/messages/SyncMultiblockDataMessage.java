/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SyncMultiblockDataMessage implements Message {

    public ConcurrentMap<ResourceLocation, Multiblock> multiblocks = new ConcurrentHashMap<>();

    public SyncMultiblockDataMessage(ConcurrentMap<ResourceLocation, Multiblock> multiblocks) {
        this.multiblocks = multiblocks;
    }

    public SyncMultiblockDataMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.multiblocks.size());
        for (var multiblock : this.multiblocks.values()) {
            buf.writeResourceLocation(multiblock.getType());
            buf.writeResourceLocation(multiblock.getId());
            multiblock.toNetwork(buf);
        }
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        int multiblockCount = buf.readVarInt();
        for (int i = 0; i < multiblockCount; i++) {
            var type = buf.readResourceLocation();
            var id = buf.readResourceLocation();
            var multiblock = LoaderRegistry.getMultiblockNetworkLoader(type).fromNetwork(buf);
            multiblock.setId(id);
            this.multiblocks.put(multiblock.getId(), multiblock);
        }
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player, Context context) {
        MultiblockDataManager.get().onDatapackSyncPacket(this);
    }
}
