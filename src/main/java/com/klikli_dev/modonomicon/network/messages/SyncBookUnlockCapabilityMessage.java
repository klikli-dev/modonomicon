/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import com.klikli_dev.modonomicon.network.Message;
import com.klikli_dev.modonomicon.registry.CapabilityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncBookUnlockCapabilityMessage implements Message {

    public CompoundTag tag;

    public SyncBookUnlockCapabilityMessage(BookUnlockCapability capability) {
        this.tag = capability.serializeNBT();
    }

    public SyncBookUnlockCapabilityMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player, Context context) {
        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(capability -> {
                    capability.deserializeNBT(this.tag);
                });
    }
}
