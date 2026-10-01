/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.associated;

import com.klikli_dev.modonomicon.book.associated.AssociatedItemLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * Data-side tooltip component marking a stack as linked to a book entry or page.
 * <p>
 * Carries the resolved title and hint (including the live hold countdown) so the
 * renderer can draw the page icon to the left of the text. Created fresh on every
 * tooltip gather, which is what makes the countdown tick.
 * <p>
 * Attached to item tooltips by the loader tooltip hooks (Neo/Forge
 * {@code RenderTooltipEvent.GatherComponents}, Fabric item stack mixin)
 * and rendered by {@link AssociatedEntryTooltipRenderer}.
 */
public record AssociatedEntryTooltip(AssociatedItemLookup.Association association, Component title, Component hint)
        implements TooltipComponent {
}
