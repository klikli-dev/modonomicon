/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.entries.BookEntryTab;
import com.klikli_dev.modonomicon.book.entries.EntryTabSide;
import com.klikli_dev.modonomicon.client.gui.book.BookPaginatedScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.BookSideButtonRenderer;
import com.klikli_dev.modonomicon.client.gui.book.button.EntryTabButton;
import com.klikli_dev.modonomicon.client.gui.book.button.TabCloseButton;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the entry tabs of a {@link BookEntryScreen}: the side tab buttons, the open tab overlay and its close button.
 * The screen delegates tab state, visibility, clicks, keys and rendering of the overlay to this controller.
 */
public class EntryTabController {

    public static final int OVERLAY_PAGE_BACKGROUND_WIDTH = 145;

    private static final int TAB_TOP_OFFSET = 8;
    private static final int TAB_SPACING = 2;
    /**
     * Distance of the bottom of the right tab stack from the bottom of the book, above the search/bookmark button.
     */
    private static final int RIGHT_TABS_BOTTOM_OFFSET = 30;
    /**
     * Distance of the bottom of the left tab stack from the bottom of the book, above the page arrows in the bottom corners.
     */
    private static final int LEFT_TABS_BOTTOM_OFFSET = 10;
    /**
     * The close button sits in the top right corner of the overlay page, mirroring the exit button of the book.
     */
    private static final int CLOSE_BUTTON_INSET_X = 2;
    private static final int CLOSE_BUTTON_OFFSET_Y = -2;

    private final BookEntryScreen screen;
    private final List<EntryTabButton> tabButtons = new ArrayList<>();

    @Nullable
    private BookEntryTab openTab;
    @Nullable
    private BookPageRenderer<?> pageRenderer;

    public EntryTabController(BookEntryScreen screen) {
        this.screen = screen;
    }

    public Book getBook() {
        return this.screen.getBook();
    }

    public int getScreenHeight() {
        return this.screen.height;
    }

    @Nullable
    public BookEntryTab getOpenTab() {
        return this.openTab;
    }

    public boolean isOpen() {
        return this.openTab != null;
    }

    public boolean isOpen(BookEntryTab tab) {
        return this.openTab == tab;
    }

    /**
     * @return the renderer of the open tab page, or null if no tab is open.
     */
    @Nullable
    public BookPageRenderer<?> getPageRenderer() {
        return this.pageRenderer;
    }

    /**
     * The book-relative x coordinate of the tab overlay page.
     */
    public int getOverlayLeft() {
        return (BookPaginatedScreen.BOOK_BACKGROUND_WIDTH - OVERLAY_PAGE_BACKGROUND_WIDTH) / 2;
    }

    /**
     * Adds the close button of the tab overlay. Only shown and clickable while a tab is open.
     */
    public void addCloseButton() {
        if (!this.screen.supportsEntryTabs()) {
            return;
        }

        int closeX = this.screen.getBookLeft() + this.getOverlayLeft() + OVERLAY_PAGE_BACKGROUND_WIDTH - TabCloseButton.WIDTH + CLOSE_BUTTON_INSET_X;
        int closeY = this.screen.getBookTop() + CLOSE_BUTTON_OFFSET_Y;
        this.screen.addRenderableWidget(new TabCloseButton(this.screen, closeX, closeY, () -> this.isOpen(), button -> this.close()));
    }

    /**
     * Must be called after the screen cleared its widgets and built its display pages.
     */
    public void onInit() {
        if (this.openTab != null) {
            if (this.isVisible(this.openTab)) {
                this.attachPage(this.openTab);
            } else {
                this.openTab = null;
                this.pageRenderer = null;
            }
        }
        this.updateButtons();
    }

    /**
     * Re-evaluates the visibility of the open tab and the tab buttons. Call whenever the book visibility, the conditions or the open spread can change.
     */
    public void refresh() {
        if (this.openTab != null && !this.isVisible(this.openTab)) {
            this.close();
        }
        this.updateButtons();
    }

    /**
     * Rebuilds the tab buttons of both sides for the current visibility and open spread.
     */
    public void updateButtons() {
        this.screen.removeRenderableWidgets(this.tabButtons);
        this.tabButtons.clear();

        if (!this.screen.supportsEntryTabs()) {
            return;
        }

        var normal = this.getBook().theme().content().collectionButtonNormal();
        int bookLeft = this.screen.getBookLeft();
        int bookTop = this.screen.getBookTop();
        this.addTabButtons(EntryTabSide.RIGHT, normal, bookLeft + BookPaginatedScreen.FULL_WIDTH,
                bookTop + TAB_TOP_OFFSET, bookTop + BookPaginatedScreen.FULL_HEIGHT - RIGHT_TABS_BOTTOM_OFFSET);
        this.addTabButtons(EntryTabSide.LEFT, normal, bookLeft,
                bookTop + TAB_TOP_OFFSET, bookTop + BookPaginatedScreen.FULL_HEIGHT - LEFT_TABS_BOTTOM_OFFSET);
    }

    /**
     * Adds the buttons of the visible tabs on one side. Tabs are stacked from top to bottom within the given height budget.
     *
     * @param scissorX the book-edge the tabs slide out from
     * @param top      the top of the stack
     * @param bottom   the bottom limit of the stack
     */
    private void addTabButtons(EntryTabSide side, GuiSprite normal, int scissorX, int top, int bottom) {
        var tabs = this.screen.getEntry().getTabs().stream()
                .filter(tab -> tab.getSide() == side)
                .filter(this::isVisible)
                .toList();
        if (tabs.isEmpty()) {
            return;
        }

        int step = Math.min(normal.height() + TAB_SPACING, (bottom - top) / tabs.size());
        int buttonX = side == EntryTabSide.RIGHT
                ? BookSideButtonRenderer.anchoredButtonX(scissorX)
                : scissorX - normal.width() + BookSideButtonRenderer.BUTTON_SLIDE_OFFSET;

        for (int i = 0; i < tabs.size(); i++) {
            var tab = tabs.get(i);
            var button = new EntryTabButton(this, tab, buttonX, top + i * step, scissorX, normal.width(), normal.height(),
                    b -> this.open(tab), this.createTooltip(tab));
            this.tabButtons.add(button);
            this.screen.addRenderableWidget(button);
        }
    }

    @Nullable
    private Tooltip createTooltip(BookEntryTab tab) {
        if (tab.getTitle() != null) {
            return Tooltip.create(tab.getTitle());
        }
        var stack = tab.getPageIconStack();
        if (!stack.isEmpty()) {
            return Tooltip.create(stack.getHoverName());
        }
        return null;
    }

    /**
     * A tab is visible if its page is visible to the player, its condition is met and, if anchored, its page is on the open spread.
     */
    public boolean isVisible(BookEntryTab tab) {
        if (!tab.isAvailableFor(this.screen.getMinecraft().player, this.screen.getEntry())) {
            return false;
        }
        return tab.getAnchor() == null || this.screen.isPageOnOpenSpread(tab.getAnchor());
    }

    /**
     * Shows the page of the given tab as an overlay over the book. Opening the open tab again closes the overlay.
     */
    public void open(BookEntryTab tab) {
        if (this.openTab == tab) {
            this.close();
            return;
        }
        if (!this.isVisible(tab)) {
            return;
        }

        this.endPage();
        this.openTab = tab;
        this.attachPage(tab);
        this.screen.beginDisplayPages();
    }

    /**
     * Closes the tab overlay, if open. Only the overlay is closed, the entry stays open.
     */
    public void close() {
        if (this.openTab == null) {
            return;
        }

        this.endPage();
        this.openTab = null;
        this.screen.beginDisplayPages();
    }

    private void attachPage(BookEntryTab tab) {
        this.pageRenderer = PageRendererRegistry.getPageRenderer(tab.getPage().getType()).create(tab.getPage());
        //the overlay is a single page without continuation fragments, so split pages scale their text to fit instead
        this.pageRenderer.setPageSplitAllowed(false);
        this.pageRenderer.onBeginDisplayPage(this.screen, this.getOverlayLeft() + BookEntryScreen.SINGLE_PAGE_X, BookEntryScreen.TOP_PADDING);
        //the overlay keeps the space the page reserved for its own block, e.g. a stage requirements page reserves its block.
        //The entry progression block is only shown on the first page, so the overlay never reserves space for it.
    }

    private void endPage() {
        if (this.pageRenderer != null) {
            this.pageRenderer.onEndDisplayPage(this.screen);
            this.pageRenderer = null;
        }
    }

    /**
     * Handles clicks while a tab overlay is open. Clicks on the overlay page are left to the screen, any other click closes the overlay.
     *
     * @return true if the click was consumed.
     */
    public boolean handleMouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (this.openTab == null) {
            return false;
        }

        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && !this.screen.canLookupTooltipStack()) {
            //let the tab page handle non-item ingredients (e.g. fluids) before right-click closes the overlay
            if (this.isInOverlayPage(event.x(), event.y()) && this.screen.mouseClickedPageIngredient(event, isDoubleClick)) {
                return true;
            }
            this.close();
            return true;
        }

        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && !this.isInOverlayPage(event.x(), event.y())) {
            //widgets (e.g. other tabs, the close button) keep working.
            //Any other click only closes the overlay and is consumed, so it does not also close the entry or book.
            if (this.clickWidgets(event, isDoubleClick)) {
                return true;
            }
            this.close();
            return true;
        }

        return false;
    }

    private boolean clickWidgets(MouseButtonEvent event, boolean isDoubleClick) {
        for (GuiEventListener child : this.screen.children()) {
            if (child.mouseClicked(event, isDoubleClick)) {
                this.screen.setFocused(child);
                return true;
            }
        }
        return false;
    }

    /**
     * Closes the overlay on escape or backspace.
     *
     * @return true if the key was consumed.
     */
    public boolean handleKeyPressed(KeyEvent event) {
        if (this.openTab != null && (event.key() == GLFW.GLFW_KEY_ESCAPE || event.key() == GLFW.GLFW_KEY_BACKSPACE)) {
            this.close();
            return true;
        }
        return false;
    }

    public boolean isInOverlayPage(double mouseX, double mouseY) {
        int left = this.screen.getBookLeft() + this.getOverlayLeft();
        int top = this.screen.getBookTop();
        return mouseX >= left && mouseX < left + OVERLAY_PAGE_BACKGROUND_WIDTH
                && mouseY >= top && mouseY < top + BookPaginatedScreen.BOOK_BACKGROUND_HEIGHT;
    }

    /**
     * Dims the book and draws the open tab page as a single page centered over it. Call with the book origin as translation.
     */
    public void renderOverlay(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.pageRenderer == null) {
            return;
        }

        var content = this.getBook().theme().content();
        int overlayLeft = this.getOverlayLeft();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.screen.getBookLeft(), this.screen.getBookTop());
        int dimColor = this.getBook().theme().palette().tabOverlayDimColor();
        if (dimColor >>> 24 != 0) {
            guiGraphics.fill(0, 0, BookPaginatedScreen.BOOK_BACKGROUND_WIDTH, BookPaginatedScreen.BOOK_BACKGROUND_HEIGHT, dimColor);
        }
        content.tabPageBackground().extractRenderState(guiGraphics, overlayLeft, 0,
                OVERLAY_PAGE_BACKGROUND_WIDTH, BookPaginatedScreen.BOOK_BACKGROUND_HEIGHT);
        this.screen.renderPage(guiGraphics, this.pageRenderer, mouseX, mouseY, partialTick);
        guiGraphics.pose().popMatrix();
        guiGraphics.nextStratum();
    }
}
