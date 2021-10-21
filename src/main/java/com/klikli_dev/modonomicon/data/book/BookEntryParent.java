/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.data.book;

public class BookEntryParent {
    protected BookEntry entry;
    protected boolean drawArrow = true;
    protected boolean lineEnabled = true;
    protected boolean lineReversed = false;
    //TODO: allow loading the booleans from json

    public BookEntryParent(BookEntry entry) {
        this.entry = entry;
    }

    public BookEntry getEntry() {
        return this.entry;
    }

    public boolean drawArrow() {
        return this.drawArrow;
    }

    public boolean isLineEnabled() {
        return this.lineEnabled;
    }

    public boolean isLineReversed() {
        return this.lineReversed;
    }
}
