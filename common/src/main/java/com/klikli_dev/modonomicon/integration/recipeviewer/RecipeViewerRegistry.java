// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry of all available recipe viewer integrations.
 * <p>
 * To support another recipe viewer, implement {@link RecipeViewerPlugin} and register it via
 * {@link #register(String, String)}. Plugins are loaded reflectively and only when their mod is present,
 * so the viewer API classes are never touched if that mod is absent.
 */
public final class RecipeViewerRegistry {

    private static final List<RecipeViewerPlugin> PLUGINS = new ArrayList<>();

    static {
        register("jei", "com.klikli_dev.modonomicon.integration.recipeviewer.jei.JeiRecipeViewerPlugin");
    }

    private RecipeViewerRegistry() {
    }

    /**
     * Registers a recipe viewer integration.
     * <p>
     * Does nothing if the given mod is not loaded, so it is safe to call unconditionally. The plugin is
     * instantiated reflectively, so the viewer API classes are never touched if that mod is absent.
     *
     * @param modId     the id of the mod that provides the viewer, e.g. {@code "jei"}
     * @param className the fully qualified name of the {@link RecipeViewerPlugin} implementation
     */
    public static void register(String modId, String className) {
        if (!Services.PLATFORM.isModLoaded(modId)) {
            return;
        }

        try {
            PLUGINS.add((RecipeViewerPlugin) Class.forName(className).getDeclaredConstructor().newInstance());
        } catch (ReflectiveOperationException | LinkageError e) {
            Modonomicon.LOG.warn("Failed to initialize recipe viewer integration for {}.", modId, e);
        }
    }

    /**
     * @return true if at least one recipe viewer is installed and ready to serve lookups
     */
    public static boolean isAnyAvailable() {
        return PLUGINS.stream().anyMatch(RecipeViewerPlugin::isAvailable);
    }

    /**
     * Shows the recipes or usages for the given stack in the highest priority available recipe viewer.
     * Does nothing if no viewer is available or none can serve the request.
     *
     * @param stack the item stack to look up
     * @param uses  true to show usages, false to show recipes
     */
    public static void show(ItemStack stack, boolean uses) {
        var screen = ClientServices.GUI.getCurrentScreen();

        RecipeLookupResult selected = null;
        for (var plugin : PLUGINS) {
            RecipeLookupResult result;
            try {
                result = plugin.lookup(stack, uses);
            } catch (Throwable e) {
                Modonomicon.LOG.warn("Recipe viewer plugin failed to look up {} for {}.",
                        uses ? "usages" : "recipes", BuiltInRegistries.ITEM.getKey(stack.getItem()), e);
                continue;
            }

            if (result == null || result.isFail()) {
                continue;
            }

            if (selected == null || result.score() > selected.score()) {
                selected = result;
            }
        }

        if (selected != null) {
            selected.action().accept(screen);
        } else {
            Modonomicon.LOG.warn("No recipe viewer could show {} for {}.",
                    uses ? "usages" : "recipes", BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }
    }
}
