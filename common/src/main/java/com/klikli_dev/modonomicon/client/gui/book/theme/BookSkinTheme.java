/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookSkinTheme {

    BookContentTheme content();

    BookOverviewTheme overview();

    BookNodeTheme node();

    BookFrameTheme frame();

    BookRecipeTheme recipes();
}
