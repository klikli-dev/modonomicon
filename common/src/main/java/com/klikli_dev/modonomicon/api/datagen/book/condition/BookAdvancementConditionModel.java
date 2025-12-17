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
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookAdvancementConditionModel extends BookConditionModel<BookAdvancementConditionModel> {
    private Identifier advancementId;

    protected BookAdvancementConditionModel() {
        super(Condition.ADVANCEMENT);
    }

    public static BookAdvancementConditionModel create() {
        return new BookAdvancementConditionModel();
    }

    @Override
    public JsonObject toJson(Identifier conditionParentId, HolderLookup.Provider provider) {
        var json = super.toJson(conditionParentId, provider);
        json.addProperty("advancement_id", this.advancementId.toString());
        return json;
    }

    public Identifier getAdvancementId() {
        return this.advancementId;
    }

    public BookAdvancementConditionModel withAdvancementId(Identifier advancementId) {
        this.advancementId = advancementId;
        return this;
    }

    public BookAdvancementConditionModel withAdvancementId(String advancementId) {
        this.advancementId = Identifier.parse(advancementId);
        return this;
    }

    public BookAdvancementConditionModel withAdvancement(AdvancementHolder advancement) {
        this.advancementId = advancement.id();
        return this;
    }

}
