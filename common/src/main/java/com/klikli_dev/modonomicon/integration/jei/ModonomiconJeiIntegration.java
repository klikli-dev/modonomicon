/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration.jei;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.world.item.ItemStack;

public interface ModonomiconJeiIntegration {

    static ModonomiconJeiIntegration get() {
        return Holder.INSTANCE;
    }

    private static ModonomiconJeiIntegration create() {
        if (!Services.PLATFORM.isModLoaded("jei")) {
            return new ModonomiconJeiIntegrationDummy();
        }

        try {
            return (ModonomiconJeiIntegration) Class.forName("com.klikli_dev.modonomicon.integration.jei.ModonomiconJeiIntegrationImpl")
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (ReflectiveOperationException | LinkageError e) {
            Modonomicon.LOG.warn("Failed to initialize JEI integration, falling back to dummy implementation.", e);
            return new ModonomiconJeiIntegrationDummy();
        }
    }

    boolean isLoaded();

    boolean isRecipesGuiOpen();

    void showRecipe(ItemStack stack);

    void showUses(ItemStack stack);

    final class Holder {
        private static final ModonomiconJeiIntegration INSTANCE = create();

        private Holder() {
        }
    }
}
