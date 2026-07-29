/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.*;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ConditionHelper {
    private final BookContextHelper context;
    private final GeneratedBookResearchStore store;

    /**
     * Creates a condition helper with no book context. Cannot be used with {@link #entryViewedOnce(BookEntryModel)}.
     */
    public ConditionHelper() {
        this(null, null);
    }

    /**
     * Creates a condition helper bound to a book generation session.
     * Enables {@link #entryViewedOnce(BookEntryModel)} which writes generated research requests.
     */
    public ConditionHelper(BookContextHelper context, GeneratedBookResearchStore store) {
        this.context = context;
        this.store = store;
    }

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
     * {@link com.klikli_dev.modonomicon.api.datagen.research.ResearchIngressHelper#onEntryViewedOnce(Identifier)}).
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
     * {@link com.klikli_dev.modonomicon.api.datagen.research.ResearchIngressHelper#onEntryViewedOnce(Identifier)}).
     */
    public BookResearchNodeUnlockedConditionModel researchNodeEntryViewedOnce(ResearchNodeRef nodeRef, BookEntryModel entry) {
        return this.researchNodeEntryViewedOnce(nodeRef.id(), entry);
    }

    /**
     * Creates a condition that requires the given entry to be viewed before the target entry can be unlocked.
     * The backing research node, fact, and hook are generated automatically during book compilation.
     * <p>
     * Uses the default tooltip "Requires entry &lt;name&gt; to be unlocked".
     *
     * @param requiredEntry the entry that must be viewed first
     */
    public BookResearchNodeUnlockedConditionModel entryViewedOnce(BookEntryModel requiredEntry) {
        return this.entryViewedOnce(requiredEntry, Component.translatable(Tooltips.CONDITION_ENTRY_UNLOCKED,
                Component.translatable(requiredEntry.getName())));
    }

    /**
     * Creates a condition that requires the given entry to be viewed before the target entry can be unlocked.
     * The backing research node, fact, and hook are generated automatically during book compilation.
     *
     * @param requiredEntry the entry that must be viewed first
     * @param tooltip       a custom tooltip for this condition
     */
    public BookResearchNodeUnlockedConditionModel entryViewedOnce(BookEntryModel requiredEntry, Component tooltip) {
        if (this.store == null || this.context == null) {
            throw new IllegalStateException("entryViewedOnce() requires a book-bound ConditionHelper. "
                    + "Ensure the book subprovider creates a ConditionHelper with book context.");
        }
        var bookId = Identifier.fromNamespaceAndPath(this.context.modId(), this.context.bookId());
        this.store.addEntryViewedOnce(requiredEntry.getId());
        return BookResearchNodeUnlockedConditionModel.create()
                .withNode(this.store.nodeId(requiredEntry.getId()))
                .withTooltip(tooltip);
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
