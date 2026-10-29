/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data.load;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.context.BookLoadConditionContext;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Runs all registered {@link BookJsonPreprocessor}s on the raw JSON of categories and entries before they are parsed.
 * For entries, inline pages are processed as well, so they behave the same as standalone page files on newer versions.
 */
public class BookJsonPreprocessing {

    private static final List<BookJsonPreprocessor> PREPROCESSORS = new CopyOnWriteArrayList<>(List.of(
            new LoadConditionPreprocessor()
    ));

    public static void register(BookJsonPreprocessor preprocessor) {
        PREPROCESSORS.add(preprocessor);
    }

    /**
     * Evaluates only the load/legacy conditions of the given json, without running transforming preprocessors.
     * Used for file-based pages before merging, so that each final (merged or inline) page runs the full
     * pipeline in {@link #process} exactly once. May throw IllegalArgumentException for unsupported
     * load_condition types, same as the full pipeline.
     *
     * @return true if the element should be kept, false if it should be removed entirely.
     */
    public static boolean testConditions(BookElementKind kind, BookLoadConditionContext context, JsonObject json) {
        return new LoadConditionPreprocessor().process(kind, context, json).isPresent();
    }

    /**
     * @return the processed json, or empty if the element should be removed entirely.
     */
    public static Optional<JsonObject> process(BookElementKind kind, BookLoadConditionContext context, JsonObject json) {
        var result = Optional.of(json);
        for (var preprocessor : PREPROCESSORS) {
            result = preprocessor.process(kind, context, result.get());
            if (result.isEmpty()) {
                return result;
            }
        }

        if (kind == BookElementKind.ENTRY) {
            result = Optional.of(processInlinePages(context, result.get()));
        }

        return result;
    }

    private static JsonObject processInlinePages(BookLoadConditionContext entryContext, JsonObject entryJson) {
        if (!(entryJson.get("pages") instanceof JsonArray pages)) {
            return entryJson;
        }

        var processedPages = new JsonArray();
        boolean changed = false;
        for (var page : pages) {
            if (!(page instanceof JsonObject pageJson)) {
                processedPages.add(page);
                continue;
            }

            var processed = process(BookElementKind.PAGE, entryContext, pageJson);
            if (processed.isEmpty() || processed.get() != pageJson) {
                changed = true;
            }
            processed.ifPresent(processedPages::add);
        }

        if (!changed) {
            return entryJson;
        }

        var copy = entryJson.deepCopy();
        copy.add("pages", processedPages);
        return copy;
    }
}
