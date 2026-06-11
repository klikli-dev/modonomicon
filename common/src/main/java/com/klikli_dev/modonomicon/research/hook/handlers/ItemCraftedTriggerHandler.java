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
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Trigger handler for item_crafted hooks.
 * Supports component matching. No replay needed — crafting is a one-shot event.
 */
public class ItemCraftedTriggerHandler implements TriggerHandler {

    @Override
    public List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier itemId) {
        return ResearchDataManager.get().hooksFor(TriggerTypeRegistry.ITEM_CRAFTED, itemId);
    }
}
