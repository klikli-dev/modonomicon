/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.gui;

import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.ClickEvent;
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
    public void resize(int width, int height) {
        this.guiLayers.forEach(screen -> screen.resize( width, height));
    }

    @Override
    public final void renderWithTooltipAndSubtitles(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushMatrix();
        for (int i = 0; i < this.guiLayers.size(); i++) {
            Screen layer = this.guiLayers.get(i);
            if (i == this.guiLayers.size() - 1) {
                // This is the last layer, it gets actual mouse over
                layer.renderWithTooltipAndSubtitles(guiGraphics, mouseX, mouseY, partialTick);
            } else {
                layer.renderWithTooltipAndSubtitles(guiGraphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTick);
            }
        }
        guiGraphics.pose().popMatrix();
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
    public boolean charTyped(CharacterEvent event) {
        return this.guiLayers.peek().charTyped(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return this.guiLayers.peek().keyReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.guiLayers.peek().mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
       return this.guiLayers.peek().mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return this.guiLayers.peek().mouseReleased(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return this.guiLayers.peek().mouseClicked(event, isDoubleClick);
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
    public void init(int width, int height) {
        super.init(width, height);
        this.guiLayers.peek().init(width, height);
    }

    @Override
    public void fillCrashDetails(CrashReport crashReport) {
        this.guiLayers.peek().fillCrashDetails(crashReport);
    }

    @Override
    public Font getFont() {
        return this.guiLayers.peek().getFont();
    }

    @Override
    public boolean showsActiveEffects() {
        return this.guiLayers.peek().showsActiveEffects();
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
    public boolean keyPressed(KeyEvent event) {
        return this.guiLayers.peek().keyPressed(event);
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
