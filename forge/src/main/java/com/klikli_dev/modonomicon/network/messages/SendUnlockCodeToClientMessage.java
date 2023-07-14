/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.network.messages;

import com.klikli_dev.modonomicon.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

public class SendUnlockCodeToClientMessage implements Message {

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
    public void onClientReceived(Minecraft minecraft, Player player, Context context) {
        Minecraft.getInstance().keyboardHandler.setClipboard(this.unlockCode);
    }
}
