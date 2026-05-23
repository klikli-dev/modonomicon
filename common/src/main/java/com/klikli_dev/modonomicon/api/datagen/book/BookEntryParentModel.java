/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.BookEntryParent;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookEntryParentModel {
    protected Identifier entryId;
    protected boolean drawArrow = true;
    protected boolean lineEnabled = true;
    protected boolean lineReversed = false;

    protected BookEntryParentModel(Identifier entryId) {
        this.entryId = entryId;
    }

    public static BookEntryParentModel create(Identifier entryId) {
        return new BookEntryParentModel(entryId);
    }

    /**
     * @param ownerEntryId the entry id of the entry that contains this parent information. This is the CHILD not the parent.
     * @param provider a registry / holder lookup provider.
     */
    public JsonObject toJson(Identifier ownerEntryId, HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        json.addProperty("entry", this.entryId.toString());
        json.addProperty("draw_arrow", this.drawArrow);
        json.addProperty("line_enabled", this.lineEnabled);
        json.addProperty("line_reversed", this.lineReversed);
        return json;
    }

    public BookEntryParent toBookEntryParent(Identifier ownerEntryId, HolderLookup.Provider provider) {
        return BookEntryParent.CODEC.parse(JsonOps.INSTANCE, this.toJson(ownerEntryId, provider)).getOrThrow();
    }

    public Identifier getEntryId() {
        return this.entryId;
    }

    public boolean isDrawArrow() {
        return this.drawArrow;
    }

    public boolean isLineEnabled() {
        return this.lineEnabled;
    }

    public boolean isLineReversed() {
        return this.lineReversed;
    }

    public BookEntryParentModel withEntryId(Identifier entryId) {
        this.entryId = entryId;
        return this;
    }

    public BookEntryParentModel withDrawArrow(boolean drawArrow) {
        this.drawArrow = drawArrow;
        return this;
    }

    public BookEntryParentModel withLineEnabled(boolean lineEnabled) {
        this.lineEnabled = lineEnabled;
        return this;
    }

    public BookEntryParentModel withLineReversed(boolean lineReversed) {
        this.lineReversed = lineReversed;
        return this;
    }
}
