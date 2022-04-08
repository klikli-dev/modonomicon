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

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.client.gui.book.BookCategoryScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.klikli_dev.modonomicon.book.BookDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.Nullable;

public class BookGuiManager {

    private static final BookGuiManager instance = new BookGuiManager();

    private Book currentBook;
    private BookCategory currentCategory;
    private BookEntry currentEntry;

    private BookOverviewScreen currentOverviewScreen;
    private BookCategoryScreen currentCategoryScreen;
    private BookContentScreen currentContentScreen;

    public static BookGuiManager get() {
        return instance;
    }

    public void openBook(ItemStack stack) {
        //TODO: we probably want to remember the last page and stuff here
        var bookId = new ResourceLocation("modonomicon", "test"); //TODO: Get from stack
        var book = BookDataManager.get().getBook(bookId);

        if(this.currentBook == book && this.currentOverviewScreen != null){
            Minecraft.getInstance().setScreen(this.currentOverviewScreen);
        } else {
            this.currentBook = book;
            this.currentOverviewScreen = new BookOverviewScreen(this.currentBook);
            Minecraft.getInstance().setScreen(this.currentOverviewScreen);
        }
    }

    public void openEntry(ResourceLocation bookId, ResourceLocation entryId, int page){
        var book = BookDataManager.get().getBook(bookId);
        var entry = book.getEntry(entryId);
        this.openEntry(bookId, entry.getCategoryId(), entryId, page);
    }

    /**
     * Opens the book at the given location. Will open as far as possible (meaning, if category and entry are null, it will not open those obviously).
     */
    public void openEntry(ResourceLocation bookId, @Nullable ResourceLocation categoryId, @Nullable ResourceLocation entryId, int page){
        if(bookId == null){
            throw new IllegalArgumentException("bookId cannot be null");
        }

        var book = BookDataManager.get().getBook(bookId);
        if(this.currentBook != book){
            this.currentBook = book;
        }

        if(this.currentOverviewScreen == null || this.currentOverviewScreen.getBook() != book){
            this.currentOverviewScreen = new BookOverviewScreen(book);
        }

        //TODO: layer cleanup
        ForgeHooksClient.clearGuiLayers(Minecraft.getInstance());
        Minecraft.getInstance().setScreen(this.currentOverviewScreen);

        if(categoryId == null){
            //if no category is provided, just open the book and exit.
            return;
        }

        var category = book.getCategory(categoryId);
        if(this.currentCategory != category){
            this.currentCategory = category;
        }

        if(this.currentCategoryScreen == null || this.currentCategoryScreen.getCategory() != category){
            this.currentOverviewScreen.changeCategory(category);
            this.currentCategoryScreen = this.currentOverviewScreen.getCurrentCategoryScreen();
        }

        if(entryId == null){
            //if no entry is provided, just open the book and category and exit.
            return;
        }

        var entry = book.getEntry(entryId);
        if(this.currentEntry != entry){
            this.currentEntry = entry;
        }

        if(this.currentContentScreen == null || this.currentContentScreen.getEntry() != entry){
            this.currentContentScreen = this.currentCategoryScreen.openEntry(entry);
        }

        //we don't need to manually check for the current page because the content screen will do that for us
        this.currentContentScreen.changePage(page, false);
        //TODO: play sound here? could just make this a client config
    }
}
