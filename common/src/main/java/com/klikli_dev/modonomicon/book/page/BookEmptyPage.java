/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookEmptyPage extends BookPage {

    public BookEmptyPage(String anchor, BookCondition condition) {
        super(anchor, condition);
    }

    public static BookEmptyPage fromJson(JsonObject json, HolderLookup.Provider provider) {
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();
        return new BookEmptyPage(anchor, condition);
    }

    public static BookEmptyPage fromNetwork(RegistryFriendlyByteBuf buffer){
        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookEmptyPage(anchor, condition);
    }

    @Override
    public ResourceLocation getType() {
        return Page.EMPTY;
    }

    @Override
    public boolean matchesQuery(String query) {
        return false;
    }
}
