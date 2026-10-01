/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.associated;

import com.klikli_dev.modonomicon.book.associated.AssociatedItemMatcher;
import com.klikli_dev.modonomicon.client.ModonomiconKeys;
import com.klikli_dev.modonomicon.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

/**
 * Tracks the currently hovered item and opens the linked book entry/page once the
 * open key has been held for the configured duration.
 * <p>
 * The hovered stack is reported by the loader tooltip hooks on every tooltip gather;
 * the client tick compares it against the held key state.
 */
public class AssociatedHoverTracker {

    /**
     * How old the last tooltip gather may be to still count as hovering.
     */
    private static final long HOVER_FRESHNESS_MS = 250;

    private static ItemStack hoveredStack = ItemStack.EMPTY;
    private static long lastGatherMs;
    private static long holdStartMs;
    private static boolean openedForCurrentHold;

    /**
     * Called by the loader tooltip hooks whenever an item tooltip is gathered.
     */
    public static void onTooltipGather(ItemStack stack) {
        long now = Util.getMillis();

        if (!AssociatedItemMatcher.isSameStack(stack, hoveredStack)) {
            hoveredStack = stack.copy();
            holdStartMs = 0;
            openedForCurrentHold = false;
        }
        lastGatherMs = now;
    }

    /**
     * True while the open key is held down for the currently hovered stack.
     * Used for the live countdown in the tooltip hint.
     */
    public static boolean isHoldingFor(ItemStack stack) {
        return !stack.isEmpty()
                && ModonomiconKeys.OPEN_ASSOCIATED_ENTRY.isDown()
                && holdStartMs > 0
                && !openedForCurrentHold
                && AssociatedItemMatcher.isSameStack(stack, hoveredStack);
    }

    /**
     * Seconds until the book opens for the given stack: the live remaining time
     * while holding, otherwise the full configured hold duration.
     */
    public static double holdRemainingSeconds(ItemStack stack) {
        double total = holdDurationMs() / 1000.0;
        if (isHoldingFor(stack)) {
            return Math.max(0, (holdDurationMs() - (Util.getMillis() - holdStartMs)) / 1000.0);
        }
        return total;
    }

    /**
     * The full configured hold duration in seconds, shown before holding starts.
     */
    public static double holdTotalSeconds() {
        return holdDurationMs() / 1000.0;
    }

    private static int holdDurationMs() {
        return ClientServices.CLIENT_CONFIG.associatedItemsHoldDurationMs();
    }

    /**
     * Called every client tick. Opens the linked entry/page once the key was held
     * long enough while hovering the same stack.
     */
    public static void onClientTick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            return;
        }

        if (!ModonomiconKeys.OPEN_ASSOCIATED_ENTRY.isDown()) {
            holdStartMs = 0;
            openedForCurrentHold = false;
            return;
        }

        long now = Util.getMillis();
        if (now - lastGatherMs > HOVER_FRESHNESS_MS) {
            return;
        }

        if (hoveredStack.isEmpty() || openedForCurrentHold) {
            return;
        }

        if (holdStartMs == 0) {
            holdStartMs = now;
            return;
        }

        int holdDuration = holdDurationMs();
        if (now - holdStartMs >= holdDuration) {
            if (AssociatedTooltipHelper.openFromHover(hoveredStack)) {
                openedForCurrentHold = true;
            } else {
                //no association (or locked): wait for a fresh hover instead of retrying every tick
                holdStartMs = now;
            }
        }
    }
}
