/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.resources.Identifier;

/**
 * Typed authoring-time reference to a research fact id.
 *
 * @param id the canonical fact identifier used in generated research resources
 */
public record ResearchFactRef(Identifier id) {
    /**
     * Creates a typed fact ref for the given explicit identifier.
     */
    public static ResearchFactRef of(Identifier id) {
        return new ResearchFactRef(id);
    }
}
