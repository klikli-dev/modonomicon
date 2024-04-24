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
import net.minecraft.resources.ResourceLocation;

public class BookEntryUnlockedConditionModel extends BookConditionModel<BookEntryUnlockedConditionModel> {
    protected String entryId;

    protected BookEntryUnlockedConditionModel() {
        super(Condition.ENTRY_UNLOCKED);
    }

    public static BookEntryUnlockedConditionModel create() {
        return new BookEntryUnlockedConditionModel();
    }

    public String getEntryId() {
        return this.entryId;
    }

    @Override
    public JsonObject toJson(HolderLookup.Provider provider) {
        var json = super.toJson(provider);
        json.addProperty("entry_id", this.entryId);
        return json;
    }

    public BookEntryUnlockedConditionModel withEntry(ResourceLocation entryId) {
        this.entryId = entryId.toString();
        return this;
    }

    public BookEntryUnlockedConditionModel withEntry(String entryId) {
        this.entryId = entryId;
        return this;
    }
}
