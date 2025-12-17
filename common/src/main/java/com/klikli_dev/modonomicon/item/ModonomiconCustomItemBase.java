/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class ModonomiconCustomItemBase extends ModonomiconItem {

    public Identifier bookId;

    public ModonomiconCustomItemBase(Identifier bookId, Properties pProperties) {
        super(pProperties);
        this.bookId = bookId;
    }

    @Override
    public Book getBookFor(ItemStack stack) {

        if (!stack.has(DataComponentRegistry.BOOK_ID.get())) {
            stack.set(DataComponentRegistry.BOOK_ID.get(), this.bookId);
        }

        return super.getBookFor(stack);
    }
}
