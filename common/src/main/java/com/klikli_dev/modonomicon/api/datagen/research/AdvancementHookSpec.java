/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.AdvancementResearchHookDefinition;
import net.minecraft.resources.Identifier;

/**
 * Authoring-time research ingress declaration for advancement completion.
 *
 * @param id the canonical hook id
 * @param advancementId the advancement whose completion acts as ingress into research
 * @param factRef the fact granted when that ingress event occurs
 */
public record AdvancementHookSpec(Identifier id, Identifier advancementId, ResearchFactRef factRef) {
    /**
     * Compiles this ingress declaration into the canonical advancement-hook record.
     */
    public AdvancementResearchHookDefinition toDefinition() {
        return new AdvancementResearchHookDefinition(this.id, this.advancementId, this.factRef.id());
    }
}
