/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.*;
import net.minecraft.resources.Identifier;

public class ConditionHelper {
    public BookAdvancementConditionModel advancement(Identifier advancementId) {
        return BookAdvancementConditionModel.create().withAdvancementId(advancementId);
    }

    public BookAdvancementConditionModel advancementBuilder(Identifier advancementId) {
        return BookAdvancementConditionModel.create().withAdvancementId(advancementId);
    }

    public BookEntryReadConditionModel entryRead(Identifier entryId) {
        return BookEntryReadConditionModel.create().withEntry(entryId);
    }

    public BookEntryReadConditionModel entryReadBuilder(Identifier entryId) {
        return BookEntryReadConditionModel.create().withEntry(entryId);
    }

    public BookEntryReadConditionModel entryRead(BookEntryModel entry) {
        return BookEntryReadConditionModel.create().withEntry(entry.getId());
    }

    public BookEntryReadConditionModel entryReadBuilder(BookEntryModel entry) {
        return BookEntryReadConditionModel.create().withEntry(entry.getId());
    }

    public BookCategoryHasVisibleEntriesConditionModel categoryHasEntries(BookEntryModel entry) {
        return BookCategoryHasVisibleEntriesConditionModel.create().withCategory(entry.getId());
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlocked(Identifier nodeId) {
        return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlockedBuilder(Identifier nodeId) {
        return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
    }

    public BookAndConditionModel and(BookConditionModel... children) {
        return BookAndConditionModel.create().withChildren(children);
    }

    public BookAndConditionModel andBuilder(BookConditionModel... children) {
        return BookAndConditionModel.create().withChildren(children);
    }

    public BookOrConditionModel or(BookConditionModel... children) {
        return BookOrConditionModel.create().withChildren(children);
    }

    public BookOrConditionModel orBuilder(BookConditionModel... children) {
        return BookOrConditionModel.create().withChildren(children);
    }
}
