/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BookPageModel<T extends BookPageModel<T>> {

    protected Identifier type;
    protected String id = "";
    protected BookConditionModel<?> condition = BookNoneConditionModel.create();
    protected int sortNumber = -1;
    protected List<Either<ItemStackTemplate, Ingredient>> associatedItems = new ArrayList<>();

    protected BookPageModel(Identifier type) {
        this.type = type;
    }

    public Identifier getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    /**
     * Serializes the model to json.
     */
    public JsonObject toJson(Identifier entryId, HolderLookup.Provider provider) {
        JsonObject json = BookPage.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), this.toBookPage(provider))
                .getOrThrow(JsonParseException::new)
                .getAsJsonObject();
        if (this.sortNumber >= 0) {
            json.addProperty("sort_number", this.sortNumber);
        }
        return json;
    }

    protected BookCondition condition(HolderLookup.Provider provider) {
        return this.condition != null ? this.condition.toBookCondition(provider) : new BookNoneCondition();
    }

    public abstract BookPage toBookPage(HolderLookup.Provider provider);
    
    public T withId(@NotNull String id) {
        this.id = id;
        //noinspection unchecked
        return (T) this;
    }

    public T withCondition(@NotNull BookConditionModel<?> condition) {
        this.condition = condition;
        //noinspection unchecked
        return (T) this;
    }

    public T withCondition(@NotNull ResearchNodeRef nodeRef) {
        return this.withCondition(BookResearchNodeUnlockedConditionModel.create().withNode(nodeRef.id()));
    }

    public List<Either<ItemStackTemplate, Ingredient>> getAssociatedItems() {
        return this.associatedItems;
    }

    /**
     * Associates an item with this page. Hovering the item shows a linked-page tooltip;
     * holding the open key opens this page. All components defined on the template must
     * be present on the hovered stack (additional components are allowed).
     */
    public T withAssociatedItem(@NotNull ItemLike item) {
        this.associatedItems.add(Either.left(new ItemStackTemplate(item.asItem())));
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Associates an item with this page. Hovering the item shows a linked-page tooltip;
     * holding the open key opens this page. All components defined on the template must
     * be present on the hovered stack (additional components are allowed).
     */
    public T withAssociatedItem(@NotNull Item item) {
        this.associatedItems.add(Either.left(new ItemStackTemplate(item)));
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Associates an item (with components) with this page. Hovering a matching stack shows
     * a linked-page tooltip; holding the open key opens this page.
     */
    public T withAssociatedItem(@NotNull ItemStackTemplate item) {
        this.associatedItems.add(Either.left(item));
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Associates an ingredient (e.g. a tag, like spotlight pages support) with this page.
     * Hovering any matching stack shows a linked-page tooltip; holding the open key opens this page.
     */
    public T withAssociatedItem(@NotNull Ingredient ingredient) {
        this.associatedItems.add(Either.right(ingredient));
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Associates an item or ingredient with this page.
     */
    public T withAssociatedItem(@NotNull Either<ItemStackTemplate, Ingredient> associated) {
        this.associatedItems.add(associated);
        //noinspection unchecked
        return (T) this;
    }

    public T withAssociatedItems(@NotNull List<Either<ItemStackTemplate, Ingredient>> items) {
        this.associatedItems.addAll(items);
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Applies the configured associated items to a freshly created page.
     */
    protected void applyAssociatedItems(@NotNull BookPage page) {
        page.setAssociatedItems(this.associatedItems);
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    /**
     * Sets the page's sort number.
     * Pages with a lower sort number will be displayed first.
     * If no sort number is set (default -1), the page is appended at the end.
     */
    public T withSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
        //noinspection unchecked
        return (T) this;
    }
}
