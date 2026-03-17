/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookTextPageRenderer extends BookPageRenderer<BookTextPage> implements PageWithTextRenderer {
    public BookTextPageRenderer(BookTextPage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), this.page.showTitleSeparator(), BookEntryScreen.PAGE_WIDTH / 2, 0);
        }

        var textY = this.getTextY();
        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            //pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX + this.parentScreen.getBookLeft() + this.left, mouseY + this.parentScreen.getBookTop() + this.top);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            if (this.page.hasTitle()) {
                var titleStyle = this.getClickedComponentStyleAtForTitle(this.page.getTitle(), BookEntryScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
                if (titleStyle != null) {
                    return titleStyle;
                }
            }

            var bounds = this.getBookTextHolderBounds(0, this.getTextY(), BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - this.getTextY());
            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), bounds.x, bounds.y, bounds.width, bounds.height, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        if (this.page.hasTitle()) {
            return this.page.showTitleSeparator() ? 17 : 7;
        }

        return -4;
    }
}
