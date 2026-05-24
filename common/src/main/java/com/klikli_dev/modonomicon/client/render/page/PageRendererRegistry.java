/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.*;
import com.klikli_dev.modonomicon.fluid.FluidHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PageRendererRegistry {
    static final Set<Item> ITEMS_NOT_TO_RENDER = ObjectSets.synchronize(new ObjectOpenHashSet<>());
    static final Set<Fluid> FLUIDS_NOT_TO_RENDER = ObjectSets.synchronize(new ObjectOpenHashSet<>());
    private static final Map<Identifier, PageRendererFactory> pageRenderers = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    /**
     * Call from client setup
     */
    public static void registerPageRenderers() {
        registerDefaultPageRenderers();
    }

    private static void registerDefaultPageRenderers() {
        registerPageRenderer(BookTextPage.ID, p -> new BookTextPageRenderer((BookTextPage) p));
        registerPageRenderer(BookMultiblockPage.ID, p -> new BookMultiblockPageRenderer((BookMultiblockPage) p));
        registerPageRenderer(BookCraftingRecipePage.ID, p -> new BookCraftingRecipePageRenderer((BookCraftingRecipePage) p));
        registerPageRenderer(BookSmeltingRecipePage.ID, p -> new BookProcessingRecipePageRenderer<>((BookSmeltingRecipePage) p) {
        });
        registerPageRenderer(BookSmokingRecipePage.ID, p -> new BookProcessingRecipePageRenderer<>((BookSmokingRecipePage) p) {
        });
        registerPageRenderer(BookCampfireCookingRecipePage.ID, p -> new BookProcessingRecipePageRenderer<>((BookCampfireCookingRecipePage) p) {
        });
        registerPageRenderer(BookBlastingRecipePage.ID, p -> new BookProcessingRecipePageRenderer<>((BookBlastingRecipePage) p) {
        });
        registerPageRenderer(BookStonecuttingRecipePage.ID, p -> new BookProcessingRecipePageRenderer<>((BookStonecuttingRecipePage) p) {
        });
        registerPageRenderer(BookSmithingRecipePage.ID, p -> new BookSmithingRecipePageRenderer((BookSmithingRecipePage) p) {
        });
        registerPageRenderer(BookSpotlightPage.ID, p -> new BookSpotlightPageRenderer((BookSpotlightPage) p) {
        });
        registerPageRenderer(BookEmptyPage.ID, p -> new BookEmptyPageRenderer((BookEmptyPage) p) {
        });
        registerPageRenderer(BookEntityPage.ID, p -> new BookEntityPageRenderer((BookEntityPage) p) {
        });
        registerPageRenderer(BookImagePage.ID, p -> new BookImagePageRenderer((BookImagePage) p) {
        });
    }

    /**
     * Call from client setup
     */
    public static void registerPageRenderer(Identifier id, PageRendererFactory factory) {
        pageRenderers.put(id, factory);
    }

    public static PageRendererFactory getPageRenderer(Identifier id) {
        var renderer = pageRenderers.get(id);
        if (renderer == null) {
            throw new IllegalArgumentException("No page renderer registered for page type " + id);
        }
        return renderer;
    }

    /**
     * Any items registered here, will not be rendered by the renderIngredient / renderItemStacks / renderItemStack methods.
     * Can be called at any time.
     */
    public static void registerItemStackNotToRender(ItemStack stack) {
        ITEMS_NOT_TO_RENDER.add(stack.getItem());
    }

    /**
     * Any fluids registered here, will not be rendered by the renderFluidStacks / renderFluidStack methods.
     * Can be called at any time.
     */
    public static void registerFluidStackNotToRender(FluidHolder stack) {
        FLUIDS_NOT_TO_RENDER.add(stack.getFluid().value());
    }

    /**
     * Returns false, if the given stack should not be rendered in the book, e.g. in recipes.
     */
    public static boolean isRenderable(ItemStack stack) {
        return !ITEMS_NOT_TO_RENDER.contains(stack.getItem());
    }

    /**
     * Returns false, if the given stack should not be rendered in the book, e.g. in recipes.
     */
    public static boolean isRenderable(FluidHolder stack) {
        return !FLUIDS_NOT_TO_RENDER.contains(stack.getFluid());
    }

    /**
     * Returns only those stacks in the list, that should be rendered in the book, e.g. in recipes.
     */
    public static List<ItemStack> filterRenderableItemStacks(Collection<ItemStack> stacks) {
        return stacks.stream().filter(stack -> !ITEMS_NOT_TO_RENDER.contains(stack.getItem())).toList();
    }

    /**
     * Returns only those stacks in the list, that should be rendered in the book, e.g. in recipes.
     */
    public static List<FluidHolder> filterRenderableFluidStacks(Collection<FluidHolder> stacks) {
        return stacks.stream().filter(stack -> !FLUIDS_NOT_TO_RENDER.contains(stack.getFluid())).toList();
    }
}
