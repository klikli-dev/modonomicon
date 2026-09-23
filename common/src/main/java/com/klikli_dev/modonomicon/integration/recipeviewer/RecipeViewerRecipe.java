// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Identifies a single recipe for a recipe viewer, without referencing any specific viewer.
 * <p>
 * The recipe can be identified either by {@link #recipeId()} (and optionally {@link #recipeTypeId()}), or - when
 * no (valid) id is available - by the {@link #input()}/{@link #output()}/{@link #workstation()} ingredient
 * conditions, which the viewer searches for.
 *
 * @param recipeId     the registry id of the recipe to render, if known
 * @param recipeTypeId the id of the vanilla recipe type the recipe belongs to, if known
 * @param background   whether the viewer should draw the recipe's own background (and border)
 * @param input        ingredient conditions that must be inputs of the recipe
 * @param output       ingredient conditions that must be outputs of the recipe
 * @param workstation  ingredient conditions that must be crafting stations of the recipe
 */
public record RecipeViewerRecipe(
        @Nullable Identifier recipeId,
        @Nullable Identifier recipeTypeId,
        boolean background,
        List<RVIngredient> input,
        List<RVIngredient> output,
        List<RVIngredient> workstation
) {

    /**
     * @return true if any ingredient condition is set, so the viewer can search for a matching recipe
     */
    public boolean hasQuery() {
        return RVIngredient.hasAny(this.input, this.output, this.workstation);
    }
}
