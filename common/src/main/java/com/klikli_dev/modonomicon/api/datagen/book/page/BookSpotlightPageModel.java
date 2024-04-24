/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;

public class BookSpotlightPageModel extends BookPageModel<BookSpotlightPageModel> {
    protected Ingredient item = Ingredient.EMPTY;
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookSpotlightPageModel() {
        super(Page.SPOTLIGHT);
    }

    public static BookSpotlightPageModel create() {
        return new BookSpotlightPageModel();
    }

    public BookTextHolderModel getTitle() {
        return this.title;
    }

    public Ingredient getItem() {
        return this.item;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    @Override
    public JsonObject toJson(HolderLookup.Provider provider) {
        var json = super.toJson(provider);
        json.add("title", this.title.toJson(provider));
        json.add("item", Ingredient.CODEC
                .encodeStart(JsonOps.INSTANCE, this.item)
                .getOrThrow(s ->
                        new IllegalStateException("Could not encode ingredient"))
        );
        json.add("text", this.text.toJson(provider));
        return json;
    }

    public BookSpotlightPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookSpotlightPageModel withTitle(Component title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookSpotlightPageModel withItem(Ingredient item) {
        this.item = item;
        return this;
    }

    public BookSpotlightPageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookSpotlightPageModel withText(Component text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }
}
