/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.button;

import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class BookSideButtonRenderer {

    public static final int BUTTON_SLIDE_OFFSET = 16;
    private static final int ICON_INSET_X = 11;
    private static final int ICON_INSET_Y = 2;

    private BookSideButtonRenderer() {
    }

    public static void renderSlidingButton(GuiGraphicsExtractor guiGraphics, int xOffset, int widgetX, int widgetY, int widgetWidth,
                                           int widgetHeight, int scissorX, int screenHeight, boolean hovered,
                                           GuiSprite background, GuiSprite icon) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(xOffset, 0);

        int absoluteScissorX = scissorX + xOffset;
        int renderX = widgetX - BUTTON_SLIDE_OFFSET;
        int scissorWidth = widgetWidth + (widgetX - scissorX);
        int scissorY = widgetY;
        int scissorHeight = widgetHeight;

        if (hovered) {
            renderX += 1;
            scissorWidth -= 1;
        }

        guiGraphics.enableScissor(absoluteScissorX, scissorY, absoluteScissorX + scissorWidth - 1, scissorY + scissorHeight - 1);
        background.extractRenderState(guiGraphics, renderX, widgetY, widgetWidth, widgetHeight);
        renderRightIcon(guiGraphics, icon, renderX, widgetY, widgetWidth);
        guiGraphics.disableScissor();

        guiGraphics.pose().popMatrix();
    }

    public static GuiSprite collectionIconOrEmpty(GuiSprite background, GuiSprite expectedCollectionBackground, GuiSprite icon) {
        return background.sprite().equals(expectedCollectionBackground.sprite()) ? icon : GuiSprite.EMPTY;
    }

    public static int anchoredButtonX(int scissorX) {
        return scissorX;
    }

    public static void renderRightIcon(GuiGraphicsExtractor guiGraphics, GuiSprite icon, int buttonX, int buttonY, int buttonWidth) {
        renderRightIcon(guiGraphics, icon, buttonX, buttonY, buttonWidth, 1.0f, 0);
    }

    public static void renderRightIcon(GuiGraphicsExtractor guiGraphics, GuiSprite icon, int buttonX, int buttonY, int buttonWidth, float scale) {
        renderRightIcon(guiGraphics, icon, buttonX, buttonY, buttonWidth, scale, 0);
    }

    public static void renderRightIcon(GuiGraphicsExtractor guiGraphics, GuiSprite icon, int buttonX, int buttonY, int buttonWidth, float scale, int xOffset) {
        if (icon.isEmpty()) {
            return;
        }

        int iconWidth = Math.max(1, Math.round(icon.width() * scale));
        int iconHeight = Math.max(1, Math.round(icon.height() * scale));
        int iconX = buttonX + buttonWidth - ICON_INSET_X - iconWidth + xOffset;
        int iconY = buttonY + ICON_INSET_Y + Math.max(0, (16 - iconHeight) / 2);
        icon.extractRenderState(guiGraphics, iconX, iconY, iconWidth, iconHeight);
    }

    public static void renderLeftIcon(GuiGraphicsExtractor guiGraphics, GuiSprite icon, int buttonX, int buttonY) {
        if (icon.isEmpty()) {
            return;
        }

        int iconY = buttonY + ICON_INSET_Y + Math.max(0, (16 - icon.height()) / 2);
        icon.extractRenderState(guiGraphics, buttonX + ICON_INSET_X, iconY);
    }
}
