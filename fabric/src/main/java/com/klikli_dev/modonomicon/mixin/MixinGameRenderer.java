/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.client.ClientTicks;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Inject(at = @At("HEAD"), method = "render(FJZ)V")
    public void renderHead(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        ClientTicks.renderTickStart(tickDelta);
    }

    @Inject(at = @At("RETURN"), method = "render(FJZ)V")
    public void renderReturn(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        ClientTicks.renderTickEnd();
    }
}
