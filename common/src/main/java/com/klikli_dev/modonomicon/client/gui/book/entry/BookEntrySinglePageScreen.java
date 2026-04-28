// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.ArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.button.BackButton;
import com.klikli_dev.modonomicon.client.gui.book.button.ExitButton;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookEntrySinglePageScreen extends BookEntryScreen {

    public static final int SINGLE_PAGE_BOOK_BACKGROUND_WIDTH = 145;
    public static final int SINGLE_PAGE_BOOK_BACKGROUND_HEIGHT = 178;
    private BookPage page;
    private BookPageRenderer<?> pageRenderer;

    public BookEntrySinglePageScreen(BookParentScreen parentScreen, BookContentEntry entry) {
        super(parentScreen, entry);
    }

    public static void renderSinglePageBookBackground(GuiGraphicsExtractor guiGraphics, com.klikli_dev.modonomicon.book.Book book) {
        int x = 0; // (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        int y = 0; // (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        BookContentRenderer.drawSprite(guiGraphics, book.theme().content().singlePageBackground(), x, y);
    }

    @Override
    protected void initNavigationButtons() {
        this.bookLeft = (this.width - SINGLE_PAGE_BOOK_BACKGROUND_WIDTH) / 2;
        this.bookTop = (this.height - SINGLE_PAGE_BOOK_BACKGROUND_HEIGHT) / 2;

        //paginated screen assumes a double page layout, so we have to override the init here and place our buttons as we like.

        //TODO: this can probably be optimized to instead hand over the height/width stuff to the parent screen
        //      maybe with configurable offsets
        this.addRenderableWidget(new ArrowButton(this, this.bookLeft - 4, this.bookTop + SINGLE_PAGE_BOOK_BACKGROUND_HEIGHT - 6, true, () -> this.canSeeArrowButton(true), this::handleArrowButton));
        this.addRenderableWidget(new ArrowButton(this, this.bookLeft + SINGLE_PAGE_BOOK_BACKGROUND_WIDTH - 14, this.bookTop + SINGLE_PAGE_BOOK_BACKGROUND_HEIGHT - 6, false, () -> this.canSeeArrowButton(false), this::handleArrowButton));
        if (this.addExitButton) {
            this.addRenderableWidget(new ExitButton(this, this.bookLeft + SINGLE_PAGE_BOOK_BACKGROUND_WIDTH - 10, this.bookTop - 2, this::handleExitButton));
        }
        this.addRenderableWidget(new BackButton(this, this.width / 2 - BackButton.WIDTH / 2, this.bookTop + SINGLE_PAGE_BOOK_BACKGROUND_HEIGHT - BackButton.HEIGHT / 2));

//        this.updateBookmarksButton(); //no bookmarks on leaflets!
    }

    @Override
    protected int getOpenPagesIndexForPage(int pageIndex) {
        return pageIndex; //for single page screens the index is equivalent to the page number
    }

    @Override
    protected int getPageForOpenPagesIndex(int openPagesIndex) {
        return openPagesIndex;
    }

    @Override
    public boolean canSeeArrowButton(boolean left) {
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + 1) < this.unlockedPages.size();
    }

    @Override
    protected void flipPage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {
            if (left) {
                this.openPagesIndex -= 1;
            } else {
                this.openPagesIndex += 1;
            }

            this.onPageChanged();
            if (playSound) {
                BookContentRenderer.playTurnPageSound(this.getBook());
            }
        }
    }

    @Override
    @Nullable
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        return this.getClickedComponentStyleAtForPage(this.pageRenderer, pMouseX, pMouseY);
    }

    @Override
    public boolean mouseClickedPage(MouseButtonEvent event, boolean isDoubleClick) {
        return this.clickPage(this.pageRenderer, event, isDoubleClick);
    }

    protected void beginDisplayPages() {
        //allow pages to clean up
        if (this.pageRenderer != null) {
            this.pageRenderer.onEndDisplayPage(this);
        }

        //get new pages
        int pageIndex = this.openPagesIndex;

        this.page = pageIndex < this.unlockedPages.size() ? this.unlockedPages.get(pageIndex) : null;

        //allow pages to prepare for being displayed
        if (this.page != null) {
            this.pageRenderer = PageRendererRegistry.getPageRenderer(this.page.getType()).create(this.page);
            this.pageRenderer.onBeginDisplayPage(this, SINGLE_PAGE_X, TOP_PADDING);
        } else {
            this.pageRenderer = null;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.resetTooltip();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);
        renderSinglePageBookBackground(guiGraphics, this.getBook());
        guiGraphics.pose().popMatrix();
        guiGraphics.nextStratum();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop);
        this.renderPage(guiGraphics, this.pageRenderer, pMouseX, pMouseY, pPartialTick);
        guiGraphics.pose().popMatrix();
        guiGraphics.nextStratum();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.extractRenderState(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //do not translate tooltip, would mess up location
        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }
}
