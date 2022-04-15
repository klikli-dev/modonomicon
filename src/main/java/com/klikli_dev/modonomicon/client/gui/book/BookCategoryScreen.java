/*
 * LGPL-3.0
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

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;


public class BookCategoryScreen {

    public static final int ENTRY_GRID_SCALE = 30;
    public static final int ENTRY_GAP = 2;
    public static final int MAX_SCROLL = 512;

    public static final int ENTRY_HEIGHT = 26;
    public static final int ENTRY_WIDTH = 26;

    private final BookOverviewScreen bookOverviewScreen;
    private final BookCategory category;
    private final EntryConnectionRenderer connectionRenderer;
    protected int backgroundTextureWidth = 512;
    protected int backgroundTextureHeight = 512;
    private float scrollX = 0;
    private float scrollY = 0;
    private boolean isScrolling;
    private float targetZoom;
    private float currentZoom;

    public BookCategoryScreen(BookOverviewScreen bookOverviewScreen, BookCategory category) {
        this.bookOverviewScreen = bookOverviewScreen;
        this.category = category;

        this.connectionRenderer = this.bookOverviewScreen.getConnectionRenderer();

        this.targetZoom = 0.7f;
        this.currentZoom = this.targetZoom;

        this.loadCategorySettings();
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public float getXOffset() {
        return ((this.bookOverviewScreen.getInnerWidth() / 2f) * (1 / this.currentZoom)) - this.scrollX / 2;
    }

    public float getYOffset() {
        return ((this.bookOverviewScreen.getInnerHeight() / 2f) * (1 / this.currentZoom)) - this.scrollY / 2;
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (ClientConfig.get().qolCategory.enableSmoothZoom.get()) {
            float diff = this.targetZoom - this.currentZoom;
            this.currentZoom = this.currentZoom + Math.min(pPartialTick * (2 / 3f), 1) * diff;
        } else
            this.currentZoom = this.targetZoom;

        //GL Scissors to the inner frame area so entries do not stick out
        int scale = (int) this.bookOverviewScreen.getMinecraft().getWindow().getGuiScale();
        int innerX = this.bookOverviewScreen.getInnerX();
        int innerY = this.bookOverviewScreen.getInnerY();
        int innerWidth = this.bookOverviewScreen.getInnerWidth();
        int innerHeight = this.bookOverviewScreen.getInnerHeight();

        GL11.glScissor(innerX * scale, innerY * scale, innerWidth * scale, innerHeight * scale);
        GL11.glEnable(GL_SCISSOR_TEST);

        this.renderEntries(pPoseStack, pMouseX, pMouseY);
        this.renderEntryTooltips(pPoseStack, pMouseX, pMouseY);

        GL11.glDisable(GL_SCISSOR_TEST);
    }

    public void zoom(double delta) {
        float step = 1.2f;
        if ((delta < 0 && this.targetZoom > 0.5) || (delta > 0 && this.targetZoom < 1))
            this.targetZoom *= delta > 0 ? step : 1 / step;
        if (this.targetZoom > 1f)
            this.targetZoom = 1f;
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        //Based on advancementsscreen
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                this.scroll(pDragX * 1.5, pDragY * 1.5);
            }
            return true;
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        float xOffset = this.getXOffset();
        float yOffset = this.getYOffset();
        for (var entry : this.category.getEntries().values()) {
            if (this.isEntryHovered(entry, xOffset, yOffset, (int) pMouseX, (int) pMouseY)) {
                this.openEntry(entry);
                return true;
            }
        }

        return false;
    }

    public BookContentScreen openEntry(BookEntry entry) {
        var bookContentScreen = new BookContentScreen(this.bookOverviewScreen, entry);
        ForgeHooksClient.pushGuiLayer(Minecraft.getInstance(), bookContentScreen);
        return bookContentScreen;
    }

    public void renderBackground(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.category.getBackground());

        //based on the frame's total width and its thickness, calculate where the inner area starts
        int innerX = this.bookOverviewScreen.getInnerX();
        int innerY = this.bookOverviewScreen.getInnerY();

        //then calculate the corresponding inner area width/height so we don't draw out of the frame
        int innerWidth = this.bookOverviewScreen.getInnerWidth();
        int innerHeight = this.bookOverviewScreen.getInnerHeight();

        //based on arcana's render research background
        //does not correctly work for non-automatic gui scale, but that only leads to background repeat -> no problem
        float xScale = MAX_SCROLL * 2.0f / ((float) MAX_SCROLL + this.bookOverviewScreen.getFrameThicknessW() - this.bookOverviewScreen.getFrameWidth());
        float yScale = MAX_SCROLL * 2.0f / ((float) MAX_SCROLL + this.bookOverviewScreen.getFrameThicknessH() - this.bookOverviewScreen.getFrameHeight());
        float scale = Math.max(xScale, yScale);
        float xOffset = xScale == scale ? 0 : (MAX_SCROLL - (innerWidth + MAX_SCROLL * 2.0f / scale)) / 2;
        float yOffset = yScale == scale ? 0 : (MAX_SCROLL - (innerHeight + MAX_SCROLL * 2.0f / scale)) / 2;

        //for some reason on this one blit overload tex width and height are switched. It does correctly call the followup though, so we have to go along
        //force offset to int here to reduce difference to entry rendering which is pos based and thus int precision only
        GuiComponent.blit(poseStack, innerX, innerY, this.bookOverviewScreen.getBlitOffset(),
                (this.scrollX + MAX_SCROLL) / scale + xOffset,
                (this.scrollY + MAX_SCROLL) / scale + yOffset,
                innerWidth, innerHeight, this.backgroundTextureHeight, this.backgroundTextureWidth);
    }

    private void renderEntries(PoseStack stack, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        //calculate the render offset
        float xOffset = this.getXOffset();
        float yOffset = this.getYOffset();

        stack.pushPose();
        stack.scale(this.currentZoom, this.currentZoom, 1.0f);
        for (var entry : this.category.getEntries().values()) {
            //TODO: handle hidden entries + don't draw their tooltip
            //TODO: select type of entry background
            int texX = 0; //select the entry background with this
            int texY = 0;

            RenderSystem.setShaderTexture(0, this.category.getEntryTextures());

            stack.pushPose();
            //we translate instead of applying the offset to the entry x/y to avoid jittering when moving
            stack.translate(xOffset, yOffset, 0);
            //render entry background
            this.bookOverviewScreen.blit(stack, entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP, entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP, texX, texY, ENTRY_WIDTH, ENTRY_HEIGHT);

            //render icon
            entry.getIcon().render(stack, entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP + 5, entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP + 5);
            stack.popPose();

            this.renderConnections(stack, entry, xOffset, yOffset);
        }
        stack.popPose();
    }

    private void renderEntryTooltips(PoseStack stack, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        //calculate the render offset
        float xOffset = this.getXOffset();
        float yOffset = this.getYOffset();

        for (var entry : this.category.getEntries().values()) {
            //TODO: handle hidden entries + don't draw their tooltip
            this.renderTooltip(stack, entry, xOffset, yOffset, mouseX, mouseY);
        }
    }

    private boolean isEntryHovered(BookEntry entry, float xOffset, float yOffset, int mouseX, int mouseY) {
        int x = (int) ((entry.getX() * ENTRY_GRID_SCALE + xOffset + 2) * this.currentZoom);
        int y = (int) ((entry.getY() * ENTRY_GRID_SCALE + yOffset + 2) * this.currentZoom);
        int innerX = this.bookOverviewScreen.getInnerX();
        int innerY = this.bookOverviewScreen.getInnerX();
        int innerWidth = this.bookOverviewScreen.getInnerWidth();
        int innerHeight = this.bookOverviewScreen.getInnerHeight();
        return mouseX >= x && mouseX <= x + (ENTRY_WIDTH * this.currentZoom)
                && mouseY >= y && mouseY <= y + (ENTRY_HEIGHT * this.currentZoom)
                && mouseX >= innerX && mouseX <= innerX + innerWidth
                && mouseY >= innerY && mouseY <= innerY + innerHeight;
    }

    private void renderTooltip(PoseStack stack, BookEntry entry, float xOffset, float yOffset, int mouseX, int mouseY) {
        //hovered?
        if (this.isEntryHovered(entry, xOffset, yOffset, mouseX, mouseY)) {

            //draw name
            var tooltip = new ArrayList<Component>();
            tooltip.add(new TranslatableComponent(entry.getName()).withStyle(ChatFormatting.BOLD));
            if (!entry.getDescription().isEmpty()) {
                tooltip.add(new TranslatableComponent(entry.getDescription()));
            }
            //draw description
            this.bookOverviewScreen.renderTooltip(stack, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private void renderConnections(PoseStack stack, BookEntry entry, float xOffset, float yOffset) {
        //our arrows are aliased and need blending
        RenderSystem.enableBlend();

        for (var parent : entry.getParents()) {
            this.bookOverviewScreen.getConnectionRenderer().setBlitOffset(this.bookOverviewScreen.getBlitOffset());
            stack.pushPose();
            stack.translate(xOffset, yOffset, 0);
            RenderSystem.setShaderTexture(0, this.category.getEntryTextures());
            this.connectionRenderer.render(stack, entry, parent);
            stack.popPose();
        }

        RenderSystem.disableBlend();
    }

    private void scroll(double pDragX, double pDragY) {
        this.scrollX = (float) Mth.clamp(this.scrollX - pDragX, -MAX_SCROLL, MAX_SCROLL);
        this.scrollY = (float) Mth.clamp(this.scrollY - pDragY, -MAX_SCROLL, MAX_SCROLL);
    }

    private void loadCategorySettings() {
        //TODO: load category settings from capability
        //      Settings = scroll, zoom etc
    }
}
