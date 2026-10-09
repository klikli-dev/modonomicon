/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
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
        var generatedItemFacts = new HashMap<String, ResearchFactRef>();
        var generatedItemNodes = new HashMap<String, ResearchNodeRef>();

        //Use a local store when no book-bound store was provided so model-based marker intents still resolve.
        var effectiveStore = store != null
                ? store
                : new GeneratedBookResearchStore(book.getId().getNamespace(), book.getId().getPath());

        // --- Resolve model-based marker intents (markerUntil*) into store requests ---
        var pendingMarkerConditions = new HashMap<BookEntryModel, List<BookResearchNodeUnlockedConditionModel>>();
        for (var category : book.getCategories()) {
            for (var entry : category.getEntries()) {
                if (!entry.hasPendingMarkerRequests()) continue;
                var generated = new ArrayList<BookResearchNodeUnlockedConditionModel>();
                for (var requiredId : entry.getMarkerUntilEntries()) {
                    effectiveStore.addEntryViewedOnce(requiredId);
                    generated.add(BookResearchNodeUnlockedConditionModel.create().withNode(effectiveStore.nodeId(requiredId)));
                }
                for (var target : entry.getMarkerUntilCraftedTargets()) {
                    effectiveStore.addItemCrafted(target);
                    generated.add(BookResearchNodeUnlockedConditionModel.create().withNode(effectiveStore.craftedNodeId(target)));
                }
                for (var target : entry.getMarkerUntilAcquiredTargets()) {
                    effectiveStore.addItemAcquired(target);
                    generated.add(BookResearchNodeUnlockedConditionModel.create().withNode(effectiveStore.acquiredNodeId(target)));
                }
                if (!generated.isEmpty()) {
                    pendingMarkerConditions.put(entry, generated);
                }
            }
        }

        // --- Process explicit entry-viewed-once requests from the store ---
        for (var request : effectiveStore.getEntryViewedOnceRequests()) {
            var requiredId = request.requiredEntryId();
            var fact = generatedFacts.computeIfAbsent(requiredId,
                    id -> ingress.onEntryViewedOnce(id).declareFact(effectiveStore.factPath(id)));
            var node = generatedNodes.computeIfAbsent(requiredId,
                    id -> research.node(effectiveStore.nodePath(id), fact));
        }

        // --- Process item-crafted requests from the store ---
        for (var request : effectiveStore.getItemCraftedRequests()) {
            var target = request.target();
            var fact = generatedItemFacts.computeIfAbsent(effectiveStore.craftedFactPath(target),
                    path -> ingress.onItemCrafted(target).declareFact(path));
            generatedItemNodes.computeIfAbsent(effectiveStore.craftedNodePath(target),
                    path -> research.node(path, fact));
        }

        // --- Process item-acquired requests from the store ---
        for (var request : effectiveStore.getItemAcquiredRequests()) {
            var target = request.target();
            var fact = generatedItemFacts.computeIfAbsent(effectiveStore.acquiredFactPath(target),
                    path -> ingress.onItemAcquired(target).declareFact(path));
            generatedItemNodes.computeIfAbsent(effectiveStore.acquiredNodePath(target),
                    path -> research.node(path, fact));
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

        // --- Apply resolved marker conditions (ANDed with explicit marker conditions when both are present) ---
        for (var resolved : pendingMarkerConditions.entrySet()) {
            var entry = resolved.getKey();
            var all = new ArrayList<BookConditionModel<?>>();
            if (entry.hasMarkerCondition()) {
                all.add(entry.getMarkerCondition());
            }
            all.addAll(resolved.getValue());
            if (all.size() == 1) {
                entry.withMarkerCondition(all.get(0));
            } else {
                entry.withMarkerCondition(BookAndConditionModel.create().withChildren(all.toArray(BookConditionModel[]::new)));
            }
        }

        if (generatedEntries.isEmpty() && effectiveStore.isEmpty()) return Optional.empty();
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
