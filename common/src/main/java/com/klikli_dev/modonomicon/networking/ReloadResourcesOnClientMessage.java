/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ReloadResourcesOnClientMessage implements Message {

    public static final ReloadResourcesOnClientMessage INSTANCE = new ReloadResourcesOnClientMessage();

    public static final Type<ReloadResourcesOnClientMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, "reload_resources_on_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadResourcesOnClientMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ReloadResourcesOnClientMessage() {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        minecraft.reloadResourcePacks().thenRun(() -> {
            Services.NETWORK.sendToServer(ReloadResourcesDoneMessage.INSTANCE);
        });
    }
}
