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

package com.klikli_dev.modonomicon.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import net.minecraft.network.chat.Component;

public class BookGsonHelper {

    public static BookTextHolder getAsBookTextHolder(JsonObject pJson, String pMemberName, BookTextHolder pFallback) {
        return pJson.has(pMemberName) ? convertToBookTextHolder(pJson.get(pMemberName), pMemberName) : pFallback;
    }

    public static BookTextHolder convertToBookTextHolder(JsonElement pJson, String pMemberName) {
        if (pJson.isJsonPrimitive()) {
            return new BookTextHolder(pJson.getAsString());
        } else {
            return new BookTextHolder(Component.Serializer.fromJson(pJson));
        }
    }
}
