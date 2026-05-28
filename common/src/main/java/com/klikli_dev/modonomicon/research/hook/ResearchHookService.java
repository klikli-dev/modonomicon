/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Function;

public class ResearchHookService {

    private final ResearchStateManager stateManager;
    private final Function<Identifier, List<ResearchHookDefinition>> entryViewedOnceLookup;

    public ResearchHookService(ResearchStateManager stateManager) {
        this(stateManager, ResearchDataManager.get()::entryViewedOnceHooksFor);
    }

    public ResearchHookService(ResearchStateManager stateManager, Function<Identifier, List<ResearchHookDefinition>> entryViewedOnceLookup) {
        this.stateManager = stateManager;
        this.entryViewedOnceLookup = entryViewedOnceLookup;
    }

    public boolean onEntryViewedOnce(ServerPlayer player, Identifier entryId) {
        boolean changed = false;
        for (var hook : this.entryViewedOnceLookup.apply(entryId)) {
            changed |= this.stateManager.grantFact(player, hook.factId());
        }
        return changed | this.stateManager.reevaluate(player);
    }
}
