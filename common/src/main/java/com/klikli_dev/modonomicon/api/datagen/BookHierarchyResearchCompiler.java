/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchBundle;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchDataBuilder;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchFactRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchIngressHelper;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BookHierarchyResearchCompiler {

    /**
     * Compiles generated research for a book from both parent hierarchy and explicit store requests.
     *
     * @param book  the book model
     * @param store the generated research store (may be null for legacy callers)
     */
    public Optional<CompiledBookResearch> compile(BookModel book, GeneratedBookResearchStore store) {
        var research = new ResearchDataBuilder(book.getId().getNamespace());
        var ingress = new ResearchIngressHelper(research);
        var parentFacts = new HashMap<Identifier, ResearchFactRef>();
        var generatedEntries = new ArrayList<BookEntryModel>();
        var generatedNodes = new HashMap<Identifier, ResearchNodeRef>();
        var generatedFacts = new HashMap<Identifier, ResearchFactRef>();

        // --- Process explicit entry-viewed-once requests from the store ---
        if (store != null) {
            for (var request : store.getEntryViewedOnceRequests()) {
                var requiredId = request.requiredEntryId();
                var fact = generatedFacts.computeIfAbsent(requiredId,
                        id -> ingress.onEntryViewedOnce(id).declareFact(store.factPath(id)));
                var node = generatedNodes.computeIfAbsent(requiredId,
                        id -> research.node(store.nodePath(id), fact));
            }
        }

        // --- Process parent hierarchy (existing behavior) ---
        if (book.generateEntryHierarchyResearch()) {
            for (var category : book.getCategories()) {
                for (var entry : category.getEntries()) {
                    if (entry.hasCondition() || entry.getParents().isEmpty()) continue;
                    var requiredFacts = new ArrayList<ResearchFactRef>();
                    for (var parent : entry.getParents()) {
                        var parentId = parent.getEntryId();
                        var fact = parentFacts.computeIfAbsent(parentId, id -> ingress.onEntryViewedOnce(id).declareFact(hierarchyFactPath(book, id)));
                        requiredFacts.add(fact);
                    }
                    ResearchNodeRef node = research.node(hierarchyNodePath(book, entry), requiredFacts.toArray(ResearchFactRef[]::new));
                    entry.withCondition(
                            BookResearchNodeUnlockedConditionModel.create()
                                    .withNode(node.id())
                                    .withTooltip(Component.translatable(Tooltips.CONDITION_ENTRY_UNLOCKED,
                                            Component.translatable(entry.getName())))
                    );
                    generatedEntries.add(entry);
                }
            }
        }

        if (generatedEntries.isEmpty() && (store == null || store.isEmpty())) return Optional.empty();
        return Optional.of(new CompiledBookResearch(
                Identifier.fromNamespaceAndPath(book.getId().getNamespace(), bundleId(book)),
                research
        ));
    }

    /**
     * Backward-compatible overload: compiles only parent hierarchy research.
     */
    public Optional<CompiledBookResearch> compile(BookModel book) {
        return this.compile(book, null);
    }

    private String bundleId(BookModel book) { return "generated/" + book.getId().getPath(); }
    private String hierarchyFactPath(BookModel book, Identifier entryId) { return bundleId(book) + "/facts/entry_viewed_once/" + entryId.getPath().replace('/', '_'); }
    private String hierarchyNodePath(BookModel book, BookEntryModel entry) { return bundleId(book) + "/nodes/entry_viewed_once/" + entry.getId().getPath().replace('/', '_'); }

    public record CompiledBookResearch(Identifier bundleId, ResearchDataBuilder research) {}
}
