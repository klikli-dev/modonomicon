/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import com.klikli_dev.modonomicon.data.load.LoadConditionPreprocessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

public class BookPageModel<T extends BookPageModel<T>> {

    protected ResourceLocation type;
    protected String anchor = "";
    protected BookConditionModel<?> condition = BookNoneConditionModel.create();
    protected BookConditionModel<?> loadCondition = null;

    protected BookPageModel(ResourceLocation type) {
        this.type = type;
    }

    public ResourceLocation getType() {
        return this.type;
    }

    public String getAnchor() {
        return this.anchor;
    }

    /**
     * Serializes the model to json.
     */
    public JsonObject toJson(ResourceLocation entryId, HolderLookup.Provider provider) {
        JsonObject json = this.toJson(provider); //to keep backwards compat
        json.addProperty("type", this.type.toString());
        json.addProperty("anchor", this.anchor);
        json.add("condition", this.condition.toJson(entryId, provider));
        this.addLoadConditionToJson(json, entryId, provider);

        return json;
    }

    /**
     * Adds the load condition (if any) to the given page json.
     */
    public void addLoadConditionToJson(JsonObject json, ResourceLocation entryId, HolderLookup.Provider provider) {
        if (this.loadCondition != null) {
            json.add(LoadConditionPreprocessor.KEY, this.loadCondition.toJson(entryId, provider));
        }
    }

    /**
     * Serializes the model to json.
     * @deprecated use {@link #toJson(ResourceLocation, HolderLookup.Provider)} instead.
     */
    @Deprecated(forRemoval = true, since="1.21.1-1.105.0")
    public JsonObject toJson(HolderLookup.Provider provider) {
        return new JsonObject();
    }

    public T withAnchor(@NotNull String anchor) {
        this.anchor = anchor;
        //noinspection unchecked
        return (T) this;
    }

    public T withCondition(@NotNull BookConditionModel<?> condition) {
        this.condition = condition;
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Sets the load condition of this page. If it is not met when the book is loaded, the page is removed entirely.
     * Only conditions that can be evaluated at load time are allowed, e.g. mod loaded, true, false, and, or, not.
     */
    public T withLoadCondition(@NotNull BookConditionModel<?> loadCondition) {
        this.loadCondition = loadCondition;
        //noinspection unchecked
        return (T) this;
    }
}
