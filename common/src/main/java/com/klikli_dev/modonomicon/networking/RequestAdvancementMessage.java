/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class RequestAdvancementMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "request_advancement");

    public ResourceLocation advancementId;

    public RequestAdvancementMessage(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    public RequestAdvancementMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.advancementId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.advancementId = buf.readResourceLocation();
    }


    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var adv = minecraftServer.getAdvancements().getAdvancement(this.advancementId);

        if (adv != null) {
            Services.NETWORK.sendTo(player, new SendAdvancementToClientMessage(this.advancementId, adv.deconstruct()));
        } else {
            Modonomicon.LOG.warn("Requested Advancement {} from server, but not found", this.advancementId);
        }
    }
}
