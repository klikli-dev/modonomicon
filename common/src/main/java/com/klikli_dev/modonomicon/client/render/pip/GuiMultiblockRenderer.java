/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.pip;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.render.fakelevel.GhostRenderState;
import com.klikli_dev.modonomicon.client.render.state.pip.GuiMultiblockRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

public class GuiMultiblockRenderer extends PictureInPictureRenderer<GuiMultiblockRenderState> {

    MultiBufferSource.BufferSource bufferSource;

    public GuiMultiblockRenderer(MultiBufferSource.BufferSource bufferSource) {
        super();

        this.bufferSource = bufferSource;
    }

    @Override
    public @NonNull Class<GuiMultiblockRenderState> getRenderStateClass() {
        return GuiMultiblockRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiMultiblockRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector) {
        var mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        // Provide level to multiblock for client-side state
        state.multiblock().setLevel(level);

        var size = state.size();
        int sizeX = size.getX();
        int sizeY = size.getY();
        int sizeZ = size.getZ();

        // Center the multiblock
        poseStack.translate((float) sizeX / 2, (float) sizeY / 2, 0);

        // Initial eye position for lighting calculations - positioned behind the scene
        Vector4f eye = new Vector4f(0, 0, -100, 1);
        Matrix4f rotMat = new Matrix4f();
        rotMat.identity();

        // Flip the structure 180° around Z-axis to correct the upside-down rendering
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
        rotMat.rotate(Axis.ZP.rotationDegrees(-180));

        // Apply isometric-style tilt: rotate around X-axis
        // Positive rotation tilts the structure for proper isometric view
        poseStack.mulPose(Axis.XP.rotationDegrees(-30F));
        rotMat.rotate(Axis.XP.rotationDegrees(30));

        // Calculate offsets for centering the rotation pivot point
        float offX = (float) -sizeX / 2;
        float offZ = (float) -sizeZ / 2 + 1;

        // Apply animated Y-axis rotation (spinning effect)
        // Translate to center, apply rotations, then translate back
        float time = state.rotationTime();
        poseStack.translate(-offX, 0, -offZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(time));
        rotMat.rotate(Axis.YP.rotationDegrees(-time));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        rotMat.rotate(Axis.YP.rotationDegrees(-45));
        poseStack.translate(offX, 0, offZ);

        // Transform the eye position by the inverse rotation matrix for correct lighting
        rotMat.transform(eye);
        eye.div(eye.w);
        Vec3 eye3 = new Vec3(eye.x(), eye.y(), eye.z());

        MultiBufferSource.BufferSource buffers = this.bufferSource;

        BlockPos checkPos = null;
        if (mc.hitResult instanceof BlockHitResult blockRes) {
            checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
        }

        // Apply slight Z offset to prevent Z-fighting between blocks
        poseStack.pushPose();
        poseStack.translate(0, 0, -1);

        for (Multiblock.SimulateResult r : state.simulateResults()) {
            float alpha = 0.3F;
            if (r.worldPosition().equals(checkPos)) {
                alpha = 0.6F + (float) (Math.sin(ClientTicks.total * 0.3F) + 1F) * 0.1F;
            }

            BlockState displayedBlockState = r.stateMatcher().getDisplayedState(ClientTicks.ticks).rotate(state.facingRotation());

            // Render block (via platform service)
            this.renderBlock(level, displayedBlockState, r.worldPosition(), alpha, poseStack);

            if (displayedBlockState.getBlock() instanceof EntityBlock eb) {
                var cache = state.blockEntityCache();
                var errored = state.erroredBlockEntities();

                var be = cache.compute(r.worldPosition().immutable(), (p, cachedBe) -> {
                    if (cachedBe != null && !cachedBe.getType().isValid(displayedBlockState)) {
                        return eb.newBlockEntity(p, displayedBlockState);
                    }
                    return cachedBe != null ? cachedBe : eb.newBlockEntity(p, displayedBlockState);
                });

                if (be != null && !errored.contains(be)) {
                    be.setLevel(mc.level);
                    //noinspection deprecation
                    be.setBlockState(displayedBlockState);

                    poseStack.pushPose();
                    var bePos = r.worldPosition();
                    poseStack.translate(bePos.getX(), bePos.getY(), bePos.getZ());

                    try {
                        var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
                        var renderer = dispatcher.getRenderer(be);
                        if (renderer != null) {
                            var renderState = renderer.createRenderState();
                            //Note: we cannot use Minecraft.getInstance().getBlockEntityRenderDispatcher().tryExtractRenderState because that takes the camera eye position of the in-world camera
                            renderer.extractRenderState(be, renderState, ClientTicks.partialTicks, eye3, null);
                            renderState.lightCoords = LevelRenderer.getLightCoords(level, bePos);
                            var featureDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
                            var cameraRenderState = new CameraRenderState();
                            dispatcher.submit(renderState, poseStack, featureDispatcher.getSubmitNodeStorage(), cameraRenderState);
                            featureDispatcher.renderAllFeatures();
                        }
                    } catch (Exception e) {
                        errored.add(be);
                        Modonomicon.LOG.error("Error rendering block entity", e);
                    }
                    poseStack.popPose();
                }
            }
        }
        poseStack.popPose();
    }

    private void renderBlock(ClientLevel level, BlockState state, BlockPos pos, float alpha, PoseStack ps) {
        if (pos != null) {
            ps.pushPose();
            ps.translate(pos.getX(), pos.getY(), pos.getZ());

            Minecraft minecraft = Minecraft.getInstance();
            BlockStateModel model = minecraft.getModelManager().getBlockStateModelSet().get(state);
            PoseStack.Pose pose = ps.last();
            int lightCoords = LevelRenderer.getLightCoords(LevelRenderer.BrightnessGetter.DEFAULT, level, state, pos);

            var renderType = model.hasMaterialFlag(net.minecraft.client.resources.model.geometry.BakedQuad.FLAG_TRANSLUCENT) ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockItemSheet();
            VertexConsumer buffer = this.bufferSource.getBuffer(renderType);

            BlockQuadOutput output = (_, _, _, quad, instance) ->
            {
                instance.setLightCoords(lightCoords);
                buffer.putBakedQuad(pose, quad, instance);
            };
            ModelBlockRenderer blockRenderer = new ModelBlockRenderer(minecraft.options.ambientOcclusion().get(), false, minecraft.getBlockColors());
            GhostRenderState renderState = new GhostRenderState(level, pos, state);
            blockRenderer.tesselateBlock(output, 0, 0, 0, renderState, pos, state, model, 0);

            ps.popPose();

        }
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "multiblock";
    }
}
