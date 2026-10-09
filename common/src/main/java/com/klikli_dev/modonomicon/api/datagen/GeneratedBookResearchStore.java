/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

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
     * Registers an item-crafted generation request.
     * Requests for equal templates are deduplicated.
     */
    public void addItemCrafted(ItemStackTemplate target) {
        this.itemCraftedRequests.putIfAbsent(itemKey(target), new ItemCraftedRequest(target));
    }

    /**
     * Registers an item-acquired generation request.
     * Requests for equal templates are deduplicated.
     */
    public void addItemAcquired(ItemStackTemplate target) {
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
        return "generated/" + this.bookPath + "/facts/item_crafted/" + itemKey(target);
    }

    /**
     * Returns the deterministic generated node path for an item-crafted request.
     */
    public String craftedNodePath(ItemStackTemplate target) {
        return "generated/" + this.bookPath + "/nodes/item_crafted/" + itemKey(target);
    }

    /**
     * Returns the deterministic generated node id for an item-crafted request.
     */
    public Identifier craftedNodeId(ItemStackTemplate target) {
        return Identifier.fromNamespaceAndPath(this.bookNamespace, this.craftedNodePath(target));
    }

    /**
     * Returns the deterministic generated fact path for an item-acquired request.
     */
    public String acquiredFactPath(ItemStackTemplate target) {
        return "generated/" + this.bookPath + "/facts/item_acquired/" + itemKey(target);
    }

    /**
     * Returns the deterministic generated node path for an item-acquired request.
     */
    public String acquiredNodePath(ItemStackTemplate target) {
        return "generated/" + this.bookPath + "/nodes/item_acquired/" + itemKey(target);
    }

    /**
     * Returns the deterministic generated node id for an item-acquired request.
     */
    public Identifier acquiredNodeId(ItemStackTemplate target) {
        return Identifier.fromNamespaceAndPath(this.bookNamespace, this.acquiredNodePath(target));
    }

    /**
     * Stable, filesystem-safe dedup key for an item template: item namespace + path,
     * suffixed with a hash of the component patch when components are present.
     * Path separators map to double underscores so that {@code example:gear/iron} and
     * {@code example:gear_iron} produce distinct keys.
     */
    public static String itemKey(ItemStackTemplate target) {
        var itemId = target.item().unwrapKey()
                .map(key -> key.identifier())
                .orElseThrow(() -> new IllegalArgumentException("ItemStackTemplate has no registry key, cannot generate research for it: " + target));
        var base = itemId.getNamespace() + "_" + itemId.getPath().replace("/", "__");
        var patch = target.components();
        if (patch == null || patch.isEmpty()) {
            return base;
        }
        return base + "_" + Integer.toHexString(patch.toString().hashCode());
    }

    public record EntryViewedOnceRequest(Identifier requiredEntryId) {}

    public record ItemCraftedRequest(ItemStackTemplate target) {}

    public record ItemAcquiredRequest(ItemStackTemplate target) {}
}
