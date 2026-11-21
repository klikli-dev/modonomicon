/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * The presenters of the built-in research trigger types.
 */
final class DefaultScholarshipTriggerPresenters {

    private DefaultScholarshipTriggerPresenters() {
    }

    static void register() {
        ScholarshipTriggerPresenterRegistry.register(TriggerTypeRegistry.ITEM_CRAFTED.id(), (hook, context) ->
                itemRow(Gui.SCHOLARSHIP_CRAFT_ITEM, Gui.SCHOLARSHIP_NOT_MET_CRAFT, (ResearchItemMatcher) hook.triggerTarget()));

        ScholarshipTriggerPresenterRegistry.register(TriggerTypeRegistry.ITEM_ACQUIRED.id(), (hook, context) ->
                itemRow(Gui.SCHOLARSHIP_OBTAIN_ITEM, Gui.SCHOLARSHIP_NOT_MET_OBTAIN, (ResearchItemMatcher) hook.triggerTarget()));

        ScholarshipTriggerPresenterRegistry.register(TriggerTypeRegistry.ITEM_USED.id(), (hook, context) ->
                itemRow(Gui.SCHOLARSHIP_USE_ITEM, Gui.SCHOLARSHIP_NOT_USED, (ResearchItemMatcher) hook.triggerTarget()));

        ScholarshipTriggerPresenterRegistry.register(TriggerTypeRegistry.ENTRY_VIEWED_ONCE.id(), DefaultScholarshipTriggerPresenters::entryRow);

        ScholarshipTriggerPresenterRegistry.register(TriggerTypeRegistry.ADVANCEMENT.id(), DefaultScholarshipTriggerPresenters::advancementRow);
    }

    private static ScholarshipRow itemRow(String titleKey, String missingKey, ResearchItemMatcher matcher) {
        return ScholarshipRow.of(List.of(ScholarshipIcon.of(matcher.displayStack())), Component.translatable(titleKey, matcher.displayName()))
                .withMissing(Component.translatable(missingKey));
    }

    /**
     * Shows the icon and name of the entry, clicking it opens the entry.
     */
    private static ScholarshipRow entryRow(ResearchHookDefinition<?> hook, ScholarshipContext context) {
        var entryId = (Identifier) hook.triggerTarget();
        for (var book : BookDataManager.get().getBooks().values()) {
            var entry = book.getEntry(entryId);
            if (entry == null) {
                continue;
            }

            var bookId = book.getId();
            var icons = entry.getIcon() != null ? List.of(ScholarshipIcon.of(entry.getIcon())) : List.<ScholarshipIcon>of();
            return ScholarshipRow.of(icons, Component.translatable(Gui.SCHOLARSHIP_READ_ENTRY, Component.translatable(entry.getName())))
                    .withAction(new ScholarshipRowAction.OpenEntry(bookId, entryId));
        }
        return ScholarshipRow.of(List.of(ScholarshipIcons.icon(ScholarshipIcons.TRIGGER_ENTRY_VIEWED)), Component.literal(entryId.toString()));
    }

    /**
     * Shows the icon and title of the advancement if the client knows it.
     */
    private static ScholarshipRow advancementRow(ResearchHookDefinition<?> hook, ScholarshipContext context) {
        var advancementId = (Identifier) hook.triggerTarget();
        var connection = Minecraft.getInstance().getConnection();
        var holder = connection != null ? connection.getAdvancements().get(advancementId) : null;
        Optional<DisplayInfo> display = holder != null ? holder.value().display() : Optional.empty();
        if (display.isPresent()) {
            return ScholarshipRow.of(List.of(ScholarshipIcon.of(display.get().getIcon().create())), display.get().getTitle());
        }
        return ScholarshipRow.of(List.of(ScholarshipIcons.icon(ScholarshipIcons.TRIGGER_ADVANCEMENT)), Component.literal(advancementId.toString()));
    }
}
