/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;

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

            int renderX = this.getX();
            int renderWidth = this.width;
            boolean selected = BookGuiManager.get().openBookCategoryScreen != null && this.category == BookGuiManager.get().openBookCategoryScreen.getCategory();
            var sprite = this.parent.getBook().theme().overview().categoryButton().state(this.isHovered(), selected);

            int color;
            if (selected) {
                renderX -= 3;
                renderWidth += 3;
                color = ARGB.colorFromFloat(1.0f, 1.0F, 1.0F, 1.0F);
            } else if (this.isHovered()) {
                renderX -= 1;
                renderWidth += 1;
                color = ARGB.colorFromFloat(1.0f, 1.0F, 1.0F, 1.0F);
            } else {
                color = ARGB.colorFromFloat(1.0f, 0.8F, 0.8F, 0.8F);
            }

            //draw category button background
            GuiGraphicsExt.blitSpriteRegion(guiGraphics, RenderPipelines.GUI_TEXTURED, sprite, renderX, this.getY(), 0, 0, renderWidth, this.height, color);

            //then draw icon
            int iconSize = 16;
            int centerIconOffset = iconSize / 2;
            float scale = this.getCategory().getBook().theme().layout().categoryButtonIconScale();

            guiGraphics.pose().pushMatrix();
            //TODO had a +100 z here
            guiGraphics.pose().translate(renderX + 8, this.getY() + 2); //move to desired render location
            //not sure why in 1.21.6+ it is 2 instead of 20, but it works ... otherwise the icons render below
//            guiGraphics.pose().translate(renderX + 8, this.getY() + 20); //move to desired render location

            //now scale around center
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(centerIconOffset, centerIconOffset);
            guiGraphics.pose().scale(scale, scale);
            guiGraphics.pose().translate(-centerIconOffset, -centerIconOffset);

            this.category.getIcon().render(guiGraphics, 0, 0);

            guiGraphics.pose().popMatrix();
            guiGraphics.pose().popMatrix();

            //render unread category indicator
            if (!BookUnlockStateManager.get().isCategoryReadFor(Minecraft.getInstance().player, this.category)) {
                BookContentRenderer.drawUnreadIndicator(guiGraphics, this.category.getBook(),
                        renderX + renderWidth - 11 + 2, this.getY() - 2, this.isHovered());
            }

            guiGraphics.pose().popMatrix();
        }
    }
}
