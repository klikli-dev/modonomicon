/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.research.data.AdvancementResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchValueDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Low-level research authoring collector that compiles typed authoring input into canonical research
 * definition records.
 *
 * This class is intentionally close to the output model. Higher-level fluent syntax such as ingress
 * helpers should delegate into this builder rather than bypassing it.
 */
public final class ResearchDataBuilder {
    private final String namespace;
    private final List<ResearchFactSpec> facts = new ArrayList<>();
    private final List<ResearchValueSpec> values = new ArrayList<>();
    private final List<ResearchNodeSpec> nodes = new ArrayList<>();
    private final List<EntryViewedOnceHookSpec> entryViewedOnceHooks = new ArrayList<>();
    private final List<ItemCraftedHookSpec> itemCraftedHooks = new ArrayList<>();
    private final List<ItemAcquiredHookSpec> itemAcquiredHooks = new ArrayList<>();
    private final List<AdvancementHookSpec> advancementHooks = new ArrayList<>();

    /**
     * Creates a builder that will namespace authored relative paths under the given namespace.
     */
    public ResearchDataBuilder(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Declares one explicit research fact definition and returns a typed ref to it.
     *
     * Facts are primitive durable inputs in the research model. Hooks grant facts, and nodes query
     * those granted facts as part of their unlock requirements.
     */
    public ResearchFactRef fact(String path) {
        var ref = ResearchFactRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
        this.facts.add(ResearchFactSpec.of(ref));
        return ref;
    }

    /**
     * Declares one explicit research value definition and returns a typed ref to it.
     *
     * Values are numeric counters that hooks can increment and nodes can require reaching a threshold.
     */
    public ResearchValueRef value(String path) {
        var ref = ResearchValueRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
        this.values.add(ResearchValueSpec.of(ref));
        return ref;
    }

    /**
     * Declares one explicit research node definition and returns a typed ref to it.
     *
     * In the current implementation, the given fact refs become the node's required facts in the
     * generated canonical research resource.
     */
    public ResearchNodeRef node(String path, ResearchFactRef... requiredFacts) {
        var ref = ResearchNodeRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
        this.nodes.add(ResearchNodeSpec.factsOnly(ref, List.of(requiredFacts)));
        return ref;
    }

    /**
     * Declares one explicit research node definition for an already-known typed ref and returns that
     * same ref.
     *
     * This overload lets higher-level authoring code centralize node ids in a typed ref catalog
     * while still compiling to the same canonical node definition output.
     */
    public ResearchNodeRef node(ResearchNodeRef ref, ResearchFactRef... requiredFacts) {
        this.nodes.add(ResearchNodeSpec.factsOnly(ref, List.of(requiredFacts)));
        return ref;
    }

    /**
     * Declares a research node with both fact and value requirements.
     */
    public ResearchNodeRef node(ResearchNodeRef ref, List<ResearchFactRef> requiredFacts, List<ResearchNodeSpec.ValueRequirement> requiredValues) {
        this.nodes.add(new ResearchNodeSpec(ref, requiredFacts, requiredValues, List.of(), List.of(), Optional.empty()));
        return ref;
    }

    /**
     * Creates a typed stage ref for use in node stage declarations.
     * The stage itself is defined inline in the node spec.
     */
    public ResearchStageRef stageRef(String path) {
        return ResearchStageRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
    }

    /**
     * Declares a research node with stages and stage dependencies.
     */
    public ResearchNodeRef node(
            ResearchNodeRef ref,
            List<ResearchFactRef> requiredFacts,
            List<ResearchNodeSpec.ValueRequirement> requiredValues,
            List<ResearchStageSpec> stages,
            List<ResearchNodeSpec.StageDependencySpec> requiredStages
    ) {
        this.nodes.add(ResearchNodeSpec.of(ref, requiredFacts, requiredValues, stages, requiredStages, Optional.empty()));
        return ref;
    }

    /**
     * Adds toast display data to an existing fact.
     */
    public void toast(ResearchFactRef factRef, ResearchToastDefinition toast) {
        for (int i = 0; i < this.facts.size(); i++) {
            var spec = this.facts.get(i);
            if (spec.ref().id().equals(factRef.id())) {
                this.facts.set(i, spec.toast(toast));
                break;
            }
        }
    }

    /**
     * Adds toast display data to an existing value.
     */
    public void toast(ResearchValueRef valueRef, ResearchToastDefinition toast) {
        for (int i = 0; i < this.values.size(); i++) {
            var spec = this.values.get(i);
            if (spec.ref().id().equals(valueRef.id())) {
                this.values.set(i, spec.toast(toast));
                break;
            }
        }
    }

    /**
     * Adds toast display data to an existing node.
     */
    public void toast(ResearchNodeRef nodeRef, ResearchToastDefinition toast) {
        for (int i = 0; i < this.nodes.size(); i++) {
            var spec = this.nodes.get(i);
            if (spec.ref().id().equals(nodeRef.id())) {
                this.nodes.set(i, spec.toast(toast));
                break;
            }
        }
    }

    /**
     * Declares an explicit {@code entry_viewed_once} research ingress hook that grants a fact.
     *
     * The generated hook means: when the specified book entry is viewed once, grant the specified
     * research fact. That fact can then participate in research-node unlocking.
     */
    public void grantFactOnEntryViewedOnce(String path, Identifier entryId, ResearchFactRef factRef) {
        this.entryViewedOnceHooks.add(EntryViewedOnceHookSpec.grantFact(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                entryId,
                factRef
        ));
    }

    /**
     * Declares an explicit {@code entry_viewed_once} research ingress hook that increments a value.
     */
    public void incrementValueOnEntryViewedOnce(String path, Identifier entryId, ResearchValueRef valueRef, int increment) {
        this.entryViewedOnceHooks.add(EntryViewedOnceHookSpec.incrementValue(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                entryId,
                valueRef,
                increment
        ));
    }

    /**
     * Declares an explicit advancement-earned research ingress hook that grants a fact.
     *
     * The generated hook means: when the specified advancement is earned, grant the specified
     * research fact. That fact can then participate in research-node unlocking.
     */
    public void grantFactOnAdvancementEarned(String path, Identifier advancementId, ResearchFactRef factRef) {
        this.advancementHooks.add(AdvancementHookSpec.grantFact(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                advancementId,
                factRef
        ));
    }

    /**
     * Declares an explicit advancement-earned research ingress hook that increments a value.
     */
    public void incrementValueOnAdvancementEarned(String path, Identifier advancementId, ResearchValueRef valueRef, int increment) {
        this.advancementHooks.add(AdvancementHookSpec.incrementValue(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                advancementId,
                valueRef,
                increment
        ));
    }

    /**
     * Declares an explicit {@code item_crafted} research ingress hook that grants a fact.
     *
     * The generated hook means: when the specified item is crafted, grant the specified
     * research fact. That fact can then participate in research-node unlocking.
     */
    public void grantFactOnItemCrafted(String path, ItemStackTemplate targetItem, ResearchFactRef factRef) {
        this.itemCraftedHooks.add(ItemCraftedHookSpec.grantFact(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                targetItem,
                factRef
        ));
    }

    /**
     * Declares an explicit {@code item_crafted} research ingress hook that grants a fact with component matching.
     */
    public void grantFactOnItemCrafted(String path, ItemStackTemplate targetItem, ResearchFactRef factRef, boolean matchComponents) {
        this.itemCraftedHooks.add(ItemCraftedHookSpec.grantFact(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                targetItem,
                factRef,
                matchComponents
        ));
    }

    /**
     * Declares an explicit {@code item_crafted} research ingress hook that increments a value.
     */
    public void incrementValueOnItemCrafted(String path, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment) {
        this.itemCraftedHooks.add(ItemCraftedHookSpec.incrementValue(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                targetItem,
                valueRef,
                increment
        ));
    }

    /**
     * Declares an explicit {@code item_crafted} research ingress hook that increments a value with component matching.
     */
    public void incrementValueOnItemCrafted(String path, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment, boolean matchComponents) {
        this.itemCraftedHooks.add(ItemCraftedHookSpec.incrementValue(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                targetItem,
                valueRef,
                increment,
                matchComponents
        ));
    }

    /**
     * Declares an explicit {@code item_acquired} research ingress hook that grants a fact.
     *
     * The generated hook means: when the specified item is acquired, grant the specified
     * research fact. That fact can then participate in research-node unlocking.
     */
    public void grantFactOnItemAcquired(String path, ItemStackTemplate targetItem, ResearchFactRef factRef) {
        this.itemAcquiredHooks.add(ItemAcquiredHookSpec.grantFact(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                targetItem,
                factRef
        ));
    }

    /**
     * Declares an explicit {@code item_acquired} research ingress hook that grants a fact with component matching.
     */
    public void grantFactOnItemAcquired(String path, ItemStackTemplate targetItem, ResearchFactRef factRef, boolean matchComponents) {
        this.itemAcquiredHooks.add(ItemAcquiredHookSpec.grantFact(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                targetItem,
                factRef,
                matchComponents
        ));
    }

    /**
     * Declares an explicit {@code item_acquired} research ingress hook that increments a value.
     */
    public void incrementValueOnItemAcquired(String path, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment) {
        this.itemAcquiredHooks.add(ItemAcquiredHookSpec.incrementValue(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                targetItem,
                valueRef,
                increment
        ));
    }

    /**
     * Declares an explicit {@code item_acquired} research ingress hook that increments a value with component matching.
     */
    public void incrementValueOnItemAcquired(String path, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment, boolean matchComponents) {
        this.itemAcquiredHooks.add(ItemAcquiredHookSpec.incrementValue(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                targetItem,
                valueRef,
                increment,
                matchComponents
        ));
    }

    /**
     * Returns the compiled canonical fact definitions collected so far.
     */
    public List<ResearchFactDefinition> factDefinitions() {
        return this.facts.stream().map(ResearchFactSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical value definitions collected so far.
     */
    public List<ResearchValueDefinition> valueDefinitions() {
        return this.values.stream().map(ResearchValueSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical node definitions collected so far.
     */
    public List<ResearchNodeDefinition> nodeDefinitions() {
        return this.nodes.stream().map(ResearchNodeSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical entry-viewed research hook definitions collected so far.
     */
    public List<ResearchHookDefinition> hookDefinitions() {
        return this.entryViewedOnceHooks.stream().map(EntryViewedOnceHookSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical advancement research hook definitions collected so far.
     */
    public List<AdvancementResearchHookDefinition> advancementHookDefinitions() {
        return this.advancementHooks.stream().map(AdvancementHookSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical item-crafted research hook definitions collected so far.
     */
    public List<ResearchHookDefinition> itemCraftedHookDefinitions() {
        return this.itemCraftedHooks.stream().map(ItemCraftedHookSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical item-acquired research hook definitions collected so far.
     */
    public List<ResearchHookDefinition> itemAcquiredHookDefinitions() {
        return this.itemAcquiredHooks.stream().map(ItemAcquiredHookSpec::toDefinition).toList();
    }
}
