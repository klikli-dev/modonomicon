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
import com.klikli_dev.modonomicon.networking.ClickReadAllButtonMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


public class ReadAllButton extends Button {

    public static final int WIDTH = 44;
    public static final int HEIGHT = 20;


    private final BookParentScreen parent;
    private final int scissorX;

    private final MutableComponent tooltipReadUnlocked;
    private final MutableComponent tooltipReadAll;
    private final MutableComponent tooltipNone;
    private final MutableComponent tooltipShiftInstructions;
    private final MutableComponent tooltipShiftWarning;
    private final Supplier<Boolean> hasUnreadEntries;
    private final Supplier<Boolean> hasUnreadUnlockedEntries;
    private final Supplier<Boolean> hasUnreadCategories;
    private final Supplier<Boolean> hasUnreadUnlockedCategories;
    private final Runnable onReadUnlocked;
    private final Runnable onReadAll;

    private boolean wasHovered;
    private int tooltipMsDelay;
    private long hoveredStartTime;


    public ReadAllButton(BookParentScreen parent, int x, int y, int scissorX,
                         Supplier<Boolean> hasUnreadEntries,
                         Supplier<Boolean> hasUnreadUnlockedEntries,
                         Supplier<Boolean> hasUnreadCategories,
                         Supplier<Boolean> hasUnreadUnlockedCategories,
                         Runnable onReadUnlocked,
                         Runnable onReadAll) {
        super(x, y, WIDTH, HEIGHT,
                Component.translatable(Gui.BUTTON_READ_ALL),
                ReadAllButton::onPress, Button.DEFAULT_NARRATION
        );
        this.parent = parent;
        this.scissorX = scissorX;
        this.tooltipReadUnlocked = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_READ_UNLOCKED);
        this.tooltipReadAll = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_READ_ALL);
        this.tooltipNone = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_NONE);
        this.tooltipShiftInstructions = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_SHIFT_INSTRUCTIONS);
        this.tooltipShiftWarning = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_SHIFT_WARNING);
        this.hasUnreadEntries = hasUnreadEntries;
        this.hasUnreadUnlockedEntries = hasUnreadUnlockedEntries;
        this.hasUnreadCategories = hasUnreadCategories;
        this.hasUnreadUnlockedCategories = hasUnreadUnlockedCategories;
        this.onReadUnlocked = onReadUnlocked;
        this.onReadAll = onReadAll;
    }

    private static void onPress(Button button) {
        ((ReadAllButton) button).onPress();
    }

    private void onPress() {
        if (this.hasUnreadUnlockedEntries.get() && !Minecraft.getInstance().hasShiftDown()) {
            Services.NETWORK.sendToServer(new ClickReadAllButtonMessage(this.parent.getBook().getId(), false));
            this.onReadUnlocked.run();
        } else if (this.hasUnreadEntries.get() && Minecraft.getInstance().hasShiftDown()) {
            Services.NETWORK.sendToServer(new ClickReadAllButtonMessage(this.parent.getBook().getId(), true));
            this.onReadAll.run();
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        this.active = this.hasUnreadEntries.get() || this.hasUnreadUnlockedEntries.get() || this.hasUnreadCategories.get() || this.hasUnreadUnlockedCategories.get();
        if (!this.active) return;
        //if focused we go to the right of our normal button (instead of down, like mc buttons do)

        guiGraphics.pose().pushMatrix();
        //TODO had a +200 z here
        var hovered = this.isHovered();

        //by default we show green if we can read unlocked entries or gray if none
        //if shift is down we offer to mark all as read
        //if neither is possible the button should be hidden which is handled by BookOverviewScreen#canSeeReadAllButton
        GuiButtonSprites sprites = this.hasUnreadUnlockedEntries.get()
                ? this.parent.getBook().theme().content().readUnlockedButton()
                : this.parent.getBook().theme().content().readNoneButton();

        if (Minecraft.getInstance().hasShiftDown()) {
            sprites = this.parent.getBook().theme().content().readAllButton();
        }

        var background = this.parent.getBook().theme().content().searchButton().state(hovered, false);

        BookSideButtonRenderer.renderSlidingButton(guiGraphics, this.parent.getBook().theme().layout().searchButtonXOffset(),
                this.getX(), this.getY(), this.width, this.height, this.scissorX,
                ((net.minecraft.client.gui.screens.Screen) this.parent).height, hovered,
                background,
                GuiSprite.EMPTY);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.parent.getBook().theme().layout().searchButtonXOffset(), 0);
        int renderX = this.getX() - BookSideButtonRenderer.BUTTON_SLIDE_OFFSET + (hovered ? 1 : 0);
        BookSideButtonRenderer.renderRightIcon(guiGraphics, sprites.state(hovered, false), renderX, this.getY(), this.width, 2f / 3f, 3);
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
            var tooltip = this.getCustomTooltip();

            guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, Optional.empty(), mouseX, mouseY);
        }

    }

    public List<Component> getCustomTooltip() {

        if (Minecraft.getInstance().hasShiftDown()) {
            return List.of(this.tooltipReadAll, Component.empty(), this.tooltipShiftWarning);
        }

        if (this.hasUnreadUnlockedEntries.get()) {
            return List.of(this.tooltipReadUnlocked, Component.empty(), this.tooltipShiftInstructions);
        }

        return List.of(this.tooltipNone, Component.empty(), this.tooltipShiftInstructions);
    }

}
