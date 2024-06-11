/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui;

import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.entries.*;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.client.gui.book.*;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class BookGuiManager {

    private static final BookGuiManager instance = new BookGuiManager();

    private final Stack<BookHistoryEntry> history = new Stack<>();

    public Book currentBook;
    public BookCategory currentCategory;
    public BookEntry currentEntry;

    public BookOverviewScreen currentOverviewScreen;
    public BookCategoryScreen currentCategoryScreen;
    public BookContentScreen currentContentScreen;

    public BookOverviewScreen openOverviewScreen;

    private BookGuiManager() {

    }

    public static BookGuiManager get() {
        return instance;
    }

    public boolean showErrorScreen(ResourceLocation bookId) {
        if (BookErrorManager.get().hasErrors(bookId)) {
            var book = BookDataManager.get().getBook(bookId);
            Minecraft.getInstance().setScreen(new BookErrorScreen(book));
            return true;
        }
        return false;
    }

    public void safeguardBooksBuilt() {
        if (!BookDataManager.get().areBooksBuilt()) {
            //This is a workaround/fallback for cases like https://github.com/klikli-dev/modonomicon/issues/48
            //Generally it should never happen, because client builds books on UpdateRecipesPacket
            //If that packet for some reason is not handled clientside, we build books here and hope for the best :)
            //Why don't we generally do it lazily like that? Because then markdown prerender errors only show in log if a book is actually opened
            BookDataManager.get().tryBuildBooks(Minecraft.getInstance().level);
            BookDataManager.get().prerenderMarkdown();
        }
    }

    public void openBook(ResourceLocation bookId) {
        this.safeguardBooksBuilt();

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

    public void pushHistory(ResourceLocation bookId, @Nullable ResourceLocation entryId, int page) {
        var book = BookDataManager.get().getBook(bookId);
        var entry = book.getEntry(entryId);
        this.history.push(new BookHistoryEntry(bookId, entry.getCategoryId(), entryId, page));
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

    public int getHistorySize() {
        return this.history.size();
    }

    public void resetHistory() {
        this.history.clear();
    }

    /**
     * Opens the book at the given location. Will open as far as possible (meaning, if category and entry are null, it
     * will not open those obviously).
     */
    public void openEntry(ResourceLocation bookId, @Nullable ResourceLocation categoryId, @Nullable ResourceLocation entryId, int page) {
        this.safeguardBooksBuilt();

        if (bookId == null) {
            throw new IllegalArgumentException("bookId cannot be null");
        }

        if (this.showErrorScreen(bookId)) {
            return;
        }

        if (!BookDataManager.get().areBooksBuilt()) {
            //This is a workaround/fallback for cases like https://github.com/klikli-dev/modonomicon/issues/48
            //Generally it should never happen, because client builds books on UpdateRecipesPacket
            //If that packet for some reason is not handled clientside, we build books here and hope for the best :)
            //Why don't we generally do it lazily like that? Because then markdown prerender errors only show in log if a book is actually opened
            BookDataManager.get().tryBuildBooks(Minecraft.getInstance().level);
            BookDataManager.get().prerenderMarkdown();
        }

        var book = BookDataManager.get().getBook(bookId);
        if (this.currentBook != book) {
            this.currentBook = book;
        }

        if (this.currentOverviewScreen == null || this.currentOverviewScreen.getBook() != book) {
            this.currentOverviewScreen = new BookOverviewScreen(book);
        }

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
        } else {
            //we are clearing the gui layers above, so we have to restore here if we do not call openentry
            //we check if the content screen was already added, e.g. by the book category screen
            if (!this.isEntryAlreadyDisplayed(entry))
                ClientServices.GUI.pushGuiLayer(this.currentContentScreen);

            //to ensure the current open entry is tracked on the category, we manually set it
            //this is necessary to ensure state is tracked and saved when closing again. Fixes #196
            this.currentCategoryScreen.setOpenEntry(entry.getId());
        }

        //we need to check for null, because this.currentCategoryScreen.openEntry(entry); returns null for "redirect" entries that open categories - because there is no content to show.
        if(this.currentContentScreen != null) {
            //we don't need to manually check for the current page because the content screen will do that for us
            this.currentContentScreen.goToPage(page, false);
        }
        //TODO: play sound here? could just make this a client config
    }

    public boolean isEntryAlreadyDisplayed(BookEntry entry) {
        return Minecraft.getInstance().screen instanceof BookContentScreen bookContentScreen && bookContentScreen.getEntry().equals(entry);
    }
}
