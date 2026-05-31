/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

/**
 * Top-level extension seam for research datagen.
 *
 * A subprovider authors one logical research bundle, then hands that bundle to the top-level
 * {@link ResearchProvider} for serialization.
 */
public interface ResearchSubProvider {
    /**
     * Generates one or more authored research bundles and passes them to the given consumer.
     */
    void generate(BiConsumer<Identifier, ResearchBundle> consumer, HolderLookup.Provider registries);
}
