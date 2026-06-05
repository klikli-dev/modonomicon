/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.*;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import net.minecraft.resources.Identifier;

public class ConditionHelper {
    public BookCategoryHasVisibleEntriesConditionModel categoryHasEntries(BookEntryModel entry) {
        return BookCategoryHasVisibleEntriesConditionModel.create().withCategory(entry.getId());
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlocked(Identifier nodeId) {
        return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlocked(ResearchNodeRef nodeRef) {
        return this.researchNodeUnlocked(nodeRef.id());
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlockedBuilder(Identifier nodeId) {
        return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
    }

    public BookResearchNodeUnlockedConditionModel researchNodeUnlockedBuilder(ResearchNodeRef nodeRef) {
        return this.researchNodeUnlockedBuilder(nodeRef.id());
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

    public BookResearchStageCompletedConditionModel researchStageCompleted(Identifier nodeId, Identifier stageId) {
        return BookResearchStageCompletedConditionModel.create().withNode(nodeId).withStage(stageId);
    }

    public BookResearchStageCompletedConditionModel researchStageCompleted(ResearchNodeRef nodeRef, ResearchStageRef stageRef) {
        return this.researchStageCompleted(nodeRef.id(), stageRef.id());
    }
}
