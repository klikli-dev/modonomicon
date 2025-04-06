/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
    @Inject(at = @At("TAIL"), method = "handleRecipeBookSettings(Lnet/minecraft/network/protocol/game/ClientboundRecipeBookSettingsPacket;)V")
    private void handleUpdateRecipes(ClientboundRecipeBookSettingsPacket pPacket, CallbackInfo info) {
        BookDataManager.get().onRecipesUpdated(Minecraft.getInstance().level);
    }
}