/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.platform;

import com.klikli_dev.modonomicon.item.ModonomiconItem;
import com.klikli_dev.modonomicon.item.NeoModonomiconItem;
import com.klikli_dev.modonomicon.platform.services.PlatformHelper;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;

public class NeoPlatformHelper implements PlatformHelper {

    @Override
    public String getPlatformName() {
        return "Neoforge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public PhysicalSide getPhysicalSide() {
        return FMLEnvironment.getDist() == Dist.CLIENT ? PhysicalSide.CLIENT : PhysicalSide.DEDICATED_SERVER;
    }

    @Override
    public ModonomiconItem createModonomiconItem(Item.Properties properties) {
        return new NeoModonomiconItem(properties);
    }
}