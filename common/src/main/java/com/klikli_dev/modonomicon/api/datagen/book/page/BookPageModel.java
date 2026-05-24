/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

public abstract class BookPageModel<T extends BookPageModel<T>> {

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
        return BookPage.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), this.toBookPage(provider))
                .getOrThrow(JsonParseException::new)
                .getAsJsonObject();
    }

    protected BookCondition condition(HolderLookup.Provider provider) {
        return this.condition != null ? this.condition.toBookCondition(provider) : new BookNoneCondition();
    }

    public abstract BookPage toBookPage(HolderLookup.Provider provider);
    
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
