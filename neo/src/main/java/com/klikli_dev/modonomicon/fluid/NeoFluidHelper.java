/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.fluid;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.platform.services.FluidHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.List;

public class NeoFluidHelper implements FluidHelper {
    @Override
    public int getColorTint(FluidHolder fluidHolder) {
        FluidStack fluidStack = NeoFluidHolder.toStack(fluidHolder);
        Fluid fluid = fluidStack.getFluid();
        var fluidState = fluid.defaultFluidState();
        var fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);
        var tintSource = fluidModel.tintSource();
        int fluidColor = tintSource.color(fluidState.createLegacyBlock());
        return fluidColor | 0xFF000000;
    }

    @Override
    public List<Component> getTooltip(FluidHolder fluidHolder, int capacity, TooltipFlag tooltipFlag, TooltipMode tooltipMode) {
        FluidStack fluidStack = NeoFluidHolder.toStack(fluidHolder);
        Fluid fluidType = fluidStack.getFluid();
        try {
            if (fluidType.isSame(Fluids.EMPTY)) {
                return new ArrayList<>();
            }

            List<Component> tooltip = getTooltipBase(fluidStack, tooltipFlag);

            long amount = fluidStack.getAmount();
            long milliBuckets = (amount * 1000) / FluidType.BUCKET_VOLUME;

            if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
                MutableComponent amountString = Component.translatable(ModonomiconConstants.I18n.Tooltips.FLUID_AMOUNT_AND_CAPACITY, milliBuckets, capacity);
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            } else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
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
            Identifier identifier = BuiltInRegistries.FLUID.getKey(fluid);
            if (identifier != null) {
                MutableComponent advancedId = Component.literal(identifier.toString())
                        .withStyle(ChatFormatting.DARK_GRAY);
                tooltip.add(advancedId);
            }
        }

        return tooltip;
    }
}
