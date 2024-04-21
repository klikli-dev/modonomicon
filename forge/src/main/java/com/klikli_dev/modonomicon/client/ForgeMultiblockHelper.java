// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.client.render.FluidBlockVertexConsumer;
import com.klikli_dev.modonomicon.platform.services.MultiblockHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class ForgeMultiblockHelper implements MultiblockHelper {
    @Override
    public void renderBlock(BlockState state, BlockPos pos, Multiblock multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand) {
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();

        var fluidState = state.getFluidState();
        if (!fluidState.isEmpty()) {
            var layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
            var buffer = buffers.getBuffer(layer);
            blockRenderer.renderLiquid(pos, multiblock, new FluidBlockVertexConsumer(buffer, ps, pos), state, fluidState);
        }

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            var model = blockRenderer.getBlockModel(state);
            for (var layer : model.getRenderTypes(state, rand, ModelData.EMPTY)) {
                var buffer = buffers.getBuffer(layer);
                blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, false, rand, ModelData.EMPTY, layer);
            }

        }
    }
}
