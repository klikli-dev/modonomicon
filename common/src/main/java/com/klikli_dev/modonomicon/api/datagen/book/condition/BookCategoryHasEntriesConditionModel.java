/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookCategoryHasEntriesConditionModel extends BookConditionModel<BookCategoryHasEntriesConditionModel> {
    private String categoryId;

    protected BookCategoryHasEntriesConditionModel() {
        super(Condition.CATEGORY_HAS_ENTRIES);
    }

    public static BookCategoryHasEntriesConditionModel create() {
        return new BookCategoryHasEntriesConditionModel();
    }


    @Override
    public JsonObject toJson(HolderLookup.Provider provider) {
        var json = super.toJson(provider);
        json.addProperty("category_id", this.categoryId);
        return json;
    }

    public String getCategoryId() {
        return this.categoryId;
    }


    public BookCategoryHasEntriesConditionModel withCategory(ResourceLocation entryId) {
        this.categoryId = entryId.toString();
        return this;
    }


    public BookCategoryHasEntriesConditionModel withCategory(String entryId) {
        this.categoryId = entryId;
        return this;
    }

    @Override
    public BookCategoryHasEntriesConditionModel withTooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    /**
     * Will overwrite withTooltip
     */
    @Override
    public BookCategoryHasEntriesConditionModel withTooltipString(String tooltipString) {
        this.tooltipString = tooltipString;
        return this;
    }
}
