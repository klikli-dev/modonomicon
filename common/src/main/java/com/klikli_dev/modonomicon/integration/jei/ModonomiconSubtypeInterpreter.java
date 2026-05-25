// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.integration.jei;

import com.klikli_dev.modonomicon.registry.DataComponentRegistry;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public class ModonomiconSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    private static final ModonomiconSubtypeInterpreter instance = new ModonomiconSubtypeInterpreter();

    public static ModonomiconSubtypeInterpreter get() {
        return instance;
    }

    @Override
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        if (!ingredient.has(DataComponentRegistry.BOOK_ID.get())) {
            return "";
        }
        return ingredient.get(DataComponentRegistry.BOOK_ID.get()).toString();
    }
}
