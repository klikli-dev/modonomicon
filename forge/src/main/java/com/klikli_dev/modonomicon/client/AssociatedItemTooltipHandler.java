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

        //icon row directly under the item name, then title and hold/locked hint as
        //vanilla text lines (the only text primitive that renders on all paths)
        AssociatedTooltipHelper.gatherImage(stack)
                .ifPresent(image -> event.getTooltipElements().add(
                        Math.min(1, event.getTooltipElements().size()), Either.right(image)));

        var lines = new ArrayList<Component>();
        AssociatedTooltipHelper.gatherText(stack, lines);
        int insertAt = Math.min(2, event.getTooltipElements().size());
        for (var line : lines) {
            event.getTooltipElements().add(insertAt++, Either.<FormattedText, TooltipComponent>left(line));
        }
    }
}
