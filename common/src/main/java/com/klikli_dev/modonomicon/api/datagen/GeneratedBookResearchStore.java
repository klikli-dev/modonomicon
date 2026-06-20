/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import net.minecraft.resources.Identifier;

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

    public boolean isEmpty() {
        return this.entryViewedOnceRequests.isEmpty();
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

    public record EntryViewedOnceRequest(Identifier requiredEntryId) {}
}
