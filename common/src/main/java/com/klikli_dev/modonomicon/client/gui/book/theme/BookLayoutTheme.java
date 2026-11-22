/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

public interface BookLayoutTheme {

    int bookTextOffsetX();

    int bookTextOffsetY();

    int bookTextOffsetWidth();

    int bookTextOffsetHeight();

    int indexTextOffsetX();

    int indexTextOffsetY();

    int indexTextOffsetWidth();

    int indexTextOffsetHeight();

    int categoryButtonXOffset();

    int categoryButtonYOffset();

    int searchButtonXOffset();

    int searchButtonYOffset();

    int readAllButtonYOffset();

    float categoryButtonIconScale();

    /**
     * Number of pixels the category and side buttons extend under the book frame.
     * The buttons are drawn behind the frame, so the frame covers their inner end.
     */
    int sideButtonFrameOverlap();

    /**
     * Horizontal offset of the category button icon in screen pixels, applied before the icon scale.
     * Moves only the icon, not the button background.
     */
    int categoryButtonIconXOffset();

    /**
     * Vertical offset of the category button icon in screen pixels, applied before the icon scale.
     * Moves only the icon, not the button background.
     */
    int categoryButtonIconYOffset();
}
