/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public interface BookPageJsonLoader<T extends BookPage> {
    T fromJson(ResourceLocation entryId, JsonObject json, HolderLookup.Provider provider);
}
