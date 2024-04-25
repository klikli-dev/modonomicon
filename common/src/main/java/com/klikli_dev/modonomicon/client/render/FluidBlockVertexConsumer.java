/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;

public record FluidBlockVertexConsumer(VertexConsumer prior, PoseStack pose, BlockPos pos) implements VertexConsumer {

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        final float dx = this.pos.getX() & 15;
        final float dy = this.pos.getY() & 15;
        final float dz = this.pos.getZ() & 15;
        return this.prior.vertex(this.pose.last().pose(), (float) x - dx, (float) y - dy, (float) z - dz);
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        return this.prior.color(r, g, b, a);
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        return this.prior.uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return this.prior.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return this.prior.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return this.prior.normal(this.pose.last(), x, y, z);
    }

    @Override
    public void endVertex() {
        this.prior.endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
        this.prior.defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor() {
        this.prior.unsetDefaultColor();
    }
}
