/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AdvancementResearchHookService {

    private final ResearchStateManager stateManager;

    public AdvancementResearchHookService(ResearchStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public boolean onAdvancement(ServerPlayer player, Identifier advancementId) {
        boolean changed = false;
        for (var hook : ResearchDataManager.get().data().advancementHooks().getOrDefault(advancementId, List.of())) {
            changed |= this.stateManager.grantFact(player, hook.factId());
        }
        if (changed) {
            changed |= this.stateManager.reevaluate(player);
        }
        return changed;
    }
}
