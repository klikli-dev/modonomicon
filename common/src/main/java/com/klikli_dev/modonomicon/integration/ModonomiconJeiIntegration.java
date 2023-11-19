/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration;

import net.minecraft.world.item.ItemStack;

public interface ModonomiconJeiIntegration {

    ModonomiconJeiIntegration instance = new ModonomiconJeiIntegrationDummy();

    static ModonomiconJeiIntegration get() {
        return instance;
    }

    boolean isJeiLoaded();

    boolean isJEIRecipesGuiOpen();

    void showRecipe(ItemStack stack);

    void showUses(ItemStack stack);
}
