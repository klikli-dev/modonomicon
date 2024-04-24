/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookSpotlightPageRenderer extends BookPageRenderer<BookSpotlightPage> implements PageWithTextRenderer {

    public static final int ITEM_X = BookContentScreen.PAGE_WIDTH / 2 - 8;
    public static final int ITEM_Y = 15;


    public BookSpotlightPageRenderer(BookSpotlightPage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookContentScreen.PAGE_WIDTH / 2, 0);
        }

        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        int w = 66;
        int h = 26;

        RenderSystem.enableBlend();
        guiGraphics.blit(this.page.getBook().getCraftingTexture(), BookContentScreen.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 256);

        this.parentScreen.renderIngredient(guiGraphics, ITEM_X, ITEM_Y, mouseX, mouseY, this.page.getItem());

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            if (this.page.hasTitle()) {
                var titleStyle = this.getClickedComponentStyleAtForTitle(this.page.getTitle(), BookContentScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
                if (titleStyle != null) {
                    return titleStyle;
                }
            }

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }

            //should not do item hover - that is handled by render ingredient, which also makes sure the tooltip does not go beyond the screen.
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        return 40;
    }
}
