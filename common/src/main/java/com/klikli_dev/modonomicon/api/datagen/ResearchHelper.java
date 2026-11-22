/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.api.datagen.GeneratedBookResearchStore.EntryResearchRequest;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

/**
 * Declares the generated research behind book conditions and entries: the item and entry facts of markers and
 * requirements, and the staged research of entries. Requires a book-bound helper, see {@link BookContextHelper}.
 */
public class ResearchHelper {
    private final BookContextHelper context;
    private final GeneratedBookResearchStore store;

    public ResearchHelper() {
        this(null, null);
    }

    public ResearchHelper(BookContextHelper context, GeneratedBookResearchStore store) {
        this.context = context;
        this.store = store;
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
     * Creates a marker condition that shows an attention dot until the given entry has been viewed.
     * The backing research node, fact, and hook are generated automatically during book compilation.
     * Markers never show tooltips.
     *
     * @param requiredEntry the entry that must be viewed to clear the marker
     */
    public BookResearchNodeUnlockedConditionModel markerUntilEntryViewed(BookEntryModel requiredEntry) {
        this.requireStore("markerUntilEntryViewed()");
        this.store.addEntryViewedOnce(requiredEntry.getId());
        return BookResearchNodeUnlockedConditionModel.create()
                .withNode(this.store.nodeId(requiredEntry.getId()));
    }

    /**
     * Creates a marker condition that shows an attention dot until the given item has been crafted.
     * The backing research node, fact, and hook are generated automatically during book compilation.
     * Markers never show tooltips.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(ItemStackTemplate target) {
        return this.markerUntilCrafted(ResearchItemMatcher.of(target));
    }

    /**
     * Creates a marker condition that shows an attention dot until the given item has been crafted.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(Item target) {
        return this.markerUntilCrafted(new ItemStackTemplate(target));
    }

    /**
     * Creates a marker condition that shows an attention dot until the given item has been crafted.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(ItemLike target) {
        return this.markerUntilCrafted(new ItemStackTemplate(target.asItem()));
    }

    /**
     * Creates a marker condition that shows an attention dot until any item in the given tag has been crafted.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(TagKey<Item> tag) {
        return this.markerUntilCrafted(ResearchItemMatcher.tag(tag));
    }

    /**
     * Creates a marker condition that shows an attention dot until the item matched by the given matcher has been crafted.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(ResearchItemMatcher target) {
        this.requireStore("markerUntilCrafted()");
        this.store.addItemCrafted(target);
        return BookResearchNodeUnlockedConditionModel.create()
                .withNode(this.store.craftedNodeId(target));
    }

    /**
     * Creates a marker condition that shows an attention dot until the given item has been acquired.
     * The backing research node, fact, and hook are generated automatically during book compilation.
     * Markers never show tooltips.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(ItemStackTemplate target) {
        return this.markerUntilAcquired(ResearchItemMatcher.of(target));
    }

    /**
     * Creates a marker condition that shows an attention dot until the given item has been acquired.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(Item target) {
        return this.markerUntilAcquired(new ItemStackTemplate(target));
    }

    /**
     * Creates a marker condition that shows an attention dot until the given item has been acquired.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(ItemLike target) {
        return this.markerUntilAcquired(new ItemStackTemplate(target.asItem()));
    }

    /**
     * Creates a marker condition that shows an attention dot until any item in the given tag has been acquired.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(TagKey<Item> tag) {
        return this.markerUntilAcquired(ResearchItemMatcher.tag(tag));
    }

    /**
     * Creates a marker condition that shows an attention dot until the item matched by the given matcher has been acquired.
     */
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(ResearchItemMatcher target) {
        this.requireStore("markerUntilAcquired()");
        this.store.addItemAcquired(target);
        return BookResearchNodeUnlockedConditionModel.create()
                .withNode(this.store.acquiredNodeId(target));
    }

    /**
     * Creates a condition that requires the given item to be crafted.
     * The backing research node, fact, and hook are generated automatically during book compilation.
     * The requirement is shown in requirement blocks as a craft row with the item's icon.
     */
    public BookResearchNodeUnlockedConditionModel crafted(ItemStackTemplate target) {
        return this.crafted(ResearchItemMatcher.of(target));
    }

    /**
     * Creates a condition that requires the given item to be crafted.
     */
    public BookResearchNodeUnlockedConditionModel crafted(ItemLike target) {
        return this.crafted(new ItemStackTemplate(target.asItem()));
    }

    /**
     * Creates a condition that requires any item of the given tag to be crafted.
     */
    public BookResearchNodeUnlockedConditionModel crafted(TagKey<Item> tag) {
        return this.crafted(ResearchItemMatcher.tag(tag));
    }

    /**
     * Creates a condition that requires an item matching the matcher to be crafted.
     */
    public BookResearchNodeUnlockedConditionModel crafted(ResearchItemMatcher target) {
        this.requireStore("crafted()");
        this.store.addItemCrafted(target);
        return BookResearchNodeUnlockedConditionModel.create()
                .withNode(this.store.craftedNodeId(target))
                .withTooltip(Component.translatable(Tooltips.CONDITION_ITEM_CRAFTED, target.displayName()));
    }

    /**
     * Creates a condition that requires the given item to be acquired.
     * The backing research node, fact, and hook are generated automatically during book compilation.
     * The requirement is shown in requirement blocks as an obtain row with the item's icon.
     */
    public BookResearchNodeUnlockedConditionModel acquired(ItemStackTemplate target) {
        return this.acquired(ResearchItemMatcher.of(target));
    }

    /**
     * Creates a condition that requires the given item to be acquired.
     */
    public BookResearchNodeUnlockedConditionModel acquired(ItemLike target) {
        return this.acquired(new ItemStackTemplate(target.asItem()));
    }

    /**
     * Creates a condition that requires any item of the given tag to be acquired.
     */
    public BookResearchNodeUnlockedConditionModel acquired(TagKey<Item> tag) {
        return this.acquired(ResearchItemMatcher.tag(tag));
    }

    /**
     * Creates a condition that requires an item matching the matcher to be acquired.
     */
    public BookResearchNodeUnlockedConditionModel acquired(ResearchItemMatcher target) {
        this.requireStore("acquired()");
        this.store.addItemAcquired(target);
        return BookResearchNodeUnlockedConditionModel.create()
                .withNode(this.store.acquiredNodeId(target))
                .withTooltip(Component.translatable(Tooltips.CONDITION_ITEM_ACQUIRED, target.displayName()));
    }

    private void requireStore(String method) {
        if (this.store == null || this.context == null) {
            throw new IllegalStateException(method + " requires a book-bound ConditionHelper. "
                    + "Ensure the book subprovider creates a ConditionHelper with book context.");
        }
    }

    /**
     * Returns the generated research node of the staged research of the given entry.
     * Requires a book-bound ConditionHelper.
     */
    public ResearchNodeRef entryResearchNode(BookEntryModel entry) {
        this.requireStore("entryResearchNode()");
        return ResearchNodeRef.of(this.store.researchNodeId(entry.getId()));
    }

    /**
     * Returns the generated stage with the given name of the staged research of the given entry.
     * Requires a book-bound ConditionHelper.
     */
    public ResearchStageRef entryResearchStage(BookEntryModel entry, String stageName) {
        this.requireStore("entryResearchStage()");
        return this.store.researchStageRef(entry.getId(), stageName);
    }

    /**
     * Registers the staged research of an entry, so its research is generated with the book.
     * Requires a book-bound ConditionHelper.
     */
    public void addEntryResearch(EntryResearchRequest request) {
        this.requireStore("addEntryResearch()");
        this.store.addEntryResearch(request);
    }
}
