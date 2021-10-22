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
import com.klikli_dev.modonomicon.config.ClientConfig;
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
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

public class BookScreen extends Screen {

    public static final int ENTRY_GRID_SCALE = 30;
    public static final int ENTRY_GAP = 2;
    public static final int MAX_SCROLL = 512;
    protected static final ResourceLocation ENTRY_TEXTURES = Modonomicon.loc("textures/gui/entry_textures.png");
    protected ItemStack bookStack;
    protected Book book;
    protected ResourceLocation frameTexture;
    protected List<BookCategory> categories;
    protected int currentCategory = 0;

    //allow repeating textures -> in that case set a reasonable scroll max instead of calculating it for the texture
    protected int backgroundTextureWidth = 512;
    protected int backgroundTextureHeight = 512;


    protected float scrollX = 0;
    protected float scrollY = 0;
    protected boolean isScrolling;

    protected float targetZoom;
    protected float currentZoom;

    //TODO: make the frame thickness configurable in the book?
    protected int frameThicknessW = 14;
    protected int frameThicknessH = 14;

    protected EntryConnectionRenderer connectionRenderer = new EntryConnectionRenderer(); //TODO: instantiate this once per category screen


    public BookScreen(Book book, ItemStack bookStack) {
        super(new TextComponent(""));

        //somehow there are render calls before init(), leaving minecraft null
        this.minecraft = Minecraft.getInstance();

        this.bookStack = bookStack;
        this.book = book;

        this.categories = book.getCategoriesSorted();
        this.frameTexture = book.getFrameTexture();
        //TODO: save/load current category and page.
        //TODO: save/load scroll, zoom, etc -> needs to be per category
        //TODO: handle books with no categories? probably fine to just crash

        //todo: get background texture width based on current tab
        //      Minecraft.getInstance().getTextureManager().getTexture(path), cast to simple texture, get native image, get height/width

        this.targetZoom = 0.7f;
        this.currentZoom = this.targetZoom;
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

    public void scroll(double pDragX, double pDragY) {
        //we need to limit the scroll amount to fit within our max texture size
        //to that end we just need the center offset, because that also acts as our positive/negative min/max
        //then we move our frame over the background texture and never hit a repeat
        //TODO: move his into the book category screen

        this.scrollX = (float) Mth.clamp(this.scrollX - pDragX, -MAX_SCROLL, MAX_SCROLL);
        this.scrollY = (float) Mth.clamp(this.scrollY - pDragY, -MAX_SCROLL, MAX_SCROLL);
    }

    public void zoom(double delta) {
        //todo: probably also needs to be in book category
        float step = 1.2f;
        if ((delta < 0 && this.targetZoom > 0.5) || (delta > 0 && this.targetZoom < 1))
            this.targetZoom *= delta > 0 ? step : 1 / step;
        if (this.targetZoom > 1f)
            this.targetZoom = 1f;
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

        //based on arcana's render research background
        //does not correctly work for non-automatic gui scale, but that only leads to background repeat -> no problem
        float xScale = MAX_SCROLL * 2.0f / ((float) MAX_SCROLL + this.frameThicknessW - this.getFrameWidth());
        float yScale = MAX_SCROLL * 2.0f / ((float) MAX_SCROLL + this.frameThicknessH - this.getFrameHeight());
        float scale = Math.max(xScale, yScale);
        float xOffset = xScale == scale ? 0 : (MAX_SCROLL - (innerWidth + MAX_SCROLL * 2.0f / scale)) / 2;
        float yOffset = yScale == scale ? 0 : (MAX_SCROLL - (innerHeight + MAX_SCROLL * 2.0f / scale)) / 2;

        //for some reason on this one blit overload tex width and height are switched. It does correctly call the followup though, so we have to go along
        //force offset to int here to reduce difference to entry rendering which is pos based and thus int precision only
        GuiComponent.blit(poseStack, innerX, innerY, this.getBlitOffset(),
                (this.scrollX + MAX_SCROLL) / scale + xOffset,
                (this.scrollY + MAX_SCROLL) / scale + yOffset,
                innerWidth, innerHeight, this.backgroundTextureHeight, this.backgroundTextureWidth);
    }

    protected void renderEntries(PoseStack stack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        //TODO: include zoom - do we need to do an overall scale before doing the entries? probably!
        //TODO: include scroll
        //calculate the render offset
        float xOffset = ((this.getInnerWidth() / 2f) * (1 / this.currentZoom)) - this.scrollX / 2;
        float yOffset = ((this.getInnerHeight() / 2f) * (1 / this.currentZoom)) - this.scrollY / 2;

        stack.pushPose();
        stack.scale(this.currentZoom, this.currentZoom, 1.0f);
        for (var entry : this.categories.get(this.currentCategory).getEntries().values()) {
            //render entry background
            int texX = 0; //select the entry background with this
            int texY = 0;

            RenderSystem.setShaderTexture(0, ENTRY_TEXTURES);

            //entries jitter when moving. this is due to background moving via uv = float based, but entries via pos = int
            //effect reduced by forcing background offset to int

            stack.pushPose();
            //we translate instead of applying the offset to the entry x/y to avoid jittering when moving
            stack.translate(xOffset, yOffset, 0);
            this.blit(stack, entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP, entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP, texX, texY, 26, 26);

            //render icon
            entry.getIcon().render(stack, entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP + 5, entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP + 5);
            stack.popPose();

            this.renderConnections(stack, entry, xOffset, yOffset);
        }
        stack.popPose();
    }

    protected void renderConnections(PoseStack stack, BookEntry entry, float xOffset, float yOffset) {
        //our arrows are aliased and need blending
        RenderSystem.enableBlend();

        for (var parent : entry.getParents()) {
            //TODO: possibly translate instead of offset
            this.connectionRenderer.setBlitOffset(this.getBlitOffset());
            //this.connectionRenderer.setOffset(xOffset, yOffset);
            this.connectionRenderer.setOffset(0, 0);
            stack.pushPose();
            stack.translate(xOffset, yOffset, 0);
            this.connectionRenderer.render(stack, entry, parent);
            stack.popPose();
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
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        this.zoom(pDelta);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        //TODO: if smooth zoom
        if (ClientConfig.get().qolCategory.enableSmoothZoom.get()) {
            float diff = this.targetZoom - this.currentZoom;
            this.currentZoom = this.currentZoom + Math.min(pPartialTick * (2 / 3f), 1) * diff;
        } else
            this.currentZoom = this.targetZoom;

        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.renderCategoryBackground(pPoseStack);

        //GL Scissors to the inner frame area so entries do not stick out
        int scale = (int) this.getMinecraft().getWindow().getGuiScale();
        int innerX = this.getInnerX();
        int innerY = this.getInnerY();
        int innerWidth = this.getInnerWidth();
        int innerHeight = this.getInnerHeight();

        GL11.glScissor(innerX * scale, innerY * scale, innerWidth * scale, innerHeight * scale);
        GL11.glEnable(GL_SCISSOR_TEST);

        this.renderEntries(pPoseStack);

        GL11.glDisable(GL_SCISSOR_TEST);

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
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        return super.handleComponentClicked(pStyle);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
