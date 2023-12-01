package com.klikli_dev.modonomicon.integration;

import net.minecraft.world.item.ItemStack;

public class ModonomiconJeiIntegrationDummy implements ModonomiconJeiIntegration {
    @Override
    public boolean isJeiLoaded() {
        return false;
    }

    @Override
    public boolean isJEIRecipesGuiOpen() {
        return false;
    }

    @Override
    public void showRecipe(ItemStack stack) {

    }

    @Override
    public void showUses(ItemStack stack) {

    }
}
