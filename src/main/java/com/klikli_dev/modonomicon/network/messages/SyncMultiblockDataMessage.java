/*
 * LGPL-3.0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.HashMap;
import java.util.Map;

public class SyncMultiblockDataMessage implements Message {

    public Map<ResourceLocation, Multiblock> multiblocks = new HashMap<>();

    public SyncMultiblockDataMessage(Map<ResourceLocation, Multiblock> multiblocks) {
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
