/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import net.minecraft.resources.Identifier;

/**
 * What shift-clicking a requirement row satisfies for players with game master permission. The server validates the request again.
 */
public sealed interface ScholarshipRowGrant {

    /**
     * Grants a research fact, e.g. a fact that an item or an advancement grants.
     *
     * @param factId the fact to grant
     */
    record Fact(Identifier factId) implements ScholarshipRowGrant {
    }

    /**
     * Raises a research value to the threshold, for values that no entry view or other hook can be used for.
     *
     * @param valueId   the value to raise
     * @param threshold the value the requirement needs
     */
    record Value(Identifier valueId, int threshold) implements ScholarshipRowGrant {
    }

    /**
     * Opens the entry that a value requirement is counting, which is how the player would satisfy it.
     *
     * @param entryId the entry to open
     */
    record Entry(Identifier entryId) implements ScholarshipRowGrant {
    }
}
