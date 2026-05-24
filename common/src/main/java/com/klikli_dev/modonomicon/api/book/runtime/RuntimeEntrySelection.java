/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.book.runtime;

import com.klikli_dev.modonomicon.book.page.BookPage;

/**
 * Entry-scoped runtime mutation view.
 */
public interface RuntimeEntrySelection {

    RuntimeEntrySelection addPage(BookPage page);
}
