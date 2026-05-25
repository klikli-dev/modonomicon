/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.book.page.BookImagePage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BookImagePageModel extends BookPageModel<BookImagePageModel> {
    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected Identifier[] images = new Identifier[0];
    protected boolean border = true;
    /**
     * If true then the image will render only the top left 200x200px out of a 256x256px image.
     * If false the full image of any size will be rendered and scaled accordingly.
     */
    protected boolean useLegacyRendering = false;

    protected BookImagePageModel() {
        super(BookImagePage.ID);
    }

    public static BookImagePageModel create() {
        return new BookImagePageModel();
    }

    public BookTextHolderModel getTitle() {
        return this.title;
    }

    public Identifier[] getImages() {
        return this.images;
    }

    public boolean isBorder() {
        return this.border;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookImagePage(this.title.toBookTextHolder(), this.text.toBookTextHolder(), this.images, this.border, this.useLegacyRendering, this.id, this.condition(provider));
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

    /**
     * If true then the image will render only the top left 200x200px out of a 256x256px image.
     * If false the full image of any size will be rendered and scaled accordingly.
     */
    public BookImagePageModel withLegacyRendering(boolean useLegacyRendering) {
        this.useLegacyRendering = useLegacyRendering;
        return this;
    }

    public BookImagePageModel withImages(Identifier... images) {
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
