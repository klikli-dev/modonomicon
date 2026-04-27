/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.client.gui.book.BookParentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;

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
            guiGraphics.pose().pushMatrix();
            int xOffset = this.parent.getBook().theme().layout().searchButtonXOffset();
            guiGraphics.pose().translate(xOffset, 0);

            int scissorX = this.scissorX + xOffset;
            var sprite = this.parent.getBook().theme().overview().searchButton().state(this.isHovered(), false);

            int renderX = this.getX() - 16;
            int scissorWidth = this.width + (this.getX() - this.scissorX);
            int scissorY = (((Screen) this.parent).height - this.getY() - this.height - 1); //from the bottom up

            if (this.isHovered()) {
                renderX += 1;
                scissorWidth -= 1;
            }

            //as of 1.20 this causes the button to vanish behind the rendered world, so we don't use it
            //guiGraphics.pose().translate(xOffset, 0, -1000);

            //GL scissors allows us to move the button on hover without intersecting with book border
            guiGraphics.enableScissor(scissorX, scissorY, scissorX + scissorWidth, scissorY + 1000);

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, sprite.texture(), renderX, this.getY(), 0, 0, this.width, this.height, sprite.width(), sprite.height());

            guiGraphics.disableScissor();
            guiGraphics.pose().popMatrix();
        }
    }
}
