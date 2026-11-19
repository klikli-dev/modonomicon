/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presenters of research effect types, keyed by effect type id. Used for reward rows and for describing actions.
 * Unregistered types fall back to a generic presenter.
 */
public final class ResearchEffectPresenterRegistry {

    private static final Map<Identifier, ResearchEffectPresenter> PRESENTERS = new ConcurrentHashMap<>();

    private static final ResearchEffectPresenter FALLBACK = new ResearchEffectPresenter() {
        @Override
        public RequirementRow present(ResearchEffect effect, RequirementContext context) {
            return RequirementRow.of(List.of(RequirementIcon.of(new ItemStack(Items.BOOK))), Component.translatable(Gui.REQUIREMENTS_REWARD_SPECIAL));
        }
    };

    private ResearchEffectPresenterRegistry() {
    }

    /**
     * Registers the presenters of the built-in effect types. Call from client setup.
     */
    public static void registerDefaults() {
        DefaultResearchEffectPresenters.register();
    }

    /**
     * Registers the presenter for an effect type. Replaces any presenter previously registered for the id.
     */
    public static void register(Identifier effectTypeId, ResearchEffectPresenter presenter) {
        PRESENTERS.put(effectTypeId, presenter);
    }

    /**
     * The presenter of an effect type, or a generic presenter if none is registered.
     */
    public static ResearchEffectPresenter get(Identifier effectTypeId) {
        return PRESENTERS.getOrDefault(effectTypeId, FALLBACK);
    }
}
