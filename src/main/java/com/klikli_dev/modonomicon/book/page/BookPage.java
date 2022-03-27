/*
 * LGPL-3-0
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

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.book.BookChapter;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.gui.book.markdown.MarkdownComponentRenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public abstract class BookPage {
    public BookChapter chapter;
    public BookContentScreen parentScreen;
    public BookTextRenderer textRenderer;

    public Minecraft mc;
    public Font font;
    public int pageNumber;
    public int left;
    public int top;

    public abstract ResourceLocation getType();

    public void init(BookChapter chapter, BookContentScreen parentScreen, BookTextRenderer textRenderer, int pageNumber, int left, int top) {
        this.chapter = chapter;
        this.parentScreen = parentScreen;
        this.textRenderer = textRenderer;

        this.mc = parentScreen.getMinecraft();
        this.font = this.mc.font;
        this.pageNumber = pageNumber;
        this.left = left;
        this.top = top;
    }


    public void renderBookTextHolder(BookTextHolder text, PoseStack poseStack, int x, int y, int width) {
        if (text.hasComponent()) {
            //if it is a component, we draw it directly
            for (FormattedCharSequence formattedcharsequence : this.font.split(text.getComponent(), width)) {
                this.font.draw(poseStack, formattedcharsequence, x, y, 0);
                y += this.font.lineHeight;
            }
        } else {
            //if it is not a component we render it from markdown
            var components = this.textRenderer.render(text.getString());

            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, width, width - 10, this.font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    this.font.draw(poseStack, formattedcharsequence, x, y, 0);
                    y += this.font.lineHeight;
                }
            }
        }
    }

    public abstract void render(PoseStack poseStack, int mouseX, int mouseY, float ticks);
}
