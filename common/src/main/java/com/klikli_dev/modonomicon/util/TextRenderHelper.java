/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class TextRenderHelper {

    /**
     * drawString for rendering at float coordinates.
     */
    public static void drawString(GuiGraphicsExtractor guiGraphics, Font font, Component component, float x, float y, int color, boolean drawShadow) {
        int x1i = Mth.floor(x);
        int y1i = Mth.floor(y);
        float x1f = x - x1i;
        float y1f = y - y1i;
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x1f, y1f);
        guiGraphics.text(font, component, x1i, y1i, color, drawShadow);
        guiGraphics.pose().popMatrix();
    }

    /**
     * drawString for rendering at float coordinates.
     */
    public static void drawString(GuiGraphicsExtractor guiGraphics, Font font, String string, float x, float y, int color, boolean drawShadow) {
        int x1i = Mth.floor(x);
        int y1i = Mth.floor(y);
        float x1f = x - x1i;
        float y1f = y - y1i;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x1f, y1f);
        guiGraphics.text(font, string, x1i, y1i, color, drawShadow);
        guiGraphics.pose().popMatrix();
    }

    /**
     * drawString for rendering at float coordinates.
     */
    public static void drawString(GuiGraphicsExtractor guiGraphics, Font font, FormattedCharSequence string, float x, float y, int color, boolean drawShadow) {
        int x1i = Mth.floor(x);
        int y1i = Mth.floor(y);
        float x1f = x - x1i;
        float y1f = y - y1i;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x1f, y1f);
        guiGraphics.text(font, string, x1i, y1i, color, drawShadow);
        guiGraphics.pose().popMatrix();
    }
}

