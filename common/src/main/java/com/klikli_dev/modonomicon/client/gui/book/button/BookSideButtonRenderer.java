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
    private static final int ICON_INSET_X = 8;
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
        int scissorY = screenHeight - widgetY - widgetHeight - 1;

        if (hovered) {
            renderX += 1;
            scissorWidth -= 1;
        }

        guiGraphics.enableScissor(absoluteScissorX, scissorY, absoluteScissorX + scissorWidth, scissorY + 1000);
        background.extractRenderState(guiGraphics, renderX, widgetY, widgetWidth, widgetHeight);
        renderRightIcon(guiGraphics, icon, renderX, widgetY, widgetWidth);
        guiGraphics.disableScissor();

        guiGraphics.pose().popMatrix();
    }

    public static GuiSprite collectionIconOrEmpty(GuiSprite background, GuiSprite expectedCollectionBackground, GuiSprite icon) {
        return background.sprite().equals(expectedCollectionBackground.sprite()) ? icon : GuiSprite.EMPTY;
    }

    public static void renderRightIcon(GuiGraphicsExtractor guiGraphics, GuiSprite icon, int buttonX, int buttonY, int buttonWidth) {
        if (icon.isEmpty()) {
            return;
        }

        int iconX = buttonX + buttonWidth - ICON_INSET_X - icon.width();
        int iconY = buttonY + ICON_INSET_Y + Math.max(0, (16 - icon.height()) / 2);
        icon.extractRenderState(guiGraphics, iconX, iconY);
    }

    public static void renderLeftIcon(GuiGraphicsExtractor guiGraphics, GuiSprite icon, int buttonX, int buttonY) {
        if (icon.isEmpty()) {
            return;
        }

        int iconY = buttonY + ICON_INSET_Y + Math.max(0, (16 - icon.height()) / 2);
        icon.extractRenderState(guiGraphics, buttonX + ICON_INSET_X, iconY);
    }
}
