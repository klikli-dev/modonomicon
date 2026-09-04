/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;

/**
 * Immutable view of the currently previewed multiblock.
 * <p>
 * Single source of truth for API and UI consumers reading preview state; for granular progress
 * details see the client-side preview package.
 * </p>
 *
 * @param multiblock   the previewed multiblock.
 * @param anchor       the anchor position, or {@code null} while the preview floats unanchored.
 * @param facing       the facing rotation used for simulation and matching.
 * @param isAnchored   whether the preview is anchored in the world (vs following the crosshair).
 * @param layerByLayer whether higher layers stay hidden until lower layers are built.
 */
public record MultiblockPreviewData(Multiblock multiblock, BlockPos anchor, Rotation facing, boolean isAnchored,
                                    boolean layerByLayer) {
}
