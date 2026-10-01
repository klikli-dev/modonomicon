/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.associated;

import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.book.associated.AssociatedItemLookup;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the linked page/entry icon (16x16) with the modonomicon book icon as a
 * small badge in the bottom right corner to indicate a linked book page.
 * <p>
 * Icon-only on purpose: title and hint are added as vanilla text lines by
 * {@link AssociatedTooltipHelper#gatherText}, because custom-component text does
 * not reach pixels on all tooltip render paths while image/fill draws do.
 */
public class AssociatedEntryTooltipRenderer implements ClientTooltipComponent {

    private static final int ICON_SIZE = 16;
    private static final int BADGE_SIZE = 8;

    private final AssociatedEntryTooltip tooltip;

    private BookIcon icon;
    private ItemStack badgeStack;
    private boolean resolved;

    public AssociatedEntryTooltipRenderer(AssociatedEntryTooltip tooltip) {
        this.tooltip = tooltip;
    }

    private void resolve() {
        if (this.resolved) {
            return;
        }
        this.resolved = true;

        var entry = AssociatedItemLookup.get().resolveEntry(this.tooltip.association());
        if (entry == null) {
            return;
        }

        var page = AssociatedItemLookup.get().resolvePage(this.tooltip.association());
        this.icon = AssociatedTooltipHelper.resolveIcon(entry, page);
        this.badgeStack = new ItemStack(ItemRegistry.MODONOMICON_PURPLE.get());
    }

    @Override
    public int getHeight(Font font) {
        return ICON_SIZE + 2;
    }

    @Override
    public int getWidth(Font font) {
        return ICON_SIZE + 2;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        this.resolve();
        if (this.icon == null) {
            return;
        }

        this.icon.render(graphics, x + 1, y + 1);

        //small book badge in the bottom right corner of the page icon
        if (this.badgeStack != null && !this.badgeStack.isEmpty()) {
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(x + 1 + ICON_SIZE - BADGE_SIZE, y + 1 + ICON_SIZE - BADGE_SIZE);
            float scale = BADGE_SIZE / (float) ICON_SIZE;
            pose.scale(scale, scale);
            graphics.item(this.badgeStack, 0, 0);
            pose.popMatrix();
        }
    }
}
