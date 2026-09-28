/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.index;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedListScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.bookmarks.BookBookmarksScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.CategoryListButton;
import com.klikli_dev.modonomicon.client.gui.book.button.BookSideButtonRenderer;
import com.klikli_dev.modonomicon.client.gui.book.button.ResearchProgressButton;
import com.klikli_dev.modonomicon.client.gui.book.button.SearchButton;
import com.klikli_dev.modonomicon.client.gui.book.button.ShowBookmarksButton;
import com.klikli_dev.modonomicon.client.gui.book.button.ShowRecentlyUnlockedButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.recentlyunlocked.BookRecentlyUnlockedScreen;
import com.klikli_dev.modonomicon.client.gui.book.search.BookSearchScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * An index-based book parent screen. Categories are displayed as a list (as opposed to a "quest/progress" view).
 */
public class BookParentIndexScreen extends BookPaginatedListScreen<BookCategory> implements BookParentScreen {
    protected final Book book;

    private boolean hasUnreadEntries;
    private boolean hasUnreadUnlockedEntries;
    private boolean hasUnreadCategories;
    private boolean hasUnreadUnlockedCategories;

    public BookParentIndexScreen(Book book) {
        super(Component.translatable(book.getName()));

        this.book = book;
    }

    protected void updateUnreadEntriesState() {
        //check if ANY entry is unread
        this.hasUnreadEntries = this.book.getEntries().values().stream().anyMatch(e -> BookServices.stateAccess().isEntryUnread(this.minecraft.player, e));

        //check if any currently unlocked entry is unread
        this.hasUnreadUnlockedEntries = this.book.getEntries().values().stream().anyMatch(e ->
                BookServices.visibility().isVisible(this.minecraft.player, e) && BookServices.stateAccess().isEntryUnread(this.minecraft.player, e));

        //check if ANY category is unread
        this.hasUnreadCategories = this.book.getCategories().values().stream().anyMatch(c -> BookServices.interaction().isCategoryUnread(this.minecraft.player, c));

        //check if any currently unlocked category is unread
        this.hasUnreadUnlockedCategories = this.book.getCategories().values().stream().anyMatch(c -> BookServices.visibility().isVisible(this.minecraft.player, c) && BookServices.interaction().isCategoryUnread(this.minecraft.player, c));
    }

    private boolean hasVisibleResearchProgress() {
        return this.book.getEntries().values().stream().anyMatch(e ->
                BookServices.visibility().isVisible(this.minecraft.player, e)
                        && ResearchServices.hooks().canProgressEntryViewedOnce(this.minecraft.player, e.getId()));
    }

    private boolean hasAnyResearchProgress() {
        return this.book.getEntries().values().stream().anyMatch(e ->
                ResearchServices.hooks().canProgressEntryViewedOnce(this.minecraft.player, e.getId()));
    }

    @Override
    public void handleButtonEntry(Button button) {
        if (button instanceof CategoryListButton categoryListButton) {
            BookGuiManager.get().openCategory(categoryListButton.getCategory(), BookAddress.defaultFor(categoryListButton.getCategory()));
        }
    }

    @Override
    protected void openOnlyVisibleEntry() {
        var category = this.visibleEntries.get(0);
        BookGuiManager.get().openCategory(category, BookAddress.defaultFor(category));
    }

    @Override
    protected boolean fillLeftPageOnFirstPage() {
        //the description takes the left page, so the list starts on the right
        return !this.shouldShowDescription();
    }

    protected boolean shouldShowDescription() {
        return !this.book.getDescription().isEmpty();
    }

    private Collection<BookCategory> getEntries() {
        return this.getBook().getCategories().values();
    }

    @Override
    protected List<BookCategory> computeAllEntries() {
        return this.getEntries().stream().sorted(Comparator.comparingInt(BookCategory::getSortNumber)
                        .thenComparing(a -> I18n.get(a.getName())))
                .toList();
    }

    @Override
    protected Button createEntryButton(BookCategory entry, int x, int y) {
        return new CategoryListButton(entry, x, y, this::handleButtonEntry);
    }

    @Override
    @Nullable
    protected LinkedText linkedText() {
        if (this.openPagesIndex == 0 && this.shouldShowDescription()) {
            return new LinkedText(this.book.getDescription(),
                    BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - (BookEntryScreen.TOP_PADDING + 22),
                    true);
        }
        return null;
    }

    @Override
    public void closeForExternalNavigation() {
        BookGuiManager.get().closeScreenStack(this);
    }

    protected void drawTitle(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.pose().pushMatrix();
        var scale = Math.min(1.0f, (float) BookEntryScreen.MAX_TITLE_WIDTH / (float) this.font.width(this.getTitle()));
        if (scale < 1) {
            guiGraphics.pose().translate(x - x * scale, y - y * scale);
            guiGraphics.pose().scale(scale, scale);
        }

        //we use scale 1 because our scale translation handling in there is off a bit. the above translation code is better
        this.drawCenteredStringNoShadow(guiGraphics, this.getTitle(), x, y, this.getBook().theme().palette().defaultTitleColor(), 1);
        guiGraphics.pose().popMatrix();
    }

    @Override
    public Book getBook() {
        return this.book;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (BookGuiManager.get().openBookCategoryScreen != null) //do not render self while a category screen is open to avoid double render effects
            return;

        this.resetTooltip();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);

        BookContentRenderer.renderBookBackground(guiGraphics, this.getBook());


        if (this.openPagesIndex == 0) {
            if (!this.shouldShowDescription()) {

                this.drawTitle(guiGraphics, BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING);

                BookContentRenderer.drawTitleSeparator(guiGraphics, this.getBook(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
            } else {

                this.drawTitle(guiGraphics, BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING);

                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.CATEGORY_INDEX_LIST_TITLE),
                        BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                        this.getBook().theme().palette().defaultTitleColor());

                BookContentRenderer.drawTitleSeparator(guiGraphics, this.getBook(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
                BookContentRenderer.drawTitleSeparator(guiGraphics, this.getBook(),
                        BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);

                BookPageRenderer.renderIndexBookTextHolder(guiGraphics, this.book.getDescription(), this.font, this.book.theme().layout(),
                        BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - (BookEntryScreen.TOP_PADDING + 22),
                        this.book.theme().palette().defaultTextColor());
            }
        }

        guiGraphics.pose().popMatrix();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //hover tooltips for links in the book description, if any
        this.renderLinkedTextHover(guiGraphics, pMouseX, pMouseY);

        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    public void onDisplay() {
        this.updateUnreadEntriesState();
    }

    @Override
    public void onClose() {
        //do not call super, as it would close the screen stack
        //In most cases closeEntryScreen should be called directly, but if our parent BookPaginatedScreen wants us to close we need to handle that
        BookGuiManager.get().closeParentScreen(this);
    }

    @Override
    public void loadState(BookVisualState state) {
        this.openPagesIndex = state.openPagesIndex;
    }

    @Override
    public void saveState(BookVisualState state) {
        state.openPagesIndex = this.openPagesIndex;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ENTER) {
            if (this.visibleEntries.size() == 1) {
                this.openOnlyVisibleEntry();
                return true;
            }
        }

        return super.keyPressed(event);
    }

    @Override
    public void init() {
        super.init();

        this.allEntries = this.computeAllEntries();

        this.createEntryList();


        int scissorX = this.bookLeft + FULL_WIDTH;//this is the render location of our frame so our search button never overlaps
        int buttonHeight = this.getBook().theme().content().searchButton().normal().height();
        int searchButtonX = BookSideButtonRenderer.anchoredButtonX(scissorX);
        int searchButtonY = this.bookTop + FULL_HEIGHT - 30;
        int searchButtonWidth = this.getBook().theme().content().searchButton().normal().width();

        var searchButton = new SearchButton(this, searchButtonX, searchButtonY,
                scissorX,
                searchButtonWidth, buttonHeight,
                (b) -> this.onSearchButtonClick((SearchButton) b),
                Tooltip.create(Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_SEARCH)));
        this.addRenderableWidget(searchButton);

        searchButtonY -= buttonHeight + 2;

        var showBookmarksButton = new ShowBookmarksButton(this, searchButtonX, searchButtonY,
                scissorX,
                searchButtonWidth, buttonHeight,
                (b) -> this.onShowBookmarksButtonClick((ShowBookmarksButton) b),
                Tooltip.create(Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_BOOKMARKS)));
        this.addRenderableWidget(showBookmarksButton);

        if (this.book.showRecentlyUnlocked()) {
            searchButtonY -= buttonHeight + 2;

            var showRecentlyUnlockedButton = new ShowRecentlyUnlockedButton(this, searchButtonX, searchButtonY,
                    scissorX,
                    searchButtonWidth, buttonHeight,
                    (b) -> this.onShowRecentlyUnlockedButtonClick((ShowRecentlyUnlockedButton) b),
                    Tooltip.create(Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_RECENTLY_UNLOCKED)));
            this.addRenderableWidget(showRecentlyUnlockedButton);
        }

        int readAllButtonY = this.bookTop + 15;
        var researchProgressButton = new ResearchProgressButton(this, searchButtonX, readAllButtonY, scissorX,
                this::hasVisibleResearchProgress,
                this::hasAnyResearchProgress,
                () -> {},
                () -> {});

        this.addRenderableWidget(researchProgressButton);
    }

    protected void onSearchButtonClick(SearchButton button) {
        ClientServices.GUI.pushGuiLayer(new BookSearchScreen(this));
    }

    protected void onShowBookmarksButtonClick(ShowBookmarksButton button) {
        ClientServices.GUI.pushGuiLayer(new BookBookmarksScreen(this));
    }

    protected void onShowRecentlyUnlockedButtonClick(ShowRecentlyUnlockedButton button) {
        ClientServices.GUI.pushGuiLayer(new BookRecentlyUnlockedScreen(this));
    }

    @Override
    protected boolean isClickOutsideEntry(double pMouseX, double pMouseY) {
        //extend the right safety margin a bit to account for search button there
        return pMouseX < this.bookLeft - BookEntryScreen.CLICK_SAFETY_MARGIN
                || pMouseX > this.bookLeft + BookEntryScreen.FULL_WIDTH + BookEntryScreen.CLICK_SAFETY_MARGIN + 20
                || pMouseY < this.bookTop - BookEntryScreen.CLICK_SAFETY_MARGIN
                || pMouseY > this.bookTop + BookEntryScreen.FULL_HEIGHT + BookEntryScreen.CLICK_SAFETY_MARGIN;
    }

    @Override
    public boolean isPauseScreen() {
        return ClientServices.CLIENT_CONFIG.pauseGameWhenOpen();
    }
}
