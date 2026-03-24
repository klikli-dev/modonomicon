/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class GuiGraphicsExt {

    public static void drawTiledSprite(GuiGraphicsExtractor guiGraphics, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite, int posX, int posY) {


        SpriteContents spriteContents = sprite.contents();
        GuiSpriteScaling.Tile tileScaling = new GuiSpriteScaling.Tile(spriteContents.width(), spriteContents.height());

        posY = posY + tiledHeight - scaledAmount;

        guiGraphics.enableScissor(posX, posY, posX + tiledWidth, posY + scaledAmount);
        {
            guiGraphics.blitTiledSprite(
                    RenderPipelines.GUI_TEXTURED,
                    sprite,
                    posX,
                    posY,
                    tiledWidth,
                    scaledAmount,
                    0,
                    0,
                    tileScaling.width(),
                    tileScaling.height(),
                    tileScaling.width(),
                    tileScaling.height(),
                    color
            );
        }
        guiGraphics.disableScissor();
    }

    /**
     * drawString for rendering at float coordinates.
     */
    public static void drawString(GuiGraphicsExtractor guiGraphics, Font font, Component component, float x, float y, int color, boolean drawShadow) {
            int x1i = Mth.floor(x);
            int y1i = Mth.floor(y);
            float x1f = x-x1i;
            float y1f = y-y1i;

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
        float x1f = x-x1i;
        float y1f = y-y1i;

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
        float x1f = x-x1i;
        float y1f = y-y1i;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x1f, y1f);
        guiGraphics.text(font, string, x1i, y1i, color, drawShadow);
        guiGraphics.pose().popMatrix();
    }


    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square)
     * based on a fixed size textured box with continuous borders and filler.
     * See Forge IForgeGuiGraphics
     *
     * @param texture       the Identifier object that contains the desired image
     * @param pipeline      the render pipeline
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
    public static void blitWithBorder(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize) {
        blitWithBorder(guiGraphics, pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize);
    }

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square)
     * based on a fixed size textured box with continuous borders and filler.
     * See Forge IForgeGuiGraphics
     *
     * @param texture       the Identifier object that contains the desired image
     * @param pipeline      the render pipeline
     * @param x             x-axis offset
     * @param y             y-axis offset
     * @param u             bound resource location image x offset
     * @param v             bound resource location image y offset
     * @param width         the desired box width
     * @param height        the desired box height
     * @param maxU          the width of the box texture in the resource location image
     * @param maxV          the height of the box texture in the resource location image
     * @param topBorder     the size of the box's top border
     * @param bottomBorder  the size of the box's bottom border
     * @param leftBorder    the size of the box's left border
     * @param rightBorder   the size of the box's right border
     */
    public static void blitWithBorder(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, Identifier texture, int x, int y, int u, int v, int width, int height, int maxU, int maxV, int topBorder, int bottomBorder, int leftBorder, int rightBorder) {
        int fillerWidth = maxU - leftBorder - rightBorder;
        int fillerHeight = maxV - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        guiGraphics.blit(pipeline, texture, x, y, u, v, leftBorder, topBorder, 256, 256);
        // Top Right
        guiGraphics.blit(pipeline, texture, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, 256, 256);
        // Bottom Left
        guiGraphics.blit(pipeline, texture, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, 256, 256);
        // Bottom Right
        guiGraphics.blit(pipeline, texture, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, 256, 256);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
            // Top Border
            guiGraphics.blit(pipeline, texture, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder, 256, 256);
            // Bottom Border
            guiGraphics.blit(pipeline, texture, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, 256, 256);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
                guiGraphics.blit(pipeline, texture, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), 256, 256);
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
            // Left Border
            guiGraphics.blit(pipeline, texture, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight), 256, 256);
            // Right Border
            guiGraphics.blit(pipeline, texture, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), 256, 256);
        }
    }

}
