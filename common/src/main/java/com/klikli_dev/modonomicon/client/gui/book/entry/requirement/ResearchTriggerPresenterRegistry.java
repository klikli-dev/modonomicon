/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presenters of research trigger types, keyed by trigger type id. Every hook that grants a fact or increments a value
 * is shown as a requirement row through its trigger presenter. Unregistered trigger types fall back to a generic row.
 */
public final class ResearchTriggerPresenterRegistry {

    private static final Map<Identifier, ResearchTriggerPresenter> PRESENTERS = new ConcurrentHashMap<>();

    private static final ResearchTriggerPresenter FALLBACK = (hook, context) ->
            RequirementRow.of(List.of(ResearchIcons.icon(ResearchIcons.GENERIC)), Component.translatable(Gui.REQUIREMENTS_TRIGGER_SPECIAL));

    private ResearchTriggerPresenterRegistry() {
    }

    /**
     * Registers the presenters of the built-in trigger types. Call from client setup.
     */
    public static void registerDefaults() {
        DefaultResearchTriggerPresenters.register();
    }

    /**
     * Registers the presenter for a trigger type. Replaces any presenter previously registered for the id.
     */
    public static void register(Identifier triggerTypeId, ResearchTriggerPresenter presenter) {
        PRESENTERS.put(triggerTypeId, presenter);
    }

    /**
     * The presenter of a trigger type, or a generic presenter if none is registered.
     */
    public static ResearchTriggerPresenter get(Identifier triggerTypeId) {
        return PRESENTERS.getOrDefault(triggerTypeId, FALLBACK);
    }

    /**
     * Describes the hook via the presenter of its trigger type.
     */
    public static RequirementRow present(ResearchHookDefinition<?> hook, RequirementContext context) {
        return get(hook.triggerType().id()).present(hook, context);
    }
}
