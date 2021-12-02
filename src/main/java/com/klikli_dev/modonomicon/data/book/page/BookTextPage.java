/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
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

package com.klikli_dev.modonomicon.data.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.data.book.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class BookTextPage extends AbstractBookPage {
    protected Component title;
    protected Component text;

    public BookTextPage(Component title, Component text) {
        this.title = title;
        this.text = text;
    }

    public static BookTextPage fromJson(JsonObject json) {
        //if it is a simple string, load as translatable component, otherwise deserialize full component
        Component title = BookGsonHelper.getAsComponent(json, "title", null);
        Component text = BookGsonHelper.getAsComponent(json, "text", null);
        return new BookTextPage(title, text);
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getText() {
        return this.text;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        //TODO: render title
        //TODO: render text
        //TODO: use page left/top here
        int j1 = this.font.width(this.text);
        int i = (this.parentScreen.width - BookContentScreen.BOOK_BACKGROUND_WIDTH) / 2;
        //TODO: fix wrapped drawing not working
        //TODO: fix newlines not working -> should be handled correctly in wrapped drawing
        //this.font.drawWordWrap(this.text, 40 , 90, 2000, 0xFFFFF);

        int height = this.top;
        for(FormattedCharSequence formattedcharsequence : this.font.split(this.text, 250)) {
            this.font.draw(poseStack, formattedcharsequence, this.left, height, 0);
            height += 9;
        }
        //this.font.drawWordWrap(this.text, (i - j1 + BookContentScreen.BOOK_BACKGROUND_WIDTH - 44), 18, BookContentScreen.BOOK_BACKGROUND_WIDTH / 2, 0);
        //this.font.draw(poseStack, this.text, (float)(i - j1 + BookContentScreen.BOOK_BACKGROUND_WIDTH - 44), 18.0F, 0);
        //this.font.draw(poseStack, this.text, this.left, this.top, 0);
    }
}
