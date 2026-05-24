/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.book.page.BookMultiblockPage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BookMultiblockPageModel extends BookPageModel<BookMultiblockPageModel> {
    protected BookTextHolderModel multiblockName = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");
    protected Identifier multiblockId = null;
    protected boolean showVisualizeButton = true;

    protected BookMultiblockPageModel() {
        super(BookMultiblockPage.ID);
    }

    public static BookMultiblockPageModel create() {
        return new BookMultiblockPageModel();
    }

    public BookTextHolderModel getMultiblockName() {
        return this.multiblockName;
    }

    public Identifier getMultiblockId() {
        return this.multiblockId;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    public boolean showVisualizeButton() {
        return this.showVisualizeButton;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookMultiblockPage(this.multiblockName.toBookTextHolder(), this.text.toBookTextHolder(), this.multiblockId, this.showVisualizeButton, this.anchor, this.condition(provider));
    }

    public BookMultiblockPageModel withMultiblockName(String title) {
        this.multiblockName = new BookTextHolderModel(title);
        return this;
    }

    public BookMultiblockPageModel withMultiblockName(Component title) {
        this.multiblockName = new BookTextHolderModel(title);
        return this;
    }

    public BookMultiblockPageModel withMultiblockId(Identifier multiblockId) {
        this.multiblockId = multiblockId;
        return this;
    }

    public BookMultiblockPageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookMultiblockPageModel withText(Component text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookMultiblockPageModel withVisualizeButton(boolean showVisualizeButton) {
        this.showVisualizeButton = showVisualizeButton;
        return this;
    }
}
