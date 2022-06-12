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

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.book.page.BookMultiblockPage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PageRendererRegistry {
    private static final Map<ResourceLocation, PageRendererFactory> pageRenderers = new HashMap<>();

    /**
     * Call from client setup
     */
    public static void registerPageRenderers() {
        registerDefaultPageRenderers();
    }

    private static void registerDefaultPageRenderers() {
        registerPageRenderer(Page.TEXT, p -> new BookTextPageRenderer((BookTextPage)p));
        registerPageRenderer(Page.MULTIBLOCK, p -> new BookMultiblockPageRenderer((BookMultiblockPage)p));
    }

    /**
     * Call from client setup
     */
    public static void registerPageRenderer(ResourceLocation id, PageRendererFactory factory) {
        pageRenderers.put(id, factory);
    }

    public static PageRendererFactory getPageRenderer(ResourceLocation id) {
        var renderer = pageRenderers.get(id);
        if (renderer == null) {
            throw new IllegalArgumentException("No page renderer registered for page type " + id);
        }
        return renderer;
    }

}
