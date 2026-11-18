/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of requirement trigger presenters, keyed by trigger type id.
 */
public class RequirementPresenterRegistry {

    private static final Map<Identifier, TriggerRequirementPresenter> triggerPresenters = new ConcurrentHashMap<>();

    /**
     * Call from client setup
     */
    public static void registerDefaultRequirementPresenters() {
        DefaultTriggerRequirementPresenters.register();
    }

    /**
     * Registers the presenter for a research trigger type. Replaces any presenter previously registered for the id.
     * Call from client setup.
     */
    public static void registerTriggerRequirementPresenter(Identifier triggerTypeId, TriggerRequirementPresenter presenter) {
        triggerPresenters.put(triggerTypeId, presenter);
    }

    @Nullable
    public static TriggerRequirementPresenter getTriggerRequirementPresenter(Identifier triggerTypeId) {
        return triggerPresenters.get(triggerTypeId);
    }

    /**
     * Describes the hook via its trigger presenter, or returns null if no presenter is registered for its trigger type.
     */
    @Nullable
    public static RequirementRow presentTrigger(ResearchHookDefinition<?> hook, RequirementContext context) {
        var presenter = getTriggerRequirementPresenter(hook.triggerType().id());
        return presenter != null ? presenter.present(hook, context) : null;
    }
}
