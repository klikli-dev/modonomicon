/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(method = "submitFeatures", at = @At("TAIL"))
    private void submitMultiblockPreview(LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, boolean renderBlockOutline, CallbackInfo ci) {
        MultiblockPreviewRenderer.submitRenderFeatures(levelRenderState, submitNodeCollector);
    }
}
