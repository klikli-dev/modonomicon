/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedEntryTooltip;
import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedEntryTooltipRenderer;
import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedHoverTracker;
import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedTooltipHelper;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;

import java.util.ArrayList;

public class AssociatedItemTooltipHandler {

    public static void onRegisterFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AssociatedEntryTooltip.class,
                tooltip -> new AssociatedEntryTooltipRenderer((AssociatedEntryTooltip) tooltip));
    }

    public static void onGatherComponents(RenderTooltipEvent.GatherComponents event) {
        var stack = event.getItemStack();
        AssociatedHoverTracker.onTooltipGather(stack);

        //TODO(#259): diagnostic control — unconditional vanilla text line. Remove after diagnosis.
        event.getTooltipElements().add(Either.left(Component.literal(">VANILLA APPEND")));

        //the single component renders icon (left of text), title and hold/locked hint
        AssociatedTooltipHelper.gather(stack)
                .ifPresent(image -> event.getTooltipElements().add(
                        Math.min(1, event.getTooltipElements().size()), Either.right(image)));
    }
}
