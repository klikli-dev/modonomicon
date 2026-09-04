/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import net.minecraft.core.BlockPos;

import java.util.function.Function;

/**
 * Options for starting (or toggling) a multiblock preview.
 * <p>
 * Replaces the former {@code setMultiblock(..., flip, offsetApplier, layerByLayer)} boolean-trap
 * overloads with a single explicit parameter object.
 * </p>
 *
 * @param offsetApplier applied to the anchor to obtain the multiblock start position.
 * @param layerByLayer  when {@code true}, higher layers stay hidden until lower layers are built.
 * @param toggleIfSame  when {@code true} and the same multiblock is already previewed, clear it instead.
 */
public record PreviewOptions(Function<BlockPos, BlockPos> offsetApplier, boolean layerByLayer, boolean toggleIfSame) {

    /** Default options: no offset, all layers visible, no toggling. */
    public static final PreviewOptions DEFAULT = new PreviewOptions(pos -> pos, false, false);

    public static PreviewOptions toggle() {
        return new PreviewOptions(pos -> pos, false, true);
    }

    public static PreviewOptions toggleLayers() {
        return new PreviewOptions(pos -> pos, true, true);
    }
}
