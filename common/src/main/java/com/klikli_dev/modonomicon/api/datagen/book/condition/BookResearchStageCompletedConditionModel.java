/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookResearchStageCompletedCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookResearchStageCompletedConditionModel extends BookConditionModel<BookResearchStageCompletedConditionModel> {
    protected Identifier nodeId;
    protected Identifier stageId;

    protected BookResearchStageCompletedConditionModel() {
        super(BookResearchStageCompletedCondition.ID);
    }

    public static BookResearchStageCompletedConditionModel create() {
        return new BookResearchStageCompletedConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookResearchStageCompletedCondition(this.tooltipComponent(), this.nodeId, this.stageId);
    }

    public BookResearchStageCompletedConditionModel withNode(Identifier nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public BookResearchStageCompletedConditionModel withStage(Identifier stageId) {
        this.stageId = stageId;
        return this;
    }
}
