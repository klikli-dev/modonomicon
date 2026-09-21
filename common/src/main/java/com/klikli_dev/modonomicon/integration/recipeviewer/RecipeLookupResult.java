// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer;

import net.minecraft.client.gui.screens.Screen;

import java.util.function.Consumer;

/**
 * The outcome of a {@link RecipeViewerPlugin#lookup} call.
 *
 * @param source the id of the viewer that produced this result, used for logging
 * @param score  the priority of this result; the highest score wins when multiple viewers are installed
 * @param action the action that opens the requested recipe/usage view
 */
public record RecipeLookupResult(String source, float score, Consumer<Screen> action) {

    public static final RecipeLookupResult FAIL = new RecipeLookupResult("n/a", 0, screen -> {
    });

    public boolean isFail() {
        return this.score <= 0;
    }
}
