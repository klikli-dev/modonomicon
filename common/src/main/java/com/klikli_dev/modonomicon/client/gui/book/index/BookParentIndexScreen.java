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
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.*;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.search.BookSearchScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.networking.ClickReadAllButtonMessage;
import com.klikli_dev.modonomicon.networking.SyncBookUnlockStatesMessage;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.util.GuiGraphicsExt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * An index-based book parent screen. Categories are displayed as a list (as opposed to a "quest/progress" view).
 */
public class BookParentIndexScreen extends BookPaginatedScreen implements BookParentScreen {
    public static final int ENTRIES_PER_PAGE = 13;
    public static final int ENTRIES_IN_FIRST_PAGE = 11;
    protected final List<Button> entryButtons = new ArrayList<>();
    protected final Book book;
    private final List<BookCategory> visibleEntries = new ArrayList<>();
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    private int openPagesIndex;
    private int maxOpenPagesIndex;
    private List<BookCategory> allEntries;
    private List<Component> tooltip;

    private boolean hasUnreadEntries;
    private boolean hasUnreadUnlockedEntries;

    public BookParentIndexScreen(Book book) {
        super(Component.translatable(book.getName()));

        this.book = book;
    }

    protected void updateUnreadEntriesState() {
        //check if ANY entry is unread
        this.hasUnreadEntries = this.book.getEntries().values().stream().anyMatch(e -> !BookUnlockStateManager.get().isReadFor(this.minecraft.player, e));

        //check if any currently unlocked entry is unread
        this.hasUnreadUnlockedEntries = this.book.getEntries().values().stream().anyMatch(e ->
                BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, e) &&
                        !BookUnlockStateManager.get().isReadFor(this.minecraft.player, e));
    }

    public void handleButtonEntry(Button button) {
        if (button instanceof CategoryListButton categoryListButton) {
            BookGuiManager.get().openCategory(categoryListButton.getCategory(), BookAddress.defaultFor(categoryListButton.getCategory()));
        }
    }

    public void drawCenteredStringNoShadow(GuiGraphics guiGraphics, Component s, int x, int y, int color) {
        this.drawCenteredStringNoShadow(guiGraphics, s, x, y, color, 1.0f);
    }

    public void drawCenteredStringNoShadow(GuiGraphics guiGraphics, Component s, int x, int y, int color, float scale) {
        GuiGraphicsExt.drawString(guiGraphics, this.font, s, x - this.font.width(s) * scale / 2.0F, y + (this.font.lineHeight * (1 - scale)), color, false);
    }

    public boolean canSeeArrowButton(boolean left) {
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + 1) < this.maxOpenPagesIndex;
    }

    protected void flipPage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {

            if (left) {
                this.openPagesIndex--;
            } else {
                this.openPagesIndex++;
            }

            this.onPageChanged();
            if (playSound) {
                BookEntryScreen.playTurnPageSound(this.getBook());
            }
        }
    }

    protected void drawTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            guiGraphics.renderComponentTooltip(this.font, this.tooltip, pMouseX, pMouseY);
        }
    }

    protected void onPageChanged() {
        this.createEntryList();
    }

    protected void resetTooltip() {
        this.tooltip = null;
    }

    protected boolean shouldShowDescription() {
        return !this.book.getDescription().isEmpty();
    }

    private void createEntryList() {
        this.entryButtons.forEach(b -> {
            this.renderables.remove(b);
            this.children().remove(b);
            this.narratables.remove(b);
        });

        this.entryButtons.clear();
        this.visibleEntries.clear();

        //here we could do some filtering like on the search screen
        this.visibleEntries.addAll(this.allEntries);

        this.maxOpenPagesIndex = 1;
        int count = this.visibleEntries.size();
        count -= ENTRIES_IN_FIRST_PAGE;
        if (count > 0) {
            this.maxOpenPagesIndex += (int) Math.ceil((float) count / (ENTRIES_PER_PAGE * 2));
        }

        while (this.getEntryCountStart() > this.visibleEntries.size()) {
            this.openPagesIndex--;
        }

        if (this.openPagesIndex == 0) {
            if (this.shouldShowDescription()) {
                //only show on the right for the first page
                this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING + 20, 0, ENTRIES_IN_FIRST_PAGE);
            } else {
                this.addEntryButtons(BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 20, 0, ENTRIES_IN_FIRST_PAGE);
                this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING, ENTRIES_IN_FIRST_PAGE, ENTRIES_PER_PAGE);
            }
        } else {
            int start = this.getEntryCountStart();
            this.addEntryButtons(BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING, start, ENTRIES_PER_PAGE);
            this.addEntryButtons(BookEntryScreen.RIGHT_PAGE_X - 3, BookEntryScreen.TOP_PADDING, start + ENTRIES_PER_PAGE, ENTRIES_PER_PAGE);
        }
    }

    private int getEntryCountStart() {
        if (this.openPagesIndex == 0) {
            return 0;
        }

        int start = ENTRIES_IN_FIRST_PAGE;
        start += (ENTRIES_PER_PAGE * 2) * (this.openPagesIndex - 1);
        return start;
    }

    private Collection<BookCategory> getEntries() {
        return this.getBook().getCategories().values();
    }

    @Override
    public void setTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public Book getBook() {
        return this.book;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (BookGuiManager.get().openBookCategoryScreen != null) //do not render self while a category screen is open to avoid double render effects
            return;

        this.resetTooltip();

        //we need to modify blit offset (now: z pose) to not draw over toasts
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -1300);  //magic number arrived by testing until toasts show, but BookOverviewScreen does not
        this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);

        BookEntryScreen.renderBookBackground(guiGraphics, this.getBook().getBookContentTexture());


        if (this.openPagesIndex == 0) {
            if (!this.shouldShowDescription()) {
                this.drawCenteredStringNoShadow(guiGraphics, this.getTitle(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                        this.getBook().getDefaultTitleColor());

                BookEntryScreen.drawTitleSeparator(guiGraphics, this.getBook(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
            } else {
                this.drawCenteredStringNoShadow(guiGraphics, this.getTitle(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                        this.getBook().getDefaultTitleColor());
                this.drawCenteredStringNoShadow(guiGraphics, Component.translatable(Gui.CATEGORY_INDEX_LIST_TITLE),
                        BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING,
                        this.getBook().getDefaultTitleColor());

                BookEntryScreen.drawTitleSeparator(guiGraphics, this.getBook(),
                        BookEntryScreen.LEFT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);
                BookEntryScreen.drawTitleSeparator(guiGraphics, this.getBook(),
                        BookEntryScreen.RIGHT_PAGE_X + BookEntryScreen.PAGE_WIDTH / 2, BookEntryScreen.TOP_PADDING + 12);

                BookPageRenderer.renderBookTextHolder(guiGraphics, this.book.getDescription(), this.font,
                        BookEntryScreen.LEFT_PAGE_X, BookEntryScreen.TOP_PADDING + 22, BookEntryScreen.PAGE_WIDTH);
            }
        }

        guiGraphics.pose().popPose();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

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
    public void onSyncBookUnlockCapabilityMessage(SyncBookUnlockStatesMessage message) {
        //this leads to re-init of the category buttons after a potential unlock
        this.rebuildWidgets();

        this.updateUnreadEntriesState();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }

        if (key == GLFW.GLFW_KEY_ENTER) {
            if (this.visibleEntries.size() == 1) {
                var entry = this.visibleEntries.get(0);
                BookGuiManager.get().openEntry(entry.getBook().getId(), entry.getId(), 0);
                return true;
            }
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    protected boolean canSeeReadAllButton() {
        return this.hasUnreadEntries || this.hasUnreadUnlockedEntries;
    }


    protected void onReadAllButtonClick(ReadAllButton button) {
        if (this.hasUnreadUnlockedEntries && !Screen.hasShiftDown()) {
            Services.NETWORK.sendToServer(new ClickReadAllButtonMessage(this.book.getId(), false));
            this.hasUnreadUnlockedEntries = false;
        } else if (this.hasUnreadEntries && Screen.hasShiftDown()) {
            Services.NETWORK.sendToServer(new ClickReadAllButtonMessage(this.book.getId(), true));
            this.hasUnreadEntries = false;
        }
    }

    @Override
    public void init() {
        super.init();

        //we filter out entries that are locked or in locked categories
        this.allEntries = this.getEntries().stream().filter(e ->
                        BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, e) &&
                                BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, e)
                ).sorted(Comparator.comparingInt(BookCategory::getSortNumber)
                        .thenComparing(a -> I18n.get(a.getName())))
                .toList();

        this.createEntryList();


        int readAllButtonX = this.bookLeft + FULL_WIDTH - ReadAllButton.WIDTH / 2;
        int readAllButtonY =  this.bookTop + ReadAllButton.HEIGHT + 15;

        var readAllButton = new ReadAllButton(this, readAllButtonX, readAllButtonY,
                () -> this.hasUnreadUnlockedEntries, //if we have unlocked entries that are not read -> blue
                this::canSeeReadAllButton, //display condition -> if we have any unlocked entries -> grey
                (b) -> this.onReadAllButtonClick((ReadAllButton) b));

        this.addRenderableWidget(readAllButton);

        int buttonHeight = 20;
        int searchButtonX = this.bookLeft + FULL_WIDTH - 5;
        int searchButtonY = this.bookTop + FULL_HEIGHT - 30;
        int searchButtonWidth = 44; //width in png
        int scissorX = this.bookLeft + FULL_WIDTH;//this is the render location of our frame so our search button never overlaps

        var searchButton = new SearchButton(this, searchButtonX, searchButtonY,
                scissorX,
                searchButtonWidth, buttonHeight,
                (b) -> this.onSearchButtonClick((SearchButton) b),
                Tooltip.create(Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_SEARCH)));

        this.addRenderableWidget(searchButton);
    }

    protected void onSearchButtonClick(SearchButton button) {
        ClientServices.GUI.pushGuiLayer(new BookSearchScreen(this));
    }

    protected void addEntryButtons(int x, int y, int start, int count) {
        for (int i = 0; i < count && (i + start) < this.visibleEntries.size(); i++) {
            Button button = new CategoryListButton(this.visibleEntries.get(start + i), this.bookLeft + x, this.bookTop + y + i * 11, this::handleButtonEntry);
            this.addRenderableWidget(button);
            this.entryButtons.add(button);
        }
    }

    @Override
    protected boolean isClickOutsideEntry(double pMouseX, double pMouseY) {
        //extend the right safety margin a bit to account for search button there
        return pMouseX < this.bookLeft - BookEntryScreen.CLICK_SAFETY_MARGIN
                || pMouseX > this.bookLeft + BookEntryScreen.FULL_WIDTH + BookEntryScreen.CLICK_SAFETY_MARGIN + 20
                || pMouseY < this.bookTop - BookEntryScreen.CLICK_SAFETY_MARGIN
                || pMouseY > this.bookTop + BookEntryScreen.FULL_HEIGHT + BookEntryScreen.CLICK_SAFETY_MARGIN;
    }
}
