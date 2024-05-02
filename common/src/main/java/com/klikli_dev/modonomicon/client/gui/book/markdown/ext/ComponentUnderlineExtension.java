/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown.ext;

import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentRenderer.Builder;
import org.commonmark.Extension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.ext.ins.internal.InsDelimiterProcessor;
import org.commonmark.parser.Parser;

public class ComponentUnderlineExtension implements Parser.ParserExtension, ComponentRenderer.ComponentRendererExtension {

    public static Extension create() {
        return new ComponentUnderlineExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        var proxy = (InsExtension) InsExtension.create();
        proxy.extend(parserBuilder); //adds the ins delimeter processor for us, because it is internal and inaccessible if modules are enforced
    }

    @Override
    public void extend(Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(UnderlineComponentNodeRenderer::new);
    }
}
