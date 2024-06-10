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
    public VertexConsumer addVertex(float x, float y, float z) {
        final float dx = this.pos.getX() & 15;
        final float dy = this.pos.getY() & 15;
        final float dz = this.pos.getZ() & 15;
        return this.prior.addVertex(this.pose.last().pose(), (float) x - dx, (float) y - dy, (float) z - dz);
    }

    @Override
    public VertexConsumer setColor(int i, int j, int k, int l) {
        return this.prior.setColor(i, j, k, l);
    }

    @Override
    public VertexConsumer setUv(float f, float g) {
        return this.prior.setUv(f, g);
    }

    @Override
    public VertexConsumer setUv1(int i, int j) {
        return this.prior.setUv1(i, j);
    }

    @Override
    public VertexConsumer setUv2(int i, int j) {
        return this.prior.setUv2(i, j);
    }

    @Override
    public VertexConsumer setNormal(float f, float g, float h) {
        return this.prior.setNormal(f, g, h);
    }
}
