/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.book.runtime;

import com.klikli_dev.modonomicon.book.entries.BookEntry;
import net.minecraft.resources.Identifier;

/**
 * Category-scoped runtime mutation view.
 */
public interface RuntimeCategorySelection {

    RuntimeCategorySelection addEntry(BookEntry entry);

    RuntimeEntrySelection entry(Identifier entryId);
}
