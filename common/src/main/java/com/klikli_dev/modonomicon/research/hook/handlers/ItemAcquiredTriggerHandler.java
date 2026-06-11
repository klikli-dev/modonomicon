/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Trigger handler for item_acquired hooks.
 * Supports component matching. No replay needed — item acquisition is a one-shot event.
 */
public class ItemAcquiredTriggerHandler implements TriggerHandler {

    @Override
    public List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier itemId) {
        return ResearchDataManager.get().hooksFor(TriggerTypeRegistry.ITEM_ACQUIRED, itemId);
    }
}
