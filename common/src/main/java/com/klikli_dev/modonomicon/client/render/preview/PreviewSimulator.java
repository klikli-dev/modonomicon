/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Runs the once-per-frame multiblock simulation shared by the extraction, block, block entity
 * and HUD passes.
 * <p>
 * Previously {@code multiblock.simulate(...)} + {@code getVisibleMaxY(...)} ran twice per frame
 * (once in each render phase) and progress counters were recomputed inline in the render loop.
 * Simulating once guarantees all passes agree on the same result set.
 * </p>
 */
public final class PreviewSimulator {

    private PreviewSimulator() {
    }

    /**
     * Simulates the session's multiblock at its start position with its effective rotation.
     * <p>
     * The returned snapshot contains no block entity render states; those are extracted
     * separately by {@code PreviewBlockEntityRenderer} and attached via
     * {@link PreviewSnapshot#withBlockEntityStates}.
     * </p>
     *
     * @return snapshot, or {@code null} when there is nothing to simulate.
     */
    @Nullable
    public static PreviewSnapshot simulate(PreviewSession session, Level level) {
        Multiblock multiblock = session.multiblock();
        BlockPos startPos = session.startPos();
        if (multiblock == null || startPos == null) {
            return null;
        }
        Rotation rotation = session.effectiveRotation();

        multiblock.setLevel(level);
        Pair<BlockPos, Collection<Multiblock.SimulateResult>> sim = multiblock.simulate(level, startPos, rotation, true, false);

        Integer visibleMaxY = session.isLayerByLayer() && session.isAnchored()
                ? LayerFilter.getVisibleMaxY(level, sim.getSecond(), rotation)
                : null;

        List<Multiblock.SimulateResult> visible = new ArrayList<>(sim.getSecond().size());
        for (var r : sim.getSecond()) {
            if (visibleMaxY != null && r.worldPosition().getY() > visibleMaxY) {
                continue;
            }
            visible.add(r);
        }

        PreviewProgress progress = LayerFilter.computeProgress(sim.getSecond(), visibleMaxY, level, rotation, session.isLayerByLayer());

        BlockPos checkPos = checkPos();
        BlockState lookingState = null;
        if (checkPos != null) {
            for (var r : visible) {
                if (r.worldPosition().equals(checkPos)) {
                    //unrotated on purpose: matches the former render loop, used for the HUD pick-block display
                    lookingState = r.stateMatcher().getDisplayedState(ClientTicks.ticks);
                    break;
                }
            }
        }

        return new PreviewSnapshot(startPos, rotation, visible, visibleMaxY, progress,
                List.of(), checkPos, lookingState);
    }

    /**
     * World position targeted by the crosshair: the neighbour of the hit block, i.e. where the
     * next placed block would go. Mirrors the former render-phase {@code checkPos} computation.
     */
    @Nullable
    public static BlockPos checkPos() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hitResult instanceof BlockHitResult blockRes) {
            return blockRes.getBlockPos().relative(blockRes.getDirection());
        }
        return null;
    }
}
