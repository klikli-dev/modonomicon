/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;

public record MultiblockPreviewData(Multiblock multiblock, BlockPos anchor, Rotation facing, boolean isAnchored,
                                    boolean layerByLayer) {
}
