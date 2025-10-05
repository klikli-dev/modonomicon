/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Nbt;
import com.klikli_dev.modonomicon.book.Book;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModonomiconCustomItemBase extends ModonomiconItem {

    public ResourceLocation bookId;

    public ModonomiconCustomItemBase(ResourceLocation bookId, Properties pProperties) {
        super(pProperties);
        this.bookId = bookId;
    }

    @Override
    public Book getBookFor(ItemStack stack) {
        if (!stack.getOrCreateTag().contains(Nbt.ITEM_BOOK_ID_TAG))
            stack.getTag().putString(Nbt.ITEM_BOOK_ID_TAG, this.bookId.toString());

        return super.getBookFor(stack);
    }
}
