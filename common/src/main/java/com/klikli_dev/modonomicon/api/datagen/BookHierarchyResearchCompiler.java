/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.api.datagen.GeneratedBookResearchStore.ActionFact;
import com.klikli_dev.modonomicon.api.datagen.GeneratedBookResearchStore.StagedProgressionRequest;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookResearchNodeUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchDataBuilder;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchFactRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchIngressHelper;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeSpec;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageSpec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class BookHierarchyResearchCompiler {

    /**
     * Compiles generated research for a book from the explicit store requests, staged progressions and the parent hierarchy.
     *
     * @param book  the book model
     * @param store the generated research store (may be null, then only model-based marker intents are compiled)
     */
    public Optional<CompiledBookResearch> compile(BookModel book, GeneratedBookResearchStore store) {
        var research = new ResearchDataBuilder(book.getId().getNamespace());
        var ingress = new ResearchIngressHelper(research);
        var generatedEntries = new ArrayList<BookEntryModel>();
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
            research.node(effectiveStore.nodePath(requiredId), fact);
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

        // --- Process item-used requests from the store ---
        for (var request : effectiveStore.getItemUsedRequests()) {
            var target = request.target();
            var consume = request.consume();
            var fact = generatedItemFacts.computeIfAbsent(effectiveStore.usedFactPath(target, consume),
                    path -> ingress.onItemUsed(target).consume(consume).declareFact(path));
            generatedItemNodes.computeIfAbsent(effectiveStore.usedNodePath(target, consume),
                    path -> research.node(path, fact));
        }

        // --- Process staged entry progressions: one node whose stages are the stages of the entry ---
        for (var progression : effectiveStore.getStagedProgressions()) {
            var baseFact = generatedFacts.computeIfAbsent(progression.entryId(),
                    id -> ingress.onEntryViewedOnce(id).declareFact(effectiveStore.factPath(id)));
            var stages = new ArrayList<ResearchStageSpec>();
            for (var stage : progression.stages()) {
                var facts = new ArrayList<ResearchFactRef>(stage.facts());
                for (var target : stage.crafted()) {
                    facts.add(generatedItemFacts.computeIfAbsent(effectiveStore.craftedFactPath(target),
                            path -> ingress.onItemCrafted(target).declareFact(path)));
                }
                for (var target : stage.acquired()) {
                    facts.add(generatedItemFacts.computeIfAbsent(effectiveStore.acquiredFactPath(target),
                            path -> ingress.onItemAcquired(target).declareFact(path)));
                }
                for (var used : stage.used()) {
                    var target = used.target();
                    var consume = used.consume();
                    facts.add(generatedItemFacts.computeIfAbsent(effectiveStore.usedFactPath(target, consume),
                            path -> ingress.onItemUsed(target).consume(consume).declareFact(path)));
                }
                for (var viewedId : stage.viewed()) {
                    facts.add(generatedFacts.computeIfAbsent(viewedId,
                            id -> ingress.onEntryViewedOnce(id).declareFact(effectiveStore.factPath(id))));
                }
                for (ActionFact action : stage.actionFacts()) {
                    var actionRef = research.fact(stage.ref().id().getPath() + "/" + action.id());
                    research.display(actionRef, action.display());
                    facts.add(actionRef);
                }
                stages.add(new ResearchStageSpec(stage.ref(), facts, stage.values(), Optional.empty(),
                        Optional.ofNullable(stage.display()), stage.completion(), stage.rewards()));
            }
            research.nodeBuilder(ResearchNodeRef.of(effectiveStore.progressionNodeId(progression.entryId())))
                    .withFact(baseFact)
                    .withStages(stages)
                    .build();
        }

        // --- Process parent hierarchy ---
        if (book.generateEntryHierarchyResearch()) {
            var entriesById = new HashMap<Identifier, BookEntryModel>();
            for (var category : book.getCategories()) {
                for (var entry : category.getEntries()) {
                    entriesById.put(entry.getId(), entry);
                }
            }
            for (var category : book.getCategories()) {
                for (var entry : category.getEntries()) {
                    if (entry.hasCondition() || entry.getParents().isEmpty()) continue;
                    //parents with a progression unlock their children on completion, all others once viewed
                    var requiredFacts = new ArrayList<ResearchFactRef>();
                    var requiredStages = new ArrayList<ResearchNodeSpec.StageDependencySpec>();
                    for (var parent : entry.getParents()) {
                        var parentId = parent.getEntryId();
                        var parentModel = entriesById.get(parentId);
                        if (parentModel != null && parentModel.hasProgression()) {
                            requiredStages.add(progressionCompletion(parentModel));
                            continue;
                        }
                        var fact = generatedFacts.computeIfAbsent(parentId,
                                id -> ingress.onEntryViewedOnce(id).declareFact(GeneratedResearchPaths.entryViewedOnceFact(book.getId().getPath(), id)));
                        requiredFacts.add(fact);
                    }
                    var nodeRef = ResearchNodeRef.of(Identifier.fromNamespaceAndPath(book.getId().getNamespace(), hierarchyNodePath(book, entry)));
                    var node = research.nodeBuilder(nodeRef)
                            .withFacts(requiredFacts)
                            .withStageDependencies(requiredStages)
                            .build();
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

    private String bundleId(BookModel book) {
        return GeneratedResearchPaths.bundle(book.getId().getPath());
    }

    private String hierarchyNodePath(BookModel book, BookEntryModel entry) {
        return GeneratedResearchPaths.entryHierarchyNode(book.getId().getPath(), entry.getId());
    }

    private ResearchNodeSpec.StageDependencySpec progressionCompletion(BookEntryModel parent) {
        var stage = parent.getProgressionCompletionStage();
        if (stage == null) {
            throw new IllegalStateException("Entry '" + parent.getId() + "' has a progression, but no completion stage. "
                    + "Declare it with this.stage(...) in EntryProvider, or use BookEntryModel.withProgression(node, lastStage) for a hand-authored node.");
        }
        return new ResearchNodeSpec.StageDependencySpec(ResearchNodeRef.of(parent.getProgressionNode()), stage);
    }

    public record CompiledBookResearch(Identifier bundleId, ResearchDataBuilder research) {}
}
