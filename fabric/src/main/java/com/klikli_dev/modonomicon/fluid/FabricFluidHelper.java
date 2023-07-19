/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.fluid;

import com.klikli_dev.modonomicon.client.fluid.FluidRenderHelper;
import com.klikli_dev.modonomicon.platform.services.FluidHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class FabricFluidHelper implements FluidHelper {
    @Override
    public void drawFluid(GuiGraphics guiGraphics, int width, int height, FluidHolder fluidHolder, int capacity) {
        FluidRenderHelper.drawFluid(guiGraphics, width, height, new FabricFluidHolder(fluidHolder), capacity);
    }

    @Override
    public List<Component> getTooltip(FluidHolder fluidHolder, int capacity, TooltipFlag tooltipFlag, TooltipMode tooltipMode) {
        return FluidRenderHelper.getTooltip(new FabricFluidHolder(fluidHolder), capacity, tooltipFlag, tooltipMode);
    }
}
