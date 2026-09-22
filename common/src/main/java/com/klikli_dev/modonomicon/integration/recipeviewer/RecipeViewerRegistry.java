// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.recipeviewer;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.ClientServices;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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

    /**
     * All registered plugins, kept sorted by descending {@link RecipeViewerPlugin#previewPriority()} so that preview
     * lookups can iterate it directly without sorting on every call.
     */
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
            var plugin = (RecipeViewerPlugin) Class.forName(className).getDeclaredConstructor().newInstance();
            int index = 0;
            while (index < PLUGINS.size() && PLUGINS.get(index).previewPriority() >= plugin.previewPriority()) {
                index++;
            }
            PLUGINS.add(index, plugin);
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
     * @return true if a viewer found and showed a recipe/usage, false otherwise
     */
    public static boolean show(ItemStack stack, boolean uses) {
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
            return true;
        }

        Modonomicon.LOG.warn("No recipe viewer could show {} for {}.",
                uses ? "usages" : "recipes", BuiltInRegistries.ITEM.getKey(stack.getItem()));
        return false;
    }

    /**
     * Prepares the given recipe for rendering in a Modonomicon page using the highest priority viewer that can
     * render it.
     *
     * @param recipe the recipe to render
     * @return a renderable preview, or null if no viewer is available or can render the recipe
     */
    @Nullable
    public static RecipeViewerRecipePreview createRecipePreview(RecipeViewerRecipe recipe) {
        for (var plugin : PLUGINS) {
            if (!plugin.isAvailable()) {
                continue;
            }

            RecipeViewerRecipePreview preview;
            try {
                preview = plugin.createRecipePreview(recipe);
            } catch (Throwable e) {
                Modonomicon.LOG.warn("Recipe viewer plugin {} failed to create a preview for recipe {}.",
                        plugin.getClass().getName(), recipe.recipeId(), e);
                continue;
            }

            if (preview != null) {
                return preview;
            }
        }

        return null;
    }
}
