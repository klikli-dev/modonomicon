/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.fakelevel;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.lighting.LevelLightEngine;

/**
 * Adapted from FramedBlocks' DelegatingBlockRenderFakeLevel.
 * Source: https://github.com/XFactHD/FramedBlocks/blob/26.1/src/main/java/io/github/xfacthd/framedblocks/api/render/fakelevel/DelegatingBlockRenderFakeLevel.java
 */
public interface DelegatingBlockRenderFakeLevel extends BlockRenderFakeLevel {

    BlockAndTintGetter realLevel();

    @Override
    default LevelLightEngine getLightEngine() {
        return realLevel().getLightEngine();
    }

    @Override
    default CardinalLighting cardinalLighting() {
        return realLevel().cardinalLighting();
    }

    @Override
    default int getBlockTint(BlockPos pos, ColorResolver resolver) {
        if (pos.equals(pos())) {
            return realLevel().getBlockTint(pos, resolver);
        }
        return -1;
    }

    @Override
    default int getHeight() {
        return realLevel().getHeight();
    }

    @Override
    default int getMinY() {
        return realLevel().getMinY();
    }
}
