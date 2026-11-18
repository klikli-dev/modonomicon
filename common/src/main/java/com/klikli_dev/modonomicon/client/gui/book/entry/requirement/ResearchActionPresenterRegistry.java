/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.action.ResearchAction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Presenters of research action types, keyed by action type id. Mods can register presenters for their own action types.
 * Unregistered types fall back to a generic presenter.
 */
public final class ResearchActionPresenterRegistry {

    private static final Map<Identifier, ResearchActionPresenter> PRESENTERS = new ConcurrentHashMap<>();

    private static final ResearchActionPresenter FALLBACK = new ResearchActionPresenter() {
        @Override
        public Component line(ResearchAction action, Component factTitle) {
            return Component.translatable(Gui.REQUIREMENTS_ACTION_PERFORM);
        }

        @Override
        public @Nullable Component unavailableReason(ResearchAction action, Player player) {
            return null;
        }
    };

    static {
        DefaultResearchActionPresenters.register();
    }

    private ResearchActionPresenterRegistry() {
    }

    /**
     * Registers the presenter for an action type. Replaces any presenter previously registered for the id.
     */
    public static void register(Identifier actionTypeId, ResearchActionPresenter presenter) {
        PRESENTERS.put(actionTypeId, presenter);
    }

    public static ResearchActionPresenter get(Identifier actionTypeId) {
        return PRESENTERS.getOrDefault(actionTypeId, FALLBACK);
    }
}
