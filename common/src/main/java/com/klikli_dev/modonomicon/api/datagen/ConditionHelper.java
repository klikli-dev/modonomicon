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
    /**
     * Creates a condition that checks whether the given entry's category has visible entries.
     */
    public BookCategoryHasVisibleEntriesConditionModel categoryHasEntries(BookEntryModel entry) {
        return BookCategoryHasVisibleEntriesConditionModel.create().withCategory(entry.getId());
    }

    /**
     * Creates a condition that checks whether the given research node is unlocked.
     * <p>
     * The default tooltip reads "Requires research node &lt;name&gt; to be unlocked".
     * Use {@link #researchNodeEntryViewedOnce(Identifier, BookEntryModel)} if you want
     * the tooltip to read "Requires entry &lt;name&gt; to be unlocked" instead.
     */
    public BookResearchNodeUnlockedConditionModel researchNodeUnlocked(Identifier nodeId) {
        return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId);
    }

    /**
     * Creates a condition that checks whether the given research node is unlocked.
     * <p>
     * The default tooltip reads "Requires research node &lt;name&gt; to be unlocked".
     * Use {@link #researchNodeEntryViewedOnce(ResearchNodeRef, BookEntryModel)} if you want
     * the tooltip to read "Requires entry &lt;name&gt; to be unlocked" instead.
     */
    public BookResearchNodeUnlockedConditionModel researchNodeUnlocked(ResearchNodeRef nodeRef) {
        return this.researchNodeUnlocked(nodeRef.id());
    }

    /**
     * Creates a condition that checks whether the given research node is unlocked, with a tooltip
     * that reads "Requires entry &lt;name&gt; to be unlocked" using the given entry's display name.
     * <p>
     * This only sets up the tooltip. The actual "entry viewed once" research fact and node must be
     * configured separately in your research subprovider (e.g. via
     * {@link com.klikli_dev.modonomicon.api.datagen.research.ResearchHookHelper#onEntryViewedOnce(Identifier)}).
     */
    public BookResearchNodeUnlockedConditionModel researchNodeEntryViewedOnce(Identifier nodeId, BookEntryModel entry) {
        return BookResearchNodeUnlockedConditionModel.create().withNode(nodeId).withEntryName(entry);
    }

    /**
     * Creates a condition that checks whether the given research node is unlocked, with a tooltip
     * that reads "Requires entry &lt;name&gt; to be unlocked" using the given entry's display name.
     * <p>
     * This only sets up the tooltip. The actual "entry viewed once" research fact and node must be
     * configured separately in your research subprovider (e.g. via
     * {@link com.klikli_dev.modonomicon.api.datagen.research.ResearchHookHelper#onEntryViewedOnce(Identifier)}).
     */
    public BookResearchNodeUnlockedConditionModel researchNodeEntryViewedOnce(ResearchNodeRef nodeRef, BookEntryModel entry) {
        return this.researchNodeEntryViewedOnce(nodeRef.id(), entry);
    }

    /**
     * Creates an AND condition that is satisfied when all child conditions are met.
     */
    public BookAndConditionModel and(BookConditionModel<?>... children) {
        return BookAndConditionModel.create().withChildren(children);
    }

    /**
     * Creates an OR condition that is satisfied when at least one child condition is met.
     */
    public BookOrConditionModel or(BookConditionModel<?>... children) {
        return BookOrConditionModel.create().withChildren(children);
    }

    /**
     * Creates a NOT condition that is satisfied when the child condition is not met.
     * <p>
     * Useful for "before / after" content swaps, e.g. a hint page visible only until
     * a research node unlocks, replaced by detail pages gated on the node itself.
     * The child's tooltip is not forwarded, as it would describe the non-negated
     * requirement; set an explicit tooltip if one is needed.
     */
    public BookNotConditionModel not(BookConditionModel<?> child) {
        return BookNotConditionModel.create().withChild(child);
    }

    /**
     * Creates a condition that checks whether a specific stage of the given research node has been completed.
     */
    public BookResearchStageCompletedConditionModel researchStageCompleted(Identifier nodeId, Identifier stageId) {
        return BookResearchStageCompletedConditionModel.create().withNode(nodeId).withStage(stageId);
    }

    /**
     * Creates a condition that checks whether a specific stage of the given research node has been completed.
     */
    public BookResearchStageCompletedConditionModel researchStageCompleted(ResearchNodeRef nodeRef, ResearchStageRef stageRef) {
        return this.researchStageCompleted(nodeRef.id(), stageRef.id());
    }
}
