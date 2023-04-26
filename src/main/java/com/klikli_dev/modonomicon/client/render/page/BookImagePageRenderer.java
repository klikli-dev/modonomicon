/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookImagePage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.button.SmallArrowButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookImagePageRenderer extends BookPageRenderer<BookImagePage> implements PageWithTextRenderer {

    int index;

    public BookImagePageRenderer(BookImagePage page) {
        super(page);
    }

    public void handleButtonArrow(Button button) {
        boolean left = ((SmallArrowButton) button).left;
        if (left) {
            this.index--;
        } else {
            this.index++;
        }
    }

    @Override
    public void onBeginDisplayPage(BookContentScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);

        int x = 94;
        int y = 101;

        this.addButton(new SmallArrowButton(parentScreen, x, y, true, () -> this.index > 0, this::handleButtonArrow));
        this.addButton(new SmallArrowButton(parentScreen, x + 10, y, false, () -> this.index < this.page.getImages().length - 1, this::handleButtonArrow));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(this.page.getTitle(), false, poseStack, BookContentScreen.PAGE_WIDTH / 2, 0);
        }

        this.renderBookTextHolder(this.getPage().getText(), poseStack, 0, this.getTextY(), BookContentScreen.PAGE_WIDTH);

        RenderSystem.setShaderTexture(0, this.page.getImages()[this.index]);

        int x = BookContentScreen.PAGE_WIDTH / 2 - 53;
        int y = 7;
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.enableBlend();
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 1);
        GuiComponent.blit(poseStack, x * 2 + 6, y * 2 + 6, 0, 0, 200, 200);
        poseStack.scale(2F, 2F, 1);
        poseStack.popPose();

        if (this.page.hasBorder()) {
            BookContentScreen.drawFromTexture(poseStack, this.getPage().getBook(), x, y, 405, 149, 106, 106);
        }

        if (this.page.getImages().length > 1 && this.page.hasBorder()) {
            int xs = x + 83;
            int ys = y + 92;
            GuiComponent.fill(poseStack, xs, ys, xs + 20, ys + 11, 0x44000000);
            GuiComponent.fill(poseStack, xs - 1, ys - 1, xs + 20, ys + 11, 0x44000000);
        }


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
        return 115;
    }
}
