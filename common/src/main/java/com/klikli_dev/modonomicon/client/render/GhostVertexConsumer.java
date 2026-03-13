/*
 * SPDX-FileCopyrightText: 2025 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public final class GhostVertexConsumer extends VertexConsumerWrapper {
    private static final Map<VertexConsumer, VertexConsumer> remappedVertexConsumers = new IdentityHashMap<>();

    private final int alpha;
    private final int white;

    public GhostVertexConsumer(VertexConsumer wrapped, int alpha) {
        super(wrapped);
        this.alpha = alpha;
        this.white = ARGB.color(alpha, 0xFFFFFF);
    }

    @Override
    public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
        this.parent.addVertex(x, y, z, ARGB.multiply(this.white, color), u, v, overlay, light, nx, ny, nz);
    }

    @Override
    public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this.parent.setColor(red, green, blue, (alpha * this.alpha) / 0xFF);
    }

    @Override
    public VertexConsumer setColor(int color) {
        return this.parent.setColor(ARGB.multiply(this.alpha, color));
    }

    @Override
    public VertexConsumer setLineWidth(float f) {
        return this.parent.setLineWidth(f);
    }
}