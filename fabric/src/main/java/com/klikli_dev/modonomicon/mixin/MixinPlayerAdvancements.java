/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public abstract class MixinPlayerAdvancements {

    @Accessor
    abstract ServerPlayer getPlayer();

    @Inject(at = @At("TAIL"), method = "award(Lnet/minecraft/advancements/AdvancementHolder;Ljava/lang/String;)Z")
    private void award(AdvancementHolder pAdvancement, String pCriterionKey, CallbackInfoReturnable<?> info) {
        if (ResearchServices.hooks().onAdvancement(this.getPlayer(), pAdvancement.id())) {
            ResearchStateManager.get().syncFor(this.getPlayer());
            BookVisualStateManager.get().syncFor(this.getPlayer());
        }
    }
}
