// SPDX-FileCopyrightText: 2023 klikli-dev
// SPDX-FileCopyrightText: 2023 mezz
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.fluid;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.platform.services.FluidHelper;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fluid rendering based on FluidTankRenderer from JEI
 */
public class FluidRenderHelper {
    private static final int TEXTURE_SIZE = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    /**
     * @param guiGraphics the gui graphics.
     * @param width       the width of the fluid "slot" to render.
     * @param height      the height of the fluid "slot" to render.
     * @param fluidStack  the fluid stack representing the fluid and the amount to render.
     * @param capacity    the capacity of the fluid "slot" - together with the amount it determines the actual height of the fluid rendered within the slot.
     */
    public static void drawFluid(GuiGraphics guiGraphics, final int width, final int height, FluidStack fluidStack, int capacity, int x, int y) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid.isSame(Fluids.EMPTY)) {
            return;
        }

        getStillFluidSprite(fluidStack)
                .ifPresent(fluidStillSprite -> {
                    int fluidColor = getColorTint(fluidStack);

                    int amount = fluidStack.getAmount();
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

    private static int getColorTint(FluidStack ingredient) {
        Fluid fluid = ingredient.getFluid();
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        return renderProperties.getTintColor(ingredient);
    }

    private static Optional<TextureAtlasSprite> getStillFluidSprite(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        Identifier fluidStill = renderProperties.getStillTexture(fluidStack);

        //noinspection deprecation
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getAtlasManager().getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS)
                .getSprite(fluidStill);
        return Optional.of(sprite)
                .filter(s -> s.atlasLocation() != MissingTextureAtlasSprite.getLocation());
    }

    public static List<Component> getTooltip(FluidStack fluidStack, int capacity, TooltipFlag tooltipFlag, FluidHelper.TooltipMode tooltipMode) {
        Fluid fluidType = fluidStack.getFluid();
        try {
            if (fluidType.isSame(Fluids.EMPTY)) {
                return new ArrayList<>();
            }

            List<Component> tooltip = getTooltipBase(fluidStack, tooltipFlag);

            long amount = fluidStack.getAmount();
            long milliBuckets = (amount * 1000) / FluidType.BUCKET_VOLUME;

            if (tooltipMode == FluidHelper.TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
                MutableComponent amountString = Component.translatable(ModonomiconConstants.I18n.Tooltips.FLUID_AMOUNT_AND_CAPACITY, milliBuckets, capacity);
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            } else if (tooltipMode == FluidHelper.TooltipMode.SHOW_AMOUNT) {
                MutableComponent amountString = Component.translatable(ModonomiconConstants.I18n.Tooltips.FLUID_AMOUNT, milliBuckets);
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            }
            return tooltip;
        } catch (RuntimeException e) {
            Component displayName = fluidStack.getHoverName();
            Modonomicon.LOG.error("Failed to get tooltip for fluid: " + displayName, e);
        }

        return new ArrayList<>();
    }

    private static List<Component> getTooltipBase(FluidStack fluidStack, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        Fluid fluid = fluidStack.getFluid();
        if (fluid.isSame(Fluids.EMPTY)) {
            return tooltip;
        }

        Component displayName = fluidStack.getHoverName();
        tooltip.add(displayName);

        if (tooltipFlag.isAdvanced()) {
            Identifier Identifier = BuiltInRegistries.FLUID.getKey(fluid);
            if (Identifier != null) {
                MutableComponent advancedId = Component.literal(Identifier.toString())
                        .withStyle(ChatFormatting.DARK_GRAY);
                tooltip.add(advancedId);
            }
        }

        return tooltip;
    }


}
