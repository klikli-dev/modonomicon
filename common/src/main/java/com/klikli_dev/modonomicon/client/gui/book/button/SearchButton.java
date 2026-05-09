/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;

import net.minecraft.network.chat.Component;

public class SearchButton extends Button {

    private final BookParentScreen parent;
    private final int scissorX;

    public SearchButton(BookParentScreen parent, int pX, int pY, int scissorX, int width, int height, OnPress pOnPress, Tooltip tooltip) {
        super(pX, pY, width, height, Component.literal(""), pOnPress, Button.DEFAULT_NARRATION);
        this.setTooltip(tooltip);
        this.scissorX = scissorX;
        this.parent = parent;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if (this.visible) {
            int xOffset = this.parent.getBook().theme().layout().searchButtonXOffset();
            var background = this.parent.getBook().theme().content().searchButton().state(this.isHovered(), false);
            BookSideButtonRenderer.renderSlidingButton(guiGraphics, xOffset, this.getX(), this.getY(), this.width, this.height,
                    this.scissorX, ((net.minecraft.client.gui.screens.Screen) this.parent).height, this.isHovered(),
                    background,
                    BookSideButtonRenderer.collectionIconOrEmpty(background,
                            this.parent.getBook().theme().content().collectionButtonNormal(),
                            this.parent.getBook().theme().content().searchButtonIcon()));
        }
    }
}

