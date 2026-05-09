/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Arcana
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.client.gui.book.node;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import com.klikli_dev.modonomicon.client.gui.book.bookmarks.BookBookmarksScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.*;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiFrameOverlay;
import com.klikli_dev.modonomicon.client.gui.book.recentlyunlocked.BookRecentlyUnlockedScreen;
import com.klikli_dev.modonomicon.client.gui.book.search.BookSearchScreen;
import com.klikli_dev.modonomicon.networking.SyncBookUnlockStatesMessage;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class BookParentNodeScreen extends Screen implements BookParentScreen, BookScreenWithButtons {

    public static final int MAX_CATEGORY_BUTTONS = 13;

    private final Book book;
    private final List<BookCategory> categories;
    //TODO: make the frame thickness configurable in the book?
    private final int frameThicknessW = 14;
    private final int frameThicknessH = 14;
    //This allows the BookCategoryIndexOnNodeScreen to give us mouseX and Y coordinates when this screen is rendered on a lower layer and does not get  x/y coordinates.
    public int renderMouseXOverride = -1;
    public int renderMouseYOverride = -1;
    private BookCategoryNodeScreen currentCategoryNodeScreen;
    private boolean hasUnreadEntries;
    private boolean hasUnreadUnlockedEntries;
    private boolean hasUnreadCategories;
    private boolean hasUnreadUnlockedCategories;

    private int categoryButtonRenderOffset = 0;

    public BookParentNodeScreen(Book book) {
        super(Component.literal(""));

        this.book = book;

        this.categories = book.getCategoriesSorted(); //we no longer handle category locking here, is done on init to be able to refresh on unlock
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    @Override
    public void onDisplay() {
        this.updateUnreadEntriesState();
    }

    protected void updateUnreadEntriesState() {
        //check if ANY entry is unread
        this.hasUnreadEntries = this.book.getEntries().values().stream().anyMatch(e -> !BookUnlockStateManager.get().isReadFor(this.minecraft.player, e));

        //check if any currently unlocked entry is unread
        this.hasUnreadUnlockedEntries = this.book.getEntries().values().stream().anyMatch(e -> BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, e) && !BookUnlockStateManager.get().isReadFor(this.minecraft.player, e));

        //check if ANY category is unread
        this.hasUnreadCategories = this.book.getCategories().values().stream().anyMatch(c -> !BookUnlockStateManager.get().isCategoryReadFor(this.minecraft.player, c));

        //check if any currently unlocked category is unread
        this.hasUnreadUnlockedCategories = this.book.getCategories().values().stream().anyMatch(c -> BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, c) && !BookUnlockStateManager.get().isCategoryReadFor(this.minecraft.player, c));
    }

    public BookCategoryNodeScreen getCurrentCategoryScreen() {
        return this.currentCategoryNodeScreen;
    }

    public void setCurrentCategoryScreen(BookCategoryNodeScreen currentCategoryNodeScreen) {
        this.currentCategoryNodeScreen = currentCategoryNodeScreen;
    }

    @Override
    public Book getBook() {
        return this.book;
    }

    @Override
    public void setTooltip(List<Component> tooltip) {
        //Just implemented for category scroll button, which will set its own tooltip
        //So we do nothing
    }

    /**
     * gets the x coordinate of the inner area of the book frame
     */
    public int getInnerX() {
        return (this.width - this.getFrameWidth()) / 2 + this.frameThicknessW / 2;
    }

    /**
     * gets the y coordinate of the inner area of the book frame
     */
    public int getInnerY() {
        return (this.height - this.getFrameHeight()) / 2 + this.frameThicknessH / 2;
    }

    /**
     * gets the width of the inner area of the book frame
     */
    public int getInnerWidth() {
        return this.getFrameWidth() - this.frameThicknessW;
    }

    /**
     * gets the height of the inner area of the book frame
     */
    public int getInnerHeight() {
        return this.getFrameHeight() - this.frameThicknessH;
    }

    public int getFrameThicknessW() {
        return this.frameThicknessW;
    }

    public int getFrameThicknessH() {
        return this.frameThicknessH;
    }


    /**
     * Gets the outer width of the book frame
     */
    public int getFrameWidth() {
        //TODO: enable config frame width
        return this.width - 60;
    }

    /**
     * Gets the outer height of the book frame
     */
    public int getFrameHeight() {
        //TODO: enable config frame height
        return this.height - 20;
    }

    protected void renderFrame(GuiGraphicsExtractor guiGraphics) {
        int width = this.getFrameWidth();
        int height = this.getFrameHeight();
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        //draw a resizeable border. Center parts of each side will be tiled.
        //this is controlled via the frame.png.mcmeta in the theme frame folder.
        this.book.theme().frame().frame().extractRenderState(guiGraphics, x, y, width, height);

        //now render overlays on top of that border to cover repeating elements
        this.renderFrameOverlay(guiGraphics, this.book.theme().frame().topOverlay(), (x + (width / 2)), y);
        this.renderFrameOverlay(guiGraphics, this.book.theme().frame().bottomOverlay(), (x + (width / 2)), (y + height));
        this.renderFrameOverlay(guiGraphics, this.book.theme().frame().leftOverlay(), x, y + (height / 2));
        this.renderFrameOverlay(guiGraphics, this.book.theme().frame().rightOverlay(), x + width, y + (height / 2));
    }

    protected void renderFrameOverlay(GuiGraphicsExtractor guiGraphics, GuiFrameOverlay overlay, int x, int y) {
        if (!overlay.sprite().isEmpty()) {
            overlay.sprite().extractRenderState(guiGraphics, overlay.getFrameX(x), overlay.getFrameY(y));
        }
    }

    protected void onBookCategoryButtonClick(CategoryButton button) {
        BookGuiManager.get().openCategory(button.getCategory(), BookAddress.defaultFor(button.getCategory().getBook()));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        //ignore return value, because we need our base class to handle dragging and such
        this.getCurrentCategoryScreen().mouseClicked(event, isDoubleClick);
        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        return this.getCurrentCategoryScreen().mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.getCurrentCategoryScreen().zoom(scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.renderMouseXOverride != -1 && this.renderMouseYOverride != -1) {
            pMouseX = this.renderMouseXOverride;
            pMouseY = this.renderMouseYOverride;
        }

        //not needed any more in 1.21.8+ because screen now calls it before render()
//        this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);

        this.getCurrentCategoryScreen().renderBackground(guiGraphics);

        this.getCurrentCategoryScreen().render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        this.renderFrame(guiGraphics);

        this.getCurrentCategoryScreen().renderEntryTooltips(guiGraphics, pMouseX, pMouseY, pPartialTick);

        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        this.renderMouseXOverride = -1;
        this.renderMouseYOverride = -1;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        //delegate key handling to the open category
        //this ensures the open category is saved by calling the right overload of onEsc
        if (this.getCurrentCategoryScreen().keyPressed(event)) {
            return true;
        }

        //This is unlikely to be reached as the category screen will already handle esc
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        //do not call super - gui manager should handle gui removal
    }

    @Override
    public void loadState(BookVisualState state) {
        //currently nothing to save - open category is handled by gui manager
    }

    @Override
    public void saveState(BookVisualState state) {
        //currently nothing to save - open category is handled by gui manager
    }

    private void sendBookClosedPacket(net.minecraft.world.InteractionHand hand) {
        Services.NETWORK.sendToServer(new com.klikli_dev.modonomicon.networking.BookClosedMessage(hand));
    }

    @Override
    public void onSyncBookUnlockStatesMessage(SyncBookUnlockStatesMessage message) {
        //this leads to re-init of the category buttons after a potential unlock
        this.rebuildWidgets();
        this.updateUnreadEntriesState();
    }

    @Override
    protected void init() {
        super.init();

        int buttonXOffset = -11;
        int buttonYOffset = 30 + this.getBook().theme().layout().categoryButtonYOffset();
        int buttonX = (this.width - this.getFrameWidth()) / 2 - this.getFrameThicknessW() + buttonXOffset;
        int buttonY = (this.height - this.getFrameHeight()) / 2 - this.getFrameThicknessH() + buttonYOffset;
        //calculate button width so it aligns with the outer edge of the frame
        int buttonWidth = (this.width - this.getFrameWidth()) / 2 + buttonXOffset + 6;
        int buttonHeight = this.getBook().theme().content().categoryButton().normal().height();
        int buttonSpacing = 2;

        this.updateCategoryButtons(buttonX, buttonY, buttonWidth, buttonHeight, buttonSpacing);

        int scissorX = this.getFrameWidth() + this.getFrameThicknessW() * 2 + 2; //this is the render location of our frame so our search button never overlaps
        int rightButtonX = BookSideButtonRenderer.anchoredButtonX(scissorX);

        int searchButtonYOffset = -30 + this.getBook().theme().layout().searchButtonYOffset();
        int searchButtonX = rightButtonX;
        int searchButtonY = this.getFrameHeight() + this.getFrameThicknessH() - ReadAllButton.HEIGHT / 2 + searchButtonYOffset;
        int searchButtonWidth = this.getBook().theme().content().searchButton().normal().width();
        buttonHeight = this.getBook().theme().content().searchButton().normal().height();

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

        int readAllButtonY = buttonY;
        var readAllButton = new ReadAllButton(this, rightButtonX, readAllButtonY, scissorX,
                () -> this.hasUnreadEntries,
                () -> this.hasUnreadUnlockedEntries,
                () -> this.hasUnreadCategories,
                () -> this.hasUnreadUnlockedCategories,
                () -> this.hasUnreadUnlockedEntries = false,
                () -> this.hasUnreadEntries = false);

        this.addRenderableWidget(readAllButton);
    }

    protected void updateCategoryButtons(int buttonX, int buttonY, int buttonWidth, int buttonHeight, int buttonSpacing) {
        int buttonCount = 0;
        for (int i = this.categoryButtonRenderOffset, size = this.categories.size(); i < size; i++) {
            if (buttonCount >= MAX_CATEGORY_BUTTONS) {
                break; // Stop adding buttons once we reach the maximum
            }
            if (this.categories.get(i).showCategoryButton() && BookUnlockStateManager.get().isUnlockedFor(this.minecraft.player, this.categories.get(i))) {
                var button = new CategoryButton(this, this.categories.get(i),
                        buttonX, buttonY + (buttonHeight + buttonSpacing) * buttonCount, buttonWidth, buttonHeight,
                        (b) -> this.onBookCategoryButtonClick((CategoryButton) b),
                        Tooltip.create(Component.translatable(this.categories.get(i).getName())));

                this.addRenderableWidget(button);
                buttonCount++;
            }
        }

        buttonX += 8;
        // Add the top scroll button if there are categories before the current offset
        if (this.categoryButtonRenderOffset > 0) {
            int topScrollButtonY = (this.height - this.getFrameHeight()) / 2 - 5;

            var topScrollButton = new CategoryScrollButton(this, buttonX, topScrollButtonY, false,
                    () -> this.categoryButtonRenderOffset > 0,
                    (b) -> {
                        this.categoryButtonRenderOffset--;
                        this.rebuildWidgets();
                    });
            this.addRenderableWidget(topScrollButton);
        }


        // Add the bottom scroll button if there are more categories than can be displayed
        if (this.categories.size() > this.categoryButtonRenderOffset + MAX_CATEGORY_BUTTONS) {
            int bottomScrollButtonY= this.getFrameHeight() + this.getFrameThicknessH() - ReadAllButton.HEIGHT / 2 - 3;

            var bottomScrollButton = new CategoryScrollButton(this, buttonX, bottomScrollButtonY, true,
                    () -> this.categories.size() > this.categoryButtonRenderOffset + MAX_CATEGORY_BUTTONS,
                    (b) -> {
                        this.categoryButtonRenderOffset++;
                        this.rebuildWidgets();
                    });
            this.addRenderableWidget(bottomScrollButton);
        }
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
    public boolean isPauseScreen() {
        return false;
    }
}

