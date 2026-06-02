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
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

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
    private final List<ResearchNodeSpec> nodes = new ArrayList<>();
    private final List<EntryViewedOnceHookSpec> entryViewedOnceHooks = new ArrayList<>();
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
        this.facts.add(new ResearchFactSpec(ref));
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
        this.nodes.add(new ResearchNodeSpec(ref, List.of(requiredFacts)));
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
        this.nodes.add(new ResearchNodeSpec(ref, List.of(requiredFacts)));
        return ref;
    }

    /**
     * Declares an explicit {@code entry_viewed_once} research ingress hook.
     *
     * The generated hook means: when the specified book entry is viewed once, grant the specified
     * research fact. That fact can then participate in research-node unlocking.
     */
    public void grantFactOnEntryViewedOnce(String path, Identifier entryId, ResearchFactRef factRef) {
        this.entryViewedOnceHooks.add(new EntryViewedOnceHookSpec(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                entryId,
                factRef
        ));
    }

    /**
     * Declares an explicit advancement-earned research ingress hook.
     *
     * The generated hook means: when the specified advancement is earned, grant the specified
     * research fact. That fact can then participate in research-node unlocking.
     */
    public void grantFactOnAdvancementEarned(String path, Identifier advancementId, ResearchFactRef factRef) {
        this.advancementHooks.add(new AdvancementHookSpec(
                Identifier.fromNamespaceAndPath(this.namespace, path),
                advancementId,
                factRef
        ));
    }

    /**
     * Returns the compiled canonical fact definitions collected so far.
     */
    public List<ResearchFactDefinition> factDefinitions() {
        return this.facts.stream().map(ResearchFactSpec::toDefinition).toList();
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
}
