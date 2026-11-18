/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchActionTypeRegistry;
import com.klikli_dev.modonomicon.research.action.GiveItemAction;
import com.klikli_dev.modonomicon.research.action.ResearchAction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

final class DefaultResearchActionPresenters {

    private DefaultResearchActionPresenters() {
    }

    static void register() {
        ResearchActionPresenterRegistry.register(ResearchActionTypeRegistry.GRANT_FACT.id(), new ResearchActionPresenter() {
            @Override
            public Component line(ResearchAction action, Component factTitle) {
                return Component.translatable(Gui.REQUIREMENTS_ACTION_RECORD, factTitle);
            }

            @Override
            public @Nullable Component unavailableReason(ResearchAction action, Player player) {
                return null;
            }
        });

        ResearchActionPresenterRegistry.register(ResearchActionTypeRegistry.GIVE_ITEM.id(), new ResearchActionPresenter() {
            @Override
            public Component line(ResearchAction action, Component factTitle) {
                var give = (GiveItemAction) action;
                return Component.translatable(give.repeatable() ? Gui.REQUIREMENTS_ACTION_RECEIVE_AGAIN : Gui.REQUIREMENTS_ACTION_RECEIVE, give.stack().create().getHoverName());
            }

            @Override
            public @Nullable Component unavailableReason(ResearchAction action, Player player) {
                var give = (GiveItemAction) action;
                ItemStack stack = give.stack().create();
                if (give.requireAbsent() && !stack.isEmpty() && player.getInventory().contains(stack)) {
                    return Component.translatable(Gui.REQUIREMENTS_ACTION_ALREADY_HELD);
                }
                return null;
            }
        });
    }
}
