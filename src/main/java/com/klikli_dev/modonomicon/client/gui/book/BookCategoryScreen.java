/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Arcana
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.capability.BookStateCapability;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.config.ClientConfig;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.network.messages.BookEntryReadMessage;
import com.klikli_dev.modonomicon.network.messages.SaveCategoryStateMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.ArrayList;
import java.util.Optional;


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

    private ResourceLocation openEntry;

    public BookCategoryScreen(BookOverviewScreen bookOverviewScreen, BookCategory category) {
        this.bookOverviewScreen = bookOverviewScreen;
        this.category = category;

        this.connectionRenderer = this.bookOverviewScreen.getConnectionRenderer();

        this.targetZoom = 0.7f;
        this.currentZoom = this.targetZoom;
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
        int innerWidth = this.bookOverviewScreen.getInnerWidth() -1; //idk magic, otherwise it overflows by one (scaled) pixel into the border
        int innerHeight = this.bookOverviewScreen.getInnerHeight();

        //use scissors to constrain entries to inner area of category screen
        //scissors always needs to use gui scale because it runs in absolute coords!
        //see also vanilla class SocialInteractionsPlayerList using scissors in #render
        RenderSystem.enableScissor(innerX * scale, innerY * scale, innerWidth * scale, innerHeight * scale);

        this.renderEntries(pPoseStack, pMouseX, pMouseY);

        RenderSystem.disableScissor();

        this.renderEntryTooltips(pPoseStack, pMouseX, pMouseY);

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
            var displayStyle = this.getEntryDisplayState(entry);
            if (displayStyle == EntryDisplayState.UNLOCKED) {
                if (this.isEntryHovered(entry, xOffset, yOffset, (int) pMouseX, (int) pMouseY)) {
                    this.openEntry(entry);
                    return true;
                }
            }
        }

        return false;
    }

    public BookContentScreen openEntry(BookEntry entry) {
        if(!BookUnlockCapability.isReadFor(Minecraft.getInstance().player, entry)){
            Networking.sendToServer(new BookEntryReadMessage(entry.getBook().getId(), entry.getId()));
        }

        if(entry.getCategoryToOpen() != null){
            this.bookOverviewScreen.changeCategory(entry.getCategoryToOpen());
            return null;
        }

        this.openEntry = entry.getId();

        var bookContentScreen = new BookContentScreen(this.bookOverviewScreen, entry);
        Minecraft.getInstance().pushGuiLayer(bookContentScreen);

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

    private EntryDisplayState getEntryDisplayState(BookEntry entry) {
        var player = this.bookOverviewScreen.getMinecraft().player;

        var isEntryUnlocked = BookUnlockCapability.isUnlockedFor(player, entry);

        var allParentsUnlocked = true;
        for (var parent : entry.getParents()) {
            if (!BookUnlockCapability.isUnlockedFor(player, parent.getEntry())) {
                allParentsUnlocked = false;
                break;
            }
        }

        if (!allParentsUnlocked)
            return EntryDisplayState.HIDDEN;

        if (!isEntryUnlocked)
            return entry.hideWhileLocked() ? EntryDisplayState.HIDDEN : EntryDisplayState.LOCKED;

        return EntryDisplayState.UNLOCKED;
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
            var displayState = this.getEntryDisplayState(entry);
            var isHovered = this.isEntryHovered(entry, xOffset, yOffset, mouseX, mouseY);

            if (displayState == EntryDisplayState.HIDDEN)
                continue;

            if(displayState == EntryDisplayState.LOCKED){
                //Draw locked entries greyed out
                RenderSystem.setShaderColor(0.2F, 0.2F, 0.2F, 1.0F);
            } else if(isHovered){
                //Draw hovered entries slightly greyed out
                RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, 1.0F);
            }

            int texX = entry.getEntryBackgroundVIndex() * ENTRY_HEIGHT;
            int texY = entry.getEntryBackgroundUIndex() * ENTRY_WIDTH;

            RenderSystem.setShaderTexture(0, this.category.getEntryTextures());

            stack.pushPose();
            //we translate instead of applying the offset to the entry x/y to avoid jittering when moving
            stack.translate(xOffset, yOffset, 0);
            //render entry background
            this.bookOverviewScreen.blit(stack, entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP, entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP, texX, texY, ENTRY_WIDTH, ENTRY_HEIGHT);

            //render icon
            entry.getIcon().render(stack, entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP + 5, entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP + 5);

            //render unread icon
            if(displayState == EntryDisplayState.UNLOCKED && !BookUnlockCapability.isReadFor(this.bookOverviewScreen.getMinecraft().player, entry)){
                final int U = 350;
                final int V = 19;
                final int width = 11;
                final int height = 11;

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                //RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.enableDepthTest();

                //testing
                stack.pushPose();
                stack.translate(0,0,10);
                //if focused we go to the right of our normal button (instead of down, like mc buttons do)
                BookContentScreen.drawFromTexture(stack, this.bookOverviewScreen.getBook(),
                        entry.getX() * ENTRY_GRID_SCALE + ENTRY_GAP + 16 + 2,
                        entry.getY() * ENTRY_GRID_SCALE + ENTRY_GAP - 2, U + (isHovered ? width : 0), V, width, height);
                stack.popPose();
            }

            stack.popPose();

            //reset color to avoid greyed out carrying over
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

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
            var displayState = this.getEntryDisplayState(entry);
            if(displayState == EntryDisplayState.HIDDEN)
                continue;

            this.renderTooltip(stack, entry, displayState, xOffset, yOffset, mouseX, mouseY);
        }
    }

    private boolean isEntryHovered(BookEntry entry, float xOffset, float yOffset, int mouseX, int mouseY) {
        int x = (int) ((entry.getX() * ENTRY_GRID_SCALE + xOffset + 2) * this.currentZoom);
        int y = (int) ((entry.getY() * ENTRY_GRID_SCALE + yOffset + 2) * this.currentZoom);
        int innerX = this.bookOverviewScreen.getInnerX();
        int innerY = this.bookOverviewScreen.getInnerY();
        int innerWidth = this.bookOverviewScreen.getInnerWidth();
        int innerHeight = this.bookOverviewScreen.getInnerHeight();
        return mouseX >= x && mouseX <= x + (ENTRY_WIDTH * this.currentZoom)
                && mouseY >= y && mouseY <= y + (ENTRY_HEIGHT * this.currentZoom)
                && mouseX >= innerX && mouseX <= innerX + innerWidth
                && mouseY >= innerY && mouseY <= innerY + innerHeight;
    }

    private void renderTooltip(PoseStack stack, BookEntry entry, EntryDisplayState displayState, float xOffset, float yOffset, int mouseX, int mouseY) {
        //hovered?
        if (this.isEntryHovered(entry, xOffset, yOffset, mouseX, mouseY)) {

            var tooltip = new ArrayList<Component>();

            if(displayState == EntryDisplayState.LOCKED){
                tooltip.addAll(entry.getCondition().getTooltip(BookConditionEntryContext.of(this.bookOverviewScreen.getBook(), entry)));
            } else if(displayState == EntryDisplayState.UNLOCKED){
                //add name in bold
                tooltip.add(new TranslatableComponent(entry.getName()).withStyle(ChatFormatting.BOLD));
                //add description
                if (!entry.getDescription().isEmpty()) {
                    tooltip.add(new TranslatableComponent(entry.getDescription()));
                }
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

    private void loadCategoryState() {
        var state = BookStateCapability.getCategoryStateFor(this.bookOverviewScreen.getMinecraft().player, this.category);
        BookGuiManager.get().currentCategory = this.category;
        BookGuiManager.get().currentCategoryScreen = this;
        if (state != null) {
            this.scrollX = state.scrollX;
            this.scrollY = state.scrollY;
            this.targetZoom = state.targetZoom;
            this.currentZoom = state.targetZoom;
            if(state.openEntry != null){
                var openEntry = this.category.getEntry(state.openEntry);
                if(openEntry != null){
                    //no need to load history here, will be handled by book content screen
                    this.openEntry(openEntry);
                }
            }
        }
    }

    public void onDisplay(){
        this.loadCategoryState();
    }

    public void onClose() {
        Networking.sendToServer(new SaveCategoryStateMessage(this.category, this.scrollX, this.scrollY, this.currentZoom, this.openEntry));
    }

    public void onCloseEntry(BookContentScreen screen) {
        this.openEntry = null;
    }
}
