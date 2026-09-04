/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Extracts and submits ghost block entity render states for a {@link PreviewSnapshot}.
 * <p>
 * Logic moved from the former {@code MultiblockPreviewRenderer} monolith (which kept
 * the caches and the extracted states in static fields shared between the extraction and render
 * phases). The caches now live on this instance, owned by {@link PreviewController}, and are
 * cleared whenever a new preview is started.
 * </p>
 * <p>
 * 26.2 port: extraction is unchanged, but submission goes directly through the 26.2
 * {@link SubmitNodeCollector} (mirroring {@code MultiblockPreviewRenderer} on this branch, see
 * 312f841) instead of the 26.1 ghost {@code FeatureRenderDispatcher} + buffer-source path, which
 * no longer exists on 26.2.
 * </p>
 */
public class PreviewBlockEntityRenderer {

    private final Map<BlockPos, BlockEntity> blockEntityCache = new Object2ObjectOpenHashMap<>();
    private final Set<BlockEntity> erroredBlockEntities = Collections.newSetFromMap(new WeakHashMap<>());

    /** Drops all cached fake block entities, e.g. when a new preview is started. */
    public void clearCache() {
        this.blockEntityCache.clear();
        this.erroredBlockEntities.clear();
    }

    /**
     * Phase 1: extract render states for every block entity in the snapshot.
     * Must be called during the render state extraction phase; the returned states are attached
     * to the snapshot and submitted later by {@link #submit(List, SubmitNodeCollector)}.
     */
    public @NonNull List<BlockEntityRenderState> extract(PreviewSnapshot snapshot) {
        List<BlockEntityRenderState> states = new ArrayList<>();
        if (snapshot.visibleResults().isEmpty()) {
            return states;
        }

        Minecraft mc = Minecraft.getInstance();
        BlockPos startPos = snapshot.startPos();

        // Extract block entity render states from the simulated multiblock
        for (var r : snapshot.visibleResults()) {
            try {
                BlockState displayedState = r.stateMatcher().getDisplayedState(ClientTicks.ticks).rotate(snapshot.effectiveRotation());

                if (displayedState.getBlock() instanceof EntityBlock eb) {
                    // Cache/create a fake block entity at the simulated position (translate by startPos)
                    var cacheKey = r.worldPosition().subtract(startPos).immutable();
                    var be = this.blockEntityCache.compute(cacheKey, (p, cachedBe) -> {
                        if (cachedBe != null && !cachedBe.getType().isValid(displayedState)) {
                            return eb.newBlockEntity(p, displayedState);
                        }
                        return cachedBe != null ? cachedBe : eb.newBlockEntity(p, displayedState);
                    });

                    if (be != null && !this.erroredBlockEntities.contains(be)) {
                        be.setLevel(mc.level);
                        // Provide the displayed state to avoid querying the real world
                        be.setBlockState(displayedState);

                        var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
                        var renderer = dispatcher.getRenderer(be);
                        if (renderer != null) {
                            var renderState = renderer.createRenderState();
                            var eye = mc.getEntityRenderDispatcher().camera.position();
                            eye = eye.subtract(startPos.getX(), startPos.getY(), startPos.getZ());

                            //Note: we cannot use Minecraft.getInstance().getBlockEntityRenderDispatcher().tryExtractRenderState because that takes the camera eye position of the in-world camera, but our multiblock exists in a virtual level close to 0 0 0
                            renderer.extractRenderState(be, renderState, ClientTicks.partialTicks, eye, null);
                            renderState.blockPos = r.worldPosition();
                            //HACK / WHY: ghosts are always fully lit so the preview stays readable at night / underground.
                            renderState.lightCoords = LightCoordsUtil.FULL_BRIGHT;
                            states.add(renderState);
                        }
                    }
                }
            } catch (Exception e) {
                // Don't let a failure extracting one block entity abort the entire extraction loop
                Modonomicon.LOG.error("Error extracting block entity render state", e);
            }
        }
        return states;
    }

    /**
     * Phase 2: submit the extracted states through the 26.2 {@link SubmitNodeCollector}.
     */
    public void submit(List<BlockEntityRenderState> states, SubmitNodeCollector submitNodeCollector) {
        if (states.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();

        // Render block entities using the extracted render states
        EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
        double renderPosX = erd.camera.position().x();
        double renderPosY = erd.camera.position().y();
        double renderPosZ = erd.camera.position().z();
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(-renderPosX, -renderPosY, -renderPosZ);

        var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        var cameraRenderState = new CameraRenderState();

        for (var blockEntityRenderState : states) {
            poseStack.pushPose();

            poseStack.translate(blockEntityRenderState.blockPos.getX(),
                    blockEntityRenderState.blockPos.getY(),
                    blockEntityRenderState.blockPos.getZ());

            dispatcher.submit(blockEntityRenderState, poseStack, submitNodeCollector, cameraRenderState);

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
