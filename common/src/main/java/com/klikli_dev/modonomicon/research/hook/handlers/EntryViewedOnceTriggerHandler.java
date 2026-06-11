/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.hook.EntryViewedContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class EntryViewedOnceTriggerHandler implements TriggerHandler<Identifier, EntryViewedContext> {

    @SuppressWarnings("unchecked")
    @Override
    public List<ResearchHookDefinition<Identifier>> resolve(
            TriggerType<Identifier, EntryViewedContext> type,
            ServerPlayer player,
            EntryViewedContext context
    ) {
        return (List<ResearchHookDefinition<Identifier>>) (List<?>) ResearchDataManager.get().hooksFor(type, context);
    }

    @Override
    public boolean matches(Identifier target, EntryViewedContext context) {
        return target.equals(context.entryId());
    }

    @Override
    public @Nullable Object indexKey(Identifier target) {
        return target;
    }

    @Override
    public @Nullable Object indexKey(EntryViewedContext context) {
        return context.entryId();
    }
}
