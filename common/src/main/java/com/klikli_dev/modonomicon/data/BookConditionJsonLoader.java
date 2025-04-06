/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public interface BookConditionJsonLoader<T extends BookCondition> {
    T fromJson(ResourceLocation conditionParentId, JsonObject json, HolderLookup.Provider provider);
}
