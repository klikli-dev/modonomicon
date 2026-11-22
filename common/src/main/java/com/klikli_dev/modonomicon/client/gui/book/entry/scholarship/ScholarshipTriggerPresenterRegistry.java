/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

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
public final class ScholarshipTriggerPresenterRegistry {

    private static final Map<Identifier, ScholarshipTriggerPresenter> PRESENTERS = new ConcurrentHashMap<>();

    private static final ScholarshipTriggerPresenter FALLBACK = (hook, context) ->
            ScholarshipRow.of(List.of(ScholarshipIcons.icon(ScholarshipIcons.GENERIC)), Component.translatable(Gui.SCHOLARSHIP_TRIGGER_SPECIAL));

    private ScholarshipTriggerPresenterRegistry() {
    }

    /**
     * Registers the presenters of the built-in trigger types. Call from client setup.
     */
    public static void registerDefaults() {
        DefaultScholarshipTriggerPresenters.register();
    }

    /**
     * Registers the presenter for a trigger type. Replaces any presenter previously registered for the id.
     */
    public static void register(Identifier triggerTypeId, ScholarshipTriggerPresenter presenter) {
        PRESENTERS.put(triggerTypeId, presenter);
    }

    /**
     * The presenter of a trigger type, or a generic presenter if none is registered.
     */
    public static ScholarshipTriggerPresenter get(Identifier triggerTypeId) {
        return PRESENTERS.getOrDefault(triggerTypeId, FALLBACK);
    }

    /**
     * Describes the hook via the presenter of its trigger type.
     */
    public static ScholarshipRow present(ResearchHookDefinition<?> hook, ScholarshipContext context) {
        return get(hook.triggerType().id()).present(hook, context);
    }
}
