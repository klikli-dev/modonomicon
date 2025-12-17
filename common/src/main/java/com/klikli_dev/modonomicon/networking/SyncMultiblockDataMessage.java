/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SyncMultiblockDataMessage implements Message {


    public static final Type<SyncMultiblockDataMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "sync_multiblock_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncMultiblockDataMessage> STREAM_CODEC = CustomPacketPayload.codec(SyncMultiblockDataMessage::encode, SyncMultiblockDataMessage::new);

    public Map<Identifier, Multiblock> multiblocks = new Object2ObjectOpenHashMap<>();

    public SyncMultiblockDataMessage(Map<Identifier, Multiblock> multiblocks) {
        this.multiblocks = new Object2ObjectOpenHashMap<>(multiblocks);
    }

    public SyncMultiblockDataMessage(RegistryFriendlyByteBuf buf) {
        this.decode(buf);
    }

    private void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.multiblocks.size());
        for (var multiblock : this.multiblocks.values()) {
            buf.writeIdentifier(multiblock.getType());
            buf.writeIdentifier(multiblock.getId());
            multiblock.toNetwork(buf);
        }
    }

    private void decode(RegistryFriendlyByteBuf buf) {
        int multiblockCount = buf.readVarInt();
        for (int i = 0; i < multiblockCount; i++) {
            var type = buf.readIdentifier();
            var id = buf.readIdentifier();
            var multiblock = LoaderRegistry.getMultiblockNetworkLoader(type).fromNetwork(buf);
            multiblock.setId(id);
            this.multiblocks.put(multiblock.getId(), multiblock);
        }
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        MultiblockDataManager.get().registries(player.registryAccess());
        MultiblockDataManager.get().onDatapackSyncPacket(this);
    }
}
