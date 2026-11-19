/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.research.action.ResearchAction;
import net.minecraft.resources.Identifier;

/**
 * What clicking a requirement row asks for. The row only describes the action; {@link ResearchRequirementInteractions#perform}
 * interprets it, so rows stay plain data.
 */
public sealed interface RowAction {

    /**
     * Performs a research action that satisfies a fact of a node.
     *
     * @param nodeId the node the fact is a requirement of
     * @param factId the fact the action satisfies
     * @param action the action attached to the fact, used for the tooltip and the local affordability check
     */
    record Perform(Identifier nodeId, Identifier factId, ResearchAction action) implements RowAction {
    }

    /**
     * Opens an entry, e.g. the entry that grants a fact.
     *
     * @param bookId the book of the entry
     * @param entryId the entry to open
     */
    record OpenEntry(Identifier bookId, Identifier entryId) implements RowAction {
    }
}
