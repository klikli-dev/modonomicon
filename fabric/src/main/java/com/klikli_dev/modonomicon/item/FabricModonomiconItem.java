/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FabricModonomiconItem extends ModonomiconItem {
    public FabricModonomiconItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public String getCreatorNamespace(ItemStack stack) {
        var bookId = getBookId(stack);
        if (bookId != null) {
            return bookId.getNamespace();
        }
        return super.getCreatorNamespace(stack);
    }
}
