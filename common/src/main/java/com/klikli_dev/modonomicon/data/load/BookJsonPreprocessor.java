/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data.load;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.context.BookLoadConditionContext;

import java.util.Optional;

/**
 * A load-time step that operates on the raw JSON of a category, entry or page before it is parsed.
 */
@FunctionalInterface
public interface BookJsonPreprocessor {

    /**
     * @param kind    the kind of element the json represents
     * @param context the load context of the element
     * @param json    the raw json of the element. Must not be mutated; return a copy instead.
     * @return the (possibly transformed) json, or empty to remove the element entirely.
     */
    Optional<JsonObject> process(BookElementKind kind, BookLoadConditionContext context, JsonObject json);
}
