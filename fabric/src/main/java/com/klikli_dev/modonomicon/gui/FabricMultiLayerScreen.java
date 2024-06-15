/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.Music;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class FabricMultiLayerScreen extends Screen {

    public final Stack<Screen> guiLayers = new Stack<>();

    protected FabricMultiLayerScreen() {
        super(Component.empty());
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.guiLayers.forEach(screen -> screen.resize(minecraft, width, height));
    }

    @Override
    public void renderWithTooltip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.pose().pushPose();
        for (int i = 0; i < this.guiLayers.size(); i++) {
            Screen layer = this.guiLayers.get(i);
            if (i == this.guiLayers.size() - 1) {
                // This is the last layer, it gets actual mouse over
                layer.renderWithTooltip(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            } else {
                layer.renderWithTooltip(pGuiGraphics, Integer.MAX_VALUE, Integer.MAX_VALUE, pPartialTick);
            }
        }
        pGuiGraphics.pose().popPose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //do nothing
    }


    @Override
    public void triggerImmediateNarration(boolean onlyNarrateNew) {
        //do nothing
    }

    @Override
    public boolean isPauseScreen() {
        return this.guiLayers.peek().isPauseScreen();
    }


    @Override
    public void added() {
        this.guiLayers.peek().added();
    }


    @Override
    public int getTabOrderGroup() {
        return this.guiLayers.peek().getTabOrderGroup();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.guiLayers.peek().mouseMoved(mouseX, mouseY);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return this.guiLayers.peek().nextFocusPath(event);
    }

    @Nullable
    @Override
    public ComponentPath getCurrentFocusPath() {
        return this.guiLayers.peek().getCurrentFocusPath();
    }

    @Override
    public boolean isFocused() {
        return this.guiLayers.peek().isFocused();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.guiLayers.peek().charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.guiLayers.peek().keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.guiLayers.peek().mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.guiLayers.peek().mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.guiLayers.peek().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.guiLayers.peek().mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
        return this.guiLayers.peek().getChildAt(mouseX, mouseY);
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.guiLayers.peek().getFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        this.guiLayers.peek().setFocused(focused);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.guiLayers.peek().setFocused(focused);
    }

    @Nullable
    @Override
    public Music getBackgroundMusic() {
        return this.guiLayers.peek().getBackgroundMusic();
    }

    @Override
    public ScreenRectangle getRectangle() {
        return this.guiLayers.peek().getRectangle();
    }

    @Override
    public void setTooltipForNextRenderPass(Tooltip tooltip, ClientTooltipPositioner positioner, boolean override) {
        this.guiLayers.peek().setTooltipForNextRenderPass(tooltip, positioner, override);
    }

    @Override
    public void setTooltipForNextRenderPass(Component tooltip) {
        this.guiLayers.peek().setTooltipForNextRenderPass(tooltip);
    }

    @Override
    public void setTooltipForNextRenderPass(List<FormattedCharSequence> tooltip, ClientTooltipPositioner positioner, boolean override) {
        this.guiLayers.peek().setTooltipForNextRenderPass(tooltip, positioner, override);
    }

    @Override
    public void setTooltipForNextRenderPass(List<FormattedCharSequence> tooltip) {
        this.guiLayers.peek().setTooltipForNextRenderPass(tooltip);
    }

    @Override
    public void updateNarratorStatus(boolean bl) {
        this.guiLayers.peek().updateNarratorStatus(bl);
    }

    @Override
    public void handleDelayedNarration() {
        this.guiLayers.peek().handleDelayedNarration();
    }

    @Override
    public void afterKeyboardAction() {
        this.guiLayers.peek().afterKeyboardAction();
    }

    @Override
    public void afterMouseAction() {
        this.guiLayers.peek().afterMouseAction();
    }

    @Override
    public void afterMouseMove() {
        this.guiLayers.peek().afterMouseMove();
    }

    @Override
    public void onFilesDrop(List<Path> packs) {
        this.guiLayers.peek().onFilesDrop(packs);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.guiLayers.peek().isMouseOver(mouseX, mouseY);
    }

    @Override
    public void renderTransparentBackground(GuiGraphics guiGraphics) {
        this.guiLayers.peek().renderTransparentBackground(guiGraphics);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.guiLayers.peek().renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void removed() {
        this.guiLayers.peek().removed();
    }

    @Override
    public void tick() {
        this.guiLayers.peek().tick();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.guiLayers.peek().children();
    }


    @Override
    public boolean handleComponentClicked(@Nullable Style style) {
        return this.guiLayers.peek().handleComponentClicked(style);
    }

    @Override
    public void onClose() {
        this.guiLayers.peek().onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.guiLayers.peek().shouldCloseOnEsc();
    }

    @Override
    public void clearFocus() {
        this.guiLayers.peek().clearFocus();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.guiLayers.peek().keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public Component getNarrationMessage() {
        return this.guiLayers.peek().getNarrationMessage();
    }

    @Override
    public Component getTitle() {
        return this.guiLayers.peek().getTitle();
    }
}
