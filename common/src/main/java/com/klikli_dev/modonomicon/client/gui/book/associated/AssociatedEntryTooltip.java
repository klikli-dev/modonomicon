/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.associated;

import com.klikli_dev.modonomicon.book.associated.AssociatedItemLookup;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * Data-side tooltip component marking a stack as linked to a book entry or page.
 * <p>
 * Attached to item tooltips by the loader tooltip hooks (Neo/Forge
 * {@code RenderTooltipEvent.GatherComponents}, Fabric item tooltip callback + mixin)
 * and rendered by {@link AssociatedEntryTooltipRenderer}.
 */
public record AssociatedEntryTooltip(AssociatedItemLookup.Association association) implements TooltipComponent {
}
