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
import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.client.gui.book.button.CategoryButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.GuiUtils;

import javax.annotation.Nullable;
import java.util.List;

public class BookOverviewScreen extends Screen {

    private final ItemStack bookStack;
    private final Book book;
    private final ResourceLocation bookOverviewTexture;
    private final EntryConnectionRenderer connectionRenderer = new EntryConnectionRenderer();
    private final List<BookCategory> categories;
    private final List<BookCategoryScreen> categoryScreens;
    //TODO: make the frame thickness configurable in the book?
    private final int frameThicknessW = 14;
    private final int frameThicknessH = 14;
    private int currentCategory = 0;
    public BookOverviewScreen(Book book, ItemStack bookStack) {
        super(new TextComponent(""));

        //somehow there are render calls before init(), leaving minecraft null
        this.minecraft = Minecraft.getInstance();

        this.bookStack = bookStack;
        this.book = book;

        this.bookOverviewTexture = book.getBookOverviewTexture();

        //TODO: only get unlocked categories / or at least only render those
        this.categories = book.getCategoriesSorted();
        this.categoryScreens = this.categories.stream().map(c -> new BookCategoryScreen(this, c)).toList();

        //TODO: save/load current category and page to capability
    }

    public ItemStack getBookStack() {
        return this.bookStack;
    }

    public EntryConnectionRenderer getConnectionRenderer() {
        return this.connectionRenderer;
    }

    public BookCategoryScreen getCurrentCategoryScreen() {
        return this.categoryScreens.get(this.currentCategory);
    }

    public int getCurrentCategory() {
        return this.currentCategory;
    }

    public Book getBook() {
        return this.book;
    }

    public ResourceLocation getBookOverviewTexture() {
        return this.bookOverviewTexture;
    }

    /**
     * gets the width of the inner area of the book frame
     */
    public int getInnerX() {
        return (this.width - this.getFrameWidth()) / 2 + this.frameThicknessW / 2;
    }

    /**
     * gets the height of the inner area of the book frame
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

    public void changeCategory(BookCategory category) {
        int index = this.categories.indexOf(category);
        if (index != -1) {
            this.changeCategory(index);
        } else {
            Modonomicon.LOGGER.warn("Tried to change to a category ({}) that does not exist in this book ({}).", this.book.getId(), category.getId());
        }
    }

    public void changeCategory(int categoryIndex) {
        this.currentCategory = categoryIndex;
        this.onCategoryChanged();
    }

    public void onCategoryChanged() {
        //TODO: SFX for category change?
    }

    /**
     * Gets the outer width of the book frame
     */
    protected int getFrameWidth() {
        //TODO: enable config frame width
        return this.width - 60;
    }

    /**
     * Gets the outer height of the book frame
     */
    protected int getFrameHeight() {
        //TODO: enable config frame height
        return this.height - 20;
    }

    protected void renderFrame(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.bookOverviewTexture);

        int width = this.getFrameWidth();
        int height = this.getFrameHeight();
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        int blitOffset = this.getBlitOffset();

        //draw a resizeable border. Center parts of each side will be stretched
        //the exact border size mostly does not matter because the center is empty anyway, but 50 gives a lot of flexiblity
        GuiUtils.drawContinuousTexturedBox(poseStack, x, y, 0, 0, width, height,
                140, 140, 50, 50, 50, 50, blitOffset);

        //now draw center parts of each side. This allows to add non-repeat features here or just generally bridge the repeated pixesl
        //top, bottom, left, right
        //these parts are to the left of the repeatable frame in the texture.
        GuiUtils.drawTexturedModalRect(poseStack, (x + (width / 2)) - 36, y, 140, 0, 72, 17, blitOffset);
        GuiUtils.drawTexturedModalRect(poseStack, (x + (width / 2)) - 36, (y + height) - 18, 140, 17, 72, 18, blitOffset);
        GuiUtils.drawTexturedModalRect(poseStack, x, (y + (height / 2)) - 35, 140, 35, 17, 70, blitOffset);
        GuiUtils.drawTexturedModalRect(poseStack, x + width - 17, (y + (height / 2)) - 35, 157, 35, 17, 70, blitOffset);
    }

    protected void onBookCategoryButtonClick(CategoryButton button) {
        this.changeCategory(button.getCategoryIndex());
    }

    protected void onBookCategoryButtonTooltip(CategoryButton button, PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.renderTooltip(pPoseStack, new TranslatableComponent(button.getCategory().getName()), pMouseX, pMouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        //ignore return value, because we need our base class to handle dragging and such
        this.getCurrentCategoryScreen().mouseClicked(pMouseX, pMouseY, pButton);
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return this.getCurrentCategoryScreen().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        this.getCurrentCategoryScreen().zoom(pDelta);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);

        this.getCurrentCategoryScreen().renderBackground(pPoseStack);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.getCurrentCategoryScreen().render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.renderFrame(pPoseStack);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        //client side only
        if (!this.bookStack.isEmpty())
            this.bookStack.getOrCreateTag().putBoolean(ModonimiconConstants.Nbt.BOOK_OPEN, false);

        //TODO: Send packet to save category variables (zoom, etc)
        super.onClose();
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        return super.handleComponentClicked(pStyle);
    }

    @Override
    protected void init() {
        super.init();

        int buttonXOffset = -8;

        int buttonX = (this.width - this.getFrameWidth()) / 2 - this.getFrameThicknessW() + buttonXOffset;
        int buttonY = (this.height - this.getFrameHeight()) / 2 - this.getFrameThicknessH() + 30;
        //calculate button width so it aligns with the outer edge of the frame
        int buttonWidth = (this.width - this.getFrameWidth()) / 2 + buttonXOffset;
        int buttonHeight = 20;
        int buttonSpacing = 2;

        for (int i = 0, size = this.categories.size(); i < size; i++) {

            var button = new CategoryButton(this, this.categories.get(i), i,
                    buttonX, buttonY + (buttonHeight + buttonSpacing) * i, buttonWidth, buttonHeight,
                    (b) -> this.onBookCategoryButtonClick((CategoryButton) b),
                    (b, stack, x, y) -> this.onBookCategoryButtonTooltip((CategoryButton) b, stack, x, y));

            this.addRenderableWidget(button);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
