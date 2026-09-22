// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.debug;

import com.klikli_dev.modonomicon.platform.ClientServices;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;

/**
 * Runtime state and helpers for the book debug overlay.
 * <p>
 * The overlay is enabled via the client config ({@code debug.debugOverlay}) and can be toggled at runtime with
 * {@link #TOGGLE_KEY} while a book screen is open. No {@code KeyMapping} is registered, the key is handled
 * directly by the book screens.
 */
public final class BookDebugOverlay {

    /**
     * The key used to toggle the overlay while a book screen is open. Not registered as a key mapping.
     */
    public static final int TOGGLE_KEY = GLFW.GLFW_KEY_F6;

    public static final int PAGE_OUTLINE = 0xFF3399FF;
    public static final int PAGE_FILL = 0x203399FF;
    public static final int CONTENT_OUTLINE = 0xFFFFAA00;
    public static final int CONTENT_FILL = 0x20FFAA00;
    public static final int TEXT_OUTLINE = 0xFF00CC66;
    public static final int TEXT_FILL = 0x2000CC66;

    private static boolean runtimeEnabled;

    private BookDebugOverlay() {
    }

    /**
     * @return true if the overlay should be rendered. Requires both the config option and the runtime toggle.
     */
    public static boolean isEnabled() {
        return runtimeEnabled && ClientServices.CLIENT_CONFIG.debugOverlay();
    }

    /**
     * Toggles the runtime state of the overlay. Does nothing if the feature is disabled in the config.
     *
     * @return true if the feature is available (config enabled) and the toggle was applied
     */
    public static boolean toggle() {
        if (!ClientServices.CLIENT_CONFIG.debugOverlay()) {
            return false;
        }
        runtimeEnabled = !runtimeEnabled;
        return true;
    }

    /**
     * Draws a translucent region with a solid outline, used to visualize content areas.
     */
    public static void renderRegion(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, int fillColor, int outlineColor) {
        if (width <= 0 || height <= 0) {
            return;
        }
        guiGraphics.fill(x, y, x + width, y + height, fillColor);
        guiGraphics.outline(x, y, width, height, outlineColor);
    }
}
