/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.ResearchStageBuilder.UsedItem;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchDisplayModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchFactRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeSpec;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchEffectModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import com.klikli_dev.modonomicon.research.data.ResearchStageCompletion;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A per-book sidecar store that collects generated research requests from book-authored conditions and researches.
 * <p>
 * The {@link ConditionHelper} and {@link EntryProvider} write into this store during authoring, and the
 * {@link BookHierarchyResearchCompiler} reads from it during compilation. Requests are deduplicated by their generated
 * path, so equal requirements share one fact and one node.
 */
public final class GeneratedBookResearchStore {
    private final String bookNamespace;
    private final String bookPath;
    private final Map<Identifier, EntryViewedOnceRequest> entryViewedOnceRequests = new LinkedHashMap<>();
    private final Map<String, ItemCraftedRequest> itemCraftedRequests = new LinkedHashMap<>();
    private final Map<String, ItemAcquiredRequest> itemAcquiredRequests = new LinkedHashMap<>();
    private final Map<String, ItemUsedRequest> itemUsedRequests = new LinkedHashMap<>();
    private final Map<Identifier, EntryResearchRequest> entryResearches = new LinkedHashMap<>();

    public GeneratedBookResearchStore(String bookNamespace, String bookPath) {
        this.bookNamespace = bookNamespace;
        this.bookPath = bookPath;
    }

    /**
     * Registers an entry-viewed-once generation request.
     * If the same required entry is registered multiple requests are deduplicated.
     *
     * @param requiredEntryId the full entry id that must be viewed (e.g. {@code modonomicon:features/condition_root})
     */
    public void addEntryViewedOnce(Identifier requiredEntryId) {
        this.entryViewedOnceRequests.putIfAbsent(requiredEntryId, new EntryViewedOnceRequest(requiredEntryId));
    }

    public Collection<EntryViewedOnceRequest> getEntryViewedOnceRequests() {
        return this.entryViewedOnceRequests.values();
    }

    /**
     * Registers an item-crafted generation request for an exact item template.
     * Requests for equal templates are deduplicated.
     */
    public void addItemCrafted(ItemStackTemplate target) {
        this.addItemCrafted(ResearchItemMatcher.of(target));
    }

    /**
     * Registers an item-crafted generation request for an item matcher (exact item or item tag).
     * Requests for equal matchers are deduplicated.
     */
    public void addItemCrafted(ResearchItemMatcher target) {
        this.itemCraftedRequests.putIfAbsent(itemKey(target), new ItemCraftedRequest(target));
    }

    /**
     * Registers an item-acquired generation request for an exact item template.
     * Requests for equal templates are deduplicated.
     */
    public void addItemAcquired(ItemStackTemplate target) {
        this.addItemAcquired(ResearchItemMatcher.of(target));
    }

    /**
     * Registers an item-acquired generation request for an item matcher (exact item or item tag).
     * Requests for equal matchers are deduplicated.
     */
    public void addItemAcquired(ResearchItemMatcher target) {
        this.itemAcquiredRequests.putIfAbsent(itemKey(target), new ItemAcquiredRequest(target));
    }

    /**
     * Registers an item-used generation request for an item matcher. Consumes {@code consume} items of the used stack
     * when it changes research state. Requests for equal matchers and consume counts are deduplicated.
     */
    public void addItemUsed(ResearchItemMatcher target, int consume) {
        this.itemUsedRequests.putIfAbsent(itemKey(target) + "/" + consume, new ItemUsedRequest(target, consume));
    }

    public Collection<ItemCraftedRequest> getItemCraftedRequests() {
        return this.itemCraftedRequests.values();
    }

    public Collection<ItemAcquiredRequest> getItemAcquiredRequests() {
        return this.itemAcquiredRequests.values();
    }

    public Collection<ItemUsedRequest> getItemUsedRequests() {
        return this.itemUsedRequests.values();
    }

    /**
     * Registers the staged research of an entry. Each craft, obtain, use and read requirement of its stages is
     * registered as a regular request, so it shares its generated fact with other entries and conditions.
     * Only one research per entry is allowed.
     *
     * @throws IllegalStateException if the entry already has a research
     */
    public void addEntryResearch(EntryResearchRequest request) {
        if (this.entryResearches.putIfAbsent(request.entryId(), request) != null) {
            throw new IllegalStateException("Entry '" + request.entryId() + "' already has a staged research.");
        }
        for (var stage : request.stages()) {
            stage.crafted().forEach(this::addItemCrafted);
            stage.acquired().forEach(this::addItemAcquired);
            stage.used().forEach(used -> this.addItemUsed(used.target(), used.consume()));
            stage.viewed().forEach(this::addEntryViewedOnce);
        }
    }

    public Collection<EntryResearchRequest> getEntryResearches() {
        return this.entryResearches.values();
    }

    public boolean isEmpty() {
        return this.entryViewedOnceRequests.isEmpty() && this.itemCraftedRequests.isEmpty() && this.itemAcquiredRequests.isEmpty()
                && this.itemUsedRequests.isEmpty() && this.entryResearches.isEmpty();
    }

    /**
     * Returns the deterministic generated node path of the research of an entry.
     */
    public String researchNodePath(Identifier entryId) {
        return GeneratedResearchPaths.entryResearchNode(this.bookPath, entryId);
    }

    /**
     * Returns the deterministic generated node id of the research of an entry.
     */
    public Identifier researchNodeId(Identifier entryId) {
        return Identifier.fromNamespaceAndPath(this.bookNamespace, this.researchNodePath(entryId));
    }

    /**
     * Returns the deterministic generated stage ref of a stage of the research of an entry.
     */
    public ResearchStageRef researchStageRef(Identifier entryId, String stageName) {
        return ResearchStageRef.of(Identifier.fromNamespaceAndPath(this.bookNamespace,
                GeneratedResearchPaths.entryResearchStage(this.bookPath, entryId, stageName)));
    }

    /**
     * Returns the deterministic generated fact path for an entry-viewed-once request.
     */
    public String factPath(Identifier requiredEntryId) {
        return GeneratedResearchPaths.entryViewedOnceFact(this.bookPath, requiredEntryId);
    }

    /**
     * Returns the deterministic generated node path for an entry-viewed-once request.
     */
    public String nodePath(Identifier requiredEntryId) {
        return GeneratedResearchPaths.entryRequiredNode(this.bookPath, requiredEntryId);
    }

    /**
     * Returns the deterministic generated node id for an entry-viewed-once request.
     */
    public Identifier nodeId(Identifier requiredEntryId) {
        return Identifier.fromNamespaceAndPath(this.bookNamespace, this.nodePath(requiredEntryId));
    }

    /**
     * Returns the deterministic generated fact path for an item-crafted request.
     */
    public String craftedFactPath(ItemStackTemplate target) {
        return this.craftedFactPath(ResearchItemMatcher.of(target));
    }

    /**
     * Returns the deterministic generated fact path for an item-crafted matcher.
     */
    public String craftedFactPath(ResearchItemMatcher target) {
        return GeneratedResearchPaths.itemFact(this.bookPath, GeneratedResearchPaths.ITEM_CRAFTED, itemKey(target));
    }

    /**
     * Returns the deterministic generated node path for an item-crafted request.
     */
    public String craftedNodePath(ItemStackTemplate target) {
        return this.craftedNodePath(ResearchItemMatcher.of(target));
    }

    /**
     * Returns the deterministic generated node path for an item-crafted matcher.
     */
    public String craftedNodePath(ResearchItemMatcher target) {
        return GeneratedResearchPaths.itemNode(this.bookPath, GeneratedResearchPaths.ITEM_CRAFTED, itemKey(target));
    }

    /**
     * Returns the deterministic generated node id for an item-crafted request.
     */
    public Identifier craftedNodeId(ItemStackTemplate target) {
        return this.craftedNodeId(ResearchItemMatcher.of(target));
    }

    /**
     * Returns the deterministic generated node id for an item-crafted matcher.
     */
    public Identifier craftedNodeId(ResearchItemMatcher target) {
        return Identifier.fromNamespaceAndPath(this.bookNamespace, this.craftedNodePath(target));
    }

    /**
     * Returns the deterministic generated fact path for an item-acquired request.
     */
    public String acquiredFactPath(ItemStackTemplate target) {
        return this.acquiredFactPath(ResearchItemMatcher.of(target));
    }

    /**
     * Returns the deterministic generated fact path for an item-acquired matcher.
     */
    public String acquiredFactPath(ResearchItemMatcher target) {
        return GeneratedResearchPaths.itemFact(this.bookPath, GeneratedResearchPaths.ITEM_ACQUIRED, itemKey(target));
    }

    /**
     * Returns the deterministic generated node path for an item-acquired request.
     */
    public String acquiredNodePath(ItemStackTemplate target) {
        return this.acquiredNodePath(ResearchItemMatcher.of(target));
    }

    /**
     * Returns the deterministic generated node path for an item-acquired matcher.
     */
    public String acquiredNodePath(ResearchItemMatcher target) {
        return GeneratedResearchPaths.itemNode(this.bookPath, GeneratedResearchPaths.ITEM_ACQUIRED, itemKey(target));
    }

    /**
     * Returns the deterministic generated node id for an item-acquired request.
     */
    public Identifier acquiredNodeId(ItemStackTemplate target) {
        return this.acquiredNodeId(ResearchItemMatcher.of(target));
    }

    /**
     * Returns the deterministic generated node id for an item-acquired matcher.
     */
    public Identifier acquiredNodeId(ResearchItemMatcher target) {
        return Identifier.fromNamespaceAndPath(this.bookNamespace, this.acquiredNodePath(target));
    }

    /**
     * Returns the deterministic generated fact path for an item-used request. Requests with different consume counts get distinct facts.
     */
    public String usedFactPath(ResearchItemMatcher target, int consume) {
        return GeneratedResearchPaths.itemUsedFact(this.bookPath, itemKey(target), consume);
    }

    /**
     * Returns the deterministic generated node path for an item-used request.
     */
    public String usedNodePath(ResearchItemMatcher target, int consume) {
        return GeneratedResearchPaths.itemUsedNode(this.bookPath, itemKey(target), consume);
    }

    /**
     * Stable dedup key and path fragment for an item template.
     * <p>
     * The item's namespace and path boundaries are preserved as real path segments
     * ({@code example:gear/iron} becomes {@code example/gear/iron}), so distinct item IDs
     * can never share a key. Templates with components get a hash suffix.
     */
    public static String itemKey(ItemStackTemplate target) {
        var itemId = target.item().unwrapKey()
                .map(key -> key.identifier())
                .orElseThrow(() -> new IllegalArgumentException("ItemStackTemplate has no registry key, cannot generate research for it: " + target));
        var base = itemId.getNamespace() + "/" + itemId.getPath();
        var patch = target.components();
        if (patch == null || patch.isEmpty()) {
            return base;
        }
        return base + "_" + Integer.toHexString(patch.toString().hashCode());
    }

    /**
     * Stable dedup key and path fragment for an item matcher.
     * <p>
     * Templates use {@link #itemKey(ItemStackTemplate)}. Tags use {@code tag_<namespace>/<path>}, which cannot
     * collide with item keys for namespaces that do not start with {@code tag_}.
     */
    public static String itemKey(ResearchItemMatcher target) {
        return target.value().map(
                GeneratedBookResearchStore::itemKey,
                tag -> "tag_" + tag.location().getNamespace() + "/" + tag.location().getPath()
        );
    }

    public record EntryViewedOnceRequest(Identifier requiredEntryId) {}

    public record ItemCraftedRequest(ResearchItemMatcher target) {}

    public record ItemAcquiredRequest(ResearchItemMatcher target) {}

    /**
     * An item-used generation request. Consumes {@code consume} items of the used stack when it changes research state.
     */
    public record ItemUsedRequest(ResearchItemMatcher target, int consume) {}

    /**
     * A hand-authored fact created by a research stage, satisfied with an action, see {@link ResearchStageBuilder#action}.
     *
     * @param id      the id of the fact within its stage
     * @param display the display of the fact, carrying its title and action
     */
    public record ActionFact(String id, ResearchDisplayModel display) {}

    /**
     * A staged research of an entry, compiled into one research node whose stages are the stages of the entry.
     *
     * @param entryId the full id of the entry with the research
     * @param stages  the stages in declaration order
     */
    public record EntryResearchRequest(Identifier entryId, List<StageRequest> stages) {}

    /**
     * One stage of a staged research, as collected by {@link ResearchStageBuilder}.
     *
     * @param ref        the generated stage ref
     * @param crafted    items that must be crafted
     * @param acquired   items that must be obtained
     * @param used       items that must be used, with the number of items each use consumes
     * @param viewed     entries that must be viewed
     * @param facts      hand-authored facts that must be granted
     * @param values     hand-authored values that must reach their threshold
     * @param actionFacts hand-authored facts that the player satisfies with an action, created for this stage
     * @param display    the display of the stage in requirement blocks
     * @param completion how the stage completes, manual with its cost or automatic
     * @param effects    effects granted once when the stage completes
     */
    public record StageRequest(
            ResearchStageRef ref,
            List<ResearchItemMatcher> crafted,
            List<ResearchItemMatcher> acquired,
            List<UsedItem> used,
            List<Identifier> viewed,
            List<ResearchFactRef> facts,
            List<ResearchNodeSpec.ValueRequirement> values,
            List<ActionFact> actionFacts,
            ResearchDisplayModel display,
            ResearchStageCompletion completion,
            List<ResearchEffectModel> effects
    ) {}
}
