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
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.commonmark.parser.Parser;

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
        //TODO: move render code into rederer classes, with registerable renderers or something
        //TODO: render title
        //TODO: render text

        //TODO: use page left/top here -> move calculation to bookcontentscreen
        //figure out a way to make these magic page boundary offsets configurable?
        int i = (this.parentScreen.width - BookContentScreen.BOOK_BACKGROUND_WIDTH) / 2 + 15;
        int j = (this.parentScreen.height - BookContentScreen.BOOK_BACKGROUND_HEIGHT) / 2 + 15;

        var parser = Parser.builder().build();
        var text =
                """
                 *Italics*  \s
                 **Bold text**  \s
                 ***Bold italics text***  \s
                 [#](32a852)colored?  \s
                 **colored bold?**  \s
                 [#]()  \s
                 translated: **<t>item.modonomicon.modonomicon</t>**
                  """;
        //TODO: Move parser to page creation
        var document = parser.parse(text);
        var visitor = new ExperimentalMDVisitor();
        document.accept(visitor);
        var comp = visitor.getComponent();
        //width should probably also be set via init
        int width = BookContentScreen.BOOK_BACKGROUND_WIDTH / 2 - 17;

        //TODO for split with indent see ComponentRenderUtils
        //  I *think* this will indent all wrapped lines. We could make this an option?

        for (FormattedCharSequence formattedcharsequence : this.font.split(comp, width)) {
            this.font.draw(poseStack, formattedcharsequence, i, j, 0);
            j += 9; //TODO: Line Height constant
        }
    }
}
