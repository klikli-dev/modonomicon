/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.entries.CategoryLinkBookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.client.gui.book.BookErrorScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookHistoryEntry;
import com.klikli_dev.modonomicon.client.gui.book.category.BookCategoryNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.category.BookCategoryScreen;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.parent.BookParentNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.parent.BookParentScreen;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.networking.BookEntryReadMessage;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class BookGuiManager {

    private static final BookGuiManager instance = new BookGuiManager();

    private final Stack<BookHistoryEntry> history = new Stack<>();

    /**
     * The currently open screen. Used for unlock state sync to immediately update the open screen.
     */
    public BookParentScreen openBookParentScreen;
    public BookCategoryScreen openBookCategoryScreen;
    public BookEntryScreen openBookEntryScreen;

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
            BookDataManager.get().prerenderMarkdown(Minecraft.getInstance().level.registryAccess());
        }
    }

    public void openBook(ResourceLocation bookId, Player player) {
        this.safeguardBooksBuilt();

        if (this.showErrorScreen(bookId)) {
            return;
        }

        var book = BookDataManager.get().getBook(bookId);

        var displayMode = book.getDisplayMode();
        if (displayMode == BookDisplayMode.INDEX) {
            this.openBookInIndexMode(book, player);
        } else if (displayMode == BookDisplayMode.NODE) {
            this.openBookInNodeMode(book, player);
        }
    }

    protected void openBookInIndexMode(Book book, Player player) {
        //TODO: here categories are always opened in index mode
        //TODO: Careful, here we only should get a category to open IF there is one saved. Don't auto open the first!
    }

    protected BookCategory getSavedCategory(Book book, Player player) {
        var state = BookVisualStateManager.get().getBookStateFor(player, book);
        if (state != null && state.openCategory != null) {
            return book.getCategory(state.openCategory);
        }
        return null;
    }

    protected BookCategory getSavedCategoryOrDefault(Book book, Player player) {
        var savedCategory = this.getSavedCategory(book, player);
        if (savedCategory == null) {
            return book.getCategoriesSorted().getFirst();
        }
        return savedCategory;
    }

    protected void openBookInNodeMode(Book book, Player player) {
        var openBookParentScreen = new BookParentNodeScreen(book);
        this.openBookParentScreen = openBookParentScreen;
        Minecraft.getInstance().setScreen(openBookParentScreen);

        //run additional init logic (e.g. unlock state determination)
        openBookParentScreen.onDisplay();

        var openCategory = this.getSavedCategoryOrDefault(book, player);
        var displayMode = openCategory.getDisplayMode();
        if (displayMode == BookDisplayMode.INDEX) {
            this.openCategoryInIndexMode(openCategory, player);
        } else if (displayMode == BookDisplayMode.NODE) {
            this.openCategoryInNodeMode(openCategory, player);
        }
    }


    protected BookEntry getSavedEntry(BookCategory category, Player player) {
        var state = BookVisualStateManager.get().getCategoryStateFor(player, category);
        if (state != null && state.openEntry != null) {
            var openEntry = category.getEntry(state.openEntry);
            //we skip link entries, they would lead to categories not being opened because it instantly jumps to the linked one
            //they should not be in the history in the first place, but but just to be sure
            if (openEntry != null && !(openEntry instanceof CategoryLinkBookEntry)) {
                //no need to load history here, will be handled by book content screen
                return openEntry;
            }
        }
        return null;
    }

    protected BookEntry getSavedEntryOrDefault(BookCategory category, Player player) {
        var savedEntry = this.getSavedEntry(category, player);
        if (savedEntry == null) {
            //If we do not have a saved entry, check if we have an entry to open specified in the category definition
            var entryToOpen = category.getEntry(category.getEntryToOpen());
            if (!category.openEntryToOpenOnlyOnce() || !BookUnlockStateManager.get().isReadFor(Minecraft.getInstance().player, entryToOpen)) {
                return entryToOpen;
            }
        }
        return savedEntry;
    }

    protected void openCategoryInNodeMode(BookCategory category, Player player) {
        //this is only possible if the book is in node mode
        if (!(this.openBookParentScreen instanceof BookParentNodeScreen bookParentNodeScreen)) {
            throw new IllegalStateException("Cannot open category in node mode if book is not in node mode.");
        }

        //on a node parent screen, we need to set the current category so it can track internally
        bookParentNodeScreen.setCurrentCategory(category);

        var openBookCategoryScreen = new BookCategoryNodeScreen(bookParentNodeScreen, category);
        this.openBookCategoryScreen = openBookCategoryScreen;

        var state = BookVisualStateManager.get().getCategoryStateFor(player, category);
        if (state != null) {
            openBookCategoryScreen.setState(state);
        }

        //if the parent screen is a node screen, we can need to set the category on it as it needs this for rendering
        bookParentNodeScreen.setCurrentCategoryScreen(openBookCategoryScreen);
        openBookCategoryScreen.onDisplay();

        var openEntry = this.getSavedEntryOrDefault(category, player);
        if (openEntry == null)
            return;

        this.openEntry(openEntry, player);
    }

    protected void openCategoryInIndexMode(BookCategory category, Player player) {
        //TODO: implement
        //this is possible if the book is in node or in index mode, and we need different behaviour for each
        //node mode needs an additional "default" screen on the book screen, that we display the category over
    }

    /**
     * Should only be called from BookEntry#openEntry()
     */
    @ApiStatus.Internal
    public void openContentEntry(BookContentEntry entry, Player player) {
        var openBookEntryScreen = new BookEntryScreen(this.openBookParentScreen, entry);
        this.openBookEntryScreen = openBookEntryScreen;
        ClientServices.GUI.pushGuiLayer(openBookEntryScreen);
    }

    /**
     * Should only be called from BookEntry#openEntry()
     */
    @ApiStatus.Internal
    public void openCategoryLinkEntry(CategoryLinkBookEntry entry, Player player) {
        var category = entry.getCategoryToOpen();
        //TODO: Change category logic
    }

    protected void openEntry(BookEntry entry, Player player) {
        if (!BookUnlockStateManager.get().isReadFor(Minecraft.getInstance().player, entry)) {
            Services.NETWORK.sendToServer(new BookEntryReadMessage(entry.getBook().getId(), entry.getId()));
        }

        entry.openEntry(player); //visitor pattern that will call openContentEntry or openCategoryLinkEntry
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

        //TODO: shouldn't saveguard do that?
        if (!BookDataManager.get().areBooksBuilt()) {
            //This is a workaround/fallback for cases like https://github.com/klikli-dev/modonomicon/issues/48
            //Generally it should never happen, because client builds books on UpdateRecipesPacket
            //If that packet for some reason is not handled clientside, we build books here and hope for the best :)
            //Why don't we generally do it lazily like that? Because then markdown prerender errors only show in log if a book is actually opened
            boolean b = BookDataManager.get().tryBuildBooks(Minecraft.getInstance().level);
            BookDataManager.get().prerenderMarkdown(Minecraft.getInstance().level.registryAccess());
        }


        //TODO: handle category link entries!
        var book = BookDataManager.get().getBook(bookId);
        if (this.lastBook != book) {
            this.lastBook = book;
        }

        if (this.lastBookParentScreen == null || this.lastBookParentScreen.getBook() != book) {
            this.lastBookParentScreen = new BookParentNodeScreen(book);
        }

        Minecraft.getInstance().setScreen(this.lastBookParentScreen);

        if (categoryId == null) {
            //if no category is provided, just open the book and exit.
            return;
        }

        var category = book.getCategory(categoryId);

        if (this.lastBookCategoryScreen == null || this.lastBookCategoryScreen.getCategory() != category) {
            this.lastBookParentScreen.changeCategory(category);
            this.lastBookCategoryScreen = this.lastBookParentScreen.getCurrentCategoryScreen();
        }

        if (entryId == null) {
            //if no entry is provided, just open the book and category and exit.
            return;
        }

        var entry = book.getEntry(entryId);
        if (this.lastEntry != entry) {
            this.lastEntry = entry;
        }

        if (this.lastBookEntryScreen == null || this.lastBookEntryScreen.getEntry() != entry) {
            this.lastBookEntryScreen = this.lastBookCategoryScreen.openEntry(entry);
        } else {
            //we are clearing the gui layers above, so we have to restore here if we do not call openentry
            //we check if the content screen was already added, e.g. by the book category screen
            if (!this.isEntryAlreadyDisplayed(entry))
                ClientServices.GUI.pushGuiLayer(this.lastBookEntryScreen);

            //to ensure the current open entry is tracked on the category, we manually set it
            //this is necessary to ensure state is tracked and saved when closing again. Fixes #196
            this.lastBookCategoryScreen.setOpenEntry(entry.getId());
        }

        //we need to check for null, because this.currentCategoryScreen.openEntry(entry); returns null for "redirect" entries that open categories - because there is no content to show.
        if (this.lastBookEntryScreen != null) {
            //we don't need to manually check for the current page because the content screen will do that for us
            this.lastBookEntryScreen.goToPage(page, false);
        }
        //TODO: play sound here? could just make this a client config
    }

    public boolean isEntryAlreadyDisplayed(BookEntry entry) {
        return Minecraft.getInstance().screen instanceof BookEntryScreen bookEntryScreen && bookEntryScreen.getEntry().equals(entry);
    }
}
