/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.klikli_dev.modonomicon.book.Book;

final class BookBackedPresentationTheme implements BookPresentationTheme {

    private final BookLayoutTheme layout;
    private final BookPaletteTheme palette;

    BookBackedPresentationTheme(Book book) {
        this.layout = new BookLayoutTheme() {
            @Override
            public int bookTextOffsetX() {
                return book.getBookTextOffsetX();
            }

            @Override
            public int bookTextOffsetY() {
                return book.getBookTextOffsetY();
            }

            @Override
            public int bookTextOffsetWidth() {
                return book.getBookTextOffsetWidth();
            }

            @Override
            public int bookTextOffsetHeight() {
                return book.getBookTextOffsetHeight();
            }

            @Override
            public int categoryButtonXOffset() {
                return book.getCategoryButtonXOffset();
            }

            @Override
            public int categoryButtonYOffset() {
                return book.getCategoryButtonYOffset();
            }

            @Override
            public int searchButtonXOffset() {
                return book.getSearchButtonXOffset();
            }

            @Override
            public int searchButtonYOffset() {
                return book.getSearchButtonYOffset();
            }

            @Override
            public int readAllButtonYOffset() {
                return book.getReadAllButtonYOffset();
            }

            @Override
            public float categoryButtonIconScale() {
                return book.getCategoryButtonIconScale();
            }
        };
        this.palette = new BookPaletteTheme() {
            @Override
            public int defaultTitleColor() {
                return book.getDefaultTitleColor();
            }

            @Override
            public int defaultTextColor() {
                return book.getDefaultTextColor();
            }
        };
    }

    @Override
    public BookLayoutTheme layout() {
        return this.layout;
    }

    @Override
    public BookPaletteTheme palette() {
        return this.palette;
    }
}
