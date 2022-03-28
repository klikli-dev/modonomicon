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
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ext.ComponentStrikethroughExtension;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ext.ComponentUnderlineExtension;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.commonmark.parser.Parser;

import java.util.List;

public class BookTextPage extends BookPage {
    protected BookTextHolder title;
    protected BookTextHolder text;

    public BookTextPage(BookTextHolder title, BookTextHolder text) {
        this.title = title;
        this.text = text;
    }

    public static BookTextPage fromJson(JsonObject json) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        return new BookTextPage(title, text);
    }

    public BookTextHolder getTitle() {
        return this.title;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    @Override
    public ResourceLocation getType() {
        return Page.TEXT;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        int i = (this.parentScreen.width - BookContentScreen.BOOK_BACKGROUND_WIDTH) / 2 + 15;
        int j = (this.parentScreen.height - BookContentScreen.BOOK_BACKGROUND_HEIGHT) / 2 + 15;

        //TODO: Render title


        //TODO: allow modders to add extensions
        var extensions = List.of(ComponentStrikethroughExtension.create(), ComponentUnderlineExtension.create());
        var parser = Parser.builder()
                .extensions(extensions)
                .build();

        //TODO: set width from init?
        int width = BookContentScreen.BOOK_BACKGROUND_WIDTH / 2 - 18;

        this.renderBookTextHolder(this.getText(), poseStack, i, j, width);
    }
}
