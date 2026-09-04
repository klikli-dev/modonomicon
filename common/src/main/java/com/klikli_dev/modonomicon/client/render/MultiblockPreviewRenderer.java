/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.MultiblockPreviewData;
import com.klikli_dev.modonomicon.client.render.preview.PreviewBlockRenderer;
import com.klikli_dev.modonomicon.client.render.preview.PreviewController;
import com.klikli_dev.modonomicon.client.render.preview.PreviewOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Handles rendering of the multiblock preview in the world as semi-transparent blocks.
 * <p>
 * This class is a thin static facade over the {@code client.render.preview} package so that
 * loader hooks (NeoForge / Forge / Fabric events), {@code BookMultiblockPageRenderer} and API
 * consumers keep working unchanged. All state and logic live in the collaborators:
 * </p>
 * <ul>
 * <li>{@link PreviewController} — session lifecycle, both render phases, tick, interaction, HUD</li>
 * <li>{@code PreviewSession} — mutable preview state (replaces the former static field soup)</li>
 * <li>{@code PreviewSimulator} + {@code LayerFilter} — once-per-frame simulation, layer filter, progress</li>
 * <li>{@code PreviewBlockRenderer} + {@code PreviewBlockEntityRenderer} — ghost submission</li>
 * <li>{@code PreviewHudRenderer} — progress overlay</li>
 * </ul>
 * <p>
 * Per-frame flow: {@code extractRenderState} (extraction phase) simulates once and caches ghost
 * block entity states in a {@code PreviewSnapshot}; {@code submitRenderFeatures} /
 * {@code renderMultiblock} (submit phase) draws ghost blocks and submits the cached states;
 * {@code onRenderHUD} draws the overlay.
 * See https://github.com/XFactHD/FramedBlocks/blob/26.1/src/main/java/io/github/xfacthd/framedblocks/client/render/special/GhostBlockRenderer.java for the architecture we used for 26.1.
 * </p>
 * <p>
 * 26.2 port notes (see 312f841): the submit phase uses {@link SubmitNodeCollector} —
 * ghost blocks via {@code submitCustomGeometry} with {@code translucentMovingBlock}, block
 * entities via direct {@code dispatcher.submit(..., submitNodeCollector, ...)} — instead of the
 * 26.1 direct {@code PoseStack} + GPU upload / ghost {@code FeatureRenderDispatcher} path.
 * Hook signatures ({@code extractRenderState}, {@code submitRenderFeatures},
 * {@code renderMultiblock}, {@code onRenderHUD}, {@code onPlayerInteract},
 * {@code onClientTick}) and the {@code hasMultiblock} field are kept compatible with this
 * branch's loader event registrations.
 * </p>
 * <p>
 * For reading preview state prefer {@link #getMultiblockPreviewData()} (also exposed to API
 * consumers via {@code ModonomiconAPI#getCurrentPreviewMultiblock()}); it carries the
 * multiblock, anchor, facing, anchored flag and layer mode in one immutable object.
 * </p>
 */
public class MultiblockPreviewRenderer {

    /**
     * Legacy field-style access kept for loader hooks (e.g. Forge
     * {@code if (MultiblockPreviewRenderer.hasMultiblock)}). Mirrors
     * {@link PreviewController#hasMultiblock()} and is synced on every facade call.
     */
    public static boolean hasMultiblock;

    public static boolean hasMultiblock() {
        syncHasMultiblock();
        return hasMultiblock;
    }

    private static void syncHasMultiblock() {
        hasMultiblock = PreviewController.get().hasMultiblock();
    }

    public static void setMultiblock(Multiblock multiblock, Component name, boolean flip) {
        setMultiblock(multiblock, name, flip, pos -> pos, false);
    }

    public static void setMultiblock(Multiblock multiblock, Component name, boolean flip, boolean layerByLayer) {
        setMultiblock(multiblock, name, flip, pos -> pos, layerByLayer);
    }

    public static void setMultiblock(Multiblock multiblock, Component name, boolean flip, Function<BlockPos, BlockPos> offsetApplier) {
        setMultiblock(multiblock, name, flip, offsetApplier, false);
    }

    public static void setMultiblock(Multiblock multiblock, Component name, boolean flip, Function<BlockPos, BlockPos> offsetApplier, boolean layerByLayer) {
        PreviewController.get().setMultiblock(multiblock, name, new PreviewOptions(offsetApplier, layerByLayer, flip));
        syncHasMultiblock();
    }

    /**
     * Clears the active preview, if any. Prefer this over toggling via {@code setMultiblock}.
     */
    public static void clearMultiblock() {
        PreviewController.get().clear();
        syncHasMultiblock();
    }

    public static void onRenderHUD(GuiGraphicsExtractor guiGraphics, float partialTicks) {
        PreviewController.get().renderHud(guiGraphics, partialTicks);
        syncHasMultiblock();
    }

    public static void submitRenderFeatures(LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector) {
        if (PreviewController.get().hasMultiblock()) {
            renderMultiblock(levelRenderState, submitNodeCollector);
        }
    }

    public static void anchorTo(BlockPos target, Rotation rot) {
        PreviewController.get().anchorTo(target, rot);
    }

    public static InteractionResult onPlayerInteract(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
        var result = PreviewController.get().onPlayerInteract(player, world, hand, hit);
        syncHasMultiblock();
        return result;
    }

    public static void onClientTick(Minecraft mc) {
        PreviewController.get().onClientTick(mc);
        syncHasMultiblock();
    }

    /**
     * Phase 1: Extract render state from the level.
     * This should be called during the render state extraction phase.
     *
     * @param levelRenderState hook signature compatibility; extraction results travel via the
     *                         internal frame snapshot, not this object.
     */
    public static void extractRenderState(LevelRenderState levelRenderState) {
        PreviewController.get().extractRenderState();
        syncHasMultiblock();
    }

    /**
     * Phase 2: Render the multiblock using the extracted render state.
     * This should be called during the actual rendering phase with the levelRenderState.
     *
     * @param levelRenderState hook signature compatibility; submission travels via the
     *                         {@code submitNodeCollector}.
     */
    public static void renderMultiblock(LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector) {
        PreviewController.get().renderLevel(levelRenderState, submitNodeCollector);
        syncHasMultiblock();
    }

    public static void renderBlock(Level world, BlockState state, BlockPos pos, float alpha, ModelBlockRenderer blockRenderer, SubmitNodeCollector submitNodeCollector) {
        PreviewBlockRenderer.renderBlock(world, state, pos, alpha, blockRenderer, submitNodeCollector);
    }

    @Nullable
    public static MultiblockPreviewData getMultiblockPreviewData() {
        return PreviewController.get().getMultiblockPreviewData();
    }

    @Nullable
    public static Multiblock getMultiblock() {
        return PreviewController.get().session().multiblock();
    }

    public static boolean isAnchored() {
        return PreviewController.get().session().isAnchored();
    }

    public static boolean isLayerByLayer() {
        return PreviewController.get().session().isLayerByLayer();
    }

    public static Rotation getFacingRotation() {
        var session = PreviewController.get().session();
        return session.effectiveRotation();
    }

    @Nullable
    public static BlockPos getStartPos() {
        return PreviewController.get().session().startPos();
    }
}
