/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.item;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModonomiconCreativeModeTab extends CreativeModeTab {
    public ModonomiconCreativeModeTab() {
        super(Modonomicon.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ItemRegistry.MODONOMICON.get());
    }
}
