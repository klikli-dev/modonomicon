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

public class BookModLoadedConditionModel extends BookConditionModel<BookModLoadedConditionModel> {
    private String modId;

    protected BookModLoadedConditionModel() {
        super(Condition.MOD_LOADED);
    }

    public static BookModLoadedConditionModel create() {
        return new BookModLoadedConditionModel();
    }

    @Override
    public JsonObject toJson(HolderLookup.Provider provider) {
        var json = super.toJson(provider);
        json.addProperty("mod_id", this.modId);
        return json;
    }

    public String getModId() {
        return this.modId;
    }

    public BookModLoadedConditionModel withModId(String modId) {
        this.modId = modId;
        return this;
    }

}
