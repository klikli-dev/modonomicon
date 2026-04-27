/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;

public final class DefaultBookPresentationTheme implements BookPresentationTheme {

    public static final DefaultBookPresentationTheme INSTANCE = new DefaultBookPresentationTheme();

    private static final BookLayoutTheme LAYOUT = new BookLayoutTheme() {
        @Override
        public int bookTextOffsetX() {
            return 0;
        }

        @Override
        public int bookTextOffsetY() {
            return 0;
        }

        @Override
        public int bookTextOffsetWidth() {
            return 0;
        }

        @Override
        public int bookTextOffsetHeight() {
            return 0;
        }

        @Override
        public int categoryButtonXOffset() {
            return 0;
        }

        @Override
        public int categoryButtonYOffset() {
            return 0;
        }

        @Override
        public int searchButtonXOffset() {
            return 0;
        }

        @Override
        public int searchButtonYOffset() {
            return 0;
        }

        @Override
        public int readAllButtonYOffset() {
            return 0;
        }

        @Override
        public float categoryButtonIconScale() {
            return 1.0f;
        }
    };

    private static final BookPaletteTheme PALETTE = new BookPaletteTheme() {
        @Override
        public int defaultTitleColor() {
            return ModonomiconConstants.Data.Book.DEFAULT_TITLE_COLOR;
        }

        @Override
        public int defaultTextColor() {
            return ModonomiconConstants.Data.Book.DEFAULT_TEXT_COLOR;
        }
    };

    private DefaultBookPresentationTheme() {
    }

    @Override
    public BookLayoutTheme layout() {
        return LAYOUT;
    }

    @Override
    public BookPaletteTheme palette() {
        return PALETTE;
    }
}
