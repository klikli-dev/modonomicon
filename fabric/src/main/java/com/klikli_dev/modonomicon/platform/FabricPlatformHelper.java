/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.platform;

import com.klikli_dev.modonomicon.item.FabricModonomiconItem;
import com.klikli_dev.modonomicon.item.ModonomiconItem;
import com.klikli_dev.modonomicon.platform.services.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;

public class FabricPlatformHelper implements PlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public PhysicalSide getPhysicalSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? PhysicalSide.CLIENT : PhysicalSide.DEDICATED_SERVER;
    }

    @Override
    public ModonomiconItem createModonomiconItem(Item.Properties properties) {
        return new FabricModonomiconItem(properties);
    }
}
