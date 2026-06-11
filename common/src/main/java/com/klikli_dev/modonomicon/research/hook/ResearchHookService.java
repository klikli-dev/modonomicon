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
import net.minecraft.resources.ResourceKey;
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
     * Fire hooks for a specific event.
     */
    public boolean fire(ServerPlayer player, TriggerType triggerType, Identifier targetId) {
        var handler = TriggerTypeRegistry.handler(triggerType);
        return this.applyHooks(player, handler.resolve(player, targetId));
    }

    /**
     * Replay all hooks of a trigger type (for login / reset).
     */
    public boolean replayAll(ServerPlayer player, TriggerType triggerType) {
        var handler = TriggerTypeRegistry.handler(triggerType);
        return handler.replayAll(player, this.stateManager);
    }

    /**
     * Check if any hook of this type can still progress for this target.
     */
    public boolean canProgress(ServerPlayer player, TriggerType triggerType, Identifier targetId) {
        var handler = TriggerTypeRegistry.handler(triggerType);
        var hooks = handler.resolve(player, targetId);
        var state = this.stateManager.getStateFor(player);
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
    private boolean canProgressByType(Player player, TriggerType triggerType, Identifier targetId) {
        var hooks = com.klikli_dev.modonomicon.research.data.ResearchDataManager.get().hooksFor(triggerType, targetId);
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
        return this.fire(player, TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId);
    }

    public boolean canProgressEntryViewedOnce(Player player, Identifier entryId) {
        return this.canProgressByType(player, TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId);
    }

    public boolean onItemCrafted(ServerPlayer player, ItemStack itemStack) {
        var itemId = itemStack.getItem().builtInRegistryHolder().unwrapKey()
                .map(ResourceKey::identifier).orElse(null);
        if (itemId == null) return false;
        return this.fire(player, TriggerTypeRegistry.ITEM_CRAFTED, itemId);
    }

    public boolean onItemAcquired(ServerPlayer player, ItemStack itemStack) {
        var itemId = itemStack.getItem().builtInRegistryHolder().unwrapKey()
                .map(ResourceKey::identifier).orElse(null);
        if (itemId == null) return false;
        return this.fire(player, TriggerTypeRegistry.ITEM_ACQUIRED, itemId);
    }

    public boolean onAdvancement(ServerPlayer player, Identifier advancementId) {
        return this.fire(player, TriggerTypeRegistry.ADVANCEMENT, advancementId);
    }

    /**
     * Replays all advancement-backed hooks against the player's current advancement progress.
     */
    public boolean replayAdvancements(ServerPlayer player) {
        return this.replayAll(player, TriggerTypeRegistry.ADVANCEMENT);
    }

    /**
     * Checks whether the player's research state is missing any advancement-backed facts.
     */
    public boolean needsAdvancementReplay(ServerPlayer player) {
        return this.canProgress(player, TriggerTypeRegistry.ADVANCEMENT, null)
                || this.hasStaleAdvancementFacts(player);
    }

    private boolean hasStaleAdvancementFacts(ServerPlayer player) {
        var state = this.stateManager.getStateFor(player);
        for (var hook : com.klikli_dev.modonomicon.research.data.ResearchDataManager.get().hooksForType(TriggerTypeRegistry.ADVANCEMENT)) {
            if (hook.factId() != null && !state.hasFact(hook.factId())) {
                return true;
            }
        }
        return false;
    }

    // --- Shared apply logic ---

    private boolean applyHooks(ServerPlayer player, List<ResearchHookDefinition> hooks) {
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
