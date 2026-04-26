/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookTheme {

    BookContentTheme content();

    BookOverviewTheme overview();

    BookFrameTheme frame();

    BookRecipeTheme recipes();

    BookLayoutTheme layout();

    BookPaletteTheme palette();
}
