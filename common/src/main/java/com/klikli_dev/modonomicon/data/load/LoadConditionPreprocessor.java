/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data.load;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookLoadConditionContext;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

/**
 * Removes elements whose "load_condition" is not met.
 * Only conditions that support load-time evaluation (see {@link BookCondition#supportsLoadTime()}) are allowed.
 */
public class LoadConditionPreprocessor implements BookJsonPreprocessor {

    public static final String KEY = "load_condition";

    @Override
    public Optional<JsonObject> process(BookElementKind kind, BookLoadConditionContext context, JsonObject json) {
        if (!json.has(KEY)) {
            return Optional.of(json);
        }

        var condition = BookCondition.fromJson(context.elementId(), GsonHelper.getAsJsonObject(json, KEY), context.registries());
        if (!condition.supportsLoadTime()) {
            throw new IllegalArgumentException("Condition type '" + condition.getType() + "' used in '" + KEY + "' of " + context.elementId() +
                    " cannot be evaluated at load time. Use it as 'condition' instead.");
        }

        return condition.testOnLoad(context) ? Optional.of(json) : Optional.empty();
    }
}
