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
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Trigger handler for advancement hooks.
 * Supports replay on login — advancements can be completed at any time
 * and need to be re-checked to restore research state.
 */
public class AdvancementTriggerHandler implements TriggerHandler {

    @Override
    public List<ResearchHookDefinition> resolve(ServerPlayer player, Identifier advancementId) {
        return ResearchDataManager.get().hooksFor(TriggerTypeRegistry.ADVANCEMENT, advancementId);
    }

    @Override
    public boolean replayAll(ServerPlayer player, ResearchStateManager stateManager) {
        boolean changed = false;
        var serverAdvancements = player.level().getServer().getAdvancements();
        var playerAdvancements = player.getAdvancements();
        for (var hook : ResearchDataManager.get().hooksForType(TriggerTypeRegistry.ADVANCEMENT)) {
            var holder = serverAdvancements.get(hook.triggerTargetId());
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
