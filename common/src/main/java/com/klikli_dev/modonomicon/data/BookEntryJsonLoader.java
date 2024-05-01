/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public interface BookEntryJsonLoader<T> {
    T fromJson(ResourceLocation id, JsonObject json, boolean autoAddReadConditions, HolderLookup.Provider provider);
}
