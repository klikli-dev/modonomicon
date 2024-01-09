// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.platform.services;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface MultiblockHelper {
    void renderBlock(BlockState state, BlockPos pos, Multiblock multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand);
}
