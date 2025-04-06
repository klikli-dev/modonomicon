/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import net.minecraft.world.item.crafting.Recipe;

public abstract class BookProcessingRecipePage<T extends Recipe<?>> extends BookRecipePage<T> {

    public BookProcessingRecipePage(JsonDataHolder common) {
        super(common);
    }

    public BookProcessingRecipePage(NetworkDataHolder common) {
        super(common);
    }
}
