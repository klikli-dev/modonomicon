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
import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.networking.ResearchToastMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class ResearchHookService {

    private final ResearchStateManager stateManager;

    public ResearchHookService(ResearchStateManager stateManager) {
        this.stateManager = stateManager;
    }

    // --- Generic API ---

    /**
     * Fire hooks for a specific event context.
     */
    public <TTarget, TContext extends TriggerContext> boolean fire(
            TriggerType<TTarget, TContext> triggerType,
            TContext context
    ) {
        var hooks = triggerType.handler().resolve(triggerType, context.player(), context);
        return this.applyHooks(context.player(), hooks);
    }

    /**
     * Replay all hooks of a trigger type (for login / reset).
     */
    public <TTarget, TContext extends TriggerContext> boolean replayAll(
            TriggerType<TTarget, TContext> triggerType,
            ServerPlayer player
    ) {
        var handler = triggerType.handler();
        return handler.replayAll(player, this.stateManager);
    }

    /**
     * Check if any hook of this type can still progress for this context.
     */
    public <TTarget, TContext extends TriggerContext> boolean canProgress(
            TriggerType<TTarget, TContext> triggerType,
            TContext context
    ) {
        var hooks = triggerType.handler().resolve(triggerType, context.player(), context);
        var state = this.stateManager.getStateFor(context.player());
        for (var hook : hooks) {
            if (hook.factId() != null && !state.hasFact(hook.factId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if any hook of this type can still progress for this target.
     * This overload accepts Player (for client-side use) and looks up hooks directly from data.
     */
    private boolean canProgressByType(Player player, TriggerType<?, ?> triggerType) {
        var hooks = com.klikli_dev.modonomicon.research.data.ResearchDataManager.get().hooksForType(triggerType);
        var state = this.stateManager.getStateFor(player);
        for (var hook : hooks) {
            if (hook.factId() != null && !state.hasFact(hook.factId())) {
                return true;
            }
        }
        return false;
    }

    // --- Convenience sugar (delegate to generic) ---

    public boolean onEntryViewedOnce(ServerPlayer player, Identifier entryId) {
        return this.fire(TriggerTypeRegistry.ENTRY_VIEWED_ONCE,
                new EntryViewedContext(player, entryId));
    }

    public boolean canProgressEntryViewedOnce(Player player, Identifier entryId) {
        return this.canProgressByType(player, TriggerTypeRegistry.ENTRY_VIEWED_ONCE);
    }

    public boolean onItemCrafted(ServerPlayer player, ItemStack itemStack) {
        return this.fire(TriggerTypeRegistry.ITEM_CRAFTED,
                new ItemCraftedContext(player, itemStack));
    }

    public boolean onItemAcquired(ServerPlayer player, ItemStack itemStack) {
        return this.fire(TriggerTypeRegistry.ITEM_ACQUIRED,
                new ItemAcquiredContext(player, itemStack));
    }

    public boolean onAdvancement(ServerPlayer player, Identifier advancementId) {
        return this.fire(TriggerTypeRegistry.ADVANCEMENT,
                new AdvancementContext(player, advancementId));
    }

    /**
     * Replays all advancement-backed hooks against the player's current advancement progress.
     */
    public boolean replayAdvancements(ServerPlayer player) {
        return this.replayAll(TriggerTypeRegistry.ADVANCEMENT, player);
    }

    /**
     * Checks whether the player's research state is missing any advancement-backed facts.
     */
    public boolean needsAdvancementReplay(ServerPlayer player) {
        var state = this.stateManager.getStateFor(player);
        for (var hook : com.klikli_dev.modonomicon.research.data.ResearchDataManager.get()
                .hooksForType(TriggerTypeRegistry.ADVANCEMENT)) {
            if (hook.factId() != null && !state.hasFact(hook.factId())) {
                return true;
            }
        }
        return false;
    }

    // --- Shared apply logic ---

    private <TTarget> boolean applyHooks(ServerPlayer player, List<ResearchHookDefinition<TTarget>> hooks) {
        var before = BookDataManager.get().getBooks().values().stream()
                .collect(Collectors.toMap(Book::getId, book -> BookVisibilitySnapshots.collect(player, book)));
        ResearchStateManager.beginToastCollection();
        boolean changed = false;
        for (var hook : hooks) {
            if (hook.factId() != null) {
                changed |= this.stateManager.grantFact(player, hook.factId());
            } else if (hook.valueId() != null) {
                changed |= this.stateManager.incrementValue(player, hook.valueId(), hook.increment());
            }
        }
        changed |= this.stateManager.reevaluate(player);
        var triggers = ResearchStateManager.endToastCollection();
        if (!triggers.isEmpty()) {
            Services.NETWORK.sendTo(player, new ResearchToastMessage(triggers));
        }
        if (changed) {
            for (var book : BookDataManager.get().getBooks().values()) {
                BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before.get(book.getId()), BookVisibilitySnapshots.collect(player, book));
            }
        }
        return changed;
    }
}
