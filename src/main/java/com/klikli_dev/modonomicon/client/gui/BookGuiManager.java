/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.*;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class BookGuiManager {

    private static final BookGuiManager instance = new BookGuiManager();

    private final Stack<BookHistoryEntry> history = new Stack<>();

    private Book currentBook;
    private BookCategory currentCategory;
    private BookEntry currentEntry;

    private BookOverviewScreen currentOverviewScreen;
    private BookCategoryScreen currentCategoryScreen;
    private BookContentScreen currentContentScreen;

    private BookGuiManager() {

    }

    public static BookGuiManager get() {
        return instance;
    }

    public boolean showErrorScreen(ResourceLocation bookId) {
        if (BookErrorManager.get().hasErrors(bookId)) {
            var book = BookDataManager.get().getBook(bookId);
            ForgeHooksClient.clearGuiLayers(Minecraft.getInstance());
            Minecraft.getInstance().setScreen(new BookErrorScreen(book));
            return true;
        }
        return false;
    }

    public void openBook(ResourceLocation bookId) {
        if (this.showErrorScreen(bookId)) {
            return;
        }

        var book = BookDataManager.get().getBook(bookId);

        if (this.currentBook == book && this.currentOverviewScreen != null) {
            Minecraft.getInstance().setScreen(this.currentOverviewScreen);
            this.currentOverviewScreen.onDisplay();
        } else {
            this.currentBook = book;
            this.currentOverviewScreen = new BookOverviewScreen(this.currentBook);
            Minecraft.getInstance().setScreen(this.currentOverviewScreen);
            this.currentOverviewScreen.onDisplay();
        }
    }

    public void openEntry(ResourceLocation bookId, ResourceLocation entryId, int page) {
        var book = BookDataManager.get().getBook(bookId);
        var entry = book.getEntry(entryId);
        this.openEntry(bookId, entry.getCategoryId(), entryId, page);
    }

    public void pushHistory(ResourceLocation bookId, @Nullable ResourceLocation categoryId, @Nullable ResourceLocation entryId, int page) {
        this.history.push(new BookHistoryEntry(bookId, categoryId, entryId, page));
    }

    public void pushHistory(BookHistoryEntry entry) {
        this.history.push(entry);
    }

    public BookHistoryEntry popHistory() {
        return this.history.pop();
    }

    public BookHistoryEntry peekHistory() {
        return this.history.peek();
    }

    public boolean hasHistory() {
        return !this.history.isEmpty();
    }

    public void resetHistory() {
        this.history.clear();
    }

    /**
     * Opens the book at the given location. Will open as far as possible (meaning, if category and entry are null, it
     * will not open those obviously).
     */
    public void openEntry(ResourceLocation bookId, @Nullable ResourceLocation categoryId, @Nullable ResourceLocation entryId, int page) {
        if (bookId == null) {
            throw new IllegalArgumentException("bookId cannot be null");
        }

        if (this.showErrorScreen(bookId)) {
            return;
        }

        this.pushHistory(bookId, categoryId, entryId, page);

        var book = BookDataManager.get().getBook(bookId);
        if (this.currentBook != book) {
            this.currentBook = book;
        }

        if (this.currentOverviewScreen == null || this.currentOverviewScreen.getBook() != book) {
            this.currentOverviewScreen = new BookOverviewScreen(book);
        }

        ForgeHooksClient.clearGuiLayers(Minecraft.getInstance());
        Minecraft.getInstance().setScreen(this.currentOverviewScreen);

        if (categoryId == null) {
            //if no category is provided, just open the book and exit.
            return;
        }

        var category = book.getCategory(categoryId);
        if (this.currentCategory != category) {
            this.currentCategory = category;
        }

        if (this.currentCategoryScreen == null || this.currentCategoryScreen.getCategory() != category) {
            this.currentOverviewScreen.changeCategory(category);
            this.currentCategoryScreen = this.currentOverviewScreen.getCurrentCategoryScreen();
        }

        if (entryId == null) {
            //if no entry is provided, just open the book and category and exit.
            return;
        }

        var entry = book.getEntry(entryId);
        if (this.currentEntry != entry) {
            this.currentEntry = entry;
        }

        if (this.currentContentScreen == null || this.currentContentScreen.getEntry() != entry) {
            this.currentContentScreen = this.currentCategoryScreen.openEntry(entry);
        }

        //we don't need to manually check for the current page because the content screen will do that for us
        this.currentContentScreen.changePage(page, false);
        //TODO: play sound here? could just make this a client config
    }
}
