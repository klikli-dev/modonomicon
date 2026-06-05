/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book.condition;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookResearchNodeUnlockedCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

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

    /**
     * Sets the tooltip to reference the node's description id translation key.
     * <p>
     * This generates a translation key following the pattern
     * {@code block.<namespace>.<path>.description}, e.g.
     * {@code block.mymod.demo/crafting_stick.description}. Ensure a matching
     * translation exists, otherwise the raw key will be displayed.
     */
    public BookResearchNodeUnlockedConditionModel withNodeName() {
        this.tooltip = Component.translatable(Util.makeDescriptionId("research_node", this.nodeId));
        return this;
    }
}
