/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.resources.Identifier;

/**
 * Typed authoring-time reference to a research value id.
 *
 * @param id the canonical value identifier used in generated research resources
 */
public record ResearchValueRef(Identifier id) {
    /**
     * Creates a typed value ref for the given explicit identifier.
     */
    public static ResearchValueRef of(Identifier id) {
        return new ResearchValueRef(id);
    }
}
