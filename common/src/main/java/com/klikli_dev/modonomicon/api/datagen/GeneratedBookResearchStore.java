/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A per-book sidecar store that collects generated research requests from book-authored conditions.
 * <p>
 * The {@link com.klikli_dev.modonomicon.api.datagen.ConditionHelper} writes into this store during
 * authoring, and the {@link BookHierarchyResearchCompiler} reads from it during compilation.
 */
public final class GeneratedBookResearchStore {
    private final String bookNamespace;
    private final String bookPath;
    private final Map<Identifier, EntryViewedOnceRequest> entryViewedOnceRequests = new LinkedHashMap<>();
    private final Map<String, ItemCraftedRequest> itemCraftedRequests = new LinkedHashMap<>();
    private final Map<String, ItemAcquiredRequest> itemAcquiredRequests = new LinkedHashMap<>();

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
        this.entryViewedOnceRequests.putIfAbsent(requiredEntryId,
                new EntryViewedOnceRequest(requiredEntryId));
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

    public Collection<ItemCraftedRequest> getItemCraftedRequests() {
        return this.itemCraftedRequests.values();
    }

    public Collection<ItemAcquiredRequest> getItemAcquiredRequests() {
        return this.itemAcquiredRequests.values();
    }

    public boolean isEmpty() {
        return this.entryViewedOnceRequests.isEmpty() && this.itemCraftedRequests.isEmpty() && this.itemAcquiredRequests.isEmpty();
    }

    /**
     * Returns the deterministic generated fact path for an entry-viewed-once request.
     */
    public String factPath(Identifier requiredEntryId) {
        return "generated/" + this.bookPath + "/facts/entry_viewed_once/" + requiredEntryId.getPath().replace('/', '_');
    }

    /**
     * Returns the deterministic generated node path for an entry-viewed-once request.
     */
    public String nodePath(Identifier requiredEntryId) {
        return "generated/" + this.bookPath + "/nodes/entry_viewed_once/required/" + requiredEntryId.getPath().replace('/', '_');
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
        return "generated/" + this.bookPath + "/facts/item_crafted/" + itemKey(target);
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
        return "generated/" + this.bookPath + "/nodes/item_crafted/" + itemKey(target);
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
        return "generated/" + this.bookPath + "/facts/item_acquired/" + itemKey(target);
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
        return "generated/" + this.bookPath + "/nodes/item_acquired/" + itemKey(target);
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
     * Stable dedup key and path fragment for an item template.
     * <p>
     * The item's namespace and path boundaries are preserved as real path segments
     * ({@code example:gear/iron} → {@code example/gear/iron}), so distinct item IDs
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
                template -> itemKey(template),
                tag -> "tag_" + tag.location().getNamespace() + "/" + tag.location().getPath()
        );
    }

    public record EntryViewedOnceRequest(Identifier requiredEntryId) {}

    public record ItemCraftedRequest(ResearchItemMatcher target) {}

    public record ItemAcquiredRequest(ResearchItemMatcher target) {}
}
