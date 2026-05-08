/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.pip;

import com.klikli_dev.modonomicon.client.render.state.pip.GuiDirectEntryConnectionRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class GuiDirectEntryConnectionRenderer extends PictureInPictureRenderer<GuiDirectEntryConnectionRenderState> {
    public GuiDirectEntryConnectionRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<GuiDirectEntryConnectionRenderState> getRenderStateClass() {
        return GuiDirectEntryConnectionRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiDirectEntryConnectionRenderState state, PoseStack poseStack) {
        VertexConsumer buffer = this.bufferSource.getBuffer(RenderTypes.linesTranslucent());
        PoseStack.Pose pose = poseStack.last();

        for (var connection : state.connections()) {
            this.drawLine(buffer, pose, connection, state.animationTime(), state.lineWidth(), state.opacity(), state.brightness(), state.oscillationAmplitude(), state.oscillationSpeed());
        }
    }

    private void drawLine(VertexConsumer buffer, PoseStack.Pose pose, GuiDirectEntryConnectionRenderState.Connection connection, float time, float lineWidth, float opacity, float brightness, float oscillationAmplitude, float oscillationSpeed) {
        double d3 = connection.startX() - connection.endX();
        double d4 = connection.startY() - connection.endY();
        float dist = Mth.sqrt((float) (d3 * d3 + d4 * d4));
        int inc = Math.max(1, (int) (dist / 2.0F));
        float dx = (float) (d3 / inc);
        float dy = (float) (d4 / inc);
        boolean dominantX = Math.abs(d3) > Math.abs(d4);
        if (dominantX) {
            dx *= 2.0F;
        } else {
            dy *= 2.0F;
        }
        float decayFactor = 1.0F - 1.0F / inc * 3.0F / 2.0F;
        float currentX = connection.startX();
        float currentY = connection.startY();

        float previousX = 0.0F;
        float previousY = 0.0F;
        int previousColor = 0;
        boolean hasPrevious = false;

        for (int a = 0; a <= inc; a++) {
            float phase = (float) a / inc;
            float mx = 0.0F;
            float my = 0.0F;
            float alpha = 0.6F;
            int color = connection.color();

            if (connection.wiggle()) {
                mx = Mth.sin((time * oscillationSpeed + a) / 7.0F) * oscillationAmplitude * (1.0F - phase);
                my = Mth.sin((time * oscillationSpeed + a) / 5.0F) * oscillationAmplitude * (1.0F - phase);
                color = ARGB.scaleRGB(color, 1.0F - phase);
                alpha *= phase;
            }

            color = this.applyVisibility(color, alpha, opacity, brightness);

            float x = (a == inc ? connection.endX() : currentX) + mx;
            float y = (a == inc ? connection.endY() : currentY) + my;

            if (hasPrevious) {
                this.drawSegment(buffer, pose, previousX, previousY, x, y, previousColor, color, lineWidth);
            }

            previousX = x;
            previousY = y;
            previousColor = color;
            hasPrevious = true;

            currentX -= dx;
            currentY -= dy;

            if (dominantX) {
                dx *= decayFactor;
            } else {
                dy *= decayFactor;
            }
        }
    }

    private int applyVisibility(int color, float alpha, float opacity, float brightness) {
        float baseAlpha = ARGB.alphaFloat(color);
        int rgb = ARGB.transparent(color);
        if (brightness != 1.0F) {
            rgb = ARGB.scaleRGB(rgb, brightness, brightness, brightness);
        }
        return ARGB.color(Math.min(alpha * baseAlpha * opacity, 1.0F), rgb);
    }

    private void drawSegment(VertexConsumer buffer, PoseStack.Pose pose, float startX, float startY, float endX, float endY, int startColor, int endColor, float lineWidth) {
        float nx = endX - startX;
        float ny = endY - startY;
        float length = Mth.sqrt(nx * nx + ny * ny);
        if (length <= 0.0001F) {
            return;
        }

        nx /= length;
        ny /= length;

        buffer.addVertex(pose, startX, startY, 0.0F)
                .setColor(startColor)
                .setNormal(pose, nx, ny, 0.0F)
                .setLineWidth(lineWidth);
        buffer.addVertex(pose, endX, endY, 0.0F)
                .setColor(endColor)
                .setNormal(pose, nx, ny, 0.0F)
                .setLineWidth(lineWidth);
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "direct_entry_connections";
    }
}
