/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

/**
 * Immutable snapshot of multiblock build progress for one frame.
 * <p>
 * Replaces the former {@code blocks / blocksDone / airFilled / currentLayer / totalLayers}
 * static int soup so progress can be passed explicitly between the extract, render and HUD passes.
 * </p>
 *
 * @param blocks       number of non-air positions counting towards the multiblock total.
 * @param blocksDone   number of those positions already correctly built in the world.
 * @param airFilled    number of positions that must stay empty but are currently occupied.
 * @param currentLayer 1-based index of the lowest incomplete layer (layer-by-layer mode), 0 when inactive.
 * @param totalLayers  total number of distinct Y layers (layer-by-layer mode), 0 when inactive.
 */
public record PreviewProgress(int blocks, int blocksDone, int airFilled, int currentLayer, int totalLayers) {

    /** Empty progress used before the first simulation ran. */
    public static final PreviewProgress EMPTY = new PreviewProgress(0, 0, 0, 0, 0);

    /**
     * @return {@code true} when every counted block is built, nothing extra fills air-only spots,
     * and (in layer-by-layer mode) all layers are reached.
     */
    public boolean isComplete(boolean layerByLayer) {
        return this.blocks == this.blocksDone
                && this.airFilled == 0
                && (!layerByLayer || this.currentLayer >= this.totalLayers);
    }
}
