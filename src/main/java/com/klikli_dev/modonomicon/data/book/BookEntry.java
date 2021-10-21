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

import java.util.List;

public class BookEntry {
    protected ResourceLocation id;
    protected BookCategory category;
    protected List<BookEntryParent> parents;
    protected String name;
    protected BookIcon icon;
    protected int x;
    protected int y;

    //TODO: description and/or tooltip
    //TODO: Pages
    //TODO: parent entries for line rendering
    //TODO: entry type for background texture

    public BookEntry(ResourceLocation id, BookCategory category, List<BookEntryParent> parents, String name, BookIcon icon, int x, int y) {
        this.id = id;
        this.category = category;
        this.parents = parents;
        this.name = name;
        this.icon = icon;
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public List<BookEntryParent> getParents() {
        return this.parents;
    }

    public String getName() {
        return this.name;
    }

    public BookIcon getIcon() {
        return this.icon;
    }
}
