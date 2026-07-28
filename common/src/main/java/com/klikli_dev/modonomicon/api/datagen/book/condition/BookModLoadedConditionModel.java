/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookModLoadedCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BookModLoadedConditionModel extends BookConditionModel<BookModLoadedConditionModel> {
    private String modId;

    protected BookModLoadedConditionModel() {
        super(BookModLoadedCondition.ID);
    }

    public static BookModLoadedConditionModel create() {
        return new BookModLoadedConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        var tooltip = this.tooltipComponent();
        if (tooltip == null) {
            tooltip = Component.translatable(Tooltips.CONDITION_MOD_LOADED, this.modId);
        }
        return new BookModLoadedCondition(tooltip, this.modId);
    }

    public String getModId() {
        return this.modId;
    }

    public BookModLoadedConditionModel withModId(String modId) {
        this.modId = modId;
        return this;
    }

}
