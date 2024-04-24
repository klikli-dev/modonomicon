/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookImagePageModel extends BookPageModel<BookImagePageModel> {
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected ResourceLocation[] images = new ResourceLocation[0];
    protected boolean border = true;

    protected BookImagePageModel() {
        super(Page.IMAGE);
    }

    public static BookImagePageModel create() {
        return new BookImagePageModel();
    }

    public BookTextHolderModel getTitle() {
        return this.title;
    }

    public ResourceLocation[] getImages() {
        return this.images;
    }

    public boolean isBorder() {
        return this.border;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    @Override
    public JsonObject toJson(HolderLookup.Provider provider) {
        var json = super.toJson(provider);
        json.add("title", this.title.toJson(provider));
        json.add("text", this.text.toJson(provider));
        json.addProperty("border", this.border);

        var imagesArray = new JsonArray();
        for (int i = 0; i < this.images.length; i++) {
            imagesArray.add(this.images[i].toString());
        }
        json.add("images", imagesArray);

        return json;
    }

    public BookImagePageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookImagePageModel withTitle(Component title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookImagePageModel withBorder(boolean border) {
        this.border = border;
        return this;
    }

    public BookImagePageModel withImages(ResourceLocation... images) {
        this.images = images;
        return this;
    }

    public BookImagePageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookImagePageModel withText(Component text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

}
