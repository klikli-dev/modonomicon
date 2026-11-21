/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presenters of research effect types, keyed by effect type id. Used for reward rows and for describing actions.
 * Unregistered types fall back to a generic presenter.
 */
public final class ScholarshipEffectPresenterRegistry {

    private static final Map<Identifier, ScholarshipEffectPresenter> PRESENTERS = new ConcurrentHashMap<>();

    private static final ScholarshipEffectPresenter FALLBACK = new ScholarshipEffectPresenter() {
        @Override
        public ScholarshipRow present(ResearchEffect effect, ScholarshipContext context) {
            return ScholarshipRow.of(List.of(ScholarshipIcons.icon(ScholarshipIcons.GENERIC)), Component.translatable(Gui.SCHOLARSHIP_REWARD_SPECIAL));
        }
    };

    private ScholarshipEffectPresenterRegistry() {
    }

    /**
     * Registers the presenters of the built-in effect types. Call from client setup.
     */
    public static void registerDefaults() {
        DefaultScholarshipEffectPresenters.register();
    }

    /**
     * Registers the presenter for an effect type. Replaces any presenter previously registered for the id.
     */
    public static void register(Identifier effectTypeId, ScholarshipEffectPresenter presenter) {
        PRESENTERS.put(effectTypeId, presenter);
    }

    /**
     * The presenter of an effect type, or a generic presenter if none is registered.
     */
    public static ScholarshipEffectPresenter get(Identifier effectTypeId) {
        return PRESENTERS.getOrDefault(effectTypeId, FALLBACK);
    }
}
