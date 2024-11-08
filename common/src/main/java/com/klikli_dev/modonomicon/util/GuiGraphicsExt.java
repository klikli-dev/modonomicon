/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class GuiGraphicsExt {


    /**
     * drawString for rendering at float coordinates.
     */
    public static int drawString(GuiGraphics guiGraphics, Font font, @Nullable Component component, float x, float y, int color, boolean drawShadow) {
        if (component == null) {
            return 0;
        } else {
            AtomicInteger i = new AtomicInteger();
            guiGraphics.drawSpecial(bufferSource -> {
                i.set(font.drawInBatch(component, x, y, color, drawShadow, guiGraphics.pose().last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880));
            });
            return i.get();
        }
    }

    /**
     * drawString for rendering at float coordinates.
     */
    public static int drawString(GuiGraphics guiGraphics, Font font, @Nullable String string, float x, float y, int color, boolean drawShadow) {
        if (string == null) {
            return 0;
        } else {
            AtomicInteger i = new AtomicInteger();
            guiGraphics.drawSpecial(bufferSource -> {
                i.set(font.drawInBatch(string, x, y, color, drawShadow, guiGraphics.pose().last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880));
            });
            return i.get();
        }
    }

    /**
     * drawString for rendering at float coordinates.
     */
    public static int drawString(GuiGraphics guiGraphics, Font font, @Nullable FormattedCharSequence string, float x, float y, int color, boolean drawShadow) {
        if (string == null) {
            return 0;
        } else {
            AtomicInteger i = new AtomicInteger();
            guiGraphics.drawSpecial(bufferSource -> {
                i.set(font.drawInBatch(string, x, y, color, drawShadow, guiGraphics.pose().last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880));
            });
            return i.get();
        }
    }


    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square)
     * based on a fixed size textured box with continuous borders and filler.
     * See Forge IForgeGuiGraphics
     *
     * @param texture       the ResourceLocation object that contains the desired image
     * @param function      the render type function
     * @param x             x-axis offset
     * @param y             y-axis offset
     * @param u             bound resource location image x offset
     * @param v             bound resource location image y offset
     * @param width         the desired box width
     * @param height        the desired box height
     * @param textureWidth  the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param borderSize    the size of the box's borders
     */
    public static void blitWithBorder(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> function, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize) {
        blitWithBorder(guiGraphics, function, texture, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize);
    }

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square)
     * based on a fixed size textured box with continuous borders and filler.
     * See Forge IForgeGuiGraphics
     *
     * @param texture       the ResourceLocation object that contains the desired image
     * @param function      the render type function
     * @param x             x-axis offset
     * @param y             y-axis offset
     * @param u             bound resource location image x offset
     * @param v             bound resource location image y offset
     * @param width         the desired box width
     * @param height        the desired box height
     * @param textureWidth  the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param topBorder     the size of the box's top border
     * @param bottomBorder  the size of the box's bottom border
     * @param leftBorder    the size of the box's left border
     * @param rightBorder   the size of the box's right border
     */
    public static void blitWithBorder(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> function, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder) {
        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        guiGraphics.blit(function, texture, x, y, u, v, leftBorder, topBorder, textureWidth, textureHeight);
        // Top Right
        guiGraphics.blit(function, texture, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, textureWidth, textureHeight);
        // Bottom Left
        guiGraphics.blit(function, texture, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, textureWidth, textureHeight);
        // Bottom Right
        guiGraphics.blit(function, texture, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, textureWidth, textureHeight);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
            // Top Border
            guiGraphics.blit(function, texture, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder, textureWidth, textureHeight);
            // Bottom Border
            guiGraphics.blit(function, texture, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, textureWidth, textureHeight);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
                guiGraphics.blit(function, texture, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), textureWidth, textureHeight);
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
            // Left Border
            guiGraphics.blit(function, texture, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight), textureWidth, textureHeight);
            // Right Border
            guiGraphics.blit(function, texture, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), textureWidth, textureHeight);
        }
    }

}
