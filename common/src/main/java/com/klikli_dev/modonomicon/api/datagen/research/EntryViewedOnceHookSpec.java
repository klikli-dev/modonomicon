/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import net.minecraft.resources.Identifier;

/**
 * Authoring-time research ingress declaration for the {@code entry_viewed_once} trigger family.
 *
 * @param id the canonical hook id
 * @param entryId the book entry whose first view acts as ingress into research
 * @param factRef the fact granted when that ingress event occurs
 */
public record EntryViewedOnceHookSpec(Identifier id, Identifier entryId, ResearchFactRef factRef) {
    /**
     * Compiles this ingress declaration into the canonical research hook record.
     */
    public ResearchHookDefinition toDefinition() {
        return new ResearchHookDefinition(
                this.id,
                ResearchHookDefinition.TriggerType.ENTRY_VIEWED_ONCE,
                this.entryId,
                this.factRef.id()
        );
    }
}
