/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

public class BookPageModel<T extends BookPageModel<T>> {

    protected Identifier type;
    protected String anchor = "";
    protected BookConditionModel<?> condition = BookNoneConditionModel.create();

    protected BookPageModel(Identifier type) {
        this.type = type;
    }

    public Identifier getType() {
        return this.type;
    }

    public String getAnchor() {
        return this.anchor;
    }

    /**
     * Serializes the model to json.
     */
    public JsonObject toJson(Identifier entryId, HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.type.toString());
        json.addProperty("anchor", this.anchor);
        json.add("condition", this.condition.toJson(entryId, provider));

        return json;
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
}
