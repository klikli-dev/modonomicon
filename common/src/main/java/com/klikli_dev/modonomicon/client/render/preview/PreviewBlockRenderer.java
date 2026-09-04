/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.render.GhostVertexConsumer;
import com.klikli_dev.modonomicon.client.render.fakelevel.GhostRenderState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Meshes the ghost blocks of a {@link PreviewSnapshot} into a transient buffer and draws them.
 * <p>
 * Tessellation logic moved verbatim from the former {@code MultiblockPreviewRenderer} monolith.
 * See <a href="https://github.com/XFactHD/FramedBlocks/blob/26.1/src/main/java/io/github/xfacthd/framedblocks/client/render/special/GhostBlockRenderer.java">FramedBlocks' GhostBlockRenderer</a>
 * for the architecture this is adapted from.
 * </p>
 */
public final class PreviewBlockRenderer {

    private PreviewBlockRenderer() {
    }

    /**
     * Renders every not-yet-built relevant position of the snapshot as a translucent ghost block.
     * Already correct blocks are skipped (the world itself shows them).
     */
    public static void render(PreviewSnapshot snapshot, Level level, PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();

        RenderType movingBlockRenderType = RenderTypes.translucentMovingBlock();
        RenderPipeline pipeline = movingBlockRenderType.pipeline();
        BufferBuilder buffer = new BufferBuilder(PreviewGhostBuffers.bufferBuilder(), pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
        ModelBlockRenderer blockRenderer = new ModelBlockRenderer(mc.options.ambientOcclusion().get(), false, mc.getBlockColors());

        for (var r : snapshot.visibleResults()) {
            if (!LayerFilter.isPreviewRelevant(r)) {
                continue;
            }
            if (r.test(level, snapshot.effectiveRotation())) {
                continue;
            }

            float alpha = PreviewConstants.BLOCK_ALPHA;
            if (r.worldPosition().equals(snapshot.checkPos())) {
                alpha = PreviewConstants.LOOKING_BASE_ALPHA
                        + (float) (Math.sin(ClientTicks.total * PreviewConstants.LOOKING_PULSE_SPEED) + 1F)
                        * PreviewConstants.LOOKING_PULSE_AMPLITUDE;
            }

            BlockState displayedState = r.stateMatcher().getDisplayedState(ClientTicks.ticks).rotate(snapshot.effectiveRotation());
            renderBlock(level, displayedState, r.worldPosition(), alpha, blockRenderer, poseStack, buffer);
        }

        MeshData meshData = buffer.build();
        if (meshData != null) {
            try (meshData) {
                PreviewGpuUploader.uploadAndDraw(pipeline, mc.getMainRenderTarget(), meshData);
            }
        }
    }

    public static void renderBlock(Level world, BlockState state, BlockPos pos, float alpha, ModelBlockRenderer blockRenderer, PoseStack poseStack, BufferBuilder buffer) {
        if (pos == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (!(world instanceof ClientLevel clientLevel)) {
            return;
        }
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().position();
        Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(cameraPos);

        VertexConsumer consumer = new GhostVertexConsumer(buffer, (int) (alpha * 255.0f));
        BlockQuadOutput output = (levelIn, stateIn, posIn, quad, instance) ->
                consumer.putBakedQuad(poseStack.last(), quad, instance);

        poseStack.pushPose();
        poseStack.translate(offset.x + .5, offset.y + .5, offset.z + .5);

        if (state.getBlock() == Blocks.AIR) {
            //HACK / WHY: air has no model, so mark must-stay-empty spots with a small red cube.
            float scale = PreviewConstants.AIR_PLACEHOLDER_SCALE;
            poseStack.scale(scale, scale, scale);
            state = Blocks.RED_CONCRETE.defaultBlockState();
        } else {
            //HACK / WHY: slight upscale to avoid z-fighting with real blocks at the same position.
            poseStack.scale(PreviewConstants.GHOST_SCALE, PreviewConstants.GHOST_SCALE, PreviewConstants.GHOST_SCALE);
        }

        poseStack.translate(-.5F, -.5F, -.5F);

        BlockStateModel model = mc.getModelManager().getBlockStateModelSet().get(state);
        GhostRenderState renderState = new GhostRenderState(clientLevel, pos, state);
        blockRenderer.tesselateBlock(output, 0, 0, 0, renderState, pos, state, model, 0);

        poseStack.popPose();
    }
}
