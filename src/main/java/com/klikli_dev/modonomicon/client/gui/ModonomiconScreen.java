/*
 * MIT License
 *
 * Copyright 2021 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.klikli_dev.modonomicon.client.gui;

import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.data.book.Book;
import com.klikli_dev.modonomicon.data.book.BookCategory;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmlclient.gui.GuiUtils;

import java.util.List;

public class ModonomiconScreen extends Screen {

    protected ItemStack bookStack;
    protected Book book;
    protected ResourceLocation frameTexture;
    protected List<BookCategory> categories;
    protected int currentCategory = 0;

    //scrolling based on AdvancementScreen/AdvancementTab
    //but we do not support repeating textures
    //TODO: Document we need 512 px
    protected int backgroundTextureWidth = 512;
    protected int backgroundTextureHeight = 512;
    protected double scrollX = 0;
    protected double scrollY = 0;
    protected boolean isScrolling;

    public ModonomiconScreen(Book book, ItemStack bookStack) {
        super(new TextComponent(""));

        //somehow there are render calls before init(), leaving minecraft null
        this.minecraft = Minecraft.getInstance();

        this.bookStack = bookStack;
        this.book = book;

        this.categories = book.getCategoriesSorted();
        this.frameTexture = book.getFrameTexture();
        //TODO: save/load current category and page.
        //TODO: save/load pan, zoom, etc -> needs to be per category
        //TODO: handle books with no categories? probably fine to just crash
    }

    public void scroll(double pDragX, double pDragY) {
        //we need to limit the scroll amount to fit within our max texture size
        //for that we need to account how our frame moves over the background texture
        //max coordinates for 512x512 texture are with our current frame size 159 306

        float centerOffsetX = this.backgroundTextureWidth / 2.0f - width / 2.0f;
        float centerOffsetY = this.backgroundTextureHeight / 2.0f - height / 2.0f;

        //then apply scrolling
        float offsetX = centerOffsetX + (float)this.scrollX;
        float offsetY = centerOffsetY + (float)this.scrollY;


        this.scrollX = Mth.clamp(this.scrollX - pDragX, -this.backgroundTextureWidth, 0.0D);
        this.scrollY = Mth.clamp(this.scrollY - pDragY, -this.backgroundTextureHeight, 0.0D);
    }

    protected void renderCategoryBackground(PoseStack poseStack) {

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.categories.get(this.currentCategory).getBackground());

        int frameThicknessW = 14;
        int frameThicknessH = 14;

        //based on the frame's total width and its thickness, calculate where the inner area starts
        int x = (this.width - this.getFrameWidth()) / 2 + frameThicknessW / 2;
        int y = (this.height - this.getFrameHeight()) / 2 + frameThicknessH / 2;

        //then calculate the corresponding width/height so we don't draw out of the frame
        int width = this.getFrameWidth() - frameThicknessW;
        int height = this.getFrameHeight() - frameThicknessH;

        //now we need to calculate the offsets of our texture, based on how we scrolled in our book
        //starting point is the center
        float centerOffsetX = this.backgroundTextureWidth / 2.0f - width / 2.0f;
        float centerOffsetY = this.backgroundTextureHeight / 2.0f - height / 2.0f;

        //then apply scrolling
        float offsetX = centerOffsetX + (float)this.scrollX;
        float offsetY = centerOffsetY + (float)this.scrollY;

        //for some reason on this one blit overload tex width and height are switched. It does correctly call the followup though, so we have to go along
        GuiComponent.blit(poseStack, x, y, this.getBlitOffset(), offsetX, offsetY, width, height, this.backgroundTextureHeight, this.backgroundTextureWidth);

    }

    protected void renderFrame(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.frameTexture);

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

    protected int getFrameWidth() {
        //TODO: enable config frame width
        return this.width - 60;
    }

    protected int getFrameHeight() {
        //TODO: enable config frame height
        return this.height - 20;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        //Based on advancementsscreen
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                this.scroll(pDragX, pDragY);
            }
            return true;
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.renderCategoryBackground(pPoseStack);
        this.renderFrame(pPoseStack);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        if (!this.bookStack.isEmpty())
            this.bookStack.getOrCreateTag().putBoolean(ModonimiconConstants.Nbt.BOOK_OPEN, false);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
