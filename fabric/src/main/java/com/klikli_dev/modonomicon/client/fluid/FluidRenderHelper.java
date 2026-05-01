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
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class FluidRenderHelper {
    public static int getColorTint(FluidHolder fluidHolder) {
        return getColorTint(new FabricFluidHolder(fluidHolder).toVariant());
    }

    private static int getColorTint(FluidVariant fluidVariant) {
        int fluidColor = FluidVariantRendering.getColor(fluidVariant);
        return fluidColor | 0xFF000000;
    }

    public static List<Component> getTooltip(FluidHolder fluidHolder, int capacity, TooltipFlag tooltipFlag, FluidHelper.TooltipMode tooltipMode) {
        var variant = new FabricFluidHolder(fluidHolder).toVariant();
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
