/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui;

import com.klikli_dev.modonomicon.api.events.EntryFirstReadEvent;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookDisplayMode;
import com.klikli_dev.modonomicon.book.PageDisplayMode;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.entries.CategoryLinkBookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookCategoryScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookErrorScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryDoublePageScreen;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntrySinglePageScreen;
import com.klikli_dev.modonomicon.client.gui.book.index.BookCategoryIndexOnNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.index.BookCategoryIndexScreen;
import com.klikli_dev.modonomicon.client.gui.book.index.BookParentIndexScreen;
import com.klikli_dev.modonomicon.client.gui.book.node.BookCategoryNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.node.DummyBookCategoryNodeScreen;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.events.ModonomiconEvents;
import com.klikli_dev.modonomicon.networking.BookEntryReadMessage;
import com.klikli_dev.modonomicon.networking.SaveBookStateMessage;
import com.klikli_dev.modonomicon.networking.SaveCategoryStateMessage;
import com.klikli_dev.modonomicon.networking.SaveEntryStateMessage;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.Stack;

public class BookGuiManager {

    private static final BookGuiManager instance = new BookGuiManager();

    private final Map<ResourceLocation, Stack<BookAddress>> history = new Object2ObjectArrayMap<>();

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

    protected boolean showErrorScreen(ResourceLocation bookId) {
        if (BookErrorManager.get().hasErrors(bookId)) {
            var book = BookDataManager.get().getBook(bookId);
            Minecraft.getInstance().setScreen(new BookErrorScreen(book));
            return true;
        }
        return false;
    }

    protected void safeguardBooksBuilt() {
        if (!BookDataManager.get().areBooksBuilt()) {
            //This is a workaround/fallback for cases like https://github.com/klikli-dev/modonomicon/issues/48
            //Generally it should never happen, because client builds books on UpdateRecipesPacket
            //If that packet for some reason is not handled clientside, we build books here and hope for the best :)
            //Why don't we generally do it lazily like that? Because then markdown prerender errors only show in log if a book is actually opened
            BookDataManager.get().registries(Minecraft.getInstance().level.registryAccess());
            BookDataManager.get().tryBuildBooks(Minecraft.getInstance().level);
            BookDataManager.get().prerenderMarkdown(Minecraft.getInstance().level.registryAccess());
        }
    }

    protected Player player() {
        return Minecraft.getInstance().player;
    }

    protected BookCategory getSavedOrAddressedCategory(Book book, BookAddress address) {
        if (address.categoryId() != null)
            return book.getCategory(address.categoryId());

        if (address.ignoreSavedCategory())
            return null;

        var state = BookVisualStateManager.get().getBookStateFor(this.player(), book);
        if (state != null && state.openCategory != null) {
            return book.getCategory(state.openCategory);
        }
        return null;
    }

    protected BookCategory getSavedOrAddressedCategoryOrDefault(Book book, BookAddress address) {
        var savedCategory = this.getSavedOrAddressedCategory(book, address);
        if (savedCategory == null) {
            return book.getCategoriesSorted().stream().filter(BookCategory::showCategoryButton).findFirst().orElseThrow();
        }
        return savedCategory;
    }

    protected BookEntry getSavedOrAddressedEntry(BookCategory category, BookAddress address) {
        if (address.entryId() != null)
            return category.getEntry(address.entryId());

        if (address.ignoreSavedEntry())
            return null;

        var state = BookVisualStateManager.get().getCategoryStateFor(this.player(), category);
        if (state != null && state.openEntry != null) {
            var openEntry = category.getEntry(state.openEntry);
            //we skip link entries, they would lead to categories not being opened because it instantly jumps to the linked one
            //they should not be in the history in the first place, but just to be sure
            if (openEntry != null && !(openEntry instanceof CategoryLinkBookEntry)) {
                //no need to load history here, will be handled by book content screen
                return openEntry;
            }
        }
        return null;
    }

    protected BookEntry getSavedOrAddressedEntryOrDefault(BookCategory category, BookAddress address) {
        var savedEntry = this.getSavedOrAddressedEntry(category, address);
        //If we do not have a saved entry, check if we have an entry to open specified in the category definition
        if (savedEntry == null && category.getEntryToOpen() != null) {
            var entryToOpen = category.getEntry(category.getEntryToOpen());
            if (!category.openEntryToOpenOnlyOnce() || !BookUnlockStateManager.get().isReadFor(Minecraft.getInstance().player, entryToOpen)) {
                return entryToOpen;
            }
        }

        return savedEntry;
    }

    public void openBook(BookAddress address) {
        this.safeguardBooksBuilt();

        if (this.showErrorScreen(address.bookId())) {
            return;
        }

        var book = BookDataManager.get().getBook(address.bookId());

        //if the book is a leaflet, ensure we have an address that directly opens the leaflet entry.
        if (book.isLeaflet()) {
            //if the address contains a specific page, preserve that. The leaflet might theoretically link within itself!
            address = address.page() > -1 ? book.getLeafletAddress().withPage(address.page()) : book.getLeafletAddress();
        }

        var displayMode = book.getDisplayMode();
        if (displayMode == BookDisplayMode.INDEX) {
            this.openBookInIndexMode(book, address);
        } else if (displayMode == BookDisplayMode.NODE) {
            this.openBookInNodeMode(book, address);
        }
    }

    protected void openBookInNodeMode(Book book, BookAddress address) {
        var openBookParentScreen = new BookParentNodeScreen(book);
        this.openBookParentScreen = openBookParentScreen;

        var state = BookVisualStateManager.get().getBookStateFor(this.player(), book);
        if (state != null) {
            openBookParentScreen.loadState(state);
        }

        //Call after restoring state, to ensure the correct page etc display right away
        Minecraft.getInstance().setScreen(openBookParentScreen);

        //run additional init logic (e.g. unlock state determination)
        openBookParentScreen.onDisplay();

        var openCategory = this.getSavedOrAddressedCategoryOrDefault(book, address);
        this.openCategory(openCategory, address);
    }

    protected void openBookInIndexMode(Book book, BookAddress address) {
        var openBookParentScreen = new BookParentIndexScreen(book);
        this.openBookParentScreen = openBookParentScreen;

        var state = BookVisualStateManager.get().getBookStateFor(this.player(), book);
        if (state != null) {
            openBookParentScreen.loadState(state);
        }

        //Call after restoring state, to ensure the correct page etc display right away
        Minecraft.getInstance().setScreen(openBookParentScreen);

        //run additional init logic (e.g. unlock state determination)
        openBookParentScreen.onDisplay();

        //Index mode does NOT use default categories, so we only get saved/address
        var openCategory = this.getSavedOrAddressedCategory(book, address);
        if (openCategory == null)
            return;

        this.openCategory(openCategory, address);
    }

    @ApiStatus.Internal
    public void openCategory(BookCategory category, BookAddress address) {
        if (this.openBookCategoryScreen != null) {
            BookGuiManager.get().closeCategoryScreen(this.openBookCategoryScreen);
        }

        var displayMode = category.getDisplayMode();
        //if the book is in index mode, force all categories into index mode too!
        if (displayMode == BookDisplayMode.INDEX || category.getBook().getDisplayMode() == BookDisplayMode.INDEX) {
            this.openCategoryInIndexMode(category, address);
        } else if (displayMode == BookDisplayMode.NODE) {
            this.openCategoryInNodeMode(category, address);
        }

        //We now need to clear the open category in the book state, so that the closing handling can work properly
        //Closing handling struggles with setting it to null (without lots of extra logic)
        //So we reset it here, and on close just set it when needed
        var bookState = BookVisualStateManager.get().getBookStateFor(this.player(), this.openBookParentScreen.getBook());
        bookState.openCategory = null;
    }

    protected void openCategoryInNodeMode(BookCategory category, BookAddress address) {
        //this is only possible if the book is in node mode
        if (category.getBook().getDisplayMode() != BookDisplayMode.NODE ||
                !(this.openBookParentScreen instanceof BookParentNodeScreen bookParentNodeScreen)) {
            throw new IllegalStateException("Cannot open category in node mode if book is not in node mode.");
        }

        var openBookCategoryScreen = new BookCategoryNodeScreen(bookParentNodeScreen, category);
        this.openBookCategoryScreen = openBookCategoryScreen;

        var state = BookVisualStateManager.get().getCategoryStateFor(this.player(), category);
        if (state != null) {
            openBookCategoryScreen.loadState(state);
        }

        //if the parent screen is a node screen, we can need to set the category on it as it needs this for rendering
        bookParentNodeScreen.setCurrentCategoryScreen(openBookCategoryScreen);
        openBookCategoryScreen.onDisplay();

        var openEntry = this.getSavedOrAddressedEntryOrDefault(category, address);
        if (openEntry == null)
            return;

        this.openEntry(openEntry, address);
    }

    protected void openCategoryInIndexMode(BookCategory category, BookAddress address) {
        //this is possible if the book is in node or in index mode, and we need different behaviour for each
        //node mode needs an additional "default" screen on the book screen, that we display the category over

        var openBookCategoryScreen = new BookCategoryIndexScreen(this.openBookParentScreen, category);

        if (category.getBook().getDisplayMode() == BookDisplayMode.NODE && this.openBookParentScreen instanceof BookParentNodeScreen bookParentNodeScreen) {
            //place a default screen on the parent node screen so we have a background to render
            bookParentNodeScreen.setCurrentCategoryScreen(new DummyBookCategoryNodeScreen(bookParentNodeScreen, category));

            //then use a special category index screen
            openBookCategoryScreen = new BookCategoryIndexOnNodeScreen(bookParentNodeScreen, category);
        }

        this.openBookCategoryScreen = openBookCategoryScreen;

        var state = BookVisualStateManager.get().getCategoryStateFor(this.player(), category);
        if (state != null) {
            openBookCategoryScreen.loadState(state);
        }

        //call after restoring state, to ensure the correct page etc display right away
        ClientServices.GUI.pushGuiLayer(openBookCategoryScreen);

        openBookCategoryScreen.onDisplay();

        var openEntry = this.getSavedOrAddressedEntryOrDefault(category, address);
        if (openEntry == null)
            return;

        this.openEntry(openEntry, address);
    }

    /**
     * Should only be called from BookEntry#openEntry()
     */
    @ApiStatus.Internal
    public void openContentEntry(BookContentEntry entry, BookAddress address) {
        var openBookEntryScreen = entry.getBook().getPageDisplayMode() == PageDisplayMode.DOUBLE_PAGE ? new BookEntryDoublePageScreen(this.openBookParentScreen, entry) : new BookEntrySinglePageScreen(this.openBookParentScreen, entry);

        this.openBookEntryScreen = openBookEntryScreen;

        if (address.page() != -1)
            openBookEntryScreen.goToPage(address.page(), false);
        else {
            var state = BookVisualStateManager.get().getEntryStateFor(this.player(), entry);
            if (state != null) {
                openBookEntryScreen.loadState(state);
                if (address.ignoreSavedPage())
                    openBookEntryScreen.setOpenPagesIndex(0);
            }
        }

        //do this after the page setup, because init() sets up the rendering for the correct page
        ClientServices.GUI.pushGuiLayer(openBookEntryScreen);

        //We now need to clear the open entry in the category state, so that the closing handling can work properly
        //Closing handling struggles with setting it to null (without lots of extra logic)
        //So we reset it here, and on close just set it when needed
        var categoryState = BookVisualStateManager.get().getCategoryStateFor(this.player(), this.openBookCategoryScreen.getCategory());
        categoryState.openEntry = null;
    }

    /**
     * Should only be called from BookEntry#openEntry()
     */
    @ApiStatus.Internal
    public void openCategoryLinkEntry(CategoryLinkBookEntry entry) {
        var category = entry.getCategoryToOpen();

        //Opening a category link means we already opened a book, and a category. These all need to be closed before reopening, so we just use our default opening method
        this.openBook(BookAddress.defaultFor(category));
    }

    @ApiStatus.Internal
    public void openEntry(BookEntry entry, BookAddress address) {
        if (!BookUnlockStateManager.get().isReadFor(this.player(), entry)) {
            Services.NETWORK.sendToServer(new BookEntryReadMessage(entry.getBook().getId(), entry.getId()));
            ModonomiconEvents.client().entryFirstRead(new EntryFirstReadEvent(entry.getBook().getId(), entry.getId()));
        }

        entry.openEntry(address); //visitor pattern that will call openContentEntry or openCategoryLinkEntry
    }

    public void pushHistory(ResourceLocation bookId, @Nullable ResourceLocation entryId, int page) {
        var book = BookDataManager.get().getBook(bookId);
        var entry = book.getEntry(entryId);
        this.history.computeIfAbsent(bookId, k -> new Stack<>()).push(BookAddress.of(bookId, entry.getCategoryId(), entryId, page));
    }

    public void pushHistory(ResourceLocation bookId, @Nullable ResourceLocation categoryId, @Nullable ResourceLocation entryId, int page) {
        this.history.computeIfAbsent(bookId, k -> new Stack<>()).push(BookAddress.of(bookId, categoryId, entryId, page));
    }

    public void pushHistory(BookAddress entry) {
        this.history.computeIfAbsent(entry.bookId(), k -> new Stack<>()).push(entry);
    }

    public BookAddress popHistory(ResourceLocation bookId) {
        if (!this.history.containsKey(bookId) || this.history.get(bookId).isEmpty())
            return null;
        return this.history.get(bookId).pop();
    }

    public BookAddress peekHistory(ResourceLocation bookId) {
        if (!this.history.containsKey(bookId) || this.history.get(bookId).isEmpty())
            return null;
        return this.history.get(bookId).peek();
    }

    public int getHistorySize(ResourceLocation bookId) {
        if (!this.history.containsKey(bookId))
            return 0;
        return this.history.get(bookId).size();
    }

    public void resetHistory() {
        this.history.clear();
    }

    /**
     * Opens the book at the given location. Will open as far as possible (meaning, if category and entry are null, it
     * will not open those obviously).
     */
    public void openEntry(ResourceLocation bookId, ResourceLocation entryId, int page) {
        var book = BookDataManager.get().getBook(bookId);
        var entry = book.getEntry(entryId);
        this.openEntry(bookId, entry.getCategoryId(), entryId, page);
    }

    /**
     * Saves the current mouse position and restores it after the given runnable is executed.
     * This is necessary because setting the screen to null will cause MC to grab the mouse and set it to screen center.
     */
    public void keepMousePosition(Runnable run) {
        var mousePos = Pair.of(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos());
        run.run();
        InputConstants.grabOrReleaseMouse(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_CURSOR_NORMAL, mousePos.getFirst(), mousePos.getSecond());
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
        }

        this.keepMousePosition(() -> {
            //First close the current book and preserve -state
            if (this.openBookEntryScreen != null)
                this.closeScreenStack(this.openBookEntryScreen);
            else if (this.openBookCategoryScreen != null)
                this.closeScreenStack(this.openBookCategoryScreen);
            else if (this.openBookParentScreen != null)
                this.closeScreenStack(this.openBookParentScreen);

            //then open the given address
            this.openBook(BookAddress.ignoreSaved(bookId, categoryId, entryId, page));
        });
    }

    /**
     * Call this when you want to close *just* the entry screen, but not the category and parent.
     * E.g. from the "close"/"x" button.
     */
    public void closeEntryScreen(BookEntryScreen screen) {
        this.closeEntryScreen(screen, false);
    }

    /**
     * Call this when you want to close *just* the entry screen, but not the category and parent.
     * E.g. from the "close"/"x" button.
     */
    public void closeEntryScreen(BookEntryScreen screen, boolean overrideStoreLastOpenPageWhenClosingEntry) {
        //close the entry screen
        if (ClientServices.GUI.getCurrentScreen() == screen)
            ClientServices.GUI.popGuiLayer();
        this.openBookEntryScreen = null;

        var state = BookVisualStateManager.get().getEntryStateFor(this.player(), screen.getEntry());
        //if we close "normally" without Esc we respect the config setting
        //for ESC closing we always save the page (see below #onEsc())
        screen.saveState(state, overrideStoreLastOpenPageWhenClosingEntry || ClientServices.CLIENT_CONFIG.storeLastOpenPageWhenClosingEntry());
        Services.NETWORK.sendToServer(new SaveEntryStateMessage(screen.getEntry(), state));

        //leaflets should never show the category or book screen
        //So we just hand the close down to the parent screen, with the instruction to close the remaining stack.
        if (screen.getBook().isLeaflet()) {
            this.closeScreenStack(this.openBookCategoryScreen);
        }
    }

    /**
     * Call this when you want to close *just* the category screen, but not the parent.
     * E.g. from the "close"/"x" button.
     */
    public void closeCategoryScreen(BookCategoryScreen screen) {
        //close the category screen
        //this check handles the case of node categories that are not real screens
        //thus no gui layer can be popped -> otherwise we already remove our parent screen!
        if (ClientServices.GUI.getCurrentScreen() == screen)
            ClientServices.GUI.popGuiLayer();
        this.openBookCategoryScreen = null;

        var state = BookVisualStateManager.get().getCategoryStateFor(this.player(), screen.getCategory());
        screen.saveState(state);
        Services.NETWORK.sendToServer(new SaveCategoryStateMessage(screen.getCategory(), state));
    }

    public void closeParentScreen(BookParentScreen screen) {
        ClientServices.GUI.popGuiLayer();
        Minecraft.getInstance().setScreen(null);
        this.openBookParentScreen = null;

        var state = BookVisualStateManager.get().getBookStateFor(this.player(), screen.getBook());
        screen.saveState(state);
        Services.NETWORK.sendToServer(new SaveBookStateMessage(screen.getBook(), state));
    }

    public void closeScreenStack(BookParentScreen screen) {
        //We don't need to do any additional state saving here, the other closeScreenStack overloads already handle that for us
        this.closeParentScreen(screen);
    }

    public void closeScreenStack(BookCategoryScreen screen) {
        this.closeCategoryScreen(screen);

        //if previous screen alreadly cleaned up, we exit early
        //this should not actually happen.
        if (this.openBookParentScreen == null)
            return;


        //set the open category on the book state, so that closeParentScreen sends it along.
        var bookState = BookVisualStateManager.get().getBookStateFor(this.player(), screen.getCategory().getBook());
        bookState.openCategory = screen.getCategory().getId();
        this.closeScreenStack(this.openBookParentScreen);
    }

    public void closeScreenStack(BookEntryScreen screen) {
        //close entry screen with forced saving of last page
        this.closeEntryScreen(screen, true);

        //if previous screen alreadly cleaned up, we exit early
        // -> this is the case for leaflets. That is also why it is safe to lose the "OpenEntry" state
        if (this.openBookCategoryScreen == null)
            return;

        //set the open entry on the category state, so that closeCategoryScreen sends it along.
        var categoryState = BookVisualStateManager.get().getCategoryStateFor(this.player(), this.openBookCategoryScreen.getCategory());
        categoryState.openEntry = screen.getEntry().getId();
        this.closeScreenStack(this.openBookCategoryScreen); //will then bubble down to close the parent screen
    }
}
