/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.resources.Identifier;

/**
 * Typed authoring-time reference to a research node id.
 *
 * @param id the canonical node identifier used in generated research resources
 */
public record ResearchNodeRef(Identifier id) {
    /**
     * Creates a typed node ref for the given explicit identifier.
     */
    public static ResearchNodeRef of(Identifier id) {
        return new ResearchNodeRef(id);
    }
}
