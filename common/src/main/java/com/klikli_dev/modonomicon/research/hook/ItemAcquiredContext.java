/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Context for item_acquired triggers.
 */
public record ItemAcquiredContext(ServerPlayer player, ItemStack stack) implements TriggerContext {
}
