/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.Nbt;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ModonomiconJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(ModonomiconAPI.ID, ModonomiconAPI.ID);

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ItemRegistry.MODONOMICON.get(), (stack, context) -> {
            if (!stack.hasTag() || !stack.getTag().contains(Nbt.ITEM_BOOK_ID_TAG)) {
                return "";
            }
            return stack.getTag().getString(Nbt.ITEM_BOOK_ID_TAG);
        });
    }
}
