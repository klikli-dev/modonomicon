// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer;

import net.minecraft.world.item.ItemStack;

/**
 * A bridge to a single recipe viewer mod (e.g. JEI).
 * <p>
 * Implementations must never be referenced from code that runs without the viewer mod being loaded.
 * They are instantiated reflectively by {@link RecipeViewerRegistry}.
 */
public interface RecipeViewerPlugin {

    /**
     * @return true if this viewer is ready to serve lookups, e.g. its runtime has been initialized
     */
    boolean isAvailable();

    /**
     * Looks up the recipes or usages for the given stack.
     *
     * @param stack the item stack to look up
     * @param uses  true to look up usages, false to look up recipes
     * @return the lookup result, or {@link RecipeLookupResult#FAIL} if this viewer cannot serve the request
     */
    RecipeLookupResult lookup(ItemStack stack, boolean uses);
}
