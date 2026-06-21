/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiButtonSprites;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.networking.ClickResearchProgressButtonMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ResearchProgressButton extends Button {

    public static final int WIDTH = 44;
    public static final int HEIGHT = 20;

    private final BookParentScreen parent;
    private final int scissorX;
    private final MutableComponent tooltipVisible;
    private final MutableComponent tooltipAll;
    private final MutableComponent tooltipNone;
    private final MutableComponent tooltipShiftInstructions;
    private final MutableComponent tooltipShiftWarning;
    private final Supplier<Boolean> hasVisibleResearchProgress;
    private final Supplier<Boolean> hasAnyResearchProgress;
    private final Runnable onProgressVisible;
    private final Runnable onProgressAll;
    private boolean wasHovered;
    private int tooltipMsDelay;
    private long hoveredStartTime;

    public ResearchProgressButton(BookParentScreen parent, int x, int y, int scissorX,
                                  Supplier<Boolean> hasVisibleResearchProgress,
                                  Supplier<Boolean> hasAnyResearchProgress,
                                  Runnable onProgressVisible,
                                  Runnable onProgressAll) {
        super(x, y, WIDTH, HEIGHT,
                Component.translatable(Gui.BUTTON_VIEWED_ONCE_RESEARCH),
                ResearchProgressButton::onPress, Button.DEFAULT_NARRATION);
        this.parent = parent;
        this.scissorX = scissorX;
        this.tooltipVisible = Component.translatable(Gui.BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_VISIBLE);
        this.tooltipAll = Component.translatable(Gui.BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_ALL);
        this.tooltipNone = Component.translatable(Gui.BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_NONE);
        this.tooltipShiftInstructions = Component.translatable(Gui.BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_SHIFT_INSTRUCTIONS);
        this.tooltipShiftWarning = Component.translatable(Gui.BUTTON_VIEWED_ONCE_RESEARCH_TOOLTIP_SHIFT_WARNING);
        this.hasVisibleResearchProgress = hasVisibleResearchProgress;
        this.hasAnyResearchProgress = hasAnyResearchProgress;
        this.onProgressVisible = onProgressVisible;
        this.onProgressAll = onProgressAll;
    }

    private static void onPress(Button button) {
        ((ResearchProgressButton) button).onPress();
    }

    private void onPress() {
        if (this.hasVisibleResearchProgress.get() && !Minecraft.getInstance().hasShiftDown()) {
            Services.NETWORK.sendToServer(new ClickResearchProgressButtonMessage(this.parent.getBook().getId(), false));
            this.onProgressVisible.run();
        } else if (this.hasAnyResearchProgress.get() && Minecraft.getInstance().hasShiftDown()) {
            Services.NETWORK.sendToServer(new ClickResearchProgressButtonMessage(this.parent.getBook().getId(), true));
            this.onProgressAll.run();
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        this.active = this.hasVisibleResearchProgress.get() || this.hasAnyResearchProgress.get();
        if (!this.active) return;

        guiGraphics.pose().pushMatrix();
        var hovered = this.isHovered();

        GuiButtonSprites sprites = this.hasVisibleResearchProgress.get()
                ? this.parent.getBook().theme().content().researchVisibleButton()
                : this.parent.getBook().theme().content().researchNoneButton();

        if (Minecraft.getInstance().hasShiftDown()) {
            sprites = this.parent.getBook().theme().content().researchAllButton();
        }

        var background = this.parent.getBook().theme().content().researchProgressButtonBackground().state(hovered, false);

        BookSideButtonRenderer.renderSlidingButton(guiGraphics, this.parent.getBook().theme().layout().searchButtonXOffset(),
                this.getX(), this.getY(), this.width, this.height, this.scissorX,
                ((Screen) this.parent).height, hovered,
                background,
                GuiSprite.EMPTY);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.parent.getBook().theme().layout().searchButtonXOffset(), 0);
        int renderX = this.getX() - BookSideButtonRenderer.BUTTON_SLIDE_OFFSET + (hovered ? 1 : 0);
        BookSideButtonRenderer.renderRightIcon(guiGraphics, sprites.state(hovered, false), renderX, this.getY(), this.width, 2f / 3f, -4);
        guiGraphics.pose().popMatrix();

        guiGraphics.pose().popMatrix();

        this.updateCustomTooltip(guiGraphics, i, j);
    }

    private void updateCustomTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        boolean flag = this.isHovered();
        if (flag != this.wasHovered) {
            if (flag) {
                this.hoveredStartTime = Util.getMillis();
            }

            this.wasHovered = flag;
        }

        if (flag && Util.getMillis() - this.hoveredStartTime > (long) this.tooltipMsDelay) {
            guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, this.getCustomTooltip(), Optional.empty(), mouseX, mouseY);
        }
    }

    public List<Component> getCustomTooltip() {
        if (Minecraft.getInstance().hasShiftDown()) {
            return List.of(this.tooltipAll, Component.empty(), this.tooltipShiftWarning);
        }

        if (this.hasVisibleResearchProgress.get()) {
            return List.of(this.tooltipVisible, Component.empty(), this.tooltipShiftInstructions);
        }

        return List.of(this.tooltipNone, Component.empty(), this.tooltipShiftInstructions);
    }
}
