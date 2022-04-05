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

package com.klikli_dev.modonomicon.client.gui;

import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.klikli_dev.modonomicon.book.BookDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ScreenHelper {
    public static void openBook(ItemStack stack) {
        Minecraft.getInstance().setScreen(new BookOverviewScreen(
                BookDataManager.get().getBook(new ResourceLocation("modonomicon", "test")),
                stack));
    }

    public static void openCategory(ItemStack stack, ResourceLocation bookId, ResourceLocation categoryId){
        var book = BookDataManager.get().getBook(bookId);
        var category = book.getCategory(categoryId);
        var overviewScreen = new BookOverviewScreen(book, stack);
        overviewScreen.changeCategory(category);
        Minecraft.getInstance().setScreen(overviewScreen);
    }

    public static void openEntry(ItemStack stack, ResourceLocation bookId, ResourceLocation entryId, int page){
        //TODO: should compare to our current book gui stack so we don't create new screens if not necessary
        var book = BookDataManager.get().getBook(bookId);
        var entry = book.getEntry(entryId);
        var category = entry.getCategory();
        var overviewScreen = new BookOverviewScreen(book, stack);
        Minecraft.getInstance().setScreen(overviewScreen);
        overviewScreen.changeCategory(category);
        var categoryScreen = overviewScreen.getCurrentCategoryScreen();
        //TODO: scroll to the entry that will be opened
        var contentScreen = categoryScreen.openEntry(entry);
        contentScreen.changePage(page, false); //TODO: play sound here?
    }
}
