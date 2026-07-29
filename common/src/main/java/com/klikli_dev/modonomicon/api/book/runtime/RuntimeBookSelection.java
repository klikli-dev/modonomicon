/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.book.runtime;

import com.klikli_dev.modonomicon.book.BookCategory;
import net.minecraft.resources.Identifier;

/**
 * Book-scoped runtime mutation view.
 */
public interface RuntimeBookSelection {

    RuntimeBookSelection addCategory(BookCategory category);

    RuntimeCategorySelection category(Identifier categoryId);
}
