/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.hook.ItemAcquiredContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAcquiredTriggerHandler implements TriggerHandler<ItemStackTemplate, ItemAcquiredContext> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<ResearchHookDefinition<ItemStackTemplate>> resolve(
            TriggerType<ItemStackTemplate, ItemAcquiredContext> type,
            ServerPlayer player,
            ItemAcquiredContext context
    ) {
        return (List) ResearchDataManager.get().hooksForType(type).stream()
                .filter(h -> matches((ItemStackTemplate) h.triggerTarget(), context))
                .toList();
    }

    @Override
    public boolean matches(ItemStackTemplate target, ItemAcquiredContext context) {
        return target.item().equals(context.stack().getItem().builtInRegistryHolder());
    }

    @Override
    public @Nullable Object indexKey(ItemStackTemplate target) {
        return target.item().unwrapKey().map(ResourceKey::identifier).orElse(null);
    }

    @Override
    public @Nullable Object indexKey(ItemAcquiredContext context) {
        return context.stack().getItem().builtInRegistryHolder().unwrapKey()
                .map(ResourceKey::identifier).orElse(null);
    }
}
