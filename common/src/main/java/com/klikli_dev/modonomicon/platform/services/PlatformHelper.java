/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.platform.services;

import com.klikli_dev.modonomicon.item.ModonomiconItem;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface PlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return this.isDevelopmentEnvironment() ? "development" : "production";
    }

    PhysicalSide getPhysicalSide();

    /**
     * Creates a ModonomiconItem for the given properties.
     * On NeoForge, this returns an item that implements IItemExtension#getCreatorModId.
     * On Fabric, this returns an item that implements FabricItem#getCreatorNamespace.
     *
     * @param properties the item properties
     * @return a new ModonomiconItem instance
     */
    ModonomiconItem createModonomiconItem(Item.Properties properties);

    enum PhysicalSide {
        CLIENT,
        DEDICATED_SERVER
    }
}