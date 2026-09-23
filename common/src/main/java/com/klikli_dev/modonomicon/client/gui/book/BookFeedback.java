// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Shows the result of a recipe/usage lookup, including a toast when a viewer is available but found nothing.
 */
public final class BookFeedback {

    private static final long NO_RESULT_TOAST_DURATION_MS = 2000L;
    private static final SystemToast.SystemToastId NO_RESULT_TOAST_ID = new SystemToast.SystemToastId(NO_RESULT_TOAST_DURATION_MS);

    private BookFeedback() {
    }

    /**
     * Looks up the recipes/usages for the given stack in the best available recipe viewer, keeping the mouse
     * position. Shows a toast if a viewer is available but no recipe/usage was found.
     *
     * @param stack the item stack to look up
     * @param uses  true to look up usages, false to look up recipes
     */
    public static void showLookup(ItemStack stack, boolean uses) {
        BookGuiManager.get().keepMousePosition(() -> {
            boolean shown = RecipeViewerRegistry.show(stack, uses);
            if (!shown && RecipeViewerRegistry.isAnyAvailable()) {
                showNoResultToast();
            }
        });
    }

    public static void showNoResultToast() {
        var minecraft = Minecraft.getInstance();
        SystemToast.addOrUpdate(minecraft.gui.toastManager(), NO_RESULT_TOAST_ID,
                Component.translatable(ModonomiconConstants.I18n.Gui.RECIPE_LOOKUP_NO_RESULT), null);
    }
}
