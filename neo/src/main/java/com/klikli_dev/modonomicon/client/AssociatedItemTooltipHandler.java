/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedEntryTooltip;
import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedEntryTooltipRenderer;
import com.klikli_dev.modonomicon.client.gui.book.associated.AssociatedTooltipHelper;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

public class AssociatedItemTooltipHandler {

    public static void onRegisterFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AssociatedEntryTooltip.class,
                tooltip -> new AssociatedEntryTooltipRenderer((AssociatedEntryTooltip) tooltip));
    }

    public static void onGatherComponents(RenderTooltipEvent.GatherComponents event) {
        var stack = event.getItemStack();

        var lines = new java.util.ArrayList<net.minecraft.network.chat.Component>();
        AssociatedTooltipHelper.gatherText(stack, lines);
        if (lines.isEmpty()) {
            return;
        }

        for (var line : lines) {
            event.getTooltipElements().add(Either.<FormattedText, TooltipComponent>left(line));
        }
        AssociatedTooltipHelper.gatherImage(stack)
                .ifPresent(image -> event.getTooltipElements().add(Either.right(image)));
    }
}
