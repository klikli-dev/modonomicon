/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Context for entry_viewed_once triggers.
 */
public record EntryViewedContext(ServerPlayer player, Identifier entryId) implements TriggerContext {
}
