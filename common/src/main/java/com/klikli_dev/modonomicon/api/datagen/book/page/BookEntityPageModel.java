/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.book.page.BookEntityPage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BookEntityPageModel extends BookPageModel<BookEntityPageModel> {
    protected BookTextHolderModel entityName = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");
    protected String entityId;
    protected float scale = 1.0f;
    protected float offset = 0f;
    protected boolean rotate = true;
    protected float defaultRotation = -45f;

    protected BookEntityPageModel() {
        super(BookEntityPage.ID);
    }

    public static BookEntityPageModel create() {
        return new BookEntityPageModel();
    }

    public BookTextHolderModel getEntityName() {
        return this.entityName;
    }

    public BookTextHolderModel getText() {
        return this.text;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public float getScale() {
        return this.scale;
    }

    public float getOffset() {
        return this.offset;
    }

    public boolean doesRotate() {
        return this.rotate;
    }

    public float getDefaultRotation() {
        return this.defaultRotation;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookEntityPage(this.entityName.toBookTextHolder(), this.text.toBookTextHolder(), this.entityId, this.scale, this.offset, this.rotate, this.defaultRotation, this.anchor, this.condition(provider));
    }

    public BookEntityPageModel withEntityName(String name) {
        this.entityName = new BookTextHolderModel(name);
        return this;
    }

    public BookEntityPageModel withEntityName(Component name) {
        this.entityName = new BookTextHolderModel(name);
        return this;
    }

    public BookEntityPageModel withEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public BookEntityPageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookEntityPageModel withText(Component text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    public BookEntityPageModel withScale(float scale) {
        this.scale = scale;
        return this;
    }

    public BookEntityPageModel withOffset(float offset) {
        this.offset = offset;
        return this;
    }

    public BookEntityPageModel withRotate(boolean rotate) {
        this.rotate = rotate;
        return this;
    }

    public BookEntityPageModel withDefaultRotation(float defaultRotation) {
        this.defaultRotation = defaultRotation;
        return this;
    }
}
