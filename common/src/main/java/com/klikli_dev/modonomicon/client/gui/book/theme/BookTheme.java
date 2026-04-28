/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookTheme {

    BookContentTheme content();

    BookLayoutTheme layout();

    BookNodeTheme node();

    BookFrameTheme frame();

    BookPaletteTheme palette();
}
