/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.data.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookEntryParent {
    protected ResourceLocation entryId;
    protected boolean drawArrow = true;
    protected boolean lineEnabled = true;
    protected boolean lineReversed = false;
    //TODO: allow loading the booleans from json

    public BookEntryParent(ResourceLocation entry) {
        this.entryId = entry;
    }

    public static BookEntryParent fromJson(JsonObject json) {
        var entry = new ResourceLocation(json.get("entry").getAsString());
        var parent = new BookEntryParent(entry);
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
        return null;
    }

    public ResourceLocation getEntryId() {
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
