/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class BookGsonHelper {

    public static BookTextHolder getAsBookTextHolder(JsonObject pJson, String pMemberName, BookTextHolder pFallback, HolderLookup.Provider provider) {
        return pJson.has(pMemberName) ? convertToBookTextHolder(pJson.get(pMemberName), pMemberName, provider) : pFallback;
    }

    public static BookTextHolder convertToBookTextHolder(JsonElement pJson, String pMemberName, HolderLookup.Provider provider) {
        if (pJson.isJsonPrimitive()) {
            return new BookTextHolder(pJson.getAsString());
        } else {
            return new BookTextHolder(ComponentSerialization.CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), pJson).getOrThrow());
        }
    }
}
