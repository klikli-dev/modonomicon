/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.hook;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.networking.ResearchToastMessage;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.networking.ResearchToastTrigger;
import com.klikli_dev.modonomicon.research.state.PlayerResearchState;
import com.klikli_dev.modonomicon.research.state.ResearchStateChangeListener;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;

/**
 * Entry point for research events. Resolves the hooks matching an event and applies them as one batch:
 * listeners before, fact/value mutations, reevaluation, toast sync, then listeners after.
 * See the {@code research} package documentation for the full pipeline.
 */
public class ResearchHookService {

    private final ResearchStateManager stateManager;
    private final List<ResearchStateChangeListener> listeners = new CopyOnWriteArrayList<>();

    public ResearchHookService(ResearchStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Registers a listener that wraps every hook batch applied by this service. Listeners run in registration order.
     */
    public void addStateChangeListener(ResearchStateChangeListener listener) {
        this.listeners.add(listener);
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
     * Check if any hook matching this context can still progress, i.e. grants a fact the player does not have yet.
     */
    public <TTarget, TContext extends TriggerContext> boolean canProgress(
            TriggerType<TTarget, TContext> triggerType,
            TContext context
    ) {
        var hooks = triggerType.handler().resolve(triggerType, context.player(), context);
        return hasPendingFact(this.stateManager.getStateFor(context.player()), hooks);
    }

    /**
     * Check if any hook of this type can still progress. Uses all hooks of the type, not only the ones matching a context.
     */
    private boolean canProgressByType(Player player, TriggerType<?, ?> triggerType) {
        return hasPendingFact(this.stateManager.getStateFor(player), ResearchDataManager.get().hooksForType(triggerType));
    }

    private static boolean hasPendingFact(PlayerResearchState state, List<? extends ResearchHookDefinition<?>> hooks) {
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

    /**
     * Returns true if any entry-viewed hook can still progress for the player.
     * <p>
     * The entry id is not used: the result is book-wide, not per entry. The book screens rely on this when they check
     * "any visible entry has progress", so changing it to a per-entry check would change those indicators.
     */
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

    /**
     * The outcome of an item_used event.
     * <ul>
     *     <li>{@code changed}: research state changed, so the state must be synced to the player.</li>
     *     <li>{@code handled}: the use is the one the hooks react to, so the vanilla use is cancelled. True if a hook grants a fact
     *     the player does not have yet, or a value hook with a positive consume changed state. Value hooks without consume count
     *     but let the vanilla use proceed, e.g. eating still eats.</li>
     *     <li>{@code consume}: the number of items of the used stack consumed, the largest consume of the handled hooks.
     *     Always 0 if not handled.</li>
     * </ul>
     */
    public record ItemUseOutcome(boolean changed, boolean handled, int consume) {
    }

    /**
     * Applies the item_used hooks for a stack the player used. A hook counts if it grants a fact the player did not have yet,
     * or if it increments a value with a positive consume. Only counting hooks handle the use and consume items.
     * If the stack holds fewer items than the largest consume of the counting hooks, they do not apply and the use is not handled.
     */
    public ItemUseOutcome onItemUsed(ServerPlayer player, ItemStack stack) {
        var type = TriggerTypeRegistry.ITEM_USED;
        var context = new ItemUsedContext(player, stack);
        var hooks = type.handler().resolve(type, player, context);
        var state = this.stateManager.getStateFor(player);
        var counting = hooks.stream().filter(hook -> counts(state, hook)).toList();
        int consume = counting.stream().mapToInt(ResearchHookDefinition::consume).max().orElse(0);
        if (consume > stack.getCount()) {
            var others = hooks.stream().filter(hook -> !counts(state, hook)).toList();
            return new ItemUseOutcome(this.applyHooks(player, others), false, 0);
        }
        boolean changed = this.applyHooks(player, hooks);
        return new ItemUseOutcome(changed, !counting.isEmpty(), consume);
    }

    private static boolean counts(PlayerResearchState state, ResearchHookDefinition<?> hook) {
        return hook.factId() != null ? !state.hasFact(hook.factId()) : hook.consume() > 0;
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
        return this.canProgressByType(player, TriggerTypeRegistry.ADVANCEMENT);
    }

    // --- Shared apply logic ---

    private <TTarget> boolean applyHooks(ServerPlayer player, List<ResearchHookDefinition<TTarget>> hooks) {
        return this.applyStateChange(player, () -> {
            boolean changed = false;
            for (var hook : hooks) {
                if (hook.factId() != null) {
                    changed |= this.stateManager.grantFact(player, hook.factId());
                } else if (hook.valueId() != null) {
                    changed |= this.stateManager.incrementValue(player, hook.valueId(), hook.increment());
                }
            }
            return changed;
        });
    }

    /**
     * Applies a batch of state mutations for a player, the same way hook batches are applied: listeners before, the
     * mutation, reevaluation, toast sync, then listeners after. Use this for every state change that is not a plain
     * hook, e.g. research actions and manual stage completion, so they share the pipeline with hooks.
     *
     * @param mutation performs the mutations and returns true if it changed research state
     * @return true if research state changed
     */
    public boolean applyStateChange(ServerPlayer player, BooleanSupplier mutation) {
        var completions = new ArrayList<ResearchStateChangeListener.Completion>(this.listeners.size());
        for (var listener : this.listeners) {
            completions.add(listener.beforeChange(player));
        }

        boolean changed;
        List<ResearchToastTrigger> triggers;
        ResearchStateManager.beginToastCollection();
        try {
            changed = mutation.getAsBoolean();
            changed |= this.stateManager.reevaluate(player);
        } finally {
            // Always pair the begin with an end so a failed batch cannot leave collection active for later batches.
            triggers = ResearchStateManager.endToastCollection();
        }
        if (!triggers.isEmpty()) {
            Services.NETWORK.sendTo(player, new ResearchToastMessage(triggers));
        }

        for (var completion : completions) {
            completion.afterChange(changed);
        }
        return changed;
    }
}
