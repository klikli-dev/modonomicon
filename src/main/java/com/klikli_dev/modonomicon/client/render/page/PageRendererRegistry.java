/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
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
