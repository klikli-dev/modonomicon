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
import com.klikli_dev.modonomicon.book.BookLockedCategoryDisplay;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import com.klikli_dev.modonomicon.client.gui.book.bookmarks.BookBookmarksScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.*;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookLayoutTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiFrameOverlay;
import com.klikli_dev.modonomicon.client.gui.book.recentlyunlocked.BookRecentlyUnlockedScreen;
import com.klikli_dev.modonomicon.client.gui.book.search.BookSearchScreen;
import com.klikli_dev.modonomicon.networking.BookClosedMessage;
import com.klikli_dev.modonomicon.networking.SyncResearchStateMessage;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;

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
    private int categoryButtonCapacity = MAX_CATEGORY_BUTTONS;
    private boolean categoriesOverflow;

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
        this.hasUnreadEntries = this.book.getEntries().values().stream().anyMatch(e -> BookServices.stateAccess().isEntryUnread(this.minecraft.player, e));

        //check if any currently unlocked entry is unread
        this.hasUnreadUnlockedEntries = this.book.getEntries().values().stream().anyMatch(e -> BookServices.visibility().isVisible(this.minecraft.player, e) && BookServices.stateAccess().isEntryUnread(this.minecraft.player, e));

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
        var currentCategoryScreen = this.getCurrentCategoryScreen();
        if (currentCategoryScreen != null) {
            currentCategoryScreen.mouseClicked(event, isDoubleClick);
        }
        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        var currentCategoryScreen = this.getCurrentCategoryScreen();
        return currentCategoryScreen != null && currentCategoryScreen.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.scrollCategoryButtonsIfHovered(mouseX, mouseY, scrollY)) {
            return true;
        }
        var currentCategoryScreen = this.getCurrentCategoryScreen();
        if (currentCategoryScreen != null) {
            currentCategoryScreen.zoom(scrollY);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    /**
     * Scrolls the category buttons if they overflow and the mouse is over a category button or scroll arrow.
     * Also used by screens rendered on top of this one (e.g. index categories) to forward wheel events.
     *
     * @return true if the scroll event was consumed.
     */
    public boolean scrollCategoryButtonsIfHovered(double mouseX, double mouseY, double scrollY) {
        if (this.categoriesOverflow && scrollY != 0 && this.isMouseOverCategoryButtons(mouseX, mouseY)) {
            this.scrollCategoryButtons(scrollY > 0 ? -1 : 1);
            return true;
        }
        return false;
    }

    private boolean isMouseOverCategoryButtons(double mouseX, double mouseY) {
        return this.children().stream().anyMatch(child -> (child instanceof CategoryButton || child instanceof CategoryScrollButton) && child.isMouseOver(mouseX, mouseY));
    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.renderMouseXOverride != -1 && this.renderMouseYOverride != -1) {
            pMouseX = this.renderMouseXOverride;
            pMouseY = this.renderMouseYOverride;
        }

        var currentCategoryScreen = this.getCurrentCategoryScreen();

        //not needed any more in 1.21.8+ because screen now calls it before render()
//        this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);

        if (currentCategoryScreen != null) {
            currentCategoryScreen.renderBackground(guiGraphics);

            currentCategoryScreen.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //category and side buttons are drawn behind the frame, so the frame covers the part of them that extends under it
        for (var renderable : this.renderables) {
            if (isDrawnBehindFrame(renderable)) {
                renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        }

        this.renderFrame(guiGraphics);

        //unread indicators overhang the category buttons into the frame, so they are drawn on top of it
        for (var renderable : this.renderables) {
            if (renderable instanceof CategoryButton categoryButton) {
                categoryButton.extractUnreadIndicator(guiGraphics);
            }
        }

        if (currentCategoryScreen != null) {
            currentCategoryScreen.renderEntryTooltips(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            if (!isDrawnBehindFrame(renderable)) {
                renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        }

        this.renderMouseXOverride = -1;
        this.renderMouseYOverride = -1;
    }

    /**
     * Category buttons and the right side buttons (search, bookmarks, recently unlocked, read all) are drawn before the frame,
     * so that the frame covers the parts that extend under it, see {@link BookLayoutTheme#sideButtonFrameOverlap()}.
     */
    private static boolean isDrawnBehindFrame(Renderable renderable) {
        return renderable instanceof CategoryButton
                || renderable instanceof SearchButton
                || renderable instanceof ShowBookmarksButton
                || renderable instanceof ShowRecentlyUnlockedButton
                || renderable instanceof ResearchProgressButton;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        //delegate key handling to the open category
        //this ensures the open category is saved by calling the right overload of onEsc
        var currentCategoryScreen = this.getCurrentCategoryScreen();
        if (currentCategoryScreen != null && currentCategoryScreen.keyPressed(event)) {
            return true;
        }

        //This is unlikely to be reached as the category screen will already handle esc
        if (event.isEscape()) {
            BookGuiManager.get().closeScreenStack(this);
            return true;
        }

        if (this.minecraft.options.keyInventory.matches(event)) {
            BookGuiManager.get().closeAll();
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

    private void sendBookClosedPacket(InteractionHand hand) {
        Services.NETWORK.sendToServer(new BookClosedMessage(hand));
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
        int frameOverlap = this.getBook().theme().layout().sideButtonFrameOverlap();

        int searchButtonYOffset = -30 + this.getBook().theme().layout().searchButtonYOffset();
        int searchButtonX = rightButtonX;
        int searchButtonY = this.getFrameHeight() + this.getFrameThicknessH() - ResearchProgressButton.HEIGHT / 2 + searchButtonYOffset;
        int searchButtonWidth = this.getBook().theme().content().searchButton().normal().width();
        buttonHeight = this.getBook().theme().content().searchButton().normal().height();

        var searchButton = new SearchButton(this, searchButtonX, searchButtonY,
                scissorX,
                searchButtonWidth, buttonHeight,
                (b) -> this.onSearchButtonClick((SearchButton) b),
                Tooltip.create(Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_SEARCH)))
                .withFrameOverlap(frameOverlap);
        this.addRenderableWidget(searchButton);

        searchButtonY -= buttonHeight + 2;

        var showBookmarksButton = new ShowBookmarksButton(this, searchButtonX, searchButtonY,
                scissorX,
                searchButtonWidth, buttonHeight,
                (b) -> this.onShowBookmarksButtonClick((ShowBookmarksButton) b),
                Tooltip.create(Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_BOOKMARKS)))
                .withFrameOverlap(frameOverlap);
        this.addRenderableWidget(showBookmarksButton);

        if (this.book.showRecentlyUnlocked()) {
            searchButtonY -= buttonHeight + 2;

            var showRecentlyUnlockedButton = new ShowRecentlyUnlockedButton(this, searchButtonX, searchButtonY,
                    scissorX,
                    searchButtonWidth, buttonHeight,
                    (b) -> this.onShowRecentlyUnlockedButtonClick((ShowRecentlyUnlockedButton) b),
                    Tooltip.create(Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_RECENTLY_UNLOCKED)))
                    .withFrameOverlap(frameOverlap);
            this.addRenderableWidget(showRecentlyUnlockedButton);
        }

        int readAllButtonY = buttonY;
        var researchProgressButton = new ResearchProgressButton(this, rightButtonX, readAllButtonY, scissorX,
                this::hasVisibleResearchProgress,
                this::hasAnyResearchProgress,
                () -> {},
                () -> {})
                .withFrameOverlap(frameOverlap);

        this.addRenderableWidget(researchProgressButton);
    }

    protected void updateCategoryButtons(int buttonX, int buttonY, int buttonWidth, int buttonHeight, int buttonSpacing) {
        var displayable = this.getDisplayableCategories();
        this.categoryButtonCapacity = this.calculateCategoryButtonCapacity(buttonY, buttonHeight, buttonSpacing);
        this.categoriesOverflow = displayable.size() > this.categoryButtonCapacity;
        this.categoryButtonRenderOffset = Mth.clamp(this.categoryButtonRenderOffset, 0, Math.max(0, displayable.size() - this.categoryButtonCapacity));

        int buttonCount = 0;
        for (int i = this.categoryButtonRenderOffset, size = displayable.size(); i < size; i++) {
            if (buttonCount >= this.categoryButtonCapacity) {
                break; // Stop adding buttons once we reach the capacity
            }
            var category = displayable.get(i);
            var button = new CategoryButton(this, category,
                    buttonX, buttonY + (buttonHeight + buttonSpacing) * buttonCount, buttonWidth, buttonHeight,
                    (b) -> this.onBookCategoryButtonClick((CategoryButton) b),
                    Tooltip.create(Component.translatable(category.getName())));

            this.addRenderableWidget(button);
            buttonCount++;
        }

        buttonX += 8;
        // Add the top scroll button if there are categories before the current offset
        if (this.categoryButtonRenderOffset > 0) {
            int topScrollButtonY = (this.height - this.getFrameHeight()) / 2 - 5;

            var topScrollButton = new CategoryScrollButton(this, buttonX, topScrollButtonY, false,
                    () -> this.categoryButtonRenderOffset > 0,
                    (b) -> this.scrollCategoryButtons(-1));
            this.addRenderableWidget(topScrollButton);
        }


        // Add the bottom scroll button if there are more categories than can be displayed
        if (this.categoriesOverflow) {
            var bottomScrollButton = new CategoryScrollButton(this, buttonX, this.getBottomScrollButtonY(), true,
                    () -> displayable.size() > this.categoryButtonRenderOffset + this.categoryButtonCapacity,
                    (b) -> this.scrollCategoryButtons(1));
            this.addRenderableWidget(bottomScrollButton);
        }
    }

    /**
     * Gets how many category buttons fit between the top of the category list and the bottom scroll button.
     */
    protected int calculateCategoryButtonCapacity(int buttonY, int buttonHeight, int buttonSpacing) {
        int fit = (this.getBottomScrollButtonY() - buttonY) / (buttonHeight + buttonSpacing);
        return Mth.clamp(fit, 1, MAX_CATEGORY_BUTTONS);
    }

    protected int getBottomScrollButtonY() {
        return this.getFrameHeight() + this.getFrameThicknessH() - ResearchProgressButton.HEIGHT / 2 - 3;
    }

    /**
     * Scrolls the category buttons by the given number of categories, clamped to the displayable range.
     */
    protected void scrollCategoryButtons(int delta) {
        int maxOffset = Math.max(0, this.getDisplayableCategories().size() - this.categoryButtonCapacity);
        int offset = Mth.clamp(this.categoryButtonRenderOffset + delta, 0, maxOffset);
        if (offset != this.categoryButtonRenderOffset) {
            this.categoryButtonRenderOffset = offset;
            this.rebuildWidgets();
        }
    }

    /**
     * Categories to show navigation buttons for: all categories with a category button,
     * minus locked categories that are configured as hidden.
     */
    protected List<BookCategory> getDisplayableCategories() {
        var player = this.minecraft == null ? null : this.minecraft.player;
        return this.categories.stream()
                .filter(BookCategory::showCategoryButton)
                .filter(c -> player == null
                        || BookServices.visibility().isVisible(player, c)
                        || c.getEffectiveLockedCategoryDisplay() == BookLockedCategoryDisplay.SHOW_AS_LOCKED)
                .toList();
    }

    /**
     * Re-evaluates category visibility against the newly synced research state,
     * so categories gated on just-unlocked (or just-relocked) conditions swap
     * without closing and reopening the book.
     */
    public void onSyncResearchStateMessage(SyncResearchStateMessage message) {
        this.rebuildWidgets();
        this.updateUnreadEntriesState();
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
        return ClientServices.CLIENT_CONFIG.pauseGameWhenOpen();
    }
}

