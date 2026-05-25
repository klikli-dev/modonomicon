/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public abstract class BookRecipePage<T extends Recipe<?>> extends BookPage {

    protected static final MapCodec<JsonDataHolder> JSON_COMMON_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BookTextHolder.CODEC.optionalFieldOf("title1", BookTextHolder.EMPTY).forGetter(JsonDataHolder::title1),
            ResourceKey.codec(Registries.RECIPE).optionalFieldOf("recipe_id_1").forGetter(holder -> Optional.ofNullable(holder.recipeId1())),
            BookTextHolder.CODEC.optionalFieldOf("title2", BookTextHolder.EMPTY).forGetter(JsonDataHolder::title2),
            ResourceKey.codec(Registries.RECIPE).optionalFieldOf("recipe_id_2").forGetter(holder -> Optional.ofNullable(holder.recipeId2())),
            BookTextHolder.CODEC.optionalFieldOf("text", BookTextHolder.EMPTY).forGetter(JsonDataHolder::text),
            Codec.STRING.fieldOf("id").forGetter(JsonDataHolder::id),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(JsonDataHolder::condition)
    ).apply(instance, (title1, recipeKey1, title2, recipeKey2, text, id, condition) -> new JsonDataHolder(
            title1,
            recipeKey1.orElse(null),
            title2,
            recipeKey2.orElse(null),
            text,
            id,
            condition
    )));

    protected static final StreamCodec<RegistryFriendlyByteBuf, NetworkDataHolder> NETWORK_COMMON_STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, NetworkDataHolder::title1,
            ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.RECIPE)), holder -> Optional.ofNullable(holder.recipeKey1()),
            ByteBufCodecs.optional(RecipeDisplayEntry.STREAM_CODEC), holder -> Optional.ofNullable(holder.recipeDisplayEntry1()),
            BookTextHolder.STREAM_CODEC, NetworkDataHolder::title2,
            ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.RECIPE)), holder -> Optional.ofNullable(holder.recipeKey2()),
            ByteBufCodecs.optional(RecipeDisplayEntry.STREAM_CODEC), holder -> Optional.ofNullable(holder.recipeDisplayEntry2()),
            BookTextHolder.STREAM_CODEC, NetworkDataHolder::text,
            ByteBufCodecs.STRING_UTF8, NetworkDataHolder::id,
            BookCondition.STREAM_CODEC, NetworkDataHolder::condition,
            (title1, recipeKey1, recipeDisplayEntry1, title2, recipeKey2, recipeDisplayEntry2, text, id, condition) -> new NetworkDataHolder(
                    title1,
                    recipeKey1.orElse(null),
                    recipeDisplayEntry1.orElse(null),
                    title2,
                    recipeKey2.orElse(null),
                    recipeDisplayEntry2.orElse(null),
                    text,
                    id,
                    condition
            )
    );

    protected BookTextHolder title1;
    protected ResourceKey<Recipe<?>> recipeKey1;

    /**
     * Can be null during serverside construction. On both server and client it can be null if the recipe does not exist.
     */
    @Nullable
    protected RecipeDisplayEntry recipeDisplayEntry1;

    protected BookTextHolder title2;
    protected ResourceKey<Recipe<?>> recipeKey2;

    /**
     * Can be null during serverside construction. On both server and client it can be null if the recipe does not exist.
     */
    @Nullable
    protected RecipeDisplayEntry recipeDisplayEntry2;

    protected BookTextHolder text;

    public BookRecipePage(JsonDataHolder common) {
        this(common.title1(), common.recipeId1(), common.title2(), common.recipeId2(), common.text(), common.id(), common.condition());
    }

    public BookRecipePage(NetworkDataHolder common) {
        this(common.title1(), common.recipeKey1(), common.recipeDisplayEntry1(), common.title2(), common.recipeKey2(), common.recipeDisplayEntry2(), common.text(), common.id(), common.condition());
    }

    private BookRecipePage(BookTextHolder title1, ResourceKey<Recipe<?>> recipeKey1, BookTextHolder title2, ResourceKey<Recipe<?>> recipeKey2, BookTextHolder text, String id, BookCondition condition) {
        super(id, condition);
        this.title1 = title1;
        this.recipeKey1 = recipeKey1;
        this.title2 = title2;
        this.recipeKey2 = recipeKey2;
        this.text = text;
    }

    private BookRecipePage(BookTextHolder title1, ResourceKey<Recipe<?>> recipeKey1, @Nullable RecipeDisplayEntry recipeDisplayEntry1, BookTextHolder title2, ResourceKey<Recipe<?>> recipeKey2, @Nullable RecipeDisplayEntry recipeDisplayEntry2, BookTextHolder text, String id, BookCondition condition) {
        super(id, condition);
        this.title1 = title1;
        this.recipeKey1 = recipeKey1;
        this.recipeDisplayEntry1 = recipeDisplayEntry1;
        this.title2 = title2;
        this.recipeKey2 = recipeKey2;
        this.recipeDisplayEntry2 = recipeDisplayEntry2;
        this.text = text;

    }

    protected static <P extends BookRecipePage<?>> MapCodec<P> codec(Function<JsonDataHolder, P> factory) {
        return JSON_COMMON_CODEC.xmap(factory, BookRecipePage::toJsonDataHolder);
    }

    protected static <P extends BookRecipePage<?>> StreamCodec<RegistryFriendlyByteBuf, P> streamCodec(Function<NetworkDataHolder, P> factory) {
        return NETWORK_COMMON_STREAM_CODEC.map(factory, BookRecipePage::toNetworkDataHolder);
    }

    protected JsonDataHolder toJsonDataHolder() {
        return new JsonDataHolder(this.title1, this.recipeKey1, this.title2, this.recipeKey2, this.text, this.id, this.condition);
    }

    protected NetworkDataHolder toNetworkDataHolder() {
        return new NetworkDataHolder(this.title1, this.recipeKey1, this.recipeDisplayEntry1, this.title2, this.recipeKey2, this.recipeDisplayEntry2, this.text, this.id, this.condition);
    }

    public BookTextHolder getTitle1() {
        return this.title1;
    }

    public ResourceKey<Recipe<?>> getRecipeKey1() {
        return this.recipeKey1;
    }

    @Nullable
    public RecipeDisplayEntry getRecipeDisplayEntry1() {
        return this.recipeDisplayEntry1;
    }

    public BookTextHolder getTitle2() {
        return this.title2;
    }

    public ResourceKey<Recipe<?>> getRecipeKey2() {
        return this.recipeKey2;
    }

    @Nullable
    public RecipeDisplayEntry getRecipeDisplayEntry2() {
        return this.recipeDisplayEntry2;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    protected ItemStack getRecipeOutput(Level level, RecipeDisplayEntry recipeDisplayEntry) {
        if (recipeDisplayEntry == null) {
            var item = new ItemStack(Items.BARRIER);
            item.set(DataComponents.CUSTOM_NAME, Component.literal("Recipe not found, please check the logs."));
            return item;
        }

        var results = recipeDisplayEntry.resultItems(SlotDisplayContext.fromLevel(level));
        return results.stream().findFirst().orElse(ItemStack.EMPTY);
    }

    private RecipeDisplayEntry getRecipeDisplayEntry(ServerLevel serverLevel, ResourceKey<Recipe<?>> key) {
        if (key == null) {
            return null;
        }

        var list = new ArrayList<RecipeDisplayEntry>();
        serverLevel.recipeAccess().listDisplaysForRecipe(key, list::add);
        var entry = list.stream().findFirst().orElse(null);

        if (entry == null) {
            Modonomicon.LOG.warn("Recipe {} not found.", key);
            return null;
        }

        // Fix for recipes with null or unregistered category - use a fallback category to avoid encoding errors.
        // Some mods create RecipeBookCategory instances without registering them in the registry.
        var category = entry.category();
        if (category == null) {
            Modonomicon.LOG.warn("Recipe {} has null recipe book category, using fallback CRAFTING_MISC.", key);
            entry = new RecipeDisplayEntry(
                    entry.id(), entry.display(), entry.group(),
                    RecipeBookCategories.CRAFTING_MISC, entry.craftingRequirements()
            );
        } else {
            // Try to get the key from registry - returns empty if not registered
            var categoryKey = BuiltInRegistries.RECIPE_BOOK_CATEGORY.getKey(category);
            if (categoryKey == null) {
                Modonomicon.LOG.warn("Recipe {} has unregistered recipe book category {}, using fallback CRAFTING_MISC.", key, category);
                entry = new RecipeDisplayEntry(
                        entry.id(), entry.display(), entry.group(),
                        RecipeBookCategories.CRAFTING_MISC, entry.craftingRequirements()
                );
            }
        }

        return entry;
    }

    @Override
    public void build(Level level, BookContentEntry parentEntry, int pageNum) {
        super.build(level, parentEntry, pageNum);

        //TODO: handle multiple displays per recipe?

        //if we are on the server we have to load the recipe display info.
        //on the client we already get it in the constructor, sent from the server.
        if (level instanceof ServerLevel serverLevel) {
            this.recipeDisplayEntry1 = this.getRecipeDisplayEntry(serverLevel, this.recipeKey1);
            this.recipeDisplayEntry2 = this.getRecipeDisplayEntry(serverLevel, this.recipeKey2);
        }

        if (this.recipeDisplayEntry1 == null && this.recipeDisplayEntry2 != null) {
            this.recipeDisplayEntry1 = this.recipeDisplayEntry2;
            this.recipeDisplayEntry2 = null;
        }

        if (this.title1.isEmpty()) {
            //use recipe title if we don't have a custom one
            this.title1 = new BookTextHolder((this.getRecipeOutput(level, this.recipeDisplayEntry1).getHoverName().copy())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().themeData().palette().defaultTitleColor())
                    ));
        }

        if (this.recipeDisplayEntry2 != null && this.title2.isEmpty()) {
            //use recipe title if we don't have a custom one
            this.title2 = new BookTextHolder((this.getRecipeOutput(level, this.recipeDisplayEntry2).getHoverName().copy())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().themeData().palette().defaultTitleColor())
                    ));
        }

        if (this.title1.equals(this.title2)) {
            this.title2 = BookTextHolder.EMPTY;
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.title1.hasComponent()) {
            this.title1 = new BookTextHolder(Component.translatable(this.title1.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getCategory().getBook().themeData().palette().defaultTitleColor())));
        }
        if (!this.title2.hasComponent()) {
            this.title2 = new BookTextHolder(Component.translatable(this.title2.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getCategory().getBook().themeData().palette().defaultTitleColor())));
        }

        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        return this.title1.getString().toLowerCase().contains(query)
                || this.title2.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }

   public record JsonDataHolder(BookTextHolder title1, ResourceKey<Recipe<?>> recipeId1, BookTextHolder title2,
                                  ResourceKey<Recipe<?>> recipeId2, BookTextHolder text, String id,
                                  BookCondition condition) {
    }

  public record NetworkDataHolder(BookTextHolder title1, ResourceKey<Recipe<?>> recipeKey1,
                                     RecipeDisplayEntry recipeDisplayEntry1, BookTextHolder title2,
                                     ResourceKey<Recipe<?>> recipeKey2, RecipeDisplayEntry recipeDisplayEntry2,
                                     BookTextHolder text, String id, BookCondition condition) {
    }
}
