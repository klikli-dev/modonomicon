/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Mutable state of the active in-world multiblock preview.
 * <p>
 * Replaces the former static field soup on {@code MultiblockPreviewRenderer} so that all preview
 * state lives in one explicit object owned by {@link PreviewController}. Per-frame simulation
 * results are intentionally <em>not</em> stored here — see {@link PreviewSnapshot}.
 * </p>
 */
public class PreviewSession {

    private Multiblock multiblock;
    private Component name;
    private BlockPos anchorPos;
    private boolean anchored;
    private Rotation facingRotation = Rotation.NONE;
    private Function<BlockPos, BlockPos> offsetApplier = pos -> pos;
    private boolean layerByLayer;
    private int timeComplete;
    private BlockState lookingState;
    private BlockPos lookingPos;

    public boolean hasMultiblock() {
        return this.multiblock != null;
    }

    @Nullable
    public Multiblock multiblock() {
        return this.multiblock;
    }

    public void setMultiblock(@Nullable Multiblock multiblock) {
        this.multiblock = multiblock;
    }

    @Nullable
    public Component name() {
        return this.name;
    }

    public void setName(@Nullable Component name) {
        this.name = name;
    }

    @Nullable
    public BlockPos anchorPos() {
        return this.anchorPos;
    }

    public void setAnchorPos(@Nullable BlockPos anchorPos) {
        this.anchorPos = anchorPos;
    }

    public boolean isAnchored() {
        return this.anchored;
    }

    public void setAnchored(boolean anchored) {
        this.anchored = anchored;
    }

    public Rotation facingRotation() {
        return this.facingRotation;
    }

    public void setFacingRotation(Rotation facingRotation) {
        this.facingRotation = facingRotation;
    }

    public Function<BlockPos, BlockPos> offsetApplier() {
        return this.offsetApplier;
    }

    public void setOffsetApplier(Function<BlockPos, BlockPos> offsetApplier) {
        this.offsetApplier = offsetApplier;
    }

    public boolean isLayerByLayer() {
        return this.layerByLayer;
    }

    public void setLayerByLayer(boolean layerByLayer) {
        this.layerByLayer = layerByLayer;
    }

    public int timeComplete() {
        return this.timeComplete;
    }

    public void setTimeComplete(int timeComplete) {
        this.timeComplete = timeComplete;
    }

    @Nullable
    public BlockState lookingState() {
        return this.lookingState;
    }

    public void setLookingState(@Nullable BlockState lookingState) {
        this.lookingState = lookingState;
    }

    @Nullable
    public BlockPos lookingPos() {
        return this.lookingPos;
    }

    public void setLookingPos(@Nullable BlockPos lookingPos) {
        this.lookingPos = lookingPos;
    }

    /**
     * Effective rotation used for simulation and matching.
     * HACK / WHY: symmetrical multiblocks only ever check one rotation, so callers must not
     * apply the player's facing to them.
     */
    public Rotation effectiveRotation() {
        if (this.multiblock != null && this.multiblock.isSymmetrical()) {
            return Rotation.NONE;
        }
        return this.facingRotation;
    }

    /**
     * Start position of the multiblock (anchor + view offset handling).
     */
    @Nullable
    public BlockPos startPos() {
        if (this.anchorPos == null || this.offsetApplier == null) {
            return null;
        }
        return this.offsetApplier.apply(this.anchorPos);
    }

    /** Resets the session to "no preview", keeping no stale references. */
    public void clear() {
        this.multiblock = null;
        this.name = null;
        this.anchorPos = null;
        this.anchored = false;
        this.facingRotation = Rotation.NONE;
        this.offsetApplier = pos -> pos;
        this.layerByLayer = false;
        this.timeComplete = 0;
        this.lookingState = null;
        this.lookingPos = null;
    }
}
