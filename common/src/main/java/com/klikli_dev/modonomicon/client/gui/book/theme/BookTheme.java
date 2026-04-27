/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookTheme {

    BookSkinTheme skin();

    BookPresentationTheme presentation();

    default BookContentTheme content() {
        return this.skin().content();
    }

    default BookOverviewTheme overview() {
        return this.skin().overview();
    }

    default BookNodeTheme node() {
        return this.skin().node();
    }

    default BookFrameTheme frame() {
        return this.skin().frame();
    }

    default BookRecipeTheme recipes() {
        return this.skin().recipes();
    }

    default BookLayoutTheme layout() {
        return this.presentation().layout();
    }

    default BookPaletteTheme palette() {
        return this.presentation().palette();
    }
}
