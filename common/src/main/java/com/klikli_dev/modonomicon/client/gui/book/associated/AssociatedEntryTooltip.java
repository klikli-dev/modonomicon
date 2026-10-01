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
 * Only carries the association; the renderer draws the page icon (image components
 * render reliably) while title and hint travel as vanilla text lines, which are the
 * only text primitive that renders in every tooltip pipeline.
 * <p>
 * Attached to item tooltips by the loader tooltip hooks (Neo/Forge
 * {@code RenderTooltipEvent.GatherComponents}, Fabric item stack mixin)
 * and rendered by {@link AssociatedEntryTooltipRenderer}.
 */
public record AssociatedEntryTooltip(AssociatedItemLookup.Association association) implements TooltipComponent {
}
