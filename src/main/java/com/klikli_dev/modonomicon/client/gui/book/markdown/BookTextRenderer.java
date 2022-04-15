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

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.klikli_dev.modonomicon.client.gui.book.markdown.ext.ComponentStrikethroughExtension;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ext.ComponentUnderlineExtension;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import org.commonmark.Extension;
import org.commonmark.parser.Parser;

import java.util.List;

public class BookTextRenderer {

    private final Parser markdownParser;

    private final List<Extension> extensions;

    public BookTextRenderer() {
        //TODO: make parser configurable for modders
        this.extensions = List.of(ComponentStrikethroughExtension.create(), ComponentUnderlineExtension.create());
        this.markdownParser = Parser.builder()
                .extensions(this.extensions)
                .build();
    }

    public List<TranslatableComponent> render(String markdown) {
        return this.render(markdown, Style.EMPTY);
    }

    public List<TranslatableComponent> render(String markdown, Style defaultStyle) {
        //TODO: make renderer configurable for modders

        //renderer needs to be instantiated every time, because it caches the results
        var renderer = new ComponentRenderer.Builder()
                .renderSoftLineBreaks(false)
                .replaceSoftLineBreaksWithSpace(true)
                .linkColor(TextColor.fromRgb(0x5555FF))
                .linkRenderers(List.of(new ColorLinkRenderer(), new BookLinkRenderer()))
                .style(defaultStyle)
                .extensions(this.extensions)
                .build();

        var document = this.markdownParser.parse(markdown);

        return renderer.render(document);
    }
}
