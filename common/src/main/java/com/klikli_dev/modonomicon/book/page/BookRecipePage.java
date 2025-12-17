/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class BookRecipePage<T extends Recipe<?>> extends BookPage {

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
        this(common.title1(), common.recipeId1(), common.title2(), common.recipeId2(), common.text(), common.anchor(), common.condition());
    }

    public BookRecipePage(NetworkDataHolder common) {
        this(common.title1(), common.recipeKey1(), common.recipeDisplayEntry1(), common.title2(), common.recipeKey2(), common.recipeDisplayEntry2(), common.text(), common.anchor(), common.condition());
    }

    private BookRecipePage(BookTextHolder title1, ResourceKey<Recipe<?>> recipeKey1, BookTextHolder title2, ResourceKey<Recipe<?>> recipeKey2, BookTextHolder text, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.title1 = title1;
        this.recipeKey1 = recipeKey1;
        this.title2 = title2;
        this.recipeKey2 = recipeKey2;
        this.text = text;
    }

    private BookRecipePage(BookTextHolder title1, ResourceKey<Recipe<?>> recipeKey1, @Nullable RecipeDisplayEntry recipeDisplayEntry1, BookTextHolder title2, ResourceKey<Recipe<?>> recipeKey2, @Nullable RecipeDisplayEntry recipeDisplayEntry2, BookTextHolder text, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.title1 = title1;
        this.recipeKey1 = recipeKey1;
        this.recipeDisplayEntry1 = recipeDisplayEntry1;
        this.title2 = title2;
        this.recipeKey2 = recipeKey2;
        this.recipeDisplayEntry2 = recipeDisplayEntry2;
        this.text = text;

    }

    public static JsonDataHolder commonFromJson(Identifier entryId, JsonObject json, HolderLookup.Provider provider) {
        var title1 = BookGsonHelper.getAsBookTextHolder(json, "title1", BookTextHolder.EMPTY, provider);
        Identifier recipeId1 = json.has("recipe_id_1") ? Identifier.tryParse(GsonHelper.getAsString(json, "recipe_id_1")) : null;
        var recipeKey1 = recipeId1 != null ? ResourceKey.create(Registries.RECIPE, recipeId1) : null;

        var title2 = BookGsonHelper.getAsBookTextHolder(json, "title2", BookTextHolder.EMPTY, provider);
        Identifier recipeId2 = json.has("recipe_id_2") ? Identifier.tryParse(GsonHelper.getAsString(json, "recipe_id_2")) : null;
        var recipeKey2 = recipeId2 != null ? ResourceKey.create(Registries.RECIPE, recipeId2) : null;

        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY, provider);

        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(entryId, json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();

        return new JsonDataHolder(title1, recipeKey1, title2, recipeKey2, text, anchor, condition);
    }

    public static NetworkDataHolder commonFromNetwork(RegistryFriendlyByteBuf buffer) {
        var title1 = BookTextHolder.fromNetwork(buffer);
        var recipeKey1 = buffer.readBoolean() ? buffer.readResourceKey(Registries.RECIPE) : null;
        var recipeDisplayEntry1 = buffer.readBoolean() ? RecipeDisplayEntry.STREAM_CODEC.decode(buffer) : null;

        var title2 = BookTextHolder.fromNetwork(buffer);
        var recipeKey2 = buffer.readBoolean() ? buffer.readResourceKey(Registries.RECIPE) : null;
        var recipeDisplayEntry2 = buffer.readBoolean() ? RecipeDisplayEntry.STREAM_CODEC.decode(buffer) : null;

        var text = BookTextHolder.fromNetwork(buffer);

        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);

        return new NetworkDataHolder(title1, recipeKey1, recipeDisplayEntry1, title2, recipeKey2, recipeDisplayEntry2, text, anchor, condition);
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
            this.title1 = new BookTextHolder(((MutableComponent) this.getRecipeOutput(level, this.recipeDisplayEntry1).getHoverName())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())
                    ));
        }

        if (this.recipeDisplayEntry2 != null && this.title2.isEmpty()) {
            //use recipe title if we don't have a custom one
            this.title2 = new BookTextHolder(((MutableComponent) this.getRecipeOutput(level, this.recipeDisplayEntry2).getHoverName())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())
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
                            .withColor(this.getParentEntry().getCategory().getBook().getDefaultTitleColor())));
        }
        if (!this.title2.hasComponent()) {
            this.title2 = new BookTextHolder(Component.translatable(this.title2.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getCategory().getBook().getDefaultTitleColor())));
        }

        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        this.title1.toNetwork(buffer);
        buffer.writeBoolean(this.recipeKey1 != null);
        if (this.recipeKey1 != null) {
            buffer.writeResourceKey(this.recipeKey1);
        }
        buffer.writeBoolean(this.recipeDisplayEntry1 != null);
        if (this.recipeDisplayEntry1 != null) {
            RecipeDisplayEntry.STREAM_CODEC.encode(buffer, this.recipeDisplayEntry1);
        }

        this.title2.toNetwork(buffer);
        buffer.writeBoolean(this.recipeKey2 != null);
        if (this.recipeKey2 != null) {
            buffer.writeResourceKey(this.recipeKey2);
        }
        buffer.writeBoolean(this.recipeDisplayEntry2 != null);
        if (this.recipeDisplayEntry2 != null) {
            RecipeDisplayEntry.STREAM_CODEC.encode(buffer, this.recipeDisplayEntry2);
        }

        this.text.toNetwork(buffer);

        super.toNetwork(buffer);
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        return this.title1.getString().toLowerCase().contains(query)
                || this.title2.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }

    public record JsonDataHolder(BookTextHolder title1, ResourceKey<Recipe<?>> recipeId1, BookTextHolder title2,
                                 ResourceKey<Recipe<?>> recipeId2, BookTextHolder text, String anchor,
                                 BookCondition condition) {
    }

    public record NetworkDataHolder(BookTextHolder title1, ResourceKey<Recipe<?>> recipeKey1,
                                    RecipeDisplayEntry recipeDisplayEntry1, BookTextHolder title2,
                                    ResourceKey<Recipe<?>> recipeKey2, RecipeDisplayEntry recipeDisplayEntry2,
                                    BookTextHolder text, String anchor, BookCondition condition) {
    }
}
