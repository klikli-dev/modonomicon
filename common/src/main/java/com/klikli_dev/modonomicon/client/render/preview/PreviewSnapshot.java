/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Immutable per-frame result of simulating the previewed multiblock.
 * <p>
 * Produced once per frame by {@link PreviewSimulator} during the render-state extraction phase
 * and consumed by the block, block entity and HUD renderers. Sharing one snapshot removes the
 * former double {@code multiblock.simulate(...)} + double {@code getVisibleMaxY(...)} per frame.
 * </p>
 *
 * @param startPos          multiblock start position the simulation ran for.
 * @param effectiveRotation rotation the simulation ran for (already symmetry-corrected).
 * @param visibleResults    simulation results after layer-by-layer filtering.
 * @param visibleMaxY       highest visible Y level, or {@code null} to display all layers.
 * @param progress          build progress counted from the visible results.
 * @param blockEntityStates extracted ghost block entity render states.
 * @param checkPos          world position the player is targeting (may be {@code null}).
 * @param lookingState      unrotated displayed state at {@code checkPos} (may be {@code null}).
 */
public record PreviewSnapshot(
        BlockPos startPos,
        Rotation effectiveRotation,
        List<Multiblock.SimulateResult> visibleResults,
        @Nullable Integer visibleMaxY,
        PreviewProgress progress,
        List<BlockEntityRenderState> blockEntityStates,
        @Nullable BlockPos checkPos,
        @Nullable BlockState lookingState
) {
    /**
     * Returns a copy of this snapshot with the given extracted block entity render states.
     * Used by the extraction phase to attach ghost block entity states to the simulated snapshot.
     */
    public PreviewSnapshot withBlockEntityStates(List<BlockEntityRenderState> blockEntityStates) {
        return new PreviewSnapshot(this.startPos, this.effectiveRotation, this.visibleResults,
                this.visibleMaxY, this.progress, blockEntityStates, this.checkPos, this.lookingState);
    }

    /**
     * Returns a copy of this snapshot with zeroed block counters.
     * Preserves the former behavior of resetting {@code blocks / blocksDone} while the preview
     * floats unanchored (air and layer counters were historically left untouched).
     */
    public PreviewSnapshot withZeroedBlockCounters() {
        PreviewProgress zeroed = new PreviewProgress(0, 0, this.progress.airFilled(),
                this.progress.currentLayer(), this.progress.totalLayers());
        return new PreviewSnapshot(this.startPos, this.effectiveRotation, this.visibleResults,
                this.visibleMaxY, zeroed, this.blockEntityStates, this.checkPos, this.lookingState);
    }
}
