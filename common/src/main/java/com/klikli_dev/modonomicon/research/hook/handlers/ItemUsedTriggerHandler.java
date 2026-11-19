/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.hook.ItemUsedContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Matches item_used hooks by item and components, like item_acquired and item_crafted.
 */
public class ItemUsedTriggerHandler implements TriggerHandler<ResearchItemMatcher, ItemUsedContext> {

    @Override
    public List<ResearchHookDefinition<ResearchItemMatcher>> resolve(
            TriggerType<ResearchItemMatcher, ItemUsedContext> type,
            ServerPlayer player,
            ItemUsedContext context
    ) {
        return ResearchDataManager.get().hooksFor(type, context);
    }

    @Override
    public boolean matches(ResearchItemMatcher target, ItemUsedContext context) {
        return target.matches(context.stack());
    }

    @Override
    public @Nullable Object indexKey(ResearchItemMatcher target) {
        return target.itemId();
    }

    @Override
    public @Nullable Object indexKey(ItemUsedContext context) {
        return context.stack().getItem().builtInRegistryHolder().unwrapKey()
                .map(ResourceKey::identifier).orElse(null);
    }
}
