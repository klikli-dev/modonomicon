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
 * Runs all registered {@link BookJsonPreprocessor}s on the raw JSON of categories, entries and pages before they are parsed.
 * For entries, inline pages are processed as well, so they behave the same as page files.
 */
public class BookJsonPreprocessing {

    private static final List<BookJsonPreprocessor> PREPROCESSORS = new CopyOnWriteArrayList<>(List.of(
            new LoadConditionPreprocessor()
    ));

    public static void register(BookJsonPreprocessor preprocessor) {
        PREPROCESSORS.add(preprocessor);
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
            result = Optional.of(processInlineElements(context, result.get(), "pages", BookElementKind.PAGE));
            result = Optional.of(processInlineElements(context, result.get(), "tabs", BookElementKind.TAB));
        }

        return result;
    }

    private static JsonObject processInlineElements(BookLoadConditionContext entryContext, JsonObject entryJson, String key, BookElementKind elementKind) {
        if (!(entryJson.get(key) instanceof JsonArray elements)) {
            return entryJson;
        }

        var processedElements = new JsonArray();
        boolean changed = false;
        for (var element : elements) {
            if (!(element instanceof JsonObject elementJson)) {
                processedElements.add(element);
                continue;
            }

            var processed = process(elementKind, entryContext, elementJson);
            if (processed.isEmpty() || processed.get() != elementJson) {
                changed = true;
            }
            processed.ifPresent(processedElements::add);
        }

        if (!changed) {
            return entryJson;
        }

        var copy = entryJson.deepCopy();
        copy.add(key, processedElements);
        return copy;
    }
}
