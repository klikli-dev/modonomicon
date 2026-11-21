/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.registry.ResearchEffectTypeRegistry;
import com.klikli_dev.modonomicon.research.effect.AddValueEffect;
import com.klikli_dev.modonomicon.research.effect.ExperienceEffect;
import com.klikli_dev.modonomicon.research.effect.GiveItemEffect;
import com.klikli_dev.modonomicon.research.effect.GrantFactEffect;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class DefaultScholarshipEffectPresenters {

    private DefaultScholarshipEffectPresenters() {
    }

    static void register() {
        ScholarshipEffectPresenterRegistry.register(ResearchEffectTypeRegistry.GIVE_ITEM.id(), new ScholarshipEffectPresenter() {
            @Override
            public ScholarshipRow present(ResearchEffect effect, ScholarshipContext context) {
                var stack = ((GiveItemEffect) effect).stack().create();
                var icon = stack.isEmpty() ? ScholarshipIcons.icon(ScholarshipIcons.EFFECT_GIVE_ITEM) : ScholarshipIcon.of(stack);
                return ScholarshipRow.of(List.of(icon), Component.translatable(Gui.SCHOLARSHIP_EFFECT, stack.getHoverName()));
            }

            @Override
            public Component line(ResearchEffect effect, ScholarshipActionContext context) {
                var stack = ((GiveItemEffect) effect).stack().create();
                var key = context.action().repeatable() && context.performed() ? Gui.SCHOLARSHIP_ACTION_TAKE_AGAIN : Gui.SCHOLARSHIP_ACTION_TAKE;
                return Component.translatable(key, stack.getHoverName());
            }

            @Override
            public @Nullable Component unavailableReason(ResearchEffect effect, Player player) {
                var give = (GiveItemEffect) effect;
                ItemStack stack = give.stack().create();
                if (give.requireAbsent() && !stack.isEmpty() && player.getInventory().contains(stack)) {
                    return Component.translatable(Gui.SCHOLARSHIP_ACTION_ALREADY_HELD);
                }
                return null;
            }
        });

        ScholarshipEffectPresenterRegistry.register(ResearchEffectTypeRegistry.EXPERIENCE.id(), new ScholarshipEffectPresenter() {
            @Override
            public ScholarshipRow present(ResearchEffect effect, ScholarshipContext context) {
                var experience = (ExperienceEffect) effect;
                var amount = experience.levels()
                        ? Component.translatable(Gui.SCHOLARSHIP_EFFECT_LEVELS, experience.amount())
                        : Component.translatable(Gui.SCHOLARSHIP_EFFECT_EXPERIENCE, experience.amount());
                return ScholarshipRow.of(List.of(ScholarshipIcons.icon(ScholarshipIcons.EFFECT_EXPERIENCE)), Component.translatable(Gui.SCHOLARSHIP_EFFECT, amount));
            }
        });

        ScholarshipEffectPresenterRegistry.register(ResearchEffectTypeRegistry.GRANT_FACT.id(), new ScholarshipEffectPresenter() {
            @Override
            public ScholarshipRow present(ResearchEffect effect, ScholarshipContext context) {
                var factId = ((GrantFactEffect) effect).factId();
                var fact = ScholarshipRows.factWithFallbackIcon(factId, context, false, ScholarshipIcons.icon(ScholarshipIcons.EFFECT_GRANT_FACT));
                return fact.withTitle(Component.translatable(Gui.SCHOLARSHIP_EFFECT, fact.title()));
            }
        });

        ScholarshipEffectPresenterRegistry.register(ResearchEffectTypeRegistry.ADD_VALUE.id(), new ScholarshipEffectPresenter() {
            @Override
            public ScholarshipRow present(ResearchEffect effect, ScholarshipContext context) {
                var add = (AddValueEffect) effect;
                var definition = context.data().valueDefinitions().get(add.valueId());
                var display = definition != null ? definition.display().orElse(null) : null;
                var icon = display != null && display.icon().isPresent()
                        ? ScholarshipIcon.of(display.icon().get())
                        : ScholarshipIcons.icon(ScholarshipIcons.EFFECT_ADD_VALUE);
                var name = display != null && display.title().isPresent()
                        ? display.title().get()
                        : Component.literal(add.valueId().getPath());
                return ScholarshipRow.of(List.of(icon), Component.translatable(Gui.SCHOLARSHIP_EFFECT,
                        Component.translatable(Gui.SCHOLARSHIP_EFFECT_VALUE, add.amount(), name)));
            }
        });

        ScholarshipEffectPresenterRegistry.register(ResearchEffectTypeRegistry.FUNCTION.id(), new ScholarshipEffectPresenter() {
            @Override
            public ScholarshipRow present(ResearchEffect effect, ScholarshipContext context) {
                return ScholarshipRow.of(List.of(ScholarshipIcons.icon(ScholarshipIcons.EFFECT_FUNCTION)), Component.translatable(Gui.SCHOLARSHIP_EFFECT_SPECIAL));
            }
        });
    }
}
