/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;

public interface JsonLoader<T> {
    T fromJson(JsonObject json, HolderLookup.Provider provider);
}
