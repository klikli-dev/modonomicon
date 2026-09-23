/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A recipe page that delegates rendering of its recipes to an installed recipe viewer (e.g. JEI).
 * <p>
 * Unlike the vanilla recipe pages, this page does not draw the recipe itself. It only references the recipe by
 * id, and the client-side {@code BookViewerRecipePageRenderer} asks the {@link com.klikli_dev.modonomicon.integration.recipeviewer.RecipeViewerRegistry}
 * to render it. This makes the page support any recipe layout a viewer can render, including modded ones.
 */
public class BookViewerRecipePage extends BookRecipePage<Recipe<?>> {

    public static final Identifier ID = Modonomicon.loc("viewer_recipe");

    public static final MapCodec<BookViewerRecipePage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BookRecipePage.JSON_COMMON_CODEC.forGetter(BookViewerRecipePage::toJsonDataHolder),
            RecipeAttribute.CODEC.optionalFieldOf("recipe1", RecipeAttribute.DEFAULT).forGetter(BookViewerRecipePage::getRecipe1),
            RecipeAttribute.CODEC.optionalFieldOf("recipe2", RecipeAttribute.DEFAULT).forGetter(BookViewerRecipePage::getRecipe2)
    ).apply(instance, BookViewerRecipePage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookViewerRecipePage> STREAM_CODEC = StreamCodec.composite(
            NETWORK_COMMON_STREAM_CODEC, BookViewerRecipePage::toNetworkDataHolder,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), page -> Optional.ofNullable(page.recipeType1),
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), page -> Optional.ofNullable(page.recipeType2),
            RecipeAttribute.STREAM_CODEC, BookViewerRecipePage::getRecipe1,
            RecipeAttribute.STREAM_CODEC, BookViewerRecipePage::getRecipe2,
            (common, recipeType1, recipeType2, recipe1, recipe2) ->
                    new BookViewerRecipePage(common, recipeType1.orElse(null), recipeType2.orElse(null), recipe1, recipe2)
    );

    /**
     * The id of the vanilla recipe type the first recipe belongs to. Resolved server-side during
     * {@link #build(Level, BookContentEntry, int)} and synced to the client.
     */
    @Nullable
    protected Identifier recipeType1;

    /**
     * The id of the vanilla recipe type the second recipe belongs to. Resolved server-side during
     * {@link #build(Level, BookContentEntry, int)} and synced to the client.
     */
    @Nullable
    protected Identifier recipeType2;

    /**
     * Attributes for the first recipe, e.g. the scale it is rendered at.
     */
    protected RecipeAttribute recipe1 = RecipeAttribute.DEFAULT;

    /**
     * Attributes for the second recipe, e.g. the scale it is rendered at.
     */
    protected RecipeAttribute recipe2 = RecipeAttribute.DEFAULT;

    public BookViewerRecipePage(JsonDataHolder common) {
        this(common, RecipeAttribute.DEFAULT, RecipeAttribute.DEFAULT);
    }

    public BookViewerRecipePage(JsonDataHolder common, RecipeAttribute recipe1, RecipeAttribute recipe2) {
        super(common);
        this.recipe1 = recipe1;
        this.recipe2 = recipe2;
    }

    public BookViewerRecipePage(NetworkDataHolder common) {
        this(common, null, null, RecipeAttribute.DEFAULT, RecipeAttribute.DEFAULT);
    }

    private BookViewerRecipePage(NetworkDataHolder common, @Nullable Identifier recipeType1, @Nullable Identifier recipeType2, RecipeAttribute recipe1, RecipeAttribute recipe2) {
        super(common);
        this.recipeType1 = recipeType1;
        this.recipeType2 = recipeType2;
        this.recipe1 = recipe1;
        this.recipe2 = recipe2;
    }

    @Nullable
    public Identifier getRecipeType1() {
        return this.recipeType1;
    }

    @Nullable
    public Identifier getRecipeType2() {
        return this.recipeType2;
    }

    public RecipeAttribute getRecipe1() {
        return this.recipe1;
    }

    public RecipeAttribute getRecipe2() {
        return this.recipe2;
    }

    @Override
    public void build(Level level, BookContentEntry parentEntry, int pageNum) {
        super.build(level, parentEntry, pageNum);

        // on the server we resolve the recipe type id, on the client it is sent via the stream codec
        if (level instanceof ServerLevel serverLevel) {
            this.recipeType1 = this.getRecipeTypeId(serverLevel, this.recipeKey1);
            this.recipeType2 = this.getRecipeTypeId(serverLevel, this.recipeKey2);
        }
    }

    @Override
    protected void swapRecipes() {
        //a recipe has content if it has an id or ingredient conditions. Only move the second recipe to the first
        //slot if the first one is completely empty, so ingredient-only recipes are not clobbered.
        boolean hasContent1 = this.recipeKey1 != null || this.recipe1.hasQuery();
        boolean hasContent2 = this.recipeKey2 != null || this.recipe2.hasQuery();

        if (!hasContent1 && hasContent2) {
            this.recipeKey1 = this.recipeKey2;
            this.recipeKey2 = null;
            this.recipeDisplayEntry1 = this.recipeDisplayEntry2;
            this.recipeDisplayEntry2 = null;
            this.recipe1 = this.recipe2;
            this.recipe2 = RecipeAttribute.DEFAULT;
        }
    }

    @Nullable
    private Identifier getRecipeTypeId(ServerLevel serverLevel, @Nullable ResourceKey<Recipe<?>> recipeKey) {
        if (recipeKey == null) {
            return null;
        }

        var holder = serverLevel.recipeAccess().byKey(recipeKey).orElse(null);
        if (holder == null) {
            return null;
        }

        return BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType());
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.VIEWER_RECIPE;
    }
}
