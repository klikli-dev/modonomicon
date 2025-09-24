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
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

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
    public static void drawFluid(GuiGraphics guiGraphics, final int width, final int height, FabricFluidHolder fluidHolder, int capacity, int x, int y) {
        var fluidVariant = fluidHolder.toVariant();
        Fluid fluid = fluidHolder.getFluid().value();
        if (fluid.isSame(Fluids.EMPTY)) {
            return;
        }

        getStillFluidSprite(fluidVariant)
                .ifPresent(fluidStillSprite -> {
                    int fluidColor = getColorTint(fluidVariant);

                    int amount = fluidHolder.getAmount();
                    int scaledAmount = (amount * height) / capacity;
                    if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
                        scaledAmount = MIN_FLUID_HEIGHT;
                    }
                    if (scaledAmount > height) {
                        scaledAmount = height;
                    }

                    GuiGraphicsExt.drawTiledSprite(guiGraphics, width, height, fluidColor, scaledAmount, fluidStillSprite, x, y);
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
