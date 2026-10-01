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
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

/**
 * TEMPORARY DIAGNOSTIC RENDERER for #259 (TODO: replace with final implementation).
 * <p>
 * Draws labeled test rows through every text path to isolate which one survives:
 * <ul>
 * <li>ET row via {@code extractText} with {@code forward()} (exactly what vanilla
 * {@code ClientTextTooltip} does for working text lines).</li>
 * <li>EI-FWD row via {@code extractImage} with {@code forward()} (bypasses Language).</li>
 * <li>EI-STR row via {@code extractImage} with a plain string (through Language).</li>
 * <li>EI-CMP row via {@code extractImage} with component visual order.</li>
 * </ul>
 * Plus the icon/badge (proves {@code extractImage} runs) and a red rect behind the
 * rows (proves fill states land).
 */
public class AssociatedEntryTooltipRenderer implements ClientTooltipComponent {

    private static final int ICON_SIZE = 16;
    private static final int GAP = 4;
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

    @Override
    public int getHeight(Font font) {
        return 58;
    }

    @Override
    public int getWidth(Font font) {
        return 240;
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        graphics.text(font, FormattedCharSequence.forward(">ET FWD-LOOP", Style.EMPTY), x + 1, y + 1, 0xFFFFFF);
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        if (!loggedDraw) {
            loggedDraw = true;
            Modonomicon.LOG.info("[AssociatedTooltip r4] probe extractImage at ({},{})", x, y);
        }

        int textX = x + 1 + ICON_SIZE + GAP;
        graphics.fill(textX, y + 12, textX + 200, y + 44, 0xFFFF0000);
        graphics.text(font, FormattedCharSequence.forward(">EI FWD IMAGE-LOOP", Style.EMPTY), textX, y + 12, 0xFFFFFF);
        graphics.text(font, ">EI STR " + this.tooltip.title().getString(), textX, y + 23, 0xFFFFFF);
        graphics.text(font, this.tooltip.title().getVisualOrderText(), textX, y + 34, 0xFFFFFF);

        this.resolve();
        if (this.icon == null) {
            return;
        }

        this.icon.render(graphics, x + 1, y + 12);

        //small book badge in the bottom right corner of the page icon
        if (this.badgeStack != null && !this.badgeStack.isEmpty()) {
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(x + 1 + ICON_SIZE - BADGE_SIZE, y + 12 + ICON_SIZE - BADGE_SIZE);
            float scale = BADGE_SIZE / (float) ICON_SIZE;
            pose.scale(scale, scale);
            graphics.item(this.badgeStack, 0, 0);
            pose.popMatrix();
        }
    }
}
