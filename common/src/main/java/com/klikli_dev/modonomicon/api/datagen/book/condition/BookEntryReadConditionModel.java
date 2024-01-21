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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookEntryReadConditionModel extends BookConditionModel<BookEntryReadConditionModel> {
    protected String entryId;

    protected BookEntryReadConditionModel() {
        super(Condition.ENTRY_READ);
    }

    public static BookEntryReadConditionModel create() {
        return new BookEntryReadConditionModel();
    }

    @Override
    public JsonObject toJson() {
        var json = super.toJson();
        json.addProperty("entry_id", this.entryId);
        return json;
    }

    public String getEntryId() {
        return this.entryId;
    }

    public BookEntryReadConditionModel withEntry(ResourceLocation entryId) {
        this.entryId = entryId.toString();
        return this;
    }

    public BookEntryReadConditionModel withEntry(String entryId) {
        this.entryId = entryId;
        return this;
    }
}
