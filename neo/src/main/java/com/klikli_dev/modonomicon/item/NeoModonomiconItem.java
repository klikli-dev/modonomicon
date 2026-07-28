/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NeoModonomiconItem extends ModonomiconItem {
    public NeoModonomiconItem(Properties properties) {
        super(properties);
    }

    @Nullable
    public String getCreatorModId(HolderLookup.Provider registries, ItemStack itemStack) {
        var bookId = getBookId(itemStack);
        if (bookId != null) {
            return bookId.getNamespace();
        }
        return null;
    }
}
