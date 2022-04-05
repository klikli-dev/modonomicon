/*
 * LGPL-3-0
 *
 * Copyright (C) 2022 klikli-dev
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

package com.klikli_dev.modonomicon.datagen.book.page;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BookPageModel {

    protected ResourceLocation type;
    protected String anchor;

    protected BookPageModel(ResourceLocation type, @NotNull String anchor) {
        this.type = type;
        this.anchor = anchor;
    }

    public ResourceLocation getType() {
        return this.type;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.type.toString());
        json.addProperty("anchor", this.anchor);
        return json;
    }
}
