/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookSpotlightPageRenderer extends BookPageRenderer<BookSpotlightPage> implements PageWithTextRenderer {
    public BookSpotlightPageRenderer(BookSpotlightPage page) {
        super(page);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(this.page.getTitle(), false, poseStack, BookContentScreen.PAGE_WIDTH / 2, 0);
        }

        this.renderBookTextHolder(this.getPage().getText(), poseStack, 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        int w = 66;
        int h = 26;

        RenderSystem.setShaderTexture(0, this.page.getBook().getCraftingTexture());
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, BookContentScreen.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 256);

        this.parentScreen.renderIngredient(poseStack, BookContentScreen.PAGE_WIDTH / 2 - 8, 15, mouseX, mouseY, this.page.getItem());


        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            if (this.page.hasTitle()) {
                var titleStyle = this.getClickedComponentStyleAtForTitle(this.page.getTitle(), BookContentScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
                if (titleStyle != null) {
                    return titleStyle;
                }
            }

            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), 0, this.getTextY(), BookContentScreen.PAGE_WIDTH, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }

    @Override
    public int getTextY() {
        return 40;
    }
}
