/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown.ext;

import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentRenderer.Builder;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.parser.Parser;

public class ComponentStrikethroughExtension implements Parser.ParserExtension, ComponentRenderer.ComponentRendererExtension {

    public static Extension create() {
        return new ComponentStrikethroughExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        var proxy = (StrikethroughExtension) StrikethroughExtension.create();
        proxy.extend(parserBuilder); //adds the strikethrough delimeter processor for us, because it is internal and inaccessible if modules are enforced
    }

    @Override
    public void extend(Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(StrikethroughComponentNodeRenderer::new);
    }
}
