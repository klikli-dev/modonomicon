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

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.data.book.BookPage;
import com.klikli_dev.modonomicon.api.data.book.BookPageLoader;
import com.klikli_dev.modonomicon.data.book.pages.BookTextPage;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BookPageLoaderRegistry {

    private static Map<ResourceLocation, BookPageLoader<? extends BookPage>> pageLoaders = new HashMap<>();

    public static void registerDefaultPageLoaders(){
        ModonomiconAPI.get().registerPageLoader(Page.TEXT, BookTextPage::fromJson);
    }

    public static void registerPageLoader(ResourceLocation id, BookPageLoader<? extends BookPage> loader) {
        pageLoaders.put(id, loader);
    }

    public static BookPageLoader<? extends BookPage> getPageLoader(ResourceLocation id) {
        return pageLoaders.get(id);
    }

}
