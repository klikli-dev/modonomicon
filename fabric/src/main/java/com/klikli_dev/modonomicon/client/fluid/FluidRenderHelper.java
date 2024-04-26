// SPDX-FileCopyrightText: 2023 klikli-dev
// SPDX-FileCopyrightText: 2023 mezz
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.fluid;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.fluid.FabricFluidHolder;
import com.klikli_dev.modonomicon.fluid.FluidHolder;
import com.klikli_dev.modonomicon.platform.services.FluidHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fluid rendering based on FluidHelper from JEI
 */
public class FluidRenderHelper {
    private static final int TEXTURE_SIZE = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    /**
     * @param guiGraphics the gui graphics.
     * @param width       the width of the fluid "slot" to render.
     * @param height      the height of the fluid "slot" to render.
     * @param fluidHolder the fluid stack representing the fluid and the amount to render.
     * @param capacity    the capacity of the fluid "slot" - together with the amount it determines the actual height of the fluid rendered within the slot.
     */
    public static void drawFluid(GuiGraphics guiGraphics, final int width, final int height, FabricFluidHolder fluidHolder, int capacity) {
        var fluidVariant = fluidHolder.toVariant();
        Fluid fluid = fluidHolder.getFluid().value();
        if (fluid.isSame(Fluids.EMPTY)) {
            return;
        }

        getStillFluidSprite(fluidVariant)
                .ifPresent(fluidStillSprite -> {
                    int fluidColor = getColorTint(fluidVariant);

                    long amount = fluidHolder.getAmount();
                    long scaledAmount = (amount * height) / capacity;
                    if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
                        scaledAmount = MIN_FLUID_HEIGHT;
                    }
                    if (scaledAmount > height) {
                        scaledAmount = height;
                    }

                    drawTiledSprite(guiGraphics, width, height, fluidColor, scaledAmount, fluidStillSprite);
                });
    }

    private static int getColorTint(FluidVariant fluidVariant) {
        int fluidColor = FluidVariantRendering.getColor(fluidVariant);
        return fluidColor | 0xFF000000;
    }

    private static Optional<TextureAtlasSprite> getStillFluidSprite(FluidVariant fluidVariant) {
        TextureAtlasSprite sprite = FluidVariantRendering.getSprite(fluidVariant);
        return Optional.ofNullable(sprite);
    }


    private static void drawTiledSprite(GuiGraphics guiGraphics, final int tiledWidth, final int tiledHeight, int color, long scaledAmount, TextureAtlasSprite sprite) {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        Matrix4f matrix = guiGraphics.pose().last().pose();
        setGLColorFromInt(color);

        final int xTileCount = tiledWidth / TEXTURE_SIZE;
        final int xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
        final long yTileCount = scaledAmount / TEXTURE_SIZE;
        final long yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

        final int yStart = tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : TEXTURE_SIZE;
                long height = (yTile == yTileCount) ? yRemainder : TEXTURE_SIZE;
                int x = (xTile * TEXTURE_SIZE);
                int y = yStart - ((yTile + 1) * TEXTURE_SIZE);
                if (width > 0 && height > 0) {
                    long maskTop = TEXTURE_SIZE - height;
                    int maskRight = TEXTURE_SIZE - width;

                    drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
        //now reset color
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, long maskTop, long maskRight, float zLevel) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();
        uMax = uMax - (maskRight / 16F * (uMax - uMin));
        vMax = vMax - (maskTop / 16F * (vMax - vMin));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
        bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
        bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
        bufferBuilder.vertex(matrix, xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
        tessellator.end();
    }

    public static List<Component> getTooltip(FabricFluidHolder fluidHolder, int capacity, TooltipFlag tooltipFlag, FluidHelper.TooltipMode tooltipMode) {
        var variant = fluidHolder.toVariant();
        Fluid fluidType = fluidHolder.getFluid().value();
        try {
            if (fluidType.isSame(Fluids.EMPTY)) {
                return new ArrayList<>();
            }

            List<Component> tooltip = FluidVariantRendering.getTooltip(variant);

            long amount = fluidHolder.getAmount();
            long milliBuckets = (amount * 1000) / FluidHolder.BUCKET_VOLUME;

            if (tooltipMode == FluidHelper.TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
                MutableComponent amountString = Component.translatable(ModonomiconConstants.I18n.Tooltips.FLUID_AMOUNT_AND_CAPACITY, milliBuckets, capacity);
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            } else if (tooltipMode == FluidHelper.TooltipMode.SHOW_AMOUNT) {
                MutableComponent amountString = Component.translatable(ModonomiconConstants.I18n.Tooltips.FLUID_AMOUNT, milliBuckets);
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            }
            return tooltip;
        } catch (RuntimeException e) {
            Component displayName = FluidVariantAttributes.getName(variant);
            Modonomicon.LOG.error("Failed to get tooltip for fluid: " + displayName, e);
        }

        return new ArrayList<>();
    }
}
