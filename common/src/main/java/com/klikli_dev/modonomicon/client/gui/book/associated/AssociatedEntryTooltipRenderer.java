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
 * Renders the page/entry icon (16x16) with the modonomicon book icon as a small
 * badge in the bottom right corner to indicate a linked book page.
 */
public class AssociatedEntryTooltipRenderer implements ClientTooltipComponent {

    private static final int ICON_SIZE = 16;
    private static final int BADGE_SIZE = 8;

    private final AssociatedItemLookup.Association association;

    private BookIcon icon;
    private ItemStack badgeStack;
    private boolean resolved;

    public AssociatedEntryTooltipRenderer(AssociatedEntryTooltip tooltip) {
        this.association = tooltip.association();
    }

    private void resolve() {
        if (this.resolved) {
            return;
        }
        this.resolved = true;

        var entry = AssociatedItemLookup.get().resolveEntry(this.association);
        if (entry == null) {
            return;
        }

        var page = AssociatedItemLookup.get().resolvePage(this.association);
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

        this.icon.render(graphics, x, y);

        //small book badge in the bottom right corner of the page icon
        if (this.badgeStack != null && !this.badgeStack.isEmpty()) {
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(x + ICON_SIZE - BADGE_SIZE, y + ICON_SIZE - BADGE_SIZE);
            float scale = BADGE_SIZE / (float) ICON_SIZE;
            pose.scale(scale, scale);
            graphics.item(this.badgeStack, 0, 0);
            pose.popMatrix();
        }
    }
}
