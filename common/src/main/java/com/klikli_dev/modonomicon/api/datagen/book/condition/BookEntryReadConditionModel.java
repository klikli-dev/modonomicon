/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookEntryReadCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookEntryReadConditionModel extends BookConditionModel<BookEntryReadConditionModel> {
    protected Identifier entryId;

    protected BookEntryReadConditionModel() {
        super(BookEntryReadCondition.ID);
    }

    public static BookEntryReadConditionModel create() {
        return new BookEntryReadConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookEntryReadCondition(this.tooltipComponent(), this.entryId);
    }

    public Identifier getEntryId() {
        return this.entryId;
    }

    public BookEntryReadConditionModel withEntry(Identifier entryId) {
        this.entryId = entryId;
        return this;
    }

    public BookEntryReadConditionModel withEntry(String entryId) {
        this.entryId = Identifier.parse(entryId);
        return this;
    }
}
