/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client;

import com.klikli_dev.modonomicon.Modonomicon;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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

    /**
     * Physical held-state of the open key, for use inside screens.
     * <p>
     * Vanilla only updates {@link KeyMapping#isDown()} while no screen is open, so it is
     * permanently false when hovering items in an inventory or recipe viewer. Like JEI and
     * Create Ponder, poll the physical state of the currently bound key instead. The bound
     * key is resolved via {@link KeyMapping#saveString()}, so rebinds (including mouse
     * buttons) keep working.
     */
    public static boolean isOpenAssociatedEntryDown() {
        var mapping = OPEN_ASSOCIATED_ENTRY;
        if (mapping.isUnbound()) {
            return false;
        }

        InputConstants.Key bound;
        try {
            bound = InputConstants.getKey(mapping.saveString());
        } catch (IllegalArgumentException e) {
            Modonomicon.LOG.warn("Failed to resolve bound key for '{}': {}", mapping.getName(), mapping.saveString());
            return false;
        }

        var window = Minecraft.getInstance().getWindow();
        if (window == null) {
            return false;
        }

        return switch (bound.getType()) {
            case KEYSYM -> InputConstants.isKeyDown(window, bound.getValue());
            case MOUSE -> GLFW.glfwGetMouseButton(window.handle(), bound.getValue()) == GLFW.GLFW_PRESS;
            //scancodes cannot be polled physically; fall back to the vanilla state (works outside screens)
            case SCANCODE -> mapping.isDown();
        };
    }
}
