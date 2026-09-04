/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.multiblock.matcher.Matchers;
import com.klikli_dev.modonomicon.registry.StateMatcherTypeRegistry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Pure simulation helpers for the multiblock preview: relevance checks, layer-by-layer
 * visibility and progress counting.
 * <p>
 * Moved verbatim from the former {@code MultiblockPreviewRenderer} monolith. All methods are
 * stateless and operate on explicit parameters so they can be unit tested without Minecraft.
 * </p>
 */
public final class LayerFilter {

    private LayerFilter() {
    }

    /**
     * Positions using the {@code ANY} matcher or display-only matchers are decoration and never
     * influence progress, completion or layer visibility.
     */
    public static boolean isPreviewRelevant(Multiblock.SimulateResult result) {
        return !result.stateMatcher().equals(Matchers.ANY) && result.stateMatcher().type() != StateMatcherTypeRegistry.DISPLAY;
    }

    /**
     * Computes the highest layer (world Y level) to display in layer-by-layer mode.
     * Layers are complete bottom-up: all layers up to and including the lowest
     * incomplete layer are displayed, higher layers stay hidden until the layers
     * below them are fully built.
     *
     * @return the maximum visible Y level, or null to display all layers.
     */
    @Nullable
    public static Integer getVisibleMaxY(Level level, Collection<Multiblock.SimulateResult> results, Rotation rotation) {
        TreeMap<Integer, List<Multiblock.SimulateResult>> byLayer = new TreeMap<>();
        for (var r : results) {
            if (!isPreviewRelevant(r)) {
                continue;
            }
            byLayer.computeIfAbsent(r.worldPosition().getY(), y -> new ArrayList<>()).add(r);
        }
        if (byLayer.isEmpty()) {
            return null;
        }
        for (var entry : byLayer.entrySet()) {
            boolean layerComplete = true;
            for (var r : entry.getValue()) {
                if (!r.test(level, rotation)) {
                    layerComplete = false;
                    break;
                }
            }
            if (!layerComplete) {
                return entry.getKey();
            }
        }
        //all layers complete, show everything
        return byLayer.lastKey();
    }

    /**
     * Counts build progress over the given results.
     *
     * @param results      all (unfiltered) simulation results.
     * @param visibleMaxY  highest visible Y level, or {@code null} when every layer is visible.
     * @param level        world used for {@code test(...)} checks.
     * @param rotation     effective (symmetry-corrected) rotation.
     * @param layerByLayer whether layer counters should be populated.
     */
    public static PreviewProgress computeProgress(Collection<Multiblock.SimulateResult> results, @Nullable Integer visibleMaxY, Level level, Rotation rotation, boolean layerByLayer) {
        int blocks = 0;
        int blocksDone = 0;
        int airFilled = 0;

        for (var r : results) {
            if (visibleMaxY != null && r.worldPosition().getY() > visibleMaxY) {
                continue;
            }
            if (!isPreviewRelevant(r)) {
                continue;
            }
            boolean air = !r.stateMatcher().countsTowardsTotalBlocks();
            if (!air) {
                blocks++;
            }
            if (!r.test(level, rotation)) {
                if (air) {
                    airFilled++;
                }
            } else if (!air) {
                blocksDone++;
            }
        }

        int currentLayer = 0;
        int totalLayers = 0;
        if (layerByLayer) {
            Set<Integer> layers = new TreeSet<>();
            for (var r : results) {
                if (!isPreviewRelevant(r)) {
                    continue;
                }
                layers.add(r.worldPosition().getY());
            }
            totalLayers = layers.size();
            if (visibleMaxY == null || totalLayers == 0) {
                currentLayer = totalLayers;
            } else {
                currentLayer = new ArrayList<>(layers).indexOf(visibleMaxY) + 1;
            }
        }
        return new PreviewProgress(blocks, blocksDone, airFilled, currentLayer, totalLayers);
    }
}
