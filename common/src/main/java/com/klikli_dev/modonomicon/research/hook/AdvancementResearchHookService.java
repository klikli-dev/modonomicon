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
import com.klikli_dev.modonomicon.networking.ResearchToastMessage;
import com.klikli_dev.modonomicon.platform.Services;
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

    /**
     * Replays all advancement-backed hooks against the player's current advancement progress.
     * Used after research reset and on player login to restore advancement-backed research state.
     *
     * @return true if any research state changed
     */
    public boolean replayAll(ServerPlayer player) {
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(java.util.stream.Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
        boolean changed = false;
        var serverAdvancements = player.level().getServer().getAdvancements();
        var playerAdvancements = player.getAdvancements();
        for (var entry : ResearchDataManager.get().data().advancementHooks().entrySet()) {
            var advancementId = entry.getKey();
            var hooks = entry.getValue();
            var holder = serverAdvancements.get(advancementId);
            if (holder == null) {
                continue; // advancement removed from datapack, skip
            }
            var progress = playerAdvancements.getOrStartProgress(holder);
            if (!progress.isDone()) {
                continue; // advancement not yet completed, skip
            }
            for (var hook : hooks) {
                if (hook.factId() != null) {
                    changed |= this.stateManager.grantFact(player, hook.factId());
                } else if (hook.valueId() != null) {
                    changed |= this.stateManager.incrementValue(player, hook.valueId(), hook.increment());
                }
            }
        }
        if (changed) {
            changed |= this.stateManager.reevaluate(player);
            for (var book : BookDataManager.get().getBooks().values()) {
                BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
            }
        }
        return changed;
    }

    /**
     * Checks whether the player's research state is missing any advancement-backed facts.
     * If so, the state was likely reset and advancement hooks need to be replayed.
     */
    public boolean needsAdvancementReplay(ServerPlayer player) {
        var state = this.stateManager.getStateFor(player);
        for (var hooks : ResearchDataManager.get().data().advancementHooks().values()) {
            for (var hook : hooks) {
                if (hook.factId() != null && !state.hasFact(hook.factId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onAdvancement(ServerPlayer player, Identifier advancementId) {
        var before = BookDataManager.get().getBooks().values().stream().collect(java.util.stream.Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
        ResearchStateManager.beginToastCollection();
        boolean changed = false;
        for (var hook : ResearchDataManager.get().data().advancementHooks().getOrDefault(advancementId, List.of())) {
            if (hook.factId() != null) {
                changed |= this.stateManager.grantFact(player, hook.factId());
            } else if (hook.valueId() != null) {
                changed |= this.stateManager.incrementValue(player, hook.valueId(), hook.increment());
            }
        }
        var triggers = ResearchStateManager.endToastCollection();
        if (!triggers.isEmpty()) {
            Services.NETWORK.sendTo(player, new ResearchToastMessage(triggers));
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
