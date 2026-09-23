// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.integration.recipeviewer.ItemRVIngredient;
import com.klikli_dev.modonomicon.integration.recipeviewer.RVIngredient;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeLookupResult;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerPlugin;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRecipe;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRecipePreview;
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class JeiRecipeViewerPlugin implements IModPlugin, RecipeViewerPlugin {

    private static final Identifier UID = Identifier.fromNamespaceAndPath(ModonomiconAPI.ID, ModonomiconAPI.ID);
    private static final float SCORE = 0.9f;

    private static IJeiRuntime runtime;

    @NotNull
    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ItemRegistry.MODONOMICON.get(), (stack, context) -> {
            if (!stack.has(DataComponentRegistry.BOOK_ID.get())) {
                return "";
            }
            return Objects.toString(stack.get(DataComponentRegistry.BOOK_ID.get()));
        });
    }

    @Override
    public boolean isAvailable() {
        return runtime != null;
    }

    @Override
    public RecipeLookupResult lookup(ItemStack stack, boolean uses) {
        if (!this.isAvailable()) {
            return RecipeLookupResult.FAIL;
        }

        var focus = runtime.getJeiHelpers().getFocusFactory().createFocus(
                uses ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT,
                VanillaTypes.ITEM_STACK,
                stack);

        //only offer the lookup if there actually is a recipe/usage for this stack
        boolean found = runtime.getRecipeManager().createRecipeCategoryLookup()
                .limitFocus(List.of(focus))
                .get()
                .findAny()
                .isPresent();
        if (!found) {
            return RecipeLookupResult.FAIL;
        }

        return new RecipeLookupResult("jei", SCORE, screen -> runtime.getRecipesGui().show(focus));
    }

    @Override
    public float previewPriority() {
        return SCORE;
    }

    @Nullable
    @Override
    public RecipeViewerRecipePreview createRecipePreview(RecipeViewerRecipe recipe) {
        if (!this.isAvailable()) {
            return null;
        }

        try {
            //prefer the recipe id if it resolves to a recipe
            if (recipe.recipeId() != null && recipe.recipeTypeId() != null) {
                var preview = this.createPreviewById(recipe);
                if (preview != null) {
                    return preview;
                }
            }

            //fall back to searching by the ingredient conditions
            if (recipe.hasQuery()) {
                return this.createPreviewByIngredients(recipe);
            }

            return null;
        } catch (RuntimeException | LinkageError e) {
            Modonomicon.LOG.warn("Failed to create a JEI recipe preview for {}.", recipe.recipeId(), e);
            return null;
        }
    }

    @Nullable
    private RecipeViewerRecipePreview createPreviewById(RecipeViewerRecipe recipe) {
        var recipeManager = runtime.getRecipeManager();

        //fast path: for vanilla and most modded categories the JEI recipe type uid equals the vanilla recipe
        //type id, so we can resolve the category directly.
        if (recipe.recipeTypeId() != null) {
            var recipeType = recipeManager.getRecipeType(recipe.recipeTypeId()).orElse(null);
            if (recipeType != null) {
                var category = recipeManager.getRecipeCategory(recipeType);
                var drawable = this.createLayoutDrawable(recipeManager, category, recipe.recipeId(), recipe.background());
                if (drawable != null) {
                    return new JeiRecipeViewerRecipePreview(drawable, runtime);
                }
            }
        }

        //fallback: some modded categories register a JEI recipe type uid that differs from the vanilla recipe
        //type id, so the category cannot be resolved from the type id alone. Search every category by recipe id.
        for (IRecipeCategory<?> category : recipeManager.createRecipeCategoryLookup().includeHidden().get().toList()) {
            var drawable = this.createLayoutDrawable(recipeManager, category, recipe.recipeId(), recipe.background());
            if (drawable != null) {
                return new JeiRecipeViewerRecipePreview(drawable, runtime);
            }
        }

        return null;
    }

    /**
     * Searches for the first recipe that matches all ingredient conditions.
     * <p>
     * JEI combines multiple focuses as a union (OR), so we use the most selective condition as the primary lookup
     * and then verify every candidate against the remaining conditions. This yields AND semantics.
     */
    @Nullable
    private RecipeViewerRecipePreview createPreviewByIngredients(RecipeViewerRecipe recipe) {
        var focusFactory = runtime.getJeiHelpers().getFocusFactory();
        var recipeManager = runtime.getRecipeManager();

        var inputFocuses = toFocuses(focusFactory, RecipeIngredientRole.INPUT, recipe.input());
        var outputFocuses = toFocuses(focusFactory, RecipeIngredientRole.OUTPUT, recipe.output());
        var workstationFocuses = toFocuses(focusFactory, RecipeIngredientRole.CRAFTING_STATION, recipe.workstation());

        var primaryFocuses = !outputFocuses.isEmpty() ? outputFocuses
                : !inputFocuses.isEmpty() ? inputFocuses
                : workstationFocuses;
        if (primaryFocuses.isEmpty()) {
            return null;
        }

        var allFocuses = new ArrayList<IFocus<?>>();
        allFocuses.addAll(inputFocuses);
        allFocuses.addAll(outputFocuses);
        allFocuses.addAll(workstationFocuses);
        var focusGroup = focusFactory.createFocusGroup(allFocuses);

        var categories = recipeManager.createRecipeCategoryLookup().limitFocus(primaryFocuses).get().toList();
        for (IRecipeCategory<?> category : categories) {
            var drawable = this.findLayout(recipeManager, category, primaryFocuses, inputFocuses, outputFocuses, workstationFocuses, focusGroup, recipe.background());
            if (drawable != null) {
                return new JeiRecipeViewerRecipePreview(drawable, runtime);
            }
        }

        return null;
    }

    /**
     * Finds the first recipe of the given category that matches all focus sets.
     */
    @Nullable
    private <T> IRecipeLayoutDrawable<T> findLayout(
            IRecipeManager recipeManager,
            IRecipeCategory<T> category,
            List<IFocus<?>> primaryFocuses,
            List<IFocus<?>> inputFocuses,
            List<IFocus<?>> outputFocuses,
            List<IFocus<?>> workstationFocuses,
            IFocusGroup focusGroup,
            boolean background
    ) {
        var candidates = recipeManager.createRecipeLookup(category.getRecipeType()).limitFocus(primaryFocuses).get().toList();
        for (var candidate : candidates) {
            if (!this.matchesAllFocuses(recipeManager, category, candidate, inputFocuses)
                    || !this.matchesAllFocuses(recipeManager, category, candidate, outputFocuses)
                    || !this.matchesAllFocuses(recipeManager, category, candidate, workstationFocuses)) {
                continue;
            }

            var drawable = this.createLayoutDrawable(recipeManager, category, candidate, focusGroup, background);
            if (drawable != null) {
                return drawable;
            }
        }

        return null;
    }

    @Nullable
    private <T> IRecipeLayoutDrawable<T> createLayoutDrawable(IRecipeManager recipeManager, IRecipeCategory<T> category, T recipe, IFocusGroup focusGroup, boolean background) {
        if (!background) {
            //pass a no-op background and no border padding, so the recipe draws without its own background
            return recipeManager.createRecipeLayoutDrawable(category, recipe, focusGroup, JeiBlankScalableDrawable.INSTANCE, 0).orElse(null);
        }
        return recipeManager.createRecipeLayoutDrawable(category, recipe, focusGroup).orElse(null);
    }

    private static List<IFocus<?>> toFocuses(IFocusFactory focusFactory, RecipeIngredientRole role, List<RVIngredient> ingredients) {
        var focuses = new ArrayList<IFocus<?>>();
        for (var ingredient : ingredients) {
            if (ingredient instanceof ItemRVIngredient item) {
                focuses.add(focusFactory.createFocus(role, VanillaTypes.ITEM_STACK, item.toItemStack()));
            }
        }
        return focuses;
    }

    private <T> boolean matchesAllFocuses(IRecipeManager recipeManager, IRecipeCategory<T> category, T candidate, List<IFocus<?>> focuses) {
        for (var focus : focuses) {
            boolean matches = recipeManager.createRecipeLookup(category.getRecipeType())
                    .limitFocus(List.of(focus))
                    .get()
                    .anyMatch(recipe -> recipe.equals(candidate));
            if (!matches) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private <T> IRecipeLayoutDrawable<T> createLayoutDrawable(IRecipeManager recipeManager, IRecipeCategory<T> category, Identifier recipeId, boolean background) {
        var focusGroup = runtime.getJeiHelpers().getFocusFactory().getEmptyFocusGroup();
        var recipe = recipeManager.createRecipeLookup(category.getRecipeType())
                .includeHidden()
                .get()
                .filter(candidate -> recipeId.equals(category.getIdentifier(candidate)))
                .findFirst()
                .orElse(null);

        if (recipe == null) {
            return null;
        }

        return this.createLayoutDrawable(recipeManager, category, recipe, focusGroup, background);
    }
}
