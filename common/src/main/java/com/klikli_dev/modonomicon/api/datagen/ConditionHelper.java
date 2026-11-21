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
import com.klikli_dev.modonomicon.api.datagen.GeneratedBookResearchStore.StagedProgressionRequest;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

public class ConditionHelper {
    private final ResearchHelper research;

    public ConditionHelper() {
        this.research = new ResearchHelper();
    }

    /**
     * Creates a condition helper bound to a book generation session, so the deprecated research shims can write research.
     */
    public ConditionHelper(BookContextHelper context, GeneratedBookResearchStore store) {
        this.research = new ResearchHelper(context, store);
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

    /**
     * @deprecated Use {@link ResearchHelper#entryViewedOnce(BookEntryModel)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel entryViewedOnce(BookEntryModel requiredEntry) {
        return this.research.entryViewedOnce(requiredEntry);
    }

    /**
     * @deprecated Use {@link ResearchHelper#entryViewedOnce(BookEntryModel, Component)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel entryViewedOnce(BookEntryModel requiredEntry, Component tooltip) {
        return this.research.entryViewedOnce(requiredEntry, tooltip);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilEntryViewed(BookEntryModel)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilEntryViewed(BookEntryModel requiredEntry) {
        return this.research.markerUntilEntryViewed(requiredEntry);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilCrafted(ItemStackTemplate)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(ItemStackTemplate target) {
        return this.research.markerUntilCrafted(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilCrafted(Item)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(Item target) {
        return this.research.markerUntilCrafted(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilCrafted(ItemLike)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(ItemLike target) {
        return this.research.markerUntilCrafted(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilCrafted(TagKey)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(TagKey<Item> tag) {
        return this.research.markerUntilCrafted(tag);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilCrafted(ResearchItemMatcher)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilCrafted(ResearchItemMatcher target) {
        return this.research.markerUntilCrafted(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilAcquired(ItemStackTemplate)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(ItemStackTemplate target) {
        return this.research.markerUntilAcquired(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilAcquired(Item)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(Item target) {
        return this.research.markerUntilAcquired(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilAcquired(ItemLike)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(ItemLike target) {
        return this.research.markerUntilAcquired(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilAcquired(TagKey)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(TagKey<Item> tag) {
        return this.research.markerUntilAcquired(tag);
    }

    /**
     * @deprecated Use {@link ResearchHelper#markerUntilAcquired(ResearchItemMatcher)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel markerUntilAcquired(ResearchItemMatcher target) {
        return this.research.markerUntilAcquired(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#crafted(ItemStackTemplate)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel crafted(ItemStackTemplate target) {
        return this.research.crafted(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#crafted(ItemLike)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel crafted(ItemLike target) {
        return this.research.crafted(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#crafted(TagKey)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel crafted(TagKey<Item> tag) {
        return this.research.crafted(tag);
    }

    /**
     * @deprecated Use {@link ResearchHelper#crafted(ResearchItemMatcher)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel crafted(ResearchItemMatcher target) {
        return this.research.crafted(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#acquired(ItemStackTemplate)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel acquired(ItemStackTemplate target) {
        return this.research.acquired(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#acquired(ItemLike)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel acquired(ItemLike target) {
        return this.research.acquired(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#acquired(TagKey)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel acquired(TagKey<Item> tag) {
        return this.research.acquired(tag);
    }

    /**
     * @deprecated Use {@link ResearchHelper#acquired(ResearchItemMatcher)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public BookResearchNodeUnlockedConditionModel acquired(ResearchItemMatcher target) {
        return this.research.acquired(target);
    }

    /**
     * @deprecated Use {@link ResearchHelper#entryResearchNode(BookEntryModel)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ResearchNodeRef entryProgressionNode(BookEntryModel entry) {
        return this.research.entryResearchNode(entry);
    }

    /**
     * @deprecated Use {@link ResearchHelper#entryResearchStage(BookEntryModel, String)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ResearchStageRef entryProgressionStage(BookEntryModel entry, String stageName) {
        return this.research.entryResearchStage(entry, stageName);
    }

    /**
     * @deprecated Use {@link ResearchHelper#addEntryResearch(GeneratedBookResearchStore.EntryResearchRequest)} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public void addEntryProgression(StagedProgressionRequest request) {
        this.research.addEntryResearch(request.toEntryResearch());
    }}
