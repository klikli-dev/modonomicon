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
import com.klikli_dev.modonomicon.data.book.Book;
import com.klikli_dev.modonomicon.data.book.BookCategory;
import com.klikli_dev.modonomicon.data.book.BookEntry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmlclient.gui.GuiUtils;

import javax.annotation.Nullable;
import java.util.List;

public class BookScreen extends Screen {

    protected static final ResourceLocation ENTRY_TEXTURES = Modonomicon.loc("textures/gui/entry_textures.png");

    protected ItemStack bookStack;
    protected Book book;
    protected ResourceLocation frameTexture;
    protected List<BookCategory> categories;
    protected int currentCategory = 0;

    //allow repeating textures -> in that case set a reasonable scroll max instead of calculating it for the texture
    protected int backgroundTextureWidth = 512;
    protected int backgroundTextureHeight = 512;
    protected double scrollX = 0;
    protected double scrollY = 0;
    protected boolean isScrolling;
    //TODO: make the frame thickness configurable in the book?
    protected int frameThicknessW = 14;
    protected int frameThicknessH = 14;

    public BookScreen(Book book, ItemStack bookStack) {
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

        //todo: get background texture width based on current tab
        //      Minecraft.getInstance().getTextureManager().getTexture(path), cast to simple texture, get native image, get height/width
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

    /**
     * Gets the render offset to draw the background texture centered within our frame
     */
    public float getCenterOffsetX() {
        return this.backgroundTextureWidth / 2.0f - this.getInnerWidth() / 2.0f;
    }

    /**
     * Gets the render offset to draw the background texture centered within our frame
     */
    public float getCenterOffsetY() {
        return this.backgroundTextureHeight / 2.0f - this.getInnerHeight() / 2.0f;
    }

    public void scroll(double pDragX, double pDragY) {
        //we need to limit the scroll amount to fit within our max texture size
        //to that end we just need the center offset, because that also acts as our positive/negative min/max
        //then we move our frame over the background texture and never hit a repeat
        //TODO: if repeating texture, allow higher value here, maybe configurable
        //TODO: move his into the book category screen
        float centerOffsetX = this.getCenterOffsetX();
        float centerOffsetY = this.getCenterOffsetY();

        this.scrollX = Mth.clamp(this.scrollX - pDragX, -centerOffsetX, centerOffsetX);
        this.scrollY = Mth.clamp(this.scrollY - pDragY, -centerOffsetY, centerOffsetY);
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

    protected void renderCategoryBackground(PoseStack poseStack) {
        //TODO: move his into the book category screen

        //TODO: zoom should not affect background, only entries
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.categories.get(this.currentCategory).getBackground());

        //based on the frame's total width and its thickness, calculate where the inner area starts
        int innerX = this.getInnerX();
        int innerY = this.getInnerY();

        //then calculate the corresponding inner area width/height so we don't draw out of the frame
        int innerWidth = this.getInnerWidth();
        int innerHeight = this.getInnerHeight();

        //now we need to calculate the offsets of our texture, based on how we scrolled in our book
        //starting point is the center
        float centerOffsetX = this.getCenterOffsetX();
        float centerOffsetY = this.getCenterOffsetY();

        //then apply scrolling
        float offsetX = centerOffsetX + (float) this.scrollX;
        float offsetY = centerOffsetY + (float) this.scrollY;

        //for some reason on this one blit overload tex width and height are switched. It does correctly call the followup though, so we have to go along
        //force offset to int here to reduce difference to entry rendering which is pos based and thus int precision only
        GuiComponent.blit(poseStack, innerX, innerY, this.getBlitOffset(), (int) offsetX, (int) offsetY, innerWidth, innerHeight,
                this.backgroundTextureHeight, this.backgroundTextureWidth);
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        return super.handleComponentClicked(pStyle);
    }

    protected void renderEntries(PoseStack stack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        //TODO: include zoom - do we need to do an overall scale before doing the entries? probably!
        //TODO: include scroll
        //calculate the render offset
        float zoom = 1.0f;
        float xOffset = ((this.getInnerWidth() / 2f) * (1 / zoom)) - ((float) this.scrollX);
        float yOffset = ((this.getInnerHeight() / 2f) * (1 / zoom)) - ((float) this.scrollY);
        //Slight parallax effect
        xOffset *= 0.8;
        yOffset *= 0.8;

        for (var entry : this.categories.get(this.currentCategory).getEntries().values()) {
            //render entry background
            int texX = 0; //select the entry background with this
            int texY = 0;

            RenderSystem.setShaderTexture(0, ENTRY_TEXTURES);

            //entries jitter when moving. this is due to background moving via uv = float based, but entries via pos = int
            //effect reduced by forcing background offset to int
            this.blit(stack, entry.getX() + (int)xOffset, entry.getY() + (int)yOffset, texX, texY, 26, 26);

            //render icon
            entry.getIcon().render(stack, entry.getX() + (int)xOffset + 5, entry.getY() + (int)yOffset + 5);
        }
    }

    protected void renderConnections(PoseStack stack, BookEntry entry, float xOffset, float yOffset){
        //our arrows are aliased and need blending
        RenderSystem.enableBlend();

        for(var parent : entry.getParents()){
            var parentEntry = parent.getEntry();

        }

        RenderSystem.disableBlend();
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

        //TODO: might need to gl scissors that
        this.renderEntries(pPoseStack);

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
