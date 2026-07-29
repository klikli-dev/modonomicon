/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.book.runtime;

import net.minecraft.resources.Identifier;

/**
 * Server-side additive runtime content batch for extending existing books.
 */
public interface RuntimeBookContentBatch extends AutoCloseable {

    RuntimeBookSelection book(Identifier bookId);

    void finish();

    @Override
    void close();
}
