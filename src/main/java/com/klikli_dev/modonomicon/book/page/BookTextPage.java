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

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;

public class BookTextPage extends BookPage {
    protected BookTextHolder title;
    protected boolean useMarkdownInTitle;
    protected BookTextHolder text;


    public BookTextPage(BookTextHolder title, BookTextHolder text, boolean useMarkdownInTitle) {
        this.title = title;
        this.text = text;
        this.useMarkdownInTitle = useMarkdownInTitle;
    }

    public static BookTextPage fromJson(JsonObject json) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
        var useMarkdownInTitle = GsonHelper.getAsBoolean(json, "useMarkdownInTitle", false);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        return new BookTextPage(title, text, useMarkdownInTitle);
    }

    public static BookTextPage fromNetwork(FriendlyByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var useMarkdownInTitle = buffer.readBoolean();
        var text = BookTextHolder.fromNetwork(buffer);
        return new BookTextPage(title, text, useMarkdownInTitle);
    }

    public BookTextHolder getTitle() {
        return this.title;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    public boolean hasTitle() {
        return !this.title.getString().isEmpty();
    }

    @Override
    public ResourceLocation getType() {
        return Page.TEXT;
    }

    @Override
    public void onBeginDisplayPage(BookContentScreen parentScreen, BookTextRenderer textRenderer, int left, int top) {
        super.onBeginDisplayPage(parentScreen, textRenderer, left, top);

        if (!this.title.hasComponent()) {
            if (this.useMarkdownInTitle) {
                this.title = new RenderedBookTextHolder(this.title, textRenderer.render(this.title.getString()));
            } else {
                this.title = new BookTextHolder(new TranslatableComponent(this.title.getKey())
                        .withStyle(Style.EMPTY
                                .withBold(true)
                                .withColor(this.parentEntry.getCategory().getBook().getDefaultTitleColor())));
            }
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {

        if (this.hasTitle()) {
            if (this.useMarkdownInTitle && this.title instanceof RenderedBookTextHolder renderedTitle) {
                //if user decided to use markdown title, we need to use the  rendered version
                var formattedCharSequence = FormattedCharSequence.fromList(
                        renderedTitle.getRenderedText().stream().map(BaseComponent::getVisualOrderText).toList());
                this.drawCenteredStringNoShadow(poseStack, formattedCharSequence, BookContentScreen.PAGE_WIDTH / 2, 0, 0);
            } else {
                //otherwise we use the component - that is either provided by the user, or created from the default title style.
                this.drawCenteredStringNoShadow(poseStack, this.title.getComponent().getVisualOrderText(), BookContentScreen.PAGE_WIDTH / 2, 0, 0);
            }

            //TODO: Draw separator
        }

        //TODO: getTextHeight() as y instead of constant
        //  depending on whether or not we have a title we have different y values
        this.renderBookTextHolder(this.getText(), poseStack, 0, 22, BookContentScreen.PAGE_WIDTH);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.title.toNetwork(buffer);
        buffer.writeBoolean(this.useMarkdownInTitle);
        this.text.toNetwork(buffer);
    }
}
