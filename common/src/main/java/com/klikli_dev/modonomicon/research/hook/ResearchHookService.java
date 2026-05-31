/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
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
        var before = BookDataManager.get().getBooks().values().stream().collect(java.util.stream.Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
        boolean changed = false;
        for (var hook : this.entryViewedOnceLookup.apply(entryId)) {
            changed |= this.stateManager.grantFact(player, hook.factId());
        }
        changed |= this.stateManager.reevaluate(player);
        if (changed) {
            for (var book : BookDataManager.get().getBooks().values()) {
                BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
            }
        }
        return changed;
    }

    public boolean canProgressEntryViewedOnce(Player player, Identifier entryId) {
        var state = this.stateManager.getStateFor(player);
        for (var hook : this.entryViewedOnceLookup.apply(entryId)) {
            if (!state.hasFact(hook.factId())) {
                return true;
            }
        }
        return false;
    }
}
