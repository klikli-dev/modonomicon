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

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.ScreenHelper;
import com.klikli_dev.modonomicon.client.gui.book.button.ArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.button.ExitButton;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.registry.SoundRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ClickAction;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;

public class BookContentScreen extends Screen {

    public static final int BOOK_BACKGROUND_WIDTH = 272;
    public static final int BOOK_BACKGROUND_HEIGHT = 178;

    public static final int TOP_PADDING = 18;
    public static final int LEFT_PAGE_X = 15;
    public static final int RIGHT_PAGE_X = 141;
    public static final int PAGE_WIDTH = 116;
    public static final int PAGE_HEIGHT = 128; //TODO: Adjust to what is real
    public static final int FULL_WIDTH = 272;
    public static final int FULL_HEIGHT = 180;
    private static long lastTurnPageSoundTime;
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

    private List<Component> tooltip;

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
        int rx = x - w / 2;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 0.8F);
        //u and v are the pixel coordinates in our book_content_texture
        drawFromTexture(poseStack, book, rx, y, 0, 253, w, h);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public static void playTurnPageSound(Book book) {
        if (ClientTicks.ticks - lastTurnPageSoundTime > 6) {
            //TODO: make mod loader agnostic
            var sound = ForgeRegistries.SOUND_EVENTS.getHolder(book.getTurnPageSound()).orElse(Holder.direct(SoundRegistry.TURN_PAGE.get()));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), (float) (0.7 + Math.random() * 0.3)));
            lastTurnPageSoundTime = ClientTicks.ticks;
        }
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
     * Needs to use Button instead of ArrowButton to conform to Button.OnPress otherwise we can't use it as method
     * reference, which we need - lambda can't use this in super constructor call.
     */
    public void handleArrowButton(Button button) {
        this.changePage(((ArrowButton) button).left, true);
    }

    public void handleExitButton(Button button) {
        this.onClose();
    }

    public void setTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
    }

    public void changePage(int pageIndex, boolean playSound) {
        int openPagesIndex = pageIndex / 2; //will floor, which is what we want
        if (openPagesIndex >= 0 && openPagesIndex < this.maxOpenPagesIndex) {
            this.openPagesIndex = openPagesIndex;

            this.onPageChanged();
            if (playSound) {
                playTurnPageSound(this.getBook());
            }
        } else {
            Modonomicon.LOGGER.warn("Tried to change to page index {pageIndex} corresponding with " +
                    "openPagesIndex {openPagesIndex} but max open pages index is ", pageIndex, openPagesIndex, this.maxOpenPagesIndex);
        }
    }

    protected void drawTooltip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (this.tooltip != null) {
            this.renderComponentTooltip(pPoseStack, this.tooltip, pMouseX, pMouseY);
        }
    }

    protected boolean clickPage(BookPage page, double mouseX, double mouseY, int mouseButton) {
        if (page != null) {
            return page.mouseClicked(mouseX - this.bookLeft - page.left, mouseY - this.bookTop - page.top, mouseButton);
        }

        return false;
    }

    protected void renderPage(PoseStack poseStack, BookPage page, int pMouseX, int pMouseY, float pPartialTick) {
        if (page == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(page.left, page.top, 0);
        page.render(poseStack, pMouseX - page.left, pMouseY - page.top, pPartialTick);
        poseStack.popPose();
    }

    protected void beginDisplayPages() {
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

    protected void changePage(boolean left, boolean playSound) {
        if (this.canSeeArrowButton(left)) {
            if (left) {
                this.openPagesIndex--;
            } else {
                this.openPagesIndex++;
            }

            this.onPageChanged();
            if (playSound) {
                playTurnPageSound(this.getBook());
            }
        }
    }

    protected void onPageChanged() {
        this.beginDisplayPages();
    }

    protected void resetTooltip() {
        this.tooltip = null;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.resetTooltip();

        this.renderBackground(pPoseStack);

        pPoseStack.pushPose();
        pPoseStack.translate(this.bookLeft, this.bookTop, 0);
        this.renderBookBackground(pPoseStack);
        pPoseStack.popPose();

        //do not translate super (= widget rendering) -> otherwise our buttons are messed up
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        pPoseStack.pushPose();
        pPoseStack.translate(this.bookLeft, this.bookTop, 0);
        this.renderPage(pPoseStack, this.leftPage, pMouseX, pMouseY, pPartialTick);
        this.renderPage(pPoseStack, this.rightPage, pMouseX, pMouseY, pPartialTick);
        pPoseStack.popPose();

        //do not translate tooltip, would mess up location
        this.drawTooltip(pPoseStack, pMouseX, pMouseY);
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

        this.addRenderableWidget(new ArrowButton(this, this.bookLeft - 4, this.bookTop + FULL_HEIGHT - 6, true));
        this.addRenderableWidget(new ArrowButton(this, this.bookLeft + FULL_WIDTH - 14, this.bookTop + FULL_HEIGHT - 6, false));
        this.addRenderableWidget(new ExitButton(this, this.bookLeft + FULL_WIDTH - 10, this.bookTop - 2));
    }


    public Style getClickedComponentStyleAtForPage(BookPage page, double pMouseX, double pMouseY) {
        if (page != null) {
            return page.getClickedComponentStyleAt(pMouseX - this.bookLeft - page.left, pMouseY - this.bookTop - page.top);
        }

        return null;
    }

    @Nullable
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        var leftPageClickedStyle = this.getClickedComponentStyleAtForPage(this.leftPage, pMouseX, pMouseY);
        if (leftPageClickedStyle != null) {
            return leftPageClickedStyle;
        }
        var rightPageClickedStyle = this.getClickedComponentStyleAtForPage(this.rightPage, pMouseX, pMouseY);
        if (rightPageClickedStyle != null) {
            return rightPageClickedStyle;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT){
            var style = this.getClickedComponentStyleAt(pMouseX, pMouseY);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }

        return this.clickPage(this.leftPage, pMouseX, pMouseY, pButton)
                || this.clickPage(this.rightPage, pMouseX, pMouseY, pButton)
                || super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        if(pStyle != null){
            var event = pStyle.getClickEvent();
            if(event != null){
                if(event.getAction() == Action.CHANGE_PAGE){
                    //TODO: Parse link here
                    ScreenHelper.openEntry(this.parentScreen.getBookStack(), this.getBook().getId(),
                            new ResourceLocation("modonomicon:test_category/test_entry"), 3);
                }
            }
        }
        return super.handleComponentClicked(pStyle);
    }
}
