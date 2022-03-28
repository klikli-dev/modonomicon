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

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.klikli_dev.modonomicon.client.gui.book.markdown.ext.ComponentStrikethroughExtension;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ext.ComponentUnderlineExtension;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import org.commonmark.parser.Parser;

import java.util.Collection;
import java.util.List;

public class BookTextRenderer {

    private final Parser markdownParser;

    private final ComponentRenderer componentRenderer;

    public BookTextRenderer() {
        //TODO: make parser and renderer configurable for modders
        var extensions = List.of(ComponentStrikethroughExtension.create(), ComponentUnderlineExtension.create());
        this.markdownParser = Parser.builder()
                .extensions(extensions)
                .build();
        this.componentRenderer = new ComponentRenderer.Builder()
                .renderSoftLineBreaks(false)
                .replaceSoftLineBreaksWithSpace(true)
                .linkColor(TextColor.fromRgb(0x5555FF))
                .linkRenderers(List.of(new ColorLinkRenderer(), new BookLinkRenderer()))
                .extensions(extensions)
                .build();
    }

    public Collection<TranslatableComponent> render(String markdown) {
        var document = this.markdownParser.parse(markdown);
        return this.componentRenderer.render(document);
    }
}
