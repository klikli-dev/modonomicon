/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SendUnlockCodeToClientMessage implements Message {

    public static final Type<SendUnlockCodeToClientMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "send_unlock_code_to_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SendUnlockCodeToClientMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            (m) -> m.unlockCode,
            SendUnlockCodeToClientMessage::new
    );

    public String unlockCode;

    public SendUnlockCodeToClientMessage(String unlockCode) {
        this.unlockCode = unlockCode;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Minecraft.getInstance().keyboardHandler.setClipboard(this.unlockCode);
    }
}
