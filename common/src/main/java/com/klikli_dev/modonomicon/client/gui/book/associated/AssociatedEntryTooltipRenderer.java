/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.associated;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.book.associated.AssociatedItemLookup;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the linked page/entry icon (16x16, with the modonomicon book icon as a
 * small badge in the bottom right corner) to the left of the title and hold hint.
 * <p>
 * Like vanilla's {@code ClientBundleTooltip}, all text is drawn from
 * {@link #extractImage} — custom {@code extractText} implementations are not
 * honored on every tooltip render path.
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

    //TODO(#259): remove diagnostic logging once the missing-text issue is resolved
    private static boolean loggedDraw;

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
        int textX = x + 1 + ICON_SIZE + GAP;
        graphics.text(font, this.tooltip.title(), textX, y + 1, 0xFFFFFF);
        graphics.text(font, this.tooltip.hint(), textX, y + 1 + LINE_HEIGHT + TEXT_GAP, 0xAAAAAA);

        if (!loggedDraw) {
            loggedDraw = true;
            Modonomicon.LOG.info("[AssociatedTooltip] extractImage at ({},{}) textX={} titleW={} hintW={} title='{}' hint='{}'",
                    x, y, textX, font.width(this.tooltip.title()), font.width(this.tooltip.hint()),
                    this.tooltip.title().getString(), this.tooltip.hint().getString());
        }

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
