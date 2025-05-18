/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api;

import com.google.common.base.Suppliers;
import com.klikli_dev.modonomicon.api.datagen.BookContextHelper;
import com.klikli_dev.modonomicon.api.datagen.CategoryEntryMap;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.MultiblockPreviewData;
import com.klikli_dev.modonomicon.api.stub.ModonomiconAPIStub;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface ModonomiconAPI {

    String ID = "modonomicon";
    String Name = "Modonomicon";

    static ModonomiconAPI get() {
        return Helper.lazyInstance.get();
    }

    /**
     * False if a real API instance is provided
     */
    boolean isStub();

    /**
     * You should use .context() in the CategoryProvider instead.
     */
    @Deprecated
    BookContextHelper getContextHelper(String modid);

    /**
     * You should use .entryMap() in the CategoryProvider instead.
     */
    @Deprecated
    CategoryEntryMap getEntryMap();

    Multiblock getMultiblock(ResourceLocation id);

    /**
     * Gets the multiblock currently previewed by the player, or null if none.
     * Make sure to check for isAnchored(), a non-anchored multiblock can move any tick if the player looks around.
     * Use {@link Multiblock#simulate(Level, BlockPos, Rotation, boolean, boolean)} to get the simulation results.
     * Client side only!
     */
    @Nullable
    MultiblockPreviewData getCurrentPreviewMultiblock();

    class Helper {
        private static final Supplier<ModonomiconAPI> lazyInstance = Suppliers.memoize(() -> {
            try {
                return (ModonomiconAPI) Class.forName("com.klikli_dev.modonomicon.apiimpl.ModonomiconAPIImpl").getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                LogManager.getLogger().warn("Could not find ModonomiconAPI, using stub.");
                return ModonomiconAPIStub.get();
            }
        });
    }

}
