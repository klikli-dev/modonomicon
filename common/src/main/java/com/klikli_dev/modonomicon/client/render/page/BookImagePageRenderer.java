/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.ImageDisplayMode;
import com.klikli_dev.modonomicon.book.ImageScaleMode;
import com.klikli_dev.modonomicon.book.page.BookImagePage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.button.SmallArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class BookImagePageRenderer extends BookPageRenderer<BookImagePage> implements PageWithTextRenderer {

    private static final int FRAME_PADDING = 3;

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

        if (this.page.getDisplayMode() == ImageDisplayMode.SMALL) {
            var prevPos = getButtonPosition();
            var nextPos = getSmallNextButtonPosition();
            this.addButton(new SmallArrowButton(parentScreen, prevPos[0], prevPos[1], true, () -> this.index > 0, this::handleButtonArrow));
            this.addButton(new SmallArrowButton(parentScreen, nextPos[0], nextPos[1], false, () -> this.index < this.page.getImages().length - 1, this::handleButtonArrow));
        } else {
            var pos = getButtonPosition();
            this.addButton(new SmallArrowButton(parentScreen, pos[0], pos[1], true, () -> this.index > 0, this::handleButtonArrow));
            this.addButton(new SmallArrowButton(parentScreen, pos[0] + 10, pos[1], false, () -> this.index < this.page.getImages().length - 1, this::handleButtonArrow));
        }
    }

    /**
     * Returns [x, y] for the left arrow button based on the display mode.
     */
    private int[] getButtonPosition() {
        var imageBounds = getImageBounds();
        var frameBounds = getFrameBounds();
        if (this.page.getDisplayMode() == ImageDisplayMode.SMALL) {
            return new int[]{imageBounds.x - 5 - SmallArrowButton.WIDTH, imageBounds.y + imageBounds.height - SmallArrowButton.HEIGHT};
        }
        return new int[]{frameBounds.x + frameBounds.width - 21, frameBounds.y + frameBounds.height - 12};
    }

    /**
     * Returns [x, y] for the next arrow button in small mode (right side of image).
     */
    private int[] getSmallNextButtonPosition() {
        var imageBounds = getImageBounds();
        return new int[]{imageBounds.x + imageBounds.width + 5, imageBounds.y + imageBounds.height - SmallArrowButton.HEIGHT};
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float ticks) {
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
        }

        var textY = this.getTextY();
        this.renderBookTextHolder(guiGraphics, this.getPage().getText(), 0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);

        var imageBounds = getImageBounds();
        var frameBounds = getFrameBounds();

        if (this.page.getImageScaleMode() == ImageScaleMode.ACTUAL_SIZE) {
            renderActualSize(guiGraphics, imageBounds);
        } else {
            renderScaleToFit(guiGraphics, imageBounds);
        }

        if (this.page.hasBorder()) {
            BookContentRenderer.drawSpriteRegion(guiGraphics, this.getPage().getBook().theme().content().mediaFrame(), frameBounds.x, frameBounds.y, frameBounds.width, frameBounds.height);
        }

        if (this.page.getImages().length > 1 && this.page.hasBorder() && this.page.getDisplayMode() != ImageDisplayMode.SMALL) {
            var buttonPos = getButtonPosition();
            int xs = buttonPos[0] - 2;
            int ys = buttonPos[1] - 3;
            guiGraphics.fill(xs, ys, xs + 20, ys + 11, 0x44000000);
            guiGraphics.fill(xs - 1, ys - 1, xs + 20, ys + 11, 0x44000000);
        }


        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
    }

    /**
     * Renders the image scaled to fit the display area (current behavior).
     */
    private void renderScaleToFit(GuiGraphicsExtractor guiGraphics, ImageBounds imageBounds) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(0.5F, 0.5F);

        int renderX = imageBounds.x * 2;
        int renderY = imageBounds.y * 2;
        int renderW = imageBounds.width * 2;
        int renderH = imageBounds.height * 2;

        if (this.page.useLegacyRendering()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getImages()[this.index], renderX, renderY, 0, 0, renderW, renderH, 256, 256);
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getImages()[this.index], renderX, renderY, 0, 0, renderW, renderH, renderW, renderH, renderW, renderH);
        }

        guiGraphics.pose().scale(2F, 2F);
        guiGraphics.pose().popMatrix();
    }

    /**
     * Renders the image at its actual pixel size, without scaling.
     * The image is rendered from the top-left corner of the display area.
     */
    private void renderActualSize(GuiGraphicsExtractor guiGraphics, ImageBounds imageBounds) {
        int actualX = imageBounds.x;
        int actualY = imageBounds.y;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.page.getImages()[this.index], actualX, actualY, 0, 0, imageBounds.width * 2, imageBounds.height * 2, imageBounds.width * 2, imageBounds.height * 2);
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
        var frame = getFrameBounds();
        return frame.y + frame.height + 2;
    }

    /**
     * Returns the position and size of the image content area based on the display mode.
     * All values are in logical (pre-0.5-scale) coordinates.
     * <p>
     * DEFAULT: 100x100 (200x200 actual)
     * WIDE: 100x50 (200x100 actual)
     * SMALL: 25x25 (50x50 actual) - 1/8th the area of default
     */
    private ImageBounds getImageBounds() {
        return switch (this.page.getDisplayMode()) {
            case DEFAULT -> new ImageBounds(12, 12, 100, 100);
            case WIDE -> new ImageBounds(12, 12, 100, 50);
            case SMALL -> new ImageBounds(49, 12, 25, 25);
        };
    }

    /**
     * Returns the position and size of the frame (image + border padding).
     * The frame encloses the image content area with FRAME_PADDING on each side.
     */
    private ImageBounds getFrameBounds() {
        var img = getImageBounds();
        return new ImageBounds(
                img.x - FRAME_PADDING,
                img.y - FRAME_PADDING,
                img.width + FRAME_PADDING * 2,
                img.height + FRAME_PADDING * 2
        );
    }

    private static final class ImageBounds {
        final int x;
        final int y;
        final int width;
        final int height;

        ImageBounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
