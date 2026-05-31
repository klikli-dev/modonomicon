/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.data.BookDataManager;
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
        var before = BookDataManager.get().getBooks().values().stream().collect(java.util.stream.Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
        boolean changed = false;
        for (var hook : ResearchDataManager.get().data().advancementHooks().getOrDefault(advancementId, List.of())) {
            changed |= this.stateManager.grantFact(player, hook.factId());
        }
        if (changed) {
            changed |= this.stateManager.reevaluate(player);
            for (var book : BookDataManager.get().getBooks().values()) {
                BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
            }
        }
        return changed;
    }
}
