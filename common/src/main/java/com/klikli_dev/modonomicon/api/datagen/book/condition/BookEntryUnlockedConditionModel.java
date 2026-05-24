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
import com.klikli_dev.modonomicon.book.conditions.BookEntryUnlockedCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookEntryUnlockedConditionModel extends BookConditionModel<BookEntryUnlockedConditionModel> {
    protected Identifier entryId;

    protected BookEntryUnlockedConditionModel() {
        super(BookEntryUnlockedCondition.ID);
    }

    public static BookEntryUnlockedConditionModel create() {
        return new BookEntryUnlockedConditionModel();
    }

    public Identifier getEntryId() {
        return this.entryId;
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookEntryUnlockedCondition(this.tooltipComponent(), this.entryId);
    }

    public BookEntryUnlockedConditionModel withEntry(Identifier entryId) {
        this.entryId = entryId;
        return this;
    }

    public BookEntryUnlockedConditionModel withEntry(String entryId) {
        this.entryId = Identifier.parse(entryId);
        return this;
    }
}
