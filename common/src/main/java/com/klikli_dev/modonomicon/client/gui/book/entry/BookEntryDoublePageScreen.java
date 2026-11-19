// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.client.render.page.BookSplitContinuationRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookEntryDoublePageScreen extends BookEntryScreen {

    /**
     * Mouse position used for underlying pages while the tab overlay is open, so they do not react to hover.
     */
    private static final int MOUSE_OUT_OF_BOOK = -10000;

    private DisplayPage leftDisplay;
    private DisplayPage rightDisplay;

    private BookPageRenderer<?> leftPageRenderer;
    private BookPageRenderer<?> rightPageRenderer;

    public BookEntryDoublePageScreen(BookParentScreen parentScreen, BookContentEntry entry) {
        super(parentScreen, entry);
    }

    @Override
    protected int getOpenPagesIndexForPage(int pageIndex) {
        for (var i = 0; i < this.displayPages.size(); i++) {
            var display = this.displayPages.get(i);
            if (!display.isContinuation() && display.page().getPageNumber() == pageIndex) {
                return i & -2; // Rounds down to even
            }
            if (display.page().getPageNumber() > pageIndex) {
                // pageNumber will only increase, so we can stop here
                break;
            }
        }
        return 0;
    }

    @Override
    protected int getPageForOpenPagesIndex(int openPagesIndex) {
        if (!this.displayPages.isEmpty() && openPagesIndex >= 0 && openPagesIndex < this.displayPages.size()) {
            return this.getAuthoredPageNumber(this.displayPages.get(openPagesIndex));
        }
        return openPagesIndex / PAGES_PER_SPREAD;
    }

    @Override
    public boolean canSeeArrowButton(boolean left) {
        if (this.entryTabs.isOpen()) {
            return false;
        }
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + PAGES_PER_SPREAD) < this.displayPages.size();
    }

    @Override
    protected void flipPage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {
            if (left) {
                this.openPagesIndex -= PAGES_PER_SPREAD;
            } else {
                this.openPagesIndex += PAGES_PER_SPREAD;
            }

            this.onPageChanged();
            if (playSound) {
                BookContentRenderer.playTurnPageSound(this.getBook());
            }
        }
    }

    @Override
    protected boolean supportsEntryTabs() {
        return true;
    }

    @Override
    @Nullable
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (this.entryTabs.isOpen()) {
            return this.getClickedComponentStyleAtForPage(this.entryTabs.getPageRenderer(), pMouseX, pMouseY);
        }

        var leftPageClickedStyle = this.getClickedComponentStyleAtForPage(this.leftPageRenderer, pMouseX, pMouseY);
        if (leftPageClickedStyle != null) {
            return leftPageClickedStyle;
        }
        return this.getClickedComponentStyleAtForPage(this.rightPageRenderer, pMouseX, pMouseY);
    }

    @Override
    protected boolean mouseClickedPage(MouseButtonEvent event, boolean isDoubleClick) {
        if (this.entryTabs.isOpen()) {
            return this.clickPage(this.entryTabs.getPageRenderer(), event, isDoubleClick);
        }

        return this.clickPage(this.leftPageRenderer, event, isDoubleClick)
                || this.clickPage(this.rightPageRenderer, event, isDoubleClick);
    }

    @Override
    protected boolean mouseClickedPageIngredient(MouseButtonEvent event, boolean isDoubleClick) {
        if (this.entryTabs.isOpen()) {
            return this.clickIngredientPage(this.entryTabs.getPageRenderer(), event);
        }

        return this.clickIngredientPage(this.leftPageRenderer, event)
                || this.clickIngredientPage(this.rightPageRenderer, event);
    }

    protected void beginDisplayPages() {
        //allow pages to clean up
        if (this.leftPageRenderer != null) {
            this.leftPageRenderer.onEndDisplayPage(this);
        }
        if (this.rightPageRenderer != null) {
            this.rightPageRenderer.onEndDisplayPage(this);
        }

        //get new pages
        int leftPageIndex = this.openPagesIndex;
        int rightPageIndex = leftPageIndex + 1;

        this.leftDisplay = leftPageIndex < this.displayPages.size() ? this.displayPages.get(leftPageIndex) : null;
        this.rightDisplay = rightPageIndex < this.displayPages.size() ? this.displayPages.get(rightPageIndex) : null;

        //allow pages to prepare for being displayed
        if (this.leftDisplay != null) {
            this.leftPageRenderer = this.createDisplayRenderer(this.leftDisplay);
            this.leftPageRenderer.setReservedBottomHeight(leftPageIndex == 0 ? this.requirements.getReservedFirstPageHeight() : 0);
            this.leftPageRenderer.onBeginDisplayPage(this, LEFT_PAGE_X, TOP_PADDING);
        } else {
            this.leftPageRenderer = null;
        }
        if (this.rightDisplay != null) {
            this.rightPageRenderer = this.createDisplayRenderer(this.rightDisplay);
            this.rightPageRenderer.onBeginDisplayPage(this, RIGHT_PAGE_X, TOP_PADDING);
        } else {
            this.rightPageRenderer = null;
        }

        //while the tab overlay is open the underlying pages must not keep their widgets
        if (this.entryTabs.isOpen()) {
            if (this.leftPageRenderer != null) {
                this.leftPageRenderer.onEndDisplayPage(this);
            }
            if (this.rightPageRenderer != null) {
                this.rightPageRenderer.onEndDisplayPage(this);
            }
        }
    }

    private BookPageRenderer<?> createDisplayRenderer(DisplayPage display) {
        if (display.isContinuation()) {
            return new BookSplitContinuationRenderer(display.page(), display.fragmentLines());
        }
        return PageRendererRegistry.getPageRenderer(display.page().getType()).create(display.page());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.resetTooltip();
        this.requirements.beginFrame();

        boolean overlayOpen = this.entryTabs.isOpen();
        int underlyingMouseX = overlayOpen ? MOUSE_OUT_OF_BOOK : pMouseX;
        int underlyingMouseY = overlayOpen ? MOUSE_OUT_OF_BOOK : pMouseY;

        //TODO we had -1000z here //push the page background to the front. Otherwise the entries render over it.
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);
        BookContentRenderer.renderBookBackground(guiGraphics, this.getBook());
        guiGraphics.pose().popMatrix();
        guiGraphics.nextStratum();

        guiGraphics.pose().pushMatrix();
        //TODO we had -1000z here //push the page background to the front. Otherwise the entries render over it.
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);

        this.renderPage(guiGraphics, this.leftPageRenderer, underlyingMouseX, underlyingMouseY, pPartialTick);
        this.renderPage(guiGraphics, this.rightPageRenderer, underlyingMouseX, underlyingMouseY, pPartialTick);
        this.requirements.renderFirstPage(guiGraphics, underlyingMouseX, underlyingMouseY, !overlayOpen);
        guiGraphics.pose().popMatrix();
        guiGraphics.nextStratum();

        if (overlayOpen) {
            this.entryTabs.renderOverlay(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //do not translate tooltip, would mess up location
        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }
}
