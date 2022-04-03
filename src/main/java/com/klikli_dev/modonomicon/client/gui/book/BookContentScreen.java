/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.button.ArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;

public class BookContentScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;

    public static final int TOP_PADDING = 18;
    public static final int LEFT_PAGE_X = 15;
    public static final int RIGHT_PAGE_X = 141;
    public static final int PAGE_WIDTH = 116;

    private final BookOverviewScreen parentScreen;
    private final BookEntry entry;

    private final ResourceLocation bookContentTexture;
    private final BookTextRenderer textRenderer;

    private BookPage leftPage;
    private BookPage rightPage;

    private int bookLeft;
    private int bookTop;
    /**
     * The index of the two pages being displayed. 0 means Pages 0 and 1, 1 means Pages 2 and 3, etc.
     */
    private int openPagesIndex;
    private int maxOpenPagesIndex;

    public BookContentScreen(BookOverviewScreen parentScreen, BookEntry entry) {
        super(new TextComponent(""));

        this.minecraft = Minecraft.getInstance();

        this.parentScreen = parentScreen;
        this.entry = entry;

        this.bookContentTexture = this.parentScreen.getBook().getBookContentTexture();
        this.textRenderer = new BookTextRenderer();
    }

    public static void drawFromTexture(PoseStack poseStack, Book book, int x, int y, int u, int v, int w, int h) {
        RenderSystem.setShaderTexture(0, book.getBookContentTexture());
        blit(poseStack, x, y, u, v, w, h, 512, 256);
    }

    public static void drawTitleSeparator(PoseStack poseStack, Book book, int x, int y) {
        int w = 110;
        int h = 3;
        int rx = x + PAGE_WIDTH / 2 - w / 2;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 0.8F);
        //u and v are the pixel coordinates in our book_content_texture
        drawFromTexture(poseStack, book, rx, y, 0, 253, w, h);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public BookEntry getEntry() {
        return this.entry;
    }

    public Book getBook() {
        return this.entry.getBook();
    }

    public void renderBookBackground(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.bookContentTexture);

        int x = 0; // (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        int y = 0; // (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        GuiComponent.blit(poseStack, x, y, 0, 0, 272, 178, 512, 256);
    }

    public boolean canSeeArrowButton(boolean left) {
        return left ? this.openPagesIndex > 0 : (this.openPagesIndex + 1) < this.maxOpenPagesIndex;
    }

    /**
     * Needs to use Button instead of ArrowButton to conform to Button.OnPress
     * otherwise we can't use it as method reference, which we need - lambda can't use this in super constructor call.
     */
    public void handleArrowButton(Button button) {
        this.changePage(((ArrowButton)button).left, false);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);

        pPoseStack.pushPose();
        pPoseStack.translate(this.bookLeft, this.bookTop, 0);
        this.renderBookBackground(pPoseStack);
        pPoseStack.popPose();

        //TODO: should super (= widget rendering) also be translated?
        //  probably makes sense so we can keep coordinates local to the book
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        pPoseStack.pushPose();
        pPoseStack.translate(this.bookLeft, this.bookTop, 0);
        this.renderPage(pPoseStack, this.leftPage, pMouseX, pMouseY, pPartialTick);
        this.renderPage(pPoseStack, this.rightPage, pMouseX, pMouseY, pPartialTick);
        pPoseStack.popPose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        this.bookLeft = (this.width - BOOK_BACKGROUND_WIDTH) / 2;
        this.bookTop = (this.height - BOOK_BACKGROUND_HEIGHT) / 2;

        this.maxOpenPagesIndex = (int) Math.ceil((float) this.entry.getPages().size() / 2);
        this.beginDisplayPages();
    }

    void renderPage(PoseStack poseStack, BookPage page, int pMouseX, int pMouseY, float pPartialTick) {
        if (page == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(page.left, page.top, 0);
        page.render(poseStack, pMouseX - page.left, pMouseY - page.top, pPartialTick);
        poseStack.popPose();
    }

    void beginDisplayPages() {
        //allow pages to clean up
        if (this.leftPage != null) {
            this.leftPage.onEndDisplayPage(this);
        }
        if (this.rightPage != null) {
            this.rightPage.onEndDisplayPage(this);
        }

        //get new pages
        var pages = this.entry.getPages();
        int leftPageIndex = this.openPagesIndex * 2;
        int rightPageIndex = leftPageIndex + 1;

        this.leftPage = leftPageIndex < pages.size() ? pages.get(leftPageIndex) : null;
        this.rightPage = rightPageIndex < pages.size() ? pages.get(rightPageIndex) : null;

        //allow pages to prepare for being displayed
        if (this.leftPage != null) {
            this.leftPage.onBeginDisplayPage(this, this.textRenderer, LEFT_PAGE_X, TOP_PADDING);
        }
        if (this.rightPage != null) {
            this.rightPage.onBeginDisplayPage(this, this.textRenderer, RIGHT_PAGE_X, TOP_PADDING);
        }
    }

    void changePage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {
            if (left) {
                this.openPagesIndex--;
            } else {
                this.openPagesIndex++;
            }

            this.onPageChanged();
            if (playSound) {
                //TODO: play sound - prevent spamming by forcing a delay of e.g. 10 ticks
                //      sound (RL) should be taken from book
            }
        }
    }

    void onPageChanged() {
        this.beginDisplayPages();
    }
}
