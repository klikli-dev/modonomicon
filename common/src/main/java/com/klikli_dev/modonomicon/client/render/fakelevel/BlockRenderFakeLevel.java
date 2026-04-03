/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.fakelevel;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

/**
 * Adapted from FramedBlocks' BlockRenderFakeLevel.
 * Source: https://github.com/XFactHD/FramedBlocks/blob/26.1/src/main/java/io/github/xfacthd/framedblocks/api/render/fakelevel/BlockRenderFakeLevel.java
 */
public interface BlockRenderFakeLevel extends BlockAndTintGetter {

    BlockPos pos();

    BlockState state();

    @Override
    default BlockState getBlockState(BlockPos pos) {
        if (pos.equals(pos())) {
            return state();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    default @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    default FluidState getFluidState(BlockPos pos) {
        return getBlockState(pos).getFluidState();
    }

    @Override
    default int getBrightness(LightLayer layer, BlockPos pos) {
        return LightEngine.MAX_LEVEL;
    }

    @Override
    default int getRawBrightness(BlockPos pos, int ambientDarkening) {
        return LightEngine.MAX_LEVEL - ambientDarkening;
    }
}
