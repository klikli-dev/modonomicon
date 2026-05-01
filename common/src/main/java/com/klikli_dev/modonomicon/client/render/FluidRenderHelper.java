/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render;

import com.klikli_dev.modonomicon.fluid.FluidHolder;
import com.klikli_dev.modonomicon.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.world.level.material.Fluids;

public class FluidRenderHelper {
    private static final int MIN_FLUID_HEIGHT = 1;

    public static void drawFluid(GuiGraphicsExtractor guiGraphics, int width, int height, FluidHolder fluidHolder, int capacity, int x, int y) {
        if (fluidHolder.isEmpty() || fluidHolder.getFluid().value().isSame(Fluids.EMPTY) || capacity <= 0) {
            return;
        }

        var fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidHolder.getFluid().value().defaultFluidState());
        var sprite = fluidModel.stillMaterial().sprite();
        if (sprite == null) {
            return;
        }

        int amount = fluidHolder.getAmount();
        int scaledAmount = (amount * height) / capacity;
        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }
        if (scaledAmount <= 0) {
            return;
        }

        SpriteContents spriteContents = sprite.contents();
        GuiSpriteScaling.Tile tileScaling = new GuiSpriteScaling.Tile(spriteContents.width(), spriteContents.height());

        int renderY = y + height - scaledAmount;
        guiGraphics.enableScissor(x, renderY, x + width, renderY + scaledAmount);
        try {
            guiGraphics.blitTiledSprite(
                    RenderPipelines.GUI_TEXTURED,
                    sprite,
                    x,
                    renderY,
                    width,
                    scaledAmount,
                    0,
                    0,
                    tileScaling.width(),
                    tileScaling.height(),
                    tileScaling.width(),
                    tileScaling.height(),
                    ClientServices.FLUID.getColorTint(fluidHolder)
            );
        } finally {
            guiGraphics.disableScissor();
        }
    }
}
