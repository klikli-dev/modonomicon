/*
 * LGPL-3.0
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

package com.klikli_dev.modonomicon.datagen.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BookTextHolderModel {
    protected Component component;
    protected String string;

    public BookTextHolderModel(Component component) {
        this.component = component;
    }

    public BookTextHolderModel(@NotNull String string) {
        this.string = string;
    }

    public boolean hasComponent() {
        return this.component != null;
    }

    public JsonElement toJson() {
        if (this.hasComponent()) {
            return Component.Serializer.toJsonTree(this.component);
        }
        return new JsonPrimitive(this.string);
    }
}
