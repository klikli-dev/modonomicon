/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.conditions.BookCategoryHasVisibleEntriesCondition;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BookCategoryHasVisibleEntriesConditionModel extends BookConditionModel<BookCategoryHasVisibleEntriesConditionModel> {
    private Identifier categoryId;

    protected BookCategoryHasVisibleEntriesConditionModel() {
        super(BookCategoryHasVisibleEntriesCondition.ID);
    }

    public static BookCategoryHasVisibleEntriesConditionModel create() {
        return new BookCategoryHasVisibleEntriesConditionModel();
    }


    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        var tooltip = this.tooltipComponent();
        if (tooltip == null) {
            tooltip = Component.translatable(ModonomiconConstants.I18n.Tooltips.CONDITION_CATEGORY_HAS_VISIBLE_ENTRIES, this.categoryId.toLanguageKey());
        }
        return new BookCategoryHasVisibleEntriesCondition(tooltip, this.categoryId);
    }

    public Identifier getCategoryId() {
        return this.categoryId;
    }


    public BookCategoryHasVisibleEntriesConditionModel withCategory(Identifier entryId) {
        this.categoryId = entryId;
        return this;
    }


    public BookCategoryHasVisibleEntriesConditionModel withCategory(String entryId) {
        this.categoryId = Identifier.parse(entryId);
        return this;
    }

    @Override
    public BookCategoryHasVisibleEntriesConditionModel withTooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    /**
     * Will overwrite withTooltip
     */
    @Override
    public BookCategoryHasVisibleEntriesConditionModel withTooltipString(String tooltipString) {
        this.tooltipString = tooltipString;
        return this;
    }
}
