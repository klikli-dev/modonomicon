/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import net.minecraft.server.level.ServerPlayer;

/**
 * Base context for all hook triggers.
 * Provides access to the player who triggered the event.
 */
public interface TriggerContext {

    ServerPlayer player();
}
