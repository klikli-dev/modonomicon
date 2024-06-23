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
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
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

            var x = this.parentScreen.getBook().getBookTextOffsetX();
            var y = this.getTextY() + this.parentScreen.getBook().getBookTextOffsetY();
            var width = BookEntryScreen.PAGE_WIDTH + this.parentScreen.getBook().getBookTextOffsetWidth() - x; //always remove the offset x from the width to avoid overflow
            var height = BookEntryScreen.PAGE_HEIGHT + this.parentScreen.getBook().getBookTextOffsetHeight() - y; //always remove the offset y from the height to avoid overflow

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), x, y, width, height, pMouseX, pMouseY);
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
