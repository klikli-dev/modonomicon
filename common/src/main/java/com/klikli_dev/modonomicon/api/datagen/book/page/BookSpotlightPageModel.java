/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class BookSpotlightPageModel extends BookPageModel<BookSpotlightPageModel> {
    protected Either<ItemStackTemplate, Ingredient> item;
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

    public Either<ItemStackTemplate, Ingredient> getItem() {
        return this.item;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    @Override
    public JsonObject toJson(Identifier entryId, HolderLookup.Provider provider) {
        var json = super.toJson(entryId, provider);
        json.add("title", this.title.toJson(provider));
        json.add("item",
                BookSpotlightPage.ITEM_CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), this.item).getOrThrow()
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
        this.item = Either.right(item);
        return this;
    }

    public BookSpotlightPageModel withItem(ItemStackTemplate item) {
        this.item = Either.left(item);
        return this;
    }

    public BookSpotlightPageModel withItem(ItemLike item) {
        this.item = Either.left(new ItemStackTemplate(item.asItem()));
        return this;
    }

    public BookSpotlightPageModel withItem(Item item) {
        this.item = Either.left(new ItemStackTemplate(item));
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
