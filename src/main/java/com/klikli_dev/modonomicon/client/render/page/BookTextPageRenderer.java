/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.book.page.PageWithText;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookTextPageRenderer extends BookPageRenderer<BookTextPage> implements PageWithText {
    public BookTextPageRenderer(BookTextPage page) {
        super(page);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(this.page.getTitle(), this.page.showTitleSeparator(), poseStack, BookContentScreen.PAGE_WIDTH / 2, 0);
        }

        this.renderBookTextHolder(this.getPage().getText(), poseStack, 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
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
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        if (this.page.hasTitle()) {
            return this.page.showTitleSeparator() ? 22 : 12;
        }

        return -4;
    }
}
