/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Page;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookEmptyPage extends BookPage {

    public BookEmptyPage(String anchor) {
        super(anchor);
    }

    public static BookEmptyPage fromJson(JsonObject json) {
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        return new BookEmptyPage(anchor);
    }

    public static BookEmptyPage fromNetwork(FriendlyByteBuf buffer) {
        var anchor = buffer.readUtf();
        return new BookEmptyPage(anchor);
    }

    @Override
    public ResourceLocation getType() {
        return Page.EMPTY;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.anchor);
    }
}
