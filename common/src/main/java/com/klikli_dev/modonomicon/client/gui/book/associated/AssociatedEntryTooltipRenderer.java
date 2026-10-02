/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.associated;

import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.book.associated.AssociatedItemLookup;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the linked page/entry icon (16x16, with the modonomicon book icon as a
 * small badge in the bottom right corner) to the left of the title and hold hint.
 * <p>
 * Like vanilla's {@code ClientBundleTooltip}, all text is drawn from
 * {@link #extractImage}.
 * <p>
 * NOTE: text colors must carry a nonzero alpha byte (e.g. {@code -1} white,
 * {@code 0xFFAAAAAA} gray). {@code GuiGraphicsExtractor.text} silently drops any
 * call whose color alpha is zero, and bare RGB literals like {@code 0xFFFFFF}
 * are fully transparent.
 */
public class AssociatedEntryTooltipRenderer implements ClientTooltipComponent {

    private static final int ICON_SIZE = 16;
    private static final int GAP = 4;
    private static final int LINE_HEIGHT = 9;
    private static final int TEXT_GAP = 2;
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
        var book = entry.getBook() != null
                ? entry.getBook()
                : BookDataManager.get().getBook(this.tooltip.association().bookId());
        this.badgeStack = AssociatedTooltipHelper.resolveBadge(book);
    }

    private int textWidth(Font font) {
        return Math.max(font.width(this.tooltip.title()), font.width(this.tooltip.hint()));
    }

    @Override
    public int getHeight(Font font) {
        return Math.max(ICON_SIZE, LINE_HEIGHT + TEXT_GAP + LINE_HEIGHT) + 2;
    }

    @Override
    public int getWidth(Font font) {
        return ICON_SIZE + GAP + this.textWidth(font) + 2;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        //same call shape as vanilla ClientTextTooltip: opaque colors are mandatory, see note above
        int textX = x + 1 + ICON_SIZE + GAP;
        graphics.text(font, this.tooltip.title().getVisualOrderText(), textX, y + 1, -1, true);
        graphics.text(font, this.tooltip.hint().getVisualOrderText(), textX, y + 1 + LINE_HEIGHT + TEXT_GAP, 0xFFAAAAAA, true);

        this.resolve();
        if (this.icon == null) {
            return;
        }

        int iconY = y + (this.getHeight(font) - ICON_SIZE) / 2;
        this.icon.render(graphics, x + 1, iconY);

        //small book badge in the bottom right corner of the page icon
        if (this.badgeStack != null && !this.badgeStack.isEmpty()) {
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(x + 1 + ICON_SIZE - BADGE_SIZE, iconY + ICON_SIZE - BADGE_SIZE);
            float scale = BADGE_SIZE / (float) ICON_SIZE;
            pose.scale(scale, scale);
            graphics.item(this.badgeStack, 0, 0);
            pose.popMatrix();
        }
    }
}
