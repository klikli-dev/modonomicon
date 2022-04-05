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

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.gui.book.markdown.MarkdownComponentRenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;

public abstract class BookPage {
    public int left;
    public int top;
    protected BookContentScreen parentScreen;
    protected Minecraft mc;
    protected Font font;

    protected Book book;
    protected BookEntry parentEntry;
    protected int pageNumber;

    protected String anchor;

    public BookPage(String anchor) {
        this.anchor = anchor;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public abstract ResourceLocation getType();

    public void build(BookEntry parentEntry, int pageNum) {
        this.parentEntry = parentEntry;
        this.pageNumber = pageNum;
        this.book = this.parentEntry.getBook();
    }

    /**
     * Call when the page is being set up to be displayed (when book content screen opens, or pages are changed)
     */
    public void onBeginDisplayPage(BookContentScreen parentScreen, BookTextRenderer textRenderer, int left, int top) {
        this.parentScreen = parentScreen;

        this.mc = parentScreen.getMinecraft();
        this.font = this.mc.font;
        this.left = left;
        this.top = top;
    }

    /**
     * Call when the page is will no longer be displayed (when book content screen opens, or pages are changed)
     */
    public void onEndDisplayPage(BookContentScreen parentScreen) {
    }

    /**
     * @param pMouseX localized to page x (mouseX - bookLeft - page.left)
     * @param pMouseY localized to page y (mouseY - bookTop - page.top)
     */
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    /**
     * Will render the given BookTextHolder as (left-aligned) content text. Will automatically handle markdown.
     */
    public void renderBookTextHolder(BookTextHolder text, PoseStack poseStack, int x, int y, int width) {
        if (text.hasComponent()) {
            //if it is a component, we draw it directly
            for (FormattedCharSequence formattedcharsequence : this.font.split(text.getComponent(), width)) {
                this.font.draw(poseStack, formattedcharsequence, x, y, 0);
                y += this.font.lineHeight;
            }
        } else if (text instanceof RenderedBookTextHolder renderedText) {
            //if it is not a component it was sent through the markdown renderer
            var components = renderedText.getRenderedText();

            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, width, width - 10, this.font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    this.font.draw(poseStack, formattedcharsequence, x, y, 0);
                    y += this.font.lineHeight;
                }
            }
        } else {
            Modonomicon.LOGGER.warn("BookTextHolder with String {} has no component, but is not rendered to markdown either.", text.getString());
        }
    }

    /**
     * Will render the given BookTextHolder as (centered) title.
     */
    public void renderTitle(BookTextHolder title, boolean showTitleSeparator, PoseStack poseStack, int x, int y) {
        if (title instanceof RenderedBookTextHolder renderedTitle) {
            //if user decided to use markdown title, we need to use the  rendered version
            var formattedCharSequence = FormattedCharSequence.fromList(
                    renderedTitle.getRenderedText().stream().map(BaseComponent::getVisualOrderText).toList());
            this.drawCenteredStringNoShadow(poseStack, formattedCharSequence, x, y, 0);
        } else {
            //otherwise we use the component - that is either provided by the user, or created from the default title style.
            this.drawCenteredStringNoShadow(poseStack, title.getComponent().getVisualOrderText(), x, y, 0);
        }

        if (showTitleSeparator)
            BookContentScreen.drawTitleSeparator(poseStack, this.book, x, y + 12);
    }

    public abstract void render(PoseStack poseStack, int mouseX, int mouseY, float ticks);

    public abstract void toNetwork(FriendlyByteBuf buffer);

    public void drawCenteredStringNoShadow(PoseStack poseStack, FormattedCharSequence s, int x, int y, int color) {
        this.font.draw(poseStack, s, x - this.font.width(s) / 2.0F, y, color);
    }

    public void drawCenteredStringNoShadow(PoseStack poseStack, String s, int x, int y, int color) {
        this.font.draw(poseStack, s, x - this.font.width(s) / 2.0F, y, color);
    }

    public BookEntry getParentEntry() {
        return this.parentEntry;
    }

    public void setParentEntry(BookEntry parentEntry) {
        this.parentEntry = parentEntry;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * @param pMouseX localized to page x (mouseX - bookLeft - page.left)
     * @param pMouseY localized to page y (mouseY - bookTop - page.top)
     */
    @Nullable
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        return null;
    }

    @Nullable
    protected Style getClickedComponentStyleAtForTitle(BookTextHolder title, int x, int y, double pMouseX, double pMouseY) {
        //they say good code comments itself. Well, this is not good code.
        if (title instanceof RenderedBookTextHolder renderedTitle) {
            //markdown title
            var formattedCharSequence = FormattedCharSequence.fromList(
                    renderedTitle.getRenderedText().stream().map(BaseComponent::getVisualOrderText).toList());
            if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                //check if we are vertically over the title line

                x = x - this.font.width(formattedCharSequence) / 2;
                if (pMouseX < x)
                    return null;
                //if we are horizontally left of the title, exit

                //horizontally over and right of the title is handled by font splitter
                return this.font.getSplitter().componentStyleAtWidth(formattedCharSequence, (int) pMouseX - x);
            }
        } else {
            if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                //check if we are vertically over the title line

                var formattedCharSequence = title.getComponent().getVisualOrderText();
                x = x - this.font.width(formattedCharSequence) / 2;
                if (pMouseX < x)
                    return null;
                //if we are horizontally left of the title, exit

                //horizontally over and right of the title is handled by font splitter
                return this.font.getSplitter().componentStyleAtWidth(formattedCharSequence, (int) pMouseX - x);
            }
        }
        return null;
    }

    @Nullable
    protected Style getClickedComponentStyleAtForTextHolder(BookTextHolder text, int x, int y, int width, double pMouseX, double pMouseY) {
        if (text.hasComponent()) {
            //we don't do math to geht the current line, we just split and iterate.
            //why? Because performance should not matter (significantly enough to bother)
            for (FormattedCharSequence formattedcharsequence : this.font.split(text.getComponent(), width)) {
                if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                    //check if we are vertically over the title line
                    //horizontally over and right of the title is handled by font splitter
                    return this.font.getSplitter().componentStyleAtWidth(formattedcharsequence, (int) pMouseX - x);
                }
                y += this.font.lineHeight;
            }
        } else if (text instanceof RenderedBookTextHolder renderedText) {
            var components = renderedText.getRenderedText();
            for (var component : components) {
                var wrapped = MarkdownComponentRenderUtils.wrapComponents(component, width, width - 10, this.font);
                for (FormattedCharSequence formattedcharsequence : wrapped) {
                    if (pMouseY > y && pMouseY < y + this.font.lineHeight) {
                        //check if we are vertically over the title line
                        //horizontally over and right of the title is handled by font splitter
                        return this.font.getSplitter().componentStyleAtWidth(formattedcharsequence, (int) pMouseX - x);
                    }
                    y += this.font.lineHeight;
                }
            }
        }

        return null;
    }
}
