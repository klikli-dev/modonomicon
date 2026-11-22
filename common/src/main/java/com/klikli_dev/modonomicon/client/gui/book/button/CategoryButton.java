/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class CategoryButton extends Button {

    private final BookParentNodeScreen parent;
    private final BookCategory category;

    public CategoryButton(BookParentNodeScreen parent, BookCategory category, int pX, int pY, int width, int height, OnPress pOnPress, Tooltip tooltip) {
        super(pX, pY, width, height, Component.literal(""), pOnPress, Button.DEFAULT_NARRATION);
        this.setTooltip(tooltip);
        this.parent = parent;
        this.category = category;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if (this.visible) {
            guiGraphics.pose().pushMatrix();
            int xOffset = this.getCategory().getBook().theme().layout().categoryButtonXOffset();
            guiGraphics.pose().translate(xOffset, 0);

            boolean selected = this.isSelected();
            int grow = this.getHighlightGrow(selected);
            int renderX = this.getX() - grow;
            int renderWidth = this.width + grow;
            var sprites = this.getCategory().getCategoryButtonSprites() != null
                    ? this.getCategory().getCategoryButtonSprites()
                    : this.parent.getBook().theme().content().categoryButton();
            var sprite = sprites.state(this.isHovered(), selected);

            int color;
            if (selected || this.isHovered()) {
                color = ARGB.colorFromFloat(1.0f, 1.0F, 1.0F, 1.0F);
            } else {
                color = ARGB.colorFromFloat(1.0f, 0.8F, 0.8F, 0.8F);
            }

            //draw category button background, extended under the frame by the theme's overlap
            sprite.extractRenderState(guiGraphics, renderX, this.getY(), renderWidth + this.getFrameOverlap(), this.height, color);

            //then draw icon
            int iconSize = 16;
            int centerIconOffset = iconSize / 2;
            float scale = this.getCategory().getBook().theme().layout().categoryButtonIconScale();
            //icon offsets are in screen pixels and applied before the scale, so they do not depend on the scale
            int iconXOffset = this.getCategory().getBook().theme().layout().categoryButtonIconXOffset();
            int iconYOffset = this.getCategory().getBook().theme().layout().categoryButtonIconYOffset();

            guiGraphics.pose().pushMatrix();
            //TODO had a +100 z here
            guiGraphics.pose().translate(renderX + 8 + iconXOffset, this.getY() + 2 + iconYOffset); //move to desired render location
            //not sure why in 1.21.6+ it is 2 instead of 20, but it works ... otherwise the icons render below
//            guiGraphics.pose().translate(renderX + 8, this.getY() + 20); //move to desired render location

            //now scale around center
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(centerIconOffset, centerIconOffset);
            guiGraphics.pose().scale(scale, scale);
            guiGraphics.pose().translate(-centerIconOffset, -centerIconOffset);

            this.category.getIcon().render(guiGraphics, 0, 0);

            //render a lock icon over the bottom right quarter of the category icon if locked
            var player = Minecraft.getInstance().player;
            if (player != null && !BookServices.visibility().isVisible(player, this.category)) {
                var lockSprite = this.category.getBook().theme().content().lockIcon();
                BookContentRenderer.drawSpriteRegion(guiGraphics, lockSprite,
                        iconSize / 2, iconSize / 2, iconSize / 2, iconSize / 2);
            }

            guiGraphics.pose().popMatrix();
            guiGraphics.pose().popMatrix();

            guiGraphics.pose().popMatrix();
        }
    }

    /**
     * Draws the unread indicator of this category button.
     * This is drawn separately, after the book frame, as the indicator overhangs the right edge of the button into the frame.
     */
    public void extractUnreadIndicator(GuiGraphicsExtractor guiGraphics) {
        if (this.visible
                && BookServices.visibility().isVisible(Minecraft.getInstance().player, this.category)
                && BookServices.interaction().isCategoryUnread(Minecraft.getInstance().player, this.category)) {
            guiGraphics.pose().pushMatrix();
            int xOffset = this.getCategory().getBook().theme().layout().categoryButtonXOffset();
            guiGraphics.pose().translate(xOffset, 0);

            //the indicator is anchored to the visible part of the button, so the frame overlap is not included
            int grow = this.getHighlightGrow(this.isSelected());
            int renderX = this.getX() - grow;
            int renderWidth = this.width + grow;
            BookContentRenderer.drawUnreadIndicator(guiGraphics, this.category.getBook(),
                    renderX + renderWidth - 11 + 2, this.getY() - 2, this.isHovered());

            guiGraphics.pose().popMatrix();
        }
    }

    private boolean isSelected() {
        return BookGuiManager.get().openBookCategoryScreen != null && this.category == BookGuiManager.get().openBookCategoryScreen.getCategory();
    }

    /**
     * How much the button grows to the left (and right) when selected or hovered.
     */
    private int getHighlightGrow(boolean selected) {
        if (selected) {
            return 3;
        }
        return this.isHovered() ? 1 : 0;
    }

    private int getFrameOverlap() {
        return Math.max(0, this.getCategory().getBook().theme().layout().sideButtonFrameOverlap());
    }
}
