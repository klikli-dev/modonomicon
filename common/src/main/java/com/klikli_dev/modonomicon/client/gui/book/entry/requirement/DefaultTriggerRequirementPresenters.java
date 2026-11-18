/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

/**
 * The presenters of the built-in research trigger types.
 */
final class DefaultTriggerRequirementPresenters {

    private DefaultTriggerRequirementPresenters() {
    }

    static void register() {
        TriggerRequirementPresenterRegistry.register(TriggerTypeRegistry.ITEM_CRAFTED.id(), (hook, context) ->
                itemRow(Gui.REQUIREMENTS_CRAFT_ITEM, Gui.REQUIREMENTS_NOT_MET_CRAFT, (ItemStackTemplate) hook.triggerTarget()));

        TriggerRequirementPresenterRegistry.register(TriggerTypeRegistry.ITEM_ACQUIRED.id(), (hook, context) ->
                itemRow(Gui.REQUIREMENTS_OBTAIN_ITEM, Gui.REQUIREMENTS_NOT_MET_OBTAIN, (ItemStackTemplate) hook.triggerTarget()));

        TriggerRequirementPresenterRegistry.register(TriggerTypeRegistry.ITEM_USED.id(), (hook, context) ->
                itemRow(Gui.REQUIREMENTS_USE_ITEM, Gui.REQUIREMENTS_NOT_USED, (ItemStackTemplate) hook.triggerTarget()));

        TriggerRequirementPresenterRegistry.register(TriggerTypeRegistry.ENTRY_VIEWED_ONCE.id(), DefaultTriggerRequirementPresenters::entryRow);

        TriggerRequirementPresenterRegistry.register(TriggerTypeRegistry.ADVANCEMENT.id(), DefaultTriggerRequirementPresenters::advancementRow);
    }

    private static RequirementRow itemRow(String titleKey, String missingKey, ItemStackTemplate template) {
        var stack = template.create();
        return RequirementRow.of(List.of(RequirementIcon.of(stack)), Component.translatable(titleKey, stack.getHoverName()))
                .withMissing(Component.translatable(missingKey));
    }

    /**
     * Shows the icon and name of the entry, clicking it opens the entry.
     */
    private static RequirementRow entryRow(ResearchHookDefinition<?> hook, RequirementContext context) {
        var entryId = (Identifier) hook.triggerTarget();
        for (var book : BookDataManager.get().getBooks().values()) {
            var entry = book.getEntry(entryId);
            if (entry == null) {
                continue;
            }

            var bookId = book.getId();
            var icons = entry.getIcon() != null ? List.of(RequirementIcon.of(entry.getIcon())) : List.<RequirementIcon>of();
            return RequirementRow.of(icons, Component.translatable(Gui.REQUIREMENTS_READ_ENTRY, Component.translatable(entry.getName())))
                    .withOnClick(() -> context.openEntry().accept(bookId, entryId));
        }
        return null;
    }

    /**
     * Shows the icon and title of the advancement if the client knows it.
     */
    private static RequirementRow advancementRow(ResearchHookDefinition<?> hook, RequirementContext context) {
        var advancementId = (Identifier) hook.triggerTarget();
        var connection = Minecraft.getInstance().getConnection();
        var holder = connection != null ? connection.getAdvancements().get(advancementId) : null;
        Optional<DisplayInfo> display = holder != null ? holder.value().display() : Optional.empty();
        if (display.isPresent()) {
            return RequirementRow.of(List.of(RequirementIcon.of(display.get().getIcon().create())), display.get().getTitle());
        }
        return RequirementRow.of(List.of(RequirementIcon.of(new ItemStack(Items.BOOK))), Component.literal(advancementId.toString()));
    }
}
