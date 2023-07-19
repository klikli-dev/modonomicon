/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SendUnlockCodeToClientMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "send_unlock_code_to_client");
    public String unlockCode;

    public SendUnlockCodeToClientMessage(String unlockCode) {
        this.unlockCode = unlockCode;
    }

    public SendUnlockCodeToClientMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.unlockCode);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.unlockCode = buf.readUtf();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Minecraft.getInstance().keyboardHandler.setClipboard(this.unlockCode);
    }
}
