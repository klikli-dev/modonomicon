/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.platform.services.PlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ReloadResourcesOnClientMessage implements Message {

    public static final ResourceLocation ID = new ResourceLocation(Modonomicon.MOD_ID, "reload_resources_on_client");

    public ReloadResourcesOnClientMessage() {
    }

    public ReloadResourcesOnClientMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        minecraft.reloadResourcePacks().thenRun(() -> {
            Services.NETWORK.sendToServer(new ReloadResourcesDoneMessage());
        });
    }
}
