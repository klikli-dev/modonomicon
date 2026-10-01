/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.Modonomicon;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Key mappings for out-of-book features. Registered per loader
 * (Fabric {@code KeyBindingHelper}, Neo/Forge {@code RegisterKeyMappingsEvent}).
 */
public class ModonomiconKeys {

    /**
     * Category id {@code modonomicon:modonomicon}, shown with the lang key
     * {@code key.category.modonomicon.modonomicon}.
     */
    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Modonomicon.loc("modonomicon"));

    /**
     * Hold this key while hovering an associated item to open the linked book entry/page.
     * Defaults to left alt, rebindable in the controls screen.
     */
    public static final KeyMapping OPEN_ASSOCIATED_ENTRY = new KeyMapping(
            "key.modonomicon.open_associated_entry",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            CATEGORY
    );
}
