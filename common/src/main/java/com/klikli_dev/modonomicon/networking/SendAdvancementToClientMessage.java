/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class SendAdvancementToClientMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "send_advancement_to_client");

    public ResourceLocation advancementId;
    public Advancement.Builder advancement;

    public SendAdvancementToClientMessage(ResourceLocation advancementId, Advancement.Builder advancement) {
        this.advancementId = advancementId;
        this.advancement = advancement;
    }

    public SendAdvancementToClientMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.advancementId);
       this.advancement.serializeToNetwork(buf);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.advancementId = buf.readResourceLocation();
        this.advancement = Advancement.Builder.fromNetwork(buf);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(player instanceof LocalPlayer localPlayer){
            localPlayer.connection.getAdvancements().getAdvancements().add(Map.of(this.advancementId, this.advancement));
        }
    }
}
