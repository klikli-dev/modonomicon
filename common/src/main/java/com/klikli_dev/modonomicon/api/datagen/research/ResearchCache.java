/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.Map;

/**
 * Shared cache for research bundles during datagen.
 * <p>
 * The {@link com.klikli_dev.modonomicon.api.datagen.BookProvider} contributes book-generated
 * research (entry hierarchy) to this cache, and the {@link ResearchProvider} reads from it
 * to merge with authored research before writing all bundles to disk.
 */
public final class ResearchCache {
    private final Map<Identifier, ResearchDataBuilder> bundles = new Object2ObjectOpenHashMap<>();

    /**
     * Accept one research bundle. Used by {@link com.klikli_dev.modonomicon.api.datagen.BookProvider}
     * to contribute book-generated research.
     *
     * @throws IllegalStateException if a bundle with the same id was already added
     */
    public void accept(Identifier bundleId, ResearchDataBuilder builder) {
        if (this.bundles.put(bundleId, builder) != null) {
            throw new IllegalStateException("Duplicate research bundle " + bundleId);
        }
    }

    /**
     * Merge one research bundle. Used by {@link ResearchProvider} to add authored research
     * that may intentionally override book-generated bundles.
     */
    public void merge(Identifier bundleId, ResearchDataBuilder builder) {
        this.bundles.put(bundleId, builder);
    }

    /**
     * Returns an unmodifiable snapshot of all collected bundles.
     */
    public Map<Identifier, ResearchDataBuilder> build() {
        return Collections.unmodifiableMap(this.bundles);
    }
}
