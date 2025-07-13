/*
 * SPDX-FileCopyrightText: 2025 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;

public abstract class VertexConsumerWrapper implements VertexConsumer {
    protected final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull VertexConsumer addVertex(float x, float y, float z) {
        this.parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setColor(int r, int g, int b, int a) {
        this.parent.setColor(r, g, b, a);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv(float u, float v) {
        this.parent.setUv(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv1(int u, int v) {
        this.parent.setUv1(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv2(int u, int v) {
        this.parent.setUv2(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setNormal(float x, float y, float z) {
        this.parent.setNormal(x, y, z);
        return this;
    }
}
