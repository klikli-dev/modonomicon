/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Nbt;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.registration.ISubtypeRegistration;
//import mezz.jei.api.runtime.IJeiRuntime;
//import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

//TODO: Re-enable jei integration once jei is updated
public class ModonomiconJeiIntegration {

    public static boolean isJeiLoaded(){
        return ModList.get().isLoaded("jei");
    }

    public static boolean isJEIRecipesGuiOpen() {
        if (isJeiLoaded()) {
//            return ModonomiconJeiHelper.isJEIRecipesGuiOpen();
        } else {
            Modonomicon.LOGGER.warn("Attempted check if JEI recipes GUI is open without JEI installed!");
        }
        return false;
    }

    public static void showRecipe(ItemStack stack) {
        if (isJeiLoaded()) {
//            ModonomiconJeiHelper.showRecipe(stack);
        } else {
            Modonomicon.LOGGER.warn("Attempted to show JEI recipe for {} without JEI installed!", ForgeRegistries.ITEMS.getKey(stack.getItem()));
        }
    }

    public static void showUses(ItemStack stack) {
        if (isJeiLoaded()) {
//            ModonomiconJeiHelper.showUses(stack);
        } else {
            Modonomicon.LOGGER.warn("Attempted to show JEI usages for {} without JEI installed!", ForgeRegistries.ITEMS.getKey(stack.getItem()));
        }
    }

    public static class ModonomiconJeiHelper {
//        public static void showRecipe(ItemStack stack) {
//            var focus = ModonomiconJeiPlugin.jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);
//            ModonomiconJeiPlugin.jeiRuntime.getRecipesGui().show(focus);
//        }
//
//        public static void showUses(ItemStack stack) {
//            var focus = ModonomiconJeiPlugin.jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, stack);
//            ModonomiconJeiPlugin.jeiRuntime.getRecipesGui().show(focus);
//        }
//
//        public static boolean isJEIRecipesGuiOpen() {
//            return Minecraft.getInstance().screen instanceof IRecipesGui;
//        }
    }

//    @JeiPlugin
//    public static class ModonomiconJeiPlugin implements IModPlugin {
//        private static final ResourceLocation UID = new ResourceLocation(ModonomiconAPI.ID, ModonomiconAPI.ID);
//
//        private static IJeiRuntime jeiRuntime;
//
//        @NotNull
//        @Override
//        public ResourceLocation getPluginUid() {
//            return UID;
//        }
//
//        @Override
//        public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
//            ModonomiconJeiPlugin.jeiRuntime = jeiRuntime;
//        }
//
//        @Override
//        public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
//
//            registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ItemRegistry.MODONOMICON.get(), (stack, context) -> {
//                if (!stack.hasTag() || !stack.getTag().contains(Nbt.ITEM_BOOK_ID_TAG)) {
//                    return "";
//                }
//                return stack.getTag().getString(Nbt.ITEM_BOOK_ID_TAG);
//            });
//        }
//    }
}
