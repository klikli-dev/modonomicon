/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.state.pip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GuiDirectEntryConnectionRenderState(
        List<Connection> connections,
        float animationTime,
        float lineWidth,
        float visibilityMultiplier,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {

    public GuiDirectEntryConnectionRenderState(
            List<Connection> connections,
            float animationTime,
            float lineWidth,
            float visibilityMultiplier,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            @Nullable ScreenRectangle scissorArea
    ) {
        this(connections, animationTime, lineWidth, visibilityMultiplier, x0, y0, x1, y1, scale, scissorArea,
                PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }

    public record Connection(float startX, float startY, float endX, float endY, int color, boolean wiggle) {
    }
}
