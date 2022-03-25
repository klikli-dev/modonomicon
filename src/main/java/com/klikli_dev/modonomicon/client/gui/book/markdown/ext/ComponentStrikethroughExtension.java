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

package com.klikli_dev.modonomicon.client.gui.book.markdown.ext;

import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentNodeRendererContext;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentNodeRendererFactory;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentRenderer.Builder;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.internal.StrikethroughDelimiterProcessor;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;

public class ComponentStrikethroughExtension implements Parser.ParserExtension, ComponentRenderer.ComponentRendererExtension {

    public static Extension create() {
        return new ComponentStrikethroughExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customDelimiterProcessor(new StrikethroughDelimiterProcessor());
    }

    @Override
    public void extend(Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(new ComponentNodeRendererFactory() {
            @Override
            public NodeRenderer create(ComponentNodeRendererContext context) {
                return new StrikethroughComponentNodeRenderer(context);
            }
        });
    }
}
