package com.klikli_dev.modonomicon.client.render.pip;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.render.state.pip.GuiMultiblockRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class GuiMultiblockRenderer extends PictureInPictureRenderer<GuiMultiblockRenderState> {

    public GuiMultiblockRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<GuiMultiblockRenderState> getRenderStateClass() {
        return GuiMultiblockRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiMultiblockRenderState state, PoseStack poseStack) {
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
            if (r.getWorldPosition().equals(checkPos)) {
                alpha = 0.6F + (float) (Math.sin(ClientTicks.total * 0.3F) + 1F) * 0.1F;
            }

            BlockState displayedBlockState = r.getStateMatcher().getDisplayedState(ClientTicks.ticks).rotate(state.facingRotation());

            // Render block (via platform service)
            renderBlock(buffers, level, state.multiblock(), displayedBlockState, r.getWorldPosition(), alpha, poseStack, state.randomSource());

            if (displayedBlockState.getBlock() instanceof EntityBlock eb) {
                var cache = state.blockEntityCache();
                var errored = state.erroredBlockEntities();

                var be = cache.compute(r.getWorldPosition().immutable(), (p, cachedBe) -> {
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
                    var bePos = r.getWorldPosition();
                    poseStack.translate(bePos.getX(), bePos.getY(), bePos.getZ());

                    try {
                        var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
                        var renderer = dispatcher.getRenderer(be);
                        if (renderer != null) {
                            var renderState = renderer.createRenderState();
                            //Note: we cannot use Minecraft.getInstance().getBlockEntityRenderDispatcher().tryExtractRenderState because that takes the camera eye position of the in-world camera
                            renderer.extractRenderState(be, renderState, ClientTicks.partialTicks, eye3, null);
                            renderState.lightCoords = LightTexture.FULL_BRIGHT;
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

    private void renderBlock(MultiBufferSource.BufferSource buffers, ClientLevel level, Multiblock multiblock, BlockState state, BlockPos pos, float alpha, PoseStack ps, net.minecraft.util.RandomSource randomSource) {
        if (pos != null) {
            ps.pushPose();
            ps.translate(pos.getX(), pos.getY(), pos.getZ());
            // Delegate to platform abstraction for block rendering
            com.klikli_dev.modonomicon.platform.ClientServices.MULTIBLOCK.renderBlock(state, pos, multiblock, ps, buffers, randomSource);
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
