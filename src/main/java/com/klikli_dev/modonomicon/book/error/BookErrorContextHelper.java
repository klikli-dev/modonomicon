/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
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

package com.klikli_dev.modonomicon.book.error;

import net.minecraft.resources.ResourceLocation;

public class BookErrorContextHelper {
    public ResourceLocation categoryId;
    public ResourceLocation entryId;
    public int pageNumber = 1;

    public void reset() {
        this.categoryId = null;
        this.entryId = null;
        this.pageNumber = -1;
    }

    @Override
    public String toString() {
        var categoryId = this.categoryId == null ? "null" : this.categoryId.toString();
        var entryId = this.entryId == null ? "null" : this.entryId.toString();
        var pageNumber = this.pageNumber == -1 ? "null" : this.pageNumber;
        return "Category: " + categoryId + ", \nEntry: " + entryId + ", \nPage: " + pageNumber;
    }
}
