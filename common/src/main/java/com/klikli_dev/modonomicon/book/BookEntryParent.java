/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

public class BookEntryParent {
    protected Identifier entryId;
    protected boolean drawArrow = true;
    protected boolean lineEnabled = true;
    protected boolean lineReversed = false;

    public BookEntryParent(Identifier entry) {
        this.entryId = entry;
    }

    /**
     * Creates a new BookEntryParent from the given json object.
     * @param ownerEntryId the entry id of the entry that contains this parent information. This is the CHILD not the parent.
     * @param json the json object to read from
     */
    public static BookEntryParent fromJson(Identifier ownerEntryId, JsonObject json) {
        var parentEntryPath = GsonHelper.getAsString(json, "entry");
        //entries can be without a namespace, in which case we use the owner entry namespace.
        var parentEntryId = parentEntryPath.contains(":") ?
                Identifier.parse(parentEntryPath) :
                Identifier.fromNamespaceAndPath(ownerEntryId.getNamespace(), parentEntryPath);

        var parent = new BookEntryParent(parentEntryId);
        parent.drawArrow = GsonHelper.getAsBoolean(json, "draw_arrow", parent.drawArrow);
        parent.lineEnabled = GsonHelper.getAsBoolean(json, "line_enabled", parent.lineEnabled);
        parent.lineReversed = GsonHelper.getAsBoolean(json, "line_reversed", parent.lineReversed);
        return parent;
    }

    public static BookEntryParent fromNetwork(FriendlyByteBuf buffer) {
        var entry = buffer.readResourceLocation();
        var parent = new BookEntryParent(entry);
        parent.drawArrow = buffer.readBoolean();
        parent.lineEnabled = buffer.readBoolean();
        parent.lineReversed = buffer.readBoolean();
        return parent;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.entryId);
        buffer.writeBoolean(this.drawArrow);
        buffer.writeBoolean(this.lineEnabled);
        buffer.writeBoolean(this.lineReversed);
    }

    public BookEntry getEntry() {
        throw new UnsupportedOperationException("BookEntryParent is not resolved yet.");
    }

    public Identifier getEntryId() {
        return this.entryId;
    }

    public boolean drawArrow() {
        return this.drawArrow;
    }

    public boolean isLineEnabled() {
        return this.lineEnabled;
    }

    public boolean isLineReversed() {
        return this.lineReversed;
    }
}
