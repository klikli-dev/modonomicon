/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.klikli_dev.modonomicon.book.page.RecipeAttribute;
import com.klikli_dev.modonomicon.integration.recipeviewer.ItemRVIngredient;
import com.klikli_dev.modonomicon.integration.recipeviewer.RVIngredient;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen model for the per-recipe attributes of a {@link BookViewerRecipePageModel}.
 * <p>
 * Mirrors {@link RecipeAttribute}, but only exposes API/model types. The ingredient conditions are given as
 * {@link ItemStackTemplate}s, so they can be created in datagen before the item components are bound.
 */
public class RecipeAttributeModel {

    protected float scale = 1.0f;
    protected boolean background = true;
    protected List<ItemStackTemplate> input = List.of();
    protected List<ItemStackTemplate> output = List.of();
    protected List<ItemStackTemplate> workstation = List.of();

    public static RecipeAttributeModel create() {
        return new RecipeAttributeModel();
    }

    public float getScale() {
        return this.scale;
    }

    public boolean getBackground() {
        return this.background;
    }

    public List<ItemStackTemplate> getInput() {
        return this.input;
    }

    public List<ItemStackTemplate> getOutput() {
        return this.output;
    }

    public List<ItemStackTemplate> getWorkstation() {
        return this.workstation;
    }

    /**
     * Sets the scale the recipe is rendered at. Defaults to {@code 1}.
     * The recipe is always clamped to fit the page, even if a larger scale is given.
     */
    public RecipeAttributeModel withScale(float scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Sets whether the viewer draws the recipe's own background (and border). Defaults to {@code true}.
     */
    public RecipeAttributeModel withBackground(boolean background) {
        this.background = background;
        return this;
    }

    public RecipeAttributeModel withInput(ItemStackTemplate... input) {
        return this.withInput(List.of(input));
    }

    public RecipeAttributeModel withInput(List<ItemStackTemplate> input) {
        this.input = List.copyOf(input);
        return this;
    }

    public RecipeAttributeModel withOutput(ItemStackTemplate... output) {
        return this.withOutput(List.of(output));
    }

    public RecipeAttributeModel withOutput(List<ItemStackTemplate> output) {
        this.output = List.copyOf(output);
        return this;
    }

    public RecipeAttributeModel withWorkstation(ItemStackTemplate... workstation) {
        return this.withWorkstation(List.of(workstation));
    }

    public RecipeAttributeModel withWorkstation(List<ItemStackTemplate> workstation) {
        this.workstation = List.copyOf(workstation);
        return this;
    }

    RecipeAttribute toRecipeAttribute() {
        return new RecipeAttribute(
                this.scale,
                this.background,
                toIngredients(this.input),
                toIngredients(this.output),
                toIngredients(this.workstation)
        );
    }

    private static List<RVIngredient> toIngredients(List<ItemStackTemplate> values) {
        var ingredients = new ArrayList<RVIngredient>(values.size());
        for (var value : values) {
            ingredients.add(ItemRVIngredient.of(value));
        }
        return ingredients;
    }
}
