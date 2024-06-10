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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SendAdvancementToClientMessage implements Message {

    public static final Type<SendAdvancementToClientMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "send_advancement_to_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SendAdvancementToClientMessage> STREAM_CODEC = StreamCodec.composite(
            AdvancementHolder.STREAM_CODEC,
            (m) -> m.advancement,
            SendAdvancementToClientMessage::new
    );

    public AdvancementHolder advancement;

    public SendAdvancementToClientMessage(AdvancementHolder advancement) {
        this.advancement = advancement;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        BookDataManager.Client.get().addAdvancement(this.advancement);
    }
}
