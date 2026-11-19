/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Context for item_used triggers: the stack the player used in the air.
 */
public record ItemUsedContext(ServerPlayer player, ItemStack stack) implements TriggerContext {
}
