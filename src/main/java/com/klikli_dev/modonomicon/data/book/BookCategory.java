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

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BookCategory {
    protected ResourceLocation id;
    protected String name;
    protected int sortNumber;
    protected ResourceLocation background;
    protected Map<ResourceLocation, BookEntry> entries;
    //TODO: additional backgrounds with custom rendertypes?

    public BookCategory(ResourceLocation id, String name, int sortNumber, Map<ResourceLocation, BookEntry> entries, ResourceLocation background) {
        this.id = id;
        this.name = name;
        this.sortNumber = sortNumber;
        this.background = background;
        this.entries = entries;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public ResourceLocation getBackground() {
        return this.background;
    }

    public Map<ResourceLocation, BookEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(BookEntry entry) {
        this.entries.putIfAbsent(entry.id, entry);
    }
}
