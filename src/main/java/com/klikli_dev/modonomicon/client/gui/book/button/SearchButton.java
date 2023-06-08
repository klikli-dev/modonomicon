/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class SearchButton extends Button {

    private final BookOverviewScreen parent;
    private final int scissorX;

    public SearchButton(BookOverviewScreen parent, int pX, int pY, int scissorX, int width, int height, OnPress pOnPress, Tooltip tooltip) {
        super(pX, pY, width, height, Component.literal(""), pOnPress, Button.DEFAULT_NARRATION);
        this.setTooltip(tooltip);
        this.scissorX = scissorX;
        this.parent = parent;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        if (this.visible) {
            guiGraphics.pose().pushPose();
            int xOffset = this.parent.getBook().getSearchButtonXOffset();
            guiGraphics.pose().translate(xOffset, 0, 0);

            int scissorX = this.scissorX + xOffset;
            int texX = 15;
            int texY = 165;

            int renderX = this.getX();
            int scissorWidth = this.width + (this.getX() - this.scissorX);
            int scissorY = (this.parent.height - this.getY() - this.height - 1); //from the bottom up

            if (this.isHovered()) {
                renderX += 1;
                scissorWidth -= 1;
            }

            int scale = (int) this.parent.getMinecraft().getWindow().getGuiScale();

            guiGraphics.pose().translate(xOffset, 0, -1000);

            //GL scissors allows us to move the button on hover without intersecting with book border
            //scissors always needs to use gui scale because it runs in absolute coords!
            //see also vanilla class SocialInteractionsPlayerList using scissors in #render
            //we are not using GuiComponent.enableScissor because a) we'd have to calculate absolute coords from width/height and b) we don't need the stack functionality

            RenderSystem.enableScissor(scissorX * scale, scissorY * scale, scissorWidth * scale, 1000);

            //TODO: Upgrade: check if blit is correct here
            //     int blitOffset = 50;
            //            GuiComponent.blit(pMatrixStack, renderX, this.getY(), blitOffset, texX, texY, this.width, this.height, 256, 256);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(this.parent.getBookOverviewTexture(), renderX, this.getY(), texX, texY, this.width, this.height, 256, 256);


            RenderSystem.disableScissor();

            guiGraphics.pose().popPose();

        }
    }
}
