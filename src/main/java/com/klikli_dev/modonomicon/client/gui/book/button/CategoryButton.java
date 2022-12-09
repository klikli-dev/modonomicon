/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class CategoryButton extends Button {

    private final BookOverviewScreen parent;
    private final BookCategory category;
    private final int categoryIndex;

    public CategoryButton(BookOverviewScreen parent, BookCategory category, int categoryIndex, int pX, int pY, int width, int height, OnPress pOnPress, Tooltip tooltip) {
        super(pX, pY, width, height, Component.literal(""), pOnPress, Button.DEFAULT_NARRATION);
        this.setTooltip(tooltip);
        this.parent = parent;
        this.category = category;
        this.categoryIndex = categoryIndex;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public int getCategoryIndex() {
        return this.categoryIndex;
    }

    @Override
    public void renderButton(PoseStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        if (this.visible) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            int texX = 0;
            int texY = 145;

            int renderX = this.getX();
            int renderWidth = this.width;

            if (this.categoryIndex == this.parent.getCurrentCategory()) {
                renderX -= 3;
                renderWidth += 3;
            } else if (this.isHoveredOrFocused()) {
                renderX -= 1;
                renderWidth += 1;
            }

            //draw category button background
            RenderSystem.setShaderTexture(0, this.parent.getBookOverviewTexture());
            GuiComponent.blit(pMatrixStack, renderX, this.getY(), this.parent.getBlitOffset() + 50, texX, texY, renderWidth, this.height, 256, 256);

            //then draw icon

            int iconOffset = 8;
            pMatrixStack.pushPose();
            pMatrixStack.translate(0, 0, 100);
            this.category.getIcon().render(pMatrixStack, renderX + iconOffset, this.getY() + 2);
            pMatrixStack.popPose();
        }
    }
}
