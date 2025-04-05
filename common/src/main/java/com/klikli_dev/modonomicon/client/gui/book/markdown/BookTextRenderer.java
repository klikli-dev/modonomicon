/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ext.ComponentStrikethroughExtension;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ext.ComponentUnderlineExtension;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.commonmark.Extension;
import org.commonmark.parser.Parser;

import java.util.List;

public class BookTextRenderer {

    private final Parser markdownParser;

    private final List<Extension> extensions;

    private final Book book;

    private final HolderLookup.Provider provider;

    public BookTextRenderer(Book book, HolderLookup.Provider provider) {
        //TODO: make parser configurable for modders
        this.extensions = List.of(ComponentStrikethroughExtension.create(), ComponentUnderlineExtension.create());
        this.markdownParser = Parser.builder()
                .extensions(this.extensions)
                .build();
        this.book = book;
        this.provider = provider;
    }

    public List<MutableComponent> render(String markdown) {
        return this.render(markdown, Style.EMPTY);
    }

    public List<MutableComponent> render(String markdown, Style defaultStyle) {
        //TODO: make renderer configurable for modders

        //renderer needs to be instantiated every time, because it caches the results
        var renderer = new ComponentRenderer.Builder()
                .renderSoftLineBreaks(false)
                .replaceSoftLineBreaksWithSpace(true)
                .linkColor(TextColor.fromRgb(0x5555FF))
                .linkRenderers(List.of(
                        new ColorLinkRenderer(),
                        new BookLinkRenderer(),
                        new ItemLinkRenderer(),
                        new PatchouliLinkRenderer(),
                        new DynamicMacroLinkRenderer(),
                        new CommandLinkRenderer()))
                .style(defaultStyle.withFont(BookDataManager.Client.get().safeFont(this.book.getFont())))
                .extensions(this.extensions)
                .build();

        var document = this.markdownParser.parse(markdown);

        return renderer.render(document, this.book, this.provider);
    }
}
