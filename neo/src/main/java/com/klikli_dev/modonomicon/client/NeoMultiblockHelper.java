// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.client.render.FluidBlockVertexConsumer;
import com.klikli_dev.modonomicon.client.render.GhostVertexConsumer;
import com.klikli_dev.modonomicon.platform.services.MultiblockHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;

public class NeoMultiblockHelper implements MultiblockHelper {
    @Override
    public void renderBlock(BlockState state, BlockPos pos, Multiblock multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand) {
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();

        //TODO: Fluid rendering
//        var fluidState = state.getFluidState();
//        if (!fluidState.isEmpty()) {
//            var layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
//            layer.
//            var buffer = buffers.getBuffer(layer);
//            //TODO see SectionCompiler#compile to see how to get the buffer from the layer
//            blockRenderer.renderLiquid(pos, multiblock, new FluidBlockVertexConsumer(buffer, ps, pos), state, fluidState);
//        }

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
//            var model = blockRenderer.getBlockModel(state);
//            for (var layer : model.getRenderTypes(state, rand, ModelData.EMPTY)) {
//                var buffer = buffers.getBuffer(layer);
//                blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, false, rand, ModelData.EMPTY, layer);
//            }

            //noinspection deprecation
            blockRenderer.renderSingleBlock(state, ps, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }
}
