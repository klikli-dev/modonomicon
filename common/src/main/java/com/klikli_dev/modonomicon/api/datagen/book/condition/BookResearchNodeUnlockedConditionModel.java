/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookResearchNodeUnlockedCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookResearchNodeUnlockedConditionModel extends BookConditionModel<BookResearchNodeUnlockedConditionModel> {
    protected Identifier nodeId;

    protected BookResearchNodeUnlockedConditionModel() {
        super(BookResearchNodeUnlockedCondition.ID);
    }

    public static BookResearchNodeUnlockedConditionModel create() {
        return new BookResearchNodeUnlockedConditionModel();
    }

    @Override
    public BookCondition toBookCondition(HolderLookup.Provider provider) {
        return new BookResearchNodeUnlockedCondition(this.tooltipComponent(), this.nodeId);
    }

    public BookResearchNodeUnlockedConditionModel withNode(Identifier nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public BookResearchNodeUnlockedConditionModel withNode(String nodeId) {
        this.nodeId = Identifier.parse(nodeId);
        return this;
    }
}
