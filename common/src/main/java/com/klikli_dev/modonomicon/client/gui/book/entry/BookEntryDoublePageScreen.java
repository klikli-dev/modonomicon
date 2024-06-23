// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry;

import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookEntryDoublePageScreen extends BookEntryScreen {

    private BookPage leftPage;
    private BookPage rightPage;

    private BookPageRenderer<?> leftPageRenderer;
    private BookPageRenderer<?> rightPageRenderer;

    public BookEntryDoublePageScreen(BookParentScreen parentScreen, BookContentEntry entry) {
        super(parentScreen, entry);
    }

    @Override
    protected int getOpenPagesIndexForPage(int pageIndex) {
        for (var i = 0; i < this.unlockedPages.size(); i++) {
            var pageNumber = this.unlockedPages.get(i).getPageNumber();
            if (pageNumber == pageIndex) {
                return i & -2; // Rounds down to even
            }
            if (pageNumber > pageIndex) {
                // pageNumber will only increase, so we can stop here
                break;
            }
        }
        return 0;
    }

    @Override
    protected int getPageForOpenPagesIndex(int openPagesIndex) {
        return openPagesIndex / 2;
    }

    @Override
    public boolean canSeeArrowButton(boolean left) {
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + 2) < this.unlockedPages.size();
    }

    @Override
    protected void flipPage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {
            if (left) {
                this.openPagesIndex -= 2;
            } else {
                this.openPagesIndex += 2;
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
        var leftPageClickedStyle = this.getClickedComponentStyleAtForPage(this.leftPageRenderer, pMouseX, pMouseY);
        if (leftPageClickedStyle != null) {
            return leftPageClickedStyle;
        }
        return this.getClickedComponentStyleAtForPage(this.rightPageRenderer, pMouseX, pMouseY);
    }

    @Override
    protected boolean mouseClickedPage(double pMouseX, double pMouseY, int pButton) {
        return this.clickPage(this.leftPageRenderer, pMouseX, pMouseY, pButton)
                || this.clickPage(this.rightPageRenderer, pMouseX, pMouseY, pButton);
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

        this.leftPage = leftPageIndex < this.unlockedPages.size() ? this.unlockedPages.get(leftPageIndex) : null;
        this.rightPage = rightPageIndex < this.unlockedPages.size() ? this.unlockedPages.get(rightPageIndex) : null;

        //allow pages to prepare for being displayed
        if (this.leftPage != null) {
            this.leftPageRenderer = PageRendererRegistry.getPageRenderer(this.leftPage.getType()).create(this.leftPage);
            this.leftPageRenderer.onBeginDisplayPage(this, LEFT_PAGE_X, TOP_PADDING);
        } else {
            this.leftPageRenderer = null;
        }
        if (this.rightPage != null) {
            this.rightPageRenderer = PageRendererRegistry.getPageRenderer(this.rightPage.getType()).create(this.rightPage);
            this.rightPageRenderer.onBeginDisplayPage(this, RIGHT_PAGE_X, TOP_PADDING);
        } else {
            this.rightPageRenderer = null;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest(); //guard against depth test being enabled by other rendering code, that would cause ui elements to vanish

        this.resetTooltip();

        //we need to modify blit offset (now: z pose) to not draw over toasts
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -1300);  //magic number arrived by testing until toasts show, but BookOverviewScreen does not
        this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);
        BookContentRenderer.renderBookBackground(guiGraphics, this.bookContentTexture);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.bookLeft, this.bookTop, 0);
        this.renderPage(guiGraphics, this.leftPageRenderer, pMouseX, pMouseY, pPartialTick);
        this.renderPage(guiGraphics, this.rightPageRenderer, pMouseX, pMouseY, pPartialTick);
        guiGraphics.pose().popPose();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        //manually call the renderables like super does -> otherwise super renders the background again on top of our stuff
        for (var renderable : this.renderables) {
            renderable.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        //do not translate tooltip, would mess up location
        this.drawTooltip(guiGraphics, pMouseX, pMouseY);
    }


}
