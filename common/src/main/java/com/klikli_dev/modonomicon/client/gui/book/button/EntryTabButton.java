/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.book.entries.BookEntryTab;
import com.klikli_dev.modonomicon.book.entries.EntryTabSide;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryTabController;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * A side bookmark on the left or right edge of the book that opens an entry tab's page as an overlay.
 */
public class EntryTabButton extends Button {

    private static final int ICON_SIZE = 16;

    /**
     * Icons are scaled like the icons of the other side buttons (search, bookmarks), see {@link BookSideButtonRenderer#renderRightIcon}.
     */
    private static final float ICON_SCALE = 2f / 3f;
    private static final int SCALED_ICON_SIZE = Math.round(ICON_SIZE * ICON_SCALE);

    /**
     * Distance of the scaled icon from the outer edge of the tab, same as the search/bookmark icons
     * (11px inset + 4px offset, see the bookmark buttons' renderRightIcon call).
     */
    private static final int ICON_OUTER_INSET = 15;
    private static final int ICON_TOP_INSET = 2 + (ICON_SIZE - SCALED_ICON_SIZE) / 2;

    private final EntryTabController controller;
    private final BookEntryTab tab;
    private final int renderWidth;
    private final int scissorX;

    public EntryTabButton(EntryTabController controller, BookEntryTab tab, int pX, int pY, int scissorX, int width, int height, OnPress pOnPress, @Nullable Tooltip tooltip) {
        super(pX, pY, width - BookSideButtonRenderer.BUTTON_SLIDE_OFFSET, height, Component.literal(""), pOnPress, Button.DEFAULT_NARRATION);
        if (tooltip != null) {
            this.setTooltip(tooltip);
        }
        this.renderWidth = width;
        this.scissorX = scissorX;
        this.controller = controller;
        this.tab = tab;
    }

    public BookEntryTab getTab() {
        return this.tab;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }

        var theme = this.controller.getBook().theme();
        var content = theme.content();
        int xOffset = theme.layout().searchButtonXOffset();
        boolean active = this.controller.isOpen(this.tab);
        boolean left = this.tab.getSide() == EntryTabSide.LEFT;
        boolean hovered = this.isHovered();

        GuiSprite background;
        if (left) {
            background = active ? content.entryTabLeftActive() : content.entryTabLeft().state(hovered, false);
            BookSideButtonRenderer.renderSlidingButtonLeft(guiGraphics, xOffset, this.getX(), this.getY(), this.renderWidth, this.height,
                    this.scissorX, this.controller.getScreenHeight(), hovered, background, GuiSprite.EMPTY);
        } else {
            background = active ? content.entryTabRightActive() : content.entryTabRight().state(hovered, false);
            BookSideButtonRenderer.renderSlidingButton(guiGraphics, xOffset, this.getX(), this.getY(), this.renderWidth, this.height,
                    this.scissorX, this.controller.getScreenHeight(), hovered, background, GuiSprite.EMPTY);
        }

        int iconY = this.getY() + ICON_TOP_INSET;
        int iconX;
        if (left) {
            int renderX = this.getX() - (hovered ? 1 : 0);
            iconX = renderX + ICON_OUTER_INSET - xOffset;
        } else {
            int renderX = this.getX() - BookSideButtonRenderer.BUTTON_SLIDE_OFFSET + (hovered ? 1 : 0);
            iconX = renderX + this.renderWidth - ICON_OUTER_INSET - SCALED_ICON_SIZE + xOffset;
        }

        var explicitIcon = this.tab.getIcon();
        var stack = this.tab.getPageIconStack();
        if (explicitIcon == null && stack.isEmpty()) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(iconX, iconY);
        guiGraphics.pose().scale(ICON_SCALE, ICON_SCALE);
        if (explicitIcon != null) {
            explicitIcon.render(guiGraphics, 0, 0);
        } else {
            guiGraphics.item(stack, 0, 0);
        }
        guiGraphics.pose().popMatrix();
    }
}
