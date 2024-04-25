/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SendAdvancementToClientMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "send_advancement_to_client");

    public AdvancementHolder advancement;

    public SendAdvancementToClientMessage(AdvancementHolder advancement) {
        this.advancement = advancement;
    }

    public SendAdvancementToClientMessage(RegistryFriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        AdvancementHolder.STREAM_CODEC.encode(buf, this.advancement);
    }

    @Override
    public void decode(RegistryFriendlyByteBuf buf) {
        this.advancement = AdvancementHolder.STREAM_CODEC.decode(buf);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        BookDataManager.Client.get().addAdvancement(this.advancement);
    }
}
