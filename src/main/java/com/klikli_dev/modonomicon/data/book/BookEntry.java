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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookEntry {
    protected ResourceLocation id;
    protected BookCategory category;
    protected List<BookEntryParent> parents;
    protected String name;
    protected String description;
    protected BookIcon icon;
    protected int x;
    protected int y;

    //TODO: Pages?
    //TODO: entry type for background texture

    public BookEntry(ResourceLocation id, String name, String description, BookIcon icon, int x, int y, BookCategory category, List<BookEntryParent> parents) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.category = category;
        this.parents = parents;
    }

    public static BookEntry fromJson(ResourceLocation id, JsonObject json, Map<ResourceLocation, BookCategory> categories) {
        var name = json.get("name").getAsString();
        var description = GsonHelper.getAsString(json, "name", "");
        var icon = BookIcon.fromJson(json.get("icon"));
        var x = GsonHelper.getAsInt(json, "x");
        var y = GsonHelper.getAsInt(json, "y");
        var category = categories.get(new ResourceLocation(GsonHelper.getAsString(json, "category")));

        var parentEntries = new ArrayList<BookEntryParent>();

        if (json.has("parents")) {
            JsonArray parents = GsonHelper.getAsJsonArray(json, "parents");
            for (var parent : parents) {
                parentEntries.add(BookEntryParent.fromJson(parent.getAsJsonObject()));
            }
        }

        return new BookEntry(id, name, description, icon, x, y, category, parentEntries);
    }

    public static BookEntry fromNetwork(ResourceLocation id, FriendlyByteBuf buffer, Map<ResourceLocation, BookCategory> categories) {
        var name = buffer.readUtf();
        var description = buffer.readUtf();
        var icon = BookIcon.fromNetwork(buffer);
        var x = buffer.readInt();
        var y = buffer.readInt();
        var category = categories.get(buffer.readResourceLocation());

        var parentEntries = new ArrayList<BookEntryParent>();

        var parentCount = buffer.readInt();
        for (var i = 0; i < parentCount; i++) {
            parentEntries.add(BookEntryParent.fromNetwork(buffer));
        }

        return new BookEntry(id, name, description, icon, x, y, category, parentEntries);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.description);
        this.icon.toNetwork(buffer);
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeResourceLocation(this.category.getId());
        buffer.writeInt(this.parents.size());
        for (var parent : this.parents) {
            parent.toNetwork(buffer);
        }
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public List<BookEntryParent> getParents() {
        return this.parents;
    }

    public void setParents(List<BookEntryParent> parents) {
        this.parents = parents;
    }

    public String getName() {
        return this.name;
    }

    public BookIcon getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }
}
