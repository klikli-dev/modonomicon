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
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

public abstract class BookPageModel<T extends BookPageModel<T>> {

    protected Identifier type;
    protected String id = "";
    protected BookConditionModel<?> condition = BookNoneConditionModel.create();
    protected int sortNumber = -1;

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
