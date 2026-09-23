/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookPageWithSplit;
import com.klikli_dev.modonomicon.client.gui.TextWrapper;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.markdown.MarkdownComponentRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Client-only locale-aware measurement and chunking for opt-in page splitting.
 * <p>
 * Authored {@code BookContentEntry.pages} stay canonical. This helper wraps text at scale
 * {@code 1.0} with the active client font, locale and markdown pipeline, then chunks the
 * already-wrapped lines into display fragments. Fragment zero uses the original page's
 * text-area height (title and separator reserve space); later fragments use the full
 * title-less text area.
 */
public final class PageSplitter {

    /**
     * Text Y used for continuation fragments, matching a title-less text page.
     */
    public static final int CONTINUATION_TEXT_Y = -4;

    private static final int CACHE_CAPACITY = 256;

    private static final Map<SplitCacheKey, List<List<FormattedCharSequence>>> CACHE = new LinkedHashMap<>(64, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<SplitCacheKey, List<List<FormattedCharSequence>>> eldest) {
            return this.size() > CACHE_CAPACITY;
        }
    };

    private PageSplitter() {
    }

    public static void clearCache() {
        CACHE.clear();
    }

    /**
     * @return true if the page opts into splitting, resolving per-page overrides against book defaults.
     */
    public static boolean isSplitEnabled(BookPage page) {
        if (!(page instanceof BookPageWithSplit splitPage)) {
            return false;
        }
        return splitPage.isSplitEnabled(page.getBook());
    }

    /**
     * @return true if scale-to-fit applies. Splitting takes precedence when enabled.
     */
    public static boolean isAutoScaleEnabled(BookPage page) {
        if (!(page instanceof BookPageWithSplit splitPage)) {
            return true;
        }
        return splitPage.isAutoScaleEnabled(page.getBook());
    }

    /**
     * @return the body text holder measured for splitting, or null when the page has no splittable text.
     */
    public static BookTextHolder getSplitText(BookPage page) {
        if (page instanceof BookPageWithSplit splitPage) {
            return splitPage.getText();
        }
        return null;
    }

    /**
     * Applies the theme-adjusted text bounds used by the actual renderer.
     */
    public static BookPageRenderer.TextHolderBounds adjustBounds(Book book, int x, int y, int width, int height) {
        var layout = book.theme().layout();
        return BookPageRenderer.applyTextOffset(layout.bookTextOffsetX(), layout.bookTextOffsetY(), layout.bookTextOffsetWidth(), layout.bookTextOffsetHeight(), x, y, width, height);
    }

    /**
     * Theme-adjusted bounds for the first fragment of a page whose body text starts at {@code textY}.
     */
    public static BookPageRenderer.TextHolderBounds firstFragmentBounds(Book book, int textY) {
        return adjustBounds(book, 0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);
    }

    /**
     * Theme-adjusted bounds for title-less continuation fragments.
     */
    public static BookPageRenderer.TextHolderBounds continuationBounds(Book book) {
        return adjustBounds(book, 0, CONTINUATION_TEXT_Y, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - CONTINUATION_TEXT_Y);
    }

    /**
     * Lines that fit into the given height at scale 1.0, clamped to at least one line.
     */
    public static int capacityForHeight(int height, Font font) {
        var lineHeight = Math.max(1, font.lineHeight);
        if (height <= 0) {
            return 1;
        }
        return Math.max(1, height / lineHeight);
    }

    /**
     * Pure chunking over already-wrapped lines. Never splits inside a line or alters styles,
     * and never creates an empty continuation fragment.
     *
     * @param lines                wrapped lines in render order.
     * @param firstCapacity        lines fitting on the first fragment.
     * @param continuationCapacity lines fitting on each continuation fragment.
     * @return fragments of lines, always containing at least one fragment.
     */
    public static List<List<FormattedCharSequence>> chunkLines(List<FormattedCharSequence> lines, int firstCapacity, int continuationCapacity) {
        var first = Math.max(1, firstCapacity);
        var continuation = Math.max(1, continuationCapacity);

        List<List<FormattedCharSequence>> fragments = new ArrayList<>();
        if (lines.size() <= first) {
            fragments.add(List.copyOf(lines));
            return fragments;
        }

        fragments.add(List.copyOf(lines.subList(0, first)));

        var from = first;
        while (from < lines.size()) {
            var to = Math.min(from + continuation, lines.size());
            fragments.add(List.copyOf(lines.subList(from, to)));
            from = to;
        }
        return fragments;
    }

    /**
     * Wraps the holder at scale 1.0 with the same pipeline as rendering:
     * markdown components via {@code MarkdownComponentRenderUtils}, component holders via {@code TextWrapper}.
     * Each returned sequence is one rendered line.
     */
    public static List<FormattedCharSequence> wrapForSplit(BookTextHolder text, Font font, int width) {
        var clampedWidth = Math.max(1, width);
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        if (text.hasComponent()) {
            return TextWrapper.split(text.getComponent(), clampedWidth, font);
        }
        if (text instanceof RenderedBookTextHolder renderedText) {
            List<FormattedCharSequence> lines = new ArrayList<>();
            for (var component : renderedText.getRenderedText()) {
                lines.addAll(MarkdownComponentRenderUtils.wrapComponents(component, clampedWidth, clampedWidth - 10, font));
            }
            return lines;
        }
        return List.of();
    }

    /**
     * Wraps and chunks the holder, caching by holder, bounds, locale and font identity.
     *
     * @return fragments of wrapped lines, always containing at least one fragment.
     */
    public static List<List<FormattedCharSequence>> split(BookTextHolder text, Font font, int width, int firstHeight, int continuationHeight) {
        var clampedWidth = Math.max(1, width);
        var key = new SplitCacheKey(text, clampedWidth, firstHeight, continuationHeight, currentLocaleCode(), font);
        var cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        var lines = wrapForSplit(text, font, clampedWidth);
        var fragments = chunkLines(lines, capacityForHeight(firstHeight, font), capacityForHeight(continuationHeight, font));
        CACHE.put(key, fragments);
        return fragments;
    }

    /**
     * Splits a page's body text using its first-fragment text Y and the title-less continuation area.
     *
     * @return fragments of wrapped lines, always containing at least one fragment.
     */
    public static List<List<FormattedCharSequence>> splitForPage(BookPage page, Book book, Font font, int textY) {
        var text = getSplitText(page);
        var first = firstFragmentBounds(book, textY);
        var continuation = continuationBounds(book);
        return split(text, font, first.width, first.height, continuation.height);
    }

    private static String currentLocaleCode() {
        return Minecraft.getInstance().getLanguageManager().getSelected();
    }

    private record SplitCacheKey(BookTextHolder holder, int width, int firstHeight, int continuationHeight, String locale, Font font) {
    }
}
