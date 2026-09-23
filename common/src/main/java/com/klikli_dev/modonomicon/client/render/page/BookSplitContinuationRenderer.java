/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Renders a text-only continuation fragment of a split page.
 * <p>
 * Continuation fragments are display-only: they have no title, separator, or non-text
 * content, and no public IDs or page numbers. They render pre-wrapped lines at scale 1.0
 * with the title-less continuation bounds.
 */
public class BookSplitContinuationRenderer extends BookPageRenderer<BookPage> {

    private final List<FormattedCharSequence> fragmentLines;

    public BookSplitContinuationRenderer(BookPage authoredPage, List<FormattedCharSequence> fragmentLines) {
        super(authoredPage);
        this.fragmentLines = List.copyOf(fragmentLines);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float ticks) {
        var bounds = PageSplitter.continuationBounds(this.parentScreen.getBook());
        renderFragmentLines(guiGraphics, this.font, this.fragmentLines, bounds.x, bounds.y,
                this.parentScreen.getBook().theme().palette().defaultTextColor());

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null) {
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
        }
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0 && this.parentScreen != null) {
            var bounds = PageSplitter.continuationBounds(this.parentScreen.getBook());
            return this.getClickedStyleAtFragmentLines(this.fragmentLines, bounds.x, bounds.y, pMouseX, pMouseY);
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    /**
     * @return the authored page this continuation fragment belongs to.
     */
    public BookPage getAuthoredPage() {
        return this.page;
    }

    /**
     * @return the pre-wrapped lines rendered by this fragment.
     */
    public List<FormattedCharSequence> getFragmentLines() {
        return this.fragmentLines;
    }

    /**
     * Left offset used for continuation fragments, matching single-page layout.
     */
    public static int continuationLeft() {
        return BookEntryScreen.SINGLE_PAGE_X;
    }

    /**
     * Top offset used for continuation fragments, matching page layout.
     */
    public static int continuationTop() {
        return BookEntryScreen.TOP_PADDING;
    }
}
