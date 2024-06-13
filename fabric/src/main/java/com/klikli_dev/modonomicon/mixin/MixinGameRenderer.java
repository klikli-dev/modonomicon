/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.mixin;

import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.gui.FabricGuiHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/DeltaTracker;Z)V")
    public void renderHead(DeltaTracker deltaTracker, boolean bl, CallbackInfo info) {
        ClientTicks.renderTickStart(deltaTracker.getGameTimeDeltaPartialTick(bl));
    }

    @Inject(at = @At("RETURN"), method = "render(Lnet/minecraft/client/DeltaTracker;Z)V")
    public void renderReturn(DeltaTracker deltaTracker, boolean bl, CallbackInfo info) {
        ClientTicks.renderTickEnd();
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lorg/joml/Matrix4fStack;translation(FFF)Lorg/joml/Matrix4f;"))
    public Matrix4f renderMatrix4fStackTranslation(Matrix4fStack matrix4fStack, float x, float y, float z)
    {
        //Offset the gui far plane, we used the numbers from the uses of net.neoforged.neoforge.client.ClientHooks.getGuiFarPlane()
        return matrix4fStack.translation(0.0F, 0.0F, 10000 - FabricGuiHelper.getGuiFarPlane());
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lorg/joml/Matrix4f;setOrtho(FFFFFF)Lorg/joml/Matrix4f;"))
    public Matrix4f renderMatrix4fStackTranslation(Matrix4f matrix4f, float left, float right, float bottom, float top, float zNear, float zFar)
    {
        //Offset the gui far plane, we used the numbers from the uses of net.neoforged.neoforge.client.ClientHooks.getGuiFarPlane()
        return matrix4f.ortho(left, right, bottom, top, zNear, FabricGuiHelper.getGuiFarPlane());
    }
}
