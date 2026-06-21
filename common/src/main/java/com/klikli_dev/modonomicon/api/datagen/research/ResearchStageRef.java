/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.resources.Identifier;

/**
 * Typed authoring-time reference to a research stage id.
 *
 * @param id the canonical stage identifier used in generated research resources
 */
public record ResearchStageRef(Identifier id) {
    public static ResearchStageRef of(Identifier id) {
        return new ResearchStageRef(id);
    }
}
