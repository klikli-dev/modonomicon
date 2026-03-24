/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookImagePage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.button.SmallArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;

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
    public void onBeginDisplayPage(BookEntryScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);

        int x = 94;
        int y = 101;

        this.addButton(new SmallArrowButton(parentScreen, x, y, true, () -> this.index > 0, this::handleButtonArrow));
        this.addButton(new SmallArrowButton(parentScreen, x + 10, y, false, () -> this.index < this.page.getImages().length - 1, this::handleButtonArrow));
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
        }

        var textY = this.getTextY();
        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);

        int x = BookEntryScreen.PAGE_WIDTH / 2 - 53;
        int y = 7;
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(0.5F, 0.5F);
        if (this.page.useLegacyRendering())
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getImages()[this.index], x * 2 + 6, y * 2 + 6, 0, 0, 200, 200, 256, 256);
        else
            //TODO: look into this once parchment is up to date. Not sure why we have to add the height + width 3 times instead of 2.
            //from experiments in bookicon it seems that the first set of parameters after the "0, 0" is the render size, the second and third are then the size in the texture and the texture size.
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getImages()[this.index], x * 2 + 6, y * 2 + 6, 0, 0, 200, 200, 200, 200, 200, 200);

        guiGraphics.pose().scale(2F, 2F);
        guiGraphics.pose().popMatrix();

        if (this.page.hasBorder()) {
            BookContentRenderer.drawFromContentTexture(RenderPipelines.GUI_TEXTURED, guiGraphics, this.getPage().getBook(), x, y, 405, 149, 106, 106);
        }

        if (this.page.getImages().length > 1 && this.page.hasBorder()) {
            int xs = x + 83;
            int ys = y + 92;
            guiGraphics.fill(xs, ys, xs + 20, ys + 11, 0x44000000);
            guiGraphics.fill(xs - 1, ys - 1, xs + 20, ys + 11, 0x44000000);
        }


        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            if (this.page.hasTitle()) {
                var titleStyle = this.getClickedComponentStyleAtForTitle(this.page.getTitle(), BookEntryScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
                if (titleStyle != null) {
                    return titleStyle;
                }
            }

            var bounds = this.getBookTextHolderBounds(0, this.getTextY(), BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - this.getTextY());
            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), bounds.x, bounds.y, bounds.width, bounds.height, pMouseX, pMouseY);
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
