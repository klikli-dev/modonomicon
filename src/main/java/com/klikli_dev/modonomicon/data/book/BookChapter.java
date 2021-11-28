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

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.data.book.BookPage;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BookChapter {
    protected ResourceLocation id;
    protected ResourceLocation entryId;
    protected BookCategory category;
    protected Map<ResourceLocation, BookPage> bookPages;

    //TODO: Pages?

    public BookChapter(ResourceLocation id, ResourceLocation entryId, BookCategory category) {
        this.id = id;
        this.entryId = entryId;
        this.category = category;
    }

    public static BookChapter fromJson(ResourceLocation id, JsonObject json, Map<ResourceLocation, BookCategory> categories) {
        return null;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public ResourceLocation getEntryId() {
        return entryId;
    }
}
