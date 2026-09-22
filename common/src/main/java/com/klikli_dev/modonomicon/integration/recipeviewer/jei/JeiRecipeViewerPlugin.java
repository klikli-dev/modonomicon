// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer.jei;

import java.util.Objects;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeLookupResult;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerPlugin;
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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

        return new RecipeLookupResult("jei", SCORE, screen -> {
            var focus = runtime.getJeiHelpers().getFocusFactory().createFocus(
                    uses ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT,
                    VanillaTypes.ITEM_STACK,
                    stack);
            runtime.getRecipesGui().show(focus);
        });
    }
}
