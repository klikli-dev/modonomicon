/*
 * LGPL-3.0
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
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookPageJsonLoader;
import com.klikli_dev.modonomicon.book.page.BookPageNetworkLoader;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BookPageLoaderRegistry {

    private static final Map<ResourceLocation, BookPageJsonLoader<? extends BookPage>> pageJsonLoaders = new HashMap<>();
    private static final Map<ResourceLocation, BookPageNetworkLoader<? extends BookPage>> pageNetworkLoaders = new HashMap<>();

    /**
     * Call from common setup
     */
    public static void registerDefaultPageLoaders() {
        registerPageLoader(Page.TEXT, BookTextPage::fromJson, BookTextPage::fromNetwork);
    }

    /**
     * Call from common setup
     */
    public static void registerPageLoader(ResourceLocation id, BookPageJsonLoader<? extends BookPage> jsonLoader,
                                          BookPageNetworkLoader<? extends BookPage> networkLoader) {
        pageJsonLoaders.put(id, jsonLoader);
        pageNetworkLoaders.put(id, networkLoader);
    }

    public static BookPageJsonLoader<? extends BookPage> getPageJsonLoader(ResourceLocation id) {
        var loader = pageJsonLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No json loader registered for page type " + id);
        }
        return loader;
    }

    public static BookPageNetworkLoader<? extends BookPage> getPageNetworkLoader(ResourceLocation id) {
        var loader = pageNetworkLoaders.get(id);
        if (loader == null) {
            throw new IllegalArgumentException("No network loader registered for page type " + id);
        }
        return loader;
    }
}
