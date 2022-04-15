/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
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

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;

public class CategoryButton extends Button {

    private final BookOverviewScreen parent;
    private final BookCategory category;
    private final int categoryIndex;
    public CategoryButton(BookOverviewScreen parent, BookCategory category, int categoryIndex, int pX, int pY, int width, int height, OnPress pOnPress, OnTooltip pOnTooltip) {
        super(pX, pY, width, height, new TextComponent(""), pOnPress, pOnTooltip);
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

            int renderX = this.x;
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
            GuiComponent.blit(pMatrixStack, renderX, this.y, this.parent.getBlitOffset()+ 50, texX, texY, renderWidth, this.height, 256, 256);

            //then draw icon

            int iconOffset = 6;
            this.category.getIcon().render(pMatrixStack, renderX + iconOffset, this.y + 2);
        }

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(pMatrixStack, pMouseX, pMouseY);
        }
    }
}
