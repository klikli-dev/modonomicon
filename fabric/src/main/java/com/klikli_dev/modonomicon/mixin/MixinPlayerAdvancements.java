/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAdvancements.class)
public abstract class MixinPlayerAdvancements {

    @Accessor
    abstract ServerPlayer getPlayer();

    @Inject(at = @At("TAIL"), method = "award(Lnet/minecraft/advancements/Advancement;Ljava/lang/String;)Z")
    private void award(Advancement pAdvancement, String pCriterionKey, CallbackInfo info) {
        BookUnlockStateManager.get().onAdvancement(this.getPlayer());
    }
}