// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.jei;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModonomiconJeiIntegrationImpl implements ModonomiconJeiIntegration {
    public boolean isLoaded() {
        return Services.PLATFORM.isModLoaded("jei") && ModonomiconJeiPlugin.isRuntimeAvailable();
    }

    public boolean isRecipesGuiOpen() {
        if (this.isLoaded()) {
            return ModonomiconJeiHelper.isJEIRecipesGuiOpen();
        } else {
            Modonomicon.LOG.warn("Attempted check if JEI recipes GUI is open without JEI installed!");
        }
        return false;
    }

    public void showRecipe(ItemStack stack) {
        if (this.isLoaded()) {
            ModonomiconJeiHelper.showRecipe(stack);
        } else {
            Modonomicon.LOG.warn("Attempted to show JEI recipe for {} without JEI installed!", BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }
    }

    public void showUses(ItemStack stack) {
        if (this.isLoaded()) {
            ModonomiconJeiHelper.showUses(stack);
        } else {
            Modonomicon.LOG.warn("Attempted to show JEI usages for {} without JEI installed!", BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }
    }

    public static class ModonomiconJeiHelper {
        public static void showRecipe(ItemStack stack) {
            var focus = ModonomiconJeiPlugin.jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);
            ModonomiconJeiPlugin.jeiRuntime.getRecipesGui().show(focus);
        }

        public static void showUses(ItemStack stack) {
            var focus = ModonomiconJeiPlugin.jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, stack);
            ModonomiconJeiPlugin.jeiRuntime.getRecipesGui().show(focus);
        }

        public static boolean isJEIRecipesGuiOpen() {
            return ClientServices.GUI.getCurrentScreen() instanceof IRecipesGui;
        }
    }

    @JeiPlugin
    public static class ModonomiconJeiPlugin implements IModPlugin {
        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ModonomiconAPI.ID, ModonomiconAPI.ID);

        private static IJeiRuntime jeiRuntime;

        public static boolean isRuntimeAvailable() {
            return jeiRuntime != null;
        }

        @NotNull
        @Override
        public ResourceLocation getPluginUid() {
            return UID;
        }

        @Override
        public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
            ModonomiconJeiPlugin.jeiRuntime = jeiRuntime;
        }

        @Override
        public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {

            registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ItemRegistry.MODONOMICON.get(), (stack, context) -> {
                if (!stack.has(DataComponentRegistry.BOOK_ID.get())) {
                    return "";
                }
                return stack.get(DataComponentRegistry.BOOK_ID.get()).toString();
            });
        }
    }
}
