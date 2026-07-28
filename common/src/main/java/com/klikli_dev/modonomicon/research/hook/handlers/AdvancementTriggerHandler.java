/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook.handlers;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.hook.AdvancementContext;
import com.klikli_dev.modonomicon.research.hook.TriggerHandler;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class AdvancementTriggerHandler implements TriggerHandler<Identifier, AdvancementContext> {

    @SuppressWarnings("unchecked")
    @Override
    public List<ResearchHookDefinition<Identifier>> resolve(
            TriggerType<Identifier, AdvancementContext> type,
            ServerPlayer player,
            AdvancementContext context
    ) {
        return (List<ResearchHookDefinition<Identifier>>) (List<?>) ResearchDataManager.get().hooksFor(type, context);
    }

    @Override
    public boolean matches(Identifier target, AdvancementContext context) {
        return target.equals(context.advancementId());
    }

    @Override
    public @Nullable Object indexKey(Identifier target) {
        return target;
    }

    @Override
    public @Nullable Object indexKey(AdvancementContext context) {
        return context.advancementId();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        boolean changed = false;
        var serverAdvancements = player.level().getServer().getAdvancements();
        var playerAdvancements = player.getAdvancements();
        for (var hook : ResearchDataManager.get().hooksForType(TriggerTypeRegistry.ADVANCEMENT)) {
            var holder = serverAdvancements.get((Identifier) ((ResearchHookDefinition) hook).triggerTarget());
            if (holder == null) {
                continue;
            }
            var progress = playerAdvancements.getOrStartProgress(holder);
            if (!progress.isDone()) {
                continue;
            }
            if (hook.factId() != null) {
                changed |= stateManager.grantFact(player, hook.factId());
            } else if (hook.valueId() != null) {
                changed |= stateManager.incrementValue(player, hook.valueId(), hook.increment());
            }
        }
        return changed;
    }
}
