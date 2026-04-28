/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemePalette;

public class BookThemePaletteModel {

    protected int defaultTitleColor = BookThemePalette.DEFAULT.defaultTitleColor();
    protected int defaultTextColor = BookThemePalette.DEFAULT.defaultTextColor();

    public BookThemePalette toData() {
        return new BookThemePalette(this.defaultTitleColor, this.defaultTextColor);
    }

    public BookThemePaletteModel withDefaultTitleColor(int value) { this.defaultTitleColor = value; return this; }
    public BookThemePaletteModel withDefaultTextColor(int value) { this.defaultTextColor = value; return this; }
}
