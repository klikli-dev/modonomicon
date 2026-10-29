/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data.load;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookLoadConditionContext;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

/**
 * Removes elements whose "load_condition" is not met.
 * Only conditions that support load-time evaluation (see {@link BookCondition#supportsLoadTime()}) are allowed.
 * <p>
 * For backwards compatibility with books made for older Modonomicon versions, a legacy "condition"
 * is still evaluated at load time with the old semantics (see {@link BookCondition#testOnLoad()}):
 * if it fails, the element is removed as well. "load_condition", if present, takes precedence.
 */
public class LoadConditionPreprocessor implements BookJsonPreprocessor {

    public static final String KEY = "load_condition";
    public static final String LEGACY_KEY = "condition";

    @Override
    public Optional<JsonObject> process(BookElementKind kind, BookLoadConditionContext context, JsonObject json) {
        if (json.has(KEY)) {
            var condition = BookCondition.fromJson(context.elementId(), GsonHelper.getAsJsonObject(json, KEY), context.registries());
            if (!condition.supportsLoadTime()) {
                throw new IllegalArgumentException("Condition type '" + condition.getType() + "' used in '" + KEY + "' of " + context.elementId() +
                        " cannot be evaluated at load time. Use it as 'condition' instead.");
            }

            return condition.testOnLoad(context) ? Optional.of(json) : Optional.empty();
        }

        // Backwards compatibility: books generated with older datagen only have "condition".
        // Keep the historic behaviour of removing content whose load time check fails,
        // so books that e.g. gate content behind mod_loaded keep working (and keep avoiding
        // load errors from content referencing missing mods).
        if (json.has(LEGACY_KEY) && json.get(LEGACY_KEY).isJsonObject()) {
            try {
                var legacyCondition = BookCondition.fromJson(context.elementId(), GsonHelper.getAsJsonObject(json, LEGACY_KEY), context.registries());
                if (!legacyCondition.testOnLoad()) {
                    Modonomicon.LOG.debug("Removing {} '{}' of book '{}' because its legacy load time condition is not met.",
                            kind.name().toLowerCase(), context.elementId(), context.bookId());
                    return Optional.empty();
                }
            } catch (Exception e) {
                // Let the regular parsing / error handling report malformed conditions.
                Modonomicon.LOG.debug("Skipping legacy load time check for {} '{}': {}", kind.name().toLowerCase(), context.elementId(), e.toString());
            }
        }

        return Optional.of(json);
    }
}
